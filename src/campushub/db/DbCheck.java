package campushub.db;

import campushub.algo.MinimumSpanningTree;
import campushub.algo.ShortestRoute;
import campushub.config.IndexParameters;
import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

/** End-to-end DB round-trip check (evidence that the database tier really runs). */
public class DbCheck {
    public static void main(String[] args) {
        IndexParameters params = new IndexParameters();
        String dbPath = "data/campushub.db";
        new java.io.File(dbPath).delete();

        try (Database db = Database.connect(dbPath)) {
            System.out.println("1) applying schema...");
            db.applySchema("data/schema.sql");

            System.out.println("2) loading + validating seed CSVs...");
            CsvLoader.Report report = CsvLoader.loadAll(db, "data");
            System.out.println(report);

            System.out.println("\n3) reading rows back into custom structures...");
            System.out.println("   locations in DB : " + db.count("locations"));
            System.out.println("   roads in DB     : " + db.count("roads"));
            System.out.println("   requests in DB  : " + db.count("service_requests"));

            Graph g = db.loadGraph(params.routePenaltyFactor());
            System.out.println("   graph built     : " + g.locationCount() + " nodes");
            MyArrayList<ServiceRequest> reqs = db.loadRequests();
            System.out.println("   requests loaded : " + reqs.size());

            System.out.println("\n4) running algorithms on DB-loaded data...");
            MyArrayList<String> locs = g.allLocations();
            String from = locs.get(0), to = locs.get(locs.size() / 2);
            ShortestRoute.Result route = ShortestRoute.findShortestPath(g, from, to);
            System.out.println("   Dijkstra " + from + " -> " + to + " : distance=" +
                    String.format("%.2f", route.totalDistance));
            MinimumSpanningTree.Result mst = MinimumSpanningTree.kruskal(g);
            System.out.println("   Kruskal MST edges=" + mst.edges.size() +
                    " totalWeight=" + String.format("%.2f", mst.totalWeight));

            System.out.println("\n5) writing results BACK to the database...");
            db.recordAlgorithmRun("Dijkstra", g.locationCount(), 123456L, 64L);
            db.pushAuditEvent("ROUTE_QUERY", "route", from + "->" + to,
                    "distance=" + String.format("%.2f", route.totalDistance));
            System.out.println("   algorithm_runs now : " + db.count("algorithm_runs"));
            System.out.println("   audit_events now   : " + db.count("audit_events"));
            System.out.println("   latest audit       : " + db.recentAuditEvents(1).get(0));

            System.out.println("\nDB ROUND-TRIP OK");
        }
    }
}
