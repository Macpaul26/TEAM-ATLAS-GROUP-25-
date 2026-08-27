package campushub;

import campushub.algo.BudgetSelector;
import campushub.algo.GraphTraversal;
import campushub.algo.GreedyAssigner;
import campushub.algo.MinimumSpanningTree;
import campushub.algo.PriorityDispatcher;
import campushub.algo.Searching;
import campushub.algo.ShortestRoute;
import campushub.algo.Sorting;
import campushub.bench.BenchmarkRunner;
import campushub.config.IndexParameters;
import campushub.db.CsvLoader;
import campushub.db.Database;
import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.ds.MyHashMap;
import campushub.ds.MySet;
import campushub.model.ServiceRequest;
import campushub.trace.Traces;

import java.util.Scanner;

/**
 * TEAM ATLAS - GROUP 25 — interactive console menu.
 *
 * This is the examiner-facing entry point required by brief section 8:
 * an examiner can run every demonstration (database load, scheduling,
 * routing, MST, optimisation, search/sort, traces, benchmarks) WITHOUT
 * editing any source code.
 *
 * It runs against the REAL SQLite database (data/campushub.db). On first
 * launch it applies data/schema.sql and loads the seed CSVs; on later
 * launches it reuses the existing database file.
 *
 * Run:  java -cp bin:lib/sqlite-jdbc.jar campushub.Main
 */
public class Main {

    private static final String DB_PATH = "data/campushub.db";
    private static final String DATA_DIR = "data";
    private static final Scanner IN = new Scanner(System.in);

