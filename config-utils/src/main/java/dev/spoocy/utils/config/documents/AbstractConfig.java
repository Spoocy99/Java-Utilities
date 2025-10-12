package dev.spoocy.utils.config.documents;

import dev.spoocy.utils.common.Version.Version;
import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.common.misc.NumberConversion;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class AbstractConfig implements Config {

    private boolean readonly = false;
    private final Config parent;

    public AbstractConfig() {
		this.parent = this;
	}

    public AbstractConfig(@NotNull Config parent) {
		this.parent = parent;
	}

    @Override
    public Config getParent() {
        return this.parent;
    }

    @Override
    public void setReadOnly() {
        this.readonly = true;
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
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
    public void set(@NotNull String path, @Nullable Object value) {
        if(isReadonly()) throw new UnsupportedOperationException("Config is readonly!");

        if(value == null) {
            remove(path);
            return;
        }

        @SuppressWarnings("unchecked")
        Serializer<Object> serializer = (Serializer<Object>) ConfigSerializer.resolve(value.getClass());

        if(serializer != null) {
            value = serializer.serialize(value);

        } else if (value instanceof byte[]) {
            value = Base64.getEncoder().encodeToString((byte[]) value);

        } else if (value instanceof Enum<?>) {
			Enum<?> type = (Enum<?>) value;
			value = type.name();
		}

        set0(path, value);
    }
    protected abstract void set0(@NotNull String path, @Nullable Object value);

    @Override
    @Nullable
    public Object getObject(@NotNull String path) {
        return get0(path);
    }
    protected abstract @Nullable Object get0(@NotNull String path);

    @Override
    public Object getObject(@NotNull String path, @Nullable Object defaultValue) {
        Object value = getObject(path);
        return value != null ? value : defaultValue;
    }

    @Override
    public <T> T get(@NotNull String path, @NotNull Class<T> clazz) {
        Serializer<T> serializer = ConfigSerializer.resolve(clazz);

        if(serializer != null) {
            Config config = getSection(path);

            try {
                return serializer.deserialize(config.values());
            } catch (Throwable e) {
                ILogger.forThisClass().error("Failed to deserialize object of type " + clazz + " at path " + path, e);
            }

        }

        Object value = this.getObject(path);
        if(value == null) {
            return null;
        }

        if(Number.class.isAssignableFrom(clazz)) {
            return NumberConversion.convert(value, clazz);
        }
        return clazz.isInstance(value) ? clazz.cast(value) : null;
    }

    @Override
    public <T> T get(@NotNull String path, @Nullable T defaultValue) {
        if(defaultValue == null) return null;

        Class<T> clazz = (Class<T>) defaultValue.getClass();
        T value = get(path, clazz);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean isOf(@NotNull String path, @NotNull Class<?> clazz) {
        Object object = getObject(path, clazz);
        return clazz.isInstance(object);
    }

    @Override
    public <T> T getSerializable(@NotNull String path, @Nullable T defaultValue) {
        if(defaultValue == null) return null;

        T value = getSerializable(path, (Class<T>) defaultValue.getClass());
       return value != null ? value : defaultValue;
    }

    @Override
    @Nullable
    public <T> T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        if(!isSet(path)) {
            return null;
        }
        return get(path, clazz);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return getObject(path) instanceof String;
    }

    @Override
    public @NotNull String getString(@NotNull String path) {
        return getString(path, "");
    }

    @Override
    public String getString(@NotNull String path, @NotNull String defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value != null ? value.toString() : defaultValue;
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, Object::toString);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return getObject(path) instanceof Integer;
    }

    @Override
    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(@NotNull String path, int defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toInt(value) : defaultValue;
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toInt);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return getObject(path) instanceof Double;
    }

    @Override
    public double getDouble(@NotNull String path) {
        return getDouble(path, 0.0);
    }

    @Override
    public double getDouble(@NotNull String path, double defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toDouble(value) : defaultValue;
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toDouble);
    }

    @Override
    public boolean isFloat(@NotNull String path) {
        return getObject(path) instanceof Float;
    }

    @Override
    public float getFloat(@NotNull String path) {
        return getFloat(path, 0f);
    }

    @Override
    public float getFloat(@NotNull String path, float defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toFloat(value) : defaultValue;
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toFloat);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return getObject(path) instanceof Long;
    }

    @Override
    public long getLong(@NotNull String path) {
        return getLong(path, 0L);
    }

    @Override
    public long getLong(@NotNull String path, long defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Number ? NumberConversion.toLong(value) : defaultValue;
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toLong);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        Object value = getObject(path, Boolean.class);
        return value instanceof Boolean;
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        Object value = this.getObject(path, defaultValue);
        return value instanceof Boolean ? (boolean) value : defaultValue;
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        List<?> list = this.getList(path);
        return map(list, NumberConversion::toBoolean);
    }

    @Override
    public Class<?> getClass(@NotNull String path) {
        return getClass(path, null);
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
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return getEnum(path, clazz, null);
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
    public UUID getUUID(@NotNull String path) {
        return getUUID(path, null);
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
    public Date getDate(@NotNull String path) {
        return getDate(path, null);
    }

    @Override
    public Date getDate(@NotNull String path, @Nullable Date defaultValue) {
        try {
            return Date.from(getOffsetDateTime(path).toInstant());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public OffsetDateTime getOffsetDateTime(@NotNull String path) {
        return getOffsetDateTime(path, null);
    }

    @Override
    public OffsetDateTime getOffsetDateTime(@NotNull String path, @Nullable OffsetDateTime defaultValue) {
        try {
            return OffsetDateTime.parse(getString(path));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Version getVersion(@NotNull String path) {
        return getVersion(path, null);
    }

    @Override
    public Version getVersion(@NotNull String path, @Nullable Version defaultValue) {
        try {
            return Version.parse(getString(path));
        } catch (Exception e) {
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
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz, @Nullable List<T> defaultValue) {
        List<T> list = new ArrayList<>();
        List<?> value = getList(path, new ArrayList<>());

        if(value == null || value.isEmpty()) {
            return list;
        }

        Serializer<T> serializer = ConfigSerializer.resolve(clazz);
        if(serializer != null) {
            return map(list, object -> serializer.deserializeSafely((Map<String, Object>) object));
        }

        for(Object object : value) {
            if(clazz.isInstance(object)) {
                list.add(clazz.cast(object));
            }
        }

        return list;
    }

    @Override
    public Map<String, String> valuesAsString() {
        Map<String, String> values = new HashMap<>();
		this.values().forEach((key, value) -> values.put(key, String.valueOf(value)));
		return values;
    }

    private <T> List<T> map(@Nullable List<?> list, @NotNull Function<Object, T> mapper) {
        if(list == null) return Collections.emptyList();

        List<T> mapped = new ArrayList<>();
        for(Object object : list) {

            T value = null;

            try {
                value = mapper.apply(object);
            } catch (Throwable ignored) { }

            if(value != null) {
                mapped.add(value);
            }
        }

        return mapped;
    }
}
