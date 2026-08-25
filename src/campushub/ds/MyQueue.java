package campushub.ds;

/**
 * A custom FIFO queue, implemented on top of MyLinkedList so that both
 * enqueue and dequeue are O(1) (tail pointer avoids the classic
 * "removeFirst is fine, addLast without a tail pointer is O(n)" trap).
 *
 * Used for ordinary, non-urgent walk-in requests that should simply be
 * served in arrival order once the priority queue has no urgent cases
 * waiting (see PriorityDispatcher).
 */
public class MyQueue<T> {

    private final MyLinkedList<T> items = new MyLinkedList<>();

    public void enqueue(T item) {
        items.addLast(item);
    }

    public T dequeue() {
        return items.removeFirst();
    }

    public T peek() {
        return items.peekFirst();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }
}
