package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigSettings {

    @NotNull
    protected final Config config;

    protected char pathSeparator = '.';

    public ConfigSettings(@NotNull Config config) {
        this.config = config;
    }

    @NotNull
    public Config configuration() {
        return this.config;
    }

    public char pathSeparator() {
        return this.pathSeparator;
    }

    @NotNull
    public ConfigSettings pathSeparator(char value) {
        this.pathSeparator = value;
        return this;
    }
}
