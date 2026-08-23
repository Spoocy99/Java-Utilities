package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.TagProcessor;
import dev.spoocy.utils.config.nodes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DefaultNodeConstructor implements NodeConstructor {

    private final TagProcessor tagProcessor;

    public DefaultNodeConstructor(@NotNull TagProcessor tagProcessor) {
        this.tagProcessor = tagProcessor;
    }

    @Override
    public @NotNull Node construct(@Nullable Object data) {

        if (data instanceof Node) {
            throw new IllegalArgumentException("Data already constructed.");
        }

        // what is the type?
        Tag tag = resolveTag(data);

        // null
        if (tag == Tag.NULL || data == null) {
            return ScalarNode.nullValue();
        }

        // sequences
        if (data instanceof Set<?>) {
            return constructSequence(Tag.SET, (Set<?>) data);
        }

        if (data instanceof Iterable<?>) {
            return constructSequence(Tag.SEQ, (Iterable<?>) data);
        }

        if (data.getClass()
                .isArray()) {
            int length = Array.getLength(data);
            List<Object> values = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                values.add(Array.get(data, i));
            }

            return constructSequence(Tag.SEQ, values);
        }

        // map
        if (data instanceof Map<?, ?>) {
            return constructMap((Map<?, ?>) data);
        }

        // scalar
        return new ScalarNode(data, tag, null, null);
    }

    protected SequenceNode constructSequence(@NotNull Tag tag, @NotNull Iterable<?> iterable) {
        List<Node> nodes = new ArrayList<>();

        for (Object item : iterable) {
            nodes.add(construct(item));
        }

        return new SequenceNode(tag, nodes, null, null);
    }

    protected NodeTree constructMap(@NotNull Map<?, ?> map) {
        List<NodeTuple> tuples = new ArrayList<>(map.size());

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Node keyNode = construct(entry.getKey());
            Node valueNode = construct(entry.getValue());
            tuples.add(NodeTuple.of(keyNode, valueNode));
        }

        return new NodeTree(Tag.MAP, tuples, null, null);
    }

    @NotNull
    protected Tag resolveTag(@Nullable Object object) {
        Class<?> type = object == null ? Node.NULL_TYPE : object.getClass();

        Tag processorTag = this.tagProcessor.process(type);

        if (processorTag != null) {
            return processorTag;
        }

        return Tag.getDefaultTag(type);
    }
}
