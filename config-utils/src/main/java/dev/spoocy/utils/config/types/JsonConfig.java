package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.AbstractConfig;
import dev.spoocy.utils.config.nodes.ScalarNode;
import dev.spoocy.utils.config.representer.Representer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
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
        JSONObject json = new JSONObject(this.representAsMap(representer));
        return json.toString(2);
    }

}
