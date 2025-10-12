package dev.spoocy.utils.reflection.accessor;

import dev.spoocy.utils.reflection.builder.FieldBuilder;
import dev.spoocy.utils.reflection.builder.MethodBuilder;
import dev.spoocy.utils.reflection.matcher.IMatcher;
import dev.spoocy.utils.reflection.scanner.Scanner;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ReflectionClass {

    /**
     * Creates a {@link ClassAccess} for the underlying class.
     *
     * @return A ClassAccess object.
     */
    @Contract(" -> new")
    @NotNull
    ClassAccess access();

    /**
     * Gets the underlying class.
     *
     * @return The underlying class.
     */
    @NotNull
    Class<?> getAccessedClass();

    /**
     * Gets the {@link Scanner} used for scanning members.
     *
     * @return The current scanner.
     */
    @NotNull
    Scanner getScanner();

    /**
     * Sets the scanner to use for scanning members.
     *
     * @param scanner The scanner to use.
     *
     * @return The current ReflectionClass instance for chaining.
     */
    @Contract("_ -> this")
    @NotNull
    ReflectionClass setScanner(@NotNull Scanner scanner);

    /**
     * Gets all constructors of the underlying class.
     *
     * @return A set of constructor objects.
     */
    @NotNull
    Set<Constructor<?>> constructors();

    /**
     * Gets a specific constructor of the underlying class.
     *
     * @param parameters The parameter types of the constructor.
     *
     * @return The constructor object, or null if not found.
     */
    @Nullable
    Constructor<?> constructor(@NotNull Class<?>... parameters);

    /**
     * Gets all constant fields of the underlying class
     * using the provided {@link Scanner}.
     *
     * @return A set of constant field objects.
     */
    @NotNull
    Set<Field> constants();

    /**
     * Gets all fields of the underlying class
     * using the provided {@link Scanner}.
     *
     * @return A set of field objects.
     */
    @NotNull
    Set<Field> fields();

    /**
     * Searches for fields of the underlying class with the specified
     * annotation using the provided {@link Scanner}.
     *
     * @param annotation The annotation class to search for.
     *
     * @return A set of field objects.
     */
    @NotNull
    Set<Field> fieldWithAnnotation(@NotNull Class<? extends Annotation> annotation);

    /**
     * Searches for fields of the underlying class using a {@link IMatcher}.
     * <br>
     * The matcher should be created using {@link FieldBuilder}.
     *
     * @param matcher The Matcher to use for searching.
     *
     * @return A set of field objects.
     */
    @NotNull
    Set<Field> fields(@NotNull IMatcher<Field> matcher);

    /**
     * Gets a specific field of the underlying class using a {@link IMatcher}.
     * <br>
     * The matcher should be created using {@link FieldBuilder}.
     *
     * @param matcher The Matcher to use for searching.
     *
     * @return The field object, or null if not found.
     */
    @Nullable
    default Field field(@NotNull IMatcher<Field> matcher) {
        Set<Field> fields = fields(matcher);
        return fields.isEmpty() ? null : fields.iterator().next();
    }

    /**
     * Gets all Methods of the underlying class
     * provided by the {@link Scanner}.
     *
     * @return A set of field objects.
     */
    @NotNull
    Set<Method> methods();

    /**
     * Searches for methods of the underlying class with the specified annotation.
     *
     * @param annotation The annotation class to search for.
     *
     * @return A set of method objects.
     */
    @NotNull
    Set<Method> methodsWithAnnotation(@NotNull Class<? extends Annotation> annotation);

    /**
     * Searches for methods of the underlying class using a {@link IMatcher}.
     * <br>
     * The matcher should be created using {@link MethodBuilder}.
     *
     * @param matcher The Matcher to use for searching.
     *
     * @return A set of method objects.
     */
    @NotNull
    Set<Method> methods(@NotNull IMatcher<Method> matcher);


    /**
     * Gets a specific method of the underlying class using a {@link IMatcher}.
     * <br>
     * The matcher should be created using {@link MethodBuilder}.
     *
     * @param matcher The Matcher to use for searching.
     *
     * @return The method object, or null if not found.
     */
    @Nullable
    default Method method(@NotNull IMatcher<Method> matcher) {
        Set<Method> methods = methods(matcher);
        return methods.isEmpty() ? null : methods.iterator().next();
    }



}
