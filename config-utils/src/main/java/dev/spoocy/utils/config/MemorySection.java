package dev.spoocy.utils.config;

import dev.spoocy.utils.common.misc.Args;
import dev.spoocy.utils.common.misc.NumberConversion;
import dev.spoocy.utils.common.tuple.Pair;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.nodes.ConfigData;
import dev.spoocy.utils.config.nodes.MemoryData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MemorySection extends ConfigData implements ConfigSection {

    protected final Map<String, ConfigData> dataMap = new LinkedHashMap<>();
    private final Config root;
    private final ConfigSection parent;
    private final String path;

    public MemorySection() {
        super(null, null);

        if (!(this instanceof Config)) {
            throw new IllegalStateException("MemorySection must be a Config if no parent is provided");
        }

        this.root = (Config) this;
        this.parent = null;
        this.path = "";
    }

    public MemorySection(@NotNull ConfigSection parent, @NotNull String path) {
        super(null, null);

        this.parent = Args.notNull(parent, "parent");
        this.path = Args.notNull(path, "path");
        this.root = parent.getRoot();
    }

    @Override
    public @NotNull String getName() {
        return this.path;
    }

    @Override
    public @NotNull Config getRoot() {
        return this.root;
    }

    @Override
    public @Nullable ConfigSection getParent() {
        return this.parent;
    }

    private @NotNull String pathSeparator() {
        return String.valueOf(this.root.settings().pathSeparator());
    }

    private @NotNull String[] splitPath(@NotNull String path) {
        return path.split(Pattern.quote(pathSeparator()));
    }

    public List<Pair<String, ConfigData>> entries() {
        List<Pair<String, ConfigData>> entries = new ArrayList<>();

        for (Map.Entry<String, ConfigData> entry : this.dataMap.entrySet()) {
            entries.add(new Pair<>(entry.getKey(), entry.getValue()));
        }

        return entries;
    }

    @Override
    public @NotNull List<String> getHeaderComments() {
        return this.root.getHeaderComments();
    }

    @Override
    public @NotNull List<String> getFooterComments() {
        return this.root.getFooterComments();
    }

    @Override
    public void setHeaderComments(@Nullable List<String> comments) {
        this.root.setHeaderComments(comments);
    }

    @Override
    public void setFooterComments(@Nullable List<String> comments) {
        this.root.setFooterComments(comments);
    }

    @Override
    public @NotNull List<String> getComments() {
        return super.getComments();
    }

    @Override
    public @NotNull List<String> getInlineComments() {
        return super.getInlineComments();
    }

    @Override
    public void setComments(@Nullable List<String> comments) {
        super.setComments(comments);
    }

    @Override
    public void setInlineComments(@Nullable List<String> inlineComments) {
        super.setInlineComments(inlineComments);
    }

    @Override
    public @NotNull List<String> getComments(@NotNull String path) {
        ConfigData data = getMapData(path);
        return data != null ? data.getComments() : Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getInlineComments(@NotNull String path) {
        ConfigData data = getMapData(path);
        return data != null ? data.getInlineComments() : Collections.emptyList();
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        ConfigData data = getMapData(path);
        if (data != null) {
            data.setComments(comments);
        }
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        ConfigData data = getMapData(path);
        if (data != null) {
            data.setInlineComments(comments);
        }
    }

    @Nullable
    private ConfigData getMapData(@NotNull String path) {
        String[] parts = splitPath(path);

        if(parts.length == 1) {
            return this.dataMap.get(path);
        }

        ConfigData sectionData = this.dataMap.get(parts[0]);
        if(sectionData instanceof MemorySection) {
            String subPath = path.substring(parts[0].length() + 1);
            return ((MemorySection) sectionData).getMapData(subPath);
        }

        return null;
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return getMapData(path) instanceof MemorySection;
    }

    @Override
    public @NotNull MemorySection getSection(@NotNull String path) {
        ConfigData data = getMapData(path);
        if (data instanceof MemorySection) {
            return (MemorySection) data;
        }

        throw new IllegalArgumentException("Path '" + path + "' is not a section");
    }

    @Override
    public @Nullable ConfigSection getSectionIfExists(@NotNull String path) {
        try {
            return getSection(path);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public @NotNull ConfigSection getSectionOrEmpty(@NotNull String path) {
        try {
            return getSection(path);
        } catch (IllegalArgumentException ex) {
            return new MemorySection(this, path);
        }
    }

    @Override
    public @NotNull ConfigSection getOrCreateSection(@NotNull String path) {
        try {
            return getSection(path);
        } catch (IllegalArgumentException ex) {
            return createSection(path);
        }
    }

    @Override
    public @NotNull MemorySection createSection(@NotNull String path) {
        String[] parts = splitPath(path);
        ConfigData current = this.dataMap.get(parts[0]);

        if(parts.length == 1) {

            MemorySection section = new MemorySection(this, path);
            this.dataMap.put(path, section);
            return section;
        }

        MemorySection next;

        if(current instanceof MemorySection) {
            next = (MemorySection) current;
        } else {
            next = new MemorySection(this, parts[0]);
            this.dataMap.put(parts[0], next);
        }

        String subPath = path.substring(parts[0].length() + 1);
        return next.createSection(subPath);
    }

    @Override
    public @NotNull MemorySection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        MemorySection section = createSection(path);
        section.applyMap(map);
        return section;
    }

    public void applyMap(@NotNull Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {

            Object keyObj = entry.getKey();

            if(keyObj == null) {
                continue;
            }

            String key = keyObj.toString();
            Object value = entry.getValue();

            if(value instanceof Map<?, ?>) {
                this.createSection(key, (Map<?, ?>) value);
                continue;
            }

            this.set(key, value);
        }
    }

    @Override
    public boolean isSet(@NotNull String path) {
        String[] parts = splitPath(path);
        ConfigData current = this.dataMap.get(parts[0]);

        if(parts.length == 1) {
            return current instanceof MemoryData;
        }

        if(current instanceof MemorySection) {
            String subPath = path.substring(parts[0].length() + 1);
            return ((MemorySection) current).isSet(subPath);
        }

        return false;
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        String[] parts = splitPath(path);
        ConfigData current = this.dataMap.get(parts[0]);

        if(parts.length == 1) {

            if(!(current instanceof MemoryData)) {
                current = new MemoryData(null, null);
                this.dataMap.put(path, current);
            }

            ((MemoryData) current).setData(value);
            return;
        }

        if(!(current instanceof MemorySection)) {
            current = new MemorySection(this, parts[0]);
            this.dataMap.put(parts[0], current);
        }

        String subPath = path.substring(parts[0].length() + 1);
        ((MemorySection) current).set(subPath, value);
    }

    @Override
    public void remove(@NotNull String path) {
        String[] parts = splitPath(path);
        ConfigData current = this.dataMap.get(parts[0]);

        if(parts.length == 1) {
            this.dataMap.remove(path);
            return;
        }

        if(current instanceof MemorySection) {
            String subPath = path.substring(parts[0].length() + 1);
            ((MemorySection) current).remove(subPath);
        }
    }

    @Override
    public void clear() {
        this.dataMap.clear();
    }

    @Override
    public @Nullable Object getObject(@NotNull String path) {
        String[] parts = splitPath(path);
        ConfigData current = this.dataMap.get(parts[0]);

        if(parts.length == 1) {
            if(current instanceof MemoryData) {
                return ((MemoryData) current).getData();
            }

            return null;
        }

        if(current instanceof MemorySection) {
            String subPath = path.substring(parts[0].length() + 1);
            return ((MemorySection) current).getObject(subPath);
        }

        return null;
    }

    @Override
    public Collection<String> keys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>();

        for (Map.Entry<String, ConfigData> entry : this.dataMap.entrySet()) {
            String key = entry.getKey();
            keys.add(key);

            if (deep && entry.getValue() instanceof MemorySection) {
                ConfigSection section = (ConfigSection) entry.getValue();
                for (String subKey : section.keys(true)) {
                    keys.add(key + root.settings().pathSeparator() + subKey);
                }
            }
        }

        return keys;
    }

    @Override
    public Map<String, Object> values(boolean deep) {
        Map<String, Object> values = new LinkedHashMap<>();

        for (Map.Entry<String, ConfigData> entry : this.dataMap.entrySet()) {
            String key = entry.getKey();
            ConfigData data = entry.getValue();

            if(data instanceof MemoryData) {
                values.put(key, ((MemoryData) data).getData());
                continue;
            }

            if (deep && data instanceof MemorySection) {
                ConfigSection section = (ConfigSection) entry.getValue();
                for (Map.Entry<String, Object> subEntry : section.values(true)
                        .entrySet()) {
                    values.put(key + root.settings().pathSeparator() + subEntry.getKey(), subEntry.getValue());
                }
            }
        }

        return values;
    }

    @Override
    public void opposite(@NotNull String path) {
        set(path, !getBoolean(path));
    }

    @Override
    public void multiply(@NotNull String path, double value) {
        set(path, getDouble(path) * value);
    }

    @Override
    public void divide(@NotNull String path, double value) {
        set(path, getDouble(path) / value);
    }

    @Override
    public void add(@NotNull String path, double value) {
        set(path, getDouble(path) + value);
    }

    @Override
    public void subtract(@NotNull String path, double value) {
        set(path, getDouble(path) - value);
    }

    @Override
    public Object getObject(@NotNull String path, @Nullable Object defaultValue) {
        Object value = getObject(path);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz) {
        Object value = this.getObject(path);
        if (value == null) {
            return null;
        }

        if (Number.class.isAssignableFrom(clazz)) {
            return NumberConversion.convert(value, clazz);
        }
        return clazz.isInstance(value) ? clazz.cast(value) : null;
    }

    @Override
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        T value = get(path, clazz);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean is(@NotNull String path, @NotNull Class<?> clazz) {
        Object object = getObject(path, clazz);
        return clazz.isInstance(object);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return getObject(path) instanceof String;
    }

    @Override
    public String getString(@NotNull String path, @Nullable String defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return getObject(path) instanceof Integer;
    }

    @Override
    public int getInt(@NotNull String path, int defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toInt(value) : defaultValue;
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return getObject(path) instanceof Double;
    }

    @Override
    public double getDouble(@NotNull String path, double defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toDouble(value) : defaultValue;
    }

    @Override
    public boolean isFloat(@NotNull String path) {
        return getObject(path) instanceof Float;
    }

    @Override
    public float getFloat(@NotNull String path, float defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toFloat(value) : defaultValue;
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return getObject(path) instanceof Long;
    }

    @Override
    public long getLong(@NotNull String path, long defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toLong(value) : defaultValue;
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        Object value = getObject(path, Boolean.class);
        return value instanceof Boolean;
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Boolean ? (boolean) value : defaultValue;
    }

    @Override
    public Class<?> getClass(@NotNull String path, @Nullable Class<?> defaultValue) {
        try {
            return Class.forName(getString(path));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        try {
            return Enum.valueOf(clazz, getString(path));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public UUID getUUID(@NotNull String path, @Nullable UUID defaultValue) {
        try {
            return UUID.fromString(getString(path));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public Version getVersion(@NotNull String path, @Nullable Version defaultValue) {
        try {
            return Version.parse(getString(path));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Override
    public boolean isList(@NotNull String path) {
        Object value = getObject(path);
        return value instanceof List;
    }

    @Override
    public List<?> getList(@NotNull String path, @Nullable List<?> defaultValue) {
        Object value = getObject(path, defaultValue);
        return (List<?>) (value instanceof List ? value : defaultValue);
    }

    @Override
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        List<T> list = new ArrayList<>();
        List<?> value = getList(path, new ArrayList<>());

        if (value == null || value.isEmpty()) {
            return list;
        }

        for (Object object : value) {
            if (clazz.isInstance(object)) {
                list.add(clazz.cast(object));
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getMapList(@NotNull String path) {
        List<?> list = getList(path, new ArrayList<>());
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (list == null || list.isEmpty()) {
            return mapList;
        }

        for (Object object : list) {
            if (object instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) object;
                mapList.add(map);
            }
        }

        return mapList;
    }

    @Override
    public List<ConfigSection> getSectionList(@NotNull String path) {
        List<Map<String, Object>> list = this.getMapList(path);
        final List<ConfigSection> sections = new ArrayList<>();

        for (Map<String, Object> map : list) {
            ConfigSection section = new MemorySection(this, path);

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                setSectionValue(section, entry.getKey(), entry.getValue());
            }
            sections.add(section);
        }

        return sections;
    }

    private void setSectionValue(@NotNull ConfigSection section, @NotNull String key, @Nullable Object value) {
        if (value instanceof Map) {
            ConfigSection child = section.createSection(key);

            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (entry.getKey() instanceof String) {
                    setSectionValue(child, (String) entry.getKey(), entry.getValue());
                }
            }
            return;
        }

        section.set(key, value);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, Object::toString);
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toBoolean);
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toInt);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toDouble);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toLong);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toFloat);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toByte);
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, object -> {
            String string = object.toString();
            return string.isEmpty() ? null : string.charAt(0);
        });
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, NumberConversion::toShort);
    }

    @Override
    public @NotNull <E extends Enum<E>> List<E> getEnumList(@NotNull String path, @NotNull Class<E> type) {
        List<?> list = this.getList(path, new ArrayList<>());
        return map(list, o -> Enum.valueOf(type, o.toString()));
    }

    @NotNull
    private <T> List<T> map(@Nullable List<?> list, @NotNull Function<Object, T> mapper) {
        if (list == null) return Collections.emptyList();

        List<T> mapped = new ArrayList<>();
        for (Object object : list) {

            T value = null;

            try {
                value = mapper.apply(object);
            } catch (Throwable ignored) {
            }

            if (value != null) {
                mapped.add(value);
            }
        }

        return mapped;
    }

}
