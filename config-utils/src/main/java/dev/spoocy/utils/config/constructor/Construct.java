package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.nodes.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
public interface Construct {

    @Nullable
    Object construct(@Nullable Node node);

}
