package dev.spoocy.utils.config.misc;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.documents.JsonConfig;
import dev.spoocy.utils.common.misc.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class FileManager {

    private static final HashMap<String, Class<? extends Config>> types = new HashMap<>();

    static {
        registerType("json", JsonConfig.class);
    }

    private FileManager() { }

    /**
     * Registers a file type with the given extension.
     *
     * @param extension the extension of the file type
     *
     * @return the class of the file type
     *
     * @throws IllegalArgumentException if the extension can not be resolved
     */
    @NotNull
    public static Class<? extends Config> resolveType(@NotNull String extension) {
        extension = extension.toLowerCase();
        Class<? extends Config> resolved = types.get(extension);

        if (resolved == null) {
            throw new IllegalArgumentException(
                    String.format("Unable to resolve file type for extension: '%s'", extension)
            );
        }

        return resolved;
	}

    /**
     * Reads the given file as a {@link Document}.
     *
     * @param file the file to read
     *
     * @return the document
     */
    public static synchronized Document getFile(@NotNull File file) {
		String extension = FileUtils.getFileExtension(file);
		return Document.readFile(resolveType(extension), file);
	}

    /**
     * Reads the file at a given path as a {@link Document}.
     *
     * @param path the path to the file
     *
     * @return the document
     */
    public static synchronized Document getFile(@NotNull Path path) {
		String extension = FileUtils.getFileExtension(path);
		return Document.readPath(resolveType(extension), path);
	}

    /**
     * Registers a file type with an extension.
     *
     * @param extension the extension of the file type
     * @param documentClass the class of the file
     */
    public static void registerType(@NotNull String extension, Class<? extends Config> documentClass) {
        types.put(extension.toLowerCase(), documentClass);
    }


}
