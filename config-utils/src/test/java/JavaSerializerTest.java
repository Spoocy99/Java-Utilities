import dev.spoocy.utils.config.serializer.impl.JavaSerializer;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

class JavaSerializerTest {

    @Test
    void serializeDeserializeDuration_roundTrip() {
        JavaSerializer<Duration> serializer = JavaSerializer.create(Duration.class);
        Duration original = Duration.ofHours(5).plusMinutes(30).plusSeconds(15);
        Map<String, Object> data = serializer.serialize(original);

        Duration restored = serializer.deserialize(data);
        assertEquals(original, restored);
    }

    @Test
    void serializeDeserializeCustomSerializable_roundTrip() {
        JavaSerializer<Person> serializer = JavaSerializer.create(Person.class);
        Person original = new Person("Alice", 28);
        Map<String, Object> data = serializer.serialize(original);

        Person restored = serializer.deserialize(data);
        assertEquals(original, restored);
    }

    @Test
    void deserialize_missingDataKey_throwsIllegalArgumentException() {
        JavaSerializer<Duration> serializer = JavaSerializer.create(Duration.class);
        Map<String, Object> bad = Map.of("class", "java.time.Duration");
        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(bad));
    }

    @Test
    void deserialize_invalidBase64_throwsIllegalArgumentException() {
        JavaSerializer<Duration> serializer = JavaSerializer.create(Duration.class);
        Map<String, Object> bad = Map.of("class", "java.time.Duration", "data", "not-base64-!!!");
        assertThrows(IllegalArgumentException.class, () -> serializer.deserialize(bad));
    }

    @Test
    void deserialize_corruptedBytes_throwsRuntimeException() {
        JavaSerializer<Duration> serializer = JavaSerializer.create(Duration.class);
        // valid base64 but not a serialized object
        String corrupted = Base64.getEncoder().encodeToString(new byte[]{1, 2, 3, 4, 5});
        Map<String, Object> bad = Map.of("class", "java.time.Duration", "data", corrupted);
        assertThrows(RuntimeException.class, () -> serializer.deserialize(bad));
    }

    // Serializable class for testing
    static final class Person implements Serializable {
        private static final long serialVersionUID = 1L;
        final String name;
        final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person p = (Person) o;
            return age == p.age && (name == null ? p.name == null : name.equals(p.name));
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }
    }
}
