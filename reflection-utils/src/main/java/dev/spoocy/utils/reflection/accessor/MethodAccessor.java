package dev.spoocy.utils.reflection.accessor;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface MethodAccessor {

    /**
     * Gets the Method.
     *
     * @return the method
     */
    @NotNull
    Method getMethod();

    /**
     * If the method is static.
     *
     * @return {@code true} if the method is static, {@code false} otherwise
     */
    boolean isStatic();

    /**
    * Invoke the method with the given instance and arguments.
    *
    * @param instance the instance to invoke the method on, can be NULL if the method is static
    * @param args the arguments to pass to the method
    *
    * @return the result of the method invocation
    */
    Object invoke(Object instance, Object... args);

    /**
     * Checks if the method has the given annotation.
     *
     * @return {@code true} if the method has the given annotation, {@code false} otherwise
     */
    <T extends Annotation> boolean hasAnnotation(Class<T> annotation);

    /**
     * Gets a certain annotation of the method.
     *
     * @param annotation the annotation to get
     *
     * @return the annotation of the method
     */
    <T extends Annotation> T getAnnotation(Class<T> annotation);

}
