package campushub.ds;

/**
 * A custom LIFO stack (from-scratch replacement for java.util.Stack /
 * java.util.ArrayDeque). Array-backed with amortised-O(1) push, following the
 * stack ADT in CLRS ch. 10.1.
 *
 * Used to back the audit_events undo log described in the Data Dictionary:
 * every ASSIGN pushes an event; an UNDO pops the most recent one.
 *
 *   push  -> O(1) amortised   pop -> O(1)   peek -> O(1)   isEmpty -> O(1)
 */
public class MyStack<T> {

    private Object[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MyStack() { data = new Object[DEFAULT_CAPACITY]; size = 0; }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void push(T item) {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
        data[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size == 0) throw new java.util.NoSuchElementException("pop() on empty stack");
        T top = (T) data[--size];
        data[size] = null; // let GC reclaim
        return top;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) throw new java.util.NoSuchElementException("peek() on empty stack");
        return (T) data[size - 1];
    }
}
