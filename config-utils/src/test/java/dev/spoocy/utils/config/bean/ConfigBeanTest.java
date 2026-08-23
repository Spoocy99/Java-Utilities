package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.BaseResourceResolver;
import dev.spoocy.utils.config.Resources;
import dev.spoocy.utils.config.constructor.SafeConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import dev.spoocy.utils.config.representer.SafeRepresenter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ConfigBeanTest {

    @TempDir
    private static Path tempDir;

    public static final TestResourceResolver RESOURCE_RESOLVER = new TestResourceResolver();
    public static final SafeRepresenter REPRESENTER = new SafeRepresenter();
    public static final SafeConstructor CONSTRUCTOR = new SafeConstructor();

    public static final ConfigBeanLoader LOADER = new ConfigBeanLoader(
            RESOURCE_RESOLVER,
            REPRESENTER,
            CONSTRUCTOR
    );


    public static class TestResourceResolver extends BaseResourceResolver {

        public TestResourceResolver() {
            super(ConfigBeanTest.class.getClassLoader(), YamlConfigLoader.INSTANCE, JsonConfigLoader.INSTANCE);
        }

        @Override
        public @NotNull Resource resolve(@NotNull String location) {
            return Resources.fromPath(tempDir.resolve("config-utils-test/" + location));
        }

    }

}
