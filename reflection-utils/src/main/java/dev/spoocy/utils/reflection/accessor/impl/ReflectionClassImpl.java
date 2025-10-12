package dev.spoocy.utils.reflection.accessor.impl;

import dev.spoocy.utils.reflection.accessor.ClassAccess;
import dev.spoocy.utils.reflection.accessor.ReflectionClass;
import dev.spoocy.utils.reflection.matcher.IMatcher;
import dev.spoocy.utils.reflection.scanner.Scanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionClassImpl implements ReflectionClass {

    private final Class<?> SOURCE;
    private Scanner scanner;

    public ReflectionClassImpl(
            @NotNull Scanner scanner,
            @NotNull Class<?> source
    ) {
        this.scanner = scanner;
        this.SOURCE = source;
    }

    @Override
    public @NotNull ClassAccess access() {
        return new ClassAccessImpl(this);
    }

    @Override
    public @NotNull Class<?> getAccessedClass() {
        return this.SOURCE;
    }

    @NotNull
    @Override
    public Scanner getScanner() {
        return this.scanner;
    }

    @NotNull
    @Override
    public ReflectionClass setScanner(@NotNull Scanner scanner) {
        this.scanner = scanner;
        return this;
    }

    @Override
    public @NotNull Set<Constructor<?>> constructors() {
        return this.scanner.constructors(this.SOURCE);
    }

    @Override
    public @Nullable Constructor<?> constructor(@NotNull Class<?>... parameters) {
        return this.scanner.lookupConstructor(this.SOURCE, parameters);
    }

    @Override
    public @NotNull Set<Field> constants() {
        List<Field> constants = new ArrayList<>();

        for (Field f : this.fields()) {
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                constants.add(f);
            }
        }

        return Set.copyOf(constants);
    }

    @Override
    public @NotNull Set<Field> fields() {
        return this.scanner.fields(this.SOURCE);
    }

    @Override
    public @NotNull Set<Field> fieldWithAnnotation(@NotNull Class<? extends Annotation> annotation) {
        Set<Field> fields = new HashSet<>();

        for (Field f : this.fields()) {
            if (f.isAnnotationPresent(annotation)) {
                fields.add(f);
            }
        }

        return fields;
    }

    @Override
    public @NotNull Set<Field> fields(@NotNull IMatcher<Field> matcher) {
        return this.scanner.lookupFields(this.SOURCE, matcher);
    }

    @Override
    public @NotNull Set<Method> methods() {
        return this.scanner.methods(this.SOURCE);
    }

    @Override
    public @NotNull Set<Method> methodsWithAnnotation(@NotNull Class<? extends Annotation> annotation) {
        Set<Method> methods = new HashSet<>();

        for (Method m : this.methods()) {
            if (m.isAnnotationPresent(annotation)) {
                methods.add(m);
            }
        }

        return methods;
    }

    @Override
    public @NotNull Set<Method> methods(@NotNull IMatcher<Method> matcher) {
        return this.scanner.lookupMethods(this.SOURCE, matcher);
    }



}
