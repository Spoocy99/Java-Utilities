package dev.spoocy.utils.config;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.update.ConfigUpdater;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ResourceResolver {

    BaseResourceResolver DEFAULT = new BaseResourceResolver(Resources.class.getClassLoader(),
            YamlConfigLoader.INSTANCE,
            JsonConfigLoader.INSTANCE
    );

    SafeResourceResolver SAFE = new SafeResourceResolver(Resources.class.getClassLoader(),
            YamlConfigLoader.INSTANCE,
            JsonConfigLoader.INSTANCE
    );

    static BaseResourceResolver defaultResolver() {
        return DEFAULT;
    }

    static SafeResourceResolver safeResolver() {
        return SAFE;
    }

    /**
     * Resolves a location string to a concrete {@link Resource}.
     *
     * @param path the string representation of the resource path; must not be null
     *
     * @return the resolved Resource instance; never null
     */
    @NotNull
    Resource resolve(@NotNull String path);

    @NotNull
    Config loadConfig(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException;

    @NotNull
    Config loadConfig(@NotNull Resource resource, @NotNull Constructor constructor, @NotNull ConfigUpdater updater)
            throws IOException;

    @NotNull
    Document loadDocument(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException;

    @NotNull
    Document loadDocument(@NotNull Resource resource, @NotNull Constructor constructor, @NotNull ConfigUpdater updater)
            throws IOException;

    @NotNull
    <T> T load(@NotNull Class<T> type, @NotNull Readable readable);

    @NotNull
    <T> T load(@NotNull Class<T> type, @NotNull Constructor constructor) throws IOException;

    @NotNull
    <T> T load(@NotNull Class<T> type, @NotNull Constructor constructor, @NotNull Resource resource) throws IOException;

    void save(@NotNull Object instance, @NotNull Representer representer) throws IOException;

    void save(@NotNull Object instance, @NotNull Representer representer, @NotNull Resource resource) throws IOException;

}
