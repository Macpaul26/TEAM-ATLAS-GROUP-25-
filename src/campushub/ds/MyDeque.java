package campushub.ds;

/**
 * A custom double-ended queue backed by a doubly linked list, giving O(1)
 * insertion and removal at BOTH ends. Satisfies the deque evidence row:
 * "addFront, addRear, removeFront, removeRear / urgent request insertion
 * example" - an urgent walk-in can be pushed to the FRONT to jump the line
 * while ordinary arrivals join the REAR.
 */
public class MyDeque<T> {

    private static class Node<T> {
        T value; Node<T> prev; Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void addFront(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) { head = tail = node; }
        else { node.next = head; head.prev = node; head = node; }
        size++;
    }

    public void addRear(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) { head = tail = node; }
        else { tail.next = node; node.prev = tail; tail = node; }
        size++;
    }

    public T removeFront() {
        if (head == null) throw new java.util.NoSuchElementException("removeFront on empty deque");
        T value = head.value;
        head = head.next;
        if (head == null) tail = null; else head.prev = null;
        size--;
        return value;
    }

    public T removeRear() {
        if (tail == null) throw new java.util.NoSuchElementException("removeRear on empty deque");
        T value = tail.value;
        tail = tail.prev;
        if (tail == null) head = null; else tail.next = null;
        size--;
        return value;
    }

    public T peekFront() {
        if (head == null) throw new java.util.NoSuchElementException("peekFront on empty deque");
        return head.value;
    }

    public T peekRear() {
        if (tail == null) throw new java.util.NoSuchElementException("peekRear on empty deque");
        return tail.value;
    }
}
