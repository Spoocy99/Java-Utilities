package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.ResourceResolver;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ConfigBeanLoaderImpl implements ConfigBeanLoader {

    private static final ConcurrentMap<Class<?>, ConfigBean<?>> TYPES = new ConcurrentHashMap<>();

    private final ResourceResolver resourceResolver;
    private final Representer representer;
    private final Constructor constructor;

    public ConfigBeanLoaderImpl(
            @NotNull ResourceResolver resourceResolver,
            @NotNull Representer representer,
            @NotNull Constructor constructor
    ) {
        this.resourceResolver = Args.notNull(resourceResolver, "resourceResolver");
        this.representer = Args.notNull(representer, "representer");
        this.constructor = Args.notNull(constructor, "constructor");
    }

    @Override
    public <T> void load(@NotNull T instance, @NotNull LoadStrategy strategy) {
        Args.notNull(instance, "instance");
        Args.notNull(strategy, "strategy");

        Class<T> clazz = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(clazz);
        Config config = resolveDocument(bean);
        load(bean, config, instance, strategy);
    }

    @Override
    public <T> void load(@NotNull T instance, @NotNull Config config, @NotNull LoadStrategy strategy) {
        Args.notNull(instance, "instance");
        Args.notNull(config, "config");
        Args.notNull(strategy, "strategy");

        Class<T> clazz = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(clazz);
        load(bean, config, instance, strategy);
    }

    @Override
    public void loadStatic(@NotNull Class<?> clazz, @NotNull LoadStrategy strategy) {
        Args.notNull(clazz, "clazz");
        Args.notNull(strategy, "strategy");

        ConfigBean<?> bean = bind(clazz);
        Config config = resolveDocument(bean);
        load(bean, config, null, strategy);
    }

    @Override
    public void loadStatic(@NotNull Class<?> clazz, @NotNull Config config, @NotNull LoadStrategy strategy) {
        Args.notNull(clazz, "clazz");
        Args.notNull(config, "config");
        Args.notNull(strategy, "strategy");

        ConfigBean<?> bean = bind(clazz);
        load(bean, config, null, strategy);
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

    private <T> void load(
            @NotNull ConfigBean<T> bean,
            @NotNull Config config,
            @Nullable T instance,
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

    }

    @Override
    public <T> void write(@NotNull T instance, @NotNull Config config) {
        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        write(bean, config, instance);
    }

    @Override
    public <T> void write(@NotNull Class<T> clazz, @NotNull Config config) {
        ConfigBean<T> bean = bind(clazz);
        write(bean, config, null);
    }

    @Override
    public <T> void save(@NotNull T instance) throws IOException {
        Args.notNull(instance, "instance");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        Document config = resolveDocument(bean);
        writeAndSave(bean, config, instance);
    }

    @Override
    public <T> void save(@NotNull T instance, @NotNull Document document) throws IOException {
        Args.notNull(instance, "instance");
        Args.notNull(document, "config");

        Class<T> type = (Class<T>) instance.getClass();
        ConfigBean<T> bean = bind(type);
        writeAndSave(bean, document, instance);
    }

    @Override
    public <T> void saveStatic(@NotNull Class<T> clazz) throws IOException {
        Args.notNull(clazz, "clazz");

        ConfigBean<?> bean = createBean(clazz);
        Document config = resolveDocument(bean);
        writeAndSave(bean, config, null);
    }

    @Override
    public <T> void saveStatic(@NotNull Class<T> clazz, @NotNull Document document) throws IOException {
        Args.notNull(clazz, "clazz");
        Args.notNull(document, "config");

        ConfigBean<?> bean = createBean(clazz);
        writeAndSave(bean, document, null);
    }

    private <T> void write(@NotNull ConfigBean<T> bean, @NotNull Config config, @Nullable T instance) {
        ConfigSection section = resolveSection(config, bean.section());
        bean.write(instance, section);
    }

    private <T> void writeAndSave(@NotNull ConfigBean<T> bean, @NotNull Document config, @Nullable T instance)
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
                source.section().isEmpty() ? null : source.section(),
                source.saveDefaults(),
                source.allowMissingResource(),
                source.headerComments(),
                source.footerComments()
        );
    }

    @VisibleForTesting
    @NotNull
    private ConfigSection resolveSection(@NotNull Config config, @Nullable String section) {
        if (section == null || section.isEmpty()) {
            return config;
        }

        ConfigSection sec = config.getSectionIfExists(section);
        return sec != null ? sec : config.createSection(section);
    }

    @VisibleForTesting
    @NotNull
    Document resolveDocument(@NotNull ConfigBean<?> bean) {
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
