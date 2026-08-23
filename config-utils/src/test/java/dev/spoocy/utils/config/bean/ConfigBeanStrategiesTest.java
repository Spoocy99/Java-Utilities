package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.types.JsonConfig;
import dev.spoocy.utils.config.types.MemoryConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigBeanStrategiesTest extends ConfigBeanTest {

    @Nested
    class JustLoad {

        @Test
        void missingValue() {
            MemoryConfig config = new MemoryConfig();

            StrategiesBean bean = LOADER.load(StrategiesBean.class, config, LoadStrategy.JUST_LOAD);

            // value in bean is overwritten
            assertEquals("default", bean.value);

            // value in config is not overwritten
            assertFalse(config.isSet("value"));
        }

        @Test
        void nonMissingValue() {
            MemoryConfig config = new MemoryConfig();
            config.set("value", "test123");

            StrategiesBean bean = LOADER.load(StrategiesBean.class, config, LoadStrategy.JUST_LOAD);

            // value in bean is overwritten
            assertEquals("test123", bean.value);

            // value in config is not overwritten
            assertEquals("test123", config.getString("value"));
        }

    }

    @Nested
    class SaveDefaults {

        @Test
        void missingValue() {
            MemoryConfig config = new MemoryConfig();

            StrategiesBean bean = LOADER.load(StrategiesBean.class, config, LoadStrategy.SAVE_DEFAULTS);

            // value in bean is overwritten
            assertEquals("default", bean.value);

            // value in config is overwritten
            assertEquals("default", config.getString("value"));

            // comments
            List<String> comments = config.getComments("value");
            assertNotNull(comments);
            assertEquals(2, comments.size());
            assertEquals("Test comment 1", comments.get(0));
            assertEquals("Test comment 2", comments.get(1));

            List<String> inlineComments = config.getInlineComments("value");
            assertNotNull(inlineComments);
            assertEquals(1, inlineComments.size());
            assertEquals("Test comment", inlineComments.get(0));
        }

        @Test
        void nonMissingValue() {
            MemoryConfig config = new MemoryConfig();
            config.set("value", "test123");

            StrategiesBean bean = LOADER.load(StrategiesBean.class, config, LoadStrategy.SAVE_DEFAULTS);

            // value in bean is not overwritten
            assertEquals("test123", bean.value);

            // value in config is not overwritten
            assertEquals("test123", config.getString("value"));
        }

    }

    @Nested
    class SaveDefaultsAndResource {

        @Test
        void missingValue() throws IOException {
            Resource resource = RESOURCE_RESOLVER.resolve("strategies1.json");
            Document doc = new JsonConfig().withRelation(resource);

            StrategiesBean bean = LOADER.load(StrategiesBean.class, doc, LoadStrategy.SAVE_DEFAULTS_AND_RESOURCE);

            // value in bean is overwritten
            assertEquals("default", bean.value);

            // value in resource is overwritten
            assertEquals("default", doc.getString("value"));

            Config loaded = YamlConfigLoader.INSTANCE.load(resource, CONSTRUCTOR);
            // value was saved to resource
            assertEquals("default", loaded.getString("value"));
        }

        @Test
        void nonMissingValue() throws IOException {
            Resource resource = RESOURCE_RESOLVER.resolve("strategies2.json");
            Document doc = new JsonConfig().withRelation(resource);
            doc.set("value", "test123");

            StrategiesBean bean = LOADER.load(StrategiesBean.class, doc, LoadStrategy.SAVE_DEFAULTS_AND_RESOURCE);

            // value in bean is not overwritten
            assertEquals("test123", bean.value);

            // value in resource is not overwritten
            assertEquals("test123", doc.getString("value"));

            // no changes so no file should be created
            assertFalse(resource.exists());

        }

    }

    @ConfigSource("strategies1.json")
    public static class StrategiesBean {

        @ConfigProperty(
                value = "value",
                inlineComments = "Test comment",
                comments = {"Test comment 1", "Test comment 2"}
        )
        public String value = "default";

    }

}
