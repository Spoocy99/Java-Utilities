package dev.spoocy.utils.common.misc;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Args {

    /**
     * Validates that the provided expression is true. If the expression is false, an
     * {@link IllegalArgumentException} is thrown with the specified message.
     *
     * <pre>
     * Args.require(true, "")             = OK
     * Args.require(false, "Is false")    = IllegalArgumentException: Is false
     * </pre>
     *
     * @param expression the boolean expression to check
     * @param message    the {@link String} exception message if invalid, not null
     *
     * @throws IllegalArgumentException if the expression is false
     */
    public static void require(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that the provided expression is true. If the expression is false, an
     * {@link IllegalArgumentException} is thrown with the specified formatted message.
     *
     * <pre>
     * Args.require(true, "Value: %d", 5)     = OK
     * Args.require(false, "Value: %d", 5)    = IllegalArgumentException: Value: 5
     * </pre>
     *
     * @param expression the boolean expression to check
     * @param message    the {@link String} exception message if invalid, not null
     * @param args       the arguments for the formatted message
     *
     * @throws IllegalArgumentException if the expression is false
     * @see String#format(String, Object...)
     */
    public static void require(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    /**
     * Checks that the specified argument is not null.
     *
     * <pre>
     * Args.requireNotNull("value", "name must not be null")    = OK
     * Args.requireNotNull(null, "name must not be null")       = NullPointerException: name must not be null
     * </pre>
     *
     * @param argument the argument to check
     * @param message  the message to include in the exception if the argument is null
     *
     * @throws NullPointerException if the argument is null
     */
    public static void requireNotNull(final Object argument, final String message) {
        if (argument == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Checks that the specified argument is not null.
     *
     * <pre>
     * Args.notNull("value", "name")    = "value"
     * Args.notNull(null, "name")       = NullPointerException: name must not be null
     * </pre>
     *
     * @param argument the argument to check
     * @param name     the name of the argument
     *
     * @return The validated argument if not null.
     *
     * @throws NullPointerException if the argument is null
     */
    public static <T> T notNull(final T argument, final String name) {
        if (argument == null) {
            throw nullPointerException(name);
        }
        return argument;
    }

    /**
     * Checks that the given value is within the specified inclusive range.
     *
     * <pre>
     * Args.inRange(5, 1, 10, "Value")    = 5
     * Args.inRange(0, 1, 10, "Value")    = IllegalArgumentException: Value: 0 is out of range [1, 10]
     * Args.inRange(11, 1, 10, "Value")   = IllegalArgumentException: Value: 11 is out of range [1, 10]
     * </pre>
     *
     * @param value         the value to check
     * @param lowInclusive  the lower bound (inclusive)
     * @param highInclusive the upper bound (inclusive)
     * @param message       the message to include in the exception if the value is out of range
     *
     * @return The validated value if within range.
     *
     * @throws IllegalArgumentException if the value is out of range
     */
    public static int inRange(
            final int value, final int lowInclusive,
            final int highInclusive,
            final String message
    ) {

        if (value < lowInclusive || value > highInclusive) {
            throw illegalArgumentException("%s: %d is out of range [%d, %d]", message, value, lowInclusive, highInclusive);
        }

        return value;
    }

    /**
     * Checks that the given value is within the specified inclusive range.
     *
     * <pre>
     * Args.inRange(5L, 1L, 10L, "Value")    = 5L
     * Args.inRange(0L, 1L, 10L, "Value")    = IllegalArgumentException: Value: 0 is out of range [1, 10]
     * Args.inRange(11L, 1L, 10L, "Value")   = IllegalArgumentException: Value: 11 is out of range [1, 10]
     * </pre>
     *
     * @param value         the value to check
     * @param lowInclusive  the lower bound (inclusive)
     * @param highInclusive the upper bound (inclusive)
     * @param message       the message to include in the exception if the value is out of range
     *
     * @return The validated value if within range.
     *
     * @throws IllegalArgumentException if the value is out of range
     */
    public static long inRange(
            final long value,
            final long lowInclusive,
            final long highInclusive,
            final String message
    ) {

        if (value < lowInclusive || value > highInclusive) {
            throw illegalArgumentException("%s: %d is out of range [%d, %d]", message, value,
                    lowInclusive, highInclusive);
        }
        return value;
    }

    /**
     * Checks that the specified {@link CharSequence} is not null or empty.
     *
     * <pre>
     * Args.notNullOrEmpty("value", "name")    = "value"
     * Args.notNullOrEmpty("", "name")         = IllegalArgumentException: name must not be empty
     * Args.notNullOrEmpty(null, "name")       = NullPointerException: name must not be null
     * </pre>
     *
     * @param argument the argument to check
     * @param name     the name of the argument
     *
     * @return The validated argument if not null or empty.
     *
     * @throws NullPointerException     if the argument is null
     * @throws IllegalArgumentException if the argument is empty
     */
    public static <T extends CharSequence> T notNullOrEmpty(final T argument, final String name) {
        notNull(argument, name);
        if (isEmpty(argument)) {
            throw illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    /**
     * Checks that the specified {@link Collection} is not null or empty.
     *
     * <pre>
     * Args.notNullOrEmpty(list, "name")        = list
     * Args.notNullOrEmpty(emptyList, "name")   = IllegalArgumentException: name must not be empty
     * Args.notNullOrEmpty(null, "name")        = NullPointerException: name must not be null
     * </pre>
     *
     * @param argument the argument to check
     * @param name     the name of the argument
     *
     * @return The validated argument if not null or empty.
     *
     * @throws NullPointerException     if the argument is null
     * @throws IllegalArgumentException if the argument is empty
     */
    public static <E, T extends Collection<E>> T notEmpty(final T argument, final String name) {
        notNull(argument, name);
        if (isEmpty(argument)) {
            throw illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    /**
     * Checks that the specified object is not null or empty.
     *
     * <pre>
     * Args.notNullOrEmpty(array, "name")        = array
     * Args.notNullOrEmpty(emptyArray, "name")   = IllegalArgumentException: name must not be empty
     * Args.notNullOrEmpty(null, "name")         = NullPointerException: name must not be null
     * </pre>
     *
     * @param argument the argument to check
     * @param name     the name of the argument
     *
     * @return Zhe validated argument if not null or empty.
     *
     * @throws NullPointerException     if the argument is null
     * @throws IllegalArgumentException if the argument is empty
     */
    public static <T> T notEmpty(final T argument, final String name) {
        notNull(argument, name);
        if (isEmpty(argument)) {
            throw illegalArgumentExceptionNotEmpty(name);
        }
        return argument;
    }

    /** Checks that the given integer is not negative.
     *
     * <pre>
     * Args.notNegative(5, "Value")    = 5
     * Args.notNegative(0, "Value")    = 0
     * Args.notNegative(-1, "Value")   = IllegalArgumentException: Value must not be negative: -1
     * </pre>
     *
     * @param n    the integer to check
     * @param name the name of the integer
     *
     * @return The validated integer if not negative.
     *
     * @throws IllegalArgumentException if the integer is negative
     */
    public static int notNegative(final int n, final String name) {
        if (n < 0) {
            throw illegalArgumentException("%s must not be negative: %d", name, n);
        }
        return n;
    }

    /** Checks that the given long is not negative.
     *
     * <pre>
     * Args.notNegative(5L, "Value")    = 5L
     * Args.notNegative(0L, "Value")    = 0L
     * Args.notNegative(-1L, "Value")   = IllegalArgumentException: Value must not be negative: -1
     * </pre>
     *
     * @param n    the long to check
     * @param name the name of the long
     *
     * @return The validated long if not negative.
     *
     * @throws IllegalArgumentException if the long is negative
     */
    public static long notNegative(final long n, final String name) {
        if (n < 0) {
            throw illegalArgumentException("%s must not be negative: %d", name, n);
        }
        return n;
    }

    /** Checks that the given integer is positive (greater than zero).
     *
     * <pre>
     * Args.positive(5, "Value")    = 5
     * Args.positive(0, "Value")    = IllegalArgumentException: Value must not be negative or zero: 0
     * Args.positive(-1, "Value")   = IllegalArgumentException: Value must not be negative or zero: -1
     * </pre>
     *
     * @param n    the integer to check
     * @param name the name of the integer
     *
     * @return The validated integer if positive.
     *
     * @throws IllegalArgumentException if the integer is not positive
     */
    public static int positive(final int n, final String name) {
        if (n <= 0) {
            throw illegalArgumentException("%s must not be negative or zero: %d", name, n);
        }
        return n;
    }

    /** Checks that the given long is positive (greater than zero).
     *
     * <pre>
     * Args.positive(5L, "Value")    = 5L
     * Args.positive(0L, "Value")    = IllegalArgumentException: Value must not be negative or zero: 0
     * Args.positive(-1L, "Value")   = IllegalArgumentException: Value must not be negative or zero: -1
     * </pre>
     *
     * @param n    the long to check
     * @param name the name of the long
     *
     * @return The validated long if positive.
     *
     * @throws IllegalArgumentException if the long is not positive
     */
    public static long positive(final long n, final String name) {
        if (n <= 0) {
            throw illegalArgumentException("%s must not be negative or zero: %d", name, n);
        }
        return n;
    }

    /**
     * Checks if the given object is considered empty.
     * <ul>
     *     <li>null is considered empty</li>
     *     <li>Empty CharSequence (length == 0) is considered empty</li>
     *     <li>Empty Array (length == 0) is considered empty</li>
     *     <li>Empty Collection is considered empty</li>
     *     <li>Empty Map is considered empty</li>
     * </ul>
     *
     * @param object the object to check
     *
     * @return {@code true} if the object is considered empty, {@code false} otherwise.
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }

        if (object.getClass()
                .isArray()) {
            return Array.getLength(object) == 0;
        }

        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }

        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

    private static IllegalArgumentException illegalArgumentException(final String format, final Object... args) {
        return new IllegalArgumentException(String.format(format, args));
    }

    private static IllegalArgumentException illegalArgumentExceptionNotEmpty(final String name) {
        return new IllegalArgumentException(name + " must not be empty");
    }

    private static NullPointerException nullPointerException(final String name) {
        return new NullPointerException(name + " must not be null");
    }

    private Args() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}
