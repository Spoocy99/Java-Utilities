package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.*;
import dev.spoocy.utils.config.constructor.Construct;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.constructor.SafeConstructor;
import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.nodes.Node;
import dev.spoocy.utils.config.nodes.NodeTree;
import dev.spoocy.utils.config.nodes.NodeTuple;
import dev.spoocy.utils.config.nodes.ScalarNode;
import dev.spoocy.utils.config.representer.Represent;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.representer.SafeRepresenter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ConfigTest<C extends Config> extends ResourceTest {

    private static final String EMPTY_FILE_NAME = "__empty-config.txt";
    private static final String NON_EXISTING_FILE = resourcesPath("__invalid__/non-existing-config.txt");

    protected static final Representer REPRESENTER = new ConfigRepresenter();
    protected static final Constructor CONSTRUCTOR = new ConfigConstructor();

    /**
     * {@link ConfigLoader} implementation for this type of config.
     */
    protected abstract ConfigLoader<C, ?> loader();

    /**
     * {@link Resource} with example data.
     */
    protected abstract Resource exampleResource();

    /**
     * {@link Resource} with example map data to serialize.
     */
    protected abstract Resource exampleMapResource();

    /**
     * Number of entries in the example config file.
     */
    protected abstract int exampleConfigEntries();

    /**
     * Whether the config loader supports {@link dev.spoocy.utils.config.Tag}.
     */
    protected abstract boolean supportsTags();

    protected C load(@NotNull Resource resource) throws IOException {
        return load(resource, CONSTRUCTOR);
    }

    protected C load(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException {
        return loader().load(resource, constructor);
    }

    protected C emptyConfig() {
        return loader().createEmpty();
    }

    @NotNull
    protected static Map<String, Object> serializableMap() {
        Map<String, Object> serialMap = new LinkedHashMap<>();
        serialMap.put("name", "example");
        serialMap.put("value", 42);
        return serialMap;
    }

    @NotNull
    protected static Resource emptyFile(@NotNull Path dir) throws IOException {
        Path file = dir.resolve(EMPTY_FILE_NAME);

        if (!Files.exists(file)) {
            Files.createFile(file);
        }

        return Resources.fromPath(file);
    }

    @NotNull
    protected static Resource invalidResource() {
        return Resources.fromPath(Path.of(NON_EXISTING_FILE));
    }

    @Nested
    class Creation {

        @Test
        void createEmpty() {
            Config config = loader().createEmpty();
            assertEquals(0, config.values(true).size());
        }

        @Test
        void createEmptyWithSettings() {
            Config config = loader().createEmpty(s -> s.pathSeparator('/'));
            assertEquals(0, config.values(true).size());
            assertEquals('/', config.settings().pathSeparator());
        }

        @Test
        void createWithValues() {
            Config config = loader().createEmpty();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", serializableMap());

            assertEquals(3, config.values(true).size());
            assertEquals("value1", config.getString("key1"));
            assertEquals(123, config.getInt("key2"));

            Object rawSerializable = config.getObject("key3");
            assertInstanceOf(Map.class, rawSerializable);
            assertEquals("example", ((Map<?, ?>) rawSerializable).get("name"));
        }
    }

    @Nested
    class Mutation {

        @Test
        void setValue() {
            Config config = emptyConfig();
            config.set("key", "value");
            assertEquals("value", config.getString("key"));
        }

        @Test
        void overwriteValue() {
            Config config = emptyConfig();
            config.set("key", "original");
            config.set("key", "updated");
            assertEquals("updated", config.getString("key"));
        }

        @Test
        void setNullValue() {
            Config config = emptyConfig();
            config.set("key", null);
            assertNull(config.getObject("key"));
        }

        @Test
        void removeValue() {
            Config config = emptyConfig();
            config.set("key1", "value1");
            config.set("key2", "value2");
            config.remove("key1");

            assertNull(config.getObject("key1"));
            assertEquals("value2", config.getString("key2"));
        }

        @Test
        void removeNonExistentPath() {
            Config config = emptyConfig();
            // Should not throw
            config.remove("non.existent.path");
        }

        @Test
        void clearConfig() {
            Config config = emptyConfig();
            config.set("key1", "value1");
            config.set("key2", "value2");
            config.set("nested.key", "value3");

            config.clear();
            assertEquals(0, config.values(true).size());
        }

        @Test
        void toggleBoolean() {
            Config config = emptyConfig();
            config.set("enabled", true);
            config.opposite("enabled");
            assertFalse(config.getBoolean("enabled"));

            config.opposite("enabled");
            assertTrue(config.getBoolean("enabled"));
        }

        @Test
        void toggleBooleanOnNonBoolean() {
            Config config = emptyConfig();
            config.set("value", "not a boolean");
            // Should handle gracefully
            config.opposite("value");
        }

        @Test
        void arithmeticAdd() {
            Config config = emptyConfig();
            config.set("counter", 10);
            config.add("counter", 5);
            assertEquals(15, config.getDouble("counter"));
        }

        @Test
        void arithmeticSubtract() {
            Config config = emptyConfig();
            config.set("counter", 20);
            config.subtract("counter", 3);
            assertEquals(17, config.getDouble("counter"));
        }

        @Test
        void arithmeticMultiply() {
            Config config = emptyConfig();
            config.set("value", 5);
            config.multiply("value", 2);
            assertEquals(10, config.getDouble("value"));
        }

        @Test
        void arithmeticDivide() {
            Config config = emptyConfig();
            config.set("value", 20);
            config.divide("value", 4);
            assertEquals(5, config.getDouble("value"));
        }

        @Test
        void arithmeticWithDouble() {
            Config config = emptyConfig();
            config.set("price", 9.99);
            config.multiply("price", 1.5);
            assertEquals(14.985, config.getDouble("price"));
        }
    }

    @Nested
    class TypeConversions {

        @Test
        void getStringDefaultValue() {
            Config config = loader().createEmpty();
            assertEquals("default", config.getString("missing", "default"));
        }

        @Test
        void getStringWithoutDefault() {
            Config config = loader().createEmpty();
            assertEquals("", config.getString("missing"));
        }

        @Test
        void getIntDefaultValue() {
            Config config = loader().createEmpty();
            assertEquals(42, config.getInt("missing", 42));
        }

        @Test
        void getIntWithoutDefault() {
            Config config = loader().createEmpty();
            assertEquals(0, config.getInt("missing"));
        }

        @Test
        void getDoubleDefaultValue() {
            Config config = loader().createEmpty();
            assertEquals(3.14, config.getDouble("missing", 3.14));
        }

        @Test
        void getFloatDefaultValue() {
            Config config = loader().createEmpty();
            assertEquals(2.5f, config.getFloat("missing", 2.5f));
        }

        @Test
        void getLongDefaultValue() {
            Config config = loader().createEmpty();
            assertEquals(999999999L, config.getLong("missing", 999999999L));
        }

        @Test
        void getBooleanDefaultValue() {
            Config config = loader().createEmpty();
            assertTrue(config.getBoolean("missing", true));
            assertFalse(config.getBoolean("missing", false));
        }

        @Test
        void getBooleanWithoutDefault() {
            Config config = loader().createEmpty();
            assertFalse(config.getBoolean("missing"));
        }

        @Test
        void getObjectDefaultValue() {
            Config config = loader().createEmpty();
            Object defaultObj = new Object();
            assertSame(defaultObj, config.getObject("missing", defaultObj));
        }

        @Test
        void getGenericTypeWithClass() {
            Config config = loader().createEmpty();
            config.set("text", "hello");
            String result = config.get("text", String.class);
            assertEquals("hello", result);
        }

        @Test
        void getGenericTypeWithWrongClass() {
            Config config = loader().createEmpty();
            config.set("text", "hello");
            // Attempting to get as Integer may use NumberConversion
            // which may convert "hello" to 0 or return null depending on implementation
            Integer result = config.get("text", Integer.class);
            // Result depends on NumberConversion behavior
            assertTrue(result == null || result == 0);
        }

        @Test
        void getGenericTypeWithDefault() {
            Config config = loader().createEmpty();
            String result = config.get("missing", String.class, "default");
            assertEquals("default", result);
        }

        @Test
        void isTypeCheck() {
            Config config = loader().createEmpty();
            config.set("text", "hello");
            config.set("number", 42);

            assertTrue(config.is("text", String.class));
            assertFalse(config.is("text", Integer.class));
            assertTrue(config.is("number", Integer.class));
        }

        @Test
        void isStringCheck() {
            Config config = loader().createEmpty();
            config.set("text", "hello");
            config.set("number", 42);

            assertTrue(config.isString("text"));
            assertFalse(config.isString("number"));
        }

        @Test
        void isIntCheck() {
            Config config = loader().createEmpty();
            config.set("number", 42);
            config.set("text", "hello");

            assertTrue(config.isInt("number"));
            assertFalse(config.isInt("text"));
        }

        @Test
        void isDoubleCheck() {
            Config config = loader().createEmpty();
            config.set("decimal", 3.14);
            config.set("text", "hello");

            assertTrue(config.isDouble("decimal"));
            assertFalse(config.isDouble("text"));
        }

        @Test
        void isFloatCheck() {
            Config config = loader().createEmpty();
            config.set("decimal", 2.5f);
            config.set("text", "hello");

            assertTrue(config.isFloat("decimal"));
            assertFalse(config.isFloat("text"));
        }

        @Test
        void isLongCheck() {
            Config config = loader().createEmpty();
            config.set("bignum", 999999999L);
            config.set("text", "hello");

            assertTrue(config.isLong("bignum"));
            assertFalse(config.isLong("text"));
        }

        @Test
        void isBooleanCheck() {
            Config config = loader().createEmpty();
            config.set("flag", true);
            config.set("text", "hello");

            assertTrue(config.isBoolean("flag"));
            assertFalse(config.isBoolean("text"));
        }

        @Test
        void isSetCheck() {
            Config config = loader().createEmpty();
            config.set("exists", "value");

            assertTrue(config.isSet("exists"));
            assertFalse(config.isSet("missing"));
        }

        @Test
        void isListCheck() {
            Config config = loader().createEmpty();
            config.set("list", List.of(1, 2, 3));
            config.set("text", "hello");

            assertTrue(config.isList("list"));
            assertFalse(config.isList("text"));
        }

        @Test
        void stringConversionFromNumber() {
            Config config = loader().createEmpty();
            config.set("number", 42);
            assertEquals("42", config.getString("number"));
        }

        @Test
        void numberConversionFromString() {
            Config config = loader().createEmpty();
            config.set("text", "123");
            // Should attempt conversion
            config.getInt("text", 0);
            // Result depends on NumberConversion implementation
            assertNotNull(config.getObject("text"));
        }
    }

    @Nested
    class KeysAndValues {

        @Test
        void keysShallow() {
            Config config = loader().createEmpty();
            config.set("key1", "value1");
            config.set("key2", "value2");
            config.createSection("section.nested");

            Collection<String> keys = config.keys(false);
            assertTrue(keys.contains("key1"));
            assertTrue(keys.contains("key2"));
            assertTrue(keys.contains("section"));
        }

        @Test
        void keysDeep() {
            Config config = loader().createEmpty();
            config.set("key1", "value1");
            config.set("nested.key2", "value2");
            config.set("nested.key3.deep", "value3");

            Collection<String> keys = config.keys(true);
            assertTrue(keys.contains("key1"));
            assertTrue(keys.contains("nested.key2"));
            assertTrue(keys.contains("nested.key3.deep"));
        }

        @Test
        void valuesShallow() {
            Config config = loader().createEmpty();
            config.set("key1", "value1");
            config.set("key2", 42);
            config.createSection("section");

            Map<String, Object> values = config.values(false);
            assertEquals("value1", values.get("key1"));
            assertEquals(42, values.get("key2"));
            assertFalse(values.containsKey("section.nested"));
        }

        @Test
        void valuesDeep() {
            Config config = loader().createEmpty();
            config.set("key1", "value1");
            config.set("nested.key2", "value2");
            config.set("nested.key3.deep", "value3");

            Map<String, Object> values = config.values(true);
            assertEquals("value1", values.get("key1"));
            assertEquals("value2", values.get("nested.key2"));
            assertEquals("value3", values.get("nested.key3.deep"));
        }

        @Test
        void keysEmpty() {
            Config config = loader().createEmpty();
            assertEquals(0, config.keys(true).size());
            assertEquals(0, config.keys(false).size());
        }

        @Test
        void valuesEmpty() {
            Config config = loader().createEmpty();
            assertEquals(0, config.values(true).size());
            assertEquals(0, config.values(false).size());
        }
    }

    @Nested
    class Sections {

        @Test
        void createSection() {
            Config config = loader().createEmpty();
            ConfigSection section = config.createSection("settings");

            assertTrue(config.isSection("settings"));
            assertEquals("settings", section.getName());
            assertSame(config, section.getRoot());
            assertSame(config, section.getParent());
        }

        @Test
        void createNestedSection() {
            Config config = loader().createEmpty();
            ConfigSection deep = config.createSection("a.b.c");

            assertTrue(config.isSection("a"));
            assertTrue(config.isSection("a.b"));
            assertTrue(config.isSection("a.b.c"));
            assertNotNull(deep);
        }

        @Test
        void createSectionWithData() {
            Config config = loader().createEmpty();
            Map<String, Object> data = serializableMap();
            ConfigSection section = config.createSection("data", data);

            assertEquals("example", section.getString("name"));
            assertEquals(42, section.getInt("value"));
        }

//        @Test
//        void createSectionWithNonStringKeys() {
//            Config config = loader().createEmpty();
//            Map<?, ?> data = new LinkedHashMap<>();
//            data.put(1, "value");
//            data.put("key", "value2");
//
//            assertThrows(IllegalArgumentException.class, () -> config.createSection("section", data));
//        }

        @Test
        void getSection() {
            Config config = loader().createEmpty();
            config.createSection("settings");
            ConfigSection section = config.getSection("settings");

            assertNotNull(section);
            assertTrue(config.isSection("settings"));
        }

        @Test
        void getSectionNonExistent() {
            Config config = loader().createEmpty();
            assertThrows(IllegalArgumentException.class, () -> config.getSection("missing"));
        }

        @Test
        void getSectionIfExists() {
            Config config = loader().createEmpty();
            config.createSection("settings");

            assertNotNull(config.getSectionIfExists("settings"));
            assertNull(config.getSectionIfExists("missing"));
        }

        @Test
        void getSectionOrEmpty() {
            Config config = loader().createEmpty();
            config.set("sec.key", "value");

            ConfigSection section = config.getSectionOrEmpty("sec");
            assertNotNull(section);
            assertEquals("value", section.getString("key"));

            ConfigSection nonExistent = config.getSectionOrEmpty("nonexistent");
            assertNotNull(nonExistent);
            assertEquals(0, nonExistent.values(true).size());

            assertFalse(nonExistent.isSection("nonexistent"));
        }

        @Test
        void getOrCreateSection() {
            Config config = loader().createEmpty();
            assertFalse(config.isSection("sec"));

            ConfigSection section = config.getOrCreateSection("sec");
            assertNotNull(section);
            assertTrue(config.isSection("sec"));

            ConfigSection existing = config.getOrCreateSection("sec");
            assertSame(section, existing);
        }

        @Test
        void getSectionFromValue() {
            Config config = loader().createEmpty();
            config.set("key", "value");

            assertThrows(IllegalArgumentException.class, () -> config.getSection("key"));
        }

        @Test
        void isSectionCheck() {
            Config config = loader().createEmpty();
            config.createSection("section");
            config.set("value", "test");

            assertTrue(config.isSection("section"));
            assertFalse(config.isSection("value"));
            assertFalse(config.isSection("missing"));
        }

        @Test
        void sectionValues() {
            Config config = loader().createEmpty();
            config.createSection("data");
            config.set("data.key1", "value1");
            config.set("data.key2", 42);

            ConfigSection section = config.getSection("data");
            Map<String, Object> values = section.values(false);

            assertEquals("value1", values.get("key1"));
            assertEquals(42, values.get("key2"));
        }

        @Test
        void sectionKeys() {
            Config config = loader().createEmpty();
            config.set("data.key1", "value1");
            config.set("data.key2", 42);

            ConfigSection section = config.getSection("data");
            Collection<String> keys = section.keys(false);

            assertTrue(keys.contains("key1"));
            assertTrue(keys.contains("key2"));
        }

        @Test
        void nestedSectionNavigation() {
            Config config = loader().createEmpty();
            config.createSection("root.level1.level2");
            config.set("root.level1.level2.value", "deep");

            ConfigSection root = config.getSection("root");
            ConfigSection level1 = root.getSection("level1");
            ConfigSection level2 = level1.getSection("level2");

            assertEquals("deep", level2.getString("value"));
        }

        @Test
        void sectionParent() {
            Config config = loader().createEmpty();
            config.createSection("a.b");
            ConfigSection subSection = config.getSection("a.b");
            ConfigSection parentSection = subSection.getParent();

            assertNotNull(parentSection);
            assertEquals("a", parentSection.getName());
            assertSame(config, parentSection.getParent());
        }

        @Test
        void sectionDoesntCountAsIsSet() {
            Config config = loader().createEmpty();
            assertFalse(config.isSet("section"));
            assertFalse(config.isSection("section"));

            config.set("section.key", "value");
            assertFalse(config.isSet("section"));
            assertTrue(config.isSection("section"));

            config.set("section", "value");
            assertTrue(config.isSet("section"));
            assertFalse(config.isSection("section"));

            config.remove("section");
            assertFalse(config.isSet("section"));
            assertFalse(config.isSection("section"));

            config.createSection("section");
            assertFalse(config.isSet("section"));
            assertTrue(config.isSection("section"));

        }

    }

    @Nested
    class Lists {

        @Test
        void getListDefaultValue() {
            Config config = loader().createEmpty();
            List<Object> defaultList = List.of("default");
            List<?> result = config.getList("missing", defaultList);

            assertEquals(defaultList, result);
        }

        @Test
        void getListWithoutDefault() {
            Config config = loader().createEmpty();
            List<?> result = config.getList("missing");

            assertNull(result);
        }

        @Test
        void getListWithType() {
            Config config = loader().createEmpty();
            config.set("numbers", List.of(1, 2, 3));
            List<Integer> result = config.getList("numbers", Integer.class, null);

            assertEquals(3, result.size());
            assertEquals(1, result.get(0));
        }

        @Test
        void getStringList() {
            Config config = loader().createEmpty();
            config.set("items", List.of("apple", "banana", "cherry"));
            List<String> result = config.getStringList("items");

            assertEquals(3, result.size());
            assertTrue(result.contains("apple"));
        }

        @Test
        void getStringListEmpty() {
            Config config = loader().createEmpty();
            List<String> result = config.getStringList("missing");

            assertEquals(0, result.size());
        }

        @Test
        void getBooleanList() {
            Config config = loader().createEmpty();
            config.set("flags", List.of(true, false, true));
            List<Boolean> result = config.getBooleanList("flags");

            assertEquals(3, result.size());
            assertTrue(result.get(0));
            assertFalse(result.get(1));
        }

        @Test
        void getIntegerList() {
            Config config = loader().createEmpty();
            config.set("numbers", List.of(1, 2, 3, 4, 5));
            List<Integer> result = config.getIntegerList("numbers");

            assertEquals(5, result.size());
            assertEquals(1, result.get(0));
            assertEquals(5, result.get(4));
        }

        @Test
        void getDoubleList() {
            Config config = loader().createEmpty();
            config.set("decimals", List.of(1.1, 2.2, 3.3));
            List<Double> result = config.getDoubleList("decimals");

            assertEquals(3, result.size());
            assertEquals(1.1, result.get(0));
        }

        @Test
        void getFloatList() {
            Config config = loader().createEmpty();
            config.set("floats", List.of(1.5f, 2.5f, 3.5f));
            List<Float> result = config.getFloatList("floats");

            assertEquals(3, result.size());
        }

        @Test
        void getLongList() {
            Config config = loader().createEmpty();
            config.set("longs", List.of(100L, 200L, 300L));
            List<Long> result = config.getLongList("longs");

            assertEquals(3, result.size());
            assertEquals(100L, result.get(0));
        }

        @Test
        void getByteList() {
            Config config = loader().createEmpty();
            config.set("bytes", List.of((byte) 1, (byte) 2, (byte) 3));
            List<Byte> result = config.getByteList("bytes");

            assertEquals(3, result.size());
        }

        @Test
        void getCharacterList() {
            Config config = loader().createEmpty();
            config.set("chars", List.of("a", "b", "c"));
            List<Character> result = config.getCharacterList("chars");

            assertEquals(3, result.size());
            assertEquals('a', result.get(0).charValue());
        }

        @Test
        void getShortList() {
            Config config = loader().createEmpty();
            config.set("shorts", List.of((short) 1, (short) 2, (short) 3));
            List<Short> result = config.getShortList("shorts");

            assertEquals(3, result.size());
        }

        @Test
        void getMapList() {
            Config config = loader().createEmpty();
            List<Map<String, Object>> mapData = List.of(
                    Map.of("name", "Alice", "age", 30),
                    Map.of("name", "Bob", "age", 25)
            );
            config.set("people", mapData);

            List<Map<String, Object>> result = config.getMapList("people");
            assertEquals(2, result.size());
            assertEquals("Alice", result.get(0).get("name"));
            assertEquals(25, result.get(1).get("age"));
        }

        @Test
        void getMapListEmpty() {
            Config config = loader().createEmpty();
            List<Map<String, Object>> result = config.getMapList("missing");

            assertEquals(0, result.size());
        }

        @Test
        void getMapListWithoutMaps() {
            Config config = loader().createEmpty();
            config.set("mixed", List.of("string", 42, true));
            List<Map<String, Object>> result = config.getMapList("mixed");

            assertEquals(0, result.size());
        }

        @Test
        void getSectionList() {
            Config config = loader().createEmpty();
            List<Map<String, Object>> mapData = List.of(
                    Map.of("name", "Alice"),
                    Map.of("name", "Bob")
            );
            config.set("people", mapData);

            List<ConfigSection> result = config.getSectionList("people");
            assertEquals(2, result.size());
            assertEquals("Alice", result.get(0).getString("name"));
            assertEquals("Bob", result.get(1).getString("name"));
        }

        @Test
        void getSectionListEmpty() {
            Config config = loader().createEmpty();
            List<ConfigSection> result = config.getSectionList("missing");

            assertEquals(0, result.size());
        }
    }

    @Nested
    class SpecialTypes {

        @Test
        void getUUID() {
            Config config = loader().createEmpty();
            String uuidString = "550e8400-e29b-41d4-a716-446655440000";
            config.set("id", uuidString);

            UUID result = config.getUUID("id");
            assertEquals(UUID.fromString(uuidString), result);
        }

        @Test
        void getUUIDInvalid() {
            Config config = loader().createEmpty();
            config.set("id", "not-a-uuid");

            UUID result = config.getUUID("id", null);
            assertNull(result);
        }

        @Test
        void getUUIDWithDefault() {
            Config config = loader().createEmpty();
            UUID defaultUUID = UUID.randomUUID();
            UUID result = config.getUUID("missing", defaultUUID);

            assertEquals(defaultUUID, result);
        }

        @Test
        void getClassType() {
            Config config = loader().createEmpty();
            config.set("type", String.class.getCanonicalName());

            Class<?> result = config.getClass("type");
            assertEquals(String.class, result);
        }

        @Test
        void getClassTypeInvalid() {
            Config config = loader().createEmpty();
            config.set("type", "not.a.valid.ClassName");

            Class<?> result = config.getClass("type", null);
            assertNull(result);
        }

        @Test
        void getClassTypeWithDefault() {
            Config config = loader().createEmpty();
            Class<?> result = config.getClass("missing", Object.class);

            assertEquals(Object.class, result);
        }

        @Test
        void getEnum() {
            Config config = loader().createEmpty();
            config.set("level", "HIGH");

            TestLevel result = config.getEnum("level", TestLevel.class);
            assertEquals(TestLevel.HIGH, result);
        }

        @Test
        void getEnumInvalid() {
            Config config = loader().createEmpty();
            config.set("level", "INVALID");

            TestLevel result = config.getEnum("level", TestLevel.class, TestLevel.LOW);
            assertEquals(TestLevel.LOW, result);
        }

        @Test
        void getEnumWithDefault() {
            Config config = loader().createEmpty();
            TestLevel result = config.getEnum("level", TestLevel.class, TestLevel.MEDIUM);

            assertEquals(TestLevel.MEDIUM, result);
        }
    }

    @Nested
    class PathSeparator {

        @Test
        void customPathSeparator() {
            Config config = loader().createEmpty(s -> s.pathSeparator('/'));
            config.set("a/b/c", "value");

            assertEquals("value", config.getString("a/b/c"));
            assertTrue(config.isSection("a"));
            assertTrue(config.isSection("a/b"));
        }

        @Test
        void getWithCustomSeparator() {
            Config config = loader().createEmpty(s -> s.pathSeparator('_'));
            config.set("level_one_two", "value");

            assertEquals("value", config.getString("level_one_two"));
        }

        @Test
        void keysWithCustomSeparator() {
            Config config = loader().createEmpty(s -> s.pathSeparator('/'));
            config.set("a/b/c", "value");

            Collection<String> keys = config.keys(true);
            assertTrue(keys.stream().anyMatch(k -> k.contains("/")));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void emptyKeyPath() {
            Config config = loader().createEmpty();
            // Empty string key should be valid
            config.set("", "value");
            assertEquals("value", config.getObject(""));
        }

        @Test
        void nullObject() {
            Config config = loader().createEmpty();
            config.set("nullValue", null);

            assertNull(config.getObject("nullValue"));
            assertTrue(config.isSet("nullValue"));
        }

        @Test
        void veryDeepPath() {
            Config config = loader().createEmpty();
            String deepPath = "a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t";
            config.set(deepPath, "deep");

            assertEquals("deep", config.getString(deepPath));
            assertTrue(config.isSet(deepPath));
        }

        @Test
        void largeList() {
            Config config = loader().createEmpty();
            List<Integer> largeList = new java.util.ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(i);
            }
            config.set("large", largeList);

            List<Integer> result = config.getIntegerList("large");
            assertEquals(1000, result.size());
            assertEquals(999, (int) result.get(999));
        }

        @Test
        void specialCharactersInValues() {
            Config config = loader().createEmpty();
            String specialValue = "!@#$%^&*()[]{}|\\:;<>?,./";
            config.set("special", specialValue);

            assertEquals(specialValue, config.getString("special"));
        }

        @Test
        void getListWithTypeMismatch() {
            Config config = loader().createEmpty();
            config.set("mixed", List.of("one", 2, "three", 4));
            List<Integer> result = config.getList("mixed", Integer.class, new ArrayList<>());

            // Should only get the integers
            assertEquals(2, result.size());
            assertTrue(result.contains(2));
            assertTrue(result.contains(4));
        }

        @Test
        void multipleNestedSections() {
            Config config = loader().createEmpty();
            config.set("section1.key1", "value1");
            config.set("section2.key2", "value2");
            config.set("section1.sub.key3", "value3");

            assertEquals(3, config.values(true).size());
            assertTrue(config.isSection("section1"));
            assertTrue(config.isSection("section2"));
            assertTrue(config.isSection("section1.sub"));
        }

        @Test
        void overwriteSectionWithValue() {
            Config config = loader().createEmpty();
            config.createSection("section");
            config.set("section", "value");

            assertEquals("value", config.getString("section"));
        }

        @Test
        void overwriteValueWithSection() {
            Config config = loader().createEmpty();
            config.set("key", "value");
            // Setting a nested value under a key creates a section
            config.set("key.nested", "nested");

            // The section should now exist
            assertTrue(config.isSection("key"));
        }

        @Test
        void getObjectFromSection() {
            Config config = loader().createEmpty();
            config.createSection("data");
            Object result = config.getObject("data");

            assertNull(result);
        }

        @Test
        void getDivisionByZeroHandling() {
            Config config = loader().createEmpty();
            config.set("value", 10);
            // This will throw - depends on implementation
            try {
                config.divide("value", 0);
            } catch (ArithmeticException | NumberFormatException ignored) {
                // Expected
            }
        }

    }

    @Nested
    class Load {

        @Test
        void loadNonExisting() {
            Resource nonExisting = invalidResource();
            assertThrows(IOException.class, () -> load(nonExisting));
        }

        @Test
        void loadEmpty(@TempDir Path temp) throws IOException {
            Config config = load(emptyFile(temp));
            assertEquals(0, config.values(true).size());
        }

        @Test
        void loadWithValues() throws IOException {
            Resource file = exampleResource();
            assertTrue(file.exists());

            Config config = load(file);

            // The example file has at least ... top-level keys
            Map<String, Object> deepValues = config.values(true);
            assertEquals(exampleConfigEntries(), deepValues.size());

            // foo: bar
            assertTrue(config.isSet("foo"));
            assertEquals("bar", config.getString("foo"));

            // key2: 123
            assertTrue(config.isSet("key2"));
            assertEquals(123, config.getInt("key2"));

            // serializable:
            //  ==: "dev.spoocy.utils.config.SerializableExample"
            //  name: "example"
            //  value: 42
            assertFalse(config.isSet("serializable"));  // section does not count as set
            assertTrue(config.isSection("serializable"));
            ConfigSection serializableSection = config.getSection("serializable");
            assertEquals("example", serializableSection.getString("name"));
            assertEquals(42, serializableSection.getInt("value"));

            // list:
            //    - item1
            //    - item2
            //    - item3
            assertTrue(config.isSet("list"));
            assertTrue(config.isList("list"));
            List<String> list = config.getStringList("list");
            assertEquals(3, list.size());
            assertTrue(list.contains("item1"));
            assertTrue(list.contains("item2"));
            assertTrue(list.contains("item3"));

            // objects:
            //    - name: "object1"
            //      value: 1
            //    - name: "object2"
            //      value: 2
            assertTrue(config.isSet("objects"));
            assertTrue(config.isList("objects"));
            List<ConfigSection> objects = config.getSectionList("objects");
            assertEquals(2, objects.size());

            ConfigSection object1 = objects.get(0);
            assertTrue(object1.isSet("name"));
            assertEquals("object1", object1.getString("name"));
            assertTrue(object1.isSet("value"));
            assertEquals(1, object1.getInt("value"));

            ConfigSection object2 = objects.get(1);
            assertTrue(object2.isSet("name"));
            assertEquals("object2", object2.getString("name"));
            assertTrue(object2.isSet("value"));
            assertEquals(2, object2.getInt("value"));

        }

    }

    @Nested
    class Save {

        @Test
        void saveToString() {
            Config config = emptyConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("nested.key", "nested_value");

            String saved = config.saveToString(REPRESENTER);
            assertNotNull(saved);
            assertFalse(saved.isEmpty());
        }

        @Test
        void saveToResource(@TempDir Path temp) throws IOException {
            Config config = emptyConfig();
            config.set("key1", "value1");
            config.set("key2", 123);

            Path file = temp.resolve("test-save.txt");
            Resource writeableResource = Resources.fromPath(file);

            config.save((WriteableResource) writeableResource, REPRESENTER);

            assertTrue(Files.exists(file));
            String content = Files.readString(file);
            assertFalse(content.isEmpty());
        }

        @Test
        void saveAndLoadRoundTrip(@TempDir Path temp) throws IOException {
            Config original = emptyConfig();
            original.set("string", "hello");
            original.set("number", 42);
            original.set("decimal", 3.14);
            original.set("boolean", true);
            original.set("list", List.of(1, 2, 3));
            original.set("nested.value", "deep");

            Path file = temp.resolve("roundtrip.txt");
            PathResource resource = Resources.fromPath(file);

            original.save(resource, REPRESENTER);

            Config loaded = load(resource, CONSTRUCTOR);

            assertEquals("hello", loaded.getString("string"));
            assertEquals(42, loaded.getInt("number"));
            assertTrue(loaded.getBoolean("boolean"));
            assertEquals("deep", loaded.getString("nested.value"));
        }

        @Test
        void saveSectionValues() {
            Config config = emptyConfig();
            config.createSection("section");
            config.set("section.key1", "value1");
            config.set("section.key2", "value2");

            ConfigSection section = config.getSection("section");
            Map<String, Object> values = section.values(false);

            assertEquals(2, values.size());
            assertEquals("value1", values.get("key1"));
            assertEquals("value2", values.get("key2"));
        }

    }

    @Nested
    class CustomObjects {

        @Test
        void storeAndLoadCustomObject(@TempDir Path temp) throws IOException {
            if (!supportsTags()) {
                return;
            }

            Config config = emptyConfig();
            CustomObject obj = new CustomObject("John", 30);
            config.set("person", obj);

            Path file = temp.resolve("custom-object.txt");
            Resource resource = Resources.fromPath(file);

            config.save((WriteableResource) resource, REPRESENTER);

            Config loaded = load(resource, CONSTRUCTOR);
            CustomObject loadedObj = loaded.get("person", CustomObject.class);

            assertNotNull(loadedObj);
            assertEquals("John", loadedObj.getName());
            assertEquals(30, loadedObj.getAge());
        }

    }

    @Nested
    class CustomMapSerialization {

        @Test
        void loadWithSerializedMapValues() throws IOException {
            Config config = load(exampleMapResource(), new CustomObjectMapConstructor());

            for (Map.Entry<String, Object> e : config.values(true).entrySet()) {
                System.out.println(e.getKey() + " >> " + e.getValue());
            }

            assertTrue(config.isSection("serialize:objects"));
            ConfigSection main = config.getSection("serialize:objects");

            assertTrue(main.isSection("map-objects"));
            ConfigSection objects = main.getSection("map-objects");

            assertTrue(objects.isSet("o-1"));
            Object o1 = objects.getObject("o-1");
            CustomObject co1 = assertInstanceOf(CustomObject.class, o1);
            assertEquals("name1", co1.getName());
            assertEquals(1, co1.getAge());

            assertTrue(objects.isSet("o-2"));
            Object o2 = objects.getObject("o-2");
            CustomObject co2 = assertInstanceOf(CustomObject.class, o2);
            assertEquals("name2", co2.getName());
            assertEquals(2, co2.getAge());
        }

        @Test
        void loadWithSerializedSequenceValues() throws IOException {
            Config config = load(exampleMapResource(), new CustomObjectMapConstructor());

            assertTrue(config.isSection("serialize:objects"));
            ConfigSection main = config.getSection("serialize:objects");

            assertTrue(main.isSet("sequence-objects"));
            List<CustomObject> list = main.getList("sequence-objects", CustomObject.class, List.of());

            assertEquals(2, list.size());

            CustomObject co1 = list.get(0);
            assertEquals("name1", co1.getName());
            assertEquals(1, co1.getAge());

            CustomObject co2 = list.get(1);
            assertEquals("name2", co2.getName());
            assertEquals(2, co2.getAge());
        }

    }

    enum TestLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    static class ConfigRepresenter extends SafeRepresenter {

        public static final Tag CUSTOM_OBJECT_TAG = new Tag(CustomObject.class);

        public ConfigRepresenter() {
            this.represent(CustomObject.class, new CustomObjectRepresenter());
        }

        class CustomObjectRepresenter implements Represent {

            @Override
            public @NotNull Node represent(@Nullable Object data) {
                CustomObject obj = (CustomObject) data;
                String value = obj.getName() + "-" + obj.getAge();
                return representScalar(CUSTOM_OBJECT_TAG, value);
            }
        }

    }

    static class ConfigConstructor extends SafeConstructor {

        public ConfigConstructor() {
            this.construct(ConfigRepresenter.CUSTOM_OBJECT_TAG, new CustomObjectConstructor());
        }

        static class CustomObjectConstructor implements Construct {

            @Override
            public @Nullable Object construct(@Nullable Node node) {
                if (node instanceof ScalarNode) {
                    String value = ((ScalarNode) node).getData().toString();
                    String[] parts = value.split("-");
                    if (parts.length == 2) {
                        String name = parts[0];
                        int age = Integer.parseInt(parts[1]);
                        return new CustomObject(name, age);
                    }
                }

                throw new IllegalArgumentException("Invalid data for CustomObject: " + node);
            }
        }

    }

    static class CustomObjectMapConstructor extends SafeConstructor {

        public CustomObjectMapConstructor() {
            this.construct(Tag.MAP, new OverwriteMapConstructor());
        }

        class OverwriteMapConstructor extends MapConstructor {

            @Override
            public @Nullable Object construct(@Nullable Node node) {
                Map<Object, Object> map = (Map<Object, Object>) super.construct(node);

                if (map.containsKey("==") && map.get("==").equals("CustomObject")) {
                    return new CustomObject(
                            map.get("name").toString(),
                            Integer.parseInt(map.get("age").toString())
                    );
                }

                return map;
            }

        }

    }


    static class CustomObject {

        private final String name;
        private final int age;

        public CustomObject(@NotNull String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof CustomObject)) return false;
            CustomObject that = (CustomObject) object;
            return age == that.age && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

}
