package campushub.model;

/** A dispatchable resource: a maintenance technician (electrician, plumber, IT support), or a shuttle/van. */
public class Resource {
    private final String id;
    private final String name;
    private final String type;      // e.g. "Electrician", "IT Technician", "Locksmith", "Shuttle"
    private String currentLocationId;
    private boolean available;

    public Resource(String id, String name, String type, String currentLocationId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.currentLocationId = currentLocationId;
        this.available = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCurrentLocationId() { return currentLocationId; }
    public boolean isAvailable() { return available; }

    public void setCurrentLocationId(String locationId) { this.currentLocationId = locationId; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%s[%s] (%s) at %s - %s", type, id, name,
                currentLocationId, available ? "available" : "busy");
    }
}
