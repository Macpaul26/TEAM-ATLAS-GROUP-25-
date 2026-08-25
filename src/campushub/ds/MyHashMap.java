package campushub.ds;

/**
 * A custom hash table using separate chaining for collision resolution,
 * each bucket being a MyLinkedList of key/value entries. This mirrors
 * the separate-chaining hash table described in Cormen, Leiserson,
 * Rivest and Stein (2009), ch. 11, and in Sedgewick and Wayne (2011),
 * ch. 3.4.
 *
 * Average-case time complexity (assuming a reasonably uniform hash and
 * load factor kept under ~0.75 by resizing):
 *   put/get/remove/containsKey -> O(1) average, O(n) worst case
 *                                  (all keys colliding in one bucket)
 */
public class MyHashMap<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private MyLinkedList<Entry<K, V>>[] buckets;
    private int size;
    private final boolean autoResize;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_LIMIT = 0.75;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = new MyLinkedList[DEFAULT_CAPACITY];
        size = 0;
        autoResize = true;
    }

    /**
     * Fixed-capacity constructor used by the load-factor experiment
     * (Module M10): when autoResize is false the table keeps a fixed
     * number of buckets, so load factor and collision counts can be
     * measured at chosen table sizes instead of the class always
     * resizing itself back under 0.75.
     */
    @SuppressWarnings("unchecked")
    public MyHashMap(int initialCapacity, boolean autoResize) {
        if (initialCapacity < 1) initialCapacity = DEFAULT_CAPACITY;
        buckets = new MyLinkedList[initialCapacity];
        size = 0;
        this.autoResize = autoResize;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    private int bucketIndex(K key, int capacity) {
        int h = (key == null) ? 0 : key.hashCode();
        h = h ^ (h >>> 16); // spread bits, same trick used in java.util.HashMap
        return Math.abs(h) % capacity;
    }

    public void put(K key, V value) {
        if (autoResize && (double) (size + 1) / buckets.length > LOAD_FACTOR_LIMIT) {
            resize();
        }
        int idx = bucketIndex(key, buckets.length);
        if (buckets[idx] == null) buckets[idx] = new MyLinkedList<>();
        MyLinkedList<Entry<K, V>> bucket = buckets[idx];
        for (int i = 0; i < bucket.size(); i++) {
            Entry<K, V> e = bucket.get(i);
            if (keysEqual(e.key, key)) {
                e.value = value; // overwrite
                return;
            }
        }
        bucket.addLast(new Entry<>(key, value));
        size++;
    }

    public V get(K key) {
        int idx = bucketIndex(key, buckets.length);
        MyLinkedList<Entry<K, V>> bucket = buckets[idx];
        if (bucket == null) return null;
        for (int i = 0; i < bucket.size(); i++) {
            Entry<K, V> e = bucket.get(i);
            if (keysEqual(e.key, key)) return e.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int idx = bucketIndex(key, buckets.length);
        MyLinkedList<Entry<K, V>> bucket = buckets[idx];
        if (bucket == null) return false;
        for (int i = 0; i < bucket.size(); i++) {
            if (keysEqual(bucket.get(i).key, key)) return true;
        }
        return false;
    }

    public V remove(K key) {
        int idx = bucketIndex(key, buckets.length);
        MyLinkedList<Entry<K, V>> bucket = buckets[idx];
        if (bucket == null) return null;
        for (int i = 0; i < bucket.size(); i++) {
            Entry<K, V> e = bucket.get(i);
            if (keysEqual(e.key, key)) {
                bucket.remove(e);
                size--;
                return e.value;
            }
        }
        return null;
    }

    /** Returns all values currently stored, for iteration by callers. */
    public MyArrayList<V> values() {
        MyArrayList<V> result = new MyArrayList<>();
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket == null) continue;
            for (int i = 0; i < bucket.size(); i++) {
                result.add(bucket.get(i).value);
            }
        }
        return result;
    }

    public MyArrayList<K> keys() {
        MyArrayList<K> result = new MyArrayList<>();
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket == null) continue;
            for (int i = 0; i < bucket.size(); i++) {
                result.add(bucket.get(i).key);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        MyLinkedList<Entry<K, V>>[] oldBuckets = buckets;
        buckets = new MyLinkedList[oldBuckets.length * 2];
        size = 0;
        for (MyLinkedList<Entry<K, V>> bucket : oldBuckets) {
            if (bucket == null) continue;
            for (int i = 0; i < bucket.size(); i++) {
                Entry<K, V> e = bucket.get(i);
                put(e.key, e.value);
            }
        }
    }

    private boolean keysEqual(K a, K b) {
        return (a == null) ? b == null : a.equals(b);
    }

    // ---- Diagnostics for the empirical load-factor experiment (M10) ----

    /** Number of buckets currently allocated. */
    public int bucketCount() { return buckets.length; }

    /** size / bucketCount — how full the table is. */
    public double currentLoadFactor() {
        return (double) size / buckets.length;
    }

    /**
     * Total collisions = number of entries that had to share a bucket
     * with an earlier entry (i.e. every element in a bucket after the
     * first). 0 means a perfect, collision-free spread.
     */
    public int collisionCount() {
        int collisions = 0;
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null && bucket.size() > 1) {
                collisions += bucket.size() - 1;
            }
        }
        return collisions;
    }

    /** Length of the longest bucket chain (worst-case probe length). */
    public int longestChain() {
        int longest = 0;
        for (MyLinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null && bucket.size() > longest) longest = bucket.size();
        }
        return longest;
    }
}
