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
     * Serializes the configuration data into a string format using the provided representer.
     *
     * @param representer the representer used to serialize the configuration data
     *
     * @return the serialized string representation of the configuration data
     */
    @NotNull
    String saveToString(@NotNull Representer representer);

    /**
     * Saves the configuration data to the specified writable resource using the provided representer.
     * This method serializes the configuration into a specific format and writes it to the provided file.
     *
     * @param file        the writable resource where the configuration data will be saved
     * @param representer the representer used for serializing the configuration data
     *
     * @throws IOException if an I/O error occurs during the save operation
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
