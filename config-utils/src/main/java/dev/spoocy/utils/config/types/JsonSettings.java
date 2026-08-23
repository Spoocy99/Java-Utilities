package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Config;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonSettings extends ConfigSettings {

    public JsonSettings(@NotNull Config config) {
        super(config);
    }

    @Override
    public @NotNull JsonSettings pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }
}
