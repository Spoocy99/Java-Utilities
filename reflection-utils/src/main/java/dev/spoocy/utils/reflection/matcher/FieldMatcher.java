package dev.spoocy.utils.reflection.matcher;

import dev.spoocy.utils.reflection.builder.FieldBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class FieldMatcher implements IMatcher<Field> {

    private final FieldBuilder data;

    public FieldMatcher(@NotNull FieldBuilder data) {
        this.data = data;
    }

    @Override
    public boolean isMatch(final @NotNull Field value, final @Nullable Object parent) {
        int modifiers = value.getModifiers();

        return nameMatches(value.getName())
                && (modifiers & data.getRequiredModifiers()) == data.getRequiredModifiers()
                && (modifiers & data.getExcludedModifiers()) == 0
                && typeMatches(value.getType())
                && requiredAnnotationsMatch(value.getDeclaredAnnotations())
                && excludedAnnotationsMatch(value.getDeclaredAnnotations())
                && matchGenericTypes(value);
    }

    private boolean nameMatches(@NotNull String name) {
        Pattern required = this.data.getName();

        // No name to match against
        if (required == null) return true;

        return required.matcher(name).matches();
    }

    private boolean typeMatches(@NotNull Class<?> type) {
        IMatcher<Class<?>> matcher = this.data.getType();

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
        return true;
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

    private boolean matchGenericTypes(@NotNull Field field) {
        // Builder has no generic types to match against
        if (this.data.getGenericTypes().isEmpty()) return true;

        Type genericType = field.getGenericType();
        List<FieldBuilder.GenericType> requiredGenericTypes = this.data.getGenericTypes();

        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        if (actualTypeArguments.length != requiredGenericTypes.size()) {
            return false;
        }

        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualType = actualTypeArguments[i];
            FieldBuilder.GenericType requiredType = requiredGenericTypes.get(i);

            if (requiredType == null || requiredType.getType() == null) {
                // Amount of generic types do not match
                return false;
            }


            IMatcher<Class<?>> matcher = requiredType.getType();

            if (!matchActualType(actualType, matcher)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchActualType(@NotNull Type actualType, @NotNull IMatcher<Class<?>> matcher) {
        if (actualType instanceof Class<?>) {
            return matcher.isMatch((Class<?>) actualType, null);

        } else if (actualType instanceof ParameterizedType) {
            return matchActualType(((ParameterizedType) actualType).getRawType(), matcher);

        } else {
            return false;
        }
    }


}


