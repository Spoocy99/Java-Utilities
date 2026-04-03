package dev.spoocy.utils.config.update.migrations;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.VersionMatcher;
import dev.spoocy.utils.config.update.base.BaseMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Applies transformations to configuration values for schema migration.
 *
 * <p>This migration provides a flexible way to restructure configurations by supporting:
 * <ul>
 *   <li>Key renaming with conflict detection</li>
 *   <li>Key removal for deprecated fields</li>
 *   <li>Value transformation using custom functions</li>
 *   <li>Custom transformations via the {@link Transformation} interface</li>
 * </ul>
 * </p>
 *
 * <p>Transformations are applied in the order they are added. Each transformation is independent
 * and failures in one transformation do not affect the execution of subsequent transformations.</p>
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * TransformationMigration migration = new TransformationMigration(
 *     Version.of("1.0.0"),
 *     Version.of("1.1.0")
 * )
 * .renameKey("old-key", "new-key")
 * .removeKey("deprecated-key")
 * .transformValue("port", val -> Integer.parseInt(val.toString()))
 * .addTransformation(new CustomTransformation());
 * }</pre>
 *
 * @see Transformation
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class TransformationMigration extends BaseMigration {

    @NotNull
    private final List<Transformation> transformations;

    public TransformationMigration(
            @Nullable Version fromVersion,
            @NotNull Version toVersion
    ) {
        this(fromVersion == null ? null : VersionMatcher.exact(fromVersion), toVersion);
    }

    public TransformationMigration(
            @Nullable VersionMatcher matcher,
            @NotNull Version toVersion
    ) {
        super(matcher, toVersion);
        this.transformations = new ArrayList<>();
    }

    /**
     * Renames a configuration key from oldPath to newPath.
     *
     * <p>If the old path does not exist, the transformation is skipped. If the new path already
     * exists, the transformation is also skipped to avoid unintended overwrites.</p>
     *
     * @param oldPath the current configuration path
     * @param newPath the new configuration path
     * @return this migration for chaining
     * @throws IllegalArgumentException if oldPath or newPath is null
     */
    @NotNull
    public TransformationMigration renameKey(@NotNull String oldPath, @NotNull String newPath) {
        Args.notNull(oldPath, "oldPath");
        Args.notNull(newPath, "newPath");
        return addTransformation(new RenameTransformation(oldPath, newPath));
    }

    /**
     * Removes a configuration key from the config.
     *
     * <p>If the path does not exist, the transformation is silently skipped.</p>
     *
     * @param path the configuration path to remove
     * @return this migration for chaining
     * @throws IllegalArgumentException if path is null
     */
    @NotNull
    public TransformationMigration removeKey(@NotNull String path) {
        Args.notNull(path, "path");
        return addTransformation(new RemoveTransformation(path));
    }

    /**
     * Transforms a value at a specific path using a transformation function.
     *
     * <p>If the path does not exist, the transformation is skipped. The transformer function
     * can access the current value and return a new value. If the transformation throws an exception,
     * it will be wrapped in an {@link IllegalStateException}.</p>
     *
     * @param path        the configuration path to transform
     * @param transformer the function to transform the value
     * @return this migration for chaining
     * @throws IllegalArgumentException if path or transformer is null
     */
    @NotNull
    public TransformationMigration transformValue(
            @NotNull String path,
            @NotNull Function<Object, Object> transformer
    ) {
        Args.notNull(path, "path");
        Args.notNull(transformer, "transformer");
        return addTransformation(new ValueTransformation(path, transformer));
    }

    /**
     * Adds a custom transformation to be applied during migration.
     *
     * <p>Custom transformations are applied in the order they are added. This method can be used
     * to extend the migration with application-specific transformation logic.</p>
     *
     * @param transformation the custom transformation to add
     * @return this migration for chaining
     * @throws IllegalArgumentException if transformation is null
     */
    @NotNull
    public TransformationMigration addTransformation(@NotNull Transformation transformation) {
        Args.notNull(transformation, "transformation");
        this.transformations.add(transformation);
        return this;
    }

    /**
     * Applies all registered transformations to the configuration in order.
     *
     * @param config the configuration section to transform
     * @return {@code true} if at least one transformation was applied, {@code false} otherwise
     * @throws IllegalStateException if a transformation fails unexpectedly
     */
    @Override
    public boolean apply(@NotNull ConfigSection config) {
        Args.notNull(config, "config");
        int appliedCount = 0;
        for (Transformation transformation : this.transformations) {
            if (transformation.apply(config)) {
                appliedCount++;
            }
        }
        return appliedCount > 0;
    }

    /**
     * Interface for configuration transformations.
     *
     * <p>Implementations should be idempotent when possible and should gracefully handle
     * cases where the expected configuration paths do not exist.</p>
     */
    public interface Transformation {
        /**
         * Applies the transformation to the config.
         *
         * @param config the configuration section
         * @return true if the transformation was applied, false otherwise
         */
        boolean apply(@NotNull ConfigSection config);
    }

    /**
     * Transformation that renames a configuration key.
     *
     * <p>The value at oldPath is moved to newPath. If either path does not exist or newPath
     * already exists, the transformation is skipped to prevent data loss.</p>
     */
    private static final class RenameTransformation implements Transformation {
        private final String oldPath;
        private final String newPath;

        RenameTransformation(@NotNull String oldPath, @NotNull String newPath) {
            this.oldPath = oldPath;
            this.newPath = newPath;
        }

        @Override
        public boolean apply(@NotNull ConfigSection config) {
            if (!config.isSet(this.oldPath)) {
                return false;
            }

            if (config.isSet(this.newPath)) {
                return false;
            }

            Object value = config.getObject(this.oldPath);
            config.set(this.newPath, value);
            config.remove(this.oldPath);
            return true;
        }
    }

    /**
     * Transformation that removes a configuration key.
     *
     * <p>If the path does not exist, the transformation is silently skipped.</p>
     */
    private static final class RemoveTransformation implements Transformation {
        private final String path;

        RemoveTransformation(@NotNull String path) {
            this.path = path;
        }

        @Override
        public boolean apply(@NotNull ConfigSection config) {
            if (config.isSet(this.path)) {
                config.remove(this.path);
                return true;
            }
            return false;
        }
    }

    /**
     * Transformation that converts a value using a custom transformation function.
     *
     * <p>The transformer function receives the current value and should return the transformed value.
     * If the transformation throws an exception, it is wrapped in an IllegalStateException.</p>
     */
    private static final class ValueTransformation implements Transformation {
        private final String path;
        private final Function<Object, Object> transformer;

        ValueTransformation(@NotNull String path, @NotNull Function<Object, Object> transformer) {
            this.path = path;
            this.transformer = transformer;
        }

        @Override
        public boolean apply(@NotNull ConfigSection config) {
            if (!config.isSet(this.path)) {
                return false;
            }

            try {
                Object value = config.getObject(this.path);
                Object transformed = this.transformer.apply(value);
                config.set(this.path, transformed);
                return true;
            } catch (Exception exception) {
                throw new IllegalStateException(
                        "Failed to transform value at path '" + this.path + "'",
                        exception
                );
            }
        }
    }
}

