package campushub.ds;

/**
 * A custom dynamic-array based list.
 *
 * Backed by a plain Object[] that doubles in capacity whenever it fills up,
 * the same amortised-doubling strategy used by java.util.ArrayList
 * internally (see Goodrich, Tamassia and Goldwasser, 2014, ch. 6, on the
 * amortised cost of dynamic arrays).
 *
 * Amortised time complexity:
 *   add(item)      -> O(1) amortised, O(n) worst case on resize
 *   get(i)/set(i)  -> O(1)
 *   remove(i)      -> O(n) (shifts trailing elements)
 *   size/isEmpty   -> O(1)
 */
public class MyArrayList<T> implements Iterable<T> {

    private Object[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 8;

    public MyArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = DEFAULT_CAPACITY;
        data = new Object[initialCapacity];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(T item) {
        ensureCapacity();
        data[size++] = item;
    }

    public void addAt(int index, T item) {
        checkIndexForInsert(index);
        ensureCapacity();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    public void set(int index, T item) {
        checkIndex(index);
        data[index] = item;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return removed;
    }

    public boolean contains(T item) {
        for (int i = 0; i < size; i++) {
            if (data[i] == null ? item == null : data[i].equals(item)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] template) {
        T[] result = (T[]) java.lang.reflect.Array.newInstance(
                template.getClass().getComponentType(), size);
        for (int i = 0; i < size; i++) {
            result[i] = (T) data[i];
        }
        return result;
    }

    /** Iterator enabling for-each traversal over the list. */
    @Override
    @SuppressWarnings("unchecked")
    public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<T>() {
            private int cursor = 0;
            @Override public boolean hasNext() { return cursor < size; }
            @Override public T next() {
                if (cursor >= size) throw new java.util.NoSuchElementException();
                return (T) data[cursor++];
            }
        };
    }

    private void ensureCapacity() {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }

    private void checkIndexForInsert(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }
}
