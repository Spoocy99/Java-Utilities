package dev.spoocy.utils.common.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> map;

    public LRUCache(int capacity) {
        this.capacity = capacity;

        this.map = Collections.synchronizedMap(new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        });


        Caches.register(this);
    }

    @Override
    public boolean contains(@NotNull K key) {
        return this.map.containsKey(key);
    }

    @Override
    public void add(@NotNull K key, @Nullable V value) {
        this.map.put(key, value);
    }

    @Override
    public void addAll(@NotNull Map<? extends K, ? extends V> map) {
        this.map.putAll(map);
    }

    @Override
    public void remove(@NotNull K key) {
        this.map.remove(key);
    }

    @Override
    public V get(@NotNull K key) {
        return this.map.get(key);
    }

    @Override
    public V getOrDefault(@NotNull K key, @Nullable V defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.map.computeIfAbsent(key, mappingFunction);
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
        return Set.copyOf(this.map.values());
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
        return this.map.entrySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder("LRUCache{");
        c.append("capacity=").append(capacity).append(", ");
        for(K key : keys()) {
            c.append(key).append("=").append(get(key)).append(", ");
        }
        return c + "}";
    }

}

