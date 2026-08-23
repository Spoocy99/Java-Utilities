package dev.spoocy.utils.config;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.ClassPathResource;
import dev.spoocy.utils.config.io.FileSystemResource;
import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.types.YamlConfig;
import dev.spoocy.utils.config.types.YamlSettings;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class BaseResourceResolverTest extends ResourceTest {

    private static final ClassLoader CLASS_LOADER = BaseResourceResolverTest.class.getClassLoader();

    private static final dev.spoocy.utils.config.loader.ConfigLoader<YamlConfig, YamlSettings> PROPERTIES_LOADER = new dev.spoocy.utils.config.loader.ConfigLoader<>() {

        @Override
        public String[] getSupportedExtensions() {
            return new String[] {"properties"};
        }

        @Override
        public YamlConfig createEmpty(@NotNull Consumer<YamlSettings> settingsEditor) {
            return new YamlConfig(settingsEditor);
        }

        @Override
        public YamlConfig load(
                @NotNull dev.spoocy.utils.config.io.Resource resource,
                @NotNull Constructor constructor,
                @NotNull Consumer<YamlSettings> settingsEditor
        ) {
            throw new UnsupportedOperationException("Not used in this test");
        }
    };

    private static BaseResourceResolver createResolver() {
        return new BaseResourceResolver(CLASS_LOADER,
                JsonConfigLoader.INSTANCE,
                YamlConfigLoader.INSTANCE
        );
    }

    @Nested
    class Config {

        @Test
        void createEmptyConfig() {
            var config = createResolver().createEmpty(Resources.fromPath(resourcesPath("types/example.yaml")));

            assertNotNull(config);
            assertTrue(config.values(true).isEmpty());
            assertInstanceOf(YamlConfig.class, config);
        }

    }

    @Nested
    class Resource {

        @Test
        void resolveClassPathResource() {
            var resource = createResolver().resolve("classpath:dev/spoocy/utils/config/types/example.yml");

            assertInstanceOf(ClassPathResource.class, resource);
        }

        @Test
        void resolveFileSystemResource() {
            var resource = createResolver().resolve("file:" + resourcesPath("types/example.yml"));

            assertInstanceOf(FileSystemResource.class, resource);
        }

        @Test
        void resolvePathResource() {
            var resource = createResolver().resolve(resourcesPath("types/example.yml"));

            assertInstanceOf(PathResource.class, resource);
        }

    }

    @Nested
    class ConfigLoader {

        @Test
        void resolveJsonLoader() {
            var loader = createResolver().resolveLoader(Resources.fromPath(resourcesPath("types/example.json")));

            assertSame(JsonConfigLoader.INSTANCE, loader);
        }

        @Test
        void resolveYamlLoader() {
            var loader = createResolver().resolveLoader(Resources.fromPath(resourcesPath("types/example.yml")));

            assertSame(YamlConfigLoader.INSTANCE, loader);
        }

        @Test
        void throwsWhenRequireLoader() {
            var resolver = createResolver();
            var resource = Resources.fromPath(resourcesPath("io/example.properties"));

            assertThrows(IllegalArgumentException.class, () -> resolver.requireLoader(resource));
        }

        @Test
        void registerCustomLoader() {
            var resolver = createResolver();
            resolver.registerLoader(PROPERTIES_LOADER);

            var resource = Resources.fromPath(resourcesPath("io/example.PROPERTIES"));
            assertSame(PROPERTIES_LOADER, resolver.requireLoader(resource));
        }

    }
}
