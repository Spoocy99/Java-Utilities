package dev.spoocy.utils.config.bean;

import dev.spoocy.utils.config.Readable;
import dev.spoocy.utils.reflection.accessor.Accessor;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BoundHook {

    private final MethodAccessor accessor;
    private final Class<?> parameterType;
    private final boolean returnsPostLoadResult;

    @Nullable
    public static BoundHook of(
            @NotNull Method method,
            @NotNull Class<? extends Annotation> annotation
    ) {
        if (!method.isAnnotationPresent(annotation)) {
            return null;
        }

        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Hook method must not be static: " + method);
        }

        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length > 1) {
            throw new IllegalArgumentException("Hook method must have zero or one parameter: " + method);
        }

        Class<?> returnType = method.getReturnType();
        boolean returnsPostLoadResult = false;
        if (annotation == PostLoad.class) {
            if (returnType == PostLoadResult.class) {
                returnsPostLoadResult = true;
            } else if (returnType != void.class) {
                throw new IllegalArgumentException("@PostLoad hook method must return void or PostLoadResult: " + method);
            }
        } else if (returnType != void.class) {
            throw new IllegalArgumentException("Hook method must return void: " + method);
        }

        Class<?> parameterType = parameters.length == 0 ? null : parameters[0];
        return new BoundHook(Accessor.getMethod(method), parameterType, returnsPostLoadResult);
    }

    private BoundHook(
            @NotNull MethodAccessor accessor,
            @Nullable Class<?> parameterType,
            boolean returnsPostLoadResult
    ) {
        this.accessor = accessor;
        this.parameterType = parameterType;
        this.returnsPostLoadResult = returnsPostLoadResult;
    }

    @NotNull
    public PostLoadResult invoke(@NotNull Object instance, @NotNull Readable readable) {
        Object result;

        if (this.parameterType == null) {
            result = this.accessor.invoke(instance);
        } else {
            if (!this.parameterType.isInstance(readable)) {
                throw new IllegalArgumentException("Hook parameter type is not compatible with readable instance: " + this.accessor.getMethod());
            }
            result = this.accessor.invoke(instance, readable);
        }

        if (this.returnsPostLoadResult && result instanceof PostLoadResult) {
            return (PostLoadResult) result;
        }

        return PostLoadResult.NONE;
    }

}
