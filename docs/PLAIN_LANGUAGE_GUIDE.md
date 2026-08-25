# Understanding Our Project — A Plain-Language Guide for the Whole Team

**Ghana Smart Service Operations Optimizer · Team SEG26-41 SYNERGY**

> **Who this is for:** every one of the 14 of us, especially if you haven't
> touched the code yet and coding isn't your thing. There is **no assumed
> knowledge** in this document. If a word sounds technical, it gets explained
> the moment it shows up. By the end, you should be able to stand in front of
> the examiner and explain your part like you built it yourself — because
> once you understand it, you *can* defend it as your own work.
>
> Read this document fully once. Then jump to **your squad's section** and
> read it two or three times until you could explain it to a friend who
> knows nothing about computers.

---

## Part 1: What Are We Actually Building?

Forget computers for a second. Imagine the University of Ghana, Legon,
decided to open a **help desk for the whole campus**. Any student or staff
member can walk up and report a problem: *"the AC in Commonwealth Hall is
broken,"* *"there's a water leak in Akuafo Hall,"* *"the Wi-Fi is down in
the CS department."*

The help desk needs to answer four practical questions, every single day:

1. **Who do we help first?** Some problems are emergencies (a broken door
   lock is a security risk). Some can wait (a wobbly chair). We can't just
   help people in the order they walked in — urgent problems must jump the
   queue.
2. **How does help get there fastest?** If an electrician is at the
   Maintenance Office and needs to reach Volta Hall, what's the quickest
   path across campus, given that some roads are busier or worse than
   others?
3. **What can we actually connect or reach?** If we're laying a new water
   pipe network, or just asking "can a technician even get from here to
   there," we need to know what's connected to what.
4. **What can we afford to fix today?** We have a maintenance budget. Some
   fixes cost more than others and matter more than others. Given a fixed
   amount of money, which combination of tickets gives us the most value
   without overspending?

**Our whole project is a computer program that answers these four
questions automatically**, using real (realistic) data about Legon's
halls, hostels, labs, and shuttle stops.

That's it. Everything else in this document — the "data structures," the
"algorithms," the "database" — exists purely to make those four questions
answerable, fast, and provably correct. Nothing in this project is there
"because computer science requires it." Every single piece has a plain,
practical job.

---

## Part 2: The Three Big Ideas You Need Before Anything Else

If you understand these three ideas, you understand 80% of the project.
Everything else is just detail.

### Idea 1: A "Data Structure" Is Just a Way of Organizing Things

You already use data structures every day — you just don't call them
that.

- When you queue at the cafeteria, first person in line gets served
  first. That's a **queue**.
- When you stack plates on top of each other, the last plate you put on
  is the first one you take off. That's a **stack**.
- Your phone's contact list lets you jump straight to "Ama" without
  scrolling past everyone else. That's a **hash map**.
- A family tree, where each person branches into their children, is a
  **tree**.
- A map of Legon with roads connecting buildings is a **graph**.

A "data structure" is simply: **a specific way of arranging information so
that certain jobs (finding something, adding something, removing
something) are fast and organized**, instead of just being a random pile.

**Why we had to build our own instead of using Java's built-in versions:**
Java actually comes with ready-made versions of a queue, a stack, a
hash map, etc. — like buying a phone book instead of writing one from
scratch. But the whole point of this course is to prove we understand
*how those things work on the inside*. So the assignment specifically
says: **build them yourself, from scratch, using nothing but plain
variables and arrays.** That's why every data structure in our project has
"My" in front of its name — `MyArrayList`, `MyQueue`, `MyHashMap` — to make
clear these are hand-built, not borrowed from Java's toolbox.

### Idea 2: An "Algorithm" Is Just a Recipe — A Set of Steps to Follow

If a data structure is "how you organize the ingredients," an algorithm is
"the recipe for what to do with them."

- "Look through every drawer until you find your keys" is a search
  algorithm (a slow one).
- "Look in the last drawer you remember leaving them in first" is a
  smarter search algorithm.
- "Arrange your books by height, shortest to tallest" is a sorting
  algorithm.
- "Find the shortest walking route from your hostel to your first
  lecture" is a routing algorithm — this is exactly what Google Maps
  does, and it's exactly what our project does for campus service
  vehicles.

