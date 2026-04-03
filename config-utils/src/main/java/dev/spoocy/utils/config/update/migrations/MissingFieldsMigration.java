package dev.spoocy.utils.config.update.migrations;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigProvider;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.ResourceProvider;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.update.ConfigUpdaterChain;
import dev.spoocy.utils.config.update.VersionMatcher;
import dev.spoocy.utils.config.update.VersionResolver;
import dev.spoocy.utils.config.update.base.ResourceBasedMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Adds missing values from a classpath defaults file to an existing config.
 * <p>
 * This migration loads default values from a resource file and merges them into the target
 * configuration, adding only the fields that are missing. Existing values are never overwritten,
 * making this migration safe for incremental updates.</p>
 *
 * @see ResourceBasedMigration
 * @see VersionResolver
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class MissingFieldsMigration extends ResourceBasedMigration {

    @NotNull
    private final VersionResolver versionResolver;

    @Nullable
    private volatile Version cachedResolvedVersion;

    public MissingFieldsMigration(
            @NotNull Config config,
            @Nullable VersionMatcher matcher,
            @NotNull VersionResolver versionResolver
    ) {
        this(() -> config, matcher, versionResolver);
    }

    /**
     * Creates a new MissingFieldsMigration that fills in missing configuration values.
     *
     * @param configProvider    the provider for loading the defaults resource
     * @param matcher          the version matcher to determine applicability, or {@code null} to match any version
     * @param versionResolver  the resolver for determining the target version from the defaults resource
     * @throws IllegalArgumentException if any required parameter is null
     */
    public MissingFieldsMigration(
            @NotNull ConfigProvider configProvider,
            @Nullable VersionMatcher matcher,
            @NotNull VersionResolver versionResolver
    ) {
        super(matcher, null, configProvider);
        this.versionResolver = Args.notNull(versionResolver, "versionResolver");
    }

    /**
     * Resolves the target version from the defaults resource using the configured version resolver.
     *
     * <p>The result is cached after the first call for performance. The lazy resolution allows
     * the version to be determined from the configuration content itself.</p>
     *
     * @return the resolved target version
     * @throws IllegalStateException if version resolution fails
     */
    @Override
    @NotNull
    public Version toVersion() {
        Version cached = this.cachedResolvedVersion;
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            Version synchronizedCached = this.cachedResolvedVersion;
            if (synchronizedCached != null) {
                return synchronizedCached;
            }

            Version resolved = this.versionResolver.resolve(loadResource());
            this.cachedResolvedVersion = resolved;
            return resolved;
        }
    }

    /**
     * Applies the migration by merging missing fields from defaults into the target configuration.
     *
     * <p>This method safely adds only missing configuration fields. Fields that exist in the target
     * configuration are never overwritten, and incompatible structure merges are skipped.</p>
     *
     * @param config the configuration to migrate
     * @return {@code true} if any fields were added
     * @throws IllegalStateException if the defaults resource fails to load or merge fails unexpectedly
     */
    @Override
    public boolean apply(@NotNull ConfigSection config) {
        Args.notNull(config, "config");
        try {
            mergeMissing(loadResource(), config);
            return true;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to apply defaults migration", e);
        }
    }

    /**
     * Recursively merges missing fields from defaults into the target configuration.
     *
     * <p>This method iterates through all paths in the defaults configuration and adds any that
     * are missing from the target. It gracefully handles structural incompatibilities by catching
     * and ignoring {@link IllegalArgumentException} when a path cannot be set (e.g., when the target
     * contains a scalar value where the defaults expect a section).</p>
     *
     * @param defaults the default configuration section with fallback values
     * @param target   the target configuration section to populate with missing fields
     */
    private void mergeMissing(@NotNull ConfigSection defaults, @NotNull ConfigSection target) {
        for (String path : defaults.keys(true)) {
            if (target.isSet(path)) {
                continue;
            }

            Object value = defaults.getObject(path);
            if (value == null || value instanceof ConfigSection) {
                continue;
            }

            // Skip incompatible trees (e.g. target contains scalar where defaults expects a section).
            try {
                target.set(path, value);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}

