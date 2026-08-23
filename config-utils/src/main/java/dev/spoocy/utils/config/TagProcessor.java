package dev.spoocy.utils.config;

import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface TagProcessor {

    @Nullable
    Tag process(@Nullable Object data);

}
