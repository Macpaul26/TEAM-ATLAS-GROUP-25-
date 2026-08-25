# Algorithms Squad — What You Own

**3 members · Weeks 2–3 focus · Roles: Search & Sort Engineer, Graph & Routing Engineer, Optimisation Engineer**

You implement the **algorithms** on top of the Structures Squad's library. You
consume their structures; if something's missing, flag it back early. Everything
here is built from scratch — no `java.util` sorting, no library shortest-path.

---

## 1. The 8 algorithm classes (all built, all tested)

| Area | File | Contains |
|---|---|---|
| Search | `algo/Searching.java` | linearSearch, binarySearch (+ `binarySearchTrace`) |
| Sort | `algo/Sorting.java` | selectionSort, insertionSort, mergeSort (stable), quickSort (median-of-three) |
| Graph traversal | `algo/GraphTraversal.java` | BFS (queue), DFS (stack), reachableFrom (set) |
| Shortest path | `algo/ShortestRoute.java` | Dijkstra with min-heap frontier + hash-map distance/predecessor tables |
| MST | `algo/MinimumSpanningTree.java` | Prim (heap) and Kruskal (disjoint set) |
| Scheduling | `algo/PriorityDispatcher.java` | urgency-ordered dispatch on the min-heap |
| Greedy | `algo/GreedyAssigner.java` | benefit/cost-ratio greedy **+ a failure counterexample** |
| DP | `algo/BudgetSelector.java` | 0/1 knapsack, bottom-up table, subset reconstruction |

---

## 2. What to be able to defend (per algorithm)

**Search**
- `linearSearch` O(n); `binarySearch` O(log n) **but requires sorted input** —
  that precondition is the whole point. The menu (option 9) demonstrates binary
  search returning the wrong answer on an unsorted array, next to linear search
  getting it right. That's one of your two required counterexamples.

**Sort**
- Selection & insertion: O(n²), in place. Insertion is stable and adaptive
  (fast on nearly-sorted data). Selection always does the same work.
- Merge sort: O(n log n), **stable**, uses an auxiliary array (not in place).
- Quicksort: O(n log n) average, O(n²) worst; median-of-three pivot + tail-call
  elimination reduce the bad cases. In place.
- Be ready to discuss **stability** and **in-place vs extra space**.

**Graph**
- BFS uses your queue and visits in layers (shortest hop count); DFS uses your
  stack and goes deep first. `reachableFrom` returns a set — used in the menu to
  show all 56 campus locations are connected.
- **Dijkstra** (`ShortestRoute`): non-negative weights only (state this
  precondition), min-heap frontier, distance + predecessor tables in hash maps,
  O((V+E) log V). Path is reconstructed by walking predecessors backwards.

**MST**
- Prim grows one tree from a start node using the heap; Kruskal sorts edges and
  unions with the disjoint set, rejecting any edge that would form a cycle.
- **They must produce the same total weight** — the code asserts this and the
  menu (option 6) shows both. Good defense point: two different strategies, same
  optimal cost.

**Greedy + its failure (required)**
- `GreedyAssigner.selectByRatio` takes highest benefit/cost ratio first.
- `greedyFailureExample()` is a 3-ticket instance where, at budget 50, greedy
  picks A+B (benefit 160) but the DP optimum is B+C (benefit 220). Menu option 8
  runs both side by side. **This is a required counterexample — know why greedy
  fails here** (taking the best ratio first consumes budget that blocks a better
  combination).

**Dynamic programming**
- `BudgetSelector` is 0/1 knapsack: `dp[i][b]` = best benefit using first `i`
  tickets within budget `b`. O(n·budget) time and space; the full table is kept
  so the funded subset can be reconstructed by backtracking. Trace table is in
  `trace/Traces.java` (knapsack, budget 5, optimum 7).

---

## 3. Where your traces come from

The Evidence Squad needs 6 trace tables; **4 of them are your algorithms**:
binary search, insertion sort, merge sort, Dijkstra, Kruskal, knapsack DP. They
are generated from real code in `trace/Traces.java` (run `java -cp bin
campushub.trace.Traces` or menu option 11), so the traces are guaranteed to match
what the system actually does. If you change an algorithm, regenerate the trace.

---

## 4. Efficiency analysis you must relate to the graphs

The Evidence Squad benchmarks these; you provide the **theory** to compare against:

| Algorithm | Best | Average | Worst |
|---|---|---|---|
| Linear search | O(1) | O(n) | O(n) |
| Binary search | O(1) | O(log n) | O(log n) |
| Selection sort | O(n²) | O(n²) | O(n²) |
| Insertion sort | O(n) | O(n²) | O(n²) |
| Merge sort | O(n log n) | O(n log n) | O(n log n) |
| Quicksort | O(n log n) | O(n log n) | O(n²) |
| BFS / DFS | O(V+E) | O(V+E) | O(V+E) |
| Dijkstra (heap) | O((V+E) log V) | — | — |
| Prim / Kruskal | O(E log V) | — | — |
| 0/1 knapsack DP | O(n·W) | O(n·W) | O(n·W) |

You must **explain any mismatch** between these and the measured runtimes (JIT
warm-up, cache effects, small-n noise). The benchmark output already flags
warm-up noise on the first size — cite that.

---

## 5. What each Algorithms member should defend orally

- **Search & Sort Engineer:** all four sorts + both searches; stability/in-place;
  the unsorted-binary-search counterexample.
- **Graph & Routing Engineer:** BFS/DFS, Dijkstra (precondition + heap frontier),
  Prim vs Kruskal producing equal cost.
- **Optimisation Engineer:** greedy vs DP; why greedy fails on the counterexample;
  the knapsack DP table + reconstruction.

---

## 6. Checklist rows you satisfy

- ✅ Searching and sorting algorithms
- ✅ Graph algorithms (BFS, DFS, Dijkstra, Prim, Kruskal)
- ✅ Greedy and DP algorithms (+ the required greedy counterexample)
- Feeds: 4 of the 6 trace tables, and the efficiency analysis
