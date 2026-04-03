package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.components.ConfigNode;
import dev.spoocy.utils.config.components.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Representer {

    @NotNull
    Map<String, Object> represent(@NotNull MemorySection section);

    @Nullable
    Object represent(@Nullable ConfigNode data);

}
