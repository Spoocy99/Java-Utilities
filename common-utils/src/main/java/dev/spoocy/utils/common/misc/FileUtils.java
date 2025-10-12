package dev.spoocy.utils.common.misc;

import dev.spoocy.utils.common.exceptions.WrappedException;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class FileUtils {

	public static String getFileName(@NotNull File file) {
		return getFileName(file.getName());
	}

	public static String getFileName(@NotNull Path path) {
		return getFileName(path.toString());
	}

    public static String getPath(@NotNull String path, char separator) {
        return path.replace(File.separatorChar, separator);
    }

    public static String getPath(@NotNull Path path, char separator) {
        return path.toString().replace(File.separatorChar, separator);
    }

    public static String getPath(@NotNull File file, char separator) {
        return file.getPath().replace(File.separatorChar, separator);
    }

    public static String removePath(@NotNull String path, @NotNull String separator) {
        if (!path.contains(separator)) {
            return path;
        }
        return StringUtils.getAfterLast(path, separator);
	}

	public static String removePath(@NotNull String path) {
        return removePath(getPath(path, '/'), "/");
	}

	public static String getFileName(@NotNull String path) {
		path = removePath(path);
		int extension = path.lastIndexOf('.');
		return extension == -1 ? path : path.substring(0, extension);
	}

	public static String getFileExtension(@NotNull File file) {
		return getFileExtension(file.getName());
	}

	public static String getFileExtension(@NotNull Path path) {
		return getFileExtension(path.toString());
	}

	public static String getFileExtension(@NotNull String path) {
        path = removePath(path);
        return StringUtils.getAfterLast(path, ".");
	}

    public static InputStream createInputStream(@NotNull Path path) throws IOException {
        return Files.newInputStream(path);
    }

    public static InputStream createInputStream(@NotNull File file) throws IOException {
        return createInputStream(file.toPath());
    }

    public static OutputStream createOutputStream(@NotNull Path path) throws IOException {
        return Files.newOutputStream(path);
    }

    public static OutputStream createOutputStream(@NotNull File file) throws IOException {
        return createOutputStream(file.toPath());
    }

    public static Writer createWriter(@NotNull Path path) throws IOException {
        return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
    }

    public static Writer createWriter(@NotNull File file) throws IOException {
        return createWriter(file.toPath());
    }

    public static void createDirectory(@NotNull Path path) throws IOException {
		if (!Files.notExists(path)) return;
        Files.createDirectories(path);
	}

    public static void createFile(@NotNull File file) throws IOException {
		if (file.exists()) return;

		if (file.isDirectory()) {
			file.mkdirs();
		}

		file.createNewFile();
	}

	public static void createFile(@NotNull Path path) throws IOException {
		if (!Files.notExists(path)) return;

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        Files.createFile(path);
	}

	public static void deleteFile(@NotNull Path file) throws IOException {
        Files.deleteIfExists(file);
	}

    public static void copy(@NotNull Path from, @NotNull Path to) throws IOException {
		copy(from, to, new byte[8192]);
	}

	public static void copy(@NotNull Path from, @NotNull Path to, byte @NotNull [] buffer) throws IOException {
		if (Files.notExists(to)) {
			createDirectory(to.getParent());
		}

        InputStream stream = Files.newInputStream(from);
        OutputStream target = Files.newOutputStream(to);
        copy(stream, target, buffer);
	}

    public static void copy(@NotNull InputStream input, @NotNull OutputStream output) throws IOException {
		copy(input, output, new byte[8192]);
	}

	public static void copy(@NotNull InputStream input, @NotNull OutputStream output, byte[] buffer) throws IOException {
		copy(input, output, buffer, null);
	}

    public static void copy(@NotNull InputStream inputStream, @NotNull OutputStream outputStream, byte[] buffer, @Nullable Consumer<Integer> lengthInputListener) throws IOException {
		int len;
		while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {

			if (lengthInputListener != null) {
				lengthInputListener.accept(len);
			}

			outputStream.write(buffer, 0, len);
			outputStream.flush();
		}

	}

    /**
     * Saves the input stream to the given path.
     *
     * @param path the path to save the file to
     * @param in the input stream to save
     * @param replace whether to replace the file if it already exists
     * @return true if the file was saved / overwritten successfully, false otherwise
     *
     * @throws IOException if an error occurs while saving the file
     */
    public static boolean save(@NotNull String path, @NotNull InputStream in, boolean replace) throws IOException {
        File outFile = new File(path);
        int lastIndex = path.lastIndexOf(47);

        File outDir = new File(path.substring(0, lastIndex >= 0 ? lastIndex : 0));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (outFile.exists() && !replace) {
            return false;
        }

        OutputStream out = createOutputStream(outFile);
        byte[] buf = new byte[1024];

        int len;
        while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        out.close();
        in.close();
        return true;
    }

    public static String getJarFile(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

    public static String getJarPath(Class<?> clazz) {
        String jarPath = null;

        try {
            jarPath = clazz.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath()
                    .replaceAll(" ", "%20");
        } catch (URISyntaxException e) {
            ILogger.forThisClass().error("Failed to get jar path. ", e);
        }

        return "jar:file:" + jarPath;
    }

    public static List<Path> getResources(Class<?> loader, String directory) throws IOException {
        List<Path> result;

        String jarPath = getJarPath(loader);
        URI uri = URI.create(jarPath);

        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            result = Files.walk(fs.getPath(directory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }

        return result;

    }

    @Nullable
    public static InputStream getResource(@NotNull Class<?> loader, @NotNull String resourcePath) {
        try {
            URL url = loader.getClassLoader().getResource(resourcePath);
            if (url == null) {
                return null;

            } else {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            }
        } catch (IOException var4) {
            return null;
        }
    }

    public static void saveResource(@NotNull Class<?> loader, @NotNull String resourcePath, @NotNull String path, boolean replace) {
        if(resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(loader, resourcePath);

        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found.");
        }

        try {
            save(path, in, replace);
        } catch (IOException e) {
           throw WrappedException.wrap(e);
        }
    }

}
