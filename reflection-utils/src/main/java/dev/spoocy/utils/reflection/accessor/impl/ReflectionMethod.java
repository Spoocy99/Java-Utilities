package dev.spoocy.utils.reflection.accessor.impl;

import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionMethod implements MethodAccessor {

    private final Method method;
    private final MethodHandle handler;
    private final boolean isStatic;

    public ReflectionMethod(Method method, MethodHandle methodHandle, boolean isStatic) {
        this.method = method;
        this.handler = methodHandle;
        this.isStatic = isStatic;
    }

    @Override
    public @NotNull Method getMethod() {
        return this.method;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Object invoke(Object target, Object... args) {
        try {
            return this.isStatic() ?
                    this.handler.invoke(null, args) :
                    this.handler.invoke(target, args)
                    ;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to invoke methode " + method, throwable);
        }
    }

    @Override
    public <T extends Annotation> boolean hasAnnotation(Class<T> annotation) {
        return this.method.isAnnotationPresent(annotation);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return hasAnnotation(annotation) ? this.method.getAnnotation(annotation) : null;
    }

    @Override
    public String toString() {
        return "ReflectionMethod{" +
                "handler=" + handler +
                ", method=" + method +
                ", isStatic=" + isStatic +
                '}';
    }
}
