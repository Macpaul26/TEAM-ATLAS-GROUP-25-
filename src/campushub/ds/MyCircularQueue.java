package campushub.ds;

/**
 * A fixed-capacity circular (ring-buffer) queue with explicit wrap-around,
 * satisfying the "queue and circular queue - wrap-around handling / trace
 * showing front/rear movement" evidence row in the brief (§6).
 *
 * front indexes the next element to dequeue; rear indexes the next free slot.
 * Both advance modulo capacity, so the buffer is reused without shifting.
 *
 *   enqueue -> O(1)   dequeue -> O(1)   peek -> O(1)
 */
public class MyCircularQueue<T> {

    private final Object[] data;
    private int front;
    private int rear;
    private int count;
    private final int capacity;

    public MyCircularQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
        this.capacity = capacity;
        this.data = new Object[capacity];
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == capacity; }
    public int size()        { return count; }
    public int capacity()    { return capacity; }
    public int front()       { return front; }
    public int rear()        { return rear; }

    public void enqueue(T item) {
        if (isFull()) throw new IllegalStateException("enqueue on full circular queue");
        data[rear] = item;
        rear = (rear + 1) % capacity;   // wrap-around
        count++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) throw new java.util.NoSuchElementException("dequeue on empty circular queue");
        T item = (T) data[front];
        data[front] = null;
        front = (front + 1) % capacity; // wrap-around
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new java.util.NoSuchElementException("peek on empty circular queue");
        return (T) data[front];
    }
}
