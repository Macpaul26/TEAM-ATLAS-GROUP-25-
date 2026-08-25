# Ghana Smart Service Operations Optimizer — Project Overview

**Team SEG26-41 SYNERGY · University of Ghana, Legon · DCIT 204/308 Joint DSA Semester Project**

Local context: **Legon campus service hub** — a maintenance/service-request
dispatch platform. Halls, hostels, labs, the library, admin blocks, security
posts and shuttle stops are locations on a road/footpath network. Broken ACs,
Wi‑Fi outages, faulty locks, water leaks, shuttle breakdowns etc. are logged as
service requests that must be prioritised, routed to, and funded within a budget.

> **Read this first.** It explains what the system does, how the pieces fit
> together, and who owns what. Then read your squad's doc
> (`SQUAD_*.md`) for the detail on your part.

---

## 1. What the system actually does

The flow is: **CSV seed data → SQLite database → custom data structures →
custom algorithms → console menu / reports → performance evidence.**

Concretely, the running program can answer:

| Operational question | Algorithm | Data structure behind it |
|---|---|---|
| Which ticket do we handle next? | priority dispatch | min-heap (priority queue) |
| Fastest route from A to B? | Dijkstra | graph + min-heap + hash map |
| Which locations are reachable? | BFS / DFS | graph + queue / stack |
| Cheapest way to connect everywhere? | Prim + Kruskal (MST) | graph + heap + disjoint set |
| Which tickets fit today's budget? | 0/1 knapsack (DP) | dynamic-array table |
| Is greedy good enough here? | greedy + counterexample | array-backed list |
| How fast is each algorithm as data grows? | empirical benchmarks | all of the above |

Everything is backed by a **real SQLite database** (`data/campushub.db`) that the
program reads from and writes back to (algorithm runs + audit log).

---

## 2. Architecture at a glance

```
campushub
├── model/        Location, Resource, ServiceRequest         (plain data)
├── ds/           15 custom data structures                  (Structures Squad)
├── algo/         8 algorithm classes                        (Algorithms Squad)
├── db/           schema.sql, Database (JDBC), CsvLoader      (Foundation Squad)
├── config/       IndexParameters (index-number-derived)     (Foundation Squad)
├── bench/        BenchmarkRunner (CSV exports)              (Evidence Squad)
├── trace/        Traces (6 required trace tables)           (Evidence Squad)
├── test/         154 unit tests, dependency-free harness    (Evidence Squad)
└── Main.java     interactive console menu (examiner entry)  (everyone)
```

**Hard rule (brief §8):** no `java.util` collections in assessed core logic.
No `ArrayList`, `HashMap`, `PriorityQueue`, `Stack`, `ArrayDeque`, `TreeMap`.
Only our own `campushub.ds.*`. Built-in utilities are used **only** for file
reading, JDBC, `Scanner` input, and `System.nanoTime()` timing.

---

## 3. Project inventory (current state)

| Thing | Count |
|---|---|
| Java source files | 41 |
| Custom data structures | 15 |
| Algorithm classes | 8 |
| Unit tests (passing) | **154** (brief minimum: 40) |
| Trace tables | 6 (binary search, insertion sort, merge sort, Dijkstra, Kruskal, knapsack DP) |
| Database tables | 6 |
| Seed records | ~596 (locations 56, roads 140, requests 320, resources 32, runs 48) |
| Benchmark experiments | 6, each averaged over 3 runs, exported to CSV |

Minimum record counts required by the brief (50/100/300/30/30) are all exceeded.

---

## 4. How to build and run

Requires a JDK (tested on OpenJDK 21) and the bundled `lib/sqlite-jdbc.jar`.
From the project root:

```bash
# compile everything
find src -name "*.java" > sources.txt
javac -cp lib/sqlite-jdbc.jar -d bin @sources.txt

# 1) interactive menu — this is the examiner entry point
java -cp bin:lib/sqlite-jdbc.jar campushub.Main

# 2) run all 154 unit tests
java -cp bin campushub.RunTests

# 3) print the 6 trace tables
java -cp bin campushub.trace.Traces

# 4) run performance benchmarks (writes results/*.csv)
java -cp bin campushub.RunBenchmarks

# 5) end-to-end database round-trip check
java -cp bin:lib/sqlite-jdbc.jar campushub.db.DbCheck
```

