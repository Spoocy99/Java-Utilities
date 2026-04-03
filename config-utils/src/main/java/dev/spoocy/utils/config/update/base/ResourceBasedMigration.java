package dev.spoocy.utils.config.update.base;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigProvider;
import dev.spoocy.utils.config.ResourceProvider;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for migrations that load configuration from classpath resources.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 * @see ResourceProvider
 */
public abstract class ResourceBasedMigration extends BaseMigration {

    @NotNull
    private final ConfigProvider configProvider;

    @Nullable
    private volatile Config cachedConfig;

    /**
     * Constructs a new ResourceBasedMigration with the specified parameters.
     *
     * @param matcher          the version matcher to determine applicability, or {@code null} for any version
     * @param toVersion        the target version after migration, or {@code null} for lazy resolution
     * @param configProvider   the config provider to use for loading configurations, must not be null
     *
     * @throws IllegalArgumentException if resourceProvider is null
     */
    public ResourceBasedMigration(
            @Nullable VersionMatcher matcher,
            @Nullable Version toVersion,
            @NotNull ConfigProvider configProvider
    ) {
        super(matcher, toVersion);
        this.configProvider = configProvider;
    }

    /**
     * Returns the resource provider used by this migration.
     *
     * @return the resource provider
     */
    @NotNull
    public ConfigProvider getConfigProvider() {
        return this.configProvider;
    }

    /**
     * Loads the configuration from the resource with automatic caching.
     *
     * <p>The configuration is loaded only once on first invocation and then cached for subsequent calls.
     * This caching is thread-safe and uses synchronized double-checked locking to minimize contention.</p>
     *
     * @return the loaded configuration
     *
     * @throws IllegalStateException if the resource fails to load
     */
    @NotNull
    protected Config loadResource() {
        Config cached = this.cachedConfig;
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            Config synchronizedCached = this.cachedConfig;
            if (synchronizedCached != null) {
                return synchronizedCached;
            }

            Config loaded;

            try {
                loaded = this.configProvider.provide();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load configuration resource for migration: " + e.getMessage(), e);
            }

            this.cachedConfig = loaded;
            return loaded;
        }
    }
}

