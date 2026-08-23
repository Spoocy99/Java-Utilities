package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.io.ClassPathResource;
import dev.spoocy.utils.config.io.FileSystemResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BaseResourceResolver implements ResourceResolver {

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

    @Override
    public @NotNull Config createEmpty(@NotNull Resource resource) {
        return requireLoader(resource).createEmpty();
    }

    @Override
    public @Nullable ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public @NotNull Resource resolve(@NotNull String location) {
        Resource resource = getByPrefix(location);
        if (resource != null) {
            return resource;
        }

        return Resources.fromPath(location);
    }

    @Nullable
    protected Resource getByPrefix(@NotNull String location) {
        Args.notNull(location, "location");

        if (location.startsWith(CLASSPATH_FILE_PREFIX)) {
            String path = location.substring(CLASSPATH_FILE_PREFIX.length());
            return new ClassPathResource(path, this.classLoader);
        }

        if (location.startsWith(EXTERNAL_FILE_PREFIX)) {
            String path = location.substring(EXTERNAL_FILE_PREFIX.length());
            return new FileSystemResource(path);
        }
        return null;
    }

    @Nullable
    protected ConfigLoader<?, ?> resolveLoaderByExtension(@NotNull String extension) {
        return this.loaders.get(extension.toLowerCase());
    }

    @Override
    public @Nullable ConfigLoader<? extends Config, ?> resolveLoader(@NotNull Resource resource) {
        String filename = resource.getFilename();
        String extension = filename == null ? "" : FileUtils.getFileExtension(filename);
        return resolveLoaderByExtension(extension);
    }

    @Override
    public @NotNull ConfigLoader<?, ?> requireLoader(@NotNull Resource resource) {
        ConfigLoader<?, ?> loader = resolveLoader(resource);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader for resource " + resource);
        }
        return loader;
    }
}
