package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.ResourceResolver;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ConfigBeanLoader {

    /**
     * Creates a new {@link ConfigBeanLoader} using the provided parameters.
     *
     * @param resourceResolver The resource resolver used to resolve the configuration file
     * @param representer      The {@link Representer} for the config files
     * @param constructor      The {@link Constructor} for the config files
     *
     * @return A new {@link ConfigBeanLoader}
     */
    @Contract("_, _, _ -> new")
    static @NotNull ConfigBeanLoader create(
            @NotNull ResourceResolver resourceResolver,
            @NotNull Representer representer,
            @NotNull Constructor constructor
    ) {
        return new ConfigBeanLoaderImpl(resourceResolver, representer, constructor);
    }

    /**
     * Loads all properties from the underlying {@link ConfigSource source}
     * using the specified {@link LoadStrategy}.
     *
     * @param instance The configuration instance
     * @param strategy The load strategy
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void load(@NotNull T instance, @NotNull LoadStrategy strategy);

    /**
     * Loads all properties from the provided {@link Config config}
     * using the specified {@link LoadStrategy}.
     *
     * @param instance The configuration instance
     * @param config   The config to use as the source
     * @param strategy The load strategy
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void load(@NotNull T instance, @NotNull Config config, @NotNull LoadStrategy strategy);

    /**
     * Loads all {@code static} properties from the underlying {@link ConfigSource source}
     * using the specified {@link LoadStrategy}.
     *
     * @param clazz    The class
     * @param strategy The load strategy
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    void loadStatic(@NotNull Class<?> clazz, @NotNull LoadStrategy strategy);

    /**
     * Loads all {@code static} properties from the provided {@link Config config}
     * using the specified {@link LoadStrategy}.
     *
     * @param clazz    The class
     * @param config   The config to use as the source
     * @param strategy The load strategy
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    void loadStatic(@NotNull Class<?> clazz, @NotNull Config config, @NotNull LoadStrategy strategy);

    /**
     * Saves all properties to the provided {@link Config config}
     * and saves it.
     *
     * @param instance The configuration instance
     * @param config   The config to use as the source
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void write(@NotNull T instance, @NotNull Config config);

    /**
     * Saves all {@code static} properties to the provided {@link Config config}
     * and saves it.
     *
     * @param clazz    The class
     * @param config   The config to use as the source
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void write(@NotNull Class<T> clazz, @NotNull Config config);

    /**
     * Saves all properties to the underlying {@link ConfigSource source}
     * and saves it.
     *
     * @param instance The configuration instance
     *
     * @throws IOException          if an I/O error occurs while saving the file
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void save(@NotNull T instance) throws IOException;

    /**
     * Saves all properties to the provided {@link Document document}
     * and saves it.
     *
     * @param instance The configuration instance
     * @param document   The document to use as the source
     *
     * @throws IOException          if an I/O error occurs while saving the file
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void save(@NotNull T instance, @NotNull Document document) throws IOException;

    /**
     * Saves all {@code static} properties to the underlying {@link ConfigSource source}
     * and saves it.
     *
     * @param clazz    The class
     *
     * @throws IOException          if an I/O error occurs while saving the file
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void saveStatic(@NotNull Class<T> clazz) throws IOException;

    /**
     * Saves all {@code static} properties to the provided {@link Document document}
     * and saves it.
     *
     * @param clazz    The class
     * @param document   The document to use as the source
     *
     * @throws IOException          if an I/O error occurs while saving the file
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void saveStatic(@NotNull Class<T> clazz, @NotNull Document document) throws IOException;

}
