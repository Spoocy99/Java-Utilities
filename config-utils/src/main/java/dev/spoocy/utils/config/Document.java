package dev.spoocy.utils.config;

import dev.spoocy.utils.config.io.Resource;
import dev.spoocy.utils.config.representer.Representer;
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
     * Returns a new {@link Config} instance without the associated relation.
     * This method is used to create a standalone configuration,
     * independent of any linked or related resources.
     *
     * @return a new {@link Config} instance without an associated relation
     */
    @NotNull
    Config withoutRelation();

    /**
     * Saves the current document state using the provided {@code Representer}.
     * <p>
     * This method serializes the document into a suitable format as defined
     * by the {@code Representer} implementation and writes it to the associated resource.
     *
     * @param representer the representer used to serialize the document data
     *
     * @throws IOException if an I/O error occurs during the save operation
     */
    void save(@NotNull Representer representer) throws IOException;
}
