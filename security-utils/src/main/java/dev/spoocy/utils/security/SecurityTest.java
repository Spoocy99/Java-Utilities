package dev.spoocy.utils.security;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnegative;
import java.lang.annotation.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SecurityTest {

    @NotNull
    String value();

    @Nonnegative
    int priority() default 50;

    @NotNull
    Stage[] stage() default Stage.FINISHED_LOADING;

    @NotNull
    CheckResult resultOnException() default CheckResult.WARNING;

    enum Stage {

        INIT,

        FINISHED_LOADING,

        READY,

        SHUTDOWN;

    }

}
