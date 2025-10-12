package dev.spoocy.utils.config.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Serializer interface for serializing and deserializing objects to and from maps.
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface Serializer<O> {

    /**
     * Serialize an object to a map.
     *
     * @param object the object to serialize
     *
     * @return the serialized object
     */
    @NotNull
    Map<String, Object> serialize(@NotNull O object);

    @Nullable
    default Map<String, Object> serializeSafely(@NotNull O object) {
        try {
            return serialize(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deserialize a map to an object
     *
     * @param map the map to deserialize
     *
     * @return the deserialized object
     */
    @NotNull
    O deserialize(@NotNull Map<String, Object> map);


    @Nullable
    default O deserializeSafely(@NotNull Map<String, Object> map) {
        try {
            return deserialize(map);
        } catch (Exception e) {
            return null;
        }
    }

}
