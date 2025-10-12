package dev.spoocy.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for repeatable `Argument` annotations.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {

    Argument[] value();

}
