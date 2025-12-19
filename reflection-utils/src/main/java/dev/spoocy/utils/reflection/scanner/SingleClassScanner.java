package dev.spoocy.utils.reflection.scanner;

import dev.spoocy.utils.common.collections.Collector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class SingleClassScanner implements Scanner {

    @Override
    public @NotNull Set<Class<?>> classes(@NotNull Class<?> source) {
        return new HashSet<>(List.of(source));
    }

    @Override
    public @NotNull Set<Constructor<?>> constructors(@NotNull Class<?> source) {
        return Collector.of(source.getDeclaredConstructors())
                .filter(c -> isVisible(c.getModifiers()))
                .asSet();
    }

    @Override
    public @NotNull Set<Field> fields(@NotNull Class<?> source) {
        return Collector.of(source.getDeclaredFields())
                .filter(f -> isVisible(f.getModifiers()))
                .asSet();
    }

    @Override
    public @NotNull Set<Method> methods(@NotNull Class<?> source) {
        return Collector.of(source.getDeclaredMethods())
                .filter(m -> isVisible(m.getModifiers()))
                .asSet();
    }

}
