package dev.spoocy.utils.config;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Json {

    /**
     * Convert this object to a JSON string.
     *
     * @return the JSON string
     */
	String toJson();

    /**
     * Create an empty JSON object.
     *
     * @return the empty JSON object
     */
	static Json empty() {
		return () -> "{}";
	}

}
