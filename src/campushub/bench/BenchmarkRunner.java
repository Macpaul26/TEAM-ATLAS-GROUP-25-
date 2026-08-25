package campushub.bench;

import campushub.algo.GraphTraversal;
import campushub.algo.MinimumSpanningTree;
import campushub.algo.Searching;
import campushub.algo.Sorting;
import campushub.algo.ShortestRoute;
import campushub.config.IndexParameters;
import campushub.ds.Graph;
import campushub.ds.MyAVLTree;
import campushub.ds.MyBST;
import campushub.ds.MyHashMap;
import campushub.ds.MyMinHeap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Empirical efficiency lab (Module M10, brief section 9).
 *
 * Runs the six required experiments, EACH REPEATED {@value #RUNS} TIMES
 * with the average reported, and exports every result to CSV under
 * results/ so the Evidence Squad can plot the graphs in Excel/Python:
 *
 *   1. Search comparison     -> results/search_results.csv
 *   2. Sorting comparison    -> results/sort_results.csv
 *   3. Hash load factor      -> results/hash_results.csv
 *   4. BST vs balanced tree  -> results/tree_results.csv
 *   5. Heap priority dispatch-> results/heap_results.csv
 *   6. Graph algorithms      -> results/graph_results.csv
 *
 * Every individual (un-averaged) run is also appended to
 * results/benchmark_raw.csv so the raw timings are kept, not just the
 * averages (brief section 12: "Keep raw timings, not only screenshots").
 *
 * The random seed is derived from the team's index numbers via
 * IndexParameters, so the generated workload is reproducible AND tied
 * to this specific team (AI-resistance / localisation, brief section 2).
 */
public class BenchmarkRunner {

    private static final int RUNS = 3; // brief: "run each experiment at least three times"
    private static final Random RNG =
            new Random(new IndexParameters().randomSeed());

    private static PrintWriter rawOut;

    public static void run() {
        new File("results").mkdirs();
        printMachineSpec();

        try {
            rawOut = new PrintWriter(new FileWriter("results/benchmark_raw.csv"));
            rawOut.println("experiment,algorithm,n,extra,run_index,time_ns");

            benchmarkSearch();
            benchmarkSort();
            benchmarkHashLoadFactor();
            benchmarkTrees();
            benchmarkHeap();
            benchmarkGraph();

            rawOut.close();
        } catch (IOException e) {
            System.out.println("Could not write results CSV: " + e.getMessage());
            return;
        }

        System.out.println("\nAll experiments complete. CSVs written to results/.");
        System.out.println("Plot these in Excel or Python (matplotlib) for the report graphs.");
    }

    // ----------------------------------------------------------------
    // machine spec (brief: "state the machine specification")
    // ----------------------------------------------------------------
    private static void printMachineSpec() {
        Runtime rt = Runtime.getRuntime();
        System.out.println("=== Machine / environment specification ===");
        System.out.println("  OS            : " + System.getProperty("os.name")
                + " " + System.getProperty("os.version")
                + " (" + System.getProperty("os.arch") + ")");
        System.out.println("  Java          : " + System.getProperty("java.version")
                + " (" + System.getProperty("java.vendor") + ")");
        System.out.println("  Logical cores : " + rt.availableProcessors());
        System.out.println("  Max heap      : " + (rt.maxMemory() / (1024 * 1024)) + " MB");
        System.out.println("  Repeats/size  : " + RUNS + " (averaged)");
        System.out.println("  NOTE: replace this with your actual demo machine's spec in the report.");
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 1. Search: linear vs binary
    // ----------------------------------------------------------------
    private static void benchmarkSearch() throws IOException {
        int[] sizes = {100, 500, 1000, 5000, 10000};
        PrintWriter out = new PrintWriter(new FileWriter("results/search_results.csv"));
        out.println("n,linear_ns,binary_ns");
        System.out.println("== 1. Search comparison (linear vs binary), averaged over " + RUNS + " runs ==");
        System.out.printf("%-10s %-15s %-15s%n", "n", "linear (ns)", "binary (ns)");

        for (int n : sizes) {
            long linTotal = 0, binTotal = 0;
            for (int r = 0; r < RUNS; r++) {
                Integer[] sorted = new Integer[n];
                for (int i = 0; i < n; i++) sorted[i] = i; // already sorted (binary precondition)
                // search 20 keys spread across the array, sum the time
                long ls = System.nanoTime();
                for (int k = 0; k < 20; k++) Searching.linearSearch(sorted, (n / 20) * k);
                long linNs = (System.nanoTime() - ls) / 20;

                long bs = System.nanoTime();
                for (int k = 0; k < 20; k++) Searching.binarySearch(sorted, (n / 20) * k);
                long binNs = (System.nanoTime() - bs) / 20;

                linTotal += linNs; binTotal += binNs;
                raw("search", "linear", n, "", r, linNs);
                raw("search", "binary", n, "", r, binNs);
            }
            long linAvg = linTotal / RUNS, binAvg = binTotal / RUNS;
            out.printf("%d,%d,%d%n", n, linAvg, binAvg);
            System.out.printf("%-10d %-15d %-15d%n", n, linAvg, binAvg);
        }
        out.close();
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 2. Sort: selection, insertion, merge, quick
    // ----------------------------------------------------------------
    private static void benchmarkSort() throws IOException {
        int[] sizes = {100, 500, 1000, 5000, 10000};
        PrintWriter out = new PrintWriter(new FileWriter("results/sort_results.csv"));
        out.println("n,selection_ns,insertion_ns,merge_ns,quick_ns");
        System.out.println("== 2. Sorting comparison, averaged over " + RUNS + " runs ==");
        System.out.printf("%-8s %-13s %-13s %-13s %-13s%n", "n", "selection", "insertion", "merge", "quick");

        for (int n : sizes) {
            long selT = 0, insT = 0, mrgT = 0, qkT = 0;
            for (int r = 0; r < RUNS; r++) {
                Integer[] base = randomIntArray(n);
                selT += time(() -> Sorting.selectionSort(base.clone()));
                insT += time(() -> Sorting.insertionSort(base.clone()));
                mrgT += time(() -> Sorting.mergeSort(base.clone()));
                qkT  += time(() -> Sorting.quickSort(base.clone()));
                raw("sort", "selection", n, "", r, selT);
                raw("sort", "insertion", n, "", r, insT);
                raw("sort", "merge", n, "", r, mrgT);
                raw("sort", "quick", n, "", r, qkT);
            }
            out.printf("%d,%d,%d,%d,%d%n", n, selT / RUNS, insT / RUNS, mrgT / RUNS, qkT / RUNS);
            System.out.printf("%-8d %-13d %-13d %-13d %-13d%n",
                    n, selT / RUNS, insT / RUNS, mrgT / RUNS, qkT / RUNS);
        }
        out.close();
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 3. Hash table: load factor vs collisions / time (fixed table sizes)
    // ----------------------------------------------------------------
    private static void benchmarkHashLoadFactor() throws IOException {
        int[] keyCounts = {100, 1000, 5000, 10000, 20000};
        int[] tableSizes = {128, 1024, 8192};
        PrintWriter out = new PrintWriter(new FileWriter("results/hash_results.csv"));
        out.println("n,table_size,load_factor,collisions,longest_chain,put_ns,get_ns");
        System.out.println("== 3. Hash table load factor (fixed table sizes), averaged over " + RUNS + " runs ==");
        System.out.printf("%-8s %-11s %-12s %-12s %-8s%n", "n", "tableSize", "loadFactor", "collisions", "chain");

        for (int table : tableSizes) {
            for (int n : keyCounts) {
                long putT = 0, getT = 0, coll = 0, chain = 0;
                double lf = 0;
                for (int r = 0; r < RUNS; r++) {
                    MyHashMap<Integer, Integer> map = new MyHashMap<>(table, false); // fixed capacity
                    long ps = System.nanoTime();
                    for (int i = 0; i < n; i++) map.put(i, i);
                    putT += System.nanoTime() - ps;
                    long gs = System.nanoTime();
                    for (int i = 0; i < n; i++) map.get(i);
                    getT += System.nanoTime() - gs;
                    coll += map.collisionCount();
                    chain += map.longestChain();
                    lf = map.currentLoadFactor();
                    raw("hash", "put_ts" + table, n, "lf=" + String.format("%.2f", lf), r, System.nanoTime() - ps);
                }
                out.printf("%d,%d,%.3f,%d,%d,%d,%d%n",
                        n, table, lf, coll / RUNS, chain / RUNS, putT / RUNS, getT / RUNS);
                System.out.printf("%-8d %-11d %-12.2f %-12d %-8d%n", n, table, lf, coll / RUNS, chain / RUNS);
            }
        }
        out.close();
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 4. BST vs balanced (AVL) tree: height + search time
    //    Keys are inserted in ASCENDING order to expose BST degeneration
    //    (height -> n) against the AVL tree staying ~log n.
    // ----------------------------------------------------------------
    private static void benchmarkTrees() throws IOException {
        int[] sizes = {100, 500, 1000, 5000, 10000};
        PrintWriter out = new PrintWriter(new FileWriter("results/tree_results.csv"));
        out.println("n,bst_height,avl_height,bst_insert_ns,avl_insert_ns,bst_search_ns,avl_search_ns");
        System.out.println("== 4. BST vs balanced (AVL) tree, ascending inserts, averaged over " + RUNS + " runs ==");
        System.out.printf("%-8s %-11s %-11s %-13s %-13s%n", "n", "bstHeight", "avlHeight", "bstSearch", "avlSearch");

        for (int n : sizes) {
            long bstIns = 0, avlIns = 0, bstSr = 0, avlSr = 0, bstH = 0, avlH = 0;
            for (int r = 0; r < RUNS; r++) {
                MyBST<Integer, Integer> bst = new MyBST<>();
                MyAVLTree<Integer, Integer> avl = new MyAVLTree<>();
                long b1 = System.nanoTime();
                for (int i = 0; i < n; i++) bst.insert(i, i);
                bstIns += System.nanoTime() - b1;
                long a1 = System.nanoTime();
                for (int i = 0; i < n; i++) avl.insert(i, i);
                avlIns += System.nanoTime() - a1;

                long b2 = System.nanoTime();
                for (int k = 0; k < 50; k++) bst.search((n / 50) * k);
                bstSr += (System.nanoTime() - b2) / 50;
                long a2 = System.nanoTime();
                for (int k = 0; k < 50; k++) avl.search((n / 50) * k);
                avlSr += (System.nanoTime() - a2) / 50;

                bstH += bst.height();
                avlH += avl.height();
                raw("tree", "bst_insert", n, "", r, System.nanoTime() - b1);
                raw("tree", "avl_insert", n, "", r, System.nanoTime() - a1);
            }
            out.printf("%d,%d,%d,%d,%d,%d,%d%n", n, bstH / RUNS, avlH / RUNS,
                    bstIns / RUNS, avlIns / RUNS, bstSr / RUNS, avlSr / RUNS);
            System.out.printf("%-8d %-11d %-11d %-13d %-13d%n",
                    n, bstH / RUNS, avlH / RUNS, bstSr / RUNS, avlSr / RUNS);
        }
        out.close();
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 5. Heap priority dispatch: insert then extractMin all
    // ----------------------------------------------------------------
    private static void benchmarkHeap() throws IOException {
        int[] sizes = {100, 1000, 5000, 10000, 20000};
        PrintWriter out = new PrintWriter(new FileWriter("results/heap_results.csv"));
        out.println("n,insert_ns,extract_ns");
        System.out.println("== 5. Heap priority dispatch, averaged over " + RUNS + " runs ==");
        System.out.printf("%-10s %-15s %-15s%n", "n", "insertAll (ns)", "extractAll (ns)");

        for (int n : sizes) {
            long insT = 0, extT = 0;
            for (int r = 0; r < RUNS; r++) {
                Integer[] vals = randomIntArray(n);
                MyMinHeap<Integer> heap = new MyMinHeap<>();
                long i1 = System.nanoTime();
                for (Integer v : vals) heap.insert(v);
                insT += System.nanoTime() - i1;
                long e1 = System.nanoTime();
                while (!heap.isEmpty()) heap.extractMin();
                extT += System.nanoTime() - e1;
                raw("heap", "insert", n, "", r, System.nanoTime() - i1);
                raw("heap", "extract", n, "", r, System.nanoTime() - e1);
            }
            out.printf("%d,%d,%d%n", n, insT / RUNS, extT / RUNS);
            System.out.printf("%-10d %-15d %-15d%n", n, insT / RUNS, extT / RUNS);
        }
        out.close();
        System.out.println();
    }

    // ----------------------------------------------------------------
    // 6. Graph algorithms: BFS, DFS, Dijkstra, MST at growing sizes
    // ----------------------------------------------------------------
    private static void benchmarkGraph() throws IOException {
        int[] sizes = {50, 100, 200, 500};
        PrintWriter out = new PrintWriter(new FileWriter("results/graph_results.csv"));
        out.println("n,bfs_ns,dfs_ns,dijkstra_ns,mst_ns");
        System.out.println("== 6. Graph algorithms (BFS/DFS/Dijkstra/MST), averaged over " + RUNS + " runs ==");
        System.out.printf("%-8s %-12s %-12s %-13s %-13s%n", "n", "bfs", "dfs", "dijkstra", "mst");

        for (int n : sizes) {
            long bfsT = 0, dfsT = 0, dijT = 0, mstT = 0;
            for (int r = 0; r < RUNS; r++) {
                Graph g = buildSparseGraph(n);
                bfsT += time(() -> GraphTraversal.bfs(g, "N0"));
                dfsT += time(() -> GraphTraversal.dfs(g, "N0"));
                dijT += time(() -> ShortestRoute.findShortestPath(g, "N0", "N" + (n / 2)));
                mstT += time(() -> MinimumSpanningTree.kruskal(g));
                raw("graph", "bfs", n, "", r, bfsT);
                raw("graph", "dfs", n, "", r, dfsT);
                raw("graph", "dijkstra", n, "", r, dijT);
                raw("graph", "mst", n, "", r, mstT);
            }
            out.printf("%d,%d,%d,%d,%d%n", n, bfsT / RUNS, dfsT / RUNS, dijT / RUNS, mstT / RUNS);
            System.out.printf("%-8d %-12d %-12d %-13d %-13d%n", n, bfsT / RUNS, dfsT / RUNS, dijT / RUNS, mstT / RUNS);
        }
        out.close();
        System.out.println();
    }

    // ---- helpers ----

    private interface Task { void run(); }

    private static long time(Task t) {
        long start = System.nanoTime();
        t.run();
        return System.nanoTime() - start;
    }

    private static Integer[] randomIntArray(int n) {
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) a[i] = RNG.nextInt();
        return a;
    }

    private static Graph buildSparseGraph(int n) {
        Graph g = new Graph();
        for (int i = 0; i < n; i++) {
            g.addRoute("N" + i, "N" + ((i + 1) % n), 1 + RNG.nextInt(10), true); // ring -> connected
        }
        for (int i = 0; i < n / 5; i++) { // random shortcuts -> resembles a real network
            g.addRoute("N" + RNG.nextInt(n), "N" + RNG.nextInt(n), 1 + RNG.nextInt(20), true);
        }
        return g;
    }

    private static void raw(String exp, String algo, int n, String extra, int run, long ns) {
        if (rawOut != null) rawOut.printf("%s,%s,%d,%s,%d,%d%n", exp, algo, n, extra, run, ns);
    }
}
