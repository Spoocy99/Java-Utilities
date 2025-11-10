package dev.spoocy.utils.config.serializer.impl;

import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Serializer implementation for atomic types.
 *
 * @see java.util.concurrent.atomic.AtomicInteger
 * @see java.util.concurrent.atomic.AtomicLong
 * @see java.util.concurrent.atomic.AtomicBoolean
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class AtomicsSerializer<A> implements Serializer<A> {

    public static final String SERIALIZED_ATOMIC_VALUE_KEY = "value";
    private final Class<A> clazz;

    public AtomicsSerializer(@NotNull Class<A> clazz) {
        this.clazz = clazz;
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull A object) {
        try {
            Map<String, Object> map = new HashMap<>();

            if (clazz.equals(AtomicInteger.class)) {
                map.put(SERIALIZED_ATOMIC_VALUE_KEY, ((AtomicInteger) object).get());

            } else if (clazz.equals(AtomicLong.class)) {
                map.put(SERIALIZED_ATOMIC_VALUE_KEY, ((AtomicLong) object).get());

            } else if (clazz.equals(AtomicBoolean.class)) {
                map.put(SERIALIZED_ATOMIC_VALUE_KEY, ((AtomicBoolean) object).get());

            } else {
                throw new IllegalArgumentException("Unsupported atomic type: " + clazz.getName());
            }

            map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, clazz.getName());
            return map;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass().getName(), e);
        }
    }

    @Override
    public @NotNull A deserialize(@NotNull Map<String, Object> map) {
        Object valueObj = map.get(SERIALIZED_ATOMIC_VALUE_KEY);

        if (valueObj == null) {
            throw new IllegalArgumentException("Serialized atomic value is missing!");
        }

        try {
            if (clazz.equals(AtomicInteger.class)) {
                return (A) new AtomicInteger((Integer) valueObj);

            } else if (clazz.equals(AtomicLong.class)) {
                return (A) new AtomicLong((Long) valueObj);

            } else if (clazz.equals(AtomicBoolean.class)) {
                return (A) new AtomicBoolean((Boolean) valueObj);

            } else {
                throw new IllegalArgumentException("Unsupported atomic type: " + clazz.getName());
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize atomic of type: " + clazz.getName(), e);
        }
    }


}