An algorithm is just: **a precise, step-by-step method for solving a
specific problem**, and different recipes can solve the same problem with
different amounts of effort. Part of this project is proving, with actual
timed evidence, which recipes are faster and why.

### Idea 3: A "Database" Is Just a Very Organized, Permanent Filing Cabinet

If you write information on a whiteboard, it disappears when someone wipes
it. A **database** is the opposite: it's a filing cabinet that:

- keeps information even after the program closes (it's **permanent**),
- is organized into labeled folders — we call these **tables** — like a
  "Locations" folder, a "Service Requests" folder, a "Resources" folder,
- and enforces rules automatically, e.g. "you can't file a service
  request against a location that doesn't exist in the Locations folder."

Our project uses a real database engine called **SQLite** — a small,
self-contained database that lives in a single file
(`data/campushub.db`) — because the assignment specifically requires that
data survive being saved, closed, and reopened, not just live in the
program's memory while it runs.

---

## Part 3: How the Whole System Fits Together (The Full Journey of One Ticket)

Let's follow a single, real example from start to finish, using plain
words at every step, so you can see how every piece we built connects to
the next.

> **Scenario:** A student reports that the Wi-Fi is down in the Department
> of Computer Science.

**Step 1 — It gets written down (the database).**
The report is saved as a row in the `service_requests` table: who
reported it, where, what kind of problem, how urgent it is, when it was
submitted, and when it needs to be fixed by. This is now permanent — even
if the program restarts, this ticket still exists.

**Step 2 — It gets loaded into memory in an organized way (data
structures).**
The program reads the ticket out of the database and places it into a
**priority queue** — a structure that always keeps the most urgent item
easiest to reach, the way a hospital triage nurse keeps the most critical
patient at the front of their attention, no matter what order patients
walked in.

**Step 3 — The system decides who gets served next (an algorithm).**
Using the priority queue, the system asks: "of everyone waiting, who is
most urgent?" If the Wi-Fi outage is urgency level 2 and someone reported
a broken chair at urgency level 5 (less urgent, lower priority), the Wi-Fi
ticket gets pulled out first — even if the chair report was filed
earlier.

**Step 4 — The system figures out how help gets there (another
algorithm).**
The nearest available IT technician needs the fastest route from wherever
they are to the CS Department. The system looks at the **campus road
network** (another data structure — a graph, like a map) and calculates
the shortest realistic path, taking into account that some roads are
slower than others (traffic, construction, poor road condition).

**Step 5 — The result gets written back to the database.**
How long the trip is expected to take, and a record that this action
happened, get saved back into the database — so there's a permanent,
searchable history (an **audit log**) of everything the system has done.

**Step 6 — We prove it actually works and is fast enough (testing +
performance evidence).**
Separately from all of this, we have to *prove*, with real timed
measurements and worked examples on paper, that every piece behaves
correctly and stays fast even as the number of tickets grows into the
thousands.

That six-step journey — **write it down → organize it → decide priority →
find the route → save the result → prove it works** — is the entire
project. Everything the four squads built is one of those six steps.

---

## Part 4: Foundation Squad — "We Built the Filing Cabinet and Gave It Real Campus Data"

**Members:** Project Lead & Systems Architect · Local Context & Dataset
Lead · Database Architect · Database Integration Engineer

### What this squad is, in one sentence

The team that decided **what our campus looks like on paper** (which
halls, which roads, which kinds of problems) and built the **permanent
filing cabinet** (the database) that everything else depends on.

### What they actually built

**1. The local context and the dataset — "what does our campus look like
in the system?"**

*What it is:* A believable, realistic version of the University of
Ghana, Legon, written out as data: 56 real-sounding locations
(Commonwealth Hall, Balme Library, the CS Department, shuttle stops...),
140 roads connecting them, 320 service tickets, 32 maintenance
resources (electricians, IT technicians, shuttles), and 48 past
performance measurements.

*What it does:* Gives every other squad something real to build against,
instead of vague, made-up placeholder data.

*Why it's here:* The lecturer's brief specifically forbids using a
generic, copy-pasted example — every team must localize their data to a
real Ghanaian context. This is also an "AI-resistance" measure: it's much
harder to fake understanding of a system that's tied to genuinely local,
specific data.

**2. The database schema — "the labeled folders in the filing cabinet"**

*What it is:* A blueprint file (`data/schema.sql`) that defines six
folders (tables): Locations, Roads, Resources, Service Requests, Algorithm
Runs, and Audit Events. For each folder, it says exactly what information
goes in it (e.g. every location has an ID, a name, an area, and a type)
and what rules must always hold (e.g. every road must connect two
locations that actually exist).

*What it does:* Keeps the data honest and consistent. If someone tries to
add a road between a real hall and a location ID that doesn't exist, the
database refuses.

*Why it's here:* Without a clear blueprint, the filing cabinet turns into
a junk drawer. This is what the marking rubric calls "database
integration" — 10 marks specifically for this.

**3. The database connection code (`Database.java`) and the CSV loader
(`CsvLoader.java`) — "the clerk who actually files the paperwork"**

*What it is:* Java code that opens the database file, reads the raw seed
data (which is stored as simple spreadsheet-style files called CSVs —
think of them as plain-text Excel sheets), checks every row for problems,
and files it into the correct folder. It also reads data back out and
hands it to the rest of the program in the shapes the other squads need
(for example, turning the Roads table into a usable campus map).

*What it does:* Bridges the gap between "data sitting in a file" and
"data the program can actually use to make decisions." It also writes
results back — every time the system calculates a route or dispatches a
ticket, this code saves that fact permanently.

*Why it's here:* The brief is explicit: *"the database is not only
storage; it must be part of the running system."* A lot of student
projects fake this by just reading a spreadsheet once. Ours genuinely
opens a real database connection every time the program runs, which you
can prove by deleting `data/campushub.db` and watching the program rebuild
it from scratch.

**4. Index-number parameters (`IndexParameters.java`) — "the fingerprint
that makes this dataset uniquely ours"**

*What it is:* A small file that takes the 14 real student index numbers
on our team and mathematically turns them into five numbers the system
actually uses — for example, the size of a certain table inside the
hash map, or a penalty multiplier applied to bad roads.

*What it does:* Makes several of the system's behaviors literally
determined by our specific team's identity numbers, not hardcoded guesses.

*Why it's here:* The brief requires "at least three algorithm parameters
derived from member index numbers" specifically so that no team can
submit identical, copy-pasted work — if you change the index numbers, the
whole system's behavior shifts slightly. **This file currently has
placeholder numbers and needs our real 14 index numbers put in before
submission** — see the checklist at the end of this document.

### What Foundation Squad members should say at the defense

*"I can show you our six database tables and explain why each rule
exists. I can show you the moment the program connects to the real
database file, reads our seed data, and proves the numbers match — 56
locations, 140 roads, 320 requests. I can also show you how our
individual index numbers get turned into real settings the system uses,
like the size of our hash table."*

---

## Part 5: Structures Squad — "We Built the Organizing Tools Everyone Else Uses"

**Members:** Linear Structures Engineer · Queue Structures Engineer ·
Tree Structures Engineer · Hash & Set/Map Engineer · Priority Queue/Heap &
Scheduling Engineer

### What this squad is, in one sentence

The team that hand-built **every "container" the rest of the system
stores and organizes information in** — the digital equivalent of
building your own filing cabinets, trays, and sorting bins from raw wood,
instead of buying them ready-made.

Fifteen of these containers were built in total. Below, each one is
explained with **what it is, what it does, and why it's here** — the same
three questions, every time.

### Linear Structures (a good starting pair to own)

**MyArrayList — "a stretchy row of labeled boxes"**

*What it is:* Picture a single shelf of numbered boxes, side by side. You
can look at box #7 instantly because you know exactly where it is. When
the shelf runs out of room, we don't panic — we build a new shelf twice
as long and move everything over.

*What it does:* Lets the program store a growing list of things (like all
320 service requests) and instantly jump to any specific one by its
position.

*Why it's here:* Almost everything in the project needs "a list of
things I can grow and look through" — this is the most basic, most-used
building block.

**MyLinkedList — "a treasure-hunt chain"**

*What it is:* Instead of numbered boxes on a shelf, imagine a chain of
notes where each note says "here's a piece of information, and here's
where to find the next note." You don't need to know where everything is
in advance — you just follow the chain.

*What it does:* Lets us add and remove things from the front or back
very quickly, without needing to shift a whole shelf of boxes around.
It's also the backbone that our queue and our hash map are quietly built
on top of.

*Why it's here:* Some jobs (like a constant stream of walk-in tickets)
need fast adding/removing from the ends, which a plain shelf-of-boxes
struggles with — the chain design solves that.

### Queue Structures ("who's next, and in what order")

**MyStack — "a stack of plates"**

*What it is:* Exactly what it sounds like — you can only add or remove
from the top. The last plate you put down is the first one you pick back
up (this is called "Last In, First Out").

*What it does:* Used for two very different things in our project: (1) it
powers one of our two ways of exploring the campus map (explained under
DFS below), and (2) it acts as an **undo log** — every action the system
takes gets "stacked," so in principle you could undo the most recent
action first, the same way Ctrl+Z works.

*Why it's here:* Some problems are naturally "most recent thing first" —
mazes, undo history, and retracing your steps are the classic real-world
examples.

**MyQueue — "the cafeteria line"**

*What it is:* First person in line is the first one served ("First In,
First Out"). No cutting.

*What it does:* Handles ordinary, non-urgent requests that should simply
be handled in the order they arrived, and also powers one of our two ways
of exploring the campus map (BFS, explained below).

*Why it's here:* Fairness by arrival order is the natural default rule
for anything that isn't flagged urgent.

**MyCircularQueue — "the cafeteria line, but the line wraps around a
fixed number of seats"**

*What it is:* A queue with a **fixed size** where, instead of running out
of space, the "next" seat wraps back around to the beginning once the
last seat is reached — like a roundabout with a set number of parking
spots.

*What it does:* Demonstrates handling a fixed-capacity buffer correctly
— knowing when it's genuinely full versus genuinely empty, which is
trickier than it sounds once positions start wrapping around.

*Why it's here:* Real systems (like a shuttle with 12 seats) often have
hard capacity limits, not infinite growth — this structure models that.

**MyDeque — "a line you can join or leave from either end"**

*What it is:* Short for "double-ended queue" — unlike a normal line,
people can be added or removed from **either** the front or the back.

*What it does:* Lets us insert a genuinely urgent request straight to
the front of the line (jumping the queue) while everything else still
respects normal front/back queue rules.

*Why it's here:* Real dispatch systems sometimes need "shove this to the
very front right now" without throwing away the whole queue and rebuilding
it — a deque does exactly that.

### Tree Structures ("organizing things so you can search fast")

**MyBST (Binary Search Tree) — "a decision-tree filing system"**

*What it is:* Imagine organizing files where every new file gets
compared to one already filed: "is my number bigger or smaller than this
one?" Smaller goes left, bigger goes right, and you repeat that decision
at every branch, like a game of "higher or lower."

*What it does:* Lets you search for something much faster than checking
every item one by one, **as long as the tree stays reasonably balanced
(bushy, not stringy)**.

*Why it's here:* It's the simplest way to demonstrate a searchable tree
structure — but it has a real weakness (see next).

**MyAVLTree — "the same filing system, but it self-straightens itself"**

*What it is:* A smarter version of the tree above. If one branch starts
getting too long and stringy (imagine filing numbers 1, 2, 3, 4, 5 in
order — a plain BST would turn into one long unbroken chain, defeating
the whole purpose), the AVL tree automatically rotates itself back into a
short, bushy shape.

*What it does:* Guarantees the tree never degrades into "basically a
list," no matter what order things are added in.

*Why it's here:* This is one of our **strongest pieces of evidence** in
the whole project. We can literally add 10,000 items in sorted order and
show that the plain BST turns into a stringy chain 10,000 levels deep
(essentially useless), while the AVL tree self-corrects and stays only
about 14 levels deep. That's the difference between "a phone book sorted
alphabetically" and "a phone book where every entry is a separate,
disconnected sticky note in a random pile you have to walk through one at
a time."

**MyBTree — "a filing cabinet drawer that holds several files per shelf,
not just one"**

*What it is:* Like the BST, but instead of exactly one item per branching
point, each "node" can hold several items before it needs to split into
more branches — similar to how a real library shelf holds several books
before you need a new shelf.

*What it does:* Models the way real databases organize their internal
search indexes (this is genuinely how professional databases like the
one powering our own filing cabinet organize things behind the scenes).

*Why it's here:* It's a required structure in the brief, and it directly
connects to how our actual SQLite database works internally — a nice
full-circle moment for the defense.

### Hash & Set/Map Structures ("instant lookup by name, not by position")

**MyHashMap — "a phone contacts app"**

*What it is:* Instead of scrolling through a list to find "Ama," a hash
map does clever math on the name itself to jump almost straight to the
right spot. Sometimes two different names "land" on the same spot by bad
luck (this is called a **collision**), and when that happens, that spot
just keeps a short backup chain (built using our MyLinkedList) of
everyone who landed there.

*What it does:* Gives near-instant lookup by ID or name, which is used
constantly throughout the project — it's literally how our in-memory
version of the database tables works, and how the campus road map is
stored internally.

*Why it's here:* Almost everything needs "find this thing by its ID,
fast" — this is the single most-used structure in the whole system.

**MySet — "a guest list where nobody can be added twice"**

*What it is:* Built directly on top of the hash map above, but it only
cares about *whether* something is present, not any extra details about
it.

*What it does:* Used to answer yes/no questions fast — e.g. "have I
already visited this location while exploring the map?"

*Why it's here:* Prevents the system from re-processing the same location
twice while exploring campus connections.

### Priority Queue / Heap & Scheduling Structures

**MyMinHeap — "a hospital triage board that always keeps the most urgent
patient on top"**

*What it is:* A structure shaped like a triangle of items where the most
urgent item is always sitting at the very top, and every level below is
"less urgent than the one above it." When the top item is dealt with, the
structure automatically reshuffles itself so the next most urgent item
rises to the top.

*What it does:* This is the actual engine behind "who gets served next"
in our whole system — it's what powers ticket dispatch.

*Why it's here:* It's dramatically faster than repeatedly scanning every
waiting ticket to find the most urgent one by hand, especially once
there are thousands of tickets.

### Graph Structures ("the campus map itself")

**Graph (adjacency list) — "campus written as a map of connections"**

*What it is:* Every location is a point, and every road is a labeled
line connecting two points, with the road's travel time/condition
written on the line — exactly like a road map with distances marked.
Internally, we store this efficiently by keeping, for every location, a
short list of "which roads lead out of here" (this efficient style is
called an **adjacency list**).

*What it does:* This is the data every routing decision in the whole
project is based on.

*Why it's here:* You cannot answer "what's the fastest route" or "what's
reachable from here" without first representing the map itself in a form
the computer can search through.

**MatrixGraph (adjacency matrix) — "the same map, drawn as a giant grid"**

*What it is:* The same campus connections, but represented instead as a
big table/grid where every location has a row and a column, and the cell
where they cross tells you if (and how) they're connected.

*What it does:* Offers a second, contrasting way to store the exact same
map.

*Why it's here:* The brief specifically asks us to show **both**
representations, because each has real trade-offs: the grid makes
"are these two directly connected?" instantly checkable, but wastes a
huge amount of space when most locations *aren't* directly connected to
each other (which is true of a real, sparse campus road network) — the
adjacency list we use everywhere else avoids that waste.

**DisjointSet (Union-Find) — "sorting people into friend groups"**

*What it is:* Imagine everyone on campus starts in their own separate
friend group of one. Every time you learn two people are friends, you
merge their two groups into one bigger group. At any point, you can
instantly ask "are these two people in the same friend group?"

*What it does:* Used to figure out, while building a minimum-cost network
connecting all campus locations, whether adding a particular road would
accidentally create a wasteful loop (connecting two places that are
already connected some other way).

*Why it's here:* It's the key trick that makes one of our two
"cheapest way to connect everywhere" algorithms (Kruskal's, explained in
the Algorithms Squad section) both correct and fast.

### What Structures Squad members should say at the defense

*"I can draw this structure on the whiteboard, tell you the everyday
real-world thing it behaves like, and show you the exact operation in our
tests that proves it works — including what happens with an empty
structure and a structure with just one item."*

---

## Part 6: Algorithms Squad — "We Wrote the Recipes That Make the Decisions"

**Members:** Search & Sort Engineer · Graph & Routing Engineer ·
Optimisation Engineer

### What this squad is, in one sentence

The team that took the organizing tools the Structures Squad built and
used them to actually **answer the four big questions** from Part 1:
who's next, what's the fastest route, what's reachable, and what can we
afford.

### Searching — "finding one specific thing in a pile"

**Linear search — "checking every drawer one by one"**

*What it is:* Start at the first item, check if it's what you want, and
if not, move to the next. Repeat until found or you run out of items.

*What it does:* Works on absolutely any list, sorted or not — it's the
"guaranteed but slow" option.

*Why it's here:* It's the honest baseline everything else gets compared
against. Every faster method has *some* condition attached (see below).

**Binary search — "the higher-or-lower guessing game"**

*What it is:* Jump straight to the middle of a **sorted** list. If your
target is smaller, throw away the entire right half and repeat in the
left half. If bigger, do the opposite. You cut the remaining pile roughly
in half every single guess.

*What it does:* Finds things dramatically faster than linear search —
searching 10,000 sorted items takes about 14 guesses instead of
potentially 10,000 checks.

*Why it's here — and its one critical catch:* Binary search **only works
if the list is already sorted.** This isn't a minor detail — it's the
whole basis of one of our two required "here's a case where things break"
demonstrations. In our project, we show binary search running on an
*unsorted* list and getting the wrong answer entirely (reporting "not
found" for something that's actually there), while plain linear search
gets it right on the same unsorted list. That's a genuine, working proof
that speed comes with a real trade-off — and this is one of two mandatory
"counterexamples" the brief requires.

### Sorting — "putting things in order"

**Selection sort — "repeatedly picking out the smallest remaining item"**

*What it is:* Go through the whole pile, find the smallest item, move it
to the front. Repeat with what's left. Repeat again. Slow, but simple and
predictable — it always takes the same amount of effort regardless of how
messy or tidy the pile already was.

**Insertion sort — "sorting a hand of playing cards as you pick them up"**

*What it is:* Keep a growing sorted pile in your hand. Each new card gets
slid into its correct position among the cards you're already holding.

*What it does differently:* Genuinely fast if the pile is *already
almost sorted* — it barely has to do any work in that case, unlike
selection sort, which always does the same amount of work either way.

**Merge sort — "divide the pile in half, sort each half, then zip them
back together"**

*What it is:* Split the pile into two, split those into two again
(and again), until you're left with tiny piles of one item each (which
are trivially "sorted"). Then merge pairs of sorted piles back together
in the correct order, over and over, until the whole thing is one sorted
pile.

*What it does:* Reliably fast even on huge piles, and it never gets
"unlucky" — its speed doesn't depend on how messy the starting pile was.

**Quicksort — "pick a referee item, split everyone into 'smaller than the
referee' and 'bigger than the referee,' then repeat inside each group"**

*What it is:* Choose one item as a reference point. Everything smaller
goes to one side, everything bigger to the other. Then do the exact same
thing again inside each side, recursively, until everything is sorted.

*What it does:* Usually the fastest of the four in practice, though a
genuinely unlucky choice of reference point can occasionally slow it
down — our version picks the reference point smartly (comparing three
candidates first) specifically to avoid that bad luck most of the time.

*Why all four exist side by side:* The brief wants us to *prove*, with
real timed measurements, that these genuinely different strategies for
the exact same job perform differently — and they do, dramatically, once
you sort thousands of items instead of just a handful.

### Graph & Routing — "finding your way around campus"

**BFS (Breadth-First Search) — "exploring outward in rings, like ripples
in a pond"**

*What it is:* Starting from one location, first visit everywhere exactly
one road away. Then everywhere exactly two roads away. Then three. And so
on — you fully finish each "ring" of distance before moving further out.
This uses our queue structure.

*What it does:* Finds the shortest route **measured in number of roads
taken**, and is a natural way to answer "what can I reach in at most N
hops?"

**DFS (Depth-First Search) — "picking one direction and committing to it
until you hit a dead end"**

*What it is:* From your starting point, follow one path as far as it
goes. Only when you truly hit a dead end do you backtrack and try the
next unexplored option. This uses our stack structure.

*What it does:* Also finds everywhere reachable, just in a completely
different order/style — useful for different kinds of exploration
problems.

**Dijkstra's Algorithm — "GPS shortest-route finding, taking road
condition into account"**

*What it is:* Not just "fewest roads," but the *genuinely fastest*
route once you account for the fact that some roads take longer to
travel (distance, traffic, poor condition). The algorithm always expands
outward from whichever known location currently has the cheapest total
travel cost so far, gradually working out the true shortest cost to
every location, using our priority-queue (heap) structure to always know
which location to check next.

*What it does:* This is literally the same core idea Google Maps uses to
give you a "fastest route," adapted to our campus. It requires the honest
assumption that no road has a *negative* cost (which makes real-world
sense — you can't travel "negative time").

*Why it's here:* This directly answers Big Question #2 from Part 1 —
"how does help get there fastest?"

**Prim's and Kruskal's Algorithms — "the cheapest possible way to make
sure every location is connected to the network at all, using the fewest
possible roads"**

*What they are:* Two completely different strategies for solving the
same problem: *if you wanted the cheapest possible set of roads that
still connects every single location on campus (no redundant extra
roads), which roads would you keep?*

- **Prim's approach:** start from one location and keep greedily adding
  the cheapest road that connects a *new* location to what you've already
  connected.
- **Kruskal's approach:** instead, sort *every* road by cost cheapest
  first, and go down the list adding each one — unless it would just
  reconnect two locations that are already connected some other way (this
  is exactly where our "friend groups" disjoint-set structure comes in,
  checking for that).

*What's remarkable, and worth showing the examiner:* these two completely
different strategies, when run on the same map, always produce a network
with the **exact same total cost** — even though the actual roads chosen
can differ. Our code proves this by running both and checking the totals
match.

*Why it's here:* This is a genuine practical question — e.g. "if we were
laying new fiber-optic cable to connect every building with the least
total cable," this is exactly the calculation you'd run.

### Optimisation — "making the smartest decision under a real constraint"

**Greedy algorithms — "always grab the best-looking option right now,
without thinking further ahead"**

*What it is:* A simple, fast strategy: at every step, just take whatever
looks like the best deal *at that moment* (in our case: highest value
for the money, spent first), and keep going until you run out of budget.

*What it does:* Fast and simple, and sometimes it happens to give the
genuinely best possible answer.

*Why it's dangerous, and why we're required to prove it:* Greedy
decisions can lock you into a worse overall outcome, because grabbing the
best-looking option *right now* can use up resources you'd have needed
for an even better combination later. **We built and can demonstrate a
real example of this failing**: given three tickets and a fixed budget,
the greedy approach (best value-for-money first) ends up funding two
tickets worth 160 total benefit, while the smarter approach below finds a
different combination worth 220 — genuinely better, but invisible to a
greedy strategy that never looks back once it's made a choice. This is
our second required "here's where a method breaks" proof.

**Dynamic Programming (the "0/1 Knapsack" problem) — "actually weighing
every real combination systematically, without wastefully re-checking the
same ones twice"**

*What it is:* Imagine packing a school bag with a strict weight limit,
and every item has both a cost (space it takes) and a value (how useful
it is) — you want the single best combination of items that fits. Instead
of blindly trying every possible combination one by one (which becomes
impossibly slow very fast), dynamic programming builds up the answer
piece by piece: "given only the first item and a tiny budget, what's the
best I can do? Now given the first two items and a slightly bigger
budget, what's the best I can do?" — reusing each smaller answer to build
the next, bigger answer, instead of recalculating from scratch every
time.

*What it does:* Finds the genuinely, provably best possible combination
of tickets to fund within a fixed budget — unlike the greedy approach,
this one actually looks at the full picture.

*Why it's here:* This directly answers Big Question #4 from Part 1 —
"what can we afford to fix today, for the most benefit?" — and it's the
"smart" method that beats greedy in the counterexample above.

### What Algorithms Squad members should say at the defense

*"I can walk you through this recipe step by step on a small example on
paper, tell you a real-life situation where this exact strategy is used
outside of computing (like GPS navigation, or packing a bag), and show
you the moment in our code and our tests where this exact behavior is
proven — including the case where it deliberately gets the 'obvious'
answer wrong, to prove we understand its limits."*

---

## Part 7: Evidence Squad — "We Proved Everything Actually Works and Is Fast Enough"

**Members:** Testing & Correctness Lead · Performance Analysis & Report
Lead

### What this squad is, in one sentence

The team responsible for the difference between *"we think it works"* and
*"here is hard, repeatable proof that it works, and proof of exactly how
fast it is."* This is the squad that turns everyone else's code into
graded evidence.

### Unit tests — "checking every single piece by hand, over and over,
automatically"

*What it is:* Small, automatic checks. Each one sets up a tiny scenario,
runs one piece of our code, and checks the result against what we
*expect* the correct answer to be — automatically, in under a second, for
every piece, every time we make a change.

*What it does:* We currently have **154 of these checks**, covering every
data structure and every algorithm — well above the 40 the brief
requires. Every structure is tested three ways: a normal, everyday case;
an edge case (what happens with nothing in it, or just one thing in it);
and an invalid case (what happens if you try to do something that
shouldn't be allowed, like removing from an empty structure).

*Why it's here:* Without this, "it works" is just an opinion. With this,
it's 154 independent, repeatable, automatic proofs, and if anyone breaks
something while making changes, these tests will immediately say so.

### Trace tables — "showing our full working, step by step, like a math
teacher demands"

*What it is:* For six of our most important recipes (binary search,
insertion sort, merge sort, Dijkstra's route-finding, Kruskal's network,
and the budget-packing calculation), we don't just show the final answer
— we print out **every single intermediate step** the algorithm actually
took, generated directly from the real running code (not typed up by
hand, so it's guaranteed to genuinely match what the program does).

*What it does:* Gives concrete, undeniable proof of *how* each algorithm
reaches its answer, not just *that* it reaches one.

*Why it's here:* The brief specifically warns that "generic code without
trace evidence will be treated as incomplete" — this is exactly the
evidence that closes that gap.

### Performance benchmarks — "timing everything with a stopwatch, at
different sizes, and proving the theory matches reality"

*What it is:* We run every major algorithm and structure at increasing
sizes (e.g. 100 items, then 1,000, then 10,000, then 20,000), timing each
one with the computer's internal clock, and repeat every measurement
three times to average out random noise — the same way you'd time a
100m sprint multiple times rather than trust a single run.

*What it does:* Produces six sets of results (search speed, sort speed,
hash table behavior under load, our two tree structures compared side by
side, priority queue speed, and route-finding speed), all saved as
spreadsheet-style files ready to be turned into graphs.

*Why it's here:* This is where theory meets reality. Computer science
gives us *predictions* about how fast something "should" be as it grows
(for example: "binary search should barely slow down even as the list
gets 100 times bigger, while linear search should take 100 times
longer"). Our actual measured numbers back this up — for instance, our
own results show the plain tree structure's height growing in a straight
line right up to 10,000 (essentially becoming a very long chain), while
our self-balancing tree barely creeps past 14, even at the exact same
size. **That single comparison is one of the strongest, easiest pieces of
evidence to show an examiner**, because the difference is dramatic and
easy to see on a graph.

### What still needs doing here (see the checklist)

The measurements exist and the spreadsheets are ready — what's left is
turning those spreadsheet files into actual line graphs (a simple task in
Excel or Google Sheets), and writing up, in your own words, what each
graph shows and why.

### What Evidence Squad members should say at the defense

*"I can show you our test summary proving 154 independent checks pass. I
can show you the exact step-by-step trace for any of our six required
algorithms. And I can show you a graph proving that our two tree
structures behave completely differently at scale, exactly as the theory
predicts, with real timed numbers, not guesses."*

---

## Part 8: A Word for Everyone Before the Defense

You don't need to memorize code. You need to be able to explain, in your
own words:

1. **What is the thing you're defending** (in the plain-language terms
   above)?
2. **What does it actually do**, using a real-life comparison?
3. **Why does the project need it** — what question would go unanswered
   without it?
4. **Can you trace through one small example by hand**, on paper, in
   front of the examiner?

If you can do those four things for your one assigned structure and your
one assigned algorithm, you have genuinely defended it — not memorized
it, *understood* it. That's the whole bar.

The examiner is specifically allowed to ask you to *change* something
live — add a location, resize a table, tweak a priority rule. If you
understand the plain-language "why" behind your piece, you can reason
about a small change even if you don't remember the exact line of code —
and that's a completely fair, honest way to handle that moment.

---

## Quick Reference: What Still Needs the Team's Input

- [ ] Put our real 14 index numbers into the parameters file (Foundation
      Squad).
- [ ] Turn the six results spreadsheets into line graphs (Evidence
      Squad).
- [ ] Write up the final report in our own words, using this document as
      the "explain it simply" source and the technical scaffold as the
      structure.
- [ ] Record the demo video.
- [ ] Confirm every one of the 14 members knows their one structure + one
      algorithm for the oral defense.
- [ ] Everyone makes at least one real GitHub commit — see
      `GITHUB_COMMIT_GUIDE.md` for exact, simple steps.
