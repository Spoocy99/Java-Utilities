package dev.spoocy.utils.reflection;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.accessor.MethodAccessor;
import dev.spoocy.utils.reflection.accessor.impl.ReflectionBuilderImpl;
import dev.spoocy.utils.reflection.builder.FieldBuilder;
import dev.spoocy.utils.reflection.builder.MethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
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

    public static <T> T invokeConstructor(@NotNull Class<T> clazz, @NotNull Object... args) {

        Class<?>[] parameterTypes = Collector.of(args)
                .map(Object::getClass)
                .asArray();

        return (T) builder()
                .forClass(clazz)
                .publicMembers()
                .buildAccess()
                .constructor(parameterTypes)
                .invoke(args);
    }

    /**
     * Checks if the given class or any of its superclasses (if inheritance is true)
     * has the specified annotation.
     *
     * @param clazz       the class to check
     * @param annotation  the annotation to look for
     * @param inheritance whether to check superclasses for the annotation
     *
     * @return true if the class or any of its superclasses has the annotation, false otherwise
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
     * Gets the specified annotation from the given class or any of its superclasses (if inheritance is true).
     *
     * @param clazz       the class to check
     * @param annotation  the annotation to look for
     * @param inheritance whether to check superclasses for the annotation
     *
     * @return the annotation if found, null otherwise
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
     * @param enumClass the enum class
     * @param name      the name of the enum constant
     *
     * @return the enum constant with the specified name
     * @throws IllegalArgumentException if the specified enum class has no constant with the specified name
     */
    public static <E extends Enum<E>> E getEnumValue(@NotNull Class<E> enumClass, @NotNull String name) {
        return Enum.valueOf(enumClass, name);
    }

    /**
     * Gets all enum constants of the specified enum class as a set.
     *
     * @param enumClass the enum class
     *
     * @return a set of all enum constants of the specified enum class
     */
    public static <E extends Enum<E>> Set<E> getEnumValues(@NotNull Class<E> enumClass) {
        return Collector.of(enumClass.getEnumConstants()).asSet();
    }

    private Reflection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
