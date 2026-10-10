package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ResourceResolver;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
     * Loads all {@link Property property fields} (instance and static) from the provided
     * instance using the specified load strategy.
     *
     * <p>
     * Resolves the file via the {@link ConfigSource annotation value}.
     *
     * @param instance The instance to load
     * @param strategy The load strategy defining how the configuration data should be processed
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void load(@NotNull T instance, @NotNull LoadStrategy strategy);

    /**
     * Loads all {@link Property property fields} (instance and static) from the provided
     * instance using the specified load strategy.
     *
     * @param instance The instance to load
     * @param config   The configuration source from which the data will be loaded
     * @param strategy The load strategy defining how the configuration data should be processed
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    <T> void load(@NotNull T instance, @NotNull Config config, @NotNull LoadStrategy strategy);

    /**
     * Loads all static {@link Property property fields} from the provided config
     * class using the specified load strategy.
     *
     * <p>
     * Resolves the file via the {@link ConfigSource annotation value}.
     *
     * @param clazz    The class type to load
     * @param strategy The load strategy defining how the configuration data should be processed
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    void loadStatic(@NotNull Class<?> clazz, @NotNull LoadStrategy strategy);

    /**
     * Loads all static {@link Property property fields} from the provided config
     * class using the specified load strategy.
     *
     * @param clazz    The class type to load
     * @param config   The configuration source from which the data will be loaded
     * @param strategy The load strategy defining how the configuration data should be processed
     *
     * @throws NullPointerException if any of the provided parameters is null
     */
    void loadStatic(@NotNull Class<?> clazz, @NotNull Config config, @NotNull LoadStrategy strategy);

}
