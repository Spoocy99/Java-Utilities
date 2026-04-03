package dev.spoocy.utils.config.serializer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SerializerTest {

    private static class LocalSerializers extends BaseSerializers {
        @Override
        public @NotNull Map<String, Object> serialize(@NotNull Object object) {
            throw new UnsupportedOperationException("Not needed in this test");
        }

        @Override
        public Object deserialize(@NotNull Map<String, Object> map) {
            throw new UnsupportedOperationException("Not needed in this test");
        }
    }

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

    private static final NamedSerializers serializers = NamedSerializers.DEFAULT_INSTANCE;

    @BeforeAll
    static void registerSerializableClass() {
        // Ensure Person class is registered before tests
        serializers.register(Person.class);
    }

    @Test
    void registerAndDeserializeUsingConfigSerializer() {
        // obtain serializer via resolve and serialize an instance
        Serializer<Person> serializer = serializers.resolve(Person.class);
        assertNotNull(serializer);

        Person original = new Person("Alice", 30);
        Map<String, Object> serialized = serializer.serialize(original);

        // ensure the serializer included the serialized type key
        assertEquals(NamedSerializers.getSerializeName(Person.class), serialized.get(NamedSerializers.SERIALIZED_TYPE_KEY));

        // use ConfigSerializer.deserialize to reconstruct the object
        ConfigSerializable cs = serializer.deserialize(serialized);
        assertNotNull(cs);
        assertTrue(cs instanceof Person);

        Person deserialized = (Person) cs;
        assertEquals(original, deserialized);
    }

    @Test
    void duplicateRegisterThrows() {
        assertThrows(IllegalArgumentException.class, () -> serializers.register(Person.class));
    }

    @Test
    void deserializeMissingOrUnknownTypeReturnsNull() {
        // missing serialized type key
        Map<String, Object> missing = new HashMap<>();
        missing.put("foo", "bar");
        assertNull(serializers.deserialize(missing));

        // unknown type name
        Map<String, Object> unknown = new HashMap<>();
        unknown.put(NamedSerializers.SERIALIZED_TYPE_KEY, "non.existent.ClassName");
        assertNull(serializers.deserialize(unknown));

        // serialized type key present but not a string
        Map<String, Object> wrongType = new HashMap<>();
        wrongType.put(NamedSerializers.SERIALIZED_TYPE_KEY, 123);
        assertNull(serializers.deserialize(wrongType));
    }

    @Test
    void resolveReturnsClassSerializerForConfigSerializable() {
        // ensure resolver returns a serializer for classes implementing ConfigSerializable
        Serializer<Person> serializer = serializers.resolve(Person.class);
        assertNotNull(serializer);

        Person p = new Person("Bob", 25);
        Map<String, Object> map = serializer.serialize(p);

        // serialized map must contain the type key and the values
        assertEquals(NamedSerializers.getSerializeName(Person.class), map.get(NamedSerializers.SERIALIZED_TYPE_KEY));
        assertEquals("Bob", map.get("name"));
        assertEquals(25, ((Number) map.get("age")).intValue());
    }

    @Test
    void baseSerializersProvideStaticDefaultsForAllSubclasses() {
        LocalSerializers first = new LocalSerializers();
        LocalSerializers second = new LocalSerializers();

        assertNotNull(first.resolve(Duration.class));
        assertNotNull(second.resolve(Duration.class));
        assertNotNull(first.resolve(AtomicInteger.class));
        assertNotNull(second.resolve(AtomicInteger.class));
    }

    @Test
    void localRegistrationOverridesBaseDefault() {
        LocalSerializers serializers = new LocalSerializers();
        Serializer<Duration> custom = new Serializer<>() {
            @Override
            public @NotNull Map<String, Object> serialize(@NotNull Duration object) {
                return Map.of("custom", object.toString());
            }

            @Override
            public @NotNull Duration deserialize(@NotNull Map<String, Object> map) {
                return Duration.parse((String) map.get("custom"));
            }
        };

        serializers.register(Duration.class, custom);
        assertSame(custom, serializers.resolve(Duration.class));
    }
}
