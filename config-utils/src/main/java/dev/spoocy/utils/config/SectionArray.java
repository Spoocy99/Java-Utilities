package dev.spoocy.utils.config;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public interface SectionArray<T extends Readable> extends Iterable<T> {

    int length();

    T get(int index);

    T[] toArray();

}
