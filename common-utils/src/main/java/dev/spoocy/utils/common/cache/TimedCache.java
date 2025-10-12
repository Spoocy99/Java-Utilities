package dev.spoocy.utils.common.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class TimedCache<K, V> implements Cache<K, V> {

    private final Map<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public TimedCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
        Caches.register(this);
    }

    @Nullable
    private CacheEntry<V> getEntry(K key) {
        CacheEntry<V> entry = this.map.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() > entry.getExpiryTime()) {
            this.map.remove(key);
            return null;
        }
        return entry;
    }

    @Getter
    @AllArgsConstructor
    private static class CacheEntry<V> {
        final @Nullable V value;
        final long expiryTime;
    }

    @Override
    public boolean contains(@NotNull K key) {
        return this.map.containsKey(key);
    }

    @Override
    public void add(@NotNull K key, @Nullable V value) {
        this.map.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }

    @Override
    public void addAll(@NotNull Map<? extends K, ? extends V> map) {
        map.forEach(this::add);
    }

    @Override
    public void remove(@NotNull K key) {
        this.map.remove(key);
    }

    @Override
    public V get(@NotNull K key) {
        CacheEntry<V> entry = getEntry(key);
        return entry != null ? entry.getValue() : null;
    }

    @Override
    public V getOrDefault(@NotNull K key, @Nullable V defaultValue) {
        V value = this.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V value = this.get(key);
        if (value == null) {
            value = mappingFunction.apply(key);
            this.add(key, value);
        }
        return value;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<K> keys() {
        return this.map.keySet();
    }

    @Override
    public Set<V> values() {
        return this.map.values()
                .stream()
                .map(CacheEntry::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
        return this.map.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().getValue()))
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toSet())
                .iterator();
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder("TimedCache{");
        c.append("ttlMillis=").append(ttlMillis).append(", ");
        for(K key : keys()) {
            c.append(key).append("=").append(get(key)).append(", ");
        }
        return c + "}";
    }

}

