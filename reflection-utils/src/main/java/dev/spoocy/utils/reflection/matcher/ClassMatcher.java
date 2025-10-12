package dev.spoocy.utils.reflection.matcher;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Getter
public final class ClassMatcher implements IMatcher<Class<?>> {

    public static final ClassMatcher MATCH_ALL = new ClassMatcher(null, MatchType.MATCH_SUPER);

    private final Class<?> clazz;
    private final MatchType match;

    public ClassMatcher(@Nullable Class<?> clazz, @NotNull MatchType type) {
        this.clazz = clazz;
        this.match = type;
    }

    @Override
    public boolean isMatch(final @NotNull Class<?> value, final Object parent) {

        if (this.clazz == null) {
            return match != MatchType.MATCH_EXACT;
        }

        switch (match) {
            case MATCH:
                return this.clazz.isAssignableFrom(value);
            case MATCH_EXACT:
                return this.clazz.equals(value);
            case MATCH_SUPER:
                return value.isAssignableFrom(this.clazz);
        }

        return false;
    }

    public enum MatchType {
        /**
         * The provided class must be assignable from the value class.
         * E.g., if the provided class is an interface, the value class must implement it.
         * <p>
         * If the provided class is a superclass, the value class must extend it.
         * This is the most flexible matching type.
         * E.g., List.class matches ArrayList.class, LinkedList.class, etc.
         * <p>
         *
         * If the provided class is null, this type will never match.
         */
        MATCH,


        /**
         * The provided class must be exactly the same as the value class.
         * This is the most strict matching type.
         * E.g., List.class only matches List.class.
         * <p>
         * If the provided class is null, this type will never match.
         */
        MATCH_EXACT,

        /**
         * The value class must be assignable from the provided class.
         * E.g., if the provided class is an interface, the value class must implement it.
         * <p>
         * If the provided class is a superclass, the value class must extend it.
         * This is the reverse of MATCH.
         * E.g., ArrayList.class matches List.class, Collection.class, Object.class, etc.
         * <p>
         * If the provided class is null, this type will always match.
         */
        MATCH_SUPER
    }

}
