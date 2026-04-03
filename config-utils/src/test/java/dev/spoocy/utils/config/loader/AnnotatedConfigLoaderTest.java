package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.AbstractAnnotatedConfigTest;
import dev.spoocy.utils.config.serializer.NamedSerializers;
import dev.spoocy.utils.config.types.SerializableExample;
import dev.spoocy.utils.config.types.YamlConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class AnnotatedConfigLoaderTest extends AbstractAnnotatedConfigTest {

    private abstract static class LoaderCase {
        protected final AnnotatedConfigLoader loader = new AnnotatedConfigLoader();
    }

    @Nested
    class SerializableBinding extends LoaderCase {

        @Test
        void loadsSerializableFromReadableMapUsingGetSerializable() {
            YamlConfig config = new YamlConfig();

            Map<String, Object> serialized = new LinkedHashMap<>();
            serialized.put(NamedSerializers.SERIALIZED_TYPE_KEY, NamedSerializers.getSerializeName(SerializableExample.class));
            serialized.put("name", "example");
            serialized.put("value", 42);
            config.set("serializable", serialized);

            SerializableFromReadable settings = this.loader.load(SerializableFromReadable.class, config);

            assertNotNull(settings.serializable);
            assertEquals("example", settings.serializable.getName());
            assertEquals(42, settings.serializable.getValue());
        }
    }

    @Nested
    class HookBinding extends LoaderCase {

        @Test
        void invokesPreAndPostLoadHooks() {
            YamlConfig config = new YamlConfig();

            HookedSettings withoutConfigValue = this.loader.load(HookedSettings.class, config);
            assertTrue(withoutConfigValue.preCalled);
            assertTrue(withoutConfigValue.postCalled);
            assertEquals("from-pre-post", withoutConfigValue.message);

            config.set("message", "from-config");
            HookedSettings withConfigValue = this.loader.load(HookedSettings.class, config);
            assertTrue(withConfigValue.preCalled);
            assertTrue(withConfigValue.postCalled);
            assertEquals("from-config-post", withConfigValue.message);
        }
    }

    @Nested
    class CollectionBinding extends LoaderCase {

        @Test
        void loadsTypedCollectionFieldsFromReadable() {
            YamlConfig config = new YamlConfig();
            config.set("flags", List.of("one", "two"));
            config.set("ids", List.of(7, 11));

            CollectionSettings settings = this.loader.load(CollectionSettings.class, config);

            assertIterableEquals(List.of("one", "two"), settings.flags);
            assertEquals(Set.of(7, 11), settings.ids);
        }
    }

    @Nested
    class WriteBinding extends LoaderCase {

        @Test
        void writeOnlyPersistsFieldsMarkedForDefaultSaving() {
            SelectiveDefaultsSettings settings = new SelectiveDefaultsSettings();
            settings.host = "db.remote";
            settings.port = 1234;
            settings.user = "admin";

            YamlConfig target = new YamlConfig();
            int mutations = this.loader.write(settings, target);

            assertEquals(2, mutations);
            assertEquals("db.remote", target.getString("database.host", ""));
            assertEquals("admin", target.getString("database.user", ""));
            assertFalse(target.isSet("database.port"));
        }
    }

}

