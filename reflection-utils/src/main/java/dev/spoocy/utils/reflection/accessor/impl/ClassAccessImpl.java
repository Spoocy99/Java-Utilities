package dev.spoocy.utils.reflection.accessor.impl;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.accessor.*;
import dev.spoocy.utils.reflection.matcher.IMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassAccessImpl implements ClassAccess {

    private final ReflectionClass reflectionClass;

    public ClassAccessImpl(@NotNull ReflectionClass reflectionClass) {
        this.reflectionClass = reflectionClass;
    }

    @Override
    public ReflectionClass reflections() {
        return this.reflectionClass;
    }

    @Override
    public Set<ConstructorAccessor> constructors() {
        return Collector.of(this.reflectionClass.constructors())
                .map(ClassAccessImpl::mapOrNull)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public ConstructorAccessor constructor(@NotNull Class<?>... parameters) {
        return mapOrNull(this.reflectionClass.constructor(parameters));
    }

    @Override
    public Set<FieldAccessor> constants() {
        return Collector.of(this.reflectionClass.constants())
                .map(ClassAccessImpl::mapOrNullField)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<FieldAccessor> fields() {
        return Collector.of(this.reflectionClass.fields())
                .map(ClassAccessImpl::mapOrNullField)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<FieldAccessor> fieldsWithAnnotation(@NotNull Class<? extends Annotation> annotation) {
        return Collector.of(this.reflectionClass.fields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .map(ClassAccessImpl::mapOrNullField)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<FieldAccessor> fields(@NotNull IMatcher<Field> matcher) {
        return Collector.of(this.reflectionClass.fields())
                .filter(o -> matcher.isMatch(o, reflectionClass.getAccessedClass()))
                .map(ClassAccessImpl::mapOrNullField)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<MethodAccessor> methods() {
        return Collector.of(this.reflectionClass.methods())
                .map(ClassAccessImpl::mapOrNullMethod)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<MethodAccessor> methodsWithAnnotation(@NotNull Class<? extends Annotation> annotation) {
        return Collector.of(this.reflectionClass.methods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .map(ClassAccessImpl::mapOrNullMethod)
                .filter(Objects::nonNull)
                .asSet();
    }

    @Override
    public Set<MethodAccessor> methods(@NotNull IMatcher<Method> matcher) {
        return Collector.of(this.reflectionClass.methods())
                .filter(o -> matcher.isMatch(o, reflectionClass.getAccessedClass()))
                .map(ClassAccessImpl::mapOrNullMethod)
                .filter(Objects::nonNull)
                .asSet();
    }

    private static ConstructorAccessor mapOrNull(@Nullable Constructor<?> constructor) {
        return constructor == null ? null : Accessor.getConstructor(constructor);
    }

    private static FieldAccessor mapOrNullField(@Nullable Field field) {
        return field == null ? null : Accessor.getField(field);
    }

    private static MethodAccessor mapOrNullMethod(@Nullable Method method) {
        return method == null ? null : Accessor.getMethod(method);
    }
}
