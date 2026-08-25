package campushub.algo;

import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyHashMap;
import campushub.ds.MyLinkedList;
import campushub.ds.MyMinHeap;

/**
 * Answers: "what's the fastest route across campus between two
 * locations?" - e.g. from the Maintenance Office to a hall reporting a
 * broken AC, or from a shuttle stop to the nearest depot.
 *
 * A textbook Dijkstra's-algorithm implementation (Dijkstra, 1959, as
 * presented in CLRS ch. 24.3 and Sedgewick & Wayne ch. 4.4), using
 * MyMinHeap as the priority queue of frontier nodes and MyHashMap for
 * the distance/predecessor tables. Assumes non-negative edge weights
 * (travel times/distances cannot be negative), which is the standard
 * precondition for Dijkstra to be correct.
 *
 * Time complexity with a binary heap: O((V + E) log V).
 */
public class ShortestRoute {

    /** A (location, currentBestDistance) pair placed on the heap frontier. */
    private static class Candidate implements Comparable<Candidate> {
        final String location;
        final double distance;
        Candidate(String location, double distance) {
            this.location = location;
            this.distance = distance;
        }
        @Override
        public int compareTo(Candidate other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static class Result {
        public final MyArrayList<String> path; // ordered list of location names, empty if unreachable
        public final double totalDistance;
        Result(MyArrayList<String> path, double totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }
        public boolean isReachable() { return !path.isEmpty(); }
    }

    public static Result findShortestPath(Graph graph, String start, String destination) {
        MyHashMap<String, Double> distance = new MyHashMap<>();
        MyHashMap<String, String> previous = new MyHashMap<>();
        MyHashMap<String, Boolean> settled = new MyHashMap<>();
        MyMinHeap<Candidate> frontier = new MyMinHeap<>();

        if (!graph.hasLocation(start) || !graph.hasLocation(destination)) {
            return new Result(new MyArrayList<>(), Double.POSITIVE_INFINITY);
        }

        distance.put(start, 0.0);
        frontier.insert(new Candidate(start, 0.0));

        while (!frontier.isEmpty()) {
            Candidate current = frontier.extractMin();
            if (Boolean.TRUE.equals(settled.get(current.location))) {
                continue; // stale entry left over from a relaxed edge; skip it
            }
            settled.put(current.location, true);

            if (current.location.equals(destination)) {
                break; // shortest distance to destination is finalised
            }

            MyLinkedList<Graph.Edge> edges = graph.neighboursOf(current.location);
            for (int i = 0; i < edges.size(); i++) {
                Graph.Edge edge = edges.get(i);
                double candidateDist = current.distance + edge.weight;
                Double bestKnown = distance.get(edge.to);
                if (bestKnown == null || candidateDist < bestKnown) {
                    distance.put(edge.to, candidateDist);
                    previous.put(edge.to, current.location);
                    frontier.insert(new Candidate(edge.to, candidateDist));
                }
            }
        }

        Double finalDistance = distance.get(destination);
        if (finalDistance == null) {
            return new Result(new MyArrayList<>(), Double.POSITIVE_INFINITY); // unreachable
        }

        // reconstruct path by walking predecessors backwards
        MyArrayList<String> reversed = new MyArrayList<>();
        String step = destination;
        while (step != null) {
            reversed.add(step);
            step = previous.get(step);
        }
        MyArrayList<String> path = new MyArrayList<>();
        for (int i = reversed.size() - 1; i >= 0; i--) {
            path.add(reversed.get(i));
        }
        return new Result(path, finalDistance);
    }
}
