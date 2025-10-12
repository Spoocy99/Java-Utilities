package dev.spoocy.utils.config.misc;

import dev.spoocy.utils.config.Readable;
import dev.spoocy.utils.config.SectionArray;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SectionList<T extends Readable> implements SectionArray<T> {

    private final List<T> values;

    @SafeVarargs
    public SectionList(@NotNull T... values) {
        this.values = List.of(values);
    }

    public SectionList(@NotNull List<T> values) {
        this.values = values;
    }

    @Override
    public int length() {
        return this.values.size();
    }

    @Override
    public T get(int index) {
        return this.values.get(index);
    }

    @Override
    public T[] toArray() {
        return (T[]) this.values.toArray();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.values.iterator();
    }
}
