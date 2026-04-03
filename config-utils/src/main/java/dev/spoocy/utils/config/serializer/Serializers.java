package dev.spoocy.utils.config.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Defines a contract for managing serializers for various object types,
 * facilitating their conversion to and from map representations. Implementations
 * may support dynamic registration, resolution, serialization, and deserialization
 * processes, allowing efficient mapping of object states to maps.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface Serializers {

    /**
     * Registers a serializer for a specific class type. The provided serializer
     * will be used to serialize and deserialize instances of the specified class.
     *
     * @param clazz      the class type to register the serializer for, must not be null
     * @param serializer the serializer implementation for the specified class type, must not be null
     * @param <T>        the type of the class
     */
    <T> void register(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer);

    /**
     * Resolves and retrieves the serializer associated with the specified class type.
     * If no serializer is registered for the given class, this method returns null.
     *
     * @param <T>   the type of the class for which the serializer is being resolved
     * @param clazz the class for which the serializer is being resolved, must not be null
     *
     * @return the serializer registered for the specified class, or null if no serializer is found
     */
    @Nullable
    <T> Serializer<T> resolve(@NotNull Class<T> clazz);
    /**
     * Serializes the given object into a map representation. The resulting map
     * contains key-value pairs that represent the state and structure of the object,
     * allowing it to be easily stored, transferred, or reconstructed.
     *
     * @param object the object to serialize, must not be null
     *
     * @return a map containing the serialized representation of the object
     *
     * @throws IllegalArgumentException if the object cannot be serialized
     *                                  due to unsupported type or invalid state
     */
    @NotNull
    Map<String, Object> serialize(@NotNull Object object);

    /**
     * Deserializes a map into an object. The map is expected to contain serialized data
     * and associated type information to recreate the original object.
     *
     * @param map the map containing serialized data and type information, must not be null
     *
     * @return the deserialized object reconstructed from the map
     *
     * @throws IllegalArgumentException if the map does not contain valid type information
     *                                  or is incompatible with the expected deserialization process
     */
    @Nullable
    Object deserialize(@NotNull Map<String, Object> map);


}
