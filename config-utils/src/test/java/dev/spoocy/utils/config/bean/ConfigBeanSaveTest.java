package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.Document;
import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.types.YamlConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigBeanSaveTest extends ConfigBeanTest {

    @Test
    void writeToConfig() {
        Bean bean = new Bean();
        Config config = LOADER.writeToConfig(bean);

        assertEquals("test", config.getString("str", null));
        assertEquals(12, config.getInt("num", 0));

        List<String> comments = config.getInlineComments("str");
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertTrue(comments.contains("An example string"));

        List<String> inlineComments = config.getComments("num");
        assertNotNull(inlineComments);
        assertEquals(1, inlineComments.size());
        assertTrue(inlineComments.contains("An integer number"));
    }

    @Test
    void writeToProvidedConfig() {
        Bean bean = new Bean();
        YamlConfig yaml = YamlConfigLoader.INSTANCE.createEmpty(s -> {});
        Config config = LOADER.writeToConfig(bean, yaml);

        assertEquals("test", config.getString("str", null));
        assertEquals(12, config.getInt("num", 0));

        List<String> comments = config.getInlineComments("str");
        assertNotNull(comments);
        assertTrue(comments.contains("An example string"));

        List<String> inlineComments = config.getComments("num");
        assertNotNull(inlineComments);
        assertTrue(inlineComments.contains("An integer number"));
    }

    @Test
    void save(@TempDir Path path) throws Exception {
        Bean bean = new Bean();
        Resource resource = Resources.fromPath(path.resolve("test-bean-save.yml"));
        Document doc = YamlConfigLoader.INSTANCE.createEmpty(s -> {}).withRelation(resource);
        LOADER.save(bean, doc);

        assertTrue(resource.exists());
        String contents = resource.getContentAsString(StandardCharsets.UTF_8);
        assertTrue(contents.contains("num: 12"));
        assertTrue(contents.contains("str: test") || contents.contains("str: \"test\""));
        assertTrue(contents.contains("# An example string"));
        assertTrue(contents.contains("# An integer number"));
    }

    @Test
    void saveToProvidedDocument() throws Exception {
        Bean bean = new Bean();
        YamlConfig yaml = YamlConfigLoader.INSTANCE.createEmpty(s -> {});
        Resource resource = Resources.fromPath(Path.of("test-bean-save1.yml"));

        Document document = yaml.withRelation(resource);
        LOADER.save(bean, document);

        assertTrue(resource.exists());
        String contents = resource.getContentAsString(StandardCharsets.UTF_8);
        assertTrue(contents.contains("num: 12"));
        assertTrue(contents.contains("str: test") || contents.contains("str: \"test\""));
        assertTrue(contents.startsWith("# Example config for testing saving a bean to a config file"));
        assertTrue(contents.contains("# An example string"));
        assertTrue(contents.contains("# An integer number"));
    }

    @ConfigSource(
            value = "test-bean-save.yml",
            allowMissingResource = true,
            headerComments = "Example config for testing saving a bean to a config file"
    )
    static class Bean {

        @ConfigProperty(value = "str", inlineComments = "An example string")
        public String str = "test";

        @ConfigProperty(value = "num", comments = "An integer number")
        public int num = 12;

    }

}
