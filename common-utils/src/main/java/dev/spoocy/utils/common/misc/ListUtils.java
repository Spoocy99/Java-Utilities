package dev.spoocy.utils.common.misc;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class ListUtils {

    private ListUtils() { }

    public static <T> T[] toArray(@NotNull List<T> list) {
        return (T[]) list.toArray();
    }

    public static <T> Set<T> toSet(@NotNull T[] list) {
        Set<T> set = new LinkedHashSet<>();
        Collections.addAll(set, list);
        return set;
    }

    public static <T> Set<T> toSet(@NotNull Iterator<T> list) {
        Set<T> set = new LinkedHashSet<>();
        while (list.hasNext()) {
            set.add(list.next());
        }
        return set;
    }

    @SafeVarargs
    public static <T> Set<T> combineArrays(T[]... arrays) {
        Set<T> result = new LinkedHashSet<>();

        for (T[] elements : arrays) {
            if (elements == null) {
                continue;
            }
            Collections.addAll(result, elements);
        }

        return result;
    }

    @SafeVarargs
    public static <T> T[] toCombinedArray(@NotNull List<T>... lists) {
        Set<T> result = new LinkedHashSet<>();

        for (List<T> list : lists) {
            result.addAll(list);
        }

        return (T[]) result.toArray();
    }

    @SafeVarargs
    public static <T> List<T> combineLists(@NotNull List<T>... lists) {
        List<T> result = new ArrayList<>();

        for (List<T> list : lists) {
            result.addAll(list);
        }

        return result;
    }

    @SafeVarargs
    public static <T> List<T> addToList(@NotNull List<T> list, @NotNull T... elements) {
        Collections.addAll(list, elements);
        return list;
    }



}
