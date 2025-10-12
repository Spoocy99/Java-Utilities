package dev.spoocy.jda.commands.annotations;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String subCommand() default "";

    String description() default "";

    Permission[] permission() default {};

    boolean async() default false;

    boolean ephemeral() default false;

    boolean sendTyping() default false;

}
