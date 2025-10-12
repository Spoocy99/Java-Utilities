package dev.spoocy.utils.common.misc;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class Reference<V> {

    public static <V> Reference<V> of(@NotNull V value) {
        return new Reference<>(value);
    }

    public static <V> Reference<V> of(@NotNull Supplier<V> supplier) {
        return new Reference<>(supplier);
    }

    private V value;
    private Supplier<V> supplier;

    private Reference(@NotNull V value) {
        this.value = value;
    }

    public Reference(@NotNull Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public V get() {
        if (this.supplier != null) {
            this.value = this.supplier.get();
            this.supplier = null;
        }

        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;
        Reference<?> refrence = (Reference<?>) o;
        return Objects.equals(value, refrence.value) && Objects.equals(supplier, refrence.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, supplier);
    }

    @Override
    public String toString() {
        return "Reference{" +
                "supplier=" + supplier +
                ", value=" + value +
                '}';
    }
}
