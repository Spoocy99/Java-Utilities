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
import java.util.function.Consumer;

/**
 * Validates and transforms configuration values for schema migration.
 *
 * <p>This migration provides a flexible way to enforce configuration constraints and normalize values:
 * <ul>
 *   <li>Custom validation logic on specific paths</li>
 *   <li>Requirement enforcement with default values</li>
 *   <li>Enum-like validation for allowed values</li>
 *   <li>Custom validators via the {@link Validator} interface</li>
 * </ul>
 * </p>
 *
 * <p>All validators are executed sequentially. If any validator throws an exception, the migration
 * fails and subsequent validators are not executed. Validators can safely modify the configuration
 * as part of their validation logic (e.g., setting default values).</p>
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * ValidationMigration migration = new ValidationMigration(
 *     VersionMatcher.atLeast(Version.of("1.0.0")),
 *     Version.of("1.1.0")
 * )
 * .requirePath("name", "DefaultName")
 * .validateAllowed("level", "DEBUG", "INFO", "WARN", "ERROR")
 * .validatePath("port", config -> {
 *     int port = config.getInt("port");
 *     if (port < 1 || port > 65535) {
 *         throw new IllegalStateException("Port must be between 1 and 65535");
 *     }
 * })
 * .addValidator(new CustomValidator());
 * }</pre>
 *
 * @see Validator
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ValidationMigration extends BaseMigration {

    @NotNull
    private final List<Validator> validators;

    public ValidationMigration(
            @Nullable Version fromVersion,
            @NotNull Version toVersion
    ) {
        this(fromVersion == null ? null : VersionMatcher.exact(fromVersion), toVersion);
    }

    public ValidationMigration(
            @Nullable VersionMatcher matcher,
            @NotNull Version toVersion
    ) {
        super(matcher, toVersion);
        this.validators = new ArrayList<>();
    }

    /**
     * Adds a validator that checks and/or transforms a configuration.
     *
     * <p>Validators are invoked in the order they are added. If a validator throws an exception,
     * the migration fails immediately.</p>
     *
     * @param validator the validator to add
     * @return this migration for chaining
     * @throws IllegalArgumentException if validator is null
     */
    @NotNull
    public ValidationMigration addValidator(@NotNull Validator validator) {
        Args.notNull(validator, "validator");
        this.validators.add(validator);
        return this;
    }

    /**
     * Adds a validator that applies custom logic to a specific configuration path.
     *
     * <p>The validator is only invoked if the path exists. The consumer receives a reference to the
     * configuration and can inspect or modify it as needed.</p>
     *
     * @param path      the configuration path to validate
     * @param validator the validation logic
     * @return this migration for chaining
     * @throws IllegalArgumentException if path or validator is null
     */
    @NotNull
    public ValidationMigration validatePath(@NotNull String path, @NotNull Consumer<ConfigSection> validator) {
        Args.notNull(path, "path");
        Args.notNull(validator, "validator");
        return addValidator(new PathValidator(path, validator));
    }

    /**
     * Adds a validator that ensures a required path exists, setting a default if missing.
     *
     * <p>If the path does not exist in the configuration, it will be created with the provided
     * default value. This is useful for ensuring backward compatibility when new required fields
     * are added to the configuration schema.</p>
     *
     * @param path         the required configuration path
     * @param defaultValue the value to set if the path is missing
     * @return this migration for chaining
     * @throws IllegalArgumentException if path is null
     */
    @NotNull
    public ValidationMigration requirePath(@NotNull String path, @NotNull Object defaultValue) {
        Args.notNull(path, "path");
        return addValidator(new RequiredPathValidator(path, defaultValue));
    }

    /**
     * Adds a validator that ensures a configuration value is one of the allowed values.
     *
     * <p>If the value at the specified path is not in the list of allowed values, an
     * {@link IllegalStateException} is thrown with details about the allowed values.</p>
     *
     * @param path           the configuration path to validate
     * @param allowedValues  the allowed values for this path
     * @return this migration for chaining
     * @throws IllegalArgumentException if path is null, allowedValues is null, or allowedValues is empty
     */
    @NotNull
    public ValidationMigration validateAllowed(@NotNull String path, @NotNull Object... allowedValues) {
        Args.notNull(path, "path");
        Args.notNull(allowedValues, "allowedValues");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("allowedValues cannot be empty");
        }
        return addValidator(new AllowedValuesValidator(path, allowedValues));
    }

    /**
     * Applies all registered validators to the configuration in order.
     *
     * @param config the configuration section to validate
     * @return {@code true} if there are any validators registered, {@code false} otherwise
     * @throws IllegalStateException if any validator fails
     */
    @Override
    public boolean apply(@NotNull ConfigSection config) {
        Args.notNull(config, "config");
        for (Validator validator : this.validators) {
            validator.validate(config);
        }
        return !this.validators.isEmpty();
    }

    /**
     * Interface for configuration validators.
     *
     * <p>Implementations should validate the configuration and may modify it as part of the validation
     * process (e.g., setting defaults or normalizing values). Validators should throw exceptions to signal
     * validation failure and prevent further migration steps.</p>
     */
    public interface Validator {
        /**
         * Validates the given configuration.
         *
         * @param config the configuration to validate
         * @throws IllegalStateException if validation fails
         */
        void validate(@NotNull ConfigSection config);
    }

    /**
     * Validator that applies custom logic to a specific configuration path.
     *
     * <p>The validator is only invoked if the path exists in the configuration. This is useful
     * for applying custom validation or transformation logic to specific configuration paths.</p>
     */
    private static final class PathValidator implements Validator {
        private final String path;
        private final Consumer<ConfigSection> validator;

        PathValidator(@NotNull String path, @NotNull Consumer<ConfigSection> validator) {
            this.path = path;
            this.validator = validator;
        }

        @Override
        public void validate(@NotNull ConfigSection config) {
            if (config.isSet(this.path)) {
                this.validator.accept(config);
            }
        }
    }

    /**
     * Validator that ensures a required path exists with a default value.
     *
     * <p>If the path does not exist, it is automatically created with the provided default value.
     * This validator is idempotent - repeated invocations on the same configuration will have no
     * additional effect after the first application.</p>
     */
    private static final class RequiredPathValidator implements Validator {
        private final String path;
        private final Object defaultValue;

        RequiredPathValidator(@NotNull String path, @NotNull Object defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        @Override
        public void validate(@NotNull ConfigSection config) {
            if (!config.isSet(this.path)) {
                config.set(this.path, this.defaultValue);
            }
        }
    }

    /**
     * Validator that ensures a configuration value is one of a set of allowed values.
     *
     * <p>If the path does not exist in the configuration, validation passes. If the value exists
     * but is not in the list of allowed values, an {@link IllegalStateException} is thrown with
     * a detailed error message listing the allowed values.</p>
     */
    private static final class AllowedValuesValidator implements Validator {
        private final String path;
        private final Object[] allowedValues;

        AllowedValuesValidator(@NotNull String path, @NotNull Object[] allowedValues) {
            this.path = path;
            this.allowedValues = allowedValues;
        }

        @Override
        public void validate(@NotNull ConfigSection config) {
            if (!config.isSet(this.path)) {
                return;
            }

            Object value = config.getObject(this.path);
            for (Object allowed : this.allowedValues) {
                if (allowed != null && allowed.equals(value)) {
                    return;
                }
            }

            throw new IllegalStateException(
                    "Configuration value at '" + this.path + "' is '" + value + "', " +
                    "but must be one of the allowed values: " + java.util.Arrays.toString(this.allowedValues)
            );
        }
    }
}

