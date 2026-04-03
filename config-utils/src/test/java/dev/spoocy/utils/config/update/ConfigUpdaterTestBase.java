package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.*;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.ResourceTest;
import dev.spoocy.utils.config.types.MemoryConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for configuration updater tests.
 * Provides common utilities for setting up and testing config updates.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public abstract class ConfigUpdaterTestBase extends ResourceTest {

    protected static final String UPDATE_RESOURCES_PREFIX = "update/";

    @NotNull
    protected static Version version(@NotNull String version) {
        return Version.parse(version);
    }

    @NotNull
    protected static MemoryConfig memoryConfig() {
        return new MemoryConfig();
    }

    @NotNull
    protected static Resource updateResource(@NotNull String resourceName) {
        return Resources.fromJar("dev/spoocy/utils/config/" + UPDATE_RESOURCES_PREFIX + resourceName);
    }

    @NotNull
    protected static VersionResolver pathVersionResolver(@NotNull String path, @NotNull Version fallback) {
        return new VersionResolver() {
            @Override
            public @NotNull Version resolve(@NotNull dev.spoocy.utils.config.ConfigSection config) {
                Version resolved = config.getVersion(path, null);
                return resolved == null ? fallback : resolved;
            }

            @Override
            public void apply(@NotNull dev.spoocy.utils.config.ConfigSection config, @NotNull Version version) {
                config.set(path, version.formatFull());
            }
        };
    }

    /**
     * Helper method to write YAML content to a temporary file.
     */
    protected Path createConfigFile(@NotNull Path tempDir, @NotNull String filename, @NotNull String content) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}

