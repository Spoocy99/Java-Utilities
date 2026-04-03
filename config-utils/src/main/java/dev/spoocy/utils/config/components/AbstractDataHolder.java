package dev.spoocy.utils.config.components;

import dev.spoocy.utils.common.misc.NumberConversion;
import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.serializer.Serializer;
import dev.spoocy.utils.config.serializer.Serializers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractDataHolder implements ConfigSection {

    private Serializers serializers() {
        return this.getRoot().settings().serializers();
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
    public <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        T value = getSerializable(path, clazz);
        return value != null ? value : defaultValue;
    }

    @Override
    @Nullable
    public <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        Object value = getObject(path);
        if (value == null) {
            return null;
        }

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        Serializers serializers = this.serializers();

        if (value instanceof ConfigSection) {
            Serializer<T> serializer = serializers.resolve(clazz);
            if (serializer != null) {
                return serializer.deserializeSafely(((ConfigSection) value).values(false));
            }
        }

        if (value instanceof Map) {
            Serializer<T> serializer = serializers.resolve(clazz);
            if (serializer != null) {
                return serializer.deserializeSafely((Map<String, Object>) value);
            }
        }

        return null;
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
    @Nullable
    public List<?> getList(@NotNull String path) {
        return getList(path, null);
    }

    @Override
    public List<?> getList(@NotNull String path, @Nullable List<?> defaultValue) {
        Object value = getObject(path, defaultValue);
        return (List<?>) (value instanceof List ? value : defaultValue);
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
                mapList.add((Map<String, Object>) object);
            }
        }

        return mapList;
    }

    @Override
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        List<T> list = new ArrayList<>();
        List<?> value = getList(path, new ArrayList<>());

        if (value == null || value.isEmpty()) {
            return list;
        }

        Serializer<T> serializer = this.serializers().resolve(clazz);
        if (serializer != null) {
            return map(list, object -> serializer.deserializeSafely((Map<String, Object>) object));
        }

        for (Object object : value) {
            if (clazz.isInstance(object)) {
                list.add(clazz.cast(object));
            }
        }

        return list;
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, Object::toString);
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toBoolean);
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toInt);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toDouble);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toLong);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toFloat);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toByte);
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, object -> {
            String string = object.toString();
            return string.isEmpty() ? null : string.charAt(0);
        });
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toShort);
    }

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
