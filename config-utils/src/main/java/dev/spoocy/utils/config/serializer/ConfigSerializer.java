package dev.spoocy.utils.config.serializer;

import dev.spoocy.utils.common.cache.Cache;
import dev.spoocy.utils.common.cache.Caches;
import dev.spoocy.utils.config.serializer.impl.AtomicsSerializer;
import dev.spoocy.utils.config.serializer.impl.DurationSerializer;
import dev.spoocy.utils.config.serializer.impl.EnumSerializer;
import dev.spoocy.utils.config.serializer.impl.JavaSerializer;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigSerializer {

    public static final String SERIALIZED_TYPE_KEY = "==";
    private static final Map<Class<?>, Serializer<?>> DIRECT_SERIALIZERS = new ConcurrentHashMap<>();
    private static final Map<String, Class<? extends ConfigSerializable>> NAMES = new ConcurrentHashMap<>();

    static {
        registerSerializer(java.time.Duration.class, DurationSerializer.INSTANCE);
        registerSerializer(java.util.concurrent.atomic.AtomicInteger.class, new AtomicsSerializer<>(java.util.concurrent.atomic.AtomicInteger.class));
        registerSerializer(java.util.concurrent.atomic.AtomicLong.class, new AtomicsSerializer<>(java.util.concurrent.atomic.AtomicLong.class));
        registerSerializer(java.util.concurrent.atomic.AtomicBoolean.class, new AtomicsSerializer<>(java.util.concurrent.atomic.AtomicBoolean.class));
    }

    /**
     * Singleton instance of ClassSerializer for handling a specific class type.
     *
     * @param serializer the ClassSerializer instance
     */
    public static <T> void registerSerializer(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer) {
        DIRECT_SERIALIZERS.put(clazz, serializer);
    }

    /**
     * Register a Java {@link Serializable} class for serialization/deserialization using Java's built-in serialization.
     *
     * @param clazz the class to register
     *
     * @see JavaSerializer
     */
    public static <T extends Serializable> void useJavaSerializer(@NotNull Class<T> clazz) {
        registerSerializer(clazz, JavaSerializer.create(clazz));
    }

    /**
     * {@link ConfigSerializable} Class to be registered for serialization/deserialization.
     *
     * @param clazz the class to register
     */
    public static void register(@NotNull Class<? extends ConfigSerializable> clazz) {
        String name = getSerializeName(clazz);
        if (NAMES.containsKey(name)) {
            throw new IllegalArgumentException("Class with name '" + name + "' is already registered for serialization: " + NAMES.get(name).getName());
        }
        NAMES.put(name, clazz);
    }

    /**
     * Get a registered {@link ConfigSerializable} class by its serialization name.
     *
     * @param name the name used during serialization
     *
     * @return the registered class, or null if not found
     */
    @Nullable
    public static Class<? extends ConfigSerializable> getSerializableClassByName(@NotNull String name) {
        return NAMES.get(name);
    }

    /**
     * Resolve an appropriate {@link Serializer} for the given class.
     *
     * @param clazz the class to find a serializer for
     * @return the resolved ObjectSerializer, or null if none found
     *
     * @param <O> the type of the object to be serialized/deserialized
     */
    @Nullable
    public static <O> Serializer<O> resolve(@NotNull Class<O> clazz) {
        Serializer<?> serializer = DIRECT_SERIALIZERS.get(clazz);

        // direct serializer
        if(serializer != null) {
            return (Serializer<O>) serializer;

        // implements ConfigSerializable
        } else if(ConfigSerializable.class.isAssignableFrom(clazz)) {
            Class<? extends ConfigSerializable> csClass = (Class<? extends ConfigSerializable>) clazz;
            return (Serializer<O>) ClassSerializer.create(csClass);

        // is Enum
        } else if (clazz.isEnum()) {
            return EnumSerializer.create((Class<? extends Enum>) clazz);
        }

        return null;
    }

    /**
     * Serialize a {@link ConfigSerializable} object to a map.
     *
     *
     * @param map the map to deserialize
     *
     * @return the deserialized object, or null if deserialization fails
     *
     * @throws IllegalArgumentException if deserialization fails
     */
    @Nullable
    public static ConfigSerializable deserialize(@NotNull Map<String, Object> map) {
        Object typeObj = map.get(SERIALIZED_TYPE_KEY);
        if (!(typeObj instanceof String)) {
            return null;
        }

        String typeName = (String) typeObj;
        Class<? extends ConfigSerializable> clazz = getSerializableClassByName(typeName);
        if (clazz == null) {
            return null;
        }

        Serializer<?> serializer = resolve(clazz);
        if (serializer == null) {
            return null;
        }

        try {
            return (ConfigSerializable) serializer.deserialize(map);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the serialization name for a given {@link ConfigSerializable} class.
     *
     * @param clazz the class to get the serialization name for
     *
     * @return the serialization name
     */
    @NotNull
    public static String getSerializeName(@NotNull Class<? extends ConfigSerializable> clazz) {
        DelegateDeserialization delegate = clazz.getAnnotation(DelegateDeserialization.class);
        if (delegate != null && delegate.value() != clazz) {
            return getSerializeName(delegate.value());
        }

        SerializableAs alias = clazz.getAnnotation(SerializableAs.class);
        if (alias != null) {
            return alias.value();
        }

        return clazz.getName();
    }

    /**
     * Serializer implementation for classes implementing {@link ConfigSerializable}.
     */
    public static class ClassSerializer<O extends ConfigSerializable> implements Serializer<O> {

        private static final Cache<Class<?>, ClassSerializer<?>> CLASS_CACHE = Caches.createLRUCache(200);
        private static final Cache<String, MethodAccessor> METHOD_CACHE = Caches.createLRUCache(200);

        public static <O extends ConfigSerializable> ClassSerializer<O> create(@NotNull Class<O> clazz) {
            return (ClassSerializer<O>) CLASS_CACHE.computeIfAbsent(clazz, c -> new ClassSerializer<>(clazz));
        }

        @Nullable
        private MethodAccessor findMethod(String methodName) {
            return METHOD_CACHE.computeIfAbsent(
                this.clazz + "#" + methodName,
                    key -> this.access.method(
                            Reflection.method()
                                    .requireStatic()
                                    .name(methodName)
                                    .parameterCount(1)
                                    .parameterType(0, Map.class)
                                    .build()
                    ));
        }

        private final Class<O> clazz;
        private final ClassAccess access;

        private ClassSerializer(@NotNull Class<O> clazz) {
            this.clazz = clazz;
            this.access = Reflection.builder()
                    .forClass(clazz)
                    .publicMembers()
                    .buildAccess();
        }

        @Override
        public @NotNull Map<String, Object> serialize(@NotNull O object) {
            try {
                Map<String, Object> map = object.serialize();
                map.put(SERIALIZED_TYPE_KEY, getSerializeName(object.getClass()));
                return map;
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass().getName(), e);
            }
        }

        @Override
        public @NotNull O deserialize(@NotNull Map<String, Object> map) {
            Object typeObj = map.get(SERIALIZED_TYPE_KEY);
            if (!(typeObj instanceof String)) {
                throw new IllegalArgumentException("Serialized type key is missing or not a string!");
            }

            MethodAccessor deserializeMethod = findMethod("deserialize");
            if (deserializeMethod == null) {
                deserializeMethod = findMethod("valueOf");
            }

            if (deserializeMethod == null) {
                throw new IllegalStateException("Class " + clazz.getName() + " does not have a static deserialize(Map<String, Object>) method!");
            }

            Object result = deserializeMethod.invoke(null, map);
            if (!clazz.isInstance(result)) {
                throw new IllegalStateException("Deserialized object is not of type " + clazz.getName() + "!");
            }

            return (O) result;
        }
    }

}
