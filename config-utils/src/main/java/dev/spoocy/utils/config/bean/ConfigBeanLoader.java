package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.*;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ConfigBeanLoader {

    private static final ConcurrentMap<Class<?>, ConfigBean<?>> TYPES = new ConcurrentHashMap<>();

    private final ResourceResolver resourceResolver;
    private final Representer representer;
    private final Constructor constructor;

    public ConfigBeanLoader(
            @NotNull ResourceResolver resourceResolver,
            @NotNull Representer representer,
            @NotNull Constructor constructor
    ) {
        this.resourceResolver = Args.notNull(resourceResolver, "resourceResolver");
        this.representer = Args.notNull(representer, "representer");
        this.constructor = Args.notNull(constructor, "constructor");
    }

    /**
     * Binds a specified class type to a configuration bean, creating it if necessary.
     *
     * @param <T>   the type of the class being bound
     * @param clazz the class type to bind; must not be null
     *
     * @return a {@code ConfigBean} instance representing the bound configuration for the provided class type
     *
     * @throws IllegalArgumentException if {@code clazz} is null
     */
    @Contract("_ -> new")
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> ConfigBean<T> bind(@NotNull Class<T> clazz) {
        Args.notNull(clazz, "clazz");
        return (ConfigBean<T>) TYPES.computeIfAbsent(clazz, this::createBean);
    }

    /**
     * Loads an instance of the specified class type from the given configuration using the provided load strategy.
     *
     * @param <T>      the type of the object being loaded
     * @param clazz    the class type to load; must not be null
     * @param config   the configuration source from which the data will be loaded; must not be null
     * @param strategy the load strategy defining how the configuration data should be processed; must not be null
     *
     * @return an instance of the specified class type populated with data from the given configuration
     *
     * @throws IllegalArgumentException if {@code clazz} or {@code config} is null
     */
    @Contract("_, _, _ -> new")
    @NotNull
    public <T> T load(@NotNull Class<T> clazz, @NotNull Config config, @NotNull LoadStrategy strategy) {
        Args.notNull(config, "config");
        Args.notNull(clazz, "clazz");

        ConfigBean<T> bean = bind(clazz);
        T instance = bean.newInstance();
        return load(bean, config, instance, strategy);
    }

    /**
     * Loads an instance of the specified class type using the given load strategy.
     *
     * @param <T>      the type of the object being loaded
     * @param clazz    the class type to load; must not be null
     * @param strategy the load strategy defining how the configuration data should be processed; must not be null
     *
     * @return an instance of the specified class type loaded with data based on the provided strategy
     *
     * @throws IllegalArgumentException if {@code clazz} or {@code strategy} is null
     */
    @Contract("_, _ -> new")
    @NotNull
    public <T> T load(@NotNull Class<T> clazz, @NotNull LoadStrategy strategy) {
        Args.notNull(clazz, "clazz");
        Args.notNull(strategy, "strategy");

        ConfigBean<T> bean = bind(clazz);
        Config config = resolveDocument(bean);
        T instance = bean.newInstance();

        return load(bean, config, instance, strategy);
    }

    @Contract("_, _, _, _ -> param3")
    @NotNull
    private <T> T load(
            @NotNull ConfigBean<T> bean,
            @NotNull Config config,
            @NotNull T instance,
            @NotNull LoadStrategy strategy
    ) {
        ConfigSection source = resolveSection(config, bean.section());
        PostLoadResult res = bean.read(instance, source);

        switch (strategy) {
            case JUST_LOAD:
                break;

            case SAVE_DEFAULTS:
                bean.writeDefaults(instance, source);
                break;

            case SAVE_DEFAULTS_AND_RESOURCE:

                if (!(config instanceof Document)) {
                    throw new IllegalArgumentException("Config must be a Document when using LoadStrategy.SAVE_DEFAULTS_AND_RESOURCE.");
                }

                boolean changed = bean.writeDefaults(instance, source);

                if(res == PostLoadResult.SAVE || changed) {

                    // some defaults were written so save the config
                    try {
                        ((Document) config).save(this.representer);
                    } catch (IOException ex) {
                        throw new IllegalStateException("Failed to save config after loading defaults for " + bean.type()
                                .getName(), ex);
                    }

                }

                break;
        }

        return instance;
    }

    /**
     * Converts the provided instance into a {@link Config} object.
     *
     * @param <T>      the type of the instance being processed
     * @param instance the instance to be written to a configuration; must not be null
     *
     * @return a {@code Config} object representing the serialized configuration of the provided instance
     */
    @Contract("_ -> new")
    @NotNull
    public <T> Config writeToConfig(@NotNull T instance) {
        Args.notNull(instance, "instance");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);

        Document config = resolveDocument(bean);
        write(bean, config, instance);

        return config.withoutRelation();
    }

    /**
     * Writes the provided instance to the specified {@link Config}.
     *
     * @param <T>      the type of the instance being written
     * @param instance the instance to be written to the configuration; must not be null
     * @param config   the configuration to which the instance data will be written; must not be null
     *
     * @return the updated {@code Config} object containing the written instance data
     *
     * @throws NullPointerException if {@code instance} or {@code config} is null
     */
    @Contract("_, _ -> param2")
    @NotNull
    public <T> Config writeToConfig(@NotNull T instance, @NotNull Config config) {
        Args.notNull(instance, "instance");
        Args.notNull(config, "config");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        write(bean, config, instance);

        return config;
    }

    private <T> void write(@NotNull ConfigBean<T> bean, @NotNull Config config, @NotNull T instance) {
        ConfigSection section = resolveSection(config, bean.section());
        bean.write(instance, section);
    }

    /**
     * Saves the provided configuration instance to the underlying configuration source.
     *
     * @param <T>      the type of the instance being saved
     * @param instance the instance to save; must not be null
     *
     * @throws IOException          if an I/O error occurs while saving the instance
     * @throws NullPointerException if {@code instance} is null
     */
    public <T> void save(@NotNull T instance) throws IOException {
        Args.notNull(instance, "instance");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        Document config = resolveDocument(bean);
        writeAndSave(bean, config, instance);
    }

    /**
     * Saves the provided configuration instance to the specified {@link Document}.
     *
     * @param <T>      the type of the instance being saved
     * @param instance the instance to save; must not be null
     * @param config   the configuration document to which the instance data will be
     *                 written; must not be null
     *
     * @throws IOException          if an I/O error occurs while saving the instance
     * @throws NullPointerException if {@code instance} or {@code config} is null
     */
    public <T> void save(@NotNull T instance, @NotNull Document config) throws IOException {
        Args.notNull(instance, "instance");
        Args.notNull(config, "config");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        writeAndSave(bean, config, instance);
    }

    private <T> void writeAndSave(@NotNull ConfigBean<T> bean, @NotNull Document config, @NotNull T instance)
            throws IOException {
        write(bean, config, instance);
        config.save(this.representer);
    }

    @NotNull
    private <T> ConfigBean<T> createBean(@NotNull Class<T> clazz) {
        ConfigSource source = clazz.getAnnotation(ConfigSource.class);
        if (source == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @ConfigSource");
        }

        return new ConfigBean<>(
                clazz,
                source.value(),
                source.section()
                        .isEmpty() ? null : source.section(),
                source.saveDefaults(),
                source.allowMissingResource(),
                source.headerComments(),
                source.footerComments()
        );
    }

    @NotNull
    private ConfigSection resolveSection(@NotNull Config config, @Nullable String section) {
        if (section == null || section.isEmpty()) {
            return config;
        }

        ConfigSection sec = config.getSectionIfExists(section);
        return sec != null ? sec : config.createSection(section);
    }

    private Document resolveDocument(@NotNull ConfigBean<?> bean) {
        Resource resource = bean.resource(this.resourceResolver);
        ConfigLoader<? extends Config, ?> loader = this.resourceResolver.requireLoader(resource);

        Config config;

        if (resource.exists()) {

            try {
                config = loader.load(resource, this.constructor);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load config: " + bean.resourcePath(), e);
            }

        } else {

            // config doesn't exist so create empty if allowed
            if (bean.allowMissingResource()) {
                config = loader.createEmpty();
            } else {
                throw new IllegalStateException("Missing config resource: " + bean.resourcePath());
            }

        }

        return config.withRelation(resource);
    }

}
