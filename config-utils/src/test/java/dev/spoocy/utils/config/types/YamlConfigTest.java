package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.ResourceTest;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import dev.spoocy.utils.config.ConfigSection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class YamlConfigTest extends ResourceTest {

    private static final String EXAMPLE_FILE = resourcesPath("types/example.yml");

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
            YamlConfig config = new YamlConfig();
            assertEquals(0, config.values(true).size());
        }

        @Test
        void createWithValues() {
            YamlConfig config = new YamlConfig();
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
        void loadEmpty() throws IOException {
            YamlConfig config = new YamlConfig();
            YamlConfigLoader.INSTANCE.loadFromString(config, "");

            assertEquals(0, config.values(true).size());
        }

        @Test
        void loadWithValues() throws IOException {
            Resource file = Resources.fromFile(EXAMPLE_FILE);
            YamlConfig config = (YamlConfig) loadDefault(file);

            assertEquals(3, config.values(true).size());
            assertEquals("bar", config.getString("foo"));
            assertEquals(123, config.getInt("key2"));

            SerializableExample expected = config.get("serializable", SerializableExample.class);
            assertEquals("example", expected.getName());
            assertEquals(42, expected.getValue());
        }

    }

    @Nested
    class Save {

        @Test
        void saveToString() {
            YamlConfig config = new YamlConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", SerializableExample.EXAMPLE);

            String yaml = config.saveToString();
            assertNotNull(yaml);
            assertTrue(yaml.contains("key1: value1"));
            assertTrue(yaml.contains("key2: 123"));
            assertTrue(yaml.contains("key3:"));
            assertTrue(yaml.contains("name: example"));
            assertTrue(yaml.contains("value: 42"));
        }

        @Test
        void write(@TempDir Path temporaryFolder) throws IOException {
            YamlConfig config = new YamlConfig();
            config.set("key1", "value1");
            config.set("key2", 123);
            config.set("key3", SerializableExample.EXAMPLE);

            Path filePath = temporaryFolder.resolve("config.yml");
            PathResource resource = new PathResource(filePath);
            config.save(resource);

            YamlConfig loadedConfig = (YamlConfig) loadDefault(resource);
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
            YamlConfig config = new YamlConfig();
            config.set("parent.child.key", "value");

            assertTrue(config.isSet("parent.child.key"));
            assertEquals("value", config.getString("parent.child.key"));

            ConfigSection parent = config.getSection("parent");
            assertNotNull(parent);
            assertEquals("value", parent.getString("child.key"));
        }

        @Test
        void listsAndSectionArrays() {
            YamlConfig config = new YamlConfig();

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
            YamlConfig config = new YamlConfig();

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
            YamlConfig config = new YamlConfig();
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
