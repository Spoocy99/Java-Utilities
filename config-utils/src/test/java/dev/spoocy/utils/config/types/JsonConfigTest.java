package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.ResourceTest;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import dev.spoocy.utils.config.ConfigSection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonConfigTest extends ResourceTest {

    private static final String EXAMPLE_FILE = resourcesPath("types/example.json");

    @BeforeAll
    static void setupSerializers() {
        String name = NamedSerializers.getSerializeName(SerializableExample.class);
        if (NamedSerializers.DEFAULT_INSTANCE.getSerializableClassByName(name) == null) {
            NamedSerializers.DEFAULT_INSTANCE.register(SerializableExample.class);
        }
    }

    @Nested
    class Creation {

        @Test
        void createEmpty() {
            JsonConfig config = new JsonConfig();
            assertEquals(0, config.values(true).size());
        }

        @Test
        void createWithValues() {
            JsonConfig config = new JsonConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", SerializableExample.EXAMPLE);

            assertEquals(3, config.values(true).size());
            assertEquals("value1", config.getString("key1"));
            assertEquals(123, config.getInt("key2"));
            assertEquals(SerializableExample.EXAMPLE, config.get("key3", SerializableExample.class));
        }

    }

    @Nested
    class Load {

        @Test
        void loadEmpty(@TempDir Path temporaryFolder) throws IOException {
            Path filePath = temporaryFolder.resolve("empty.json");
            Files.createFile(filePath);

            Resource resource = new PathResource(filePath);
            JsonConfig config = (JsonConfig) loadDefault(resource);

            assertEquals(0, config.values(true).size());
        }

        @Test
        void loadWithValues() throws IOException {
            Resource file = Resources.fromFile(EXAMPLE_FILE);
            JsonConfig config = (JsonConfig) loadDefault(file);

            assertEquals(3, config.values(true).size());
            assertEquals("bar", config.getString("foo"));
            assertEquals(123, config.getInt("key2"));

            SerializableExample expected = config.get("serializable", SerializableExample.class);
            assertEquals("example", expected.getName());
            assertEquals(42, expected.getValue());
        }

        @Test
        void loadObjectNodeAsSection(@TempDir Path temporaryFolder) throws IOException {
            Path filePath = temporaryFolder.resolve("object-node.json");
            Files.writeString(filePath, "{\n  \"database\": {\n    \"host\": \"localhost\"\n  }\n}");

            JsonConfig config = (JsonConfig) loadDefault(new PathResource(filePath));

            assertTrue(config.isSection("database"));
            assertInstanceOf(ConfigSection.class, config.getObject("database"));
            assertEquals("localhost", config.getString("database.host"));
        }

        @Test
        void loadArrayObjectNodesAsLinkedHashMap(@TempDir Path temporaryFolder) throws IOException {
            Path filePath = temporaryFolder.resolve("array-node.json");
            Files.writeString(filePath, "{\n  \"servers\": [\n    { \"name\": \"alpha\" },\n    { \"name\": \"beta\" }\n  ]\n}");

            JsonConfig config = (JsonConfig) loadDefault(new PathResource(filePath));

            List<?> rawList = config.getList("servers");
            assertNotNull(rawList);
            assertEquals(2, rawList.size());
            assertInstanceOf(LinkedHashMap.class, rawList.get(0));

            // Section conversion happens in getSectionList, not during JSON array parsing.
            List<ConfigSection> sectionList = config.getSectionList("servers");
            assertEquals(2, sectionList.size());
            assertEquals("alpha", sectionList.get(0).getString("name"));
            assertEquals("beta", sectionList.get(1).getString("name"));
        }

        @Test
        void loadNestedObjectInsideArrayAsSectionInSectionList(@TempDir Path temporaryFolder) throws IOException {
            Path filePath = temporaryFolder.resolve("formats.json");
            Files.writeString(filePath,
                    "{\n" +
                            "  \"formats\": [\n" +
                            "    {\n" +
                            "      \"name\": \"digital\",\n" +
                            "      \"units\": {\n" +
                            "        \"seconds\": {\n" +
                            "          \"text\": \"<value>\",\n" +
                            "          \"display-when-zero\": true\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}");

            JsonConfig config = (JsonConfig) loadDefault(new PathResource(filePath));

            List<ConfigSection> formats = config.getSectionList("formats");
            assertEquals(1, formats.size());
            assertTrue(formats.get(0).isSection("units"));
            assertEquals("<value>", formats.get(0).getString("units.seconds.text"));
            assertTrue(formats.get(0).getBoolean("units.seconds.display-when-zero"));
        }

    }

    @Nested
    class Save {

        @Test
        void saveToString() {
            JsonConfig config = new JsonConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", SerializableExample.EXAMPLE);

            String json = config.saveToString();
            assertNotNull(json);
            assertTrue(json.contains("\"key1\": \"value1\""));
            assertTrue(json.contains("\"key2\": 123"));
            assertTrue(json.contains("\"==\":"));
            assertTrue(json.contains("\"name\": \"example\""));
            assertTrue(json.contains("\"value\": 42"));
        }

        @Test
        void write(@TempDir Path temporaryFolder) throws IOException {
            JsonConfig config = new JsonConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", SerializableExample.EXAMPLE);

            Path filePath = temporaryFolder.resolve("config.json");
            PathResource resource = new PathResource(filePath);
            config.save(resource);

            JsonConfig loadedConfig = (JsonConfig) loadDefault(resource);
            assertEquals(3, loadedConfig.values(true).size());
            assertEquals("value1", loadedConfig.getString("key1"));
            assertEquals(123, loadedConfig.getInt("key2"));

            SerializableExample expected = loadedConfig.get("key3", SerializableExample.class);
            assertEquals("example", expected.getName());
            assertEquals(42, expected.getValue());

        }

    }

    @Nested
    class Advanced {

        @Test
        void nestedSectionsAndKeys() {
            JsonConfig config = new JsonConfig();
            config.set("parent.child.key", "value");

            assertTrue(config.isSet("parent.child.key"));
            assertEquals("value", config.getString("parent.child.key"));

            ConfigSection parent = config.getSection("parent");
            assertNotNull(parent);
            assertEquals("value", parent.getString("child.key"));
        }

        @Test
        void listsAndSectionArrays() {
            JsonConfig config = new JsonConfig();

            List<String> list = Arrays.asList("a", "b", "c");
            config.set("strings", list);

            assertTrue(config.isList("strings"));
            assertEquals(list, config.getStringList("strings"));

            Map<String, Object> m1 = new LinkedHashMap<>();
            m1.put("k", "v");
            Map<String, Object> m2 = new LinkedHashMap<>();
            m2.put("k", "w");

            config.set("sections", Arrays.asList(m1, m2));
            List<ConfigSection> sections = config.getSectionList("sections");
            assertEquals(2, sections.size());
            assertEquals("v", sections.get(0).getString("k"));

            List<Map<String, Object>> mapList = config.getMapList("sections");
            assertEquals("v", mapList.get(0).get("k"));
        }

        @Test
        void serializableFromMap() {
            JsonConfig config = new JsonConfig();

            Map<String, Object> serialMap = new LinkedHashMap<>();
            serialMap.put(NamedSerializers.SERIALIZED_TYPE_KEY, NamedSerializers.getSerializeName(SerializableExample.class));
            serialMap.put("name", "example");
            serialMap.put("value", 42);

            config.set("serialMap", serialMap);

            SerializableExample se = config.getSerializable("serialMap", SerializableExample.class);
            assertNotNull(se);
            assertEquals("example", se.getName());
            assertEquals(42, se.getValue());
        }

        @Test
        void removeAndKeysValuesSeparator() {
            JsonConfig config = new JsonConfig();
            config.set("toRemove", "x");
            assertTrue(config.isSet("toRemove"));
            config.remove("toRemove");
            assertFalse(config.isSet("toRemove"));

            // nested key and path separator behavior
            config.set("a.b.c", "d");
            Collection<String> keysDefault = config.keys(true);
            assertTrue(keysDefault.contains("a.b.c"));

            config.settings().pathSeparator('/');
            Collection<String> keysAlt = config.keys(true);
            assertTrue(keysAlt.contains("a/b/c"));
        }

    }

}
