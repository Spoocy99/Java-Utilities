package dev.spoocy.utils.config;

import dev.spoocy.utils.config.loader.*;
import dev.spoocy.utils.config.io.ResourceTest;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.types.SerializableExample;
import dev.spoocy.utils.config.types.YamlConfig;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.Set;

/**
 * Shared fixtures for annotated config binding/resolver tests.
 * 
 * @author Spoocy99 | GitHub: Spoocy99
 */
public abstract class AbstractAnnotatedConfigTest extends ResourceTest {

    @BeforeAll
    static void setupSerializers() {
        String name = NamedSerializers.getSerializeName(SerializableExample.class);
        if (NamedSerializers.DEFAULT_INSTANCE.getSerializableClassByName(name) == null) {
            NamedSerializers.DEFAULT_INSTANCE.register(SerializableExample.class);
        }
    }

    public static final class TestResourceResolver extends BaseResourceResolver {

        public TestResourceResolver(ClassLoader classLoader) {
            super(classLoader);
            registerLoader(JsonConfigLoader.INSTANCE);
            registerLoader(YamlConfigLoader.INSTANCE);
        }
    }

    public enum Mode {
        EASY,
        HARD
    }

    @ConfigSource("classpath:dev/spoocy/utils/config/loader/annotation/example.yml")
    public static class BasicSettings {

        @ConfigProperty("database.host")
        public String host = "localhost";

        @ConfigProperty("database.port")
        public int port = 3306;

        @ConfigProperty("feature.enabled")
        public boolean enabled;

        @ConfigProperty("feature.mode")
        public Mode mode = Mode.EASY;

        public String name = "fallback";

        @ConfigIgnore
        public String ignored = "keep-me";
    }

    @ConfigSource(section = "database")
    public static class DatabaseSettings {

        public String host;
        public int port;
    }

    @ConfigSource(saveDefaults = true)
    public static class DefaultsSettings {

        public String present;

        @ConfigProperty("missing.number")
        public int missingNumber = 42;
    }

    @ConfigSource(saveDefaults = true, allowMissingResource = true)
    public static class AllowMissingDefaultsSettings {

        @ConfigProperty("database.host")
        public String host = "127.0.0.1";

        @ConfigProperty(value = "database.port", saveDefault = false, comments = {"Port should stay unmanaged"})
        public int port = 3306;

        @ConfigProperty("feature.enabled")
        public boolean enabled = true;
    }

    @ConfigSource(saveDefaults = true)
    public static class SelectiveDefaultsSettings {

        @ConfigProperty("database.host")
        public String host = "localhost";

        @ConfigProperty(value = "database.port", saveDefault = false)
        public int port = 3306;

        @ConfigProperty("database.user")
        public String user = "root";
    }

    @ConfigSource(
            section = "database",
            saveDefaults = true,
            headerComments = {"Generated config", "Managed by annotations"},
            footerComments = {"End of config"},
            comments = {"Database connection settings"}
    )
    public static class CommentedSettings {

        @ConfigProperty(value = "host", comments = {"Database host"}, inlineComments = {"required"})
        public String host = "localhost";

        @ConfigProperty(value = "port", comments = {"Database port"})
        public int port = 3306;
    }

    public static class BasePrivateSettings {

        @ConfigProperty("database.host")
        public String host = "localhost";
    }

    @ConfigSource("classpath:dev/spoocy/utils/config/loader/annotation/example.yml")
    public static class PrivateInheritedSettings extends BasePrivateSettings {

        @ConfigProperty("database.port")
        public int port = 3306;

        private PrivateInheritedSettings() {
        }
    }

    @ConfigSource("classpath:dev/spoocy/utils/config/loader/annotation/serializable.yml")
    public static class SerializableFromResource {

        @ConfigProperty("serializable")
        public SerializableExample serializable;
    }

    public static class SerializableFromReadable {

        @ConfigProperty("serializable")
        public SerializableExample serializable;
    }

    public static class HookedSettings {

        @ConfigProperty("message")
        public String message = "default";

        public boolean preCalled;
        public boolean postCalled;

        @PreLoad
        private void beforeLoad(YamlConfig config) {
            this.preCalled = true;
            if (!config.isSet("message")) {
                this.message = "from-pre";
            }
        }

        @PostLoad
        private void afterLoad() {
            this.postCalled = true;
            this.message = this.message + "-post";
        }
    }

    @ConfigSource(saveDefaults = true)
    public static class PostLoadMutationSettings {

        @ConfigProperty("message")
        public String message = "default";

        public boolean postCalled;

        @PostLoad
        private PostLoadResult afterLoad() {
            this.postCalled = true;
            this.message = this.message + "-modified";
            return PostLoadResult.SAVE;
        }
    }

    @ConfigSource(saveDefaults = true)
    public static class SelectivePostLoadMutationSettings {

        @ConfigProperty("message")
        public String message = "default";

        @ConfigProperty(value = "runtime.only", saveDefault = false)
        public String runtimeOnly = "runtime";

        @PostLoad
        private PostLoadResult afterLoad() {
            this.message = this.message + "-saved";
            this.runtimeOnly = this.runtimeOnly + "-changed";
            return PostLoadResult.SAVE;
        }
    }

    public static class CollectionSettings {

        @ConfigProperty("flags")
        public List<String> flags = List.of();

        @ConfigProperty("ids")
        public Set<Integer> ids = Set.of();
    }
}

