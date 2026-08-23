package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.nodes.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
public interface Represent {

    @NotNull
    Node represent(@Nullable Object data);

}

