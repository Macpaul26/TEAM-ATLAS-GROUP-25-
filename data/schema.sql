-- Ghana Smart Service Operations Optimizer
-- SQLite schema for all 6 tables (brief §4 + Data_Dictionary.docx).
-- Load first, then import the 5 seed CSVs in FK-safe order:
--   locations -> roads / resources / service_requests -> algorithm_runs
-- audit_events is populated at runtime, not seeded.

PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS audit_events;
DROP TABLE IF EXISTS algorithm_runs;
DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS roads;
DROP TABLE IF EXISTS locations;

CREATE TABLE locations (
    location_id   TEXT PRIMARY KEY,
    name          TEXT NOT NULL,
    area          TEXT NOT NULL,
    location_type TEXT NOT NULL CHECK (location_type IN
                    ('Hostel','Academic','Library','Health','Admin',
                     'Security','Retail','Sports','Shuttle')),
    x_coord       REAL NOT NULL,
    y_coord       REAL NOT NULL
);

CREATE TABLE roads (
    road_id          TEXT PRIMARY KEY,
    from_location_id TEXT NOT NULL REFERENCES locations(location_id),
    to_location_id   TEXT NOT NULL REFERENCES locations(location_id),
    distance_km      REAL NOT NULL CHECK (distance_km > 0),
    travel_time_min  INTEGER NOT NULL CHECK (travel_time_min > 0),
    condition_weight REAL NOT NULL CHECK (condition_weight >= 0.5)
);
CREATE INDEX idx_roads_from ON roads(from_location_id);
CREATE INDEX idx_roads_to   ON roads(to_location_id);

CREATE TABLE resources (
    resource_id         TEXT PRIMARY KEY,
    resource_type       TEXT NOT NULL,
    home_location_id    TEXT NOT NULL REFERENCES locations(location_id),
    capacity            INTEGER NOT NULL CHECK (capacity > 0),
    availability_status TEXT NOT NULL CHECK (availability_status IN
                          ('AVAILABLE','BUSY','MAINTENANCE'))
);

CREATE TABLE service_requests (
    request_id              TEXT PRIMARY KEY,
    source_location_id      TEXT NOT NULL REFERENCES locations(location_id),
    destination_location_id TEXT NOT NULL REFERENCES locations(location_id),
    category                TEXT NOT NULL,
    urgency                 INTEGER NOT NULL CHECK (urgency BETWEEN 1 AND 5),
    time_submitted          TEXT NOT NULL,
    deadline                TEXT NOT NULL,
    status                  TEXT NOT NULL CHECK (status IN
                              ('NEW','IN_PROGRESS','COMPLETED','CANCELLED'))
);
CREATE INDEX idx_requests_status  ON service_requests(status);
CREATE INDEX idx_requests_urgency ON service_requests(urgency);

CREATE TABLE algorithm_runs (
    run_id         TEXT PRIMARY KEY,
    algorithm_name TEXT NOT NULL,
    input_size     INTEGER NOT NULL CHECK (input_size > 0),
    time_ns        INTEGER NOT NULL CHECK (time_ns >= 0),
    memory_kb      INTEGER NOT NULL CHECK (memory_kb >= 0),
    date_run       TEXT NOT NULL
);

CREATE TABLE audit_events (
    event_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type  TEXT NOT NULL,
    entity_type TEXT NOT NULL,
    entity_id   TEXT NOT NULL,
    details     TEXT,
    event_time  TEXT NOT NULL DEFAULT (datetime('now'))
);
