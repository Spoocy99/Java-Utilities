package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.ConfigLoader;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class JsonConfigTest extends ConfigTest<JsonConfig> {

    private static final String EXAMPLE_FILE = resourcesPath("types/example.json");
    private static final String EXAMPLE_MAP_FILE = resourcesPath("types/map-example.json");

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
    protected ConfigLoader<JsonConfig, ?> loader() {
        return JsonConfigLoader.INSTANCE;
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
        JsonConfig config = loader().createEmpty();
        JsonSettings settings = (JsonSettings) config.settings();

        assertEquals('.', settings.pathSeparator());
        assertSame(settings, settings.pathSeparator('/'));
        assertEquals('/', settings.pathSeparator());
    }

    @Test
    void loadAndSaveFlatValues() throws IOException {
        Path file = Files.createTempFile("json-flat", ".json");
        Files.writeString(file, "{\"foo\":\"bar\",\"key2\":123}", StandardCharsets.UTF_8);

        JsonConfig config = load(Resources.fromPath(file));

        assertEquals("bar", config.getString("foo"));
        assertEquals(123, config.getInt("key2"));

        String saved = config.saveToString(REPRESENTER);
        assertTrue(saved.contains("foo"));
        assertTrue(saved.contains("bar"));
        assertTrue(saved.contains("key2"));
        assertTrue(saved.contains("123"));
    }

}
