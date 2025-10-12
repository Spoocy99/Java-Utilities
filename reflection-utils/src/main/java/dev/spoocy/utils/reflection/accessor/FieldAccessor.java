package dev.spoocy.utils.reflection.accessor;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface FieldAccessor {

    /**
     * Gets the Field.
     *
     * @return the method
     */
    @NotNull
    Field getField();

    /**
     * If the field is static.
     *
     * @return {@code true} if the field is static, {@code false} otherwise
     */
    boolean isStatic();

    /**
     * Gets the value of the field using the getter.
     * <p>
     * If this fails the value will be retrieved directly.
     * @see #getDirectly(Object)
     *
     * @param instance the instance to get the value from, can be NULL if the field is static
     *
     * @return the value of the field
     */
    Object get(Object instance);

    /**
     * Sets the value of the field using the Setter.
     * <p>
     * If this fails the value will be set directly.
     * @see #setDirectly(Object, Object)
     *
     * @param instance the instance to set the value to, can be NULL if the field is static
     * @param value the value to set
     */
    void set(Object instance, Object value);

    /**
     * Gets the value of the field directly.
     *
     * @param instance the instance to get the value from, can be NULL if the field is static
     *
     * @return the value of the field
     */
    Object getDirectly(Object instance);

    /**
     * Sets the value of the field directly.
     *
     * @param instance the instance to set the value to, can be NULL if the field is static
     * @param value the value to set
     */
    void setDirectly(Object instance, Object value);

}
