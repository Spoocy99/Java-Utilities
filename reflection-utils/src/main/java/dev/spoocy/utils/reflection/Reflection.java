package dev.spoocy.utils.reflection;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.accessor.ConstructorAccessor;
import dev.spoocy.utils.reflection.accessor.FieldAccessor;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import dev.spoocy.utils.reflection.accessor.impl.ReflectionBuilderImpl;
import dev.spoocy.utils.reflection.builder.FieldBuilder;
import dev.spoocy.utils.reflection.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Reflection {

    public static ReflectionBuilder builder() {
        return new ReflectionBuilderImpl();
    }

    public static MethodBuilder method() {
        return MethodBuilder.create();
    }

    public static FieldBuilder field() {
        return FieldBuilder.create();
    }

    /**
     * Gets a public constructor accessor for the specified class and parameter types.
     *
     * @param clazz
     *          the class to get the constructor from
     * @param parameters
     *          the parameter types of the constructor
     *
     * @return the constructor accessor.
     */
    public static ConstructorAccessor getConstructor(@NotNull Class<?> clazz, @NotNull Class<?>... parameters) {
        return builder()
                .forClass(clazz)
                .publicMembers()
                .buildAccess()
                .constructor(parameters);
    }

    /**
     * Gets a field accessor for the specified class, field name, and field type.
     * Either fieldName or fieldType can be null, but not both.
     *
     * @param clazz
     *          the class to get the field from
     * @param fieldName
     *          the name of the field (can be null)
     * @param fieldType
     *          the type of the field (can be null)
     *
     * @return the field accessor.
     *
     * @throws IllegalArgumentException
     *          if both fieldName and fieldType are null
     */
    public static FieldAccessor getField(@NotNull Class<?> clazz, @Nullable String fieldName, @Nullable Class<?> fieldType) {
        FieldBuilder builder = field();

        if (fieldName != null) {
            builder.name(fieldName);
        }

        if (fieldType != null) {
            builder.type(fieldType);
        }

        return Reflection.builder()
                .forClass(clazz)
                .inheritedMembers()
                .buildAccess()
                .field(builder.build());
    }

    /**
     * Gets a method accessor for the specified class, method name, and parameter types.
     * Method name can be null, in which case the first method matching the parameter types will be returned.
     *
     * @param clazz
     *          the class to get the method from
     * @param methodName
     *          the name of the method (can be null)
     * @param args
     *          the parameter types of the method
     *
     * @return the method accessor.
     */
    public static MethodAccessor getMethod(@NotNull Class<?> clazz, @Nullable String methodName, @NotNull Object... args) {

        Class<?>[] parameterTypes = new Class<?>[args != null ? args.length : 0];
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];

                // check if arg is already a Class, if so keep it as is
                if (arg instanceof Class<?>) {
                    parameterTypes[i] = (Class<?>) arg;
                    continue;
                }

                parameterTypes[i] = args[i].getClass();
            }
        }

        MethodBuilder builder = method();

        if (methodName != null) {
            builder.name(methodName);
        }

        if (parameterTypes.length > 0) {
            builder.parameterCount(parameterTypes.length);
            for(int i = 0; i < parameterTypes.length; i++) {
                builder.parameterType(i, parameterTypes[i]);
            }
        }

        return Reflection.builder()
                .forClass(clazz)
                .inheritedMembers()
                .buildAccess()
                .method(builder.build());
    }

    /**
     * Checks if the given class has the specified annotation or any of its superclasses (if inheritance is true).
     *
     * @param clazz
     *          the class to check
     * @param annotation
     *          the annotation to look for
     * @param inheritance
     *          whether to check superclasses for the annotation
     *
     * @return true if the annotation is present, false otherwise
     */
    public static boolean hasAnnotation(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation, boolean inheritance) {
        if (clazz.isAnnotationPresent(annotation)) {
            return true;
        }

        if (inheritance && clazz.getSuperclass() != null) {
            return hasAnnotation(clazz.getSuperclass(), annotation, true);
        }

        return false;
    }

    /**
     * Gets the specified annotation from the given class or its superclasses (if inheritance is true).
     *
     * @param clazz
     *          the class to get the annotation from
     * @param annotation
     *          the annotation to look for
     * @param inheritance
     *          whether to check superclasses for the annotation
     *
     * @return the annotation if found, {@code null} otherwise.
     */
    @Nullable
    public static <A extends Annotation> A getAnnotation(@NotNull Class<?> clazz, @NotNull Class<A> annotation, boolean inheritance) {
        if (clazz.isAnnotationPresent(annotation)) {
            return clazz.getAnnotation(annotation);
        }

        if (inheritance && clazz.getSuperclass() != null) {
            return getAnnotation(clazz.getSuperclass(), annotation, true);
        }

        return null;
    }

    /**
     * Gets the enum constant of the specified enum class with the specified name.
     *
     * @param enumClass
     *          the enum class
     * @param name
     *          the name of the enum constant
     *
     * @return the enum constant with the specified name.
     *
     * @throws IllegalArgumentException
     *          if the specified enum class has no constant with the specified name
     */
    public static <E extends Enum<E>> E getEnumValue(@NotNull Class<E> enumClass, @NotNull String name) {
        return Enum.valueOf(enumClass, name);
    }

    /**
     * Gets all enum constants of the specified enum class as a set.
     *
     * @param enumClass the enum class
     *
     * @return a set of all enum constants of the specified enum class.
     */
    public static <E extends Enum<E>> Set<E> getEnumValues(@NotNull Class<E> enumClass) {
        return Collector.of(enumClass.getEnumConstants()).asSet();
    }

    private Reflection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
