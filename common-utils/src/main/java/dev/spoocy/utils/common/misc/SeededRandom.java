package dev.spoocy.utils.common.misc;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SeededRandom extends Random {

    private static final AtomicLong uniquifier = new AtomicLong(8682522807148012L);
    private long seed;

    public SeededRandom() {
        this(nextSeed());
    }

    public SeededRandom(long seed) {
        super(seed);
        this.seed = seed;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        super.setSeed(seed);
        this.seed = seed;
    }

    public double nextDouble(double bound) {
        // Specialize boundedNextDouble for origin == 0, bound > 0
        double r = nextDouble();
        r = r * bound;
        if (r >= bound)  // may need to correct a rounding problem
            r = Math.nextDown(bound);
        return r;
    }

    public long nextLong(long bound) {
        // Specialize boundedNextLong for origin == 0, bound > 0
        final long m = bound - 1;
        long r = nextLong();
        if ((bound & m) == 0L) {
            // The bound is a power of 2.
            r &= m;
        } else {
            // Must reject over-represented candidates
            /* This loop takes an unlovable form (but it works):
               because the first candidate is already available,
               we need a break-in-the-middle construction,
               which is concisely but cryptically performed
               within the while-condition of a body-less for loop. */
            for (long u = r >>> 1;
                 u + m - (r = u % bound) < 0L;
                 u = nextLong() >>> 1)
                ;
        }
        return r;
    }

    public int between(int min, int max) {
        return nextInt(max - min) + min;
    }

    public double between(double min, double max) {
        return nextDouble() * (max - min) + min;
    }

    public float between(float min, float max) {
        return nextFloat() * (max - min) + min;
    }

    public long between(long min, long max) {
        return nextLong(max - min) + min;
    }

    public boolean chance(double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1");
        }
        return nextDouble() < chance;
    }

    public int around(int value, int variance) {
        return value + between(-variance, variance);
    }

    public double around(double value, double variance) {
        return value + between(-variance, variance);
    }

    public float around(float value, float variance) {
        return value + between(-variance, variance);
    }

    public long around(long value, long variance) {
        return value + between(-variance, variance);
    }

    @SafeVarargs
    public final <T> T choose(@NotNull T... array) {
        if (array.length == 0) {
            return null;
        }
        return array[nextInt(array.length)];
    }

    public <T> T choose(@NotNull List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(nextInt(list.size()));
    }

    public void scramble() {
        setSeed(nextSeed());
    }

    public static long nextSeed() {
        return seedUniquifier() ^ System.nanoTime();
    }

    private static long seedUniquifier() {
        // Different Sizes and Good Lattice Structure, 1999
        for (;;) {
            long current = uniquifier.get();
            long next = current * 1181783497276652981L;
            if (uniquifier.compareAndSet(current, next))
                return next;
        }
    }

    @Override
    public String toString() {
        return "SeededRandom{" +
                "seed=" + seed +
                '}';
    }
}
