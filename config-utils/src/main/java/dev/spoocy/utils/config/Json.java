package dev.spoocy.utils.config;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Json {

    /** An empty JSON object. */
    Json EMPTY = () -> "{}";

    /**
     * Convert this object to a JSON string.
     *
     * @return the JSON string
     */
	String toJson();

}