On Windows use `;` instead of `:` in the classpath
(`-cp bin;lib/sqlite-jdbc.jar`).

First launch of the menu creates `data/campushub.db`, applies `data/schema.sql`,
and loads the seed CSVs automatically.

---

## 5. The four squads (who does what)

| Squad | Members | Owns | Detail doc |
|---|---|---|---|
| **Foundation** | 4 | context, dataset, schema, DB loader, index params | `SQUAD_FOUNDATION.md` |
| **Structures** | 5 | the 15 custom data structures + tests | `SQUAD_STRUCTURES.md` |
| **Algorithms** | 3 | search/sort/graph/greedy/DP | `SQUAD_ALGORITHMS.md` |
| **Evidence** | 2 | tests, traces, benchmarks, report, defense | `SQUAD_EVIDENCE.md` |

Timeline (brief §13 / workflow plan): Foundation finishes Week 1 to unblock
everyone; Structures runs Weeks 1–2; Algorithms Weeks 2–3 on top of Structures;
Evidence runs throughout and intensifies Week 4.

---

## 6. What is DONE vs what each member must still personalise

**Done (working, tested, committed):** all 15 data structures, all 8 algorithm
classes, the SQLite schema + JDBC layer + CSV loader, ~596 seed records, the
console menu, 154 unit tests, 6 trace tables, the 6-experiment benchmark suite
with CSV export, and this documentation set.

**Still needs the team (cannot be faked, and the brief checks for it):**

1. **Real index numbers.** `config/IndexParameters.java` currently holds
   placeholder indices (`10000001…`). Replace them with the 14 members' real
   8-digit index numbers. Every derived parameter (hash capacity, seed, route
   penalty, budget, priority weight) then changes to values unique to this team.
2. **Performance graphs.** Plot the CSVs in `results/` (Excel or Python) — one
   line graph per experiment — and drop them into the report.
3. **Screenshots + run logs** of the menu, tests and DB for the report.
4. **The written report** — fill in `TECHNICAL_REPORT_SCAFFOLD.md` with your own
   words, graphs and screenshots.
5. **Demo video (5–8 min)** and **oral defense**: every member must be able to
   explain **one data structure and one algorithm** live (brief §2, §15). Pick
   yours from your squad doc and rehearse the trace by hand.

---

## 7. Academic integrity note (please read)

The brief (§15) allows AI assistance **if it is acknowledged and every member can
explain and modify their own part**. This scaffold was built with AI help and
that must be declared. More importantly: do **not** treat this as a black box.
Before the defense, each member should read their assigned files, run the
relevant menu option, and be able to re-derive the trace on paper. The examiner
can ask you to change a priority rule, add a location, or resize the hash table
live — so understanding beats memorising.

---

## 8. Suggested division for the oral defense

Fourteen members, each defends 1 structure + 1 algorithm. A clean split:

| Member | Data structure to defend | Algorithm to defend |
|---|---|---|
| 1 | MyArrayList | Linear search |
| 2 | MyLinkedList | Binary search |
| 3 | MyStack | Selection sort |
| 4 | MyQueue | Insertion sort |
| 5 | MyCircularQueue | Merge sort |
| 6 | MyDeque | Quicksort |
| 7 | MyMinHeap | Priority dispatch |
| 8 | MyHashMap | Dijkstra |
| 9 | MyBST | BFS |
| 10 | MyAVLTree | DFS |
| 11 | MyBTree | Prim's MST |
| 12 | MySet | Kruskal's MST |
| 13 | DisjointSet | Greedy assignment (+ its failure case) |
| 14 | Graph / MatrixGraph | 0/1 knapsack DP |

Adjust to taste, but make sure every structure and every algorithm has an owner.
