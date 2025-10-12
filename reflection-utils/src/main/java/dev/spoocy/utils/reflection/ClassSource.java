package dev.spoocy.utils.reflection;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface ClassSource {

    static ClassSource empty() {
        return fromMap(Collections.emptyMap());
    }

    static ClassSource fromClassLoader() {
        return fromClassLoader(ClassSource.class.getClassLoader());
    }

    static ClassSource fromPackage(String packageName) {
        return fromClassLoader().usingPackage(packageName);
    }

    static ClassSource fromClassLoader(final ClassLoader loader) {
        return name -> {

            try {
                return Optional.of(loader.loadClass(name));
            } catch (ClassNotFoundException ignored) {
                return Optional.empty();
            }

        };
    }

    static ClassSource fromMap(final Map<String, Class<?>> map) {
        return name -> Optional.ofNullable(map.get(name));
    }

    Optional<Class<?>> loadClass(String name);

    default ClassSource usingPackage(final String packageName) {
        return canonicalName -> this.loadClass(appendPackageName(packageName, canonicalName));
    }

    private static String appendPackageName(String a, String b) {
        boolean left = a.endsWith(".");
        boolean right = b.endsWith(".");

        if (left && right) {
            return a.substring(0, a.length() - 1) + b;
        }

        if (left != right) {
            return a + b;
        }

        return a + "." + b;
    }

}
