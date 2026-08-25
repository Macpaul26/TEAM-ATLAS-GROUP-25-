package campushub.test;

import campushub.algo.*;
import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MySet;
import campushub.model.ServiceRequest;

/** Tests for the search/sort, graph and optimisation algorithms added for M4/M7/M8. */
public class NewAlgorithmTests {

    public static void run() {
        testLinearSearch();
        testBinarySearch();
        testBinarySearchUnsortedCounterexample();
        testSorts();
        testSortsEdgeCases();
        testBfsDfs();
        testMst();
        testGreedyVsDp();
        testIndexParameters();
    }

    private static Integer[] shuffled() {
        return new Integer[]{9, 3, 7, 1, 8, 2, 5, 0, 6, 4};
    }

    private static void testLinearSearch() {
        TestRunner.section("Searching.linearSearch");
        Integer[] a = shuffled();
        TestRunner.assertEquals("finds 7", 2, Searching.linearSearch(a, 7));
        TestRunner.assertEquals("missing returns -1", -1, Searching.linearSearch(a, 99));
    }

    private static void testBinarySearch() {
        TestRunner.section("Searching.binarySearch (sorted precondition met)");
        Integer[] a = {0,1,2,3,4,5,6,7,8,9};
        TestRunner.assertEquals("finds 6", 6, Searching.binarySearch(a, 6));
        TestRunner.assertEquals("finds first", 0, Searching.binarySearch(a, 0));
        TestRunner.assertEquals("finds last", 9, Searching.binarySearch(a, 9));
        TestRunner.assertEquals("missing returns -1", -1, Searching.binarySearch(a, 42));
    }

    private static void testBinarySearchUnsortedCounterexample() {
        TestRunner.section("Searching.binarySearch COUNTEREXAMPLE (precondition violated)");
        // 1 is present (index 1), but on this UNSORTED array binary search misses it:
        //  {5,1,3,2,4} target 1: mid=3 -> go left; mid=5 -> go left; lo>hi -> -1.
        Integer[] unsorted = {5, 1, 3, 2, 4};
        int result = Searching.binarySearch(unsorted, 1);
        TestRunner.assertEquals("binary search on unsorted input wrongly reports 1 missing (-1)", -1, result);
        TestRunner.assertEquals("linear search on the SAME array still finds 1 at index 1", 1,
                Searching.linearSearch(unsorted, 1));
    }

    private static void testSorts() {
        TestRunner.section("Sorting (all four produce sorted output)");
        Integer[] base = shuffled();
        Integer[] a1 = base.clone(); Sorting.selectionSort(a1);
        TestRunner.assertTrue("selectionSort sorted", Sorting.isSorted(a1));
        Integer[] a2 = base.clone(); Sorting.insertionSort(a2);
        TestRunner.assertTrue("insertionSort sorted", Sorting.isSorted(a2));
        Integer[] a3 = base.clone(); Sorting.mergeSort(a3);
        TestRunner.assertTrue("mergeSort sorted", Sorting.isSorted(a3));
        Integer[] a4 = base.clone(); Sorting.quickSort(a4);
        TestRunner.assertTrue("quickSort sorted", Sorting.isSorted(a4));
        TestRunner.assertEquals("quickSort min", 0, a4[0]);
        TestRunner.assertEquals("quickSort max", 9, a4[9]);
    }

    private static void testSortsEdgeCases() {
        TestRunner.section("Sorting edge cases (empty, single, duplicates, reverse)");
        Integer[] empty = {}; Sorting.mergeSort(empty);
        TestRunner.assertTrue("empty stays sorted", Sorting.isSorted(empty));
        Integer[] one = {42}; Sorting.quickSort(one);
        TestRunner.assertEquals("single element unchanged", 42, one[0]);
        Integer[] dups = {5,1,5,1,5,1}; Sorting.insertionSort(dups);
        TestRunner.assertTrue("duplicates sorted", Sorting.isSorted(dups));
        Integer[] rev = {5,4,3,2,1}; Sorting.quickSort(rev);
        TestRunner.assertTrue("reverse-sorted handled", Sorting.isSorted(rev));
    }

