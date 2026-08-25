# Technical Report — Ghana Smart Service Operations Optimizer

> **How to use this file.** Every section below is a heading required by brief §11.
> Replace each _italic prompt_ with your own words, graphs and screenshots, then
> export to PDF **and** DOCX. Don't submit this file as-is — it's a skeleton.

**Team:** SEG26-41 SYNERGY · **Course:** DCIT 204/308 · **Context:** University of
Ghana, Legon — campus service hub.

---

## Cover page
_Title, all 14 team members with index numbers, the local Ghana context, and the
organisation/problem modelled (campus maintenance & service dispatch)._

## 1. Problem statement, assumptions, input–output definitions, system boundaries
_What operational problem the system solves. Assumptions (e.g. non-negative travel
times, urgency 1–5). For at least five major operations (dispatch, route, reach,
budget, search) give: inputs, outputs, preconditions, edge cases. This is Module M1._

## 2. Dataset description, data dictionary and database schema
_Summarise the six tables and record counts (locations 56, roads 140, requests 320,
resources 32, runs 48). Pull the field-by-field detail from `Data_Dictionary.docx`.
Include the schema diagram / `schema.sql` highlights. Explain how the data was built
from public campus knowledge with no personal data (AI-resistance note)._

## 3. System architecture and module design
_The layer diagram from `PROJECT_OVERVIEW.md` §2. Map the ten brief modules (M1–M10)
to packages: model, ds, algo, db, config, bench, trace, test, Main. State the
"no java.util collections in core logic" constraint and how it's honoured._

## 4. Data-structure implementation (with diagrams)
_For each of the 15 structures: a short description, a diagram (draw the array/heap/
tree/linked list/graph), the operations and their Big-O. Reference `SQUAD_STRUCTURES.md`.
Include the resize trace for MyArrayList and an AVL rotation before/after diagram._

## 5. Algorithm implementation (pseudocode + selected Java snippets)
_For each algorithm: pseudocode, a short Java snippet, preconditions, complexity.
Reference `SQUAD_ALGORITHMS.md`. Cover search, sort, BFS/DFS, Dijkstra, Prim,
Kruskal, greedy, and the knapsack DP._

## 6. Correctness evidence
_Paste the 6 trace tables (from `campushub.trace.Traces`). Write the 3 proof
sketches (loop invariant, merge-sort recurrence, knapsack optimality). Present the
2 counterexamples (greedy failure; unsorted binary search). List the edge-case
tests with the assertions that cover them. Screenshot the 154-test PASS summary._

## 7. Performance analysis
_Method (System.nanoTime, 3-run average, seeded workload). Machine spec (real CPU,
RAM, OS, Java version). Raw-results tables (from `results/*.csv`). The six line
graphs. Interpretation of each, and an explanation of any theory-vs-practice
mismatch (JIT warm-up, cache, small-n noise). This is Modules M4/M10._

## 8. Database integration evidence
_Schema, sample rows, screenshots of the DB loading (menu first run), and a run log
showing algorithm_runs / audit_events being written back. Explain the derived
cost/benefit note for service_requests. Show FK enforcement rejecting a bad row._

## 9. Responsible algorithm selection
_When each chosen algorithm is appropriate and when it is NOT. e.g. Dijkstra needs
non-negative weights; binary search needs sorted input; greedy is fast but can be
suboptimal (your counterexample); DP is optimal but O(n·budget) in space; adjacency
list beats matrix for sparse graphs but not dense ones._

## 10. Individual contribution statement
_Who did what, per member. Be honest and specific. Tie each member to the structure
+ algorithm they will defend (table in `PROJECT_OVERVIEW.md` §8)._

## 11. Oral-defense preparation notes
_Each member's structure + algorithm, and a one-line summary of how they'd hand-trace
it. Note that the examiner may ask for a live change (new location, resized hash
table, changed priority rule)._

## 12. AI-assistance acknowledgment (brief §15)
_Declare where AI assistance was used, include the prompts, and confirm every member
can explain and modify their own part. This is required — do not omit it._

## 13. References and appendices
_Harvard-style references (CLRS; Sedgewick & Wayne; Goodrich, Tamassia & Goldwasser;
Lewis, DePasquale & Chase; MIT OCW; OpenDSA; Stanford CS166; Princeton Algorithms).
Appendices: full data dictionary, extra trace output, raw timing CSVs._
