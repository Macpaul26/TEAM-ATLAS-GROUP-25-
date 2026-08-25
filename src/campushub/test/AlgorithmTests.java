package campushub.test;

import campushub.algo.BudgetSelector;
import campushub.algo.PriorityDispatcher;
import campushub.algo.ShortestRoute;
import campushub.ds.Graph;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

public class AlgorithmTests {

    public static void run() {
        testPriorityDispatcher();
        testShortestRoute();
        testBudgetSelector();
    }

    private static void testPriorityDispatcher() {
        TestRunner.section("PriorityDispatcher (urgent tickets jump the queue)");
        PriorityDispatcher dispatcher = new PriorityDispatcher();
        // arrival order: two routine tickets, then a security-critical one arrives last
        dispatcher.addRequest(new ServiceRequest("R1", "Ama", "Broken Furniture", 5, "L2", 10, 1, 1));
        dispatcher.addRequest(new ServiceRequest("R2", "Kwesi", "Broken AC", 4, "L1", 10, 2, 2));
        dispatcher.addRequest(new ServiceRequest("R3", "Efua", "Faulty Door Lock", 1, "L4", 50, 10, 3)); // critical, arrived last

        ServiceRequest first = dispatcher.dispatchNext();
        TestRunner.assertEquals("most critical ticket (R3, faulty lock) served first despite arriving last", "R3", first.getId());

        ServiceRequest second = dispatcher.dispatchNext();
        TestRunner.assertEquals("next most urgent (R2, urgency 4) served next", "R2", second.getId());

        ServiceRequest third = dispatcher.dispatchNext();
        TestRunner.assertEquals("least urgent (R1) served last", "R1", third.getId());

        TestRunner.assertTrue("dispatcher empty after all served", !dispatcher.hasWaitingRequests());

        // FIFO tie-break check: two tickets with equal urgency
        PriorityDispatcher tieDispatcher = new PriorityDispatcher();
        tieDispatcher.addRequest(new ServiceRequest("T1", "First", "WiFi Outage", 3, "L5", 5, 1, 100));
        tieDispatcher.addRequest(new ServiceRequest("T2", "Second", "WiFi Outage", 3, "L5", 5, 1, 101));
        TestRunner.assertEquals("equal urgency -> earlier arrival served first", "T1", tieDispatcher.dispatchNext().getId());
    }

    private static void testShortestRoute() {
        TestRunner.section("ShortestRoute (Dijkstra) on a small campus network");
        Graph g = new Graph();
        // classic small graph with an obvious shortcut to verify correctness
        g.addRoute("MaintenanceOffice", "CommonwealthHall", 4, true);
        g.addRoute("MaintenanceOffice", "BalmeLibrary", 1, true);
        g.addRoute("BalmeLibrary", "CommonwealthHall", 1, true);
        g.addRoute("CommonwealthHall", "LegonHall", 2, true);

        ShortestRoute.Result result = ShortestRoute.findShortestPath(g, "MaintenanceOffice", "LegonHall");
        TestRunner.assertTrue("path found", result.isReachable());
        // MaintenanceOffice->BalmeLibrary->CommonwealthHall->LegonHall = 1+1+2 = 4,
        // cheaper than the direct MaintenanceOffice->CommonwealthHall->LegonHall = 4+2 = 6
        TestRunner.assertEquals("shortest distance uses the shortcut via Balme Library", 4.0, result.totalDistance, 0.0001);
        TestRunner.assertEquals("path length (4 hops)", 4, result.path.size());
        TestRunner.assertEquals("path starts at Maintenance Office", "MaintenanceOffice", result.path.get(0));
        TestRunner.assertEquals("path ends at Legon Hall", "LegonHall", result.path.get(result.path.size() - 1));

        ShortestRoute.Result unreachable = ShortestRoute.findShortestPath(g, "MaintenanceOffice", "Unregistered");
        TestRunner.assertTrue("unknown destination reported unreachable", !unreachable.isReachable());
    }

    private static void testBudgetSelector() {
        TestRunner.section("BudgetSelector (0/1 knapsack) on maintenance tickets");
        MyArrayList<ServiceRequest> requests = new MyArrayList<>();
        // cost, benefit chosen so the optimum is NOT just "cheapest first" or "highest benefit first"
        requests.add(new ServiceRequest("B1", "Job1", "WiFi Outage", 3, "L1", 10, 60, 1));
        requests.add(new ServiceRequest("B2", "Job2", "Broken AC", 3, "L2", 20, 100, 2));
        requests.add(new ServiceRequest("B3", "Job3", "Water Leak", 3, "L3", 30, 120, 3));

        BudgetSelector.Result result = BudgetSelector.selectWithinBudget(requests, 50);
        // All subsets: {B1}=60/10, {B2}=100/20, {B3}=120/30, {B1,B2}=160/30,
        // {B1,B3}=180/40, {B2,B3}=220/50, {B1,B2,B3}=infeasible (cost 60 > 50).
        // Optimum within budget 50 is B2+B3 = benefit 220.
        TestRunner.assertEquals("optimal total benefit within budget 50", 220, result.totalBenefit);
        TestRunner.assertTrue("total cost stays within budget", result.totalCost <= 50);
        TestRunner.assertEquals("2 tickets funded", 2, result.fundedRequests.size());

        BudgetSelector.Result zeroBudget = BudgetSelector.selectWithinBudget(requests, 0);
        TestRunner.assertEquals("zero budget funds nothing", 0, zeroBudget.totalBenefit);
        TestRunner.assertEquals("zero budget funds 0 tickets", 0, zeroBudget.fundedRequests.size());
    }
}
