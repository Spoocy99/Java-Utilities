package dev.spoocy.utils.config;

import dev.spoocy.utils.config.loader.JsonConfigLoader;
import dev.spoocy.utils.config.loader.YamlConfigLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class ResourceTest {

	protected static final String TEST_RESOURCES_DIR = "src/test/resources/dev/spoocy/utils/config";
    protected static final TestResourceResolver RESOLVER = new TestResourceResolver(ResourceTest.class.getClassLoader());

	@NotNull
    protected static String resourcesPath(@NotNull String string) {
		return (TEST_RESOURCES_DIR + '/' + string).replace('/', File.separatorChar);
	}

    @NotNull
    protected static BaseResourceResolver resolver() {
        return RESOLVER;
    }

    public static final class TestResourceResolver extends BaseResourceResolver {

        public TestResourceResolver(ClassLoader classLoader) {
            super(classLoader);
            registerLoader(JsonConfigLoader.INSTANCE);
            registerLoader(YamlConfigLoader.INSTANCE);
        }
    }

}
