package dev.spoocy.utils.reflection.scanner;

import com.google.common.collect.Sets;
import dev.spoocy.utils.common.misc.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ForceScanner extends SingleClassScanner {

    @Override
    public boolean isVisible(int modifiers) {
        return true;
    }

    @Override
    public @NotNull Set<Constructor<?>> constructors(@NotNull Class<?> source) {
        return ListUtils.toSet(source.getDeclaredConstructors());
    }

    @Override
    public @NotNull Set<Field> fields(@NotNull Class<?> source) {
        return ListUtils.toSet(source.getDeclaredFields());
    }

    @Override
    public @NotNull Set<Method> methods(@NotNull Class<?> source) {
        return ListUtils.toSet(source.getDeclaredMethods());
    }

    @Override
    public @Nullable Constructor<?> lookupConstructor(@NotNull Class<?> source, @NotNull Class<?>... parameters) {
        try {
            return source.getDeclaredConstructor(parameters);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }
}
