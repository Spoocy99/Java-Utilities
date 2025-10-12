package dev.spoocy.utils.reflection.scanner;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.ClassWalker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class InheritedScanner extends MultiClassScanner {

    @Override
    public boolean isVisible(int modifiers) {
        return true;
    }

    @Override
    public @NotNull Set<Class<?>> classes(@NotNull Class<?> source) {
        return Collector.of(ClassWalker.walk(source).iterator()).asSet();
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
