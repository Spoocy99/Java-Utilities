package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.SerializerAssignable;
import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.ConfigSettings;
import dev.spoocy.utils.config.types.JsonConfig;
import dev.spoocy.utils.config.types.JsonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private JsonConfigLoader() { }

    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"json"};
    }

    @Override
    public JsonConfig createEmpty(@NotNull Consumer<JsonSettings> settingsEditor) {
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


        // apply settings
        config.settings().constructor(constructor);
        if(constructor instanceof SerializerAssignable) {
            config.settings().serializers(((SerializerAssignable) constructor).getSerializers());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String data = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            if (data.trim().isEmpty()) {
                return config;
            }

            JSONObject json = new JSONObject(data);
            Map<Object, Object> map = new LinkedHashMap<>(toMap(json));
            constructor.constructMappings(config, map);

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

    private static Map<String, Object> toMap(@NotNull JSONObject json) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : json.keySet()) {
            Object val = json.get(key);
            map.put(key, toObject(val));
        }
        return map;
    }

    private static Object toObject(@Nullable Object val) {
        if (val instanceof JSONObject) {
            return toMap((JSONObject) val);
        }
        
        if (val instanceof JSONArray) {
            JSONArray arr = (JSONArray) val;
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                Object element = toObject(arr.get(i));
                list.add(element);
            }
            return list;
        }
        
        if (val == JSONObject.NULL) {
            return null;
        }
        return val;
    }
}
