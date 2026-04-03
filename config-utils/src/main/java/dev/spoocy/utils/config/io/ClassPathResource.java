package dev.spoocy.utils.config.io;

import dev.spoocy.utils.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Classpath based {@link Resource} implementation.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class ClassPathResource extends ResolvableResource {

    @NotNull
    private final String path;

    @NotNull
    private final String absolutePath;

    @Nullable
    private final ClassLoader classLoader;

    @Nullable
    private final Class<?> clazz;

    public ClassPathResource(@NotNull String path, @Nullable Class<?> clazz) {
		this.path = FileUtils.cleanPath(path);

		String absolutePath = this.path;
		if (clazz != null && !absolutePath.startsWith("/")) {
			absolutePath = FileUtils.classPackageAsResourcePath(clazz) + "/" + absolutePath;
		} else if (absolutePath.startsWith("/")) {
			absolutePath = absolutePath.substring(1);
		}

		this.absolutePath = absolutePath;
		this.classLoader = null;
		this.clazz = clazz;
	}

    public ClassPathResource(@NotNull String path, @Nullable ClassLoader classLoader) {

		String cleaned = FileUtils.cleanPath(path);
		if (cleaned.startsWith("/")) {
			cleaned = cleaned.substring(1);
		}

		this.path = cleaned;
		this.absolutePath = cleaned;
		this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
		this.clazz = null;
    }

    @Nullable
    private URL resolveURL() {
		try {
			if (this.clazz != null) {
				return this.clazz.getResource(this.path);
			}

            if (this.classLoader != null) {
				return this.classLoader.getResource(this.absolutePath);
			}

			return ClassLoader.getSystemResource(this.absolutePath);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

    @Override
    protected String getDescription() {
        return "ClassPath Resource [" + this.path + "]";
    }

    @Override
	public boolean exists() {
		return resolveURL() != null;
	}

    @Override
    public URL getURL() throws IOException {
        URL url = this.resolveURL();

        if (url == null) {
            throw new FileNotFoundException("Resource not found: " + this.path);
        }

        return url;
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return getURL().toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI syntax: " + getURL(), e);
        }
    }

    @Override
    public boolean isFile() {
        URL url = this.resolveURL();
        return url != null && FileUtils.isFileURL(url);
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException("Classpath resource cannot be resolved to absolute file path: " + this.path);
    }

    @Override
    public @NotNull String getFilename() {
        return FileUtils.getFileName(this.path, true);
    }

    @Override
    public Path getPath() {
        return Path.of(this.absolutePath);
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = this.resolveURL();
            return url != null && super.isReadable(url);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        InputStream in;

        if (this.clazz != null) {
            in = this.clazz.getResourceAsStream(this.path.startsWith("/") ? this.path : "/" + this.path);

        } else if (this.classLoader != null) {
            in = this.classLoader.getResourceAsStream(this.path.startsWith("/") ? this.path.substring(1) : this.path);

        } else {
            in = getDefaultClassLoader().getResourceAsStream(this.path.startsWith("/") ? this.path.substring(1) : this.path);
        }

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + this.path);
        }

        return in;
    }

    @Override
	public Resource createRelative(@NotNull String relativePath) {
		String pathToUse = FileUtils.applyRelativePath(this.path, relativePath);
		return (this.clazz != null ? new ClassPathResource(pathToUse, this.clazz) :
				new ClassPathResource(pathToUse, this.classLoader));
	}

    @Override
    protected boolean shouldApplyCaches() {
        return false;
    }

    @Override
    public int hashCode() {
        return this.absolutePath.hashCode();
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathResource.class.getClassLoader();
        }
        return cl;
    }
}
