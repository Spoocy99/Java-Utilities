package dev.spoocy.utils.config.update;

import dev.spoocy.utils.common.version.Version;
import dev.spoocy.utils.config.update.match.ExactMatcher;
import dev.spoocy.utils.config.update.match.GreaterMatcher;
import dev.spoocy.utils.config.update.match.LowerMatcher;
import dev.spoocy.utils.config.update.match.WildcardMatcher;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface VersionMatcher {

    /**
     * Determines whether the given version matches specific criteria.
     *
     * @param version The version to be checked; must not be null.
     *
     * @return {@code true} if the version matches the criteria defined by this matcher, {@code false} otherwise.
     */
    boolean matches(@NotNull Version version);

    /**
     * Indicates whether this version matcher represents an exact version match.
     *
     * @return {@code true} if this matcher represents a specific, exact version; {@code false} otherwise.
     */
    boolean isExact();

    /**
     * Provides a textual description of the version matcher criteria.
     *
     * @return A non-null string representing the description of the version matcher's behavior.
     */
    @NotNull
    String describe();

    /**
     * A predefined implementation of the {@link VersionMatcher} interface that matches any version.
     */
    VersionMatcher ANY = new VersionMatcher() {

        @Override
        public boolean matches(@NotNull Version version) {
            return true;
        }

        @Override
        public boolean isExact() {
            return false;
        }

        @Override
        public @NotNull String describe() {
            return "Any Version";
        }
    };

    static boolean equalsVersion(@NotNull Version version, @NotNull Version other) {
        return version.compareTo(other) == 0;
    }

    /**
     * Creates a matcher for a single exact version.
     *
     * @param version exact version to match
     *
     * @return exact matcher
     */
    @NotNull
    static VersionMatcher exact(@NotNull Version version) {
        return new ExactMatcher(version);
    }

    /**
     * Creates a matcher that matches versions greater than the specified version.
     *
     * @param version The version to compare against; must not be null.
     *
     * @return A version matcher that matches versions greater than the specified version.
     */
    @NotNull
    static VersionMatcher above(@NotNull Version version) {
        return new GreaterMatcher(version);
    }

    /**
     * Creates a matcher that matches versions lower than the specified version.
     *
     * @param version The version to compare against; must not be null.
     *
     * @return A version matcher that matches versions lower than the specified version.
     */
    @NotNull
    static VersionMatcher below(@NotNull Version version) {
        return new LowerMatcher(version);
    }

    /**
     * Creates a matcher from a human-friendly pattern.
     * Supported patterns:
     * - exact version, e.g. {@code 1.2.0}
     * - wildcard major/minor/build, e.g. {@code 1.x.x}, {@code 1.2.x}
     * - any version: {@code *}, {@code x}, {@code any}
     *
     * @param pattern version pattern
     *
     * @return parsed matcher
     */
    @NotNull
    static VersionMatcher parse(@NotNull String pattern) {
        String normalized = pattern.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("pattern cannot be empty");
        }

        String lower = normalized.toLowerCase();
        if ("*".equals(lower) || "x".equals(lower) || "any".equals(lower)) {
            return ANY;
        }

        String[] parts = normalized.split("\\.");
        if (parts.length == 0 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid version pattern: " + pattern);
        }

        boolean containsWildcard = false;
        for (String part : parts) {
            String lowerPart = part.toLowerCase();
            if ("x".equals(lowerPart) || "*".equals(lowerPart)) {
                containsWildcard = true;
                break;
            }
        }

        if (!containsWildcard) {
            return exact(Version.parse(normalized));
        }

        if ("x".equalsIgnoreCase(parts[0]) || "*".equals(parts[0])) {
            throw new IllegalArgumentException("Major version cannot be a wildcard: " + pattern);
        }

        int major = Integer.parseInt(parts[0]);
        Integer minor = null;
        Integer build = null;

        if (parts.length > 1 && !"x".equalsIgnoreCase(parts[1]) && !"*".equals(parts[1])) {
            minor = Integer.parseInt(parts[1]);
        }

        if (parts.length > 2 && !"x".equalsIgnoreCase(parts[2]) && !"*".equals(parts[2])) {
            build = Integer.parseInt(parts[2]);
        }

        return new WildcardMatcher(major, minor, build, normalized);
    }

}
