package campushub.algo;

import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyQueue;
import campushub.ds.MySet;
import campushub.ds.MyStack;

/**
 * Module M7 - BFS and DFS over the adjacency-list Graph. Answers "which
 * locations are reachable from the dispatch point, and in what order do we
 * discover them?". Satisfies "BFS and DFS - reachable locations and traversal
 * orders / trace table and graph diagram" (§7). CLRS ch. 22.
 *
 * BFS uses the custom FIFO MyQueue (level-by-level, shortest hop count).
 * DFS uses the custom LIFO MyStack (go deep before wide).
 */
public final class GraphTraversal {

    private GraphTraversal() {}

    /** Breadth-first visit order starting at 'start'. */
    public static MyArrayList<String> bfs(Graph graph, String start) {
        MyArrayList<String> order = new MyArrayList<>();
        if (!graph.hasLocation(start)) return order;
        MySet<String> seen = new MySet<>();
        MyQueue<String> queue = new MyQueue<>();
        queue.enqueue(start); seen.add(start);
        while (!queue.isEmpty()) {
            String node = queue.dequeue();
            order.add(node);
            MyArrayList<Graph.Edge> edges = toList(graph, node);
            for (int i = 0; i < edges.size(); i++) {
                String next = edges.get(i).to;
                if (seen.add(next)) queue.enqueue(next); // add() returns true only if new
            }
        }
        return order;
    }

    /** Depth-first visit order starting at 'start' (iterative, explicit stack). */
    public static MyArrayList<String> dfs(Graph graph, String start) {
        MyArrayList<String> order = new MyArrayList<>();
        if (!graph.hasLocation(start)) return order;
        MySet<String> seen = new MySet<>();
        MyStack<String> stack = new MyStack<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (!seen.add(node)) continue; // already visited
            order.add(node);
            MyArrayList<Graph.Edge> edges = toList(graph, node);
            // push in reverse so the first neighbour is explored first
            for (int i = edges.size() - 1; i >= 0; i--) {
                String next = edges.get(i).to;
                if (!seen.contains(next)) stack.push(next);
            }
        }
        return order;
    }

    /** Set of all locations reachable from 'start' (via BFS). */
    public static MySet<String> reachableFrom(Graph graph, String start) {
        MySet<String> reachable = new MySet<>();
        MyArrayList<String> order = bfs(graph, start);
        for (int i = 0; i < order.size(); i++) reachable.add(order.get(i));
        return reachable;
    }

    private static MyArrayList<Graph.Edge> toList(Graph graph, String node) {
        MyArrayList<Graph.Edge> out = new MyArrayList<>();
        for (Graph.Edge e : graph.neighboursOf(node)) out.add(e); // uses the new iterator
        return out;
    }
}
