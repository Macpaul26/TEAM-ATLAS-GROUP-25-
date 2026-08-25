# GitHub Guide — How Every Member Makes Their Own Commit

**Ghana Smart Service Operations Optimizer · Team SEG26-41 SYNERGY**

> **Why this document exists:** the lecturer requires every one of the 14
> team members to personally make commits to our GitHub repository — this
> is how they check individual participation, separately from the oral
> defense. This guide assumes you have **never used Git or GitHub before**
> and walks you through it in plain steps. You only need to do this
> **once** to satisfy the requirement, though you're welcome to do more.

---

## Part 1: What Is GitHub, in Plain Words?

Think of GitHub as **Google Drive, but built specifically for code, with a
permanent, provable history of exactly who changed what, and when.**

- **Repository ("repo")** = the shared project folder. Ours will be
  called something like `SEG26-41-SYNERGY`.
- **Commit** = a saved snapshot of a change, with your name attached to
  it automatically, and a short message explaining what you changed.
  This is the actual proof of your individual contribution.
- **Branch** = your own personal, separate copy of the project folder to
  work in, so you can't accidentally break anyone else's work while
  you're making your change. Once you're happy with your change, you
  merge your branch back into the main copy.
- **Pull Request (PR)** = a request saying "I've made this change on my
  branch, please review it and merge it into the main project." This
  gives at least one teammate a chance to look over your change before it
  becomes permanent.

**The rule we're following:** nobody commits directly to the `main`
branch. Everyone works on their own branch, then opens a Pull Request,
and at least one other teammate approves it before it's merged in. This
is standard real-world practice, and it also means the repo's history
clearly shows who did what.

---

## Part 2: One-Time Setup (Do This Once, Before Your First Commit)

### Step 1 — Install Git on your computer

