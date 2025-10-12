package dev.spoocy.utils.common.collections;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class NormalizedArrayList<E> extends ArrayList<E> {

    public NormalizedArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public NormalizedArrayList() {
        super();
    }

    public NormalizedArrayList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public E get(int index) {
        return super.get(index - 1);
    }

    @Override
    public E set(int index, E element) {
        return super.set(index - 1, element);
    }

    @Override
    public void add(int index, E element) {
        super.add(index - 1, element);
    }

    @Override
    public E remove(int index) {
        return super.remove(index - 1);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return super.addAll(index - 1, c);
    }

        @Override
    public int indexOf(Object o) {
        int i = super.indexOf(o);
        return i >= 0 ? i + 1 : -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = super.lastIndexOf(o);
        return i >= 0 ? i + 1 : -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(1);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ListIterator<E> it = super.listIterator(index - 1);
        return new ListIterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                return it.next();
            }

            @Override
            public boolean hasPrevious() {
                return it.hasPrevious();
            }

            @Override
            public E previous() {
                return it.previous();
            }

            @Override
            public int nextIndex() {
                return it.nextIndex() + 1;
            }

            @Override
            public int previousIndex() {
                return it.previousIndex() + 1;
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public void set(Object o) {
                it.set((E) o);
            }

            @Override
            public void add(Object o) {
                it.add((E) o);
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return super.subList(fromIndex - 1, toIndex - 1);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        super.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return super.removeIf(filter);
    }

}
