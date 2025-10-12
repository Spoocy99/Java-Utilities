package dev.spoocy.utils.config;

import dev.spoocy.utils.common.Version.Version;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.scheduler.Scheduler;
import dev.spoocy.utils.common.scheduler.task.Task;
import dev.spoocy.utils.config.misc.SectionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Document extends Config {

    static Document readPath(@NotNull Class<? extends Config> documentClass, @NotNull Path path) {
		return Config.readPath(documentClass, path).setPath(path);
	}

    static Document readFile(@NotNull Class<? extends Config> documentClass, @NotNull File file) {
        return Config.readFile(documentClass, file).setPath(file);
    }

    /**
     * Gets the configuration this document is associated with.
     *
     * @return the document
     */
    @NotNull
    Config getConfig();

    /**
     * Gets the file this document is associated with.
     *
     * @return the file
     */
    @NotNull
    File getFile();

    /**
     * Gets the path of the file this document is associated with.
     *
     * @return the path of the file
     */
    @NotNull
    Path getPath();

    /**
     * Rereads the file and loads the configuration.
     */
    void reload();

    /**
     * Saves the file to the given location.
     *
     * @throws IOException if an error occurs while saving the file
     */
    default void save() throws IOException {
        getConfig().save(getFile());
    }

    /**
     * Saves the file to the watched location.
     *
     * @return {@code true} if the save operation was successful, otherwise {@code false}
     */
    default boolean saveSafely() {
        try {
            save(getFile());
            return true;
        } catch (IOException e) {
            ILogger.forThisClass().error("An error occurred while saving document at " + getFile().getPath(), e);
            return false;
        }
    }

    /**
     * Saves the file to the given location asynchronously.
     *
     * @return a task for handling the result of the save operation
     */
    default Task<Void> saveAsync() {
        return Scheduler.runAsyncCallable(() -> {
            save();
            return null;
        });
    }

    @Override
    default boolean isCommentable() {
        return getConfig() instanceof Commentable;
    }

    @Override
    default Commentable asCommentable() {
        return (Commentable) getConfig();
    }

    @Override
    default Config getParent() {
        return getConfig().getParent();
    }

    @Override
    default void write(@NotNull Writer writer) throws IOException {
        getConfig().write(writer);
    }

    @Override
    default Config getSection(@NotNull String path) {
        return getConfig().getSection(path);
    }

    @Override
    default SectionList<? extends Config> getSectionArray(@NotNull String path) {
        return getConfig().getSectionArray(path);
    }

    @Override
    default void setReadOnly() {
        getConfig().setReadOnly();
    }

    @Override
    default boolean isReadonly() {
        return getConfig().isReadonly();
    }

    @Override
    default void set(@NotNull String path, @Nullable Object value) {
        getConfig().set(path, value);
    }

    @Override
    default void remove(@NotNull String path) {
        getConfig().remove(path);
    }

    @Override
    default void clear() {
        getConfig().clear();
    }

    @Override
    default void opposite(@NotNull String path) {
        getConfig().opposite(path);
    }

    @Override
    default void multiply(@NotNull String path, double value) {
        getConfig().multiply(path, value);
    }

    @Override
    default void divide(@NotNull String path, double value) {
        getConfig().divide(path, value);
    }

    @Override
    default void add(@NotNull String path, double value) {
        getConfig().add(path, value);
    }

    @Override
    default void subtract(@NotNull String path, double value) {
        getConfig().subtract(path, value);
    }

    @Override
    default boolean isString(@NotNull String path) {
        return getConfig().isString(path);
    }

    @Override
    default boolean isInt(@NotNull String path) {
        return getConfig().isInt(path);
    }

    @Override
    default boolean isDouble(@NotNull String path) {
        return getConfig().isDouble(path);
    }

    @Override
    default boolean isFloat(@NotNull String path) {
        return getConfig().isFloat(path);
    }

    @Override
    default boolean isLong(@NotNull String path) {
        return getConfig().isLong(path);
    }

    @Override
    default boolean isBoolean(@NotNull String path) {
        return getConfig().isBoolean(path);
    }

    @Override
    @Nullable
    default Object getObject(@NotNull String path) {
        return getConfig().getObject(path);
    }

    @Override
    default Object getObject(@NotNull String path, @Nullable Object defaultValue) {
        return getConfig().getObject(path, defaultValue);
    }

    @Override
    default <T> T get(@NotNull String path, @NotNull Class<T> clazz) {
        return getConfig().get(path, clazz);
    }

    @Override
    default  <T> T get(@NotNull String path, @Nullable T defaultValue) {
        return getConfig().get(path, defaultValue);
    }

    @Override
    default  <T> T getSerializable(@NotNull String path, @NotNull T defaultValue) {
        return getConfig().getSerializable(path, defaultValue);
    }

    @Override
    default <T> @Nullable T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return getConfig().getSerializable(path, clazz);
    }

    @Override
    default String getString(@NotNull String path) {
        return getConfig().getString(path);
    }

    @Override
    default String getString(@NotNull String path, @NotNull String defaultValue) {
        return getConfig().getString(path, defaultValue);
    }

    @Override
    default @NotNull List<String> getStringList(@NotNull String path) {
        return getConfig().getStringList(path);
    }

    @Override
    default int getInt(@NotNull String path) {
        return getConfig().getInt(path);
    }

    @Override
    default int getInt(@NotNull String path, int defaultValue) {
        return getConfig().getInt(path, defaultValue);
    }

    @NotNull
    @Override
    default List<Integer> getIntegerList(@NotNull String path) {
        return getConfig().getIntegerList(path);
    }

    @Override
    default double getDouble(@NotNull String path) {
        return getConfig().getDouble(path);
    }

    @Override
    default double getDouble(@NotNull String path, double defaultValue) {
        return getConfig().getDouble(path, defaultValue);
    }

    @NotNull
    @Override
    default List<Double> getDoubleList(@NotNull String path) {
        return getConfig().getDoubleList(path);
    }

    @Override
    default float getFloat(@NotNull String path) {
        return getConfig().getFloat(path);
    }

    @Override
    default float getFloat(@NotNull String path, float defaultValue) {
        return getConfig().getFloat(path, defaultValue);
    }

    @NotNull
    @Override
    default List<Float> getFloatList(@NotNull String path) {
        return getConfig().getFloatList(path);
    }

    @Override
    default long getLong(@NotNull String path) {
        return getConfig().getLong(path);
    }

    @Override
    default long getLong(@NotNull String path, long defaultValue) {
        return getConfig().getLong(path, defaultValue);
    }

    @NotNull
    @Override
    default List<Long> getLongList(@NotNull String path) {
        return getConfig().getLongList(path);
    }

    @Override
    default boolean getBoolean(@NotNull String path) {
        return getConfig().getBoolean(path);
    }

    @Override
    default boolean getBoolean(@NotNull String path, boolean defaultValue) {
        return getConfig().getBoolean(path, defaultValue);
    }

    @NotNull
    @Override
    default List<Boolean> getBooleanList(@NotNull String path) {
        return getConfig().getBooleanList(path);
    }

    @Override
    default Class<?> getClass(@NotNull String path) {
        return getConfig().getClass(path);
    }

    @Override
    default Class<?> getClass(@NotNull String path, @Nullable Class<?> defaultValue) {
        return getConfig().getClass(path, defaultValue);
    }

    @Override
    default  <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return getConfig().getEnum(path, clazz);
    }

    @Override
    default  <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        return getConfig().getEnum(path, clazz, defaultValue);
    }

    @Override
    default UUID getUUID(@NotNull String path) {
        return getConfig().getUUID(path);
    }

    @Override
    default UUID getUUID(@NotNull String path, @Nullable UUID defaultValue) {
        return getConfig().getUUID(path, defaultValue);
    }

    @Override
    default Date getDate(@NotNull String path) {
        return getConfig().getDate(path);
    }

    @Override
    default Date getDate(@NotNull String path, @Nullable Date defaultValue) {
        return getConfig().getDate(path, defaultValue);
    }

    @Override
    default OffsetDateTime getOffsetDateTime(@NotNull String path) {
        return getConfig().getOffsetDateTime(path);
    }

    @Override
    default OffsetDateTime getOffsetDateTime(@NotNull String path, @Nullable OffsetDateTime defaultValue) {
        return getConfig().getOffsetDateTime(path, defaultValue);
    }

    @Override
    default Version getVersion(@NotNull String path) {
        return getConfig().getVersion(path);
    }

    @Override
    default Version getVersion(@NotNull String path, @Nullable Version defaultValue) {
        return getConfig().getVersion(path, defaultValue);
    }

    @Override
    default boolean isSet(@NotNull String path) {
        return getConfig().isSet(path);
    }

    @Override
    default boolean isOf(@NotNull String path, @NotNull Class<?> clazz) {
        return getConfig().isOf(path, clazz);
    }

    @Override
    default boolean isList(@NotNull String path) {
        return getConfig().isList(path);
    }

    @Override
    @Nullable
    default List<?> getList(@NotNull String path) {
        return getConfig().getList(path);
    }

    @Override
    @Nullable
    default List<?> getList(@NotNull String path, @Nullable List<?> defaultValue) {
        return getConfig().getList(path, defaultValue);
    }

    @Override
    default <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        return getConfig().getList(path, clazz, defaultValue);
    }

    @Override
    default Collection<String> keys() {
        return getConfig().keys();
    }

    @Override
    default Map<String, Object> values() {
        return getConfig().values();
    }

    @Override
    default Map<String, String> valuesAsString() {
        return getConfig().valuesAsString();
    }

    @Override
    default String toJson() {
        return getConfig().toJson();
    }
}