    private static Database db;
    private static IndexParameters params;
    private static Graph graph;
    private static MyHashMap<String, String> names; // locationId -> name

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" TEAM ATLAS - GROUP 25");
        System.out.println(" University of Ghana, Legon");
        System.out.println(" DCIT 204/308 Joint DSA Semester Project");
        System.out.println("============================================================");

        params = new IndexParameters();
        db = Database.connect(DB_PATH);
        ensureLoaded();
        graph = db.loadGraph(params.routePenaltyFactor());
        names = db.loadLocationNames();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = IN.nextLine().trim();
            System.out.println();
            try {
                switch (choice) {
                    case "1":  showDatabaseCounts(); break;
                    case "2":  System.out.println(params.describe()); break;
                    case "3":  demoDispatch(); break;
                    case "4":  demoShortestRoute(); break;
                    case "5":  demoReachability(); break;
                    case "6":  demoMst(); break;
                    case "7":  demoBudget(); break;
                    case "8":  demoGreedyCounterexample(); break;
                    case "9":  demoSearch(); break;
                    case "10": demoSort(); break;
                    case "11": demoTraces(); break;
                    case "12": demoAudit(); break;
                    case "13": BenchmarkRunner.run(); break;
                    case "0":  running = false; break;
                    default:   System.out.println("Unknown option: " + choice);
                }
            } catch (Exception e) {
                System.out.println("!! Operation failed: " + e.getMessage());
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                IN.nextLine();
            }
        }
        db.close();
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println("\n---------------------- MAIN MENU ----------------------");
        System.out.println(" 1. Show database record counts        (DB integration)");
        System.out.println(" 2. Show index-number-derived params   (localisation)");
        System.out.println(" 3. Dispatch next tickets              (priority heap)");
        System.out.println(" 4. Fastest route between locations    (Dijkstra)");
        System.out.println(" 5. Reachable locations from a point   (BFS / DFS)");
        System.out.println(" 6. Minimum connection network         (Prim + Kruskal)");
        System.out.println(" 7. Budget-constrained selection       (0/1 knapsack DP)");
        System.out.println(" 8. Greedy-vs-optimal counterexample   (greedy failure)");
        System.out.println(" 9. Search demo                        (linear vs binary)");
        System.out.println("10. Sort demo                          (4 sorts)");
        System.out.println("11. Show a trace table                 (6 required traces)");
        System.out.println("12. Recent audit events                (stack-based log)");
        System.out.println("13. Run performance benchmarks         (writes results/*.csv)");
        System.out.println(" 0. Exit");
        System.out.print("Select an option: ");
    }

    // ---- first-run bootstrap ----
    private static void ensureLoaded() {
        db.applySchema(DATA_DIR + "/schema.sql");
        if (db.count("locations") == 0) {
            System.out.println("First run: loading seed CSVs into the database...");
            CsvLoader.Report r = CsvLoader.loadAll(db, DATA_DIR);
            System.out.println(r);
        } else {
            System.out.println("Existing database found (" + db.count("locations")
                    + " locations). Reusing data/campushub.db.");
        }
    }

    // ---- 1 ----
    private static void showDatabaseCounts() {
        System.out.println("=== Live database record counts ===");
        String[] tables = {"locations", "roads", "resources", "service_requests", "algorithm_runs", "audit_events"};
        for (String t : tables) {
            System.out.printf("  %-18s %d%n", t, db.count(t));
        }
    }

    // ---- 3 ----
    private static void demoDispatch() {
        MyArrayList<ServiceRequest> reqs = db.loadRequests();
        PriorityDispatcher dispatcher = new PriorityDispatcher();
        for (int i = 0; i < reqs.size(); i++) dispatcher.addRequest(reqs.get(i));
        int show = Math.min(10, dispatcher.waitingCount());
        System.out.println("=== Next " + show + " tickets by urgency (most critical first) ===");
        for (int i = 0; i < show; i++) {
            ServiceRequest r = dispatcher.dispatchNext();
            System.out.printf("  %2d. %s (urgency L%d) at %s%n",
                    i + 1, r.getId(), r.getUrgencyLevel(), nameOf(r.getLocationId()));
        }
        System.out.println("  ... " + dispatcher.waitingCount() + " more waiting.");
    }

    // ---- 4 ----
    private static void demoShortestRoute() {
        listSomeLocations();
        String from = ask("Enter START location id (e.g. L001): ");
        String to = ask("Enter DESTINATION location id (e.g. L030): ");
        ShortestRoute.Result res = ShortestRoute.findShortestPath(graph, from, to);
        if (!res.isReachable()) {
            System.out.println("No route found between " + nameOf(from) + " and " + nameOf(to) + ".");
            return;
        }
        System.out.println("Shortest path (weighted travel time):");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.path.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(nameOf(res.path.get(i)));
        }
        System.out.println("  " + sb);
        System.out.printf("  Total weighted distance: %.2f%n", res.totalDistance);
        db.recordAlgorithmRun("Dijkstra", graph.locationCount(), 0L, 0L);
        db.pushAuditEvent("ROUTE_QUERY", "route", from + "->" + to,
                String.format("distance=%.2f", res.totalDistance));
        System.out.println("  (logged to algorithm_runs + audit_events)");
    }

    // ---- 5 ----
    private static void demoReachability() {
        listSomeLocations();
        String from = ask("Enter a START location id: ");
        MyArrayList<String> bfs = GraphTraversal.bfs(graph, from);
        MyArrayList<String> dfs = GraphTraversal.dfs(graph, from);
        MySet<String> reach = GraphTraversal.reachableFrom(graph, from);
        System.out.println("BFS order (first 12): " + firstNames(bfs, 12));
        System.out.println("DFS order (first 12): " + firstNames(dfs, 12));
        System.out.println("Total reachable from " + nameOf(from) + ": " + reach.size()
                + " of " + graph.locationCount() + " locations.");
    }

    // ---- 6 ----
    private static void demoMst() {
        MyArrayList<String> locs = graph.allLocations();
        MinimumSpanningTree.Result prim = MinimumSpanningTree.prim(graph, locs.get(0));
        MinimumSpanningTree.Result kruskal = MinimumSpanningTree.kruskal(graph);
        System.out.println("=== Minimum connection network ===");
        System.out.printf("  Prim    : %d edges, total weight %.2f%n", prim.edges.size(), prim.totalWeight);
        System.out.printf("  Kruskal : %d edges, total weight %.2f%n", kruskal.edges.size(), kruskal.totalWeight);
        System.out.println("  (Prim and Kruskal agree on total cost: "
                + (Math.abs(prim.totalWeight - kruskal.totalWeight) < 0.01) + ")");
    }

    // ---- 7 ----
    private static void demoBudget() {
        MyArrayList<ServiceRequest> reqs = db.loadRequests();
        // keep the DP table small: use the first 40 requests
        MyArrayList<ServiceRequest> subset = new MyArrayList<>();
        for (int i = 0; i < Math.min(40, reqs.size()); i++) subset.add(reqs.get(i));
        int budget = askInt("Enter budget in GHS (default " + params.budgetConstraint() + "): ",
                params.budgetConstraint());
        BudgetSelector.Result res = BudgetSelector.selectWithinBudget(subset, budget);
        System.out.printf("Funded %d of %d candidate tickets%n", res.fundedRequests.size(), subset.size());
        System.out.printf("  Total benefit: %d   Total cost: GHS %.0f (budget %d)%n",
                res.totalBenefit, res.totalCost, budget);
        int show = Math.min(8, res.fundedRequests.size());
        for (int i = 0; i < show; i++) {
            ServiceRequest r = res.fundedRequests.get(i);
            System.out.printf("   - %s  cost GHS%.0f  benefit %d%n", r.getId(), r.getCost(), r.getBenefit());
        }
    }

    // ---- 8 ----
    private static void demoGreedyCounterexample() {
        System.out.println("=== Greedy vs optimal (a case where greedy FAILS) ===");
        MyArrayList<ServiceRequest> instance = GreedyAssigner.greedyFailureExample();
        int budget = 50;
        System.out.println("Tickets (budget = GHS " + budget + "):");
        for (int i = 0; i < instance.size(); i++) {
            ServiceRequest r = instance.get(i);
            System.out.printf("   %s: cost GHS%.0f, benefit %d, ratio %.2f%n",
                    r.getId(), r.getCost(), r.getBenefit(), r.getBenefit() / r.getCost());
        }
        GreedyAssigner.Result greedy = GreedyAssigner.selectByRatio(instance, budget);
        BudgetSelector.Result dp = BudgetSelector.selectWithinBudget(instance, budget);
        System.out.printf("%nGreedy (best ratio first): benefit %d, cost GHS%.0f -> %s%n",
                greedy.totalBenefit, greedy.totalCost, idsOf(greedy.funded));
        System.out.printf("DP (optimal)             : benefit %d, cost GHS%.0f -> %s%n",
                dp.totalBenefit, dp.totalCost, idsOf(dp.fundedRequests));
        System.out.println("\n=> Greedy is WORSE here (" + greedy.totalBenefit + " < " + dp.totalBenefit
                + "): taking the best ratio first blocks the better B+C combination.");
    }

    private static String idsOf(MyArrayList<ServiceRequest> list) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i).getId());
        }
        return sb.append("}").toString();
    }

    // ---- 9 ----
    private static void demoSearch() {
        System.out.println("=== Search demo (your own data) ===");
        Integer[] arr = readIntArray("Enter your array, comma-separated (e.g. 5,3,8,1,9): ");
        System.out.println("Your array: " + java.util.Arrays.toString(arr));
        int target = askInt("Search for which value? ", arr[0]);

        String type = ask("Type of search - (l)inear or (b)inary? ").trim().toLowerCase();
        if (type.startsWith("b")) {
            boolean isSorted = Sorting.isSorted(arr);
            if (!isSorted) {
                System.out.println("NOTE: this array is NOT sorted. Binary search's precondition");
                System.out.println("requires sorted input - running it anyway so you can see what happens.");
            }
            int result = Searching.binarySearch(arr, target);
            System.out.println("binarySearch(" + target + ") -> index " + result);
            if (!isSorted) {
                int linResult = Searching.linearSearch(arr, target);
                System.out.println("(for comparison) linearSearch(" + target + ") -> index " + linResult);
                if (result != linResult) {
                    System.out.println("^ That mismatch is binary search's precondition being violated.");
                }
            }
        } else {
            int result = Searching.linearSearch(arr, target);
            System.out.println("linearSearch(" + target + ") -> index " + result);
        }
    }

    // ---- 10 ----
    private static void demoSort() {
        System.out.println("=== Sort demo (your own data) ===");
        Integer[] base = readIntArray("Enter your array, comma-separated (e.g. 29,10,14,37,13,1): ");
        System.out.println("Original: " + java.util.Arrays.toString(base));

        System.out.println("Choose: 1) selection  2) insertion  3) merge  4) quick  5) all four");
        String choice = ask("Choice: ").trim();
        Integer[] a;
        switch (choice) {
            case "1":
                a = base.clone(); Sorting.selectionSort(a);
                System.out.println("selection: " + java.util.Arrays.toString(a));
                break;
            case "2":
                a = base.clone(); Sorting.insertionSort(a);
                System.out.println("insertion: " + java.util.Arrays.toString(a));
                break;
            case "3":
                a = base.clone(); Sorting.mergeSort(a);
                System.out.println("merge    : " + java.util.Arrays.toString(a));
                break;
            case "4":
                a = base.clone(); Sorting.quickSort(a);
                System.out.println("quick    : " + java.util.Arrays.toString(a));
                break;
            default:
                a = base.clone(); Sorting.selectionSort(a); System.out.println("selection: " + java.util.Arrays.toString(a));
                a = base.clone(); Sorting.insertionSort(a); System.out.println("insertion: " + java.util.Arrays.toString(a));
                a = base.clone(); Sorting.mergeSort(a);     System.out.println("merge    : " + java.util.Arrays.toString(a));
                a = base.clone(); Sorting.quickSort(a);     System.out.println("quick    : " + java.util.Arrays.toString(a));
        }
    }

    /** Reads a comma-separated list of integers from the user, re-prompting on bad input. */
    private static Integer[] readIntArray(String prompt) {
        while (true) {
            String input = ask(prompt);
            String[] parts = input.split(",");
            try {
                Integer[] arr = new Integer[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    arr[i] = Integer.parseInt(parts[i].trim());
                }
                if (arr.length == 0) throw new NumberFormatException("empty");
                return arr;
            } catch (NumberFormatException e) {
                System.out.println("Please enter whole numbers separated by commas, e.g. 5,3,8,1,9");
            }
        }
    }

    // ---- 11 ----
    private static void demoTraces() {
        System.out.println("Which trace?");
        System.out.println("  a) Binary search   b) Insertion sort   c) Merge sort");
        System.out.println("  d) Dijkstra        e) Kruskal          f) Knapsack DP   g) ALL");
        String c = ask("Choose: ").toLowerCase();
        switch (c) {
            case "a": Traces.binarySearchTrace(); break;
            case "b": Traces.insertionSortTrace(); break;
            case "c": Traces.mergeSortTrace(); break;
            case "d": Traces.dijkstraTrace(); break;
            case "e": Traces.kruskalTrace(); break;
            case "f": Traces.knapsackTrace(); break;
            default:  Traces.runAll();
        }
    }

    // ---- 12 ----
    private static void demoAudit() {
        int n = db.count("audit_events");
        System.out.println("=== Recent audit events (" + n + " total, stack-backed) ===");
        MyArrayList<String> recent = db.recentAuditEvents(10);
        if (recent.size() == 0) {
            System.out.println("  (none yet — run a route query, option 4, to generate one)");
        }
        for (int i = 0; i < recent.size(); i++) System.out.println("  " + recent.get(i));
    }

    // ---- helpers ----
    private static void listSomeLocations() {
        MyArrayList<String> locs = graph.allLocations();
        System.out.println("Some available locations (id -> name):");
        int show = Math.min(12, locs.size());
        for (int i = 0; i < show; i++) {
            String id = locs.get(i);
            System.out.printf("   %s -> %s%n", id, nameOf(id));
        }
        System.out.println("   ... " + locs.size() + " locations total.");
    }

    private static String nameOf(String id) {
        String n = names.get(id);
        return (n == null) ? id : n + " (" + id + ")";
    }

    private static String firstNames(MyArrayList<String> ids, int k) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(k, ids.size()); i++) {
            if (i > 0) sb.append(", ");
            String nm = names.get(ids.get(i));
            sb.append(nm == null ? ids.get(i) : nm);
        }
        if (ids.size() > k) sb.append(", ...");
        return sb.toString();
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return IN.nextLine().trim();
    }

    private static int askInt(String prompt, int dflt) {
        System.out.print(prompt);
        String s = IN.nextLine().trim();
        if (s.isEmpty()) return dflt;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return dflt; }
    }
}
