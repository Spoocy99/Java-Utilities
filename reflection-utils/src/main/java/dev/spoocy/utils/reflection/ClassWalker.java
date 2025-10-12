package dev.spoocy.utils.reflection;

import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ClassWalker implements Iterable<Class<?>> {

    public static ClassWalker range(@NotNull Class<?> start, @NotNull Class<?> end) {
        return new ClassWalker(start, end);
    }

    public static ClassWalker walk(@NotNull Class<?> start) {
        return new ClassWalker(start);
    }

    private final Class<?> clazz;
    private final Class<?> end;

    private ClassWalker(@NotNull Class<?> clazz) {
        this(clazz, Object.class);
    }

    private ClassWalker(@NotNull Class<?> clazz, @NotNull Class<?> end) {
        this.clazz = clazz;
        this.end = end;
    }

    @NotNull
    @Override
    public Iterator<Class<?>> iterator() {
        return new Iterator<>() {
            private final Set<Class<?>> done = new HashSet<>();
            private final Deque<Class<?>> work = new LinkedList<>();

            {
                work.addLast(clazz);
                done.add(end);
            }

            @Override
            public boolean hasNext() {
                return !work.isEmpty();
            }

            @Override
            public Class<?> next() {
                Class<?> current = work.removeFirst();
                done.add(current);

                for (Class<?> parent : current.getInterfaces()) {
                    if (!done.contains(parent))
                        work.addLast(parent);
                }

                Class<?> parent = current.getSuperclass();
                if (parent != null && !done.contains(parent)) work.addLast(parent);

                return current;
            }
        };
    }
}
