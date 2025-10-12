package dev.spoocy.utils.common.collections;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class SortedArray<T extends Comparable<T>> implements Collection<T> {

	private volatile List<T> list;

	public SortedArray() {
		this.list = new ArrayList<>();
	}

	public SortedArray(Collection<T> wrapped) {
		this.list = new ArrayList<>(wrapped);
		Collections.sort(list);
	}

    public int size() {
		return list.size();
	}

    @Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

    @Override
	public Iterator<T> iterator() {
		return Iterables.unmodifiableIterable(list).iterator();
	}

    @Override
	public boolean contains(@NotNull Object value) {
		return list.contains(value);
	}

	@Override
	public synchronized boolean add(@NotNull T value) {
		List<T> copy = new ArrayList<>();
		boolean inserted = false;

		for (T element : list) {
			if (!inserted && value.compareTo(element) < 0) {
				copy.add(value);
				inserted = true;
			}
			copy.add(element);
		}

		if (!inserted) {
			copy.add(value);
		}

		list = copy;
		return true;
	}

	@Override
	public synchronized boolean remove(@NotNull Object value) {
		List<T> copy = new ArrayList<>();
		boolean result = false;

		for (T element : list) {
			if (!Objects.equal(value, element)) {
				copy.add(element);
			} else {
				result = true;
			}
		}

		list = copy;
		return result;
	}

    @Override
	public boolean containsAll(@NotNull Collection<?> values) {
		return new HashSet<>(list).containsAll(values);
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
	public boolean removeAll(@NotNull Collection<?> values) {
		if (values.isEmpty()) {
			return false;
		}

		List<T> copy = new ArrayList<>(list);
		copy.removeAll(values);

		list = copy;
		return true;
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> values) {
		if (values.isEmpty()) return false;

		List<T> copy = new ArrayList<>(list);
		copy.removeAll(values);

		list = copy;
		return true;
	}

	@Override
	public void clear() {
		list = new ArrayList<>();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return list.toArray(a);
    }

    @Override
	public String toString() {
		return list.toString();
	}

    public T get(int index) {
		return list.get(index);
	}

    public synchronized void remove(int index) {
		List<T> copy = new ArrayList<>(this.list);

		copy.remove(index);
		list = copy;
	}

}
