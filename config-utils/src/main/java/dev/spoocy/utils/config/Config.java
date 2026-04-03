package dev.spoocy.utils.config;

import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.io.WriteableResource;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.types.ConfigSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Config extends ConfigSection {

    /**
     * Retrieves the {@link ConfigSettings} associated with this config.
     *
     * @return the config settings
     */
    @NotNull
    ConfigSettings settings();

    /**
     * Serializes the config into a string representation using the default representer.
     *
     * @return the string representation of the config
     *
     * @see ConfigSettings#representer(Representer)
     */
    @NotNull
    default String saveToString() {
        return saveToString(this.settings().representer());
    }

    /**
     * Serializes the config to a string representation using a custom representer.
     *
     * @param representer the representer to use for converting values to config nodes
     *
     * @return the string representation of the config
     */
    @NotNull
    String saveToString(@NotNull Representer representer);

    /**
     * Saves the configuration to the specified writable resource using the default representer for serialization.
     *
     * @param file the writable resource where the configuration should be saved; must not be null
     *
     * @throws IOException if an error occurs while writing to the resource
     *
     * @see ConfigSettings#representer(Representer)
     */
    default void save(@NotNull WriteableResource file) throws IOException {
        save(file, this.settings().representer());
    }

    /**
     * Saves the configuration to the specified writable resource using the provided representer for serialization.
     *
     * @param file        the writable resource where the configuration should be saved; must not be null
     * @param representer the representer used to serialize the configuration; must not be null
     *
     * @throws IOException if an error occurs while writing to the resource
     */
    void save(@NotNull WriteableResource file, @NotNull Representer representer) throws IOException;

    /**
     * Creates a new {@link Document} instance with the specified relation.
     *
     * @param resource the resource to the related config file
     *
     * @return a new config instance with the specified relation
     */
    @NotNull
    Document withRelation(@NotNull Resource resource);

}
