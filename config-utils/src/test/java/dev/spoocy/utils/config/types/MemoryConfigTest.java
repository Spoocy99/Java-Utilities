package dev.spoocy.utils.config.types;

import dev.spoocy.utils.config.io.PathResource;
import dev.spoocy.utils.config.representer.SafeRepresenter;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class MemoryConfigTest {

    @Test
    void settingsAreAvailableAndMutable() {
        MemoryConfig config = new MemoryConfig();
        ConfigSettings settings = config.settings();

        assertSame(config, settings.configuration());
        assertEquals('.', settings.pathSeparator());
        assertSame(settings, settings.pathSeparator('/'));
        assertEquals('/', settings.pathSeparator());
    }

    @Test
    void savingIsNotSupported() {
        MemoryConfig config = new MemoryConfig();
        SafeRepresenter representer = new SafeRepresenter();

        assertThrows(UnsupportedOperationException.class, () -> config.saveToString(representer));
        assertThrows(UnsupportedOperationException.class, () -> config.save(new PathResource(Path.of("memory-config-test.yml")), representer));
    }

}

