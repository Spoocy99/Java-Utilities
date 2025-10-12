package dev.spoocy.jda.commands.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Arguments.class)
public @interface Argument {

    OptionType type();

    String name();

    String description();

    boolean required() default false;

}