- **Windows:** download and install from [git-scm.com](https://git-scm.com/downloads).
  Accept the default options during installation.
- **Mac:** open Terminal and type `git --version` — if it's not
  installed, your Mac will prompt you to install it automatically.
- **Linux:** run `sudo apt install git` (or your distribution's
  equivalent).

### Step 2 — Create a free GitHub account

Go to [github.com](https://github.com) and sign up, if you don't already
have an account.

### Step 3 — Get added to the repository

Whoever on the **Foundation Squad** creates the repository (this should be
the Project Lead) needs to:

1. Create a new repository on GitHub named `SEG26-41-SYNERGY`.
2. Upload this entire project folder into it (drag-and-drop upload works
   fine for the first upload, or use the commands in Part 3 below).
3. Go to the repo's **Settings → Collaborators**, and add all 13 other
   teammates by their GitHub username or email, so everyone has
   permission to push their own branches.

Once you've been added, you'll get an email invite — accept it.

### Step 4 — Introduce yourself to Git (one time, on your own computer)

Open a terminal (Command Prompt, PowerShell, or Terminal app) and type,
replacing the name and email with your own:

```bash
git config --global user.name "Your Full Name"
git config --global user.email "your.email@example.com"
```

This is what makes your commits show up under your real name — it's how
the lecturer confirms it was genuinely you.

### Step 5 — Download ("clone") the project to your computer

```bash
git clone https://github.com/<your-team-org-or-username>/SEG26-41-SYNERGY.git
cd SEG26-41-SYNERGY
```

*(Replace the URL with the actual repo link once it's created — your
Project Lead will share it.)*

You now have your own full copy of the project on your computer.

---

## Part 3: Making Your Commit — The Same Five Steps, Every Time

No matter which squad you're on, the process is always the same five
steps. We'll give each squad a specific, safe, easy task below, but here
is the general pattern first:

```bash
# 1. Make sure you're starting from the latest version
git checkout main
git pull

# 2. Create your own branch (name it after your task)
git checkout -b yourname-shortdescription

# 3. Make your change (edit the file — see your squad's task below)

# 4. Save (commit) your change with a clear message
git add .
git commit -m "M3: <describe your change here>"

# 5. Upload your branch to GitHub
git push -u origin yourname-shortdescription
```

After step 5, go to the repository on GitHub in your browser — you'll see
a banner offering to **"Compare & pull request."** Click it, add a short
description, and submit it. Ask one teammate (ideally from your squad) to
click **"Approve"** and then **"Merge"** on your PR.

**That's it — you now have a real, permanent commit under your name.**

> **Commit message convention:** start your message with the module you
> touched, e.g. `M3: add a test case for MyDeque`, `M7: add comment
> explaining Dijkstra's precondition`, `M2: fix typo in schema comment`.
> This is what the workflow plan calls "the repo doubling as evidence" —
> the lecturer can read the commit history and see exactly who worked on
> what.

---

## Part 4: Your Squad's Specific, Safe Task

Everyone doesn't need to write new code to satisfy this requirement — a
genuine, understood, useful change is enough. Below is a real, safe task
per squad member that touches a file they're already responsible for
defending, so making the commit doubles as reinforcing your own
understanding.

### Foundation Squad

| Member | A safe, real task |
|---|---|
| Project Lead & Systems Architect | Open `README.md`. Add your name to a new "Team Members" section at the bottom, with your index number and your assigned structure/algorithm from `PLAIN_LANGUAGE_GUIDE.md`. |
| Local Context & Dataset Lead | Open `data/locations.csv`. Add one more realistic Legon location as a new row (pick a real building not already listed), giving it a unique ID. |
| Database Architect | Open `data/schema.sql`. Add a short comment (a line starting with `--`) above one table explaining, in your own words, what that table is for. |
| Database Integration Engineer | Open `src/campushub/db/Database.java`. Add a short `//` comment above the `loadGraph` method explaining, in plain words, what it does. |

### Structures Squad

| Member | A safe, real task |
|---|---|
| Linear Structures Engineer | Open `src/campushub/test/DataStructureTests.java`. Add one new small test case for `MyArrayList` (e.g. test that `contains()` returns false on an empty list). Run the tests to confirm it passes before committing. |
| Queue Structures Engineer | Open `src/campushub/ds/MyCircularQueue.java`. Add a short comment explaining, in your own words, what "wrap-around" means, above the `enqueue` method. |
| Tree Structures Engineer | Open `src/campushub/ds/MyAVLTree.java`. Add a comment above `insert` explaining, in plain words, why rotations are needed. |
| Hash & Set/Map Engineer | Open `src/campushub/ds/MyHashMap.java`. Add a comment above `collisionCount()` explaining what a collision is, in plain words. |
| Priority Queue/Heap & Scheduling Engineer | Open `src/campushub/ds/MyMinHeap.java`. Add a comment above `siftUp` explaining, in plain words, what it's doing. |

### Algorithms Squad

| Member | A safe, real task |
|---|---|
| Search & Sort Engineer | Open `src/campushub/algo/Searching.java`. Add a comment above `binarySearch` stating the precondition (the list must be sorted) in your own words. |
| Graph & Routing Engineer | Open `src/campushub/algo/ShortestRoute.java`. Add a comment above `findShortestPath` explaining, in plain words, why Dijkstra needs non-negative weights. |
| Optimisation Engineer | Open `src/campushub/algo/GreedyAssigner.java`. Add a comment above `greedyFailureExample` explaining, in your own words, why greedy fails on that example. |

### Evidence Squad

| Member | A safe, real task |
|---|---|
| Testing & Correctness Lead | Run `java -cp bin campushub.RunTests`, screenshot the "154 PASSED" summary, and add the screenshot to a new `docs/screenshots/` folder, then commit it. |
| Performance Analysis & Report Lead | Run `java -cp bin campushub.RunBenchmarks`, then open one of the new `results/*.csv` files and commit it (if it isn't already tracked), with a message noting which experiment it's evidence for. |

**Everyone:** after making your change, re-run the tests
(`java -cp bin campushub.RunTests`) if you touched any `.java` file, to
make sure nothing broke, before you commit.

---

## Part 5: Common Problems and Simple Fixes

**"git pull" says I have local changes that would be overwritten**
You probably still have an uncommitted change from before. Either commit
it first, or run `git stash` to temporarily set it aside.

**I get a permission error when I push**
You likely haven't been added as a collaborator yet (Part 2, Step 3), or
you're not logged into Git with the right account. Ask the Project Lead
to confirm your invite was sent and accepted.

**Someone else changed the same file as me**
Git will tell you there's a "merge conflict." Don't panic — this is
normal on a 14-person team. Open the file; Git marks the conflicting
lines with `<<<<<<<`, `=======`, and `>>>>>>>`. Decide which version (or
combination) is correct, delete the marker lines, save, then
`git add .` and `git commit` as normal. If you're stuck, ask a squad-mate
who's done this before.

**I don't know how to use a terminal at all**
GitHub Desktop (a free app from
[desktop.github.com](https://desktop.github.com)) does everything above
with buttons instead of typed commands — clone, branch, commit, push, and
pull request, all clickable. If terminals aren't your thing, use this
instead.

---

## Part 6: Checklist

- [ ] Git installed and configured with your real name and email.
- [ ] GitHub account created, and you've accepted the collaborator invite.
- [ ] Repository cloned to your computer.
- [ ] You created your own branch.
- [ ] You made your squad's assigned change (or another genuine,
      understood change to something you're defending).
- [ ] You committed with a clear message starting with the module code.
- [ ] You pushed your branch and opened a Pull Request.
- [ ] A teammate reviewed and merged it.
- [ ] Your name now appears in the repository's commit history —
      this is your proof of individual contribution.
