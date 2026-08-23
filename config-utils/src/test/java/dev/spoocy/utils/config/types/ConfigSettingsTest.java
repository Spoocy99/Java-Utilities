package dev.spoocy.utils.config.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ConfigSettingsTest {

    @Test
    void configurationReturnsOriginalConfig() {
        MemoryConfig config = new MemoryConfig();
        ConfigSettings settings = new ConfigSettings(config);

        assertSame(config, settings.configuration());
    }

    @Test
    void pathSeparatorIsFluent() {
        ConfigSettings settings = new ConfigSettings(new MemoryConfig());

        assertSame(settings, settings.pathSeparator('/'));
        assertEquals('/', settings.pathSeparator());
    }

}

