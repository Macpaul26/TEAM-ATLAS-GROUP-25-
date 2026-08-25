# Evidence Squad — What You Own

**2 members · Weeks 1–4 (runs throughout, intensifies Week 4) · Roles: Testing & Correctness Lead, Performance Analysis & Report Lead**

You turn working code into **graded evidence**: tests, trace tables, proof
sketches, counterexamples, performance graphs, the report and the defense pack.
The other three squads produce artifacts; you collect, verify and present them —
continuously, not the night before submission.

---

## 1. Correctness evidence (Testing & Correctness Lead)

### Unit tests — 154 passing (minimum required: 40)

Run: `java -cp bin campushub.RunTests`

| File | Covers |
|---|---|
| `test/DataStructureTests.java` | array, linked list, queue, heap, hash map, graph |
| `test/NewStructureTests.java` | stack, circular queue, deque, set, disjoint set, BST, AVL, B-tree, matrix graph |
| `test/AlgorithmTests.java` | dispatch, Dijkstra, knapsack |
| `test/NewAlgorithmTests.java` | search, sort, BFS/DFS, MST, greedy vs DP, index params |
| `test/TestRunner.java` | the tiny dependency-free harness (no JUnit needed) |

Every structure has **normal / boundary / invalid-input** cases. Keep it that way
— if a squad adds a method without tests, bounce the PR.

### 6 trace tables (brief §10 requires 6)

Generated from real code: `java -cp bin campushub.trace.Traces` (or menu 11).
Binary search, insertion sort, merge sort, Dijkstra, Kruskal, 0/1 knapsack DP.
**Screenshot each** for the report's Correctness section.

### 3 proof sketches (write these — a Week-4 task)

1. **Loop invariant** for binary search *or* insertion sort (e.g. "at the start of
   each iteration, `a[0..i-1]` is sorted").
2. **Induction / recurrence** for merge sort (T(n)=2T(n/2)+O(n) ⇒ O(n log n)).
3. **Greedy-vs-optimal** correctness idea for the knapsack: why the DP is optimal
   (optimal substructure) and why the greedy is not.

### 2 counterexamples (both already coded — just write them up)

1. **Greedy failure:** menu option 8 / `GreedyAssigner.greedyFailureExample()` —
   greedy 160 vs optimal 220 at budget 50.
2. **Invalid precondition:** menu option 9 — binary search on an unsorted array
   returns the wrong index while linear search is correct.

### Edge cases to explicitly mention

empty structure, single element, duplicate keys, disconnected graph, unreachable
path, queue full/empty, hash collision — all are exercised in the tests; point to
the specific assertions.

---

## 2. Performance evidence (Performance Analysis & Report Lead)

### Run the benchmarks

`java -cp bin campushub.RunBenchmarks` — writes CSVs to `results/`:

| Experiment | CSV | The graph to plot |
|---|---|---|
| 1. Search | `search_results.csv` | linear vs binary runtime |
| 2. Sort | `sort_results.csv` | selection/insertion/merge/quick |
| 3. Hash load factor | `hash_results.csv` | load factor vs collisions/time (3 table sizes) |
| 4. BST vs AVL | `tree_results.csv` | height & search time, BST degenerating |
| 5. Heap dispatch | `heap_results.csv` | insert/extract time |
| 6. Graph | `graph_results.csv` | BFS/DFS/Dijkstra/MST runtime |

Every experiment is **averaged over 3 runs** (brief requirement) and every
individual run is kept in `results/benchmark_raw.csv` ("keep raw timings, not only
screenshots"). The random workload is seeded from the team's index numbers, so
it's reproducible and team-specific.

### Your Week-4 tasks

1. **Plot one line graph per experiment** (Excel: open the CSV, insert line chart;
   or Python + matplotlib). Six graphs total.
2. **State the machine spec** — the benchmark prints OS/Java/cores/heap; replace
   with the actual demo machine's real spec (CPU model, RAM) in the report.
3. **Interpret each graph** against the theory the Algorithms Squad gave you, and
   **explain mismatches** (JIT warm-up on the first size, cache effects, small-n
   noise). The console output already flags warm-up noise — quote it.

The story the data tells (sanity-check your graphs against this):
- binary search flattens out while linear search climbs with n;
- selection/insertion curve upward (O(n²)) while merge/quick stay low;
- collisions explode as load factor rises on the small fixed table;
- BST height grows linearly with n while AVL height stays ~log n;
- heap and Dijkstra grow gently (log-linear).

---

## 3. The report (brief §11 structure)

Use `TECHNICAL_REPORT_SCAFFOLD.md` — it already has every required section as a
heading with a note on what to put there. Fill it with your own words, the six
graphs, and screenshots (menu runs, test PASS summary, DB counts, trace output).
Export to **PDF and DOCX** for submission.

---

## 4. Demo video + oral defense (Week 4)

- **Video (5–8 min):** show the DB loading (menu first run), a dispatch, a route,
  the tests passing, and a couple of graphs. Screen-record menu options 1→13.
- **Oral defense:** confirm every member's structure+algorithm assignment (see the
  table in `PROJECT_OVERVIEW.md`) and make sure each person has run their own menu
  option at least once and can hand-trace their part.
- **AI acknowledgment:** the brief requires declaring AI assistance and keeping
  the prompts used. Prepare that acknowledgment note and make sure every member
  can explain and modify their own code.

---

## 5. Development log (brief §15 requires it)

Keep a short weekly log: what each person did, what was blocked, decisions made.
The workflow plan's weekly stand-up feeds this. It's a graded academic-integrity
artifact — start it Week 1, don't reconstruct it at the end.

---

## 6. Checklist rows you satisfy

- ✅ Correctness tests and trace tables
- ✅ Performance CSV and graphs
- ✅ Technical report
- ✅ Demo video / oral defense prepared
- Plus: proof sketches, counterexamples, edge cases, development log
