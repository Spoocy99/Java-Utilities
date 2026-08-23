package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the contract for configuration migrations that update a configuration from one schema version to another.
 *
 * <p>Configuration migrations are used to evolve configuration schemas over time. Each migration specifies
 * the source version range it applies to (via {@link #fromVersion()}) and the target version it updates to
 * (via {@link #toVersion()}). Migrations can be chained in a {@link ConfigUpdaterChain} to progressively
 * update configurations across multiple schema versions.</p>
 *
 * <h3>Migration Lifecycle</h3>
 * <ol>
 *   <li>A migration is created with source and target versions</li>
 *   <li>The version matcher ({@link #fromVersion()}) is checked against the current config version</li>
 *   <li>If the matcher returns true, {@link #apply(ConfigSection)} is invoked to perform the update</li>
 *   <li>If successful, the configuration is marked as being at the target version</li>
 * </ol>
 *
 * <h3>Implementation Guidelines</h3>
 * <ul>
 *   <li><strong>Atomicity:</strong> Migrations should be atomic - either fully apply or not at all</li>
 *   <li><strong>Idempotence:</strong> Migrations should be safe to apply multiple times (when practical)</li>
 *   <li><strong>Backward Compatibility:</strong> Avoid breaking existing configurations unnecessarily</li>
 *   <li><strong>Error Handling:</strong> Throw exceptions on failure rather than silently degrading</li>
 * </ul>
 *
 * <h3>Example Implementation</h3>
 * <pre>{@code
 * public class RenameFieldMigration implements ConfigMigration {
 *     private final String oldKey;
 *     private final String newKey;
 *
 *     public RenameFieldMigration(String oldKey, String newKey) {
 *         this.oldKey = oldKey;
 *         this.newKey = newKey;
 *     }
 *
 *     @Override
 *     public VersionMatcher fromVersion() {
 *         return VersionMatcher.atLeast(Version.of("1.0.0"));
 *     }
 *
 *     @Override
 *     public Version toVersion() {
 *         return Version.of("1.1.0");
 *     }
 *
 *     @Override
 *     public boolean apply(ConfigSection config) {
 *         if (config.isSet(oldKey) && !config.isSet(newKey)) {
 *             Object value = config.getObject(oldKey);
 *             config.set(newKey, value);
 *             config.remove(oldKey);
 *             return true;
 *         }
 *         return false;
 *     }
 * }
 * }</pre>
 *
 * @see ConfigUpdaterChain
 * @see VersionMatcher
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface ConfigMigration {
    
    /**
     * Returns the version matcher that determines if this migration applies to a configuration.
     *
     * <p>The matcher checks the current configuration version and returns {@code true} if this migration
     * should be applied, {@code false} otherwise.</p>
     *
     * <p>If this method returns null, the migration is considered to apply to any version. This is equivalent
     * to {@link VersionMatcher#ANY}.</p>
     *
     * @return the version matcher, or {@code null} to apply to any version
     */
    @Nullable
    default VersionMatcher fromVersion() {
        return null;
    }

    /**
     * Retrieves the target version to which the configuration is updated after this migration.
     *
     * <p>This version should be higher than the source version(s) specified by {@link #fromVersion()}.
     * In a migration chain, this becomes the current version for the next migration to check.</p>
     *
     * @return the target {@link Version} that this migration updates to, never null
     */
    @NotNull
    Version toVersion();

    /**
     * Applies this migration to the provided configuration section.
     *
     * <p>This method performs the actual transformation, which may include:
     * <ul>
     *   <li>Renaming, adding, or removing configuration fields</li>
     *   <li>Converting data types or restructuring the configuration tree</li>
     *   <li>Validating or normalizing configuration values</li>
     * </ul>
     * </p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>The method must not be null</li>
     *   <li>Changes are made in-place on the provided ConfigSection</li>
     *   <li>Return {@code true} if any changes were made, {@code false} if the config was already in the target state</li>
     *   <li>Throw an exception on error (do not silently fail)</li>
     * </ul>
     * </p>
     *
     * @param config the configuration section to be updated
     * @return {@code true} if the migration was applied and made changes, {@code false} otherwise
     * @throws IllegalArgumentException if the configuration is invalid for this migration
     * @throws IllegalStateException if the migration fails for any reason
     */
    boolean apply(@NotNull ConfigSection config);

}
