package campushub.db;

import campushub.ds.MyArrayList;
import campushub.ds.MySet;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Module M2 - reads the five seed CSVs, VALIDATES them (headers, minimum row
 * counts, foreign-key integrity, CHECK-style ranges) and bulk-inserts them into
 * the SQLite database in FK-safe order.
 *
 * Includes a small quote-aware CSV parser, because at least one location name
 * ("University Hospital, Legon") contains a comma and would break a naive
 * split(","). File reading/parsing is a permitted built-in (§8).
 */
public final class CsvLoader {

    private CsvLoader() {}

    public static class Report {
        public int locations, roads, resources, requests, runs;
        public final MyArrayList<String> warnings = new MyArrayList<>();
        public boolean meetsMinimums() {
            return locations >= 50 && roads >= 100 && resources >= 30 && requests >= 300 && runs >= 30;
        }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("  locations=%d (min 50)%n", locations));
            sb.append(String.format("  roads=%d (min 100)%n", roads));
            sb.append(String.format("  resources=%d (min 30)%n", resources));
            sb.append(String.format("  service_requests=%d (min 300)%n", requests));
            sb.append(String.format("  algorithm_runs=%d (min 30)%n", runs));
            sb.append("  minimums satisfied: ").append(meetsMinimums() ? "YES" : "NO");
            for (int i = 0; i < warnings.size(); i++) sb.append("\n  WARN: ").append(warnings.get(i));
            return sb.toString();
        }
    }

    public static Report loadAll(Database db, String dataDir) {
        Report r = new Report();
        Connection c = db.connection();
        try {
            c.setAutoCommit(false);

            MySet<String> locIds = new MySet<>();
            // 1. locations
            for (String[] row : read(dataDir + "/locations.csv")) {
                insert(c, "INSERT INTO locations VALUES(?,?,?,?,?,?)", ps -> {
                    ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setString(3, row[2]);
                    ps.setString(4, row[3]); ps.setDouble(5, Double.parseDouble(row[4]));
                    ps.setDouble(6, Double.parseDouble(row[5]));
                });
                locIds.add(row[0]); r.locations++;
            }
            // 2. roads (FK check)
            for (String[] row : read(dataDir + "/roads.csv")) {
                if (!locIds.contains(row[1]) || !locIds.contains(row[2])) {
                    r.warnings.add("road " + row[0] + " references unknown location; skipped"); continue;
                }
                insert(c, "INSERT INTO roads VALUES(?,?,?,?,?,?)", ps -> {
                    ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setString(3, row[2]);
                    ps.setDouble(4, Double.parseDouble(row[3])); ps.setInt(5, Integer.parseInt(row[4]));
                    ps.setDouble(6, Double.parseDouble(row[5]));
                });
                r.roads++;
            }
            // 3. resources (FK check)
            for (String[] row : read(dataDir + "/resources.csv")) {
                if (!locIds.contains(row[2])) {
                    r.warnings.add("resource " + row[0] + " has unknown home location; skipped"); continue;
                }
                insert(c, "INSERT INTO resources VALUES(?,?,?,?,?)", ps -> {
                    ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setString(3, row[2]);
                    ps.setInt(4, Integer.parseInt(row[3])); ps.setString(5, row[4]);
                });
                r.resources++;
            }
            // 4. service_requests (FK + urgency range check)
            for (String[] row : read(dataDir + "/service_requests.csv")) {
                if (!locIds.contains(row[1]) || !locIds.contains(row[2])) {
                    r.warnings.add("request " + row[0] + " references unknown location; skipped"); continue;
                }
                int urgency = Integer.parseInt(row[4]);
                if (urgency < 1 || urgency > 5) {
                    r.warnings.add("request " + row[0] + " urgency out of range; skipped"); continue;
                }
                insert(c, "INSERT INTO service_requests VALUES(?,?,?,?,?,?,?,?)", ps -> {
                    ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setString(3, row[2]);
                    ps.setString(4, row[3]); ps.setInt(5, urgency); ps.setString(6, row[5]);
                    ps.setString(7, row[6]); ps.setString(8, row[7]);
                });
                r.requests++;
            }
            // 5. algorithm_runs
            for (String[] row : read(dataDir + "/algorithm_runs.csv")) {
                insert(c, "INSERT INTO algorithm_runs VALUES(?,?,?,?,?,?)", ps -> {
                    ps.setString(1, row[0]); ps.setString(2, row[1]); ps.setInt(3, Integer.parseInt(row[2]));
                    ps.setLong(4, Long.parseLong(row[3])); ps.setLong(5, Long.parseLong(row[4]));
                    ps.setString(6, row[5]);
                });
                r.runs++;
            }
            c.commit();
        } catch (Exception e) {
            try { c.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("CSV load failed: " + e.getMessage(), e);
        } finally {
            try { c.setAutoCommit(true); } catch (Exception ignored) {}
        }
        return r;
    }

    private interface Binder { void bind(PreparedStatement ps) throws Exception; }

    private static void insert(Connection c, String sql, Binder b) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(sql)) { b.bind(ps); ps.executeUpdate(); }
    }

    /** Reads a CSV, skips the header, returns rows as String[] (quote-aware). */
    private static MyArrayList<String[]> read(String path) throws Exception {
        MyArrayList<String[]> rows = new MyArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of(path))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    /** Minimal RFC-4180-style parser: handles "quoted, fields" with commas. */
    static String[] parseCsvLine(String line) {
        MyArrayList<String> fields = new MyArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else cur.append(ch);
            } else {
                if (ch == '"') inQuotes = true;
                else if (ch == ',') { fields.add(cur.toString().trim()); cur.setLength(0); }
                else cur.append(ch);
            }
        }
        fields.add(cur.toString().trim());
        String[] out = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) out[i] = fields.get(i);
        return out;
    }
}
