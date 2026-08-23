package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class YamlConfigTest extends ConfigTest<YamlConfig> {

    private static final String EXAMPLE_FILE = resourcesPath("types/example.yml");
    private static final String EXAMPLE_MAP_FILE = resourcesPath("types/map-example.yml");

    private static Resource resolveExisting(@NotNull String file) {
        Resource classpath = Resources.fromJar(file);
        if (classpath.exists()) {
            return classpath;
        }

        Path direct = Path.of(file).toAbsolutePath();
        if (Files.exists(direct)) {
            return Resources.fromPath(direct);
        }

        return Resources.fromPath(Path.of("config-utils", file).toAbsolutePath());
    }

    @Override
    protected ConfigLoader<YamlConfig, ?> loader() {
        return YamlConfigLoader.INSTANCE;
    }

    @Override
    protected Resource exampleResource() {
        return resolveExisting(EXAMPLE_FILE);
    }

    @Override
    protected Resource exampleMapResource() {
        return resolveExisting(EXAMPLE_MAP_FILE);
    }

    @Override
    protected int exampleConfigEntries() {
        return 7;
    }

    @Override
    protected boolean supportsTags() {
        return false;
    }

    @Test
    void settings() {
        YamlSettings settings = loader().createEmpty().settings();

        assertEquals('.', settings.pathSeparator());
        assertSame(settings, settings.pathSeparator('/'));
        assertEquals('/', settings.pathSeparator());
    }

    @Test
    void loadAndSaveFlatValues() throws IOException {
        Path file = Files.createTempFile("yaml-flat", ".yml");
        Files.writeString(file, "foo: bar\nkey2: 123\n", StandardCharsets.UTF_8);

        YamlConfig config = load(Resources.fromPath(file));

        assertEquals("bar", config.getString("foo"));
        assertEquals(123, config.getInt("key2"));

        String saved = config.saveToString(REPRESENTER);
        assertTrue(saved.contains("foo"));
        assertTrue(saved.contains("bar"));
        assertTrue(saved.contains("key2"));
        assertTrue(saved.contains("123"));
    }

    @Test
    void savePlacesBlockCommentsBeforeTheKey() {
        YamlConfig config = new YamlConfig();
        config.set("num", 12);
        config.setComments("num", "An integer number");

        String saved = config.saveToString(REPRESENTER).replace("\r", "");

        assertTrue(saved.contains("# An integer number\nnum: 12"));
        assertFalse(saved.contains("num:\n  # An integer number"));
    }

    @Test
    void commentLayout() {
        YamlConfig config = new YamlConfig();

        config.setHeaderComments("Header comment");

        config.set("test", "abc");
        config.set("num", 12);
        config.setComments("num", "An integer number");
        config.setInlineComments("num", "This is an inline comment");

        config.setComments("num.sub", "A sub-number");

        String saved = config.saveToString(REPRESENTER).replace("\r", "");
        System.out.println(saved);
        assertEquals(
                "# Header comment" + "\n"
                        + "\n"
                        + "test: abc" + "\n"
                        + "\n"
                        + "# An integer number" + "\n"
                        + "num: 12 # This is an inline comment" + "\n",
                saved
        );
    }

}
