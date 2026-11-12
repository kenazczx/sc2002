# Restaurant Rush - Stage 1: basic restaurant operation

SC2002 Object-Oriented Design and Programming group assignment.
This tag is the Stage 1 version: Regular customers, the three menu items, the Waiter,
Chef and Cashier, the full turn sequence, and both endings. It contains no Stage 2 or
Stage 3 feature.

## Team

**Lab:** SCMD | **Team:** 3

| Members |
| --- |
| CHAN ZHAO XIAN |
| DAKSHINAMOORTHI THIYAGESWARAN |
| STEPHANIE ANN KOH SHU HUI |
| TIAN KAI YANG |

## Build and run

From this directory, on any machine with a JDK 17 or newer. The same two commands work in
Windows PowerShell or Command Prompt and in a macOS or Linux terminal: `-sourcepath src`
lets javac find and compile every class that `RestaurantRush` uses.

```bash
javac -d out -sourcepath src src/app/RestaurantRush.java
java -cp out app.RestaurantRush
```

Options:

| Option | Effect |
| --- | --- |
| `--seed <number>` | changes what the six customers order (default 3) |
| `--script <file>` | replays a recorded list of decisions instead of reading the keyboard |

## Playing

At each prompt the game prints the tasks that are currently legal, then waits for a command:

```
wait | seat <customerId> | order <customerId> | serve <orderId> | prep <orderId>
```

An illegal command is explained and the same staff member is asked again, without the
staff task being used up. A staff member whose only legal task is to wait is not asked
at all.

Customer identifiers and order identifiers are numbered independently: orders are
numbered in the order they are taken, so if a customer's order is never taken, later
order numbers do not line up with customer numbers. The floor listing shows both.

## Reproducible runs

```bash
java -cp out app.RestaurantRush --script runs/victory.txt   # VICTORY: 5 paid, $72.00, average 72.8
java -cp out app.RestaurantRush --script runs/defeat.txt    # DEFEAT:  0 paid, 2 customers walk out
```

`runs/victory.txt` also reproduces the worked example in Section 3.3 of the brief exactly:
C1 is a Regular who orders Pasta, loses 8 satisfaction on turns 1, 2 and 3, is served on
turn 4, finishes eating on turn 5 and pays $18.00 on turn 6 with satisfaction 76.

## Class diagram

`docs/class-diagram-stage-1.png` is the class diagram of exactly this code: all 32 classes,
with every member and every relationship checked against the compiled classes of this tag.
It uses the class diagram notation of the Chapter 6 lecture slides: every field, constructor
and method, with parameters written as `name: type` and initial values where the code sets
them, `{abstract}` under the name of an abstract class, a boundary, control or entity
stereotype icon on every class, a multiplicity and role name at the end of every
association, and notes that point out encapsulation, polymorphism and composition.
`docs/class-diagram-stage-1.drawio` is the editable version (open it in draw.io or at
diagrams.net) and `docs/class-diagram-stage-1.svg` a scalable copy.

## Development harnesses

`tools/` is not part of the game. `tools.AutoRun` plays the game with an
earliest-deadline-first policy and prints the script it chose; `tools.FuzzRun` plays
thousands of games choosing at random among the legal tasks and fails if any state
transition throws or if any task the controller offered is then refused.

```bash
javac -d out -sourcepath src tools/AutoManager.java tools/AutoRun.java tools/FuzzRun.java
java -cp out tools.FuzzRun 4000
```
