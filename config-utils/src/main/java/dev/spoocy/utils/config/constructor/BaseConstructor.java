package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.AbstractConfig;
import dev.spoocy.utils.config.MemorySection;
import dev.spoocy.utils.config.Tag;
import dev.spoocy.utils.config.nodes.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class BaseConstructor implements Constructor {


    /**
     * A {@link Construct} defines how a specific tag should be constructed.
     */
    @NotNull
    protected final Map<Tag, Construct> constructors = new HashMap<>();

    /**
     * Default constructor for null values.
     */
    @NotNull
    protected Construct nullConstructor = data -> null;

    public BaseConstructor() {

    }

    protected void constructNull(@NotNull Construct constructor) {
        this.nullConstructor = constructor;
    }

    protected void construct(@NotNull Tag tag, @NotNull Construct constructor) {
        this.constructors.put(tag, constructor);
    }

    @Nullable
    protected Construct getConstruct(@NotNull Tag tag) {
        return this.constructors.get(tag);
    }

    @Override
    public void constructMappings(@NotNull AbstractConfig config, @NotNull Map<Object, Object> map, @NotNull NodeConstructor nodeConstructor) {
        applyTree(config, constructTree(map, nodeConstructor));
    }

    protected void applyTree(@NotNull MemorySection section, @NotNull NodeTree tree) {
        // first level will always be treated as base
        for (NodeTuple tuple : tree) {

            String key = constructObject(tuple.getKeyNode()).toString();
            Object value = constructObject(tuple.getValueNode());

            if (value instanceof Map<?, ?>) {
                // map should be converted to section for easier access
                section.createSection(key, (Map<?, ?>) value);
                continue;
            }

            section.set(key, value);
        }
    }

    @Override
    public @NotNull NodeTree constructTree(@NotNull Map<Object, Object> mappings, @NotNull NodeConstructor nodeConstructor) {
        List<NodeTuple> tuples = new ArrayList<>(mappings.size());

        for (Map.Entry<?, ?> entry : mappings.entrySet()) {
            Node keyNode = nodeConstructor.construct(entry.getKey());
            Node valueNode = nodeConstructor.construct(entry.getValue());
            tuples.add(NodeTuple.of(keyNode, valueNode));
        }

        return new NodeTree(Tag.MAP, tuples, null, null);
    }

    protected Object constructObject(@NotNull Node node) {

        Tag tag = node.getTag();

        if (tag == Tag.NULL) {
            return this.nullConstructor.construct(node);
        }

        Construct constructor = getConstruct(tag);
        if (constructor != null) {
            return constructor.construct(node);
        }

        if(node instanceof ScalarNode) {
            return constructScalar((ScalarNode) node);
        }

        throw new IllegalStateException("Unsupported tag: " + tag);
    }

    @Nullable
    protected Object constructScalar(@NotNull ScalarNode node) {
        return node.getData();
    }

     @Nullable
    protected String constructScalarString(@NotNull ScalarNode node) {
        Object data = constructScalar(node);
        if (data == null) {
            return "null";
        }

        return data.toString();
    }

    protected List<Object> createList(int initSize) {
        return new ArrayList<>(initSize);
    }

    protected Set<Object> createSet(int initSize) {
        return new LinkedHashSet<>(initSize);
    }

    protected Map<Object, Object> createMap(int initSize) {
        return new LinkedHashMap<>(initSize);
    }

}
