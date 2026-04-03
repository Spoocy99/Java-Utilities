package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.serializer.Serializers;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonSettings extends ConfigSettings {

    public JsonSettings(@NotNull Config config) {
        super(config);
    }

    @Override
    public @NotNull JsonSettings constructor(@NotNull Constructor constructor) {
        super.constructor(constructor);
        return this;
    }

    @Override
    public @NotNull JsonSettings representer(@NotNull Representer representer) {
        super.representer(representer);
        return this;
    }

    @Override
    public @NotNull JsonSettings pathSeparator(char value) {
        super.pathSeparator(value);
        return this;
    }

    @Override
    public @NotNull ConfigSettings serializers(@NotNull Serializers serializers) {
        super.serializers(serializers);
        return this;
    }
}
