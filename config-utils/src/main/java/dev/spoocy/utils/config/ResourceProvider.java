package dev.spoocy.utils.config;

import dev.spoocy.utils.config.io.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for resolving resources.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@FunctionalInterface
public interface ResourceProvider {

    /**
     * Provides a resource for further processing or usage.
     *
     * @return a non-null {@link Resource} object representing the provided resource
     */
    @NotNull
    Resource provide();
}
