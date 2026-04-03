package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.components.MemorySection;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.serializer.Serializer;
import dev.spoocy.utils.config.serializer.Serializers;
import dev.spoocy.utils.config.serializer.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Default representation strategy that converts config sections and nodes into serializable data.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class SerializingRepresenter extends BaseRepresenter {

    public static final SerializingRepresenter DEFAULT_INSTANCE = new SerializingRepresenter(NamedSerializers.DEFAULT_INSTANCE);

    private final Serializers serializers;

    public SerializingRepresenter(@NotNull Serializers serializers) {
        this.serializers = Args.notNull(serializers, "serializers");

        setRepresent(Tag.SECTION, this::representSection);
        setRepresent(Tag.MAP, this::representMap);
        setRepresent(Tag.SEQ, this::representList);
    }

    @Override
    public @Nullable Object represent(@Nullable ConfigNode data) {
        Object represented = super.represent(data);
        if (represented != null) {
            return represented;
        }

        if (data == null) {
            return null;
        }

        Object nodeData = data.getData();
        Tag tag = data.getTag();

        // For typed objects (those with a custom class tag), attempt serialization
        Class<?> type = data.getType();
        if (type != null && !tag.isStandard()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> serialized = serializeIfSupported(nodeData, (Class<Object>) type);
            if (serialized != null) {
                return serialized;
            }
        }

        // Return the data as-is for scalars and other types
        return nodeData;
    }

    @Nullable
    private Object representSection(@NotNull ConfigNode node) {
        Object data = node.getData();
        if (data instanceof MemorySection) {
            return represent((MemorySection) data);
        }
        return data;
    }

    @Nullable
    private Object representMap(@NotNull ConfigNode node) {
        Object data = node.getData();
        if (!(data instanceof Map)) {
            return data;
        }

        Map<Object, Object> represented = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) data).entrySet()) {
            Object value = entry.getValue();
            if (value instanceof ConfigNode) {
                value = represent((ConfigNode) value);
            }

            represented.put(entry.getKey(), value);
        }

        return represented;
    }

    @Nullable
    private Object representList(@NotNull ConfigNode node) {
        Object data = node.getData();
        if (!(data instanceof List)) {
            return data;
        }

        List<Object> represented = new ArrayList<>(((List<?>) data).size());
        for (Object element : (List<?>) data) {
            if (element instanceof ConfigNode) {
                represented.add(represent((ConfigNode) element));
            } else {
                represented.add(element);
            }
        }

        return represented;
    }


    @Nullable
    private <T> Map<String, Object> serializeIfSupported(@NotNull T data, @NotNull Class<T> clazz) {
        Serializer<T> serializer = this.serializers.resolve(clazz);
        return serializer == null ? null : serializer.serializeSafely(data);
    }
}
