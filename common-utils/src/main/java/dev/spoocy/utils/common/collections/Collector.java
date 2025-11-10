package dev.spoocy.utils.common.collections;

import com.google.common.collect.ImmutableList;
import dev.spoocy.utils.common.misc.SeededRandom;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Collector<T> implements Iterable<T>, Cloneable {

    public static <T> Collector<T> of(@NotNull Collection<T> collection) {
        return new Collector<>(collection);
    }

    public static <T> Collector<T> of(@NotNull List<T> list) {
        return new Collector<>(list);
    }

    @SafeVarargs
    public static <T> Collector<T> of(T... values) {
        return new Collector<>(Arrays.asList(values));
    }

    public static <T> Collector<T> of(@NotNull Stream<T> stream) {
        return new Collector<>(stream.collect(Collectors.toList()));
    }

    public static <T> Collector<T> of(@NotNull Iterator<T> values) {
        return new Collector<>(values);
    }

    public static Collector<Integer> range(int start, int end) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < end; i++) {
            list.add(i);
        }
        return new Collector<>(list);
    }

    private final List<T> list;

    public Collector() {
        this.list = new ArrayList<>();
    }

    public Collector(@NotNull Collection<T> collection) {
        this.list = new ArrayList<>(collection);
    }

    public Collector(@NotNull List<T> list) {
        this.list = new ArrayList<>(list);
    }

    public Collector(@NotNull Iterator<T> i) {
        this.list = new ArrayList<>();
        i.forEachRemaining(list::add);
    }

    @SafeVarargs
    public final Collector<T> add(T... values) {
        list.addAll(Arrays.asList(values));
        return this;
    }

    public Collector<T> add(@NotNull List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public Collector<T> add(@NotNull Collector<T> collector) {
        list.addAll(collector.list);
        return this;
    }

    public Collector<T> remove(T value) {
        list.remove(value);
        return this;
    }

    public Collector<T> remove(int index) {
        list.remove(index);
        return this;
    }

    public Collector<T> filter(@NotNull Predicate<T> filter) {
        list.removeIf(value -> !filter.test(value));
        return this;
    }

    public T get(int index) {
        return list.get(index);
    }

    public T first() {
        return list.get(0);
    }

    public Optional<T> first(@NotNull Function<T, Boolean> filter) {
        for (T t : list) {
            if (filter.apply(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Optional<T> findFirst() {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Collector<T> removeFirst() {
        list.remove(0);
        return this;
    }

    public Collector<T> removeFirst(int count) {
        if (count > 0) {
            list.subList(0, count).clear();
        }
        return this;
    }

    public T last() {
        return list.get(list.size() - 1);
    }

    public Optional<T> last(@NotNull Function<T, Boolean> filter) {
        ListIterator<T> iterator = list.listIterator(list.size());
        while (iterator.hasPrevious()) {
            T t = iterator.previous();
            if (filter.apply(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public Optional<T> findLast() {
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() - 1));
    }

    public Collector<T> removeLast() {
        list.remove(list.size() - 1);
        return this;
    }

    public Collector<T> removeLast(int count) {
        for (int i = 0; i < count; i++) {
            list.remove(list.size() - 1);
        }
        return this;
    }

    public Optional<T> max(@NotNull ToIntFunction<T> function) {
        return stream().max(Comparator.comparingInt(function));
    }

    public Optional<T> max(@NotNull Comparator<? super T> comparator) {
        return stream().max(comparator);
    }

    public T random() {
        return list.get((int) (Math.random() * list.size()));
    }

    public T random(@NotNull SeededRandom random) {
        return random.choose(list);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int count() {
        return list.size();
    }

    public Collector<T> clear() {
        list.clear();
        return this;
    }


    public Collector<T> order(@NotNull Function<T, ? extends Comparable<?>> sort) {
        return order(sort, true);
    }

    public Collector<T> order(@NotNull Function<T, ? extends Comparable<?>> sort, boolean ascending) {
        list.sort((a, b) -> {
            Comparable<Object> v1 = (Comparable<Object>) sort.apply(a);
            Comparable<Object> v2 = (Comparable<Object>) sort.apply(b);
            return ascending ? v1.compareTo(v2) : v2.compareTo(v1);
        });
        return this;
    }

    public Collector<T> slice(int from, int to) {
        return new Collector<>(list.subList(from, Math.min(to, list.size())));
    }

    public <R> Collector<R> map(@NotNull Function<? super T, ? extends R> mapper) {
        final List<R> result = new ArrayList<>(list.size());
        for (T t : list) {
            result.add(mapper.apply(t));
        }
        return new Collector<>(result);
    }

    public boolean anyMatch(@NotNull Predicate<T> filter) {
        for (T t : list) {
            if (filter.test(t)) {
                return true;
            }
        }
        return false;
    }

    public Collector<T> clone() {
        return new Collector<>(list);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.list.iterator();
    }

    public boolean allMatch(@NotNull Function<T, Boolean> filter) {
        for (T t : list) {
            if (!filter.apply(t)) {
                return false;
            }
        }
        return true;
    }

    public Set<T> asSet() {
        return new HashSet<>(this.list);
    }

    public Set<T> asLinkedSet() {
        return new LinkedHashSet<>(this.list);
    }

    public List<T> asList() {
        return new ArrayList<>(this.list);
    }

    public <V> List<V> asList(Class<? extends V> type) {
        List<V> result = new ArrayList<>();
        for (T element : list) {
            if (type.isInstance(element)) {
                result.add(type.cast(element));
            }
        }
        return result;
    }

    public List<T> asImmutableList() {
        return ImmutableList.copyOf(this.list);
    }

    public Object[] asArray() {
        return this.list.toArray();
    }

    public T[] asArray(@NotNull Class<T> type) {
        T[] array = (T[]) Array.newInstance(type, list.size());
        return this.list.toArray(array);
    }

    public T[] asArray(@NotNull T[] array) {
        return this.list.toArray(array);
    }

    public T[] asArray(@NotNull IntFunction<T[]> generator) {
        return this.list.toArray(generator);
    }

    public int[] asIntArray() {
        return this.list.stream()
                .mapToInt(value -> (int) value)
                .toArray();
    }

    public Stream<T> stream() {
        return this.list.stream();
    }

    public static <T> Collector<T> flatten(@NotNull Collection<? extends Collection<T>> collections) {
        List<T> flatList = new ArrayList<>();
        for (Collection<T> collection : collections) {
            flatList.addAll(collection);
        }
        return new Collector<>(flatList);
    }

    public static <T, O> List<T> mapList(@NotNull Collection<O> collection, @NotNull Function<O, T> function) {
        return collection.stream()
                .map(function)
                .collect(Collectors.toList());
    }

    public static <F extends T, T> List<T> castList(@NotNull Collection<F> collection, @NotNull Class<T> castTo) {
        return collection.stream()
                .filter(castTo::isInstance)
                .map(castTo::cast)
                .collect(Collectors.toList());
    }

}
