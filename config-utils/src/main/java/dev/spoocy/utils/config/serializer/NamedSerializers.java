package dev.spoocy.utils.config.serializer;

import dev.spoocy.utils.common.cache.Cache;
import dev.spoocy.utils.common.cache.Caches;
import dev.spoocy.utils.reflection.Reflection;
import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class NamedSerializers extends BaseSerializers {

    public static final NamedSerializers DEFAULT_INSTANCE = new NamedSerializers();

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

    private final Map<String, Class<?>> names = new ConcurrentHashMap<>();

    public NamedSerializers() {
        super();
    }

    public void register(@NotNull Class<? extends ConfigSerializable> clazz) {
        String name = getSerializeName(clazz);
        this.registerName(clazz, name);
    }

    protected void registerName(@NotNull Class<?> clazz, @NotNull String name) {
        if (this.names.containsKey(name)) {
            throw new IllegalArgumentException("Class with name '" + name + "' is already registered for serialization: " + this.names.get(name).getName());
        }
        this.names.put(name, clazz);
    }

    /**
     * Get a registered {@link ConfigSerializable} class by its serialization name.
     *
     * @param name the name used during serialization
     *
     * @return the registered class, or null if not found
     */
    @Nullable
    public Class<?> getSerializableClassByName(@NotNull String name) {
        return this.names.get(name);
    }

    @Nullable
    public <T> Serializer<T> resolve(@NotNull Class<T> clazz) {
        Serializer<?> serializer = super.resolve(clazz);

        if(serializer != null) {
            // direct serializer found

            return (Serializer<T>) serializer;
        }

        if(ConfigSerializable.class.isAssignableFrom(clazz)) {
            // implements ConfigSerializable

            Class<? extends ConfigSerializable> csClass = (Class<? extends ConfigSerializable>) clazz;
            return (Serializer<T>) ClassSerializer.create(csClass);

        }

        return null;
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Object object) {
        Serializer serializer = resolve(object.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for class: " + object.getClass().getName());
        }
        return serializer.serialize(object);
    }

    @Override
    public @Nullable Object deserialize(@NotNull Map<String, Object> map) {
        Object typeObj = map.get(SERIALIZED_TYPE_KEY);
        if (!(typeObj instanceof String)) {
            return null;
        }

        String typeName = (String) typeObj;
        Class<?> clazz = getSerializableClassByName(typeName);
        if (clazz == null) {
            return null;
        }

        Serializer<?> serializer = resolve(clazz);
        if (serializer == null) {
            return null;
        }

        try {
            return serializer.deserialize(map);
        } catch (Exception e) {
            return null;
        }
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
                Map<String, Object> map = new HashMap<>(object.serialize());
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

        @Nullable
        private MethodAccessor findMethod(@NotNull String methodName) {
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

    }

}
