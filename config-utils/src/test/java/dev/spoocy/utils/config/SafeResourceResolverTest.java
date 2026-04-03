
package dev.spoocy.utils.config;

import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.types.JsonConfig;
import dev.spoocy.utils.config.types.YamlConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class SafeResourceResolverTest {

    private final SafeResourceResolver resolver = ResourceResolver.safeResolver();

    @Nested
    class LoadConfig {

        @Test
        void returnsEmptyConfigWhenResourceDoesNotExist(@TempDir Path tempDir) throws IOException {
            Path path = tempDir.resolve("missing.yml");

            Config loaded = resolver.loadConfig(Resources.fromPath(path), SerializingConstructor.DEFAULT_INSTANCE);

            YamlConfig yaml = assertInstanceOf(YamlConfig.class, loaded);
            assertEquals(0, yaml.values(true).size());
        }

        @Test
        void loadsExistingYamlResource(@TempDir Path tempDir) throws IOException {
            Path path = tempDir.resolve("config.YML");
            Files.writeString(path, "key: value\n", StandardCharsets.UTF_8);

            Config loaded = resolver.loadConfig(Resources.fromPath(path), SerializingConstructor.DEFAULT_INSTANCE);

            YamlConfig yaml = assertInstanceOf(YamlConfig.class, loaded);
            assertEquals("value", yaml.getString("key"));
        }

        @Test
        void loadsExistingJsonResource(@TempDir Path tempDir) throws IOException {
            Path path = tempDir.resolve("config.json");
            Files.writeString(path, "{\"key\":\"value\"}", StandardCharsets.UTF_8);

            Config loaded = resolver.loadConfig(Resources.fromPath(path), SerializingConstructor.DEFAULT_INSTANCE);

            JsonConfig json = assertInstanceOf(JsonConfig.class, loaded);
            assertEquals("value", json.getString("key"));
        }

        @Test
        void throwsWhenNoLoaderMatchesResource(@TempDir Path tempDir) {
            SafeResourceResolver noLoaderResolver = new SafeResourceResolver(SafeResourceResolverTest.class.getClassLoader());
            Path path = tempDir.resolve("config.yml");

            IOException exception = assertThrows(
                    IOException.class,
                    () -> noLoaderResolver.loadConfig(Resources.fromPath(path), SerializingConstructor.DEFAULT_INSTANCE)
            );

            assertTrue(exception.getMessage().contains("No config loader"));
        }

        @Test
        void validatesArguments(@TempDir Path tempDir) {
            Path path = tempDir.resolve("config.yml");

            assertThrows(
                    NullPointerException.class,
                    () -> resolver.loadConfig(null, SerializingConstructor.DEFAULT_INSTANCE)
            );
            assertThrows(
                    NullPointerException.class,
                    () -> resolver.loadConfig(Resources.fromPath(path), null)
            );
        }
    }
}
