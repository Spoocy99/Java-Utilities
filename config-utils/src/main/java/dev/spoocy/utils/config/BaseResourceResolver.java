package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.ClassPathResource;
import dev.spoocy.utils.config.io.FileSystemResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.loader.AnnotatedConfigLoader;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.loader.ConfigSource;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.update.ConfigUpdater;
import dev.spoocy.utils.reflection.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BaseResourceResolver implements ResourceResolver {

    private static final AnnotatedConfigLoader CONFIG_LOADER = new AnnotatedConfigLoader();
    private static final String CLASSPATH_FILE_PREFIX = "classpath:";
    private static final String EXTERNAL_FILE_PREFIX = "file:";

    private final Map<String, ConfigLoader<?, ?>> loaders = new HashMap<>();

    @Nullable
    private final ClassLoader classLoader;

    public BaseResourceResolver(@Nullable ClassLoader classLoader, @NotNull ConfigLoader<?, ?>... loader) {
        this.classLoader = classLoader;

        for (ConfigLoader<?, ?> configLoader : loader) {
            registerLoader(configLoader);
        }
    }

    public void registerLoader(@NotNull ConfigLoader<?, ?> loader) {
        Args.notNull(loader, "loader");

        for (String extension : loader.getSupportedExtensions()) {
            this.loaders.put(extension.toLowerCase(), loader);
        }
    }

    protected Config createEmpty(@NotNull Resource resource) {
        return requireLoader(resource).createEmpty();
    }

    @Override
    public @NotNull Resource resolve(@NotNull String location) {
        Args.notNull(location, "location");

        if (location.startsWith(CLASSPATH_FILE_PREFIX)) {
            String path = location.substring(CLASSPATH_FILE_PREFIX.length());
            return new ClassPathResource(path, this.classLoader);
        }

        if (location.startsWith(EXTERNAL_FILE_PREFIX)) {
            String path = location.substring(EXTERNAL_FILE_PREFIX.length());
            return new FileSystemResource(path);
        }

        return Resources.fromPath(location);
    }

    @Nullable
    protected ConfigLoader<? extends Config, ?> resolveLoader(@NotNull Resource resource) {
        String filename = resource.getFilename();
        String extension = filename == null ? "" : FileUtils.getFileExtension(filename);
        return resolveLoaderByExtension(extension);
    }

    @Nullable
    protected ConfigLoader<?, ?> resolveLoaderByExtension(@NotNull String extension) {
        return this.loaders.get(extension.toLowerCase());
    }

    @NotNull
    protected ConfigLoader<?, ?> requireLoader(@NotNull Resource resource) {
        ConfigLoader<?, ?> loader = resolveLoader(resource);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader for resource " + resource);
        }
        return loader;
    }

    @Override
    public @NotNull Config loadConfig(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException {
        Args.notNull(resource, "resource");
        Args.notNull(constructor, "constructor");

        ConfigLoader<? extends Config, ?> loader = requireLoader(resource);
        return loader.load(resource, constructor);
    }

    @Override
    public @NotNull Config loadConfig(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @NotNull ConfigUpdater updater
    ) throws IOException {
        Config config = loadConfig(resource, constructor);
        updater.run(config);
        return config;
    }

    @Override
    public @NotNull Document loadDocument(@NotNull Resource resource, @NotNull Constructor constructor)
            throws IOException {
        return loadConfig(resource, constructor).withRelation(resource);
    }

    @Override
    public @NotNull Document loadDocument(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @NotNull ConfigUpdater updater
    ) throws IOException {
        Document document = loadDocument(resource, constructor);

        if (updater.run(document) > 0) {

            Resource relation = document.getRelation();

            if (relation instanceof WriteableResource) {
                document.save((WriteableResource) relation);
            }

        }

        return document;
    }

    @Override
    public @NotNull <T> T load(@NotNull Class<T> type, @NotNull Readable readable) {
        return CONFIG_LOADER.load(type, readable);
    }

    @Override
    public @NotNull <T> T load(@NotNull Class<T> type, @NotNull Constructor constructor) throws IOException {
        Args.notNull(type, "type");
        Args.notNull(constructor, "constructor");

        ConfigSource source = requireSource(type);
        Resource resource = resolveSourceResource(type, source);
        return load(type, constructor, resource, source);
    }

    @Override
    public @NotNull <T> T load(@NotNull Class<T> type, @NotNull Constructor constructor, @NotNull Resource resource)
            throws IOException {
        Args.notNull(type, "type");
        Args.notNull(constructor, "constructor");
        Args.notNull(resource, "resource");

        ConfigSource source = Reflection.getAnnotation(type, ConfigSource.class, false);
        return load(type, constructor, resource, source);
    }

    @NotNull
    private <T> T load(
            @NotNull Class<T> type,
            @NotNull Constructor constructor,
            @NotNull Resource resource,
            @Nullable ConfigSource source
    ) throws IOException {
        Document document = loadAnnotatedDocument(resource, constructor, source);
        AnnotatedConfigLoader.LoadResult<T> result = CONFIG_LOADER.loadResult(type, document);

        if (result.mutations() > 0) {
            Resource relation = document.getRelation();
            if (relation instanceof WriteableResource) {
                document.save((WriteableResource) relation);
            }
        }

        return result.instance();
    }

    @NotNull
    private Document loadAnnotatedDocument(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @Nullable ConfigSource source
    ) throws IOException {
        if (source != null && source.allowMissingResource() && !resource.exists()) {
            return createEmpty(resource).withRelation(resource);
        }

        return loadDocument(resource, constructor);
    }

    @Override
    public void save(@NotNull Object instance, @NotNull Representer representer) throws IOException {
        Args.notNull(instance, "instance");
        Args.notNull(representer, "representer");

        ConfigSource source = requireSource(instance.getClass());
        Resource resource = resolveSourceResource(instance.getClass(), source);
        save(instance, representer, resource);
    }

    @Override
    public void save(@NotNull Object instance, @NotNull Representer representer, @NotNull Resource resource) throws IOException {
        Args.notNull(instance, "instance");
        Args.notNull(resource, "resource");

        Document document = createEmpty(resource).withRelation(resource);
        Resource relation = document.getRelation();

        if (!(relation instanceof WriteableResource)) {
            throw new IllegalArgumentException("Resource is not writeable: " + resource);
        }

        CONFIG_LOADER.write(instance, document);
        document.save((WriteableResource) relation, representer);
    }

    @NotNull
    private static ConfigSource requireSource(@NotNull Class<?> type) {
        ConfigSource source = Reflection.getAnnotation(type, ConfigSource.class, false);
        if (source == null) {
            throw new IllegalArgumentException("Missing @ConfigSource for class: " + type.getName());
        }
        return source;
    }

    @NotNull
    private Resource resolveSourceResource(@NotNull Class<?> type, @NotNull ConfigSource source) {
        String location = source.value().trim();
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Missing @ConfigSource(value) for class: " + type.getName());
        }

        return resolve(location);
    }
}
