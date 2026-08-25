package campushub.db;

import campushub.ds.MyArrayList;
import campushub.ds.MyHashMap;
import campushub.model.Location;
import campushub.model.Resource;
import campushub.model.ServiceRequest;

/**
 * The "database" tier of the system. Every table is a MyHashMap keyed
 * by entity ID, giving O(1) average-case lookup/insert/delete instead
 * of the O(n) scan a plain list would require. This plays the role a
 * SQL table + primary-key index would play in the full system; the
 * structures team's job is to provide these O(1) in-memory equivalents
 * that the rest of the app (and the algorithms below) build on.
 */
public class CampusHubDatabase {

    private final MyHashMap<String, Location> locations = new MyHashMap<>();
    private final MyHashMap<String, ServiceRequest> requests = new MyHashMap<>();
    private final MyHashMap<String, Resource> resources = new MyHashMap<>();

    // ---- Locations ----
    public void addLocation(Location location) { locations.put(location.getId(), location); }
    public Location getLocation(String id) { return locations.get(id); }
    public MyArrayList<Location> allLocations() { return locations.values(); }

    // ---- Requests ----
    public void addRequest(ServiceRequest request) { requests.put(request.getId(), request); }
    public ServiceRequest getRequest(String id) { return requests.get(id); }
    public ServiceRequest removeRequest(String id) { return requests.remove(id); }
    public MyArrayList<ServiceRequest> allRequests() { return requests.values(); }
    public int requestCount() { return requests.size(); }

    // ---- Resources ----
    public void addResource(Resource resource) { resources.put(resource.getId(), resource); }
    public Resource getResource(String id) { return resources.get(id); }
    public MyArrayList<Resource> allResources() { return resources.values(); }

    /** Finds the first available resource of a given type, or null if none free. */
    public Resource findAvailableResource(String type) {
        MyArrayList<Resource> all = resources.values();
        for (int i = 0; i < all.size(); i++) {
            Resource r = all.get(i);
            if (r.getType().equals(type) && r.isAvailable()) {
                return r;
            }
        }
        return null;
    }
}
