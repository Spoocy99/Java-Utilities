package dev.spoocy.utils.config.loader;

/**
 * Return value for {@link PostLoad} hook methods.
 * <p>
 * Returning {@link #SAVE} signals the config loader to overwrite every bound config property
 * with the current instance field values and persist the document.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public enum PostLoadResult {

    /**
     * No special action – identical to returning {@code void}.
     */
    NONE,

    /**
     * Write all bound class attributes back to the config and save the document.
     */
    SAVE
}

