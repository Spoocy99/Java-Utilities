package dev.spoocy.utils.config;

import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.types.YamlConfig;
import dev.spoocy.utils.reflection.Reflection;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class BaseResourceResolverTest extends AbstractAnnotatedConfigTest {

    private abstract static class ResolverCase {
        protected final TestResourceResolver resolver = new TestResourceResolver(BaseResourceResolverTest.class.getClassLoader());

        protected YamlConfig loadYaml(Path path) throws IOException {
            return (YamlConfig) this.resolver.loadConfig(Resources.fromPath(path), SerializingConstructor.DEFAULT_INSTANCE);
        }
    }

    @Nested
    class SourceResolution extends ResolverCase {

        @Test
        void loadFromClassPathSourceAnnotation() throws IOException {
            BasicSettings settings = this.resolver.load(BasicSettings.class, SerializingConstructor.DEFAULT_INSTANCE);

            assertEquals("db.local", settings.host);
            assertEquals(3307, settings.port);
            assertTrue(settings.enabled);
            assertEquals(Mode.HARD, settings.mode);
            assertEquals("ExamplePlugin", settings.name);
            assertEquals("keep-me", settings.ignored);
        }

        @Test
        void loadFromExplicitResourceAndSection() throws IOException {
            String file = resourcesPath("loader/annotation/example.yml");
            DatabaseSettings settings = this.resolver.load(
                    DatabaseSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromFile(file)
            );

            assertEquals("db.local", settings.host);
            assertEquals(3307, settings.port);
        }

        @Test
        void loadsInheritedFieldsWithPrivateConstructor() throws IOException {
            PrivateInheritedSettings settings = this.resolver.load(PrivateInheritedSettings.class, SerializingConstructor.DEFAULT_INSTANCE);

            assertEquals("db.local", Reflection.getField(PrivateInheritedSettings.class, "host", String.class).get(settings));
            assertEquals(3307, settings.port);
        }

        @Test
        void loadsSerializableFromClasspathYaml() throws IOException {
            SerializableFromResource settings = this.resolver.load(SerializableFromResource.class, SerializingConstructor.DEFAULT_INSTANCE);

            assertNotNull(settings.serializable);
            assertEquals("example", settings.serializable.getName());
            assertEquals(42, settings.serializable.getValue());
        }
    }

    @Nested
    class MutationPersistence extends ResolverCase {

        @Test
        void writesMissingDefaultsWhenEnabled(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("annotated.yml");
            Files.writeString(target, "present: hello\n", StandardCharsets.UTF_8);

            DefaultsSettings settings = this.resolver.load(
                    DefaultsSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );
            assertEquals("hello", settings.present);
            assertEquals(42, settings.missingNumber);

            YamlConfig written = loadYaml(target);
            assertEquals(42, written.getInt("missing.number"));
        }

        @Test
        void allowsMissingSourceResourceWhenEnabled(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("missing-allowed.yml");

            AllowMissingDefaultsSettings settings = this.resolver.load(
                    AllowMissingDefaultsSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );

            assertEquals("127.0.0.1", settings.host);
            assertEquals(3306, settings.port);
            assertTrue(settings.enabled);
            assertTrue(Files.exists(target));

            YamlConfig written = loadYaml(target);
            assertEquals("127.0.0.1", written.getString("database.host", ""));
            assertTrue(written.getBoolean("feature.enabled"));
            assertFalse(written.isSet("database.port"));
        }

        @Test
        void persistsOnlySaveDefaultFieldsWhenConfigIsPartial(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("partial.yml");
            Files.writeString(target, "database:\n  host: db.local\n", StandardCharsets.UTF_8);

            SelectiveDefaultsSettings settings = this.resolver.load(
                    SelectiveDefaultsSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );

            assertEquals("db.local", settings.host);
            assertEquals(3306, settings.port);
            assertEquals("root", settings.user);

            YamlConfig written = loadYaml(target);
            assertEquals("db.local", written.getString("database.host", ""));
            assertEquals("root", written.getString("database.user", ""));
            assertFalse(written.isSet("database.port"));
        }

        @Test
        void doesNotPersistCommentsForUnmanagedFields(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("comments-unmanaged.yml");
            Files.writeString(target, "database:\n  host: db.local\n  port: 5500\n", StandardCharsets.UTF_8);

            SelectiveDefaultsSettings settings = this.resolver.load(
                    SelectiveDefaultsSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );

            assertEquals(5500, settings.port);
            YamlConfig written = loadYaml(target);
            assertIterableEquals(List.of(), written.getComments("database.port"));
            assertFalse(Files.readString(target, StandardCharsets.UTF_8).contains("Port should stay unmanaged"));
        }

        @Test
        void writesConfiguredComments(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("comments.yml");
            Files.writeString(target, "database:\n  host: db.local\n", StandardCharsets.UTF_8);

            CommentedSettings settings = this.resolver.load(
                    CommentedSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );
            assertEquals("db.local", settings.host);
            assertEquals(3306, settings.port);

            YamlConfig written = loadYaml(target);
            String yaml = Files.readString(target, StandardCharsets.UTF_8);
            assertIterableEquals(List.of("Generated config", "Managed by annotations"), written.getHeaderComments());
            assertIterableEquals(List.of("End of config"), written.getFooterComments());
            assertIterableEquals(List.of("Database connection settings"), written.getComments("database"));
            assertIterableEquals(List.of("Database host"), written.getComments("database.host"));
            assertIterableEquals(List.of("required"), written.getInlineComments("database.host"));
            assertIterableEquals(List.of("Database port"), written.getComments("database.port"));
            assertEquals(3306, written.getInt("database.port"));
            assertTrue(yaml.contains("# Generated config"));
            assertTrue(yaml.contains("# Database connection settings"));
            assertTrue(yaml.contains("host: db.local # required"));
        }

        @Test
        void postLoadResultSaveWritesInstanceFieldsBack(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("post-load-save.yml");
            Files.writeString(target, "message: hello\n", StandardCharsets.UTF_8);

            PostLoadMutationSettings settings = this.resolver.load(
                    PostLoadMutationSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );
            assertTrue(settings.postCalled);
            assertEquals("hello-modified", settings.message);

            YamlConfig written = loadYaml(target);
            assertEquals("hello-modified", written.getString("message", ""));
            assertTrue(Files.readString(target, StandardCharsets.UTF_8).contains("hello-modified"));
        }

        @Test
        void postLoadSaveSkipsFieldsWithSaveDefaultDisabled(@TempDir Path tempDir) throws IOException {
            Path target = tempDir.resolve("post-load-selective.yml");
            Files.writeString(target, "message: hello\n", StandardCharsets.UTF_8);

            SelectivePostLoadMutationSettings settings = this.resolver.load(
                    SelectivePostLoadMutationSettings.class,
                    SerializingConstructor.DEFAULT_INSTANCE,
                    Resources.fromPath(target)
            );

            assertEquals("hello-saved", settings.message);
            assertEquals("runtime-changed", settings.runtimeOnly);

            YamlConfig written = loadYaml(target);
            assertEquals("hello-saved", written.getString("message", ""));
            assertFalse(written.isSet("runtime.only"));
        }
    }
}

