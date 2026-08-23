package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.ConfigSection;
import dev.spoocy.utils.config.Readable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class DefaultPropertyLoader implements PropertyLoader {

    private static final Object UNRESOLVED = new Object();

    @Override
    public Object load(@NotNull ConfigSection config, @NotNull BoundField field) {
        String path = field.propertyKey();

        Object raw;

        if (config.isSection(path)) {
            raw = config.getSection(path).values(true);
        } else {

            if (!config.isSet(path)) {
                return null;
            }

            raw = config.getObject(path);
        }

        if (raw == null) {
            return null;
        }

        Class<?> fieldType = field.type();

        if (fieldType == String.class) {
            return config.getString(path, raw.toString());
        }

        if (fieldType == int.class || fieldType == Integer.class) {
            return config.getInt(path, raw instanceof Number ? ((Number) raw).intValue() : 0);
        }

        if (fieldType == long.class || fieldType == Long.class) {
            return config.getLong(path, raw instanceof Number ? ((Number) raw).longValue() : 0L);
        }

        if (fieldType == double.class || fieldType == Double.class) {
            return config.getDouble(path, raw instanceof Number ? ((Number) raw).doubleValue() : 0.0D);
        }

        if (fieldType == float.class || fieldType == Float.class) {
            return config.getFloat(path, raw instanceof Number ? ((Number) raw).floatValue() : 0.0F);
        }

        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return config.getBoolean(path, raw instanceof Boolean && (Boolean) raw);
        }

        if (fieldType == UUID.class) {
            return config.getUUID(path, raw instanceof UUID ? (UUID) raw : null);
        }

        if (fieldType == Version.class) {
            return config.getVersion(path, raw instanceof Version ? (Version) raw : null);
        }

        if (fieldType.isEnum()) {
            return resolveEnum(config, path, fieldType, raw);
        }

        if (Collection.class.isAssignableFrom(fieldType)) {
            Object collection = loadCollection(field, raw);
            if (collection != null) {
                return collection;
            }
        }

        if (fieldType.isArray() && raw instanceof List<?>) {
            return loadArray(fieldType.getComponentType(), (List<?>) raw);
        }

        if (fieldType.isInstance(raw)) {
            return raw;
        }

        Object converted = convertComplexValue(raw, fieldType);
        if (converted != UNRESOLVED) {
            return converted;
        }

        Object fallback = config.get(path, fieldType);
        if (fallback != null) {
            return fallback;
        }

        return raw;
    }

    @Nullable
    private Object loadCollection(@NotNull BoundField field, @NotNull Object raw) {
        Class<?> genericType = field.collectionElementType();
        if (genericType == null) {
            return raw instanceof Collection<?> ? raw : null;
        }

        if (List.class.isAssignableFrom(field.type())) {
            return convertCollectionValue(raw, genericType, false, field);
        }

        if (Set.class.isAssignableFrom(field.type())) {
            return convertCollectionValue(raw, genericType, true, field);
        }

        return null;
    }

    @NotNull
    private Collection<?> convertCollectionValue(
            @NotNull Object raw,
            @NotNull Class<?> elementType,
            boolean set,
            @NotNull BoundField field
    ) {
        if (!(raw instanceof Collection<?>)) {
            throw new IllegalArgumentException(
                    "Unsupported collection field data for '" + field.name() + "': " + raw.getClass()
                            .getName()
            );
        }

        Collection<?> source = (Collection<?>) raw;
        Collection<Object> values = set ? new java.util.LinkedHashSet<>(source.size()) : new ArrayList<>(source.size());

        for (Object entry : source) {
            Object converted = convertComplexValue(entry, elementType);
            if (converted == UNRESOLVED) {
                String entryType = entry == null ? "null" : entry.getClass()
                        .getName();
                throw new IllegalArgumentException(
                        "Cannot convert collection element of field '" + field.name() + "' from " + entryType +
                                " to " + elementType.getName()
                );
            }
            values.add(converted);
        }

        return values;
    }

    @Nullable
    private Object loadArray(@NotNull Class<?> componentType, @NotNull List<?> values) {
        Object array = java.lang.reflect.Array.newInstance(componentType, values.size());
        for (int i = 0; i < values.size(); i++) {
            Object converted = convertComplexValue(values.get(i), componentType);
            if (converted == UNRESOLVED) {
                throw new IllegalArgumentException("Cannot convert array element to " + componentType.getName());
            }
            java.lang.reflect.Array.set(array, i, converted);
        }
        return array;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E resolveEnum(
            @NotNull Readable readable,
            @NotNull String path,
            @NotNull Class<?> enumType,
            @Nullable Object defaultValue
    ) {
        Class<E> castedType = (Class<E>) enumType;
        E fallback = castedType.isInstance(defaultValue) ? castedType.cast(defaultValue) : null;
        return readable.getEnum(path, castedType, fallback);
    }

    private Object convertComplexValue(@NotNull Object raw, @NotNull Class<?> targetType) {
        if (targetType.isInstance(raw)) {
            return raw;
        }

        Class<?> boxedType = box(targetType);

        if (boxedType == String.class) {
            return raw.toString();
        }

        if (Number.class.isAssignableFrom(boxedType) && raw instanceof Number) {
            Number number = (Number) raw;
            if (boxedType == Integer.class) return number.intValue();
            if (boxedType == Long.class) return number.longValue();
            if (boxedType == Double.class) return number.doubleValue();
            if (boxedType == Float.class) return number.floatValue();
            if (boxedType == Short.class) return number.shortValue();
            if (boxedType == Byte.class) return number.byteValue();
        }

        if (boxedType == Boolean.class) {
            if (raw instanceof Boolean) {
                return raw;
            }
            if (raw instanceof String) {
                return Boolean.parseBoolean((String) raw);
            }
        }

        if (boxedType == Character.class) {
            if (raw instanceof Character) {
                return raw;
            }
            if (raw instanceof String) {
                String text = (String) raw;
                if (!text.isEmpty()) {
                    return text.charAt(0);
                }
            }
        }

        if (boxedType == UUID.class && raw instanceof String) {
            return UUID.fromString((String) raw);
        }

        if (boxedType == Version.class && raw instanceof String) {
            return Version.parse((String) raw);
        }

        if (boxedType.isEnum() && raw instanceof String) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object constant = Enum.valueOf((Class<? extends Enum>) boxedType.asSubclass(Enum.class), (String) raw);
            return constant;
        }

        if (raw instanceof ConfigSection) {
            return convertComplexValue(((ConfigSection) raw).values(true), targetType);
        }

        if (raw instanceof Map<?, ?>) {
            Object mapped = instantiateFromMap(targetType, (Map<?, ?>) raw);
            if (mapped != UNRESOLVED) {
                return mapped;
            }
        }

        if (raw instanceof Collection<?> && targetType.isArray()) {
            return loadArray(targetType.getComponentType(), List.copyOf((Collection<?>) raw));
        }

        Object singleArg = instantiateFromSingleArgument(targetType, raw);
        if (singleArg != UNRESOLVED) {
            return singleArg;
        }

        return UNRESOLVED;
    }

    private Object instantiateFromSingleArgument(@NotNull Class<?> targetType, @NotNull Object raw) {
        for (Constructor<?> constructor : targetType.getDeclaredConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (parameters.length != 1) {
                continue;
            }

            Object value = convertForParameter(raw, parameters[0]);
            if (value == UNRESOLVED) {
                continue;
            }

            try {
                constructor.setAccessible(true);
                return constructor.newInstance(value);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException("Failed to construct " + targetType.getName(), ex);
            }
        }

        return UNRESOLVED;
    }

    private Object instantiateFromMap(@NotNull Class<?> targetType, @NotNull Map<?, ?> raw) {
        if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) {
            return UNRESOLVED;
        }

        // If the map is empty, try the default constructor first
        if (raw.isEmpty()) {
            try {
                Constructor<?> constructor = targetType.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (ReflectiveOperationException ignored) {
                // Fall through to other constructor strategies.
            }
        }

        // Try constructors that accept a single parameter or match the map size
        for (Constructor<?> constructor : targetType.getDeclaredConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();

            if (parameters.length == 1) {
                Object value = convertForParameter(raw, parameters[0]);
                if (value == UNRESOLVED) {
                    continue;
                }

                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance(value);
                } catch (ReflectiveOperationException ex) {
                    throw new IllegalArgumentException("Failed to construct " + targetType.getName(), ex);
                }
            }

            if (parameters.length != raw.size()) {
                continue;
            }

            Object[] arguments = new Object[parameters.length];
            int index = 0;
            boolean compatible = true;
            for (Object value : raw.values()) {
                Object converted = convertForParameter(value, parameters[index]);
                if (converted == UNRESOLVED) {
                    compatible = false;
                    break;
                }
                arguments[index] = converted;
                index++;
            }

            if (!compatible) {
                continue;
            }

            try {
                constructor.setAccessible(true);
                return constructor.newInstance(arguments);
            } catch (ReflectiveOperationException ex) {
                throw new IllegalArgumentException("Failed to construct " + targetType.getName(), ex);
            }
        }

        // As a fallback, if a no-arg constructor exists, instantiate and populate fields from the map
        try {
            Constructor<?> noArg = targetType.getDeclaredConstructor();
            noArg.setAccessible(true);
            Object instance = noArg.newInstance();

            Class<?> current = targetType;
            while (current != null && current != Object.class) {
                for (java.lang.reflect.Field field : current.getDeclaredFields()) {
                    if (field.isSynthetic()) continue;
                    int mods = field.getModifiers();
                    if (Modifier.isStatic(mods) || Modifier.isTransient(mods) || Modifier.isFinal(mods)) continue;

                    ConfigProperty ann = field.getAnnotation(ConfigProperty.class);
                    String key;
                    if (ann != null) {
                        key = ann.value();
                    } else {
                        // convert camelCase field name to property name (dash + lowercase for uppercase letters)
                        String name = field.getName();
                        StringBuilder builder = new StringBuilder(name.length());
                        for (int i = 0; i < name.length(); i++) {
                            char c = name.charAt(i);
                            if (Character.isUpperCase(c)) {
                                builder.append('-');
                                builder.append(Character.toLowerCase(c));
                            } else {
                                builder.append(c);
                            }
                        }
                        key = builder.toString();
                    }

                    if (!raw.containsKey(key)) continue;

                    Object rawValue = raw.get(key);
                    Object converted = convertComplexValue(rawValue, field.getType());
                    if (converted == UNRESOLVED) {
                        throw new IllegalArgumentException("Cannot convert map value for field '" + field.getName() + "' to " + field.getType().getName());
                    }

                    field.setAccessible(true);
                    field.set(instance, converted);
                }
                current = current.getSuperclass();
            }

            return instance;
        } catch (ReflectiveOperationException ignored) {
            // No suitable no-arg constructor or failed to populate, fall through
        }

        return UNRESOLVED;
    }

    private Object convertForParameter(@NotNull Object raw, @NotNull Class<?> parameterType) {
        Object converted = convertComplexValue(raw, parameterType);
        if (converted != UNRESOLVED) {
            return converted;
        }

        Class<?> boxed = box(parameterType);
        if (boxed.isInstance(raw)) {
            return raw;
        }

        return UNRESOLVED;
    }

    @NotNull
    private static Class<?> box(@NotNull Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }

        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        return type;
    }
}
