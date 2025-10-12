package dev.spoocy.utils.reflection.scanner;

import com.google.common.collect.Sets;
import dev.spoocy.utils.common.collections.Collector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public abstract class MultiClassScanner implements Scanner{

    @Override
    public abstract @NotNull Set<Class<?>> classes(@NotNull Class<?> source);

    @Override
    public @NotNull Set<Constructor<?>> constructors(@NotNull Class<?> source) {
        return compute(this.classes(source), Class::getDeclaredConstructors)
                .filter(c -> isVisible(c.getModifiers()))
                .asSet();
    }

    @Override
    public @NotNull Set<Field> fields(@NotNull Class<?> source) {
        return compute(this.classes(source), Class::getDeclaredFields)
                .filter(f -> isVisible(f.getModifiers()))
                .asSet();
    }

    @Override
    public @NotNull Set<Method> methods(@NotNull Class<?> source) {
        return compute(this.classes(source), Class::getDeclaredMethods)
                .filter(m -> isVisible(m.getModifiers()))
                .asSet();
    }

    private static <B, T> Collector<T> compute(@NotNull Set<B> bases, @NotNull Function<B, T[]> function) {
        List<T> list = new ArrayList<>();

        for (B base : bases) {
            Collections.addAll(list, function.apply(base));
        }

        return Collector.of(list);
    }

}
