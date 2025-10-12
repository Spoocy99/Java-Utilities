package dev.spoocy.utils.reflection.matcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class ParameterMatcher implements IMatcher<Class<?>[]> {

    private final IMatcher<Class<?>> typeMatcher;
    private final Integer indexMatch;

    public ParameterMatcher(@NotNull IMatcher<Class<?>> typeMatcher) {
        this(typeMatcher, null);
    }

    public ParameterMatcher(@NotNull IMatcher<Class<?>> typeMatcher, @Nullable Integer indexMatch) {
        this.typeMatcher = typeMatcher;
        this.indexMatch = indexMatch;
    }

    public boolean isParameterMatch(final @NotNull Class<?> param, final @NotNull Method parent, int index) {
        if (this.indexMatch == null || this.indexMatch == index) {
            return this.typeMatcher.isMatch(param, parent);
        }
        return false;
    }

    @Override
    public boolean isMatch(final @NotNull Class<?> @NotNull [] value, final @Nullable Object parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "ParameterMatcher{" +
                "indexMatch=" + indexMatch +
                ", typeMatcher=" + typeMatcher +
                '}';
    }
}
