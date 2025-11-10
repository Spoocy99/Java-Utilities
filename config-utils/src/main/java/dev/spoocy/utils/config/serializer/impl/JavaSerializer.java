package dev.spoocy.utils.config.serializer.impl;

import dev.spoocy.utils.common.cache.Cache;
import dev.spoocy.utils.common.cache.Caches;
import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;
import java.util.Map;

/**
 * Serializer implementation using Java's built-in serialization mechanism.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JavaSerializer<O extends Serializable> implements Serializer<O> {

    private static final Cache<Class<?>, JavaSerializer<?>> CLASS_CACHE = Caches.createLRUCache(50);

    public static <O extends Serializable> JavaSerializer<O> create(@NotNull Class<O> clazz) {
        return (JavaSerializer<O>) CLASS_CACHE.computeIfAbsent(clazz, c -> new JavaSerializer(c));
    }

    private static final String SERIALIZED_DATA_KEY = "data";
    private final Class<O> clazz;

    private JavaSerializer(@NotNull Class<O> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull O object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(object);
            oos.flush();
            String data = Base64.getEncoder().encodeToString(baos.toByteArray());

            return Map.of(
                    ConfigSerializer.SERIALIZED_TYPE_KEY, clazz.getName(),
                    SERIALIZED_DATA_KEY, data
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    @Override
    public @NotNull O deserialize(@NotNull Map<String, Object> map) {
        Object dataObj = map.get(SERIALIZED_DATA_KEY);

        if (!(dataObj instanceof String)) {
            throw new IllegalArgumentException("Serialized data missing or not a String under key '" + SERIALIZED_DATA_KEY + "'");
        }

        byte[] bytes = Base64.getDecoder().decode((String) dataObj);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            Object obj = ois.readObject();

            return clazz.cast(obj);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }

}
