package dev.spoocy.utils.config.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class PathResource implements WriteableResource {

    private final Path path;

    public PathResource(@NotNull Path path) {
		this.path = path.normalize();
	}

    @Override
    public boolean exists() {
        return Files.exists(this.path);
    }

    @Override
    public boolean isReadable() {
        return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
    }

    @Override
	public URL getURL() throws IOException {
		return this.path.toUri().toURL();
	}

	@Override
	public URI getURI() throws IOException {
		return this.path.toUri();
	}

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public @Nullable String getFilename() {
        return this.path.getFileName().toString();
    }

    @Override
	public File getFile() {
		return this.path.toFile();
	}

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        if (!exists()) {
			throw new FileNotFoundException(getPath() + " does not exist.");
		}

		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " is a directory.");
		}

		return Files.newInputStream(this.path);
    }

    @Override
	public byte[] getContentAsByteArray() throws IOException {
		try {
			return Files.readAllBytes(this.path);
		}
		catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

    @Override
    public String getContentAsString(@NotNull Charset charset) throws IOException {
        try {
			return Files.readString(this.path, charset);
		}
		catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
    }

    @Override
	public long contentLength() throws IOException {
		return Files.size(this.path);
	}

	@Override
	public long lastModified() throws IOException {
		return Files.getLastModifiedTime(this.path).toMillis();
	}

    @Override
	public Resource createRelative(@NotNull String relativePath) {
		return new PathResource(this.path.resolve(relativePath));
	}

    @Override
	public boolean isWritable() {
		return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
	}

    @Override
	public OutputStream getOutputStream() throws IOException {
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " is a directory.");
		}

		return Files.newOutputStream(this.path);
	}

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public String toString() {
        return "Path resource [" + this.path.toAbsolutePath() + "]";
    }

}
