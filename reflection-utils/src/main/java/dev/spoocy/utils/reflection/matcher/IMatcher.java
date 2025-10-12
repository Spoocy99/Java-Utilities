package dev.spoocy.utils.reflection.matcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple matcher interface for reflection finders.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

@FunctionalInterface
public interface IMatcher<T> {

    /**
     * Determines if the given value matches the object.
     *
     * @param value the value.
     * @param parent the object, NULL if the value root
     *
     * @return {@code true} if the value matches, otherwise {@code false}.
     */
    boolean isMatch(final @NotNull T value, final @Nullable Object parent);

    /**
     * Creates a new matcher that requires both of the given matcher to match.
     *
     * @param other the other matcher.
     *
     * @return a new matcher.
     *
     * @see #isMatch(Object, Object)
     */
    default IMatcher<T> and(final IMatcher<T> other) {
        return (value, parent) ->
                this.isMatch(value, parent) && other.isMatch(value, parent)
                ;
    }

    /**
     * Creates a new matcher that requires any of the given matcher to match.
     *
     * @param other the other matcher.
     *
     * @return a new matcher.
     *
     * @see #isMatch(Object, Object)
     */
    default IMatcher<T> or(final IMatcher<T> other) {
        return (value, parent) ->
                this.isMatch(value, parent) || other.isMatch(value, parent)
                ;
    }

}
