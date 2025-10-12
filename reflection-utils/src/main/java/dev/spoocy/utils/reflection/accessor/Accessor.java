package dev.spoocy.utils.reflection.accessor;

import dev.spoocy.utils.reflection.accessor.impl.ReflectionConstructor;
import dev.spoocy.utils.reflection.accessor.impl.ReflectionField;
import dev.spoocy.utils.reflection.accessor.impl.ReflectionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utility class to create accessors for constructors, methods and fields.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Accessor {

    private Accessor() { }

    public static ConstructorAccessor getConstructor(@NotNull Constructor<?> constructor) {
        return getAccessor(constructor);
    }

    public static MethodAccessor getMethod(@NotNull Method method) {
        return getAccessor(method);
    }

    public static FieldAccessor getField(@NotNull Field field) {
        return getAccessor(field);
    }

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType STATIC_FIELD_GETTER = MethodType.methodType(Object.class);
    private static final MethodType STATIC_FIELD_SETTER = MethodType.methodType(void.class, Object.class);
    private static final MethodType VIRTUAL_FIELD_GETTER = MethodType.methodType(Object.class, Object.class);
    private static final MethodType VIRTUAL_FIELD_SETTER = MethodType.methodType(void.class, Object.class, Object.class);

    private static ConstructorAccessor getAccessor(@NotNull Constructor<?> constructor) {
        try {
            constructor.setAccessible(true);
            MethodHandle unreflected = LOOKUP.unreflectConstructor(constructor);
            MethodHandle generified = toGeneric(unreflected, false, true);

            return new ReflectionConstructor(constructor, generified);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot access constructor " + constructor);
        }
    }

    private static MethodAccessor getAccessor(@NotNull Method method) {
        try {
            method.setAccessible(true);
            MethodHandle unreflected = LOOKUP.unreflect(method);
            boolean staticMethod = Modifier.isStatic(method.getModifiers());

            MethodHandle generified = toGeneric(unreflected, staticMethod, false);
            return new ReflectionMethod(method, generified, staticMethod);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot access method " + method);
        }
    }

    private static FieldAccessor getAccessor(@NotNull Field field) {
        try {
            field.setAccessible(true);
            boolean isStatic = Modifier.isStatic(field.getModifiers());

            MethodHandle getter = getGetter(field, isStatic);
            MethodHandle setter = getSetter(field, isStatic);

            return new ReflectionField(field, setter, getter, isStatic);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot access field " + field);
        }
    }

    @Nullable
    private static MethodHandle getGetter(@NotNull Field field, boolean isStatic) {
        try {
            MethodHandle handle;
            if (isStatic) {
                handle = LOOKUP.findStaticGetter(field.getDeclaringClass(), field.getName(), field.getType());
                handle = handle.asType(STATIC_FIELD_GETTER);
            } else {
                handle = LOOKUP.findGetter(field.getDeclaringClass(), field.getName(), field.getType());
                handle = handle.asType(VIRTUAL_FIELD_GETTER);
            }
            return handle;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static MethodHandle getSetter(@NotNull Field field, boolean isStatic) {
        try {
            MethodHandle handle;
            if (isStatic) {
                handle = LOOKUP.findStaticSetter(field.getDeclaringClass(), field.getName(), field.getType());
                handle = handle.asType(STATIC_FIELD_SETTER);
            } else {
                handle = LOOKUP.findSetter(field.getDeclaringClass(), field.getName(), field.getType());
                handle = handle.asType(VIRTUAL_FIELD_SETTER);
            }
            return handle;
        } catch (Exception e) {
            return null;
        }
    }

    private static MethodHandle toGeneric(@NotNull MethodHandle handle, boolean staticMethod, boolean ctor) {
        MethodHandle m = handle.asFixedArity();
        int i = handle.type().parameterCount() - (ctor || staticMethod ? 0 : 1);

        MethodType methodType = MethodType.genericMethodType(ctor ? 0 : 1, true);

        m = m.asSpreader(Object[].class, i);

        if (staticMethod) {
            m = MethodHandles.dropArguments(m, 0, Object.class);
        }

        return m.asType(methodType);
    }



}
