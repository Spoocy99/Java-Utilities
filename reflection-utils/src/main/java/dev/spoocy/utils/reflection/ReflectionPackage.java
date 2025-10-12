package dev.spoocy.utils.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class to manage and cache classes within a
 * specific package using a given ClassSource.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ReflectionPackage {

    private final String packageName;
    private final ClassSource source;
    private final ConcurrentHashMap<String, Optional<Class<?>>> cache;

    public ReflectionPackage(@NotNull String packageName, @NotNull ClassSource source) {
        this.source = source;
        this.packageName = packageName;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Manually set or remove a cached class in this package.
     * If clazz is null, the cache entry will be removed.
     * If clazz is not null, it will be cached.
     * This can be used to pre-load classes or clear cache entries.
     *
     * @param className the simple name of the class (without package)
     * @param clazz the class to cache, or null to remove from cache
     */
    public void setPackageClass(@NotNull String className, @Nullable Class<?> clazz) {
        if (clazz != null) {
            this.cache.put(className, Optional.of(clazz));
        } else {
            this.cache.remove(className);
        }
    }

    /**
     * Resolve a class by its simple name within the package.
     * If the class is not found, returns Optional.empty().
     *
     * @param className the simple name of the class (without package)
     * @return an Optional containing the Class if found, or {@link Optional#empty()} if not found
     */
     private Optional<Class<?>> resolveClass(String className) {
        return source.loadClass(combine(packageName, className));
    }

    /**
     * Get a class by its simple name within the package, with optional aliases.
     * The result is cached for future lookups.
     * If the class is not found under any of the provided names, returns Optional.empty().
     *
     * @param className the primary simple name of the class (without package)
     * @param aliases optional alternative simple names to try if the primary name is not found
     *
     * @return an Optional containing the Class if found, or {@link Optional#empty()} if not found
     */
    public Optional<Class<?>> getPackageClass(String className, String... aliases) {
        return cache.computeIfAbsent(className, x -> {
            Optional<Class<?>> clazz = resolveClass(className);
            if (clazz.isPresent()) {
                return clazz;
            }

            for (String alias : aliases) {
                clazz = resolveClass(alias);
                if (clazz.isPresent()) {
                    return clazz;
                }
            }

            return Optional.empty();
        });
    }

    /**
     * Combines a package name and class name into a fully qualified class name.
     * If the package name is null or empty, returns just the class name.
     *
     * @param packageName the package name, or null/empty for default package
     * @param className the simple class name (without package)
     *
     * @return the fully qualified class name
     */
    public static String combine(@Nullable String packageName, @NotNull String className) {
        if (packageName == null || packageName.isEmpty()) {
            return className;
        } else {
            return packageName + "." + className;
        }
    }

}
