package dev.spoocy.utils.config.loader;

import dev.spoocy.utils.config.Config;
import dev.spoocy.utils.config.constructor.SerializingConstructor;
import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.constructor.Constructor;
import dev.spoocy.utils.config.types.ConfigSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ConfigLoader<C extends Config, S extends ConfigSettings> {

    /**
     * @return The file extensions supported by this loader
     */
    String[] getSupportedExtensions();

    /**
     * Creates a new empty configuration instance of type C with default settings applied.
     *
     * @return A new empty configuration instance of type C.
     */
    default C createEmpty() {
        return createEmpty(s -> {});
    }

    /**
     * Creates a new empty configuration instance of type C and applies the specified settings editor
     * to configure its settings before being returned.
     *
     * @param settingsEditor A consumer that allows modifying the configuration settings of type S.
     *                       Must not be null.
     *
     * @return A new empty configuration instance of type C.
     */
    C createEmpty(@NotNull Consumer<S> settingsEditor);

    /**
     * Loads a configuration from the given resource using the specified constructor.
     *
     * @param resource    The resource to load the configuration from. Must not be null.
     * @param constructor The constructor to use for initializing the configuration. Must not be null.
     *
     * @return The loaded configuration instance.
     *
     * @throws IOException If an I/O error occurs while reading the resource.
     */
    default C load(@NotNull Resource resource, @NotNull Constructor constructor) throws IOException {
        return this.load(resource, constructor, s -> {});
    }

    /**
     * Loads a configuration from the provided resource using the specified constructor
     * and applies the given settings editor to modify configuration settings before loading.
     *
     * @param resource       The resource to load the configuration from. Must not be null and must exist.
     * @param constructor    The constructor to use for initializing the configuration structure. Must not be null.
     * @param settingsEditor A consumer function that applies modifications to the configuration settings. Must not be null.
     *
     * @return The loaded configuration instance of type C.
     *
     * @throws IOException If an I/O error occurs while reading the resource.
     */
    C load(@NotNull Resource resource, @NotNull Constructor constructor, @NotNull Consumer<S> settingsEditor) throws IOException;


}
