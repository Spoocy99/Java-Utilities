package dev.spoocy.utils.reflection.scanner;

import dev.spoocy.utils.common.collections.Collector;
import dev.spoocy.utils.reflection.matcher.IMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface Scanner {
    /**
     * A scanner that only scans declared members (excluding inherited ones).
     */
    Scanner PUBLIC_DECLARED = new BasicScanner();

    /**
     * A scanner that scans all members, including inherited ones.
     */
    Scanner FORCE_ACCESS = new ForceScanner();

    /**
     * A scanner that scans all members, including inherited ones.
     */
    Scanner INHERITED = new InheritedScanner();

    /**
     * Determines if a member with the given modifiers is considered visible.
     *
     * @param modifiers The modifiers of the member (e.g., public, private, protected).
     *
     * @return {@code true} if the member is visible according to the scanner's criteria, {@code false} otherwise.
     */
    boolean isVisible(int modifiers);

    @NotNull
    Set<Class<?>> classes(@NotNull Class<?> source);

    @NotNull
    Set<Constructor<?>> constructors(@NotNull Class<?> source);

    @NotNull
    Set<Field> fields(@NotNull Class<?> source);

    @NotNull
    Set<Method> methods(@NotNull Class<?> source);

    @Nullable
    default Constructor<?> lookupConstructor(@NotNull Class<?> source, @NotNull Class<?>... parameters) {
        try {
            Constructor<?> constructor = source.getDeclaredConstructor(parameters);
            return isVisible(constructor.getModifiers()) ? constructor : null;
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    @NotNull
    default Set<Method> lookupMethods(@NotNull Class<?> source, @NotNull IMatcher<Method> matcher) {
        return Collector.of(this.methods(source))
                .filter(o -> matcher.isMatch(o, source))
                .asSet();
    }

    @NotNull
    default Set<Field> lookupFields(@NotNull Class<?> source, @NotNull IMatcher<Field> matcher) {
        return Collector.of(this.fields(source))
                .filter(o -> matcher.isMatch(o, source))
                .asSet();
    }

}
