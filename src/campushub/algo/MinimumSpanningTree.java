package campushub.algo;

import campushub.algo.Sorting;
import campushub.ds.DisjointSet;
import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyMinHeap;
import campushub.ds.MySet;

/**
 * Module M7 - minimum spanning tree via BOTH Prim and Kruskal. Answers "what is
 * the cheapest set of roads that still connects every location?" - e.g. the
 * minimum cabling/patrol network across campus. Satisfies "Prim and Kruskal -
 * minimum connection network / MST edge list and total cost" (§7). CLRS ch. 23.
 *
 * Prim    grows one tree outward using the custom MyMinHeap as its frontier.
 * Kruskal sorts all edges (custom quicksort) and adds the cheapest that does
 *         not form a cycle, using the custom DisjointSet to detect cycles.
 * On a connected graph both return the SAME total weight - a nice cross-check
 * the tests assert.
 */
public final class MinimumSpanningTree {

    private MinimumSpanningTree() {}

    /** An undirected MST edge (u,v,weight), Comparable by weight for Kruskal. */
    public static class MstEdge implements Comparable<MstEdge> {
        public final String from, to; public final double weight;
        public MstEdge(String from, String to, double weight) {
            this.from = from; this.to = to; this.weight = weight;
        }
        @Override public int compareTo(MstEdge o) { return Double.compare(this.weight, o.weight); }
        @Override public String toString() { return from + " -- " + to + " (" + weight + ")"; }
    }

    public static class Result {
        public final MyArrayList<MstEdge> edges; public final double totalWeight;
        Result(MyArrayList<MstEdge> edges, double totalWeight) {
            this.edges = edges; this.totalWeight = totalWeight;
        }
    }

    // ---------------- Prim ----------------
    private static class Frontier implements Comparable<Frontier> {
        final String from, to; final double weight;
        Frontier(String from, String to, double weight) { this.from = from; this.to = to; this.weight = weight; }
        @Override public int compareTo(Frontier o) { return Double.compare(this.weight, o.weight); }
    }

    public static Result prim(Graph graph, String start) {
        MyArrayList<MstEdge> mst = new MyArrayList<>();
        double total = 0;
        if (!graph.hasLocation(start)) return new Result(mst, 0);
        MySet<String> inTree = new MySet<>();
        MyMinHeap<Frontier> heap = new MyMinHeap<>();
        inTree.add(start);
        pushEdges(graph, start, inTree, heap);
        int target = graph.locationCount();
        while (inTree.size() < target && !heap.isEmpty()) {
            Frontier f = heap.extractMin();
            if (inTree.contains(f.to)) continue; // stale
            inTree.add(f.to);
            mst.add(new MstEdge(f.from, f.to, f.weight));
            total += f.weight;
            pushEdges(graph, f.to, inTree, heap);
        }
        return new Result(mst, total);
    }

    private static void pushEdges(Graph graph, String node, MySet<String> inTree, MyMinHeap<Frontier> heap) {
        for (Graph.Edge e : graph.neighboursOf(node)) {
            if (!inTree.contains(e.to)) heap.insert(new Frontier(node, e.to, e.weight));
        }
    }

    // ---------------- Kruskal ----------------
    public static Result kruskal(Graph graph) {
        MyArrayList<MstEdge> unique = uniqueEdges(graph);
        MstEdge[] arr = unique.toArray(new MstEdge[0]);
        Sorting.quickSort(arr); // ascending by weight (custom quicksort)

        DisjointSet ds = new DisjointSet();
        MyArrayList<String> locs = graph.allLocations();
        for (int i = 0; i < locs.size(); i++) ds.makeSet(locs.get(i));

        MyArrayList<MstEdge> mst = new MyArrayList<>();
        double total = 0;
        for (MstEdge e : arr) {
            if (ds.union(e.from, e.to)) { // union returns true only if no cycle formed
                mst.add(e);
                total += e.weight;
            }
        }
        return new Result(mst, total);
    }

    /** Collapse each bidirectional pair (u->v, v->u) into a single undirected edge. */
    private static MyArrayList<MstEdge> uniqueEdges(Graph graph) {
        MyArrayList<MstEdge> out = new MyArrayList<>();
        MySet<String> seen = new MySet<>();
        MyArrayList<String> locs = graph.allLocations();
        for (int i = 0; i < locs.size(); i++) {
            String u = locs.get(i);
            for (Graph.Edge e : graph.neighboursOf(u)) {
                String v = e.to;
                String key = (u.compareTo(v) <= 0) ? u + "\u0000" + v : v + "\u0000" + u;
                if (seen.add(key)) out.add(new MstEdge(u, v, e.weight));
            }
        }
        return out;
    }
}
