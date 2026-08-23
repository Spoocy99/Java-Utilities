package dev.spoocy.utils.config;

import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ResourceResolver {

    BaseResourceResolver DEFAULT = new BaseResourceResolver(Resources.class.getClassLoader(),
            YamlConfigLoader.INSTANCE,
            JsonConfigLoader.INSTANCE
    );

    static BaseResourceResolver defaultResolver() {
        return DEFAULT;
    }

    /**
     * Retrieves the {@link ClassLoader} associated with the current implementation of the resource resolver.
     *
     * @return the {@link ClassLoader} used for resource resolution, or null if no specific
     *         {@link ClassLoader} is associated.
     */
    @Nullable
    ClassLoader getClassLoader();

    /**
     * Resolves a location string to a concrete {@link Resource}.
     *
     * @param path the string representation of the resource path; must not be null
     *
     * @return the resolved Resource instance; never null
     */
    @NotNull
    Resource resolve(@NotNull String path);

    /**
     * Resolves a suitable {@link ConfigLoader} for the given {@link Resource}. The resulting loader
     * is capable of parsing and handling the configuration data associated with the resource.
     *
     * @param resource the resource for which a configuration loader needs to be resolved;
     *                 must not be null
     *
     * @return a {@link ConfigLoader} instance capable of processing the specified resource,
     *         or null if no suitable loader is found
     */
    @Nullable
    ConfigLoader<? extends Config, ?> resolveLoader(@NotNull Resource resource);

    /**
     * Resolves and retrieves a suitable {@link ConfigLoader} for the specified {@link Resource}.
     * The resulting loader is guaranteed to be non-null and capable of parsing and handling
     * configuration data associated with the given resource.
     *
     * @param resource the resource for which a configuration loader is required;
     *                 must not be null
     * @return a {@link ConfigLoader} instance capable of processing the specified resource;
     *         never null
     * @throws IllegalArgumentException if no suitable loader is found for the resource
     */
    @NotNull
    ConfigLoader<? extends Config, ?> requireLoader(@NotNull Resource resource);

    /**
     * Creates an empty configuration instance associated with the provided resource.
     *
     * @param resource the resource with which the empty configuration will be associated;
     *                 must not be null
     * @return a new instance of {@link Config} representing an empty configuration
     */
    @NotNull
    Config createEmpty(@NotNull Resource resource);
}
