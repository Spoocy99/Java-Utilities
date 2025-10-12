package dev.spoocy.utils.reflection.accessor;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ConstructorAccessor {

    /**
     * Gets the Constructor.
     *
     * @return the method
     */
    @NotNull
    Constructor<?> getConstructor();

    /**
     * Invoke the constructor with the given arguments.
     * Creates a new instance of the class.
     *
     * @param args the arguments to pass to the constructor
     *             (the types of the arguments must match the types of the constructor)
     *
     * @return the new instance of the class
     */
    Object invoke(Object... args);


}
