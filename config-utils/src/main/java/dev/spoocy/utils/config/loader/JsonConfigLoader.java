package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.DefaultNodeConstructor;
import dev.spoocy.utils.config.constructor.NodeConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.JsonConfig;
import dev.spoocy.utils.config.types.JsonSettings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class JsonConfigLoader implements ConfigLoader<JsonConfig, JsonSettings> {

    public static final JsonConfigLoader INSTANCE = new JsonConfigLoader();

    protected final NodeConstructor nodeConstructor = new DefaultNodeConstructor(o -> null);

    private JsonConfigLoader() { }

    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"json"};
    }

    @Override
    public JsonConfig createEmpty(@NotNull Consumer<JsonSettings> settingsEditor) {
        checkDependency();
        return new JsonConfig(settingsEditor);
    }

    @Override
    public JsonConfig load(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @NotNull Consumer<JsonSettings> settingsEditor
    ) throws IOException {
        checkDependency();

        JsonConfig config = new JsonConfig();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String data = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            if (data.trim().isEmpty()) {
                return config;
            }

            Map<Object, Object> map = new LinkedHashMap<>((JsonProcessor.toJsonMap(data)));
            constructor.constructMappings(config, map, nodeConstructor);

            return config;
        }
    }

    private static void checkDependency() {
        try {
            Class.forName("org.json.JSONObject");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("org.json:json could not be found in the classpath.");
        }
    }
}
