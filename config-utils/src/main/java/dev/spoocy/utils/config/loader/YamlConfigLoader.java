package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.types.YamlConfig;
import dev.spoocy.utils.config.types.YamlSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public class YamlConfigLoader implements ConfigLoader<YamlConfig, YamlSettings> {

    public static final YamlConfigLoader INSTANCE = new YamlConfigLoader();

    private YamlConfigLoader() { }

    @Override
    public String[] getSupportedExtensions() {
        return new String[]{"yml", "yaml"};
    }

    @Override
    public YamlConfig createEmpty(@NotNull Consumer<YamlSettings> settingsEditor) {
        checkDependency();
        return new YamlConfig(settingsEditor);
    }

    @Override
    public YamlConfig load(
            @NotNull Resource resource,
            @NotNull Constructor constructor,
            @NotNull Consumer<YamlSettings> settingsEditor
    ) throws IOException{
        checkDependency();

        YamlConfig config = createEmpty(settingsEditor);
        String contents = resource.getContentAsString(StandardCharsets.UTF_8);
        new YamlProcessor(config.settings()).loadFromString(config, contents, constructor);
        return config;
    }

    private static void checkDependency() {
        try {
            Class.forName("org.yaml.snakeyaml.Yaml");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SnakeYAML could not be found in the classpath.");
        }
    }
}
