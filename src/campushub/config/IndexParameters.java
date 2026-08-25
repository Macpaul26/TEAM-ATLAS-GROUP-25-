package campushub.config;

/**
 * Brief §2 / §15 requirement: at least THREE algorithm parameters must be
 * derived from team members' student index numbers (not hard-coded magic
 * numbers). This class is the single source of truth for those parameters.
 *
 * >>> TEAM ACTION REQUIRED <<<
 * Replace the placeholder index numbers in TEAM_INDEX_NUMBERS with the real
 * 8-digit index numbers of the 14 team members before submission. The values
 * printed by describe() must then be quoted in Dataset_Evidence_Note.docx so
 * the examiner can see the parameters really came from your indices.
 *
 * The derivations are deterministic: the same indices always give the same
 * parameters, which is exactly what you want for reproducible experiments.
 */
public final class IndexParameters {

    /** TODO: swap these placeholders for the team's real index numbers. */
    private static final long[] TEAM_INDEX_NUMBERS = {
            10000001L, 10000002L, 10000003L, 10000004L, 10000005L,
            10000006L, 10000007L, 10000008L, 10000009L, 10000010L,
            10000011L, 10000012L, 10000013L, 10000014L
    };

    private final long[] indices;

    public IndexParameters() { this(TEAM_INDEX_NUMBERS); }

    public IndexParameters(long[] indices) {
        if (indices == null || indices.length == 0) {
            throw new IllegalArgumentException("At least one index number is required");
        }
        this.indices = indices.clone();
    }

    private long sum() {
        long s = 0;
        for (long v : indices) s += v;
        return s;
    }

    private int digitSum() {
        int total = 0;
        for (long v : indices) {
            long x = Math.abs(v);
            while (x > 0) { total += (int) (x % 10); x /= 10; }
        }
        return total;
    }

    /**
     * PARAMETER 1 - hash-table starting capacity.
     * Derived from the team's combined digit sum, snapped up to the next power
     * of two (>= 16) so the table stays power-of-two sized for fast indexing.
     */
    public int hashTableCapacity() {
        int base = Math.max(16, digitSum());
        int cap = 16;
        while (cap < base) cap <<= 1;
        return cap;
    }

    /**
     * PARAMETER 2 - random seed for reproducible benchmark data generation.
     * Every experiment reuses this seed so runs are repeatable on any machine.
     */
    public long randomSeed() {
        return sum() % 100000L;
    }

    /**
     * PARAMETER 3 - route penalty factor applied to poor-condition roads in the
     * graph engine (multiplies edge weight for roads flagged in bad condition).
     * Kept in a sensible 1.10 .. 1.60 band regardless of the raw indices.
     */
    public double routePenaltyFactor() {
        return 1.0 + (digitSum() % 51) / 100.0 + 0.10;
    }

    /**
     * PARAMETER 4 - daily maintenance budget (GHS) used by the knapsack /
     * BudgetSelector optimisation, in a realistic 300 .. 800 band.
     */
    public int budgetConstraint() {
        return 300 + (int) (sum() % 501);
    }

    /**
     * PARAMETER 5 - priority weight used to break scheduling ties / bias the
     * greedy assigner. Kept small so it nudges rather than dominates urgency.
     */
    public int priorityWeight() {
        return 1 + (digitSum() % 5);
    }

    /** Human-readable dump for the evidence note and the console menu banner. */
    public String describe() {
        return "Index-derived parameters (from " + indices.length + " member indices):\n"
             + "  1. hashTableCapacity   = " + hashTableCapacity()   + "  (digit sum snapped to power of two)\n"
             + "  2. randomSeed          = " + randomSeed()          + "  (sum of indices mod 100000)\n"
             + "  3. routePenaltyFactor  = " + String.format("%.2f", routePenaltyFactor()) + "  (bad-road weight multiplier)\n"
             + "  4. budgetConstraint    = GHS " + budgetConstraint() + "  (sum of indices, 300..800 band)\n"
             + "  5. priorityWeight      = " + priorityWeight()      + "  (scheduling tie-break bias)";
    }
}
