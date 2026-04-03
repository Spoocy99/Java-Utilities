package dev.spoocy.utils.config.representer;

import dev.spoocy.utils.config.components.ConfigNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
public interface Represent {

    /**
     * Represents and returns a serializable object based on the provided ConfigNode.
     *
     * @param node the ConfigNode containing data to represent; must not be null
     *
     * @return represented data
     */
    Object represent(@NotNull ConfigNode node);

}

