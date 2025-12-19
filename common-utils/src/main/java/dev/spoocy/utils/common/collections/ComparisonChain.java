package dev.spoocy.utils.common.collections;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Utility class to perform a chain of comparisons fluently.
 *
 * @author Spoocy99 | GitHub: Spoocy99
 */
public abstract class ComparisonChain {

    /**
     * Starts a new comparison chain.
     *
     * @return A new ComparisonChain instance.
     */
    public static ComparisonChain start() {
        return ACTIVE;
    }

    /**
     * Compares two comparable objects as specified by {@link Comparable#compareTo}, <i>if</i> the
     * result of this comparison chain has not already been determined.
     *
     * @param left       the first object to compare
     * @param right      the second object to compare
     * @param comparator the comparator to use for comparison
     * @param <T>        the type of the objects being compared
     *
     * @return The updated ComparisonChain instance.
     *
     * @throws NullPointerException if an argument is null and this
     *         comparator does not permit null arguments
     * @throws ClassCastException if the arguments' types prevent them from
     *         being compared by this comparator.
     */
    public abstract <T extends @Nullable Object> ComparisonChain compare(
            @Nullable T left,
            @Nullable T right,
            Comparator<T> comparator
    );

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     *
     * @param left  the first integer to compare
     * @param right the second integer to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compare(int left, int right);

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     *
     * @param left  the first long to compare
     * @param right the second long to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compare(long left, long right);

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     *
     * @param left  the first float to compare
     * @param right the second float to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compare(float left, float right);

    /**
     * Compares two objects using a comparator, <i>if</i> the result of this comparison chain has not
     * already been determined.
     *
     * @param left  the first double to compare
     * @param right the second double to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compare(double left, double right);

    /**
     * Compares two boolean values, treating true as less than false, <i>if</i> the result of this
     * comparison chain has not already been determined.
     *
     * @param left  the first boolean to compare
     * @param right the second boolean to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public final ComparisonChain compare(Boolean left, Boolean right) {
        return compareFalseFirst(left, right);
    }

    /**
     * Compares two boolean values, treating true as less than false, <i>if</i> the result of this
     * comparison chain has not already been determined.
     *
     * @param left  the first boolean to compare
     * @param right the second boolean to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compareTrueFirst(boolean left, boolean right);

    /**
     * Compares two boolean values, treating false as less than true, <i>if</i> the result of this
     * comparison chain has not already been determined.
     *
     * @param left  the first boolean to compare
     * @param right the second boolean to compare
     *
     * @return The updated ComparisonChain instance.
     */
    public abstract ComparisonChain compareFalseFirst(boolean left, boolean right);

    /**
     * Ends this comparison chain and returns its result: a value having the same sign as the first
     * nonzero comparison result in the chain, or zero if every result was zero.
     *
     * @return {@code <0} or {@code >0} depending on whether the first
     * nonzero comparison result in the chain, or {@code 0} if all results were zero.
     */
    public abstract int result();


    private static final ComparisonChain ACTIVE = new ComparisonChain() {

        @Override
        public <T extends @Nullable Object> ComparisonChain compare(
                @Nullable T left,
                @Nullable T right,
                Comparator<T> comparator
        ) {
            return append(comparator.compare(left, right));
        }

        @Override
        public ComparisonChain compare(int left, int right) {
            return append(Integer.compare(left, right));
        }

        @Override
        public ComparisonChain compare(long left, long right) {
            return append(Long.compare(left, right));
        }

        @Override
        public ComparisonChain compare(float left, float right) {
            return append(Float.compare(left, right));
        }

        @Override
        public ComparisonChain compare(double left, double right) {
            return append(Double.compare(left, right));
        }

        @Override
        public ComparisonChain compareTrueFirst(boolean left, boolean right) {
            return append(Boolean.compare(!left, !right));
        }

        @Override
        public ComparisonChain compareFalseFirst(boolean left, boolean right) {
            return append(Boolean.compare(left, right));
        }

        @Override
        public int result() {
            return 0;
        }

        ComparisonChain append(int result) {
            return (result < 0) ? LESS :
                    ((result > 0) ? GREATER : ACTIVE);
        }
    };

    private static final ComparisonChain LESS = new Inactive(-1);
    private static final ComparisonChain GREATER = new Inactive(1);

    private static final class Inactive extends ComparisonChain {

        private final int result;

        private Inactive(int result) {
            this.result = result;
        }

        @Override
        public <T> ComparisonChain compare(@Nullable T left, @Nullable T right, Comparator<T> comparator) {
            return this;
        }

        @Override
        public ComparisonChain compare(int left, int right) {
            return this;
        }

        @Override
        public ComparisonChain compare(long left, long right) {
            return this;
        }

        @Override
        public ComparisonChain compare(float left, float right) {
            return this;
        }

        @Override
        public ComparisonChain compare(double left, double right) {
            return this;
        }

        @Override
        public ComparisonChain compareTrueFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public ComparisonChain compareFalseFirst(boolean left, boolean right) {
            return this;
        }

        @Override
        public int result() {
            return this.result;
        }
    }

}
