package dev.spoocy.utils.common.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SortedArrayList<T extends Comparable<T>> implements List<T> {

	private volatile List<T> list;

	private static <E extends Comparable<E>> int findInsertIndex(@NotNull List<E> values, @NotNull E value) {
		int index = Collections.binarySearch(values, value);
		return index >= 0 ? index : -index - 1;
	}

	public SortedArrayList() {
		this.list = new ArrayList<>();
	}

	public SortedArrayList(@NotNull Collection<T> wrapped) {
		this.list = new ArrayList<>(wrapped);
		Collections.sort(list);
	}

	@Override
	public int size() {
		return this.list.size();
	}

    @Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public boolean contains(@NotNull Object value) {
		return this.list.contains(value);
	}

	@Override
	public synchronized boolean add(@NotNull T value) {
		List<T> copy = new ArrayList<>(this.list);
		copy.add(findInsertIndex(copy, value), value);
		this.list = copy;
		return true;
	}

	@Override
	public synchronized boolean remove(@NotNull Object value) {
		List<T> copy = new ArrayList<>(this.list);
		int index = copy.indexOf(value);

		if (index < 0) {
			return false;
		}

		copy.remove(index);
		this.list = copy;
		return true;
	}

    @Override
	public boolean containsAll(@NotNull Collection<?> values) {
		return new HashSet<>(this.list).containsAll(values);
	}

    @Override
	public synchronized boolean addAll(@NotNull Collection<? extends T> values) {

		if (values.isEmpty()) {
			return false;
		}

		List<T> copy = new ArrayList<>(list);

		copy.addAll(values);
		Collections.sort(copy);

		list = copy;
		return true;
	}

    @Override
    public synchronized boolean addAll(int index, @NotNull Collection<? extends T> c) {
		if (index < 0 || index > this.list.size()) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.list.size());
		}
		return this.addAll(c);
    }

    @Override
	public synchronized boolean removeAll(@NotNull Collection<?> values) {
		if (values.isEmpty()) {
			return false;
		}

		List<T> copy = new ArrayList<>(this.list);
		boolean changed = copy.removeAll(values);
		if (!changed) {
			return false;
		}

		this.list = copy;
		return true;
	}

	@Override
	public synchronized boolean retainAll(@NotNull Collection<?> values) {
		if (values.isEmpty() && this.list.isEmpty()) {
			return false;
		}

		List<T> copy = new ArrayList<>(this.list);
		boolean changed = copy.retainAll(values);
		if (!changed) {
			return false;
		}

		this.list = copy;
		return true;
	}

	@Override
	public synchronized void clear() {
		this.list = new ArrayList<>();
	}

	@Override
	public Object @NotNull [] toArray() {
		return this.list.toArray();
	}

    @NotNull
    @Override
    public <T1> T1 @NotNull [] toArray(@NotNull T1[] a) {
        return this.list.toArray(a);
    }

    @Override
	public String toString() {
		return this.list.toString();
	}

	@Override
	public T get(int index) {
		return this.list.get(index);
	}

    @Override
    public T set(int index, T element) {
	throw new UnsupportedOperationException("set(index, element) is not supported by SortedArrayList");
    }

    @Override
    public void add(int index, T element) {
	throw new UnsupportedOperationException("add(index, element) is not supported by SortedArrayList");
    }

    @Override
    public synchronized T remove(int index) {
		List<T> copy = new ArrayList<>(this.list);
		T removed = copy.remove(index);
		this.list = copy;
		return removed;
    }

    @Override
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
		List<T> snapshot = List.copyOf(this.list);
		return snapshot.listIterator();
    }

    @Override
    public @NotNull ListIterator<T> listIterator(int index) {
		List<T> snapshot = List.copyOf(this.list);
		return snapshot.listIterator(index);
    }

    @Override
    public @NotNull List<T> subList(int fromIndex, int toIndex) {
		List<T> snapshot = List.copyOf(this.list);
		return snapshot.subList(fromIndex, toIndex);
    }

	@Override
	public @NotNull Iterator<T> iterator() {
		List<T> snapshot = List.copyOf(this.list);
		return snapshot.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof List<?>)) {
			return false;
		}
		List<?> other = (List<?>) o;
		return this.list.equals(other);
	}

	@Override
	public int hashCode() {
		return this.list.hashCode();
	}

}
