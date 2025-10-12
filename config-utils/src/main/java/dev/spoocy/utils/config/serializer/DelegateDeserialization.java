package dev.spoocy.utils.config.serializer;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies to a {@link ConfigSerializable} that will delegate all
 * deserialization to another {@link ConfigSerializable}.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelegateDeserialization {

    /**
     * Defines which class should be used as a delegate for this
     * classes' deserialization.
     *
     * @return the delegate class
     */
    @NotNull
    Class<? extends ConfigSerializable> value();

}
