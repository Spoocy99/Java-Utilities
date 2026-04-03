package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.components.MemorySection;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.serializer.Serializer;
import dev.spoocy.utils.config.serializer.Serializers;
import dev.spoocy.utils.config.serializer.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SerializingConstructor extends BaseConstructor implements SerializerAssignable {

    public static final SerializingConstructor DEFAULT_INSTANCE = new SerializingConstructor(NamedSerializers.DEFAULT_INSTANCE);

    @NotNull
    private final Serializers serializers;

    public SerializingConstructor(@NotNull Serializers serializers) {
        this.serializers = Args.notNull(serializers, "serializers");

        setConstruct(Tag.SECTION, this::constructSection);
        setConstruct(Tag.MAP, this::constructMap);
        setConstruct(Tag.SEQ, this::constructList);
    }

    @Override
    public @NotNull Serializers getSerializers() {
        return serializers;
    }

    private boolean shouldDeserialize(@NotNull Map<?, ?> map) {
        Object type = map.get(NamedSerializers.SERIALIZED_TYPE_KEY);
        return type instanceof String;
    }

    @Override
    public void constructMappings(@NotNull MemorySection section, @NotNull Map<Object, Object> mapping) {
        for (Map.Entry<Object, Object> entry : mapping.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                continue;
            }

            String key = (String) entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map && !shouldDeserialize((Map<?, ?>) value)) {
                ConfigSection child = section.createSection(key);
                this.constructMappings((MemorySection) child, castMap((Map<?, ?>) value));
                continue;
            }

            section.set(key, normalize(value));
        }

        super.constructMappings(section, mapping);
    }

    @Nullable
    @Override
    protected Object construct(@Nullable ConfigNode node) {
        Object value = super.construct(node);
        if (value != null) {
            return value;
        }

        if (node == null) {
            return null;
        }

        Class<?> type = node.getType();
        if (type != null && node.getData() instanceof Map) {
            Serializer<?> serializer = serializers.resolve(type);
            if (serializer != null) {
                Object deserialized = serializer.deserializeSafely(castStringMap((Map<?, ?>) node.getData()));
                if (deserialized != null) {
                    return deserialized;
                }
            }
        }

        return node.getData();
    }

    @Nullable
    private Object constructSection(@NotNull ConfigNode node) {
        Object data = node.getData();
        if (data instanceof MemorySection) {
            super.flattenMappings((MemorySection) data);
        }
        return data;
    }

    @Nullable
    private Object constructMap(@NotNull ConfigNode node) {
        if (!(node.getData() instanceof Map)) {
            return node.getData();
        }

        Map<?, ?> map = (Map<?, ?>) node.getData();
        if (shouldDeserialize(map)) {
            Object deserialized = serializers.deserialize(castStringMap(map));
            if (deserialized != null) {
                return deserialized;
            }
        }

        Map<Object, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            copy.put(entry.getKey(), normalize(entry.getValue()));
        }

        return copy;
    }

    @Nullable
    private Object constructList(@NotNull ConfigNode node) {
        Object data = node.getData();
        if (!(data instanceof List)) {
            return data;
        }

        List<Object> represented = new ArrayList<>(((List<?>) data).size());
        for (Object element : (List<?>) data) {
            represented.add(normalize(element));
        }

        return represented;
    }

    @Nullable
    private Object normalize(@Nullable Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;

            if (shouldDeserialize(map)) {
                Object deserialized = serializers.deserialize(castStringMap(map));
                if (deserialized != null) {
                    return deserialized;
                }
            }

            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                copy.put(entry.getKey(), normalize(entry.getValue()));
            }
            return copy;
        }

        if (value instanceof List) {
            List<Object> represented = new ArrayList<>(((List<?>) value).size());
            for (Object element : (List<?>) value) {
                represented.add(normalize(element));
            }
            return represented;
        }

        return value;
    }

    @NotNull
    private static Map<Object, Object> castMap(@NotNull Map<?, ?> map) {
        Map<Object, Object> typed = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            typed.put(entry.getKey(), entry.getValue());
        }
        return typed;
    }

    @NotNull
    private static Map<String, Object> castStringMap(@NotNull Map<?, ?> map) {
        Map<String, Object> typed = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            typed.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return typed;
    }
}

