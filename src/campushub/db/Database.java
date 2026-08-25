package campushub.db;

import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyHashMap;
import campushub.model.ServiceRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Module M2 - the REAL persistent database tier (SQLite via JDBC). This is what
 * makes the DB "part of the running system" (brief §4): the app applies the
 * schema, loads seed CSVs, reads rows back INTO the custom data structures, and
 * writes results (algorithm_runs, audit_events) BACK to disk between runs.
 *
 * JDBC / file I/O are explicitly permitted built-ins (§8); the custom
 * campushub.ds structures still do all the assessed in-memory logic.
 *
 * NOTE: requires lib/sqlite-jdbc.jar on the classpath (see README run steps).
 */
public class Database implements AutoCloseable {

    private final Connection conn;

    private Database(Connection conn) { this.conn = conn; }

    public static Database connect(String dbFilePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            c.createStatement().execute("PRAGMA foreign_keys = ON");
            return new Database(c);
        } catch (Exception e) {
            throw new RuntimeException("Could not open SQLite database at " + dbFilePath
                    + " (is lib/sqlite-jdbc.jar on the classpath?)", e);
        }
    }

    /** Runs schema.sql to (re)create all six tables. */
    public void applySchema(String schemaSqlPath) {
        try {
            String sql = Files.readString(Path.of(schemaSqlPath));
            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (s.isEmpty() || s.startsWith("--")) continue;
                try (Statement st = conn.createStatement()) { st.execute(s); }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply schema: " + e.getMessage(), e);
        }
    }

    Connection connection() { return conn; } // package-private, for CsvLoader

    // ---- counts (for the console menu banner) ----
    public int count(String table) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---- READ: DB rows -> custom structures ----

    /** locationId -> human-readable name, loaded into the custom MyHashMap. */
    public MyHashMap<String, String> loadLocationNames() {
        MyHashMap<String, String> map = new MyHashMap<>();
        query("SELECT location_id, name FROM locations", rs -> map.put(rs.getString(1), rs.getString(2)));
        return map;
    }

    /**
     * Builds the routing Graph from the roads table. Edge weight = travel time
     * scaled by road condition, with the index-derived route penalty applied to
     * poor-condition roads (condition_weight >= 1.4 treated as "bad").
     */
    public Graph loadGraph(double routePenaltyFactor) {
        Graph g = new Graph();
        query("SELECT location_id FROM locations", rs -> g.addLocation(rs.getString(1)));
        query("SELECT from_location_id, to_location_id, travel_time_min, condition_weight FROM roads", rs -> {
            double base = rs.getInt(3) * rs.getDouble(4);
            double w = (rs.getDouble(4) >= 1.4) ? base * routePenaltyFactor : base;
            g.addRoute(rs.getString(1), rs.getString(2), Math.round(w * 100.0) / 100.0, true);
        });
        return g;
    }

    /**
     * Loads service_requests into the custom MyArrayList<ServiceRequest>.
     * The official schema carries no cost/benefit columns (those are needed only
     * by the budget-optimiser demo), so we DERIVE them deterministically from
     * urgency + id here and note it in the report. Everything else is the real
     * stored row.
     */
    public MyArrayList<ServiceRequest> loadRequests() {
        MyArrayList<ServiceRequest> list = new MyArrayList<>();
        long[] seq = {0};
        query("SELECT request_id, source_location_id, category, urgency FROM service_requests ORDER BY request_id", rs -> {
            String id = rs.getString(1);
            String src = rs.getString(2);
            String cat = rs.getString(3);
            int urgency = rs.getInt(4);
            int h = Math.abs(id.hashCode());
            double cost = 20 + (h % 180);            // derived stand-in (GHS)
            int benefit = (6 - urgency) * 25 + (h % 40); // more urgent -> more benefit
            list.add(new ServiceRequest(id, "DB", cat, urgency, src, cost, benefit, seq[0]++));
        });
        return list;
    }

    // ---- WRITE: results back to the database ----

    /** Persists one empirical measurement to algorithm_runs (M10 -> DB). */
    public void recordAlgorithmRun(String algorithmName, int inputSize, long timeNs, long memoryKb) {
        String runId = "APP" + System.nanoTime();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO algorithm_runs(run_id,algorithm_name,input_size,time_ns,memory_kb,date_run) " +
                "VALUES(?,?,?,?,?,date('now'))")) {
            ps.setString(1, runId); ps.setString(2, algorithmName);
            ps.setInt(3, inputSize); ps.setLong(4, timeNs); ps.setLong(5, memoryKb);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Appends a stack-style audit/undo event to audit_events. */
    public void pushAuditEvent(String eventType, String entityType, String entityId, String details) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO audit_events(event_type,entity_type,entity_id,details) VALUES(?,?,?,?)")) {
            ps.setString(1, eventType); ps.setString(2, entityType);
            ps.setString(3, entityId); ps.setString(4, details);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public MyArrayList<String> recentAuditEvents(int limit) {
        MyArrayList<String> out = new MyArrayList<>();
        query("SELECT event_id,event_type,entity_type,entity_id,event_time FROM audit_events " +
              "ORDER BY event_id DESC LIMIT " + limit, rs ->
              out.add("#" + rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) +
                      " " + rs.getString(4) + " @ " + rs.getString(5)));
        return out;
    }

    // ---- helpers ----
    private interface RowConsumer { void accept(ResultSet rs) throws SQLException; }

    private void query(String sql, RowConsumer c) {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) c.accept(rs);
        } catch (SQLException e) { throw new RuntimeException("Query failed: " + sql, e); }
    }

    @Override public void close() {
        try { conn.close(); } catch (SQLException ignored) {}
    }
}
