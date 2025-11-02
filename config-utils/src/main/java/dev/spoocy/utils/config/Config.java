package dev.spoocy.utils.config;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.misc.FileUtils;
import dev.spoocy.utils.config.documents.DocumentFile;
import dev.spoocy.utils.config.documents.JsonConfig;
import dev.spoocy.utils.config.misc.SectionList;
import dev.spoocy.utils.reflection.Reflection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Config extends Writeable {

    /**
     * Creates an empty instance of the default file type.
     *
     * @return an empty config
     */
    static Config create() {
        return new JsonConfig();
    }

    /**
     * Creates an empty instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     *
     * @return an empty config
     */
    static Config create(@NotNull Class<? extends Config> configClass) {
        try {
            return (Config) Reflection.getConstructor(configClass).invoke();
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Unable to create empty instance of " + configClass.getName() + ".", e);
        }
    }

    /**
     * Reads the file at the given {@link Path} and creates an instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     * @param path the path to the file to read
     *
     * @return an instance of the given file type
     *
     * @see #readPath(Class, Path)
     */
    static Config readPath(@NotNull Class<? extends Config> configClass, @NotNull String path) {
        return readPath(configClass, Paths.get(path));
    }

    /**
     * Reads the file at the given {@link Path} and creates an instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     * @param path the path to the file to read
     *
     * @return an instance of the given file type
     *
     * @see #readFile(Class, File)
     */
    static Config readPath(@NotNull Class<? extends Config> configClass, @NotNull Path path) {
        return readFile(configClass, path.toFile());
    }

    /**
     * Reads the file and creates an instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     * @param file the file to read
     *
     * @return an instance of the given file type
     *
     * @throws UnsupportedOperationException if the config cannot read files
     */
    static Config readFile(@NotNull Class<? extends Config> configClass, @NotNull File file) {
        try {
            return (Config) Reflection.getConstructor(configClass, File.class).invoke(file);
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Unable to create instance of " + configClass.getName() + " using file.", e);
        }
    }

    /**
     * Reads the {@link InputStream} and creates an instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     * @param stream the input stream to read
     *
     * @return an instance of the given file type
     *
     * @throws UnsupportedOperationException if the config cannot read the InputStreams
     */
    static @NotNull Config readInputStream(@NotNull Class<? extends Config> configClass, @NotNull InputStream stream) {
        try {
            return (Config) Reflection.getConstructor(configClass, InputStream.class).invoke(stream);
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Unable to create instance of " + configClass.getName() + " using InputStream.", e);
        }
    }

    /**
     * Reads the object and creates an instance of the given file type.
     *
     * @param configClass the class of the Config to create an instance of
     * @param read the object to read
     *
     * @return an instance of the given file type
     *
     * @throws UnsupportedOperationException if the config cannot read the object
     */
    static Config readObject(@NotNull Class<? extends Config> configClass, @NotNull Object read) {
        try {
            return (Config) Reflection.getConstructor(configClass, Object.class).invoke(read);
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Unable to create instance of " + configClass.getName() + " using object.", e);
        }
    }

    /**
     * Creates a new Document instance.
     *
     * @param config the document to watch
     * @param file the file to watch
     *
     * @return the new WatchedDocument
     */
    static Document createDocument(@NotNull Config config, @NotNull File file) {
        return new DocumentFile(config, file);
    }

    /**
     * Creates a new Document instance.
     *
     * @param config the document to watch
     * @param path the path to watch
     *
     * @return the new WatchedDocument
     */
    static Document createDocument(@NotNull Config config, @NotNull Path path) {
        return new DocumentFile(config, path);
    }

    /**
     * Converts the current Config to a Document.
     *
     * @param file the file to watch
     *
     * @return the new WatchedFile
     */
    default Document setPath(@NotNull File file) {
        return createDocument(this, file);
    }

    /**
     * Converts the current Config to a Document.
     *
     * @param path the path to the document to watch
     *
     * @return the new WatchedFile
     */
    default Document setPath(@NotNull Path path) {
        return createDocument(this, path);
    }

    /**
     * Copies the config to a new JSONConfig instance.
     *
     * @return the Document
     */
    default Config copyToJson() {
        return new JsonConfig(values());
    }

    /**
     * Executes the given consumer for each value in the config.
     *
     * @param consumer the consumer to execute with the key and the value
     */
    default void forEachValue(@NotNull BiConsumer<? super String, ? super Object> consumer) {
		values().forEach(consumer);
	}

    /**
     * Saves the config to the given path.
     *
     * @param path the path to save the document to
     *
     * @throws IOException if an error occurs while saving the config
     */
    default void save(@NotNull String path) throws IOException {
        File file = Paths.get(path).toFile();
        save(file);
    }

    /**
     * Saves the config to the given path.
     *
     * @param path the path to save the document to
     *
     * @return true if the document was saved successfully
     */
    default boolean saveSafely(@NotNull String path) {
        try {
            save(path);
            return true;
        } catch (IOException e) {
            ILogger.forThisClass().error("An error occurred while saving config at " + path, e);
            return false;
        }
    }

    /**
     * Saves the config to the given file.
     *
     * @param file the file to save the document to
     *
     * @throws IOException if an error occurs while saving the document
     */
    default void save(@NotNull File file) throws IOException {
        FileUtils.createFile(file);
        Writer writer = FileUtils.createWriter(file);
        write(writer);
        writer.flush();
        writer.close();
    }

    /**
     * Saves the config to the given file.
     *
     * @param file the file to save the document to
     *
     * @return true if the document was saved successfully
     */
    default boolean saveSafely(@NotNull File file) {
        try {
            save(file);
            return true;
        } catch (IOException e) {
            ILogger.forThisClass().error("An error occurred while saving config at " + file, e);
            return false;
        }
    }

    default boolean isCommentable() {
        return this instanceof Commentable;
    }

    default Commentable asCommentable() {
        if(!isCommentable()) {
            throw new UnsupportedOperationException("This config is not commentable.");
        }
        return (Commentable) this;
    }

    /**
     * Gets the parent config. If the document has no parent, the config itself is returned.
     *
     * @return the parent config
     */
    Config getParent();

    /**
     * Writes the content to the given writer.
     *
     * @param writer the writer to write the content to
     *
     * @throws IOException if an error occurs while writing the content
     */
    void write(@NotNull Writer writer) throws IOException;

    /**
     * Gets the section at the given path.
     *
     * @param path the path to the section
     *
     * @return the section as a Document
     */
    @Override
    Config getSection(@NotNull String path);

    /**
     * Gets an array of all sections at the given path.
     *
     * @param path the path to the section array
     *
     * @return the section array
     */
    @Override
    SectionList<? extends Config> getSectionArray(@NotNull String path);
}
