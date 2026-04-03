package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.components.ConfigNode;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
public interface Construct {

    /**
     * Constructs and returns an object based on the provided ConfigNode.
     *
     * @param node the ConfigNode containing the necessary data for the construction process; must not be null
     *
     * @return the constructed object based on the data and structure of the given ConfigNode
     */
    Object construct(@NotNull ConfigNode node);

}
