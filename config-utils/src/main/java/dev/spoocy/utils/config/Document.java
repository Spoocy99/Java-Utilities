package dev.spoocy.utils.config;

import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.representer.Representer;
import dev.spoocy.utils.config.types.ConfigSettings;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Document extends Config {

    /**
     * Gets the file associated with this document.
     *
     * @return the file
     */
    @NotNull
    Resource getRelation();

    /**
     * Saves the file to the documents location.
     *
     * @throws IOException if an error occurs while saving the file
     * 
     * @see ConfigSettings#representer(Representer)
     */
    void save() throws IOException;
}
