package dev.spoocy.jda.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {

    long value();

    TimeUnit timeUnit();

    Scope scope() default Scope.USER;

    enum Scope {
        USER,
        GUILD,
        GLOBAL;
    }

}
