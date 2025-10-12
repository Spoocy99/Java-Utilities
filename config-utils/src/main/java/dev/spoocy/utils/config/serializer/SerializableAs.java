package dev.spoocy.utils.config.serializer;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an "alias" that a {@link ConfigSerializable} may be stored as.
 * If this is not present on a {@link ConfigSerializable} class, it
 * will use the fully qualified name of the class.
 * <p>
 * This value will be stored in the configuration so that the configuration
 * deserialization can determine what type it is.
 * <p>
 * Using this annotation on any other class than a {@link ConfigSerializable}
 * will have no effect.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializableAs {

    /**
     * This is the name your class will be stored and retrieved as.
     * <p>
     * This name MUST be unique.
     *
     * @return Name to serialize the class as.
     */
    @NotNull
    public String value();

}
