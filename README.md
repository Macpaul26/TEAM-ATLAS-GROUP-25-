# Ghana Smart Service Operations Optimizer

**TEAM ATLAS · GROUP 25 · DCIT 204/308 Joint DSA Semester Project · University of Ghana, Legon**

## What we're doing

The University of Ghana's Legon campus has a constant stream of maintenance
and service requests — broken ACs in the halls, Wi-Fi outages in the labs,
faulty locks, water leaks, shuttle breakdowns — and no automated way to
decide what gets handled first, how help gets there fastest, or what the
maintenance budget can actually cover on a given day.

**We built that system.** It's a console-based Java application, backed by a
real SQLite database, that:

- **Stores** real campus data — 56 locations, 140 roads, 320 service
  requests, 32 maintenance resources — in a permanent database, not just in
  memory;
- **Organizes** that data using **15 data structures we built entirely from
  scratch** (no `java.util.ArrayList`, `HashMap`, `PriorityQueue`, etc. — the
  brief requires our own implementations for every core structure);
- **Decides**, using **8 algorithms we implemented ourselves**, who gets
  served next (priority scheduling), the fastest route across campus
  (Dijkstra), what's reachable (BFS/DFS), the cheapest way to connect every
  location (Prim & Kruskal), and which tickets fit today's budget for the
  most benefit (greedy vs. dynamic programming, deliberately including a
  case where greedy gets it wrong);
- **Proves** all of the above actually works — 154 automated tests, 6
  required trace tables, and 6 empirical performance experiments comparing
  theory against real timed measurements.

Every part of this maps directly to the assignment brief: custom data
structures and algorithms, a real database integration, correctness
evidence, and performance analysis, all wrapped in a menu an examiner can
run without touching source code.

> **New to the project? Read `docs/PROJECT_OVERVIEW.md` first** — it explains
> how everything fits together and who owns what. If you want it explained
> in plain, everyday language with no assumed technical background, read
> `docs/PLAIN_LANGUAGE_GUIDE.md` instead.

---

## Quick start (easiest — just run the scripts)

Requires a JDK (tested on OpenJDK 21) — [get one here](https://adoptium.net) if
`java -version` in a terminal doesn't work. The SQLite driver is bundled in
`lib/`, no separate install needed.

**Windows:** double-click `compile.bat` once, then double-click `run.bat`.
(Or `test.bat`, `trace.bat`, `benchmarks.bat` for the other entry points.)

**Mac / Linux:** in a terminal, inside this folder:
```bash
./compile.sh
./run.sh
```
(Or `./test.sh`, `./trace.sh`, `./benchmarks.sh`.)

If double-clicking a `.bat` file does nothing on Windows, open Command Prompt
or PowerShell, `cd` into this exact folder, and type `compile.bat` then `run.bat`.

### Getting the "Could not open SQLite database... is lib/sqlite-jdbc.jar on the classpath?" error?

This happens if the program is run **manually** with the wrong classpath —
Windows needs `;` between `bin` and the jar path, not `:` (that's a Mac/Linux
thing). **Use `run.bat` / `run.sh` above and this can't happen** — they set the
classpath correctly for you. If you must run it manually, the exact commands are
in the "Manual commands" section below.

Two other common causes of this same error:
1. You're not standing **inside** this project folder when you run the
   command (check with `dir` on Windows or `ls` on Mac/Linux — you should see
   `bin`, `lib`, `src`, `data` right there).
2. The zip only partly extracted and `lib/sqlite-jdbc.jar` is missing or 0 KB —
   re-extract the zip and confirm the jar is about 9 MB.

---

## Manual commands (if you're not using the scripts)

```bash
find src -name "*.java" > sources.txt
javac -cp lib/sqlite-jdbc.jar -d bin @sources.txt

java -cp bin:lib/sqlite-jdbc.jar campushub.Main          # interactive menu (examiner entry point)
java -cp bin campushub.RunTests                          # 154 unit tests
java -cp bin campushub.trace.Traces                      # 6 trace tables
java -cp bin campushub.RunBenchmarks                     # perf benchmarks -> results/*.csv
java -cp bin:lib/sqlite-jdbc.jar campushub.db.DbCheck    # DB round-trip proof
```

Windows: use `;` instead of `:` in the classpath.

The first menu launch creates `data/campushub.db`, applies `data/schema.sql`, and
loads the seed CSVs automatically.

---

## Repository layout

```
.
├── compile.bat / compile.sh   one-click build (Windows / Mac-Linux)
├── run.bat / run.sh           one-click launch of the console menu
├── test.bat / test.sh         one-click run of all 154 tests
├── trace.bat / trace.sh       one-click print of the 6 trace tables
├── benchmarks.bat / .sh       one-click performance benchmark run
├── src/campushub/
│   ├── model/     Location, Resource, ServiceRequest
│   ├── ds/        15 custom data structures
│   ├── algo/      8 algorithm classes
│   ├── db/        schema.sql loader, JDBC Database, CsvLoader, DbCheck
│   ├── config/    IndexParameters (index-number-derived)
│   ├── bench/     BenchmarkRunner
│   ├── trace/     Traces (6 required trace tables)
│   ├── test/      154 unit tests + dependency-free harness
│   └── Main.java  interactive console menu
├── data/          schema.sql + 5 seed CSVs (~596 records)
├── results/       benchmark CSV exports (generated)
├── lib/           sqlite-jdbc.jar
├── reference/     original lecturer-issued brief and dictionary documents
└── docs/          plain-language guide, project overview, 4 squad docs,
                    GitHub guides, report scaffold
```

---

## Status

- ✅ 15 custom data structures, 8 algorithm classes — **154 tests passing**
- ✅ Real SQLite database: schema, JDBC layer, CSV loader, round-trip verified
- ✅ ~596 seed records (all above brief minimums)
- ✅ Console menu covering every operation (examiner-runnable, no source edits)
- ✅ 6 trace tables, 6 benchmark experiments (3-run averages + raw timings)
- ✅ Squad documentation + technical-report scaffold


---

## Constraint compliance

No `java.util.ArrayList/LinkedList/HashMap/PriorityQueue/Stack/ArrayDeque/TreeMap`
in assessed logic — only `campushub.ds.*`. Built-in utilities are used **only**
for file reading, JDBC, `Scanner` input, `System.nanoTime()` timing, and the
`NoSuchElementException` standard exception type.
