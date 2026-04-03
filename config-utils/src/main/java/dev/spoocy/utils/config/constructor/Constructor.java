package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.components.MemorySection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Constructor {

    void constructMappings(@NotNull MemorySection section, Map<Object, Object> mapping);

}
