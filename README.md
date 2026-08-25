# Ghana Smart Service Operations Optimizer

**Team SEG26-41 SYNERGY · University of Ghana, Legon · DCIT 204/308 Joint DSA Semester Project**

A console-based Java service-operations platform for a **Legon campus service hub**:
it stores campus locations, roads, service requests and resources in a SQLite
database, loads them into **custom-built data structures** (no `java.util`
collections in core logic), and runs **custom algorithms** to prioritise tickets,
find routes, check reachability, build minimum-cost networks, and select work
within a budget — then measures its own performance empirically.

> **New to the project? Read `docs/PROJECT_OVERVIEW.md` first.**

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
1. You're not standing **inside** the `SEG26-41-SYNERGY` folder when you run the
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
└── docs/          PROJECT_OVERVIEW + 4 squad docs + report scaffold
```

---

## Status

- ✅ 15 custom data structures, 8 algorithm classes — **154 tests passing**
- ✅ Real SQLite database: schema, JDBC layer, CSV loader, round-trip verified
- ✅ ~596 seed records (all above brief minimums)
- ✅ Console menu covering every operation (examiner-runnable, no source edits)
- ✅ 6 trace tables, 6 benchmark experiments (3-run averages + raw timings)
- ✅ Squad documentation + technical-report scaffold

### Before submission (team action)

1. Put the **real 14 index numbers** into `src/campushub/config/IndexParameters.java`.
2. Plot the six `results/*.csv` files into graphs for the report.
3. Fill in `docs/TECHNICAL_REPORT_SCAFFOLD.md`, add screenshots, export PDF+DOCX.
4. Record the 5–8 min demo video; rehearse the oral defense (1 structure + 1
   algorithm per member — table in `docs/PROJECT_OVERVIEW.md`).
5. Add the AI-assistance acknowledgment (brief §15).

---

## Constraint compliance

No `java.util.ArrayList/LinkedList/HashMap/PriorityQueue/Stack/ArrayDeque/TreeMap`
in assessed logic — only `campushub.ds.*`. Built-in utilities are used **only**
for file reading, JDBC, `Scanner` input, `System.nanoTime()` timing, and the
`NoSuchElementException` standard exception type.
