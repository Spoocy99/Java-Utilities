package dev.spoocy.utils.common.collections;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class SortedArrayListTest {

    @Test
    public void addKeepsElementsSorted() {
        SortedArrayList<Integer> list = new SortedArrayList<>();

        list.add(5);
        list.add(1);
        list.add(3);

        assertEquals(Arrays.asList(1, 3, 5), list);
    }

    @Test
    public void removeObjectRemovesOnlyFirstMatch() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(1, 2, 2, 3));

        boolean removed = list.remove(Integer.valueOf(2));

        assertTrue(removed);
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void removeByIndexReturnsRemovedElement() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(1, 2, 3));

        Integer removed = list.remove(2);

        assertEquals(3, removed);
        assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void retainAllRetainsMatchingElements() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(1, 2, 3, 4));

        boolean changed = list.retainAll(Arrays.asList(2, 4, 8));

        assertTrue(changed);
        assertEquals(Arrays.asList(2, 4), list);
    }

    @Test
    public void positionalMutatorsAreUnsupported() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(1, 2, 3));

        assertThrows(UnsupportedOperationException.class, () -> list.set(1, 10));
        assertThrows(UnsupportedOperationException.class, () -> list.add(1, 10));
    }

    @Test
    public void iteratorIsReadOnlySnapshot() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(1, 2, 3));
        Iterator<Integer> iterator = list.iterator();

        assertEquals(1, iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void addAllAtIndexValidatesBoundsAndKeepsSortedOrder() {
        SortedArrayList<Integer> list = new SortedArrayList<>(Arrays.asList(2, 4));

        assertTrue(list.addAll(1, Arrays.asList(3, 1)));
        assertEquals(Arrays.asList(1, 2, 3, 4), list);
        assertThrows(IndexOutOfBoundsException.class, () -> list.addAll(9, Arrays.asList(5)));
    }
}

