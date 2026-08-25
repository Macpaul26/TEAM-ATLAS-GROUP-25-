# Foundation Squad — What You Own

**4 members · Week 1 focus (you unblock everyone else) · Roles: Project Lead & Systems Architect, Local Context & Dataset Lead, Database Architect, Database Integration Engineer**

Your job is to lock the **local context, the dataset, the database schema, the
loader, and the index-number-derived parameters** so every other squad has real,
validated data to build against. If you're late, everyone is late — that's why
you finish Week 1.

---

## 1. Why this squad exists

The Structures Squad can't test their hash map on real data, and the Algorithms
Squad can't run Dijkstra on the campus network, until the database exists and is
loaded. You own the bottom of the stack: **data in, validated, queryable.**

---

## 2. What's already built (your deliverables, done)

| Deliverable | File | What it does |
|---|---|---|
| Ghana local context | (Legon campus service hub) | Real hall/hostel/lab/shuttle names, campus road network |
| Database schema | `data/schema.sql` | 6 tables with primary keys, foreign keys, CHECK constraints, indexes |
| Seed data | `data/*.csv` | locations (56), roads (140), resources (32), service_requests (320), algorithm_runs (48) |
| JDBC database layer | `db/Database.java` | connect, apply schema, count, load into custom structures, write results back, audit log |
| CSV loader + validation | `db/CsvLoader.java` | parses CSVs (handles quoted commas), validates, inserts, reports min-count status |
| Round-trip proof | `db/DbCheck.java` | loads everything, runs algorithms on DB data, writes results back — evidence the DB really runs |
| Index-derived parameters | `config/IndexParameters.java` | derives 5 operational parameters from member index numbers |

The six tables are: `locations`, `roads`, `resources`, `service_requests`,
`algorithm_runs`, `audit_events`. The last one starts empty and is filled at
runtime by the app's stack-based audit logging.

---

## 3. The one thing you MUST do: real index numbers

Open `config/IndexParameters.java`. At the top is:

```java
private static final long[] TEAM_INDEX_NUMBERS = {
        10000001L, 10000002L, ...   // <-- PLACEHOLDERS
};
```

**Replace these 14 placeholders with the team's real 8-digit index numbers.**
This is a graded AI-resistance requirement (brief §2, §15): at least three
algorithm parameters must be derived from member index numbers. We derive five:

| Parameter | Meaning | How it's derived |
|---|---|---|
| `hashTableCapacity` | starting bucket count for hash experiments | digit sum snapped to a power of two |
| `randomSeed` | reproducible benchmark workload seed | sum of indices mod 100000 |
| `routePenaltyFactor` | multiplier for bad-road edges in routing | mapped into 1.10–1.60 |
| `budgetConstraint` | default GHS budget for the knapsack | sum of indices, banded to 300–800 |
| `priorityWeight` | scheduling tie-break bias | derived digit |

Run `java -cp bin campushub.config.IndexParameters` (or menu option 2) to print
them. Screenshot that for the report — it proves the parameters are yours.

---

## 4. Data dictionary (know your schema for defense)

- **locations**(location_id PK, name, area, location_type, x_coord, y_coord)
- **roads**(road_id PK, from_location_id FK, to_location_id FK, distance_km,
  travel_time_min, condition_weight) — the weighted graph edges
- **service_requests**(request_id PK, source_location_id FK,
  destination_location_id FK, category, urgency 1–5, time_submitted, deadline, status)
- **resources**(resource_id PK, resource_type, home_location_id FK, capacity,
  availability_status)
- **algorithm_runs**(run_id PK, algorithm_name, input_size, time_ns, memory_kb, date_run)
- **audit_events**(event_id PK auto, event_type, entity_type, entity_id, details, event_time)

Foreign keys are enforced (`PRAGMA foreign_keys = ON`) **and** independently
re-checked by `CsvLoader` before insertion. Load order respects FKs:
locations → roads/resources/requests → algorithm_runs.

> **Note for the report:** the official schema has no cost/benefit columns on
> `service_requests` (those are only needed by the budget optimiser). `Database.loadRequests()`
> derives them deterministically from urgency + id hash and this is documented in
> the code. Mention it so the marker isn't surprised.

---

## 5. What each Foundation member should defend orally

- **Project Lead / Architect:** the overall data-flow (CSV → DB → structures →
  algorithms → menu), and why the DB is part of the running system, not just storage.
- **Context & Dataset Lead:** how the dataset was constructed from public campus
  knowledge with no personal data, and the realistic constraints (traffic via
  `condition_weight`, urgency levels, limited resources).
- **Database Architect:** the schema — keys, foreign keys, CHECK constraints,
  indexes, and why each exists.
- **Database Integration Engineer:** `Database.java` + `CsvLoader.java` — how a
  row travels from CSV to a `MyHashMap`/`Graph`, and how results are written back.

---

## 6. If an examiner asks you to change something live

- **Add a new location:** insert a row in `locations.csv` (and any roads), delete
  `data/campushub.db`, relaunch the menu — it reloads. Or `INSERT` via SQL.
- **Change the route penalty:** it's index-derived; change an index number or the
  mapping in `IndexParameters.routePenaltyFactor()`.
- **Show FK enforcement:** try inserting a road with a non-existent location id —
  the loader/validation rejects it.

---

## 7. Checklist rows you satisfy

- ✅ Local dataset with data dictionary
- ✅ Database schema and seed data
- ✅ Database integration (persistent storage, import/export, clean separation)
- ✅ Index-number-derived parameters (once real indices are in)
