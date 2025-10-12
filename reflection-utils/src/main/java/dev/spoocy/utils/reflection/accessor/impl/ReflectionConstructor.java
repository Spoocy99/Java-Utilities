package dev.spoocy.utils.reflection.accessor.impl;

import dev.spoocy.utils.reflection.accessor.ConstructorAccessor;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionConstructor implements ConstructorAccessor {

    private final Constructor<?> constructor;
    private final MethodHandle handle;

    public ReflectionConstructor(Constructor<?> constructor, MethodHandle handle) {
        this.constructor = constructor;
        this.handle = handle;
    }

    @Override
    public @NotNull Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Override
    public Object invoke(Object... args) {
        try {
            return this.handle.invokeExact(args);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Cannot create instance of " + constructor, throwable);
        }
    }

    @Override
    public String toString() {
        return "ReflectionConstructor{" +
                "constructor=" + constructor +
                ", handle=" + handle +
                '}';
    }
}
