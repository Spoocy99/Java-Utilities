package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.io.ClassPathResource;
import dev.spoocy.utils.config.io.FileSystemResource;
import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.UrlResource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility entry points for creating resources.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public final class Resources {

    @NotNull
    public static YamlConfigLoader yaml() {
        return YamlConfigLoader.INSTANCE;
    }

    @NotNull
    public static JsonConfigLoader json() {
        return JsonConfigLoader.INSTANCE;
    }

    @NotNull
    public static PathResource fromPath(@NotNull String path, @NotNull String... paths) {
        Args.notNull(path, "path");
        Args.notNull(paths, "paths");
        return fromPath(Paths.get(path, paths));
    }

    @NotNull
    public static PathResource fromPath(@NotNull Path path) {
        Args.notNull(path, "path");
        return new PathResource(path);
    }

    @NotNull
    public static PathResource fromPath(@NotNull String path) {
        Args.notNull(path, "path");
        return fromPath(Paths.get(path));
    }

    @NotNull
    public static PathResource fromPath(@NotNull URI path) {
        Args.notNull(path, "path");
        return fromPath(Paths.get(path));
    }

    @NotNull
    public static FileSystemResource fromFile(@NotNull String path) {
        Args.notNull(path, "path");
        return new FileSystemResource(path);
    }

    @NotNull
    public static FileSystemResource fromFile(@NotNull File file) {
        Args.notNull(file, "file");
        return new FileSystemResource(file);
    }

    @NotNull
    public static FileSystemResource fromFile(@NotNull Path path) {
        Args.notNull(path, "path");
        return new FileSystemResource(path);
    }

    @NotNull
    public static FileSystemResource fromFile(@NotNull FileSystem fileSystem, @NotNull String path) {
        Args.notNull(fileSystem, "fileSystem");
        Args.notNull(path, "path");
        return new FileSystemResource(fileSystem, path);
    }

    @NotNull
    public static ClassPathResource fromJar(@NotNull String path, Class<?> clazz) {
        Args.notNull(path, "path");
        return new ClassPathResource(path, clazz);
    }

    @NotNull
    public static ClassPathResource fromJar(@NotNull String path, ClassLoader loader) {
        Args.notNull(path, "path");
        return new ClassPathResource(path, loader);
    }

    @NotNull
    public static ClassPathResource fromJar(@NotNull String path) {
        Args.notNull(path, "path");
        return fromJar(path, (ClassLoader) null);
    }

    @NotNull
    public static UrlResource fromUrl(@NotNull URL url) {
        Args.notNull(url, "url");
        return new UrlResource(url);
    }

    @NotNull
    public static UrlResource fromUri(@NotNull URI uri) throws IOException {
        Args.notNull(uri, "uri");
        return new UrlResource(uri);
    }

    public static void copy(@NotNull Resource from, @NotNull WriteableResource to) throws IOException {
        Args.notNull(from, "from resource");
        Args.notNull(to, "to resource");
        FileUtils.copy(from.getInputStream(), to.getOutputStream());
    }

    private Resources() {
        throw new UnsupportedOperationException("Cannot instantiate utility class: " + Resources.class.getName());
    }

}
