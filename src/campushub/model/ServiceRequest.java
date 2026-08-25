package campushub.model;

/**
 * A maintenance / service request logged against a campus location -
 * e.g. a broken AC unit in a hall, a Wi-Fi outage in a lab, a faulty
 * lock on a hostel room, or a shuttle breakdown at a bus stop.
 *
 * Urgency 1 = most critical (e.g. a faulty lock, a safety hazard),
 * higher numbers = less urgent (e.g. a cosmetic furniture issue). This
 * mirrors the triage-level convention used in incident-management
 * systems generally: lower number = handle first.
 *
 * Implements Comparable so it can be dropped straight into MyMinHeap:
 * the heap's root will always be the most urgent request that arrived
 * earliest among equally urgent ones (FIFO tie-break).
 */
public class ServiceRequest implements Comparable<ServiceRequest> {

    private final String id;
    private final String reporterName;
    private final String issueType;      // e.g. "Broken AC", "WiFi Outage", "Faulty Lock", "Shuttle Breakdown"
    private final int urgencyLevel;      // 1 (critical) .. 5 (routine)
    private final String locationId;     // where the request originated
    private final double cost;           // estimated cost to resolve (GHS: parts, technician-hours, callout fee)
    private final int benefit;           // value/priority score used by the budget selector
    private final long arrivalSequence;  // monotonically increasing arrival order (report/ticket order)

    public ServiceRequest(String id, String reporterName, String issueType, int urgencyLevel,
                           String locationId, double cost, int benefit, long arrivalSequence) {
        if (urgencyLevel < 1 || urgencyLevel > 5) {
            throw new IllegalArgumentException("urgencyLevel must be between 1 (critical) and 5 (routine)");
        }
        this.id = id;
        this.reporterName = reporterName;
        this.issueType = issueType;
        this.urgencyLevel = urgencyLevel;
        this.locationId = locationId;
        this.cost = cost;
        this.benefit = benefit;
        this.arrivalSequence = arrivalSequence;
    }

    public String getId() { return id; }
    public String getReporterName() { return reporterName; }
    public String getIssueType() { return issueType; }
    public int getUrgencyLevel() { return urgencyLevel; }
    public String getLocationId() { return locationId; }
    public double getCost() { return cost; }
    public int getBenefit() { return benefit; }
    public long getArrivalSequence() { return arrivalSequence; }

    @Override
    public int compareTo(ServiceRequest other) {
        if (this.urgencyLevel != other.urgencyLevel) {
            return Integer.compare(this.urgencyLevel, other.urgencyLevel); // lower level = more urgent = comes first
        }
        return Long.compare(this.arrivalSequence, other.arrivalSequence); // FIFO among equal urgency
    }

    @Override
    public String toString() {
        return String.format("Ticket[%s] %s - %s (urgency L%d, est. cost GHS%.0f, at %s)",
                id, issueType, reporterName, urgencyLevel, cost, locationId);
    }
}
