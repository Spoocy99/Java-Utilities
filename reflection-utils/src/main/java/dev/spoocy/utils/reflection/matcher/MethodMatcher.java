package dev.spoocy.utils.reflection.matcher;

import dev.spoocy.utils.reflection.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class MethodMatcher implements IMatcher<Method> {

    private final MethodBuilder data;

    public MethodMatcher(@NotNull MethodBuilder data) {
        this.data = data;
    }

    @Override
    public boolean isMatch(final @NotNull Method value, final @Nullable Object parent) {
        Class<?>[] parameters = value.getParameterTypes();
        int modifiers = value.getModifiers();

        return nameMatches(value.getName())
                && (modifiers & data.getRequiredModifiers()) == data.getRequiredModifiers()
                && (modifiers & data.getExcludedModifiers()) == 0
                && typeMatches(value.getReturnType())
                && requiredAnnotationsMatch(value.getDeclaredAnnotations())
                && excludedAnnotationsMatch(value.getDeclaredAnnotations())
                && data.getParameterCount() == parameters.length
                && matchParameters(parameters, value, data.getParameters())
                ;
    }

    private boolean nameMatches(String name) {
        if (data.getName() == null) return true;
        return data.getName().matcher(name).matches();
    }

    private boolean typeMatches(@NotNull Class<?> type) {
        IMatcher<Class<?>> matcher = this.data.getReturnType();

        // No type to match against
        if (matcher == null) return true;

        return matcher.isMatch(type, null);
    }

    private boolean requiredAnnotationsMatch(@NotNull Annotation[] annotations) {
        List<Class<? extends Annotation>> requiredAnnotations = this.data.getAnnotations();

        // No required annotations to match against
        if (requiredAnnotations == null || requiredAnnotations.isEmpty()) return true;

        for (Class<? extends Annotation> requiredAnnotation : requiredAnnotations) {
            boolean found = false;
            for (Annotation annotation : annotations) {
                if (requiredAnnotation.isInstance(annotation)) {
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return false;
    }

    private boolean excludedAnnotationsMatch(@NotNull Annotation[] annotations) {
        List<Class<? extends Annotation>> excludedAnnotations = this.data.getExcludedAnnotations();

        // No excluded annotations to match against
        if (excludedAnnotations == null || excludedAnnotations.isEmpty()) return true;

        for (Class<? extends Annotation> excludedAnnotation : excludedAnnotations) {
            for (Annotation annotation : annotations) {
                if (excludedAnnotation.isInstance(annotation)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean matchParameters(@NotNull Class<?>[] types, @NotNull Method parent, @NotNull List<ParameterMatcher> matchers) {
        if (matchers.isEmpty()) {
            return true;
        }

        int acceptingMatchers = 0;
        for (int i = 0; i < types.length; i++) {

            if (checkParameters(types[i], parent, i, matchers)) {
                acceptingMatchers++;

                if (acceptingMatchers == matchers.size()) {
                    return true;
                }

            }
        }

        return false;
    }

    private boolean checkParameters(@NotNull Class<?> value, @NotNull Method parent, int index, @NotNull List<ParameterMatcher> matchers) {
        for (ParameterMatcher matcher : matchers) {
            if (matcher.isParameterMatch(value, parent, index)) {
                return true;
            }
        }
        return false;
    }
}
