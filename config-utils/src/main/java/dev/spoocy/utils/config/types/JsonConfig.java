package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.components.AbstractConfig;
import dev.spoocy.utils.config.representer.SerializingRepresenter;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * Configuration implementation backed by a JSON object.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonConfig extends AbstractConfig {

    protected final JsonSettings settings;

    public JsonConfig() {
        this(s -> {});
    }

    public JsonConfig(@NotNull Consumer<JsonSettings> settingsEditor) {
        super();
        this.settings = new JsonSettings(this);
        settingsEditor.accept(this.settings);
    }

    @Override
    public @NotNull ConfigSettings settings() {
        return this.settings;
    }

    @Override
    public @NotNull String saveToString(@NotNull Representer representer) {
        JSONObject json = new JSONObject(serializedValues(this, representer));
        return json.toString(2);
    }
}
