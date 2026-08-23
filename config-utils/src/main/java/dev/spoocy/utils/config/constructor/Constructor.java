package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.AbstractConfig;
import dev.spoocy.utils.config.TagProcessor;
import dev.spoocy.utils.config.nodes.NodeTree;
import dev.spoocy.utils.config.representer.Represent;
import dev.spoocy.utils.config.types.JsonConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Constructor {

    @NotNull
    NodeTree constructTree(@NotNull Map<Object, Object> mappings, @NotNull NodeConstructor nodeConstructor);

    /**
     * Constructs and populates mappings into the given map based on the provided configuration.
     *
     * @param config the configuration object containing the data required to construct the mappings; must not be null
     * @param map    the map into which the constructed mappings are populated; must not be null
     */
    void constructMappings(@NotNull AbstractConfig config, @NotNull Map<Object, Object> map, @NotNull NodeConstructor nodeConstructor);
}
