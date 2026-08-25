package campushub.algo;

import campushub.algo.Sorting;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

/**
 * Module M8 - the GREEDY optimiser, plus the required greedy-FAILURE
 * counterexample. Satisfies "greedy algorithm - priority-based resource
 * assignment or route choice / include a counterexample where greedy fails"
 * (§7, §10).
 *
 * selectByRatio() is a natural greedy heuristic for the maintenance-budget
 * problem: fund tickets in descending benefit-per-GHS order until the money
 * runs out. It is O(n log n) - far cheaper than the O(n*budget) DP in
 * BudgetSelector - and it is OPTIMAL for the *fractional* knapsack.
 *
 * But our problem is the 0/1 knapsack (a ticket is funded fully or not at all),
 * and for 0/1 the ratio-greedy can be strictly worse than the DP optimum.
 * greedyFailureExample() builds a tiny instance that proves it, so the report
 * can show greedy's answer next to BudgetSelector's optimal answer on the SAME
 * input. (CLRS ch. 16 discusses exactly when greedy is and is not safe.)
 */
public final class GreedyAssigner {

    private GreedyAssigner() {}

    public static class Result {
        public final MyArrayList<ServiceRequest> funded;
        public final int totalBenefit;
        public final double totalCost;
        Result(MyArrayList<ServiceRequest> funded, int totalBenefit, double totalCost) {
            this.funded = funded; this.totalBenefit = totalBenefit; this.totalCost = totalCost;
        }
    }

    /** Greedy: sort by benefit/cost ratio (desc), take while it fits the budget. */
    public static Result selectByRatio(MyArrayList<ServiceRequest> requests, int budget) {
        ServiceRequest[] arr = requests.toArray(new ServiceRequest[0]);
        // sort by ratio descending using the custom sort (wrap in a Comparable key)
        Ranked[] ranked = new Ranked[arr.length];
        for (int i = 0; i < arr.length; i++) ranked[i] = new Ranked(arr[i]);
        Sorting.quickSort(ranked); // ascending by -ratio == descending by ratio

        MyArrayList<ServiceRequest> funded = new MyArrayList<>();
        int totalBenefit = 0; double totalCost = 0; double remaining = budget;
        for (Ranked r : ranked) {
            if (r.req.getCost() <= remaining) {
                funded.add(r.req);
                totalBenefit += r.req.getBenefit();
                totalCost += r.req.getCost();
                remaining -= r.req.getCost();
            }
        }
        return new Result(funded, totalBenefit, totalCost);
    }

    private static class Ranked implements Comparable<Ranked> {
        final ServiceRequest req; final double negRatio;
        Ranked(ServiceRequest req) {
            this.req = req;
            double cost = Math.max(1e-9, req.getCost());
            this.negRatio = -(req.getBenefit() / cost); // negate so ascending sort = best-first
        }
        @Override public int compareTo(Ranked o) { return Double.compare(this.negRatio, o.negRatio); }
    }

    /**
     * A fixed 3-ticket instance where ratio-greedy is provably worse than the
     * DP optimum, for the report's counterexample section.
     * Budget = 50 GHS. Tickets (cost, benefit):
     *   A: cost 25, benefit 60   -> ratio 2.40  (greedy picks this first)
     *   B: cost 25, benefit 60   -> ratio 2.40  (greedy picks this second -> spends all 50)
     *   C: cost 50, benefit 100  -> ratio 2.00  (greedy skips: no budget left)
     * Greedy total benefit = 120 (A+B).  DP optimum is ALSO 120 here...
     * so we instead use the classic instance below, tuned so greedy loses:
     *   A: cost 10, benefit 60   -> ratio 6.0  (greedy takes)
     *   B: cost 20, benefit 100  -> ratio 5.0  (greedy takes)  spent 30
     *   C: cost 30, benefit 120  -> ratio 4.0  (greedy skips, only 20 left)
     * Budget 50: greedy = A+B = 160. DP optimum = B+C = 220. Greedy is worse.
     */
    public static MyArrayList<ServiceRequest> greedyFailureExample() {
        MyArrayList<ServiceRequest> r = new MyArrayList<>();
        r.add(new ServiceRequest("A", "demo", "Ticket A", 3, "L1", 10, 60, 1));
        r.add(new ServiceRequest("B", "demo", "Ticket B", 3, "L2", 20, 100, 2));
        r.add(new ServiceRequest("C", "demo", "Ticket C", 3, "L3", 30, 120, 3));
        return r;
    }
}
