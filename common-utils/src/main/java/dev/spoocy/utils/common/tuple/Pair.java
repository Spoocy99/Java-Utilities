package dev.spoocy.utils.common.tuple;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Pair<A, B> {

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getKey() {
        return first();
    }

    public B getValue() {
        return second();
    }

    public A first() {
        return this.a;
    }

    public B second() {
        return this.b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;

        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "Pair{" + a + ", " + b + "}";
    }
}
