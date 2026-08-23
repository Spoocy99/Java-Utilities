package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.nodes.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface NodeConstructor {

    @NotNull
    Node construct(@Nullable Object value);

}

