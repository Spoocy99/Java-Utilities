package dev.spoocy.utils.common.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */
public interface Cache<K, V> extends Iterable<Map.Entry<K, V>> {

    boolean contains(@NotNull K key);

    void add(@NotNull K key, @Nullable V value);

    void addAll(@NotNull Map<? extends K, ? extends V> map);

    void remove(@NotNull K key);

    V get(@NotNull K key);

    V getOrDefault(@NotNull K key, @Nullable V defaultValue);

    V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

    int size();

    boolean isEmpty();

    Set<K> keys();

    Set<V> values();

    void clear();

}
