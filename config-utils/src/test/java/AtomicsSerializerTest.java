import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.impl.AtomicsSerializer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class AtomicsSerializerTest {

    @Test
    void serializeAtomicInteger() {
        AtomicsSerializer<AtomicInteger> serializer = new AtomicsSerializer<>(AtomicInteger.class);
        AtomicInteger value = new AtomicInteger(42);

        Map<String, Object> map = serializer.serialize(value);

        assertEquals(42, ((Number) map.get(AtomicsSerializer.SERIALIZED_ATOMIC_VALUE_KEY)).intValue());
        assertEquals(AtomicInteger.class.getName(), map.get(ConfigSerializer.SERIALIZED_TYPE_KEY));
    }

    @Test
    void deserializeAtomicInteger() {
        AtomicsSerializer<AtomicInteger> serializer = new AtomicsSerializer<>(AtomicInteger.class);
        Map<String, Object> map = new HashMap<>();
        map.put("value", Integer.valueOf(7));
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, AtomicInteger.class.getName());

        AtomicInteger result = serializer.deserialize(map);

        assertEquals(7, result.get());
    }

    @Test
    void serializeAtomicLong() {
        AtomicsSerializer<AtomicLong> serializer = new AtomicsSerializer<>(AtomicLong.class);
        AtomicLong value = new AtomicLong(123456789L);

        Map<String, Object> map = serializer.serialize(value);

        assertEquals(123456789L, ((Number) map.get(AtomicsSerializer.SERIALIZED_ATOMIC_VALUE_KEY)).longValue());
        assertEquals(AtomicLong.class.getName(), map.get(ConfigSerializer.SERIALIZED_TYPE_KEY));
    }

    @Test
    void deserializeAtomicLong() {
        AtomicsSerializer<AtomicLong> serializer = new AtomicsSerializer<>(AtomicLong.class);
        Map<String, Object> map = new HashMap<>();
        map.put(AtomicsSerializer.SERIALIZED_ATOMIC_VALUE_KEY, Long.valueOf(99L));
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, AtomicLong.class.getName());

        AtomicLong result = serializer.deserialize(map);

        assertEquals(99L, result.get());
    }

    @Test
    void serializeAtomicBoolean() {
        AtomicsSerializer<AtomicBoolean> serializer = new AtomicsSerializer<>(AtomicBoolean.class);
        AtomicBoolean value = new AtomicBoolean(true);

        Map<String, Object> map = serializer.serialize(value);

        assertEquals(Boolean.TRUE, map.get(AtomicsSerializer.SERIALIZED_ATOMIC_VALUE_KEY));
        assertEquals(AtomicBoolean.class.getName(), map.get(ConfigSerializer.SERIALIZED_TYPE_KEY));
    }

    @Test
    void deserializeAtomicBoolean() {
        AtomicsSerializer<AtomicBoolean> serializer = new AtomicsSerializer<>(AtomicBoolean.class);
        Map<String, Object> map = new HashMap<>();
        map.put(AtomicsSerializer.SERIALIZED_ATOMIC_VALUE_KEY, Boolean.FALSE);
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, AtomicBoolean.class.getName());

        AtomicBoolean result = serializer.deserialize(map);

        assertFalse(result.get());
    }

    @Test
    void serializeUnsupportedTypeThrows() {
        AtomicsSerializer<String> serializer = new AtomicsSerializer<>(String.class);
        assertThrows(IllegalArgumentException.class, () -> serializer.serialize("not-an-atomic"));
    }

    @Test
    void deserializeMissingValueThrows() {
        AtomicsSerializer<AtomicInteger> serializer = new AtomicsSerializer<>(AtomicInteger.class);
        Map<String, Object> map = new HashMap<>();
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, AtomicInteger.class.getName());

        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(map));
    }

    @Test
    void deserializeUnsupportedTypeThrows() {
        AtomicsSerializer<String> serializer = new AtomicsSerializer<>(String.class);
        Map<String, Object> map = new HashMap<>();
        map.put("value", "x");
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, String.class.getName());

        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(map));
    }

}
