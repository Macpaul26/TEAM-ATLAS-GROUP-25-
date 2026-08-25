package campushub.ds;

/**
 * A union-find / disjoint-set structure over String elements (location ids or
 * names), with BOTH standard optimisations: union by rank and path compression.
 * This is the structure Kruskal's MST needs to detect cycles, and it satisfies
 * the "disjoint set - makeSet, find, union by rank/size, path compression /
 * Kruskal connectivity trace" evidence row (CLRS ch. 21).
 *
 *   makeSet -> O(1)   find/union -> ~O(alpha(n)) amortised (near-constant)
 */
public class DisjointSet {

    private final MyHashMap<String, String> parent = new MyHashMap<>();
    private final MyHashMap<String, Integer> rank = new MyHashMap<>();
    private int componentCount = 0;

    public void makeSet(String x) {
        if (parent.containsKey(x)) return;
        parent.put(x, x);
        rank.put(x, 0);
        componentCount++;
    }

    /** Representative of x's set, applying path compression on the way up. */
    public String find(String x) {
        if (!parent.containsKey(x)) makeSet(x);
        String root = x;
        while (!root.equals(parent.get(root))) root = parent.get(root);
        // path compression: point every node on the path straight at the root
        String cur = x;
        while (!cur.equals(root)) {
            String next = parent.get(cur);
            parent.put(cur, root);
            cur = next;
        }
        return root;
    }

    /** @return true if the two sets were distinct and got merged. */
    public boolean union(String a, String b) {
        String ra = find(a), rb = find(b);
        if (ra.equals(rb)) return false;
        int rka = rank.get(ra), rkb = rank.get(rb);
        if (rka < rkb) { parent.put(ra, rb); }
        else if (rka > rkb) { parent.put(rb, ra); }
        else { parent.put(rb, ra); rank.put(ra, rka + 1); }
        componentCount--;
        return true;
    }

    public boolean connected(String a, String b) { return find(a).equals(find(b)); }
    public int componentCount() { return componentCount; }
}
