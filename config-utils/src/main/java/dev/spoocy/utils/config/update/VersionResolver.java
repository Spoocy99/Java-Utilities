package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves the target version from the defaults configuration for a config update process.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface VersionResolver {

    /**
     * Resolves the target version based on the provided default configuration section.
     * This method extracts and determines the version information necessary for
     * configuration updates or validations.
     *
     * @param defaultsConfig the default configuration section to be analyzed for
     *                       extracting versioning details. Must not be null.
     *
     * @return the resolved {@link Version} object derived from the given default
     * configuration. Will never return null.
     */
    @NotNull
    Version resolve(@NotNull ConfigSection defaultsConfig);

    /**
     * Applies a specific version to the given default configuration section.
     * This method ensures that the provided configuration section is updated
     * or validated against the specified version.
     *
     * @param defaultsConfig the default configuration section to which the version
     *                       will be applied. Must not be null.
     * @param version        the version to apply to the given default configuration
     *                       section. Must not be null.
     */
    void apply(@NotNull ConfigSection defaultsConfig, @NotNull Version version);

}
