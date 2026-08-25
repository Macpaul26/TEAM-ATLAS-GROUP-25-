package campushub.ds;

/**
 * A custom set built on top of MyHashMap (values are a shared sentinel).
 * Satisfies the "Set, map - custom set/map on top of hash table" evidence row.
 * Use case: membership tests such as "have we already visited this location?"
 * during graph traversal, or "is this request id already registered?".
 *
 *   add/contains/remove -> O(1) average (inherits MyHashMap behaviour)
 */
public class MySet<T> {

    private static final Object PRESENT = new Object();
    private final MyHashMap<T, Object> map;

    public MySet() { this.map = new MyHashMap<>(); }

    /** @return true if the element was newly added (was not already present). */
    public boolean add(T item) {
        if (map.containsKey(item)) return false;
        map.put(item, PRESENT);
        return true;
    }

    public boolean contains(T item) { return map.containsKey(item); }

    /** @return true if the element was present and removed. */
    public boolean remove(T item) {
        if (!map.containsKey(item)) return false;
        map.remove(item);
        return true;
    }

    public int size() { return map.size(); }
    public boolean isEmpty() { return map.isEmpty(); }
    public MyArrayList<T> elements() { return map.keys(); }
}
