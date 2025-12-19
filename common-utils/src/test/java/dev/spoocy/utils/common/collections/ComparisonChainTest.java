package dev.spoocy.utils.common.collections;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class ComparisonChainTest {

    @Test
    public void testCompareInts() {
        int result = ComparisonChain.start()
                .compare(1, 1)
                .compare(2, 3)
                .result();
        assertTrue(result < 0);

        result = ComparisonChain.start()
                .compare(5, 4)
                .compare(1, 10)
                .result();
        assertTrue(result > 0);
    }

    @Test
    public void testCompareLongsAndDoubles() {
        int result = ComparisonChain.start()
                .compare(10L, 10L)
                .compare(1.0, 2.0)
                .result();
        assertTrue(result < 0);

        result = ComparisonChain.start()
                .compare(2.0f, 2.0f)
                .compare(7L, 3L)
                .result();
        assertTrue(result > 0);
    }

    @Test
    public void testCompareBooleansTrueFirstAndFalseFirst() {
        int resultTrueFirst = ComparisonChain.start()
                .compareTrueFirst(true, false)
                .result();
        assertTrue(resultTrueFirst < 0); // true should come before false

        int resultFalseFirst = ComparisonChain.start()
                .compareFalseFirst(true, false)
                .result();
        assertTrue(resultFalseFirst > 0); // false should come before true
    }

    @Test
    public void testCompareWithComparatorAndNulls() {
        Comparator<String> natural = Comparator.nullsFirst(String::compareTo);

        int result = ComparisonChain.start()
                .compare((String) null, "a", natural)
                .compare("a", "a", natural)
                .result();
        assertTrue(result < 0);

        result = ComparisonChain.start()
                .compare("b", "a", natural)
                .compare("x", "y", natural)
                .result();
        assertTrue(result > 0);
    }

    @Test
    public void testEarlyReturnBehavior() {
        ComparisonChain chain = ComparisonChain.start()
                .compare(1, 2)
                .compare(5, 4); // this comparison should not change result because chain became inactive after first

        assertTrue(chain.result() < 0);
    }
}

