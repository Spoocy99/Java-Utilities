package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.MemorySection;
import dev.spoocy.utils.config.nodes.NodeTree;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Representer {

    /**
     *
     */
    @NotNull
    NodeTree createTree(@NotNull MemorySection section);

}
