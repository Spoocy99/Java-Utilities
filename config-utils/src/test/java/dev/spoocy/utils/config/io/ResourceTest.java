package dev.spoocy.utils.config.io;

import dev.spoocy.utils.config.BaseResourceResolver;
import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.ResourceResolver;
import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ResourceTest {

	public static final String TEST_RESOURCES_DIR = "src/test/resources/dev/spoocy/utils/config";
    private static final ResourceResolver RESOLVER = createResolver();

    private static ResourceResolver createResolver() {
        BaseResourceResolver resolver = new BaseResourceResolver(ResourceTest.class.getClassLoader());
        resolver.registerLoader(JsonConfigLoader.INSTANCE);
        resolver.registerLoader(YamlConfigLoader.INSTANCE);
        return resolver;
    }

	public static String resourcesPath(@NotNull String string) {
		return (TEST_RESOURCES_DIR + '/' + string).replace('/', File.separatorChar);
	}

    public static Config loadDefault(@NotNull Resource resource) {
        try {
            return RESOLVER.loadConfig(resource, SerializingConstructor.DEFAULT_INSTANCE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + resource, e);
        }
    }

}
