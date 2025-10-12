package dev.spoocy.utils.config.documents;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.misc.SectionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MapConfig extends AbstractConfig {

    private final Map<String, Object> values;

    public MapConfig() {
        super();
        this.values = new LinkedHashMap<>();
    }

    public MapConfig(@NotNull Map<String, Object> values) {
        super();
        this.values = values;
    }

    public MapConfig(@NotNull Map<String, Object> values, Config parent) {
        super(parent);
        this.values = values;
    }

    @Override
    protected void set0(@NotNull String path, @Nullable Object value) {
        this.values.put(path, value);

    }

    @Override
    protected @Nullable Object get0(@NotNull String path) {
        return this.values.get(path);
    }

    @Override
    public void remove(@NotNull String path) {
        this.values.remove(path);
    }

    @Override
    public void clear() {
        keys().forEach(this.values::remove);
    }

    @Override
    public Config getSection(@NotNull String path) {
		Object value = this.values.computeIfAbsent(path, key -> new HashMap<>());

        if(value instanceof Map) {
            return new MapConfig((Map<String, Object>) value, this);
        }

        if(value instanceof Config) {
            return (Config) value;
        }

        throw new UnsupportedOperationException(path + " is not a Section in MapDocument.");
    }

    @Override
    public SectionList<? extends Config> getSectionArray(@NotNull String path) {
		List<Config> sections = new ArrayList<>();
		Object value = values.get(path);

        if(!(value instanceof List)) {
            return new SectionList<>();
        }

        List<Object> list = (List<Object>) value;
        for (Object object : list) {

            if (object instanceof Map) {
                sections.add(new MapConfig((Map<String, Object>) object, this));
            }

            if (object instanceof Config) {
                sections.add((Config) object);
            }

        }

        return new SectionList<>(sections);
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return this.values.containsKey(path);
    }

    @Override
    public Collection<String> keys() {
        return this.values.keySet();
    }

    @Override
    public Map<String, Object> values() {
        return new LinkedHashMap<>(values);
    }

    @Override
    public void write(@NotNull Writer writer) throws IOException {
        new JsonConfig(values).write(writer);
    }

    @Override
    public String toJson() {
        return new JsonConfig(values).toJson();
    }

    @Override
    public String toString() {
        return this.toJson();
    }
}
