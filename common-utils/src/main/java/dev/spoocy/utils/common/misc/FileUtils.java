package dev.spoocy.utils.common.misc;

import dev.spoocy.utils.common.exceptions.WrappedException;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class FileUtils {

    private static final char PACKAGE_SEPARATOR = '.';
    private static final char PATH_SEPARATOR = '/';
    private static final char FOLDER_SEPARATOR_CHAR = '/';
    private static final String FOLDER_SEPARATOR = String.valueOf(FOLDER_SEPARATOR_CHAR);
    private static final char WINDOWS_FOLDER_SEPARATOR_CHAR = '\\';
    private static final String WINDOWS_FOLDER_SEPARATOR = String.valueOf(WINDOWS_FOLDER_SEPARATOR_CHAR);
    private static final String DOUBLE_BACKSLASHES = "\\\\";
    private static final String CURRENT_PATH = ".";
    private static final String TOP_PATH = "..";
    private static final String DOT_CHAR = ".";

    public static String getPath(@NotNull String path, char separator) {
        return path.replace(File.separatorChar, separator);
    }

    public static String getPath(@NotNull Path path, char separator) {
        return path.toString()
                .replace(File.separatorChar, separator);
    }

    public static String getPath(@NotNull File file, char separator) {
        return file.getPath()
                .replace(File.separatorChar, separator);
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

    public static String getFileName(@NotNull File file, boolean withExtension) {
        return getFileName(file.getName(), withExtension);
    }

    public static String getFileName(@NotNull Path path, boolean withExtension) {
        return getFileName(path.toString(), withExtension);
    }

    public static String getFileName(@NotNull String path, boolean withExtension) {
        path = removePath(path);

        if (withExtension) {
            return path;
        }

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
            return;
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

    public static void copy(byte[] input, @NotNull OutputStream output) throws IOException {
        output.write(input);
        output.flush();
    }

    public static void copy(@NotNull InputStream input, @NotNull OutputStream output) throws IOException {
        copy(input, output, new byte[8192]);
    }

    public static void copy(@NotNull InputStream input, @NotNull OutputStream output, byte[] buffer)
            throws IOException {
        copy(input, output, buffer, null);
    }

    public static void copy(
            @NotNull InputStream inputStream,
            @NotNull OutputStream outputStream,
            byte[] buffer,
            @Nullable Consumer<Integer> lengthInputListener
    ) throws IOException {
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
     * @param path    the path to save the file to
     * @param in      the input stream to save
     * @param replace whether to replace the file if it already exists
     *
     * @return true if the file was saved / overwritten successfully, false otherwise
     *
     * @throws IOException if an error occurs while saving the file
     */
    public static boolean save(@NotNull Path path, @NotNull InputStream in, boolean replace) throws IOException {
        if (Files.exists(path)) {
            if (!replace) {
                return false;
            }
            deleteFile(path);
        }

        createDirectory(path.getParent());
        try (OutputStream out = Files.newOutputStream(path)) {
            copy(in, out);
        }

        return true;
    }

    public static String getJarFile(@NotNull Class<?> clazz) {
        return clazz.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile();
    }

    public static String getJarPath(@NotNull Class<?> clazz) {
        String jarPath = null;

        try {
            jarPath = clazz.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath()
                    .replaceAll(" ", "%20");
        } catch (URISyntaxException e) {
            ILogger.forThisClass()
                    .error("Failed to get jar path. ", e);
        }

        return "jar:file:" + jarPath;
    }

    @Nullable
    public static InputStream getResource(@NotNull Class<?> loader, @NotNull String resourcePath) {
        try {
            URL url = loader.getClassLoader()
                    .getResource(resourcePath);
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

    public static List<Path> listResources(@NotNull Class<?> loader, @NotNull String directory) throws IOException {
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

    public static void saveResource(
            @NotNull Class<?> loader,
            @NotNull String resourcePath,
            @NotNull String path,
            boolean replace
    ) throws IOException {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(loader, resourcePath);

        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found.");
        }

        save(Path.of(path), in, replace);
    }

    public static void createParentDirs(@NotNull File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static byte[] copyToByteArray(@NotNull InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(inputStream, output);
        return output.toByteArray();
    }

    public static String copyToString(@NotNull InputStreamReader inputStreamReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[8192];
        int read;

        while ((read = inputStreamReader.read(buffer)) != -1) {
            sb.append(buffer, 0, read);
        }

        return sb.toString();
    }

    public static URI toURI(@NotNull String path) throws URISyntaxException {
        return new URI(cleanPath(path));
    }

    public static URL toURL(@NotNull String path) throws MalformedURLException {
        try {
            return toURI(cleanPath(path)).toURL();
        } catch (URISyntaxException | IllegalArgumentException ex) {
            return new URL(path);
        }
    }

    public static boolean isFileURL(@NotNull URL url) {
        return url.getProtocol()
                .equals("file");
    }

    public static boolean isJarURL(@NotNull URL url) {
        return url.getProtocol()
                .equals("jar");
    }

    public static String applyRelativePath(@NotNull String path, @NotNull String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR_CHAR);

        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR_CHAR;
            }

            return newPath + relativePath;
        }

        return relativePath;
    }

    public static URL toRelativeURL(URL root, String relativePath) throws MalformedURLException {
		relativePath = StringUtils.replace(relativePath, "#", "%23");
		return new URL(root, FileUtils.cleanPath(FileUtils.applyRelativePath(root.toString(), relativePath)));
	}

    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();

        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) {
            return "";
        }

        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    public static String cleanPath(@NotNull String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return path;
        }

        String normalizedPath;

        if (path.indexOf(WINDOWS_FOLDER_SEPARATOR_CHAR) != -1) {
            normalizedPath = StringUtils.replace(path, DOUBLE_BACKSLASHES, FOLDER_SEPARATOR);
            normalizedPath = StringUtils.replace(normalizedPath, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        } else {
            normalizedPath = path;
        }


        String pathToUse = normalizedPath;

        // path doesn't contain "." or ".." so skip
        if (!pathToUse.contains(DOT_CHAR)) {
            return pathToUse;
        }

        // We need to parse the path element by element, and deal with special folders based on their names
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";

        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);

            if (prefix.contains(FOLDER_SEPARATOR)) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }

        }

        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = StringUtils.tokenizeToStringArray(pathToUse, FOLDER_SEPARATOR);

        // we never require more elements than pathArray and in the common case the same number
        Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];

            if (CURRENT_PATH.equals(element)) {
                continue;
            }

            if (TOP_PATH.equals(element)) {
                // Registering top path found
                tops++;
                continue;
            }


            if (tops > 0) {
                // Merging path element with registered top path
                tops--;
                continue;
            }
            // Normal path element found
            pathElements.addFirst(element);
        }

        // If nothing needs to be retained, return the normalized path
        if (pathArray.length == pathElements.size()) {
            return normalizedPath;
        }

        // Remaining top path need to be retained. Adding such number of top paths to the start of the path
        for (int i = 0; i < tops; i++) {
            pathElements.addFirst(TOP_PATH);
        }

        // If the path is now empty, there was only references to current path
        if (pathElements.size() == 1 && pathElements.getLast()
                .isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
            pathElements.addFirst(CURRENT_PATH);
        }

        String joined = StringUtils.collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
        return (prefix.isEmpty() ? joined : prefix + joined);
    }
}
