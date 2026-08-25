package campushub.ds;

/**
 * The adjacency-MATRIX representation of the campus network. The brief (§6)
 * requires BOTH representations: the sparse adjacency list (Graph.java, used by
 * the routing algorithms) AND this dense matrix. Keeping both lets the report
 * compare them: the matrix gives O(1) "is there an edge u->v?" lookups but costs
 * O(V^2) space, which is wasteful for our sparse footpath network - the exact
 * space/time trade-off the report is meant to discuss (CLRS ch. 22.1).
 *
 * Locations are mapped to integer row/column indices via a MyHashMap.
 */
public class MatrixGraph {

    private final MyHashMap<String, Integer> index = new MyHashMap<>();
    private final MyArrayList<String> names = new MyArrayList<>();
    private double[][] weight;
    private boolean[][] present;
    private int count;
    private final int capacity;

    public MatrixGraph(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
        this.capacity = capacity;
        this.weight = new double[capacity][capacity];
        this.present = new boolean[capacity][capacity];
    }

    public void addLocation(String name) {
        if (index.containsKey(name)) return;
        if (count == capacity) throw new IllegalStateException("MatrixGraph capacity exceeded");
        index.put(name, count);
        names.add(name);
        count++;
    }

    public void addRoute(String from, String to, double w, boolean bidirectional) {
        addLocation(from); addLocation(to);
        int i = index.get(from), j = index.get(to);
        weight[i][j] = w; present[i][j] = true;
        if (bidirectional) { weight[j][i] = w; present[j][i] = true; }
    }

    public boolean hasEdge(String from, String to) {
        if (!index.containsKey(from) || !index.containsKey(to)) return false;
        return present[index.get(from)][index.get(to)];
    }

    public double weightOf(String from, String to) {
        if (!hasEdge(from, to)) return Double.POSITIVE_INFINITY;
        return weight[index.get(from)][index.get(to)];
    }

    public int locationCount() { return count; }
    public MyArrayList<String> allLocations() { return names; }
}
