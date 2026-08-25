package campushub.algo;

import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

/**
 * Answers: "which maintenance tickets should Student Affairs / the
 * Maintenance Office fund today with a limited budget, so that total
 * benefit (impact x urgency) is maximised without overspending?"
 *
 * A textbook 0/1 knapsack solved by bottom-up dynamic programming
 * (CLRS ch. 15 covers the DP paradigm this is built on; Goodrich,
 * Tamassia & Goldwasser ch. 12 walks through 0/1 knapsack directly).
 * "0/1" because each request is either fully funded or not funded at
 * all - it cannot be split.
 *
 * Cost is discretised to whole GHS units so it can index the DP table.
 * Time complexity: O(n * W), where n = number of requests and
 * W = budget. Space: O(n * W) for the table (kept, rather than
 * collapsed to O(W), so the funded subset can be reconstructed).
 */
public class BudgetSelector {

    public static class Result {
        public final MyArrayList<ServiceRequest> fundedRequests;
        public final int totalBenefit;
        public final double totalCost;
        Result(MyArrayList<ServiceRequest> fundedRequests, int totalBenefit, double totalCost) {
            this.fundedRequests = fundedRequests;
            this.totalBenefit = totalBenefit;
            this.totalCost = totalCost;
        }
    }

    public static Result selectWithinBudget(MyArrayList<ServiceRequest> requests, int budget) {
        int n = requests.size();
        if (budget < 0) budget = 0;

        int[] cost = new int[n];
        int[] benefit = new int[n];
        for (int i = 0; i < n; i++) {
            cost[i] = (int) Math.round(requests.get(i).getCost());
            benefit[i] = requests.get(i).getBenefit();
        }

        // dp[i][b] = best achievable benefit using the first i requests within budget b
        int[][] dp = new int[n + 1][budget + 1];
        for (int i = 1; i <= n; i++) {
            for (int b = 0; b <= budget; b++) {
                dp[i][b] = dp[i - 1][b]; // option: skip request i-1
                if (cost[i - 1] <= b) {
                    int withItem = dp[i - 1][b - cost[i - 1]] + benefit[i - 1];
                    if (withItem > dp[i][b]) {
                        dp[i][b] = withItem;
                    }
                }
            }
        }

        // backtrack to find which requests were funded
        MyArrayList<ServiceRequest> funded = new MyArrayList<>();
        int remainingBudget = budget;
        double totalCost = 0;
        for (int i = n; i >= 1; i--) {
            if (dp[i][remainingBudget] != dp[i - 1][remainingBudget]) {
                ServiceRequest chosen = requests.get(i - 1);
                funded.add(chosen);
                totalCost += chosen.getCost();
                remainingBudget -= cost[i - 1];
            }
        }

        return new Result(funded, dp[n][budget], totalCost);
    }
}
