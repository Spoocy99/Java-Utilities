package dev.spoocy.utils.config.constructor;

import dev.spoocy.utils.config.serializer.Serializers;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface SerializerAssignable {

    /**
     * Retrieves the registered {@link Serializers} instance.
     *
     * @return the registered Serializers instance
     */
    Serializers getSerializers();

}
