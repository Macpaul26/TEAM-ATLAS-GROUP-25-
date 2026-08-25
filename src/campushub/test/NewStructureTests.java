package campushub.test;

import campushub.ds.*;

/** Tests for the data structures added to complete the §6 requirement set. */
public class NewStructureTests {

    public static void run() {
        testStack();
        testCircularQueue();
        testDeque();
        testSet();
        testDisjointSet();
        testBST();
        testAVL();
        testBTree();
        testMatrixGraph();
        testLinkedListIteratorAndInsertAfter();
    }

    private static void testStack() {
        TestRunner.section("MyStack (LIFO undo log)");
        MyStack<String> s = new MyStack<>();
        s.push("ASSIGN R1"); s.push("ASSIGN R2"); s.push("STATUS_CHANGE R1");
        TestRunner.assertEquals("size after 3 pushes", 3, s.size());
        TestRunner.assertEquals("peek is last pushed", "STATUS_CHANGE R1", s.peek());
        TestRunner.assertEquals("pop returns last pushed (undo)", "STATUS_CHANGE R1", s.pop());
        TestRunner.assertEquals("pop returns next-last", "ASSIGN R2", s.pop());
        TestRunner.assertEquals("size after 2 pops", 1, s.size());
        // invalid case
        s.pop();
        boolean threw = false;
        try { s.pop(); } catch (java.util.NoSuchElementException e) { threw = true; }
        TestRunner.assertTrue("pop() on empty stack throws", threw);
    }

    private static void testCircularQueue() {
        TestRunner.section("MyCircularQueue (wrap-around)");
        MyCircularQueue<Integer> q = new MyCircularQueue<>(3);
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        TestRunner.assertTrue("queue full", q.isFull());
        boolean threw = false;
        try { q.enqueue(4); } catch (IllegalStateException e) { threw = true; }
        TestRunner.assertTrue("enqueue on full throws", threw);
        TestRunner.assertEquals("dequeue 1", 1, q.dequeue());
        q.enqueue(4); // wraps around into slot 0
        TestRunner.assertEquals("rear wrapped to index 1", 1, q.rear());
        TestRunner.assertEquals("dequeue 2", 2, q.dequeue());
        TestRunner.assertEquals("dequeue 3", 3, q.dequeue());
        TestRunner.assertEquals("dequeue 4 (the wrapped one)", 4, q.dequeue());
        TestRunner.assertTrue("empty after draining", q.isEmpty());
    }

    private static void testDeque() {
        TestRunner.section("MyDeque (both ends)");
        MyDeque<String> d = new MyDeque<>();
        d.addRear("normal1"); d.addRear("normal2");
        d.addFront("URGENT"); // urgent request jumps to the front
        TestRunner.assertEquals("size", 3, d.size());
        TestRunner.assertEquals("front is urgent", "URGENT", d.peekFront());
        TestRunner.assertEquals("rear is normal2", "normal2", d.peekRear());
        TestRunner.assertEquals("removeFront serves urgent first", "URGENT", d.removeFront());
        TestRunner.assertEquals("removeRear serves newest normal", "normal2", d.removeRear());
        TestRunner.assertEquals("one left", 1, d.size());
    }

    private static void testSet() {
        TestRunner.section("MySet (membership)");
        MySet<String> set = new MySet<>();
        TestRunner.assertTrue("add new returns true", set.add("L1"));
        TestRunner.assertTrue("add duplicate returns false", !set.add("L1"));
        TestRunner.assertTrue("contains L1", set.contains("L1"));
        TestRunner.assertTrue("does not contain L99", !set.contains("L99"));
        TestRunner.assertEquals("size is 1", 1, set.size());
        TestRunner.assertTrue("remove existing returns true", set.remove("L1"));
        TestRunner.assertTrue("empty after remove", set.isEmpty());
    }

    private static void testDisjointSet() {
        TestRunner.section("DisjointSet (union-find)");
        DisjointSet ds = new DisjointSet();
        for (String x : new String[]{"A","B","C","D"}) ds.makeSet(x);
        TestRunner.assertEquals("4 components initially", 4, ds.componentCount());
        TestRunner.assertTrue("union A,B merges", ds.union("A", "B"));
        TestRunner.assertTrue("union C,D merges", ds.union("C", "D"));
        TestRunner.assertTrue("A,B connected", ds.connected("A", "B"));
        TestRunner.assertTrue("A,C not connected yet", !ds.connected("A", "C"));
        TestRunner.assertTrue("union B,C merges the two pairs", ds.union("B", "C"));
        TestRunner.assertTrue("A,D now connected", ds.connected("A", "D"));
        TestRunner.assertTrue("re-union already-joined returns false", !ds.union("A", "D"));
        TestRunner.assertEquals("1 component at the end", 1, ds.componentCount());
    }

