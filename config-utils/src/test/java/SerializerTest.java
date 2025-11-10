import dev.spoocy.utils.config.serializer.ConfigSerializable;
import dev.spoocy.utils.config.serializer.ConfigSerializer;
import dev.spoocy.utils.config.serializer.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SerializerTest {

    // Simple ConfigSerializable implementation used in tests
    public static class Person implements ConfigSerializable {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("age", age);
            return map;
        }

        public static Person deserialize(Map<String, Object> map) {
            Object nameObj = map.get("name");
            Object ageObj = map.get("age");
            String name = nameObj instanceof String ? (String) nameObj : null;
            int age = ageObj instanceof Number ? ((Number) ageObj).intValue() : 0;
            return new Person(name, age);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person person = (Person) o;
            return age == person.age && (name == null ? person.name == null : name.equals(person.name));
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            return result;
        }
    }

    @BeforeAll
    static void registerSerializableClass() {
        // Ensure Person class is registered before tests
        ConfigSerializer.register(Person.class);
    }

    @Test
    void registerAndDeserializeUsingConfigSerializer() {
        // obtain serializer via resolve and serialize an instance
        Serializer<Person> serializer = ConfigSerializer.resolve(Person.class);
        assertNotNull(serializer);

        Person original = new Person("Alice", 30);
        Map<String, Object> serialized = serializer.serialize(original);

        // ensure the serializer included the serialized type key
        assertEquals(ConfigSerializer.getSerializeName(Person.class), serialized.get(ConfigSerializer.SERIALIZED_TYPE_KEY));

        // use ConfigSerializer.deserialize to reconstruct the object
        ConfigSerializable cs = ConfigSerializer.deserialize(serialized);
        assertNotNull(cs);
        assertTrue(cs instanceof Person);

        Person deserialized = (Person) cs;
        assertEquals(original, deserialized);
    }

    @Test
    void duplicateRegisterThrows() {
        assertThrows(IllegalArgumentException.class, () -> ConfigSerializer.register(Person.class));
    }

    @Test
    void deserializeMissingOrUnknownTypeReturnsNull() {
        // missing serialized type key
        Map<String, Object> missing = new HashMap<>();
        missing.put("foo", "bar");
        assertNull(ConfigSerializer.deserialize(missing));

        // unknown type name
        Map<String, Object> unknown = new HashMap<>();
        unknown.put(ConfigSerializer.SERIALIZED_TYPE_KEY, "non.existent.ClassName");
        assertNull(ConfigSerializer.deserialize(unknown));

        // serialized type key present but not a string
        Map<String, Object> wrongType = new HashMap<>();
        wrongType.put(ConfigSerializer.SERIALIZED_TYPE_KEY, 123);
        assertNull(ConfigSerializer.deserialize(wrongType));
    }

    @Test
    void resolveReturnsClassSerializerForConfigSerializable() {
        // ensure resolver returns a serializer for classes implementing ConfigSerializable
        Serializer<Person> serializer = ConfigSerializer.resolve(Person.class);
        assertNotNull(serializer);

        Person p = new Person("Bob", 25);
        Map<String, Object> map = serializer.serialize(p);

        // serialized map must contain the type key and the values
        assertEquals(ConfigSerializer.getSerializeName(Person.class), map.get(ConfigSerializer.SERIALIZED_TYPE_KEY));
        assertEquals("Bob", map.get("name"));
        assertEquals(25, ((Number) map.get("age")).intValue());
    }
}
