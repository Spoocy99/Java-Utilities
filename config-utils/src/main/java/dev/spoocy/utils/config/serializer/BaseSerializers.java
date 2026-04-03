package dev.spoocy.utils.config.serializer;

import dev.spoocy.utils.config.serializer.impl.AtomicsSerializer;
import dev.spoocy.utils.config.serializer.impl.DurationSerializer;
import dev.spoocy.utils.config.serializer.impl.JavaSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class BaseSerializers implements Serializers {

    public static final String SERIALIZED_TYPE_KEY = "==";
    private static final Map<Class<?>, Serializer<?>> DEFAULT_SERIALIZERS = new ConcurrentHashMap<>();

    static {
        registerDefault(Duration.class, DurationSerializer.INSTANCE);
        registerDefault(AtomicInteger.class, new AtomicsSerializer<>(AtomicInteger.class));
        registerDefault(AtomicLong.class, new AtomicsSerializer<>(AtomicLong.class));
        registerDefault(AtomicBoolean.class, new AtomicsSerializer<>(AtomicBoolean.class));
    }

    public static <T> void registerDefault(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer) {
        DEFAULT_SERIALIZERS.put(clazz, serializer);
    }

    private final Map<Class<?>, Serializer<?>> serializers = new ConcurrentHashMap<>();

    protected BaseSerializers() { }

    @Override
    public <T> void register(@NotNull Class<T> clazz, @NotNull Serializer<T> serializer) {
        this.serializers.put(clazz, serializer);
    }

    public  <T extends Serializable> void registerJavaSerializer(@NotNull Class<T> clazz) {
        this.register(clazz, JavaSerializer.create(clazz));
    }

    @Override
    public @Nullable <T> Serializer<T> resolve(@NotNull Class<T> clazz) {
        Serializer<?> serializer = this.serializers.get(clazz);
        if (serializer != null) {
            return (Serializer<T>) serializer;
        }

        return (Serializer<T>) DEFAULT_SERIALIZERS.get(clazz);
    }

}
