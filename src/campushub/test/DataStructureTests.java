package campushub.test;

import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyHashMap;
import campushub.ds.MyLinkedList;
import campushub.ds.MyMinHeap;
import campushub.ds.MyQueue;

public class DataStructureTests {

    public static void run() {
        testArrayList();
        testLinkedList();
        testQueue();
        testMinHeap();
        testHashMap();
        testGraph();
    }

    private static void testArrayList() {
        TestRunner.section("MyArrayList");
        MyArrayList<Integer> list = new MyArrayList<>(2); // force early resize
        for (int i = 0; i < 20; i++) list.add(i);
        TestRunner.assertEquals("size after 20 adds", 20, list.size());
        TestRunner.assertEquals("get(0)", 0, list.get(0));
        TestRunner.assertEquals("get(19)", 19, list.get(19));
        list.addAt(0, -1);
        TestRunner.assertEquals("addAt(0) shifts elements", -1, list.get(0));
        TestRunner.assertEquals("addAt(0) shifts elements (old 0 now at 1)", 0, list.get(1));
        int removed = list.remove(0);
        TestRunner.assertEquals("remove(0) returns removed value", -1, removed);
        TestRunner.assertTrue("contains(19)", list.contains(19));
        TestRunner.assertTrue("does not contain 999", !list.contains(999));
    }

    private static void testLinkedList() {
        TestRunner.section("MyLinkedList");
        MyLinkedList<String> ll = new MyLinkedList<>();
        ll.addLast("b");
        ll.addLast("c");
        ll.addFirst("a");
        TestRunner.assertEquals("size after 3 inserts", 3, ll.size());
        TestRunner.assertEquals("get(0) is a", "a", ll.get(0));
        TestRunner.assertEquals("get(2) is c", "c", ll.get(2));
        TestRunner.assertEquals("removeFirst returns a", "a", ll.removeFirst());
        TestRunner.assertEquals("size after removeFirst", 2, ll.size());
        TestRunner.assertTrue("remove(c) succeeds", ll.remove("c"));
        TestRunner.assertEquals("size after remove(c)", 1, ll.size());
    }

    private static void testQueue() {
        TestRunner.section("MyQueue (FIFO order)");
        MyQueue<Integer> q = new MyQueue<>();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        TestRunner.assertEquals("dequeue order 1", 1, q.dequeue());
        TestRunner.assertEquals("dequeue order 2", 2, q.dequeue());
        q.enqueue(4);
        TestRunner.assertEquals("dequeue order 3", 3, q.dequeue());
        TestRunner.assertEquals("dequeue order 4", 4, q.dequeue());
        TestRunner.assertTrue("queue empty after all dequeued", q.isEmpty());
    }

    private static void testMinHeap() {
        TestRunner.section("MyMinHeap (extraction order)");
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        int[] values = {5, 1, 9, 3, 7, 2, 8, 0, 6, 4};
        for (int v : values) heap.insert(v);
        TestRunner.assertEquals("heap size after inserts", 10, heap.size());
        int previous = Integer.MIN_VALUE;
        boolean sortedOrder = true;
        while (!heap.isEmpty()) {
            int next = heap.extractMin();
            if (next < previous) sortedOrder = false;
            previous = next;
        }
        TestRunner.assertTrue("extractMin() always returns non-decreasing sequence", sortedOrder);
    }

    private static void testHashMap() {
        TestRunner.section("MyHashMap");
        MyHashMap<String, Integer> map = new MyHashMap<>();
        for (int i = 0; i < 50; i++) {
            map.put("key" + i, i * i);
        }
        TestRunner.assertEquals("size after 50 puts", 50, map.size());
        TestRunner.assertEquals("get(key10)", 100, map.get("key10"));
        map.put("key10", -1); // overwrite, must not grow size
        TestRunner.assertEquals("overwrite does not change size", 50, map.size());
        TestRunner.assertEquals("get(key10) after overwrite", -1, map.get("key10"));
        TestRunner.assertTrue("containsKey(key49)", map.containsKey("key49"));
        Integer removed = map.remove("key49");
        TestRunner.assertEquals("remove(key49) returns old value", 2401, removed);
        TestRunner.assertTrue("containsKey(key49) false after removal", !map.containsKey("key49"));
        TestRunner.assertEquals("size after removal", 49, map.size());
    }

    private static void testGraph() {
        TestRunner.section("Graph (adjacency list)");
        Graph g = new Graph();
        g.addRoute("A", "B", 5, true);
        g.addRoute("B", "C", 3, true);
        TestRunner.assertEquals("location count", 3, g.locationCount());
        TestRunner.assertEquals("A has 1 neighbour", 1, g.neighboursOf("A").size());
        TestRunner.assertEquals("B has 2 neighbours (bidirectional)", 2, g.neighboursOf("B").size());
        TestRunner.assertTrue("hasLocation(C)", g.hasLocation("C"));
        TestRunner.assertTrue("does not have location Z", !g.hasLocation("Z"));
    }
}
