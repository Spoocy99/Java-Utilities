package dev.spoocy.utils.reflection.unwrapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a utility to unwrap wrapped objects
 * to their original form.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Unwrapper {

    @Nullable
    Object unwrapObject(@NotNull Object obj);

    @NotNull
    default Class<?> getClassOf(@NotNull Object obj) {
        if (obj instanceof Class) {
            return (Class<?>) obj;
        }
        return obj.getClass();
    }

    @Nullable
    default Class<?> getIfExpected(@NotNull Class<?> input, @NotNull Class<?> expected, @NotNull Class<?> result) {
        if (expected.isAssignableFrom(input)) {
            return result;
        }
        return null;
    }

}
