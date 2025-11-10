package dev.spoocy.utils.config.serializer.impl;

import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Map;

/**
 * Serializer for {@link Duration}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class DurationSerializer implements Serializer<Duration> {

    public static final DurationSerializer INSTANCE = new DurationSerializer();

    public static final String SERIALIZED_DURATION_SECONDS_KEY = "seconds";
    public static final String SERIALIZED_DURATION_NANOS_KEY = "nanos";

    private DurationSerializer() {}

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Duration object) {
        return Map.of(
                ConfigSerializer.SERIALIZED_TYPE_KEY, Duration.class.getName(),
                SERIALIZED_DURATION_SECONDS_KEY, object.getSeconds(),
                SERIALIZED_DURATION_NANOS_KEY, object.getNano()
        );
    }

    @Override
    public @NotNull Duration deserialize(@NotNull Map<String, Object> map) {
        return Duration.ofSeconds(
                ((Number) map.get(SERIALIZED_DURATION_SECONDS_KEY)).longValue(),
                ((Number) map.get(SERIALIZED_DURATION_NANOS_KEY)).longValue()
        );
    }
}
