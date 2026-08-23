package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface PropertyLoader {

    Object load(@NotNull ConfigSection config, @NotNull BoundField field);

}
