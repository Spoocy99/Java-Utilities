package dev.spoocy.utils.common.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ILoggerFactory {

    @NotNull
    ILogger getOrCreateLogger(@Nullable String name);

    ILoggerFactory setLevel(@NotNull LogLevel level);

}
