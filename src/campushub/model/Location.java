package campushub.model;

/** A physical point on campus - a hall, hostel, lab, department building, or shuttle stop. */
public class Location {
    private final String id;
    private final String name;

    public Location(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
