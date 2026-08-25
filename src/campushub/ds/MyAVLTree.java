package campushub.ds;

/**
 * A self-balancing AVL tree - the "balanced tree" the brief asks for. The brief
 * allows a red-black tree OR "a clear simplified balanced-tree implementation";
 * AVL is the cleaner choice to implement, test and DEFEND orally, because its
 * balance rule (|height(left) - height(right)| <= 1 at every node) is easy to
 * state and check.
 *
 * Every insert restores the AVL invariant with single or double rotations, so
 * height stays O(log n) even for sorted input - the exact case that makes a
 * plain BST degenerate to O(n). The efficiency lab benchmarks the two trees
 * side by side to show this (see the BST-vs-balanced-tree experiment).
 *
 * Satisfies the balanced-tree evidence row: "insertion with rotations /
 * before-after rotation discussion and height discussion" (§6). Follows the
 * AVL treatment in Goodrich, Tamassia & Goldwasser ch. 11.
 */
public class MyAVLTree<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key; V value; Node<K, V> left, right; int height = 1;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private Node<K, V> root;
    private int size;
    private int lastRotations; // rotations performed by the most recent insert

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public int height() { return height(root); }
    public int rotationsInLastInsert() { return lastRotations; }

    private int height(Node<K, V> n) { return n == null ? 0 : n.height; }
    private int balanceFactor(Node<K, V> n) { return n == null ? 0 : height(n.left) - height(n.right); }
    private void update(Node<K, V> n) { n.height = 1 + Math.max(height(n.left), height(n.right)); }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> t2 = x.right;
        x.right = y; y.left = t2;
        update(y); update(x);
        lastRotations++;
        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> t2 = y.left;
        y.left = x; x.right = t2;
        update(x); update(y);
        lastRotations++;
        return y;
    }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("null key not allowed");
        lastRotations = 0;
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) { size++; return new Node<>(key, value); }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = insert(node.left, key, value);
        else if (cmp > 0) node.right = insert(node.right, key, value);
        else { node.value = value; return node; } // duplicate -> overwrite

        update(node);
        int bf = balanceFactor(node);

        // Left-Left
        if (bf > 1 && key.compareTo(node.left.key) < 0) return rotateRight(node);
        // Right-Right
        if (bf < -1 && key.compareTo(node.right.key) > 0) return rotateLeft(node);
        // Left-Right
        if (bf > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Right-Left
        if (bf < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
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

    /** True if the AVL invariant holds everywhere (used by tests). */
    public boolean isBalanced() { return isBalanced(root); }
    private boolean isBalanced(Node<K, V> n) {
        if (n == null) return true;
        if (Math.abs(balanceFactor(n)) > 1) return false;
        return isBalanced(n.left) && isBalanced(n.right);
    }
}
