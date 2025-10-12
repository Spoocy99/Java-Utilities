package dev.spoocy.utils.reflection;

import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.ReflectionClass;
import dev.spoocy.utils.reflection.scanner.Scanner;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ReflectionBuilder {

    /**
     * Sets the target class for the builder.
     *
     * @param clazz the class.
     *
     * @return the builder instance.
     */
    ReflectionBuilder forClass(@NotNull Class<?> clazz);

    /**
     * Sets the target class for the builder by its name.
     *
     * @param className the class name.
     *
     * @return the builder instance.
     */
    ReflectionBuilder forName(@NotNull String className);

    /**
     * Sets the scanner to use for scanning members.
     *
     * @param scanner the scanner.
     *
     * @return the builder instance.
     */
    ReflectionBuilder scanner(@NotNull Scanner scanner);

    /**
     * Sets the scanner to {@link Scanner#PUBLIC_DECLARED} to scan
     * public declared members.
     *
     * @return the builder instance.
     */
    ReflectionBuilder publicMembers();

    /**
     * Sets the scanner to {@link Scanner#FORCE_ACCESS} to scan
     * all declared members with force access.
     *
     * @return the builder instance.
     */
    ReflectionBuilder privateMembers();

    /**
     * Sets the scanner to {@link Scanner#INHERITED} to scan
     * all members including inherited ones.
     *
     * @return the builder instance.
     */
    ReflectionBuilder inheritedMembers();

    /**
     * Builds a {@link ReflectionClass} using the builder.
     *
     * @return the reflection class.
     */
    ReflectionClass build();

    /**
     * Builds a {@link ClassAccess} for the target class.
     *
     * @return the class access.
     */
    ClassAccess buildAccess();


}
