package campushub.gui;

import campushub.config.IndexParameters;
import campushub.db.Database;
import campushub.ds.Graph;
import campushub.ds.MyHashMap;

/**
 * Holds references to the real backend objects (database connection,
 * campus graph, location-name lookup, index-derived parameters) so every
 * page panel can call the exact same classes the console menu uses,
 * without each panel needing its own bootstrap logic.
 */
public class AppContext {
    public final Database db;
    public final Graph graph;
    public final MyHashMap<String, String> names; // locationId -> name
    public final IndexParameters params;

    public AppContext(Database db, Graph graph, MyHashMap<String, String> names, IndexParameters params) {
        this.db = db;
        this.graph = graph;
        this.names = names;
        this.params = params;
    }

    public String nameOf(String id) {
        String n = names.get(id);
        return (n == null) ? id : n;
    }

    public String nameWithId(String id) {
        String n = names.get(id);
        return (n == null) ? id : n + " (" + id + ")";
    }
}
