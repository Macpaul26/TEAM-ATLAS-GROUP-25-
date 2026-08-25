package campushub.ds;

/**
 * A custom B-tree of minimum degree t (each non-root node holds between t-1 and
 * 2t-1 keys). This is the "B-tree - node split and search / search trace and
 * node split explanation" evidence row (§6), and models the kind of multi-way,
 * disk-page-friendly index a database uses for its primary key. Follows the
 * CLRS ch. 18 B-tree insert-by-splitting algorithm.
 *
 * Because every node packs many keys, a B-tree stays very shallow (height grows
 * ~log_t n), which is why real database indexes use B-trees / B+-trees rather
 * than binary trees: fewer node visits = fewer disk page reads.
 *
 *   search -> O(t log_t n)   insert -> O(t log_t n)
 */
public class MyBTree<K extends Comparable<K>> {

    private final int t; // minimum degree
    private Node<K> root;
    private int size;
    private int lastSplits; // splits performed by the most recent insert

    @SuppressWarnings("unchecked")
    private static class Node<K> {
        int n;                 // current number of keys
        K[] keys;
        Node<K>[] children;
        boolean leaf;
        Node(int t, boolean leaf) {
            this.leaf = leaf;
            this.keys = (K[]) new Comparable[2 * t - 1];
            this.children = (Node<K>[]) new Node[2 * t];
            this.n = 0;
        }
    }

    public MyBTree() { this(3); }               // default order: t = 3
    public MyBTree(int minimumDegree) {
        if (minimumDegree < 2) throw new IllegalArgumentException("minimum degree t must be >= 2");
        this.t = minimumDegree;
        this.root = new Node<>(t, true);
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public int splitsInLastInsert() { return lastSplits; }

    public boolean contains(K key) { return search(root, key); }

    private boolean search(Node<K> x, K key) {
        int i = 0;
        while (i < x.n && key.compareTo(x.keys[i]) > 0) i++;
        if (i < x.n && key.compareTo(x.keys[i]) == 0) return true;
        if (x.leaf) return false;
        return search(x.children[i], key);
    }

    public void insert(K key) {
        if (key == null) throw new IllegalArgumentException("null key not allowed");
        lastSplits = 0;
        if (contains(key)) return; // keep keys unique for this index
        Node<K> r = root;
        if (r.n == 2 * t - 1) {          // root full -> grow height by splitting root
            Node<K> s = new Node<>(t, false);
            s.children[0] = r;
            root = s;
            splitChild(s, 0);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
        size++;
    }

    private void insertNonFull(Node<K> x, K key) {
        int i = x.n - 1;
        if (x.leaf) {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) { x.keys[i + 1] = x.keys[i]; i--; }
            x.keys[i + 1] = key;
            x.n++;
        } else {
            while (i >= 0 && key.compareTo(x.keys[i]) < 0) i--;
            i++;
            if (x.children[i].n == 2 * t - 1) {
                splitChild(x, i);
                if (key.compareTo(x.keys[i]) > 0) i++;
            }
            insertNonFull(x.children[i], key);
        }
    }

    /** Splits the full child x.children[i] around its median key. */
    private void splitChild(Node<K> x, int i) {
        Node<K> y = x.children[i];
        Node<K> z = new Node<>(t, y.leaf);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) z.keys[j] = y.keys[j + t];
        if (!y.leaf) for (int j = 0; j < t; j++) z.children[j] = y.children[j + t];
        y.n = t - 1;
        for (int j = x.n; j >= i + 1; j--) x.children[j + 1] = x.children[j];
        x.children[i + 1] = z;
        for (int j = x.n - 1; j >= i; j--) x.keys[j + 1] = x.keys[j];
        x.keys[i] = y.keys[t - 1];
        x.n++;
        lastSplits++;
    }

    public int height() { return height(root); }
    private int height(Node<K> x) {
        if (x.leaf) return 1;
        return 1 + height(x.children[0]);
    }

    /** Keys in sorted order (inorder walk of the B-tree). */
    public MyArrayList<K> inorderKeys() {
        MyArrayList<K> out = new MyArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K> x, MyArrayList<K> out) {
        int i;
        for (i = 0; i < x.n; i++) {
            if (!x.leaf) inorder(x.children[i], out);
            out.add(x.keys[i]);
        }
        if (!x.leaf) inorder(x.children[i], out);
    }
}
