package dev.spoocy.utils.config.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be invoked after annotated field binding has finished.
 * <p>
 * The method may optionally receive the {@link dev.spoocy.utils.config.Readable} instance that was
 * used during binding as its single parameter.
 * <p>
 * <b>Supported signatures:</b>
 * <ul>
 *   <li>{@code void method()}</li>
 *   <li>{@code void method(Readable readable)}</li>
 *   <li>{@code PostLoadResult method()}</li>
 *   <li>{@code PostLoadResult method(Readable readable)}</li>
 * </ul>
 *
 * <b>Return type behaviour:</b>
 * <ul>
 *   <li>{@code void} – no special action after the hook returns.</li>
 *   <li>{@link PostLoadResult#NONE} – same as {@code void}, no action taken.</li>
 *   <li>{@link PostLoadResult#SAVE} – every bound field of the class is written back to the
 *       config, overwriting its values, and the document is saved.</li>
 * </ul>
 *
 * Any other return type will cause an {@link IllegalArgumentException} at startup.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 * @see PostLoadResult
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostLoad {
}

