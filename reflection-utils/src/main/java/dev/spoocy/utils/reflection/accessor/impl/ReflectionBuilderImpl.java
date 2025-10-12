package dev.spoocy.utils.reflection.accessor.impl;

import com.google.common.base.Preconditions;
import dev.spoocy.utils.reflection.ReflectionBuilder;
import dev.spoocy.utils.reflection.ReflectionException;
import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.ReflectionClass;
import dev.spoocy.utils.reflection.scanner.Scanner;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionBuilderImpl implements ReflectionBuilder {

    private Class<?> targetClass;
    private Scanner scanner;

    @Override
    public ReflectionBuilderImpl forClass(@NotNull Class<?> targetClass) {
        this.targetClass = targetClass;
        return this;
    }

    @Override
    public ReflectionBuilderImpl forName(@NotNull String className) {
        try {
            return forClass(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("Class not found: " + className, e);
        }
    }

    @Override
    public ReflectionBuilderImpl scanner(@NotNull Scanner scanner) {
        this.scanner = scanner;
        return this;
    }

    @Override
    public ReflectionBuilderImpl publicMembers() {
        return scanner(Scanner.PUBLIC_DECLARED);
    }

    @Override
    public ReflectionBuilderImpl privateMembers() {
        return scanner(Scanner.FORCE_ACCESS);
    }

    @Override
    public ReflectionBuilderImpl inheritedMembers() {
        return scanner(Scanner.INHERITED);
    }

    @Override
    public ReflectionClass build() {
        validate();
        return new ReflectionClassImpl(this.scanner, this.targetClass);
    }

    @Override
    public ClassAccess buildAccess() {
        return build().access();
    }

    private void validate() {
        Preconditions.checkNotNull(targetClass, "Target class must be set.");
        Preconditions.checkNotNull(scanner, "Scanner must be set.");
    }

}
