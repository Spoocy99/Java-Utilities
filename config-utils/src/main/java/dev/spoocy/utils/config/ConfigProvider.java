package dev.spoocy.utils.config;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for resolving configurations.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@FunctionalInterface
public interface ConfigProvider {

    /**
     * Provides a configuration for further processing or usage.
     *
     * @return a non-null {@link Config} object representing the provided configuration
     */
    @NotNull
    Config provide();
}
