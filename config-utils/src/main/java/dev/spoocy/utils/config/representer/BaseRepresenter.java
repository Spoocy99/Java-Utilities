package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.nodes.ConfigData;
import dev.spoocy.utils.config.MemorySection;
import dev.spoocy.utils.config.nodes.NodeTree;
import dev.spoocy.utils.config.nodes.ScalarNode;
import dev.spoocy.utils.config.nodes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static dev.spoocy.utils.config.nodes.Node.NULL_TYPE;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class BaseRepresenter implements Representer {

    /**
     * The representer for null values.
     */
    protected Represent nullRepresenter = data -> ScalarNode.nullValue();

    /**
     * Direct representers.
     */
    protected final Map<Class<?>, Represent> representers = new HashMap<>();

    /**
     * Assignable representers.
     */
    protected final Map<Class<?>, Represent> parentRepresenters = new LinkedHashMap<>();

    /**
     * Representer required for all types.
     */
    private boolean strict = false;

    public BaseRepresenter() {

    }

    protected void setStrict(boolean strict) {
        this.strict = strict;
    }

    protected void representNull(@NotNull Represent represent) {
        this.nullRepresenter = represent;
    }

    protected void represent(@NotNull Class<?> type, @NotNull Represent represent) {
        this.representers.put(type, represent);
    }

    protected void representOf(@NotNull Class<?> parent, @NotNull Represent represent) {
        this.parentRepresenters.put(parent, represent);
    }

    @Nullable
    protected Represent getRepresent(@NotNull Class<?> type) {
        type = unwrap(type);

        if (type == NULL_TYPE) {
            return nullRepresenter;
        }

        Represent represent = representers.get(type);
        if (represent != null) {
            return represent;
        }

        for (Map.Entry<Class<?>, Represent> entry : parentRepresenters.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return entry.getValue();
            }
        }

        return null;
    }

    protected Class<?> unwrap(@NotNull Class<?> type) {

        if(type.isPrimitive()) {
            if(type == int.class) return Integer.class;
            if(type == long.class) return Long.class;
            if(type == double.class) return Double.class;
            if(type == float.class) return Float.class;
            if(type == boolean.class) return Boolean.class;
            if(type == char.class) return Character.class;
            if(type == byte.class) return Byte.class;
            if(type == short.class) return Short.class;
        }

        return type;
    }

    @Override
    public @NotNull NodeTree createTree(@NotNull MemorySection section) {
        List<NodeTuple> tuples = new ArrayList<>();

        for (Pair<String, ConfigData> entry : section.entries()) {

            ConfigData data = entry.second();
            if (data == null) {
                continue;
            }

            ScalarNode keyNode = representScalar(Tag.STR, entry.first());
            Node valueNode = representData(data);

            tuples.add(NodeTuple.of(keyNode, valueNode));
        }

        return new NodeTree(Tag.MAP, tuples, section.getComments(), section.getInlineComments());
    }

    @NotNull
    protected Node representData(@NotNull ConfigData data) {
        if (data instanceof MemorySection) {
            return createTree((MemorySection) data);
        }

        if (data instanceof MemoryData) {
            MemoryData memoryData = (MemoryData) data;
            Node node = representObject(memoryData.getData());

            node.setComments(data.getComments());
            node.setInlineComments(data.getInlineComments());
            return node;
        }

        throw new IllegalArgumentException("Invalid ConfigData type: " + data.getClass().getName());
    }

    @NotNull
    protected Node representObject(@Nullable Object data) {

        if (data instanceof Node) {
            throw new IllegalArgumentException("Data already represented.");
        }

        if (data instanceof ConfigData) {
            data = representData((ConfigData) data);
        }

        if (data == null) {
            return nullRepresenter.represent(null);
        }

        Class<?> type = data.getClass();
        Represent represent = getRepresent(type);

        if (represent != null) {
            return represent.represent(data);
        }

        if(strict) {
            throw new IllegalArgumentException("Type '" + type.getName() + "' cannot be represented.");
        }

        return representScalar(new Tag(data.getClass()), data);
    }

    protected ScalarNode representScalar(@NotNull Tag tag, @NotNull Object data) {
        return representScalar(tag, data, null, null);
    }

    protected ScalarNode representScalar(
            @NotNull Tag tag,
            @NotNull Object data,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        return new ScalarNode(data, tag, comments, inlineComments);
    }

    protected SequenceNode representSequence(@NotNull Tag tag, @NotNull Collection<?> data) {
        return representSequence(tag, data, null, null);
    }

    protected SequenceNode representSequence(
            @NotNull Tag tag,
            @NotNull Collection<?> data,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        final List<Node> nodes = new ArrayList<>(data.size());
        for (Object obj : data) {
            nodes.add(representObject(obj));
        }
        return new SequenceNode(tag, nodes, comments, inlineComments);
    }

    protected NodeTree representMapping(@NotNull Map<?, ?> data) {
        return representMapping(data, null, null);
    }

    protected NodeTree representMapping(
            @NotNull Map<?, ?> data,
            @Nullable List<String> comments,
            @Nullable List<String> inlineComments
    ) {
        final List<NodeTuple> tuples = new ArrayList<>(data.size());
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            Node keyNode = representObject(entry.getKey());
            Node valueNode = representObject(entry.getValue());
            tuples.add(NodeTuple.of(keyNode, valueNode));
        }
        return new NodeTree(Tag.MAP, tuples, comments, inlineComments);
    }

}