    private static void testBST() {
        TestRunner.section("MyBST (search path + sorted inorder)");
        MyBST<Integer, String> bst = new MyBST<>();
        int[] keys = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keys) bst.insert(k, "v" + k);
        TestRunner.assertEquals("size", 7, bst.size());
        TestRunner.assertEquals("search hit", "v60", bst.search(60));
        TestRunner.assertTrue("search miss returns null", bst.search(999) == null);
        MyArrayList<Integer> inorder = bst.inorderKeys();
        boolean sorted = true;
        for (int i = 1; i < inorder.size(); i++) if (inorder.get(i) < inorder.get(i - 1)) sorted = false;
        TestRunner.assertTrue("inorder is sorted", sorted);
        MyArrayList<Integer> path = bst.searchPath(60);
        TestRunner.assertEquals("search path root is 50", 50, path.get(0));
        TestRunner.assertEquals("search path ends at 60", 60, path.get(path.size() - 1));
    }

    private static void testAVL() {
        TestRunner.section("MyAVLTree (stays balanced on sorted input)");
        MyAVLTree<Integer, Integer> avl = new MyAVLTree<>();
        MyBST<Integer, Integer> bst = new MyBST<>();
        for (int i = 1; i <= 15; i++) { avl.insert(i, i); bst.insert(i, i); }
        TestRunner.assertTrue("AVL invariant holds", avl.isBalanced());
        TestRunner.assertEquals("AVL height is logarithmic (<=4 for n=15)", 4, avl.height());
        TestRunner.assertEquals("BST degenerates to height 15 on sorted input", 15, bst.height());
        TestRunner.assertTrue("AVL search works", avl.contains(9));
        MyArrayList<Integer> inorder = avl.inorderKeys();
        TestRunner.assertEquals("inorder count", 15, inorder.size());
        TestRunner.assertEquals("inorder[0]", 1, inorder.get(0));
        TestRunner.assertEquals("inorder[14]", 15, inorder.get(14));
    }

    private static void testBTree() {
        TestRunner.section("MyBTree (node splits + shallow height)");
        MyBTree<Integer> bt = new MyBTree<>(3); // t=3
        int totalSplits = 0;
        for (int i = 1; i <= 50; i++) { bt.insert(i); totalSplits += bt.splitsInLastInsert(); }
        TestRunner.assertEquals("size after 50 inserts", 50, bt.size());
        TestRunner.assertTrue("at least one node split happened", totalSplits > 0);
        TestRunner.assertTrue("B-tree stays shallow (height <= 4)", bt.height() <= 4);
        TestRunner.assertTrue("contains existing key", bt.contains(37));
        TestRunner.assertTrue("does not contain absent key", !bt.contains(999));
        MyArrayList<Integer> inorder = bt.inorderKeys();
        TestRunner.assertEquals("inorder count", 50, inorder.size());
        TestRunner.assertEquals("inorder sorted first", 1, inorder.get(0));
        TestRunner.assertEquals("inorder sorted last", 50, inorder.get(49));
    }

    private static void testMatrixGraph() {
        TestRunner.section("MatrixGraph (adjacency matrix)");
        MatrixGraph g = new MatrixGraph(5);
        g.addRoute("A", "B", 4, true);
        g.addRoute("B", "C", 2, true);
        TestRunner.assertEquals("3 locations", 3, g.locationCount());
        TestRunner.assertTrue("edge A-B present", g.hasEdge("A", "B"));
        TestRunner.assertTrue("edge B-A present (bidirectional)", g.hasEdge("B", "A"));
        TestRunner.assertTrue("no edge A-C", !g.hasEdge("A", "C"));
        TestRunner.assertEquals("weight A-B", 4.0, g.weightOf("A", "B"), 0.0001);
        TestRunner.assertTrue("weight A-C is infinity", Double.isInfinite(g.weightOf("A", "C")));
    }

    private static void testLinkedListIteratorAndInsertAfter() {
        TestRunner.section("MyLinkedList (iterator + insertAfter)");
        MyLinkedList<String> ll = new MyLinkedList<>();
        ll.addLast("a"); ll.addLast("c");
        TestRunner.assertTrue("insertAfter existing returns true", ll.insertAfter("a", "b"));
        TestRunner.assertEquals("insertAfter put b at index 1", "b", ll.get(1));
        TestRunner.assertTrue("insertAfter missing returns false", !ll.insertAfter("z", "x"));
        StringBuilder sb = new StringBuilder();
        for (String s : ll) sb.append(s);
        TestRunner.assertEquals("for-each iterator yields a,b,c in order", "abc", sb.toString());
    }
}
