package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Applies one-step config migrations in sequence based on schema versions.
 * Maintains a registry of available migrations and executes them in dependency order
 * until the configuration reaches the latest schema version.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ConfigUpdaterChain implements ConfigUpdater {

    /**
     * Creates a new builder for configuring a ConfigUpdaterChain.
     *
     * @return a new builder
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static final String DEFAULT_VERSION_PATH = "config-version";
    private static final Version DEFAULT_FALLBACK_VERSION = Version.ZERO;
    private static final int MAX_CHAIN_LENGTH = 1000;

    @NotNull
    private final VersionResolver versionResolver;

    @NotNull
    private final Map<VersionMatcher, ConfigMigration> migrationMap;

    @NotNull
    private final Version targetVersion;

    public ConfigUpdaterChain(
            @NotNull VersionResolver versionResolver,
            @NotNull Map<ConfigMigration, VersionMatcher> migrations,
            @NotNull Version targetVersion
    ) {
        this.versionResolver = Args.notNull(versionResolver, "versionResolver");
        this.targetVersion = Args.notNull(targetVersion, "targetVersion");
        this.migrationMap = migrationMap(Args.notNull(migrations, "migrations"));
    }

    @Override
    @NotNull
    public Collection<ConfigMigration> getPossibleMigrations() {
        return Collections.unmodifiableCollection(this.migrationMap.values());
    }

    private Map<VersionMatcher, ConfigMigration> migrationMap(@NotNull Map<ConfigMigration, VersionMatcher> migrations) {
        final Map<VersionMatcher, ConfigMigration> migrationMap = new HashMap<>(migrations.size());
        for (Map.Entry<ConfigMigration, VersionMatcher> entry : migrations.entrySet()) {

            ConfigMigration migration = entry.getKey();
            VersionMatcher matcher = entry.getValue();

            if (migration == null) {
                throw new IllegalArgumentException("Migrations cannot be null");
            }

            if(matcher == null) {
                matcher = migration.fromVersion();
            }

            if(matcher == null) {
                throw new IllegalArgumentException("Version matcher cannot be null. Register directly or overwrite #fromVersion()");
            }

            if (migrationMap.containsKey(matcher)) {
                throw new IllegalArgumentException("Duplicate matcher: " + matcher.describe());
            }

            migrationMap.put(matcher, migration);
        }
        return migrationMap;
    }

    @Override
    public int run(@NotNull ConfigSection config) {
        Args.notNull(config, "config");

        Version currentVersion = this.versionResolver.resolve(config);
        int appliedCount = 0;

        for (int iteration = 0; iteration < MAX_CHAIN_LENGTH; iteration++) {

            // Stop if we've reached or exceeded the target version
            if (currentVersion.compareTo(this.targetVersion) >= 0) {
                return appliedCount;
            }

            ConfigMigration next = findNext(currentVersion);

            // no applicable migration found, stop the chain
            if (next == null) {
                return appliedCount;
            }

            // Apply the migration and update the version if successful
            boolean applied = next.apply(config);
            if (!applied) {
                return appliedCount;
            }

            Version nextVersion = Args.notNull(next.toVersion(), "migration target version");
            appliedCount++;
            this.versionResolver.apply(config, nextVersion);

            // Stop safely if a migration does not advance the schema version.
            if (nextVersion.compareTo(currentVersion) <= 0) {
                return appliedCount;
            }

            currentVersion = nextVersion;
        }

        throw new IllegalStateException(
                "Config update chain exceeded maximum iterations (" + MAX_CHAIN_LENGTH + ")"
        );
    }

    /**
     * Gets the target version that this updater aims to reach.
     *
     * @return the target schema version
     */
    @NotNull
    public Version getTargetVersion() {
        return this.targetVersion;
    }

    @Nullable
    private ConfigMigration findNext(@NotNull Version currentVersion) {
        // check exact first
        for (Map.Entry<VersionMatcher, ConfigMigration> entry : this.migrationMap.entrySet()) {

            VersionMatcher matcher = entry.getKey();
            if(!matcher.isExact()) continue;

            if (matcher.matches(currentVersion)) {
                return entry.getValue();
            }

        }

        // check not exact after
        for (Map.Entry<VersionMatcher, ConfigMigration> entry : this.migrationMap.entrySet()) {
            VersionMatcher matcher = entry.getKey();
            if(matcher.isExact()) continue;

            if (matcher.matches(currentVersion)) {
                return entry.getValue();
            }
        }


        return null;
    }

    /**
     * Builder for constructing ConfigUpdaterChain instances with a fluent API.
     */
    public static final class Builder {

        private static final VersionResolver DEFAULT_RESOLVER = new PathVersionResolver(DEFAULT_VERSION_PATH, DEFAULT_FALLBACK_VERSION);

        @NotNull
        private final Map<ConfigMigration, VersionMatcher> migrations = new HashMap<>();

        @NotNull
        private VersionResolver versionResolver = DEFAULT_RESOLVER;

        @Nullable
        private Version targetVersion;

        /**
         * Adds a migration to this chain in execution order.
         *
         * @param migration migration to add
         *
         * @return this builder
         */
        @NotNull
        public Builder apply(@NotNull ConfigMigration migration) {
            Args.notNull(migration, "migration");
            this.migrations.put(migration, null);
            return this;
        }

        @NotNull
        public Builder apply(@NotNull VersionMatcher matcher, @NotNull ConfigMigration migration) {
            Args.notNull(migration, "migration");
            this.migrations.put(migration, matcher);
            return this;
        }

        @NotNull
        public Builder applyWhen(@NotNull String pattern, @NotNull ConfigMigration migration) {
            return apply(VersionMatcher.parse(pattern), migration);
        }

        @NotNull
        public Builder applyExact(@NotNull Version exact, @NotNull ConfigMigration migration) {
            return apply(VersionMatcher.exact(exact), migration);
        }

        @NotNull
        public Builder applyAbove(@NotNull Version above, @NotNull ConfigMigration migration) {
            return apply(VersionMatcher.above(above), migration);
        }

        @NotNull
        public Builder applyBelow(@NotNull Version below, @NotNull ConfigMigration migration) {
            return apply(VersionMatcher.below(below), migration);
        }

        /**
         * Sets a custom resolver for reading and writing schema versions.
         *
         * @param versionResolver custom resolver
         *
         * @return this builder
         */
        @NotNull
        public Builder versionResolver(@NotNull VersionResolver versionResolver) {
            this.versionResolver = Args.notNull(versionResolver, "versionResolver");
            return this;
        }

        /**
         * Sets the config path where the schema version is stored.
         * Used by the default resolver when no custom resolver is configured.
         *
         * @param versionPath version key path
         *
         * @return this builder
         */
        @NotNull
        public Builder versionPath(@NotNull String versionPath, @NotNull Version fallback) {
            String path = Args.notNullOrEmpty(versionPath, "versionPath").trim();
            Args.notEmpty(fallback, "fallback");
            this.versionResolver = new PathVersionResolver(path, fallback);
            return this;
        }

        /**
         * Sets the target version that the updater should reach.
         * If not specified, the target will be the version of the last migration in the chain.
         *
         * @param targetVersion the target schema version
         *
         * @return this builder
         */
        @NotNull
        public Builder targetVersion(@NotNull Version targetVersion) {
            this.targetVersion = Args.notNull(targetVersion, "targetVersion");
            return this;
        }

        /**
         * Builds and returns the ConfigUpdaterChain instance.
         *
         * @return a new ConfigUpdaterChain
         *
         * @throws IllegalStateException if no migrations are configured
         */
        @NotNull
        public ConfigUpdaterChain build() {
            if (this.migrations.isEmpty()) {
                throw new IllegalStateException("Cannot build ConfigUpdaterChain without migrations");
            }

            VersionResolver resolver = this.versionResolver;

            Version chainTargetVersion = this.targetVersion;
            if (chainTargetVersion == null) {
                chainTargetVersion = findHighestTargetVersion(this.migrations.keySet());
            }

            return new ConfigUpdaterChain(resolver, this.migrations, chainTargetVersion);
        }

        @NotNull
        private static Version findHighestTargetVersion(@NotNull Collection<ConfigMigration> migrations) {
            Version highest = null;

            for (ConfigMigration migration : migrations) {
                Version toVersion = Args.notNull(migration.toVersion(), "migration target version");
                if (highest == null || toVersion.compareTo(highest) > 0) {
                    highest = toVersion;
                }
            }

            if (highest == null) {
                throw new IllegalStateException("Cannot determine target version from empty migration list");
            }

            return highest;
        }
    }

    private static final class PathVersionResolver implements VersionResolver {

        @NotNull
        private final String versionPath;

        @Nullable
        private final Version fallbackVersion;

        private PathVersionResolver(@NotNull String versionPath, @Nullable Version fallbackVersion) {
            this.versionPath = versionPath;
            this.fallbackVersion = fallbackVersion;
        }

        @Override
        @NotNull
        public Version resolve(@NotNull ConfigSection config) {
            Version resolved = config.getVersion(this.versionPath, null);
            if (resolved != null) {
                return resolved;
            }

            if (this.fallbackVersion != null) {
                return this.fallbackVersion;
            }

            return DEFAULT_FALLBACK_VERSION;
        }

        @Override
        public void apply(@NotNull ConfigSection config, @NotNull Version version) {
            config.set(this.versionPath, Args.notNull(version, "version")
                    .formatFull());
        }
    }
}

