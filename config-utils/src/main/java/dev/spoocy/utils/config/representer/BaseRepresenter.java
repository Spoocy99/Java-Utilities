package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.components.MemorySection;
import dev.spoocy.utils.config.serializer.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class BaseRepresenter implements Representer {

    private final Map<Tag, Represent> represents = new HashMap<>();

    public BaseRepresenter() {

    }

    protected void setRepresent(@NotNull Tag tag, @NotNull Represent represent) {
        this.represents.put(tag, represent);
    }

    protected void setRepresent(@NotNull Class<?> clazz, @NotNull Represent represent) {
        setRepresent(new Tag(clazz), represent);
    }

    @Override
    public @NotNull Map<String, Object> represent(@NotNull MemorySection section) {
        Map<String, Object> represented = new LinkedHashMap<>();

        for (Pair<String, ConfigNode> pair : section.entries()) {
            represented.put(pair.first(), represent(pair.second()));
        }

        return represented;
    }

    @Nullable
    protected Represent getRepresent(@NotNull Tag tag) {
        return this.represents.get(tag);
    }

    @Nullable
    @Override
    public Object represent(@Nullable ConfigNode data) {
        if (data == null) {
            return null;
        }

        Represent represent = getRepresent(data.getTag());
        if (represent != null) {
            return represent.represent(data);
        }

        return null;
    }
}

