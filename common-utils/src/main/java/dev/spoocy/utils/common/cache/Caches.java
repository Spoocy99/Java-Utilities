package dev.spoocy.utils.common.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Caches {

    private static final Set<Cache<?, ?>> CACHES = ConcurrentHashMap.newKeySet();

    public static void register(@NotNull Cache<?, ?> cache) {
        CACHES.add(cache);
    }

    public static void clearAll() {
        for (Cache<?, ?> cache : CACHES) {
            cache.clear();
        }
    }

    public static <K, V> Cache<K, V> createCache() {
        return new NormalCache<>();
    }

    public static <K, V> Cache<K, V> createLRUCache(int maxSize) {
        return new LRUCache<>(maxSize);
    }

    public static <K, V> Cache<K, V> createTimedCache(long durationMillis) {
        return new TimedCache<>(durationMillis);
    }


    private Caches() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
