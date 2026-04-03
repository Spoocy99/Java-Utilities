package dev.spoocy.utils.config.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be invoked before annotated field binding starts.
 * <p>
 * The method may optionally receive the {@link dev.spoocy.utils.config.Readable} instance that will be
 * used during binding as its single parameter.
 * <p>
 * <b>Supported signatures:</b>
 * <ul>
 *   <li>{@code void method()}</li>
 *   <li>{@code void method(Readable readable)}</li>
 * </ul>
 *
 * <b>Return type:</b> must always be {@code void}.
 * Any other return type will cause an {@link IllegalArgumentException} at startup.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreLoad {
}

