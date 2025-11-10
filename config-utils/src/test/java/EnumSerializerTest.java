import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.impl.EnumSerializer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class EnumSerializerTest {

    private enum Color {
        RED, GREEN, BLUE
    }

    @Test
    void serializeEnum() {
        EnumSerializer<Color> serializer = EnumSerializer.create(Color.class);
        Color value = Color.RED;

        Map<String, Object> map = serializer.serialize(value);

        assertEquals("RED", map.get(EnumSerializer.SERIALIZED_ENUM_NAME_KEY));
        assertEquals(Color.class.getName(), map.get(ConfigSerializer.SERIALIZED_TYPE_KEY));
    }

    @Test
    void deserializeEnum() {
        EnumSerializer<Color> serializer = EnumSerializer.create(Color.class);
        Map<String, Object> map = new HashMap<>();
        map.put(EnumSerializer.SERIALIZED_ENUM_NAME_KEY, "GREEN");
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, Color.class.getName());

        Color result = serializer.deserialize(map);

        assertEquals(Color.GREEN, result);
    }

    @Test
    void deserializeMissingValueThrows() {
        EnumSerializer<Color> serializer = EnumSerializer.create(Color.class);
        Map<String, Object> map = new HashMap<>();
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, Color.class.getName());

        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(map));
    }

    @Test
    void deserializeUnknownConstantThrows() {
        EnumSerializer<Color> serializer = EnumSerializer.create(Color.class);
        Map<String, Object> map = new HashMap<>();
        map.put(EnumSerializer.SERIALIZED_ENUM_NAME_KEY, "UNKNOWN");
        map.put(ConfigSerializer.SERIALIZED_TYPE_KEY, Color.class.getName());

        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(map));
    }
}
