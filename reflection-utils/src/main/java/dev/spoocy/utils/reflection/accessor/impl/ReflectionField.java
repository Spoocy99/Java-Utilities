package dev.spoocy.utils.reflection.accessor.impl;

import dev.spoocy.utils.common.log.ILogger;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionField implements FieldAccessor {

    private final Field field;
    private final MethodHandle setter;
    private final MethodHandle getter;
    private final boolean isStatic;

    public ReflectionField(@NotNull Field field, @NotNull MethodHandle setter, @NotNull MethodHandle getter, boolean isStatic) {
        this.field = field;
        this.setter = setter;
        this.getter = getter;
        this.isStatic = isStatic;
    }

    @Override
    public @NotNull Field getField() {
        return this.field;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public Object get(Object instance) {
        try {
            return isStatic ?
                    getter.invokeExact(null) :
                    getter.invoke(instance)
                    ;
        } catch (Throwable throwable) {
            ILogger.forThisClass().debug("Cannot access Getter for Field {}, trying to get directly.", field);
            return getDirectly(instance);
        }
    }

    @Override
    public void set(Object instance, Object value) {
        try {
            if (isStatic) {
                setter.invokeExact(null, value);
                return;
            }
            setter.invoke(instance, value);
        } catch (Throwable throwable) {
            setDirectly(instance, value);
        }
    }

    @Override
    public Object getDirectly(Object instance) {
        try {
            return field.get(instance);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to get value of field " + field, throwable);
        }
    }

    @Override
    public void setDirectly(Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to set value of field " + field, throwable);
        }
    }

    @Override
    public String toString() {
        return "ReflectionField{" +
                "field=" + field +
                ", setter=" + setter +
                ", getter=" + getter +
                ", isStatic=" + isStatic +
                '}';
    }
}
