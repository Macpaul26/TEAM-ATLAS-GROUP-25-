package campushub.ds;

/**
 * A custom (unbalanced) binary search tree used by the indexing engine to
 * index service requests / locations by a comparable key. Satisfies the
 * "BST - insert, search, inorder traversal / search path and sorted inorder
 * output" evidence row (§6). Follows CLRS ch. 12.
 *
 * Average case (random inserts): insert/search -> O(log n).
 * Worst case (sorted inserts, degenerate to a "linked list"): O(n) - which is
 * exactly the motivation for the balanced tree (MyAVLTree) benchmarked against
 * it in the efficiency lab.
 */
public class MyBST<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key; V value; Node<K, V> left, right;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private Node<K, V> root;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("null key not allowed");
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) { size++; return new Node<>(key, value); }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = insert(node.left, key, value);
        else if (cmp > 0) node.right = insert(node.right, key, value);
        else node.value = value; // key exists -> overwrite, size unchanged
        return node;
    }

    public V search(K key) {
        Node<K, V> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return node.value;
        }
        return null;
    }

    public boolean contains(K key) { return search(key) != null; }

    /** Records the comparison path taken while searching for key (for traces). */
    public MyArrayList<K> searchPath(K key) {
        MyArrayList<K> path = new MyArrayList<>();
        Node<K, V> node = root;
        while (node != null) {
            path.add(node.key);
            int cmp = key.compareTo(node.key);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else break;
        }
        return path;
    }

    /** Keys in sorted order (inorder traversal). */
    public MyArrayList<K> inorderKeys() {
        MyArrayList<K> out = new MyArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K, V> node, MyArrayList<K> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.key);
        inorder(node.right, out);
    }

    public int height() { return height(root); }
    private int height(Node<K, V> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }
}
