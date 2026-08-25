package campushub.ds;

/**
 * A weighted, optionally-directed graph represented as an adjacency
 * list: a MyHashMap from location name -> MyLinkedList of outgoing
 * edges. This is the standard adjacency-list representation from
 * Cormen, Leiserson, Rivest and Stein (2009), ch. 22, chosen over an
 * adjacency matrix because our campus footpath/road network is sparse
 * (each hall, hostel or shuttle stop only connects to a handful of
 * others), giving O(V + E) space instead of O(V^2).
 */
public class Graph {

    public static class Edge {
        public final String to;
        public final double weight;
        public Edge(String to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private final MyHashMap<String, MyLinkedList<Edge>> adjacency = new MyHashMap<>();

    public void addLocation(String name) {
        if (!adjacency.containsKey(name)) {
            adjacency.put(name, new MyLinkedList<>());
        }
    }

    /** Adds a weighted route. If bidirectional, adds the reverse edge too. */
    public void addRoute(String from, String to, double weight, boolean bidirectional) {
        addLocation(from);
        addLocation(to);
        adjacency.get(from).addLast(new Edge(to, weight));
        if (bidirectional) {
            adjacency.get(to).addLast(new Edge(from, weight));
        }
    }

    public MyLinkedList<Edge> neighboursOf(String location) {
        MyLinkedList<Edge> edges = adjacency.get(location);
        return edges == null ? new MyLinkedList<>() : edges;
    }

    public boolean hasLocation(String location) {
        return adjacency.containsKey(location);
    }

    public MyArrayList<String> allLocations() {
        return adjacency.keys();
    }

    public int locationCount() {
        return adjacency.size();
    }
}
