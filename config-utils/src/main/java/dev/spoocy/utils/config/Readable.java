package dev.spoocy.utils.config;

import dev.spoocy.utils.common.version.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Readable {

    /**
     * Gets the value at the specified path as an Object.
     *
     * @param path the path to the value
     *
     * @return the value at the specified path as an Object, or {@code null} if the path does not exist or is not set
     */
    @Nullable
    Object getObject(@NotNull String path);

    /**
     * Gets the value at the specified path as an Object,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as an Object, or {@code defaultValue} if the path does not exist or is not set
     */
    @Nullable
    Object getObject(@NotNull String path, @Nullable Object defaultValue);

    /**
     * Gets the value at the specified path as an instance of the specified class.
     *
     * @param path  the path to the value
     * @param clazz the class to convert the value to
     *
     * @return the value at the specified path as an instance of the specified class,
     * or {@code null} if the path does not exist, is not set, or cannot be converted to an instance of the specified class
     */
    <T> T get(@NotNull String path, @NotNull Class<T> clazz);

    /**
     * Gets the value at the specified path as an instance of the specified class,
     * or returns a default value if the path does not exist, is not set, or cannot be converted to an instance of the specified class.
     *
     * @param path         the path to the value
     * @param clazz        the class to convert the value to
     * @param defaultValue the default value to return if the path does not exist, is not set, or cannot be converted to an instance of the specified class
     *
     * @return the value at the specified path as an instance of the specified class, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to an instance of the specified class
     */
    <T> T get(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue);

    /**
     * Checks if the value at the specified path is an instance of the specified class.
     *
     * @param path  the path to the value
     * @param clazz the class to check the value against
     *
     * @return {@code true} if the value at the specified path is an instance of the specified class, {@code false} otherwise
     */
    boolean is(@NotNull String path, @NotNull Class<?> clazz);

    /**
     * Retrieves a serializable object from the specified path. If the retrieved value is null or
     * not serializable to the expected type, the provided default value will be returned.
     *
     * @param <T>          The type of the serializable object to retrieve.
     * @param path         The path to the value in the configuration. Must not be null.
     * @param clazz        The class type to which the object should be cast. Must not be null.
     * @param defaultValue The default value to return if the retrieved object is null or cannot be serialized to the expected type. Can be null.
     *
     * @return The serializable object at the specified path, or the default value if the object
     * is null or cannot be serialized to the expected type.
     */
    <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue);

    /**
     * Retrieves a serializable object from the specified path in the configuration.
     *
     * @param path  the path to the object in the configuration (not null)
     * @param clazz the class type to which the object should be cast (not null)
     * @param <T>   the type of the object to be retrieved
     *
     * @return the deserialized object of the specified type, or {@code null} if the object
     * is not found, cannot be cast to the specified type, or deserialization fails
     */
    @Nullable
    <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz);

    /**
     * Checks if the value at the specified path is a {@link String}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a String, {@code false} otherwise
     */
    boolean isString(@NotNull String path);

    /**
     * Gets the value at the specified path as a String,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as a String, or {@code defaultValue} if the path does not exist or is not set
     */
    String getString(@NotNull String path, @Nullable String defaultValue);

    @NotNull
    default String getString(@NotNull String path) {
        return getString(path, "");
    }

    /**
     * Checks if the value at the specified path is an {@link Integer}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is an Integer, {@code false} otherwise
     */
    boolean isInt(@NotNull String path);

    /**
     * Gets the value at the specified path as an int,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as an int, or {@code defaultValue} if the path does not exist or is not set
     */
    int getInt(@NotNull String path, int defaultValue);

    default int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    /**
     * Checks if the value at the specified path is a {@link Double}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a Double, {@code false} otherwise
     */
    boolean isDouble(@NotNull String path);

    /**
     * Gets the value at the specified path as a double,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as a double, or {@code defaultValue} if the path does not exist or is not set
     */
    double getDouble(@NotNull String path, double defaultValue);

    default double getDouble(@NotNull String path) {
        return getDouble(path, 0.0);
    }

    /**
     * Checks if the value at the specified path is a {@link Float}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a Float, {@code false} otherwise
     */
    boolean isFloat(@NotNull String path);

    /**
     * Gets the value at the specified path as a float,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as a float, or {@code defaultValue} if the path does not exist or is not set
     */
    float getFloat(@NotNull String path, float defaultValue);

    default float getFloat(@NotNull String path) {
        return getFloat(path, 0.0f);
    }

    /**
     * Checks if the value at the specified path is a {@link Long}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a Long, {@code false} otherwise
     */
    boolean isLong(@NotNull String path);

    /**
     * Gets the value at the specified path as a long,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the value at the specified path as a long, or {@code defaultValue} if the path does not exist or is not set
     */
    long getLong(@NotNull String path, long defaultValue);

    default long getLong(@NotNull String path) {
        return getLong(path, 0L);
    }

    /**
     * Checks if the value at the specified path is a {@link Boolean}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a Boolean, {@code false} otherwise
     */
    boolean isBoolean(@NotNull String path);

    /**
     * Gets the value at the specified path as a boolean,
     * or returns a default value if the path does not exist or is not set.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist or is not set
     *
     * @return the boolean value at the specified path, or {@code defaultValue} if the path does not exist or is not set
     */
    boolean getBoolean(@NotNull String path, boolean defaultValue);

    default boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }


    /**
     * Gets the value at the specified path as a Class,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to a Class.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist, is not set,
     *                     or cannot be converted to a Class
     *
     * @return the value at the specified path as a Class, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to a Class
     */
    Class<?> getClass(@NotNull String path, @Nullable Class<?> defaultValue);

    default Class<?> getClass(@NotNull String path) {
        return getClass(path, null);
    }

    /**
     * Gets the value at the specified path as an Enum,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to an Enum.
     *
     * @param path         the path to the value
     * @param clazz        the class of the Enum to convert the value to
     * @param defaultValue the default value to return if the path does not exist, is not set, or cannot be converted to an Enum
     *
     * @return the value at the specified path as an Enum, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to an Enum
     */
    <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue);

    default <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return getEnum(path, clazz, null);
    }

    /**
     * Gets the value at the specified path as a UUID,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to a UUID.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist, is not set, or cannot be converted to a UUID
     *
     * @return the value at the specified path as a UUID, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to a UUID
     */
    UUID getUUID(@NotNull String path, @Nullable UUID defaultValue);

    default UUID getUUID(@NotNull String path) {
        return getUUID(path, null);
    }

    Version getVersion(@NotNull String path, @Nullable Version defaultValue);

    default Version getVersion(@NotNull String path) {
        return getVersion(path, null);
    }

    /**
     * Checks if the value at the specified path is {@link Set}
     *
     * @param path the path to check
     *
     * @return {@code true} if the value at the specified path is set, {@code false} otherwise
     */
    boolean isSet(@NotNull String path);

    /**
     * Checks if the value at the specified path is a {@link List}.
     *
     * @param path the path to the value
     *
     * @return {@code true} if the value at the specified path is a List, {@code false} otherwise
     */
    boolean isList(@NotNull String path);

    /**
     * Gets the value at the specified path as a List,
     * or returns {@code null} if the path does not exist, is not set,
     * or cannot be converted to a List.
     *
     * @param path the path to the value
     *
     * @return the value at the specified path as a List, or {@code null} if the path does not exist, is not set, or cannot be converted to a List
     */
    @Nullable
    List<?> getList(@NotNull String path);

    /**
     * Gets the value at the specified path as a List,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to a List.
     *
     * @param path         the path to the value
     * @param defaultValue the default value to return if the path does not exist, is not set, or cannot be converted to a List
     *
     * @return the value at the specified path as a List, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to a List
     */
    List<?> getList(@NotNull String path, @Nullable List<?> defaultValue);

    /**
     * Gets the value at the specified path as a List of the specified class,
     * or returns {@code null} if the path does not exist, is not set,
     * or cannot be converted to a List of the specified class.
     *
     * @param path  the path to the value
     * @param clazz the class to convert the values in the list to
     *
     * @return the value at the specified path as a List of the specified class, or {@code null} if the path does not exist, is not set, or cannot be converted to a List of the specified class
     */
    <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue);

    @NotNull
    List<String> getStringList(@NotNull String path);

    @NotNull
    List<Boolean> getBooleanList(@NotNull String path);

    @NotNull
    List<Integer> getIntegerList(@NotNull String path);

    @NotNull
    List<Double> getDoubleList(@NotNull String path);

    @NotNull
    List<Float> getFloatList(@NotNull String path);

    @NotNull
    List<Long> getLongList(@NotNull String path);

    @NotNull
    List<Byte> getByteList(@NotNull String path);

    @NotNull
    List<Character> getCharacterList(@NotNull String path);

    @NotNull
    List<Short> getShortList(@NotNull String path);

    /**
     * Gets the value at the specified path as a List of the specified class,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to a List of the specified class.
     *
     * @param path the path to the value
     *
     * @return the value at the specified path as a List of the specified class, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to a List of the specified class
     */
    List<Map<String, Object>> getMapList(@NotNull String path);

    /**
     * Gets the value at the specified path as a List of the specified class,
     * or returns a default value if the path does not exist, is not set,
     * or cannot be converted to a List of the specified class.
     *
     * @param path the path to the value
     *
     * @return the value at the specified path as a List of the specified class, or {@code defaultValue} if the path does not exist, is not set, or cannot be converted to a List of the specified class
     */
    List<? extends Readable> getSectionList(@NotNull String path);

    /**
     * Gets a Collection of all keys in this config, including nested keys.
     *
     * @return a collection of all keys in this config, including nested keys.
     *
     * @see #keys(boolean)
     */
    default Collection<String> keys() {
        return keys(true);
    }

    /**
     * Gets a Collection of all keys in this config, including nested keys.
     *
     * @param deep whether to include nested keys in the collection
     *
     * @return a collection of all keys in this config, including nested keys if {@code deep} is {@code true}.
     */
    Collection<String> keys(boolean deep);

    /**
     * Gets a Map of all keys and values in this config, including nested keys and values.
     *
     * @return a map of all keys and values in this config, including nested keys and values.
     *
     * @see #values(boolean)
     */
    default Map<String, Object> values() {
        return values(true);
    }

    /**
     * Gets a Map of all keys and values in this config.
     *
     * @param deep whether to include nested keys and values in the map
     *
     * @return a map of all keys and values in this config, including nested keys and values if {@code deep} is {@code true}.
     */
    Map<String, Object> values(boolean deep);

}
