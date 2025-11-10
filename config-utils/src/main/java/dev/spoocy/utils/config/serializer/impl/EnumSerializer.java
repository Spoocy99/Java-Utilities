package dev.spoocy.utils.config.serializer.impl;

import dev.spoocy.utils.common.cache.Cache;
import dev.spoocy.utils.common.cache.Caches;
import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Serializer implementation for Enum types.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class EnumSerializer<O extends Enum<O>> implements Serializer<O> {

    private static final Cache<Class<?>, EnumSerializer<?>> CLASS_CACHE = Caches.createLRUCache(50);

    public static <O extends Enum<O>> EnumSerializer<O> create(@NotNull Class<O> clazz) {
        return (EnumSerializer<O>) CLASS_CACHE.computeIfAbsent(clazz, c -> new EnumSerializer<>(clazz));
    }

    public static final String SERIALIZED_ENUM_NAME_KEY = "value";
    private final Class<O> clazz;

    private EnumSerializer(@NotNull Class<O> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull O object) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(SERIALIZED_ENUM_NAME_KEY, object.name());
            map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, object.getClass().getName());
            return map;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass().getName(), e);
        }
    }

    @Override
    public @NotNull O deserialize(@NotNull Map<String, Object> map) {
        Object nameObj = map.get(SERIALIZED_ENUM_NAME_KEY);
        if (!(nameObj instanceof String)) {
            throw new IllegalArgumentException("Serialized enum name is missing or not a string!");
        }

        String name = (String) nameObj;
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant " + clazz.getName() + "." + name, e);
        }
    }
}
