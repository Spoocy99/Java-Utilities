package dev.spoocy.utils.config.documents;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.misc.SectionList;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.text.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonConfig extends AbstractConfig {

    private final JSONObject json;

    public JsonConfig() {
        super();
        this.json = new JSONObject();
    }

    public JsonConfig(@NotNull JSONObject json) {
        super();
        this.json = json;
    }

    public JsonConfig(@NotNull Map<String, Object> values) {
        super();
        this.json = new JSONObject();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    public JsonConfig(@NotNull Config root) {
        super(root);
        this.json = new JSONObject();
    }

    public JsonConfig(@NotNull JSONObject json, @NotNull Config root) {
        super(root);
        this.json = json;
    }

    public JsonConfig(@NotNull String content) {
        super();
        this.json = new JSONObject(content);
    }

    public JsonConfig(@NotNull File file) {
        super();
        String content = "{}";

        try {
            content = getContent(new FileReader(file));
        } catch (IOException ignored) {}

        this.json = new JSONObject(content);
    }

    public JsonConfig(@NotNull InputStream inputStream) {
        super();
        String content = getContent(new InputStreamReader(inputStream));
        this.json = new JSONObject(content);
    }

    @Override
    public Config getSection(@NotNull String path) {
        JSONObject section;

        try {
            section = json.getJSONObject(path);
        } catch (Exception e) {
            section = new JSONObject();
            this.json.put(path, section);
        }

        return new JsonConfig(section, this);
    }

    @Override
    public SectionList<Config> getSectionArray(@NotNull String path) {
        try {
            JSONArray array = json.getJSONArray(path);
            List<Config> sections = new ArrayList<>();

            for(Object object : array) {
                if(object instanceof JSONObject) {
                    sections.add(new JsonConfig((JSONObject) object, this));
                }
            }
            return new SectionList<>(sections);
        } catch (Exception e) {
            return new SectionList<>();
        }
    }

    @Override
    public void write(@NotNull Writer writer) throws IOException {
        String data = this.saveToString();
        writer.write(data);
    }

    @Override
    protected  void set0(@NotNull String path, @Nullable Object value) {
        this.json.put(path, value);
    }

    @Override
    protected @Nullable Object get0(@NotNull String path) {
        try {
            return this.json.get(path);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public void remove(@NotNull String path) {
        this.json.remove(path);
    }

    @Override
    public void clear() {
        this.keys().forEach(this::remove);
    }

    @Override
    public String getString(@NotNull String path, @NotNull String defaultValue) {
        try {
            return json.getString(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        try {
            JSONArray array = json.getJSONArray(path);
            return array.toList().stream().map(Object::toString).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int getInt(@NotNull String path, int defaultValue) {
        try {
            return json.getInt(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        try {
            JSONArray array = json.getJSONArray(path);
            return array.toList().stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public double getDouble(@NotNull String path, double defaultValue) {
        try {
            return json.getDouble(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        try {
            JSONArray array = json.getJSONArray(path);
            return array.toList().stream().map(o -> Double.parseDouble(o.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public float getFloat(@NotNull String path, float defaultValue) {
        try {
            return json.getFloat(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        try {
            JSONArray array = json.getJSONArray(path);
            return array.toList().stream().map(o -> Float.parseFloat(o.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public long getLong(@NotNull String path, long defaultValue) {
        try {
            return this.json.getLong(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        try {
            JSONArray array = this.json.getJSONArray(path);
            return array.toList().stream().map(o -> Long.parseLong(o.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        try {
            return this.json.getBoolean(path);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        try {
            JSONArray array = this.json.getJSONArray(path);
            return array.toList().stream().map(o -> Boolean.parseBoolean(o.toString())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return json.has(path);
    }

    @Override
    public boolean isList(@NotNull String path) {
        try {
            return this.json.getJSONArray(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Collection<String> keys() {
        return this.json.keySet();
    }

    @Override
    public Map<String, Object> values() {
        return this.json.toMap();
    }

    public String saveToString() {
        return this.json.toString(2);
    }

    @Override
    public String toString() {
        return this.saveToString();
    }

    @Override
    public String toJson() {
        return this.saveToString();
    }

    private String getContent(@NotNull Reader reader) {
        String content = null;

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            content = bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            ILogger.forThisClass().error("Failed to read content", e);
        }
        if (StringUtils.isNullOrEmpty(content)) {
            content = "{}";
        }
        return content;
    }

    private String listToString(@NotNull List<?> list) {
        StringBuilder builder = new StringBuilder().append("[");
        list.forEach(element -> builder.append("\"").append(element.toString()).append("\"").append(","));
        builder.append("]");
        if (builder.toString().contains(","))
            builder.replace(builder.lastIndexOf(","), builder.lastIndexOf(",") + 1, "");
        return builder.toString();
    }
}
