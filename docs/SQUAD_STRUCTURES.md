# Structures Squad — What You Own

**5 members · Weeks 1–2 focus · Roles: Linear Structures, Queue Structures, Tree Structures, Hash & Set/Map, Priority Queue/Heap & Scheduling Engineers**

You build the **custom data-structure library** the entire system stands on. Every
algorithm the next squad writes calls your structures. The hard rule: **implement
these yourself — no `java.util` collections allowed** in assessed logic.

---

## 1. The 15 structures (all built, all tested)

| # | Structure | File | Key operations | Backs / used by |
|---|---|---|---|---|
| 1 | Dynamic array list | `ds/MyArrayList.java` | add, get, set, addAt, remove, resize (doubling) | everything |
| 2 | Singly linked list | `ds/MyLinkedList.java` | addFirst/Last, insertAfter, remove, **iterator** | queue, hash buckets |
| 3 | Stack (LIFO) | `ds/MyStack.java` | push, pop, peek, isEmpty | DFS, audit/undo log |
| 4 | Queue (FIFO) | `ds/MyQueue.java` | enqueue, dequeue, peek | BFS, walk-in requests |
| 5 | Circular queue | `ds/MyCircularQueue.java` | enqueue/dequeue with wrap-around | fixed-capacity buffering |
| 6 | Deque | `ds/MyDeque.java` | addFront/Rear, removeFront/Rear | urgent-insertion scheduling |
| 7 | Min-heap / priority queue | `ds/MyMinHeap.java` | insert, extractMin, peek, siftUp/Down | dispatch, Dijkstra, Prim |
| 8 | Hash map | `ds/MyHashMap.java` | put, get, remove, resize @0.75, collision stats | DB tables, graph adjacency |
| 9 | Set | `ds/MySet.java` | add, contains, size (on hash map) | reachability, membership |
| 10 | BST | `ds/MyBST.java` | insert, search, searchPath, inorder, height | indexing, tree comparison |
| 11 | AVL (balanced tree) | `ds/MyAVLTree.java` | insert with rotations, isBalanced, height | balanced-tree deliverable |
| 12 | B-tree | `ds/MyBTree.java` | insert with node split, search (min degree t) | DB index simulation |
| 13 | Disjoint set (union-find) | `ds/DisjointSet.java` | makeSet, find (path compression), union by rank | Kruskal MST |
| 14 | Graph (adjacency list) | `ds/Graph.java` | addLocation, addRoute, neighboursOf | all graph algorithms |
| 15 | Graph (adjacency matrix) | `ds/MatrixGraph.java` | addEdge, hasEdge, weight | matrix representation deliverable |

---

## 2. Design decisions to be able to defend

- **MyArrayList doubles capacity** on overflow → amortised O(1) `add`
  (Goodrich, Tamassia & Goldwasser, ch. 6). Know why amortised ≠ worst case.
- **MyLinkedList keeps a tail pointer** so `addLast` is O(1), not O(n). It also
  implements `Iterable` so `for (x : list)` works — used across the algorithms.
- **MyMinHeap is a flat array**; parent of `i` is `(i-1)/2`, children `2i+1`,
  `2i+2`. insert/extractMin are O(log n) (CLRS ch. 6).
- **MyHashMap uses separate chaining** (each bucket a linked list) and resizes
  past load factor 0.75. It exposes `collisionCount()`, `longestChain()`,
  `currentLoadFactor()`, and a **fixed-capacity mode** — the Evidence Squad uses
  these for the load-factor experiment. Average O(1), worst case O(n).
- **MyBST vs MyAVLTree** is the whole point of the tree comparison: insert sorted
  keys and the BST degenerates to height n (a linked list) while the AVL tree
  stays ~log n via rotations. `MyAVLTree.rotationsInLastInsert()` lets you show
  the rebalancing count.
- **DisjointSet** uses union by rank + path compression → near-O(1) `find`,
  which is what makes Kruskal efficient.
- **Graph is an adjacency list** (not a matrix) because the campus network is
  sparse → O(V+E) space instead of O(V²). `MatrixGraph` exists so you can show
  and contrast the matrix representation too.

---

## 3. Testing conventions (keep these when you extend anything)

Tests live in `test/DataStructureTests.java` and `test/NewStructureTests.java`,
run via `java -cp bin campushub.RunTests`. Every structure has **normal,
boundary, and invalid-input** cases (brief §8). Examples already covered:

- empty structure (pop/dequeue/removeFirst on empty → correct exception)
- single element
- resize under load (add 20 into capacity-2 list; 50 keys into hash map)
- duplicate keys (hash map overwrite must not grow size)
- heap always extracts in non-decreasing order

**If you add a method, add its three test cases in the same PR.** Don't let the
test count drop below the current 154.

---

## 4. What each Structures member should defend orally

Split the structures so each of the 5 owns a cluster:

- **Linear Structures Engineer:** MyArrayList, MyLinkedList (+ iterator).
- **Queue Structures Engineer:** MyQueue, MyCircularQueue (wrap-around), MyDeque.
- **Tree Structures Engineer:** MyBST, MyAVLTree (rotations!), MyBTree (node split).
- **Hash & Set/Map Engineer:** MyHashMap (chaining, resize, collisions), MySet.
- **Heap/Scheduling Engineer:** MyMinHeap (siftUp/Down), and how PriorityDispatcher uses it.

Each member should be able to **draw the structure**, state the Big-O of each
operation, and hand-trace one operation (e.g. a heap insert bubbling up, an AVL
rotation, a hash collision landing in a chain).

---

## 5. Shared-repo discipline (from the workflow plan)

- One branch per structure/module; never commit to `main` directly.
- Every merge is a PR reviewed by at least one other Structures member.
- Commit messages reference the module, e.g. `M3: add AVL insert + rotations + tests`.
- Keep interfaces consistent (all structures use the same naming style) so the
  Algorithms Squad can rely on them without surprises.

---

## 6. Checklist rows you satisfy

- ✅ Custom data structures implemented (all 13 required kinds + matrix graph)
- ✅ Correctness tests (normal/boundary/invalid) — contributes to the 154 total
- Supports: graph algorithms, indexing, scheduling, MST (via disjoint set)
