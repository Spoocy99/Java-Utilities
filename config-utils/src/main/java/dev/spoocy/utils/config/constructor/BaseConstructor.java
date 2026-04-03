package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.components.MemorySection;
import dev.spoocy.utils.config.serializer.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class BaseConstructor implements Constructor {

    private final Map<Tag, Construct> constructs = new HashMap<>();

    public BaseConstructor() {

    }

    protected void setConstruct(@NotNull Tag tag, @NotNull Construct construct) {
        this.constructs.put(tag, construct);
    }

    protected void setConstruct(@NotNull Class<?> clazz, @NotNull Construct construct) {
        setConstruct(new Tag(clazz), construct);
    }

    @Override
    public void constructMappings(@NotNull MemorySection section, Map<Object, Object> mapping) {
        flattenMappings(section);
    }

    @Nullable
    protected Construct getConstruct(@NotNull Tag tag) {
        return this.constructs.get(tag);
    }

    protected void flattenMappings(@NotNull MemorySection section) {
        List<Pair<String, ConfigNode>> nodes = section.entries();

        for (Pair<String, ConfigNode> pair : nodes) {
            ConfigNode node = pair.second();

            if (node.getTag() == Tag.SECTION && node.getData() instanceof MemorySection) {
                MemorySection data = (MemorySection) node.getData();
                flattenMappings(data);
            }

            Object value = construct(node);
            if (value != null) {
                node.setData(value);
            }
        }
    }

    protected Object construct(@Nullable ConfigNode node) {
        if (node == null) {
            return null;
        }

        Tag tag = node.getTag();
        Construct construct = getConstruct(tag);

        if (construct != null) {
            return construct.construct(node);
        }
        return null;
    }
}
