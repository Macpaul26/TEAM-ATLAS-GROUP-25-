package campushub.ds;

/**
 * A custom singly linked list with head and tail pointers, now Iterable so it
 * can be used in for-each loops (the "iterator demo" evidence item in §6).
 *
 * Used as the backing structure for MyQueue (FIFO access) and for the chaining
 * buckets inside MyHashMap. See CLRS ch. 10 for the classic operations.
 *
 *   addFirst/addLast/removeFirst -> O(1)
 *   insertAfter(target,value)    -> O(n) (locate target) then O(1) splice
 *   get(i)                       -> O(n)
 *   size/isEmpty                 -> O(1)
 */
public class MyLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T value; Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) head = tail = node;
        else { tail.next = node; tail = node; }
        size++;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
        size++;
    }

    /** Inserts value immediately after the first node whose value equals target.
     *  @return true if target was found and the insert happened. */
    public boolean insertAfter(T target, T value) {
        Node<T> cur = head;
        while (cur != null) {
            boolean matches = (cur.value == null) ? target == null : cur.value.equals(target);
            if (matches) {
                Node<T> node = new Node<>(value);
                node.next = cur.next;
                cur.next = node;
                if (cur == tail) tail = node;
                size++;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    public T removeFirst() {
        if (head == null) throw new java.util.NoSuchElementException("removeFirst() on empty list");
        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }

    public T peekFirst() {
        if (head == null) throw new java.util.NoSuchElementException("peekFirst() on empty list");
        return head.value;
    }

    public T get(int index) {
        checkIndex(index);
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.value;
    }

    /** Removes the first occurrence equal to target. Returns true if removed. */
    public boolean remove(T target) {
        Node<T> prev = null;
        Node<T> cur = head;
        while (cur != null) {
            boolean matches = (cur.value == null) ? target == null : cur.value.equals(target);
            if (matches) {
                if (prev == null) head = cur.next; else prev.next = cur.next;
                if (cur == tail) tail = prev;
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] template) {
        T[] result = (T[]) java.lang.reflect.Array.newInstance(
                template.getClass().getComponentType(), size);
        Node<T> cur = head;
        int i = 0;
        while (cur != null) { result[i++] = cur.value; cur = cur.next; }
        return result;
    }

    /** Iterator over the list, enabling for-each traversal (evidence: iterator). */
    @Override
    public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<T>() {
            private Node<T> cursor = head;
            @Override public boolean hasNext() { return cursor != null; }
            @Override public T next() {
                if (cursor == null) throw new java.util.NoSuchElementException();
                T v = cursor.value;
                cursor = cursor.next;
                return v;
            }
        };
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
    }
}
