package campushub.ds;

/**
 * A custom array-based binary min-heap (a from-scratch replacement for
 * java.util.PriorityQueue). The element that "compares smallest" under
 * its Comparable ordering always sits at the root, so extractMin() is
 * the next item to be served.
 *
 * Standard heap operations, following the presentation in Cormen,
 * Leiserson, Rivest and Stein (2009), ch. 6:
 *   insert()      -> O(log n)   (bubble up)
 *   extractMin()  -> O(log n)   (swap root with last, sift down)
 *   peek()        -> O(1)
 *   isEmpty/size  -> O(1)
 */
public class MyMinHeap<T extends Comparable<T>> {

    private Object[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MyMinHeap() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void insert(T item) {
        ensureCapacity();
        data[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) throw new java.util.NoSuchElementException("peek() on empty heap");
        return (T) data[0];
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) throw new java.util.NoSuchElementException("extractMin() on empty heap");
        T min = (T) data[0];
        size--;
        data[0] = data[size];
        data[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (((T) data[i]).compareTo((T) data[parent]) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;
            if (left < size && ((T) data[left]).compareTo((T) data[smallest]) < 0) smallest = left;
            if (right < size && ((T) data[right]).compareTo((T) data[smallest]) < 0) smallest = right;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int i, int j) {
        Object tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
    }
}
