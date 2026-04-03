package dev.spoocy.utils.config.update.base;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.update.ConfigMigration;
import dev.spoocy.utils.config.update.VersionMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for configuration migrations implementing the {@link ConfigMigration} interface.
 *
 * @see ConfigMigration
 * @see VersionMatcher
 * @author Spoocy99 | GitHub: Spoocy99
 */
public abstract class BaseMigration implements ConfigMigration {

    @NotNull
    private final VersionMatcher matcher;

    @Nullable
    private final Version toVersion;

    /**
     * Constructs a new BaseMigration instance with the specified version matcher and target version.
     *
     * <p>The version matcher determines whether this migration should be applied to a given configuration.
     * If the matcher is null, the migration will apply to any configuration version (using {@link VersionMatcher#ANY}).</p>
     *
     * <p>The target version indicates the schema version that the configuration will be updated to.
     * If null, subclasses must override {@link #toVersion()} for lazy version resolution.</p>
     *
     * @param matcher   the version matcher to determine applicability, or {@code null} to apply to any version
     * @param toVersion the target version after migration, or {@code null} for lazy resolution via {@link #toVersion()}
     */
    public BaseMigration(
            @Nullable VersionMatcher matcher,
            @Nullable Version toVersion
    ) {
        this.matcher = matcher == null ? VersionMatcher.ANY : matcher;
        this.toVersion = toVersion;
    }

    /**
     * Convenience constructor for migrations that apply from one exact source version.
     *
     * <p>This constructor is a shorthand for creating migrations that apply to a specific version.
     * If {@code fromVersion} is null, the migration applies to any configuration version.</p>
     *
     * @param fromVersion exact source version, or {@code null} to apply to any version
     * @param toVersion   target version after migration
     */
    public BaseMigration(@Nullable Version fromVersion, @Nullable Version toVersion) {
        this(fromVersion == null ? VersionMatcher.ANY : VersionMatcher.exact(fromVersion), toVersion);
    }

    @Override
    public VersionMatcher fromVersion() {
        return this.matcher;
    }

    /**
     * Returns the target version that this migration updates to.
     *
     * <p>If a non-null target version was provided in the constructor, it is returned directly.
     * Otherwise, subclasses must override this method to provide lazy version resolution (e.g., by
     * reading the version from a defaults resource).</p>
     *
     * @return the target version
     * @throws IllegalStateException if no target version is available and this method has not been overridden
     */
    @Override
    @NotNull
    public Version toVersion() {
        if (this.toVersion == null) {
            throw new IllegalStateException("toVersion() must be overridden by subclasses that use lazy version resolution");
        }
        return this.toVersion;
    }

    @Override
    public abstract boolean apply(@NotNull ConfigSection config);

    /**
     * Returns the matcher used to determine this migration's applicability.
     *
     * @return version matcher for this migration
     */
    @NotNull
    protected VersionMatcher matcher() {
        return this.matcher;
    }

}