    private static Graph sampleGraph() {
        Graph g = new Graph();
        g.addRoute("A", "B", 1, true);
        g.addRoute("A", "C", 4, true);
        g.addRoute("B", "C", 2, true);
        g.addRoute("C", "D", 1, true);
        g.addRoute("B", "D", 5, true);
        return g;
    }

    private static void testBfsDfs() {
        TestRunner.section("GraphTraversal (BFS/DFS)");
        Graph g = sampleGraph();
        MyArrayList<String> bfs = GraphTraversal.bfs(g, "A");
        TestRunner.assertEquals("BFS starts at A", "A", bfs.get(0));
        TestRunner.assertEquals("BFS visits all 4 nodes", 4, bfs.size());
        MyArrayList<String> dfs = GraphTraversal.dfs(g, "A");
        TestRunner.assertEquals("DFS starts at A", "A", dfs.get(0));
        TestRunner.assertEquals("DFS visits all 4 nodes", 4, dfs.size());
        MySet<String> reach = GraphTraversal.reachableFrom(g, "A");
        TestRunner.assertTrue("D reachable from A", reach.contains("D"));
        // disconnected node
        g.addLocation("Island");
        TestRunner.assertTrue("Island not reachable from A", !GraphTraversal.reachableFrom(g, "A").contains("Island"));
    }

    private static void testMst() {
        TestRunner.section("MinimumSpanningTree (Prim == Kruskal total)");
        Graph g = sampleGraph();
        MinimumSpanningTree.Result prim = MinimumSpanningTree.prim(g, "A");
        MinimumSpanningTree.Result kruskal = MinimumSpanningTree.kruskal(g);
        // MST of this graph: A-B(1), B-C(2), C-D(1) = 4
        TestRunner.assertEquals("Prim total weight", 4.0, prim.totalWeight, 0.0001);
        TestRunner.assertEquals("Kruskal total weight", 4.0, kruskal.totalWeight, 0.0001);
        TestRunner.assertEquals("MST has V-1 edges (Prim)", 3, prim.edges.size());
        TestRunner.assertEquals("MST has V-1 edges (Kruskal)", 3, kruskal.edges.size());
    }

    private static void testGreedyVsDp() {
        TestRunner.section("GreedyAssigner COUNTEREXAMPLE (greedy < DP optimum)");
        MyArrayList<ServiceRequest> reqs = GreedyAssigner.greedyFailureExample();
        int budget = 50;
        GreedyAssigner.Result greedy = GreedyAssigner.selectByRatio(reqs, budget);
        BudgetSelector.Result dp = BudgetSelector.selectWithinBudget(reqs, budget);
        TestRunner.assertEquals("greedy-by-ratio total benefit", 160, greedy.totalBenefit);
        TestRunner.assertEquals("DP optimal total benefit", 220, dp.totalBenefit);
        TestRunner.assertTrue("greedy is strictly worse than DP here", greedy.totalBenefit < dp.totalBenefit);
    }

    private static void testIndexParameters() {
        TestRunner.section("IndexParameters (derived, deterministic, in-range)");
        campushub.config.IndexParameters p = new campushub.config.IndexParameters(new long[]{22252461L, 22245678L, 22239999L});
        int cap = p.hashTableCapacity();
        TestRunner.assertTrue("hash capacity is a power of two", (cap & (cap - 1)) == 0);
        TestRunner.assertTrue("hash capacity >= 16", cap >= 16);
        double pen = p.routePenaltyFactor();
        TestRunner.assertTrue("route penalty in 1.10..1.60", pen >= 1.10 && pen <= 1.61);
        int budget = p.budgetConstraint();
        TestRunner.assertTrue("budget in 300..800", budget >= 300 && budget <= 800);
        // determinism
        campushub.config.IndexParameters p2 = new campushub.config.IndexParameters(new long[]{22252461L, 22245678L, 22239999L});
        TestRunner.assertEquals("same indices -> same seed", p.randomSeed(), p2.randomSeed());
    }
}
