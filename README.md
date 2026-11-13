# Restaurant Rush - Stage 2: VIP and Critic customers

SC2002 Object-Oriented Design and Programming group assignment.
This tag is the Stage 2 version: everything in Stage 1, plus VIP and Critic customers.
It contains no Stage 3 feature.

The base line-up is C1 Regular, C2 Critic, C3 Regular, C4 VIP, C5 Regular, C6 Regular:
still six arrivals, with two of the regulars replaced (Section 4.2).

| Customer | Loss per waiting turn | Bill | Special behaviour |
| --- | --- | --- | --- |
| Regular | 8 | full price | - |
| VIP | 5 | 10% off | - |
| Critic | 12 | full price | always orders the most demanding dish; the satisfaction recorded at payment swings by 10 points, up if the Critic was served within 3 turns of arriving and down otherwise |

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
java -cp out app.RestaurantRush --script runs/victory.txt        # VICTORY: 5 paid, $79.20, average 69.0
java -cp out app.RestaurantRush --script runs/critic-first.txt   # VICTORY: 4 paid, $61.20, average 69.3
java -cp out app.RestaurantRush --script runs/defeat.txt         # DEFEAT:  3 paid, $45.00, average 78.7
```

The three runs are the evidence that the manager's choices matter.

* `victory.txt` maximises the number of paying customers and accepts the Critic's
  unfavourable review.
* `critic-first.txt` differs in one decision, the Waiter's turn 4 task. Seating the Critic
  instead of serving C1 is the only way to serve the Critic within three turns, so the
  review swings from -10 to +10, but C1's table stays occupied until turn 8 and the fifth
  customer never gets a table. Four pay instead of five and revenue falls by $18.00.
* `defeat.txt` is a manager who looks busy and still loses: three customers are served
  well, revenue reaches exactly the $45.00 target and average satisfaction is 78.7, but
  the Critic is left in the queue, walks out at the end of turn 12, and only three
  customers pay. All three targets have to be met.

## Class diagram

`docs/class-diagram-stage-2.png` is the class diagram of exactly this code: all 35 classes,
with every member and every relationship checked against the compiled classes of this tag.
It uses the class diagram notation of the Chapter 6 lecture slides, as the Stage 1 diagram
does: every field, constructor and method, with parameters written as `name: type` and
initial values where the code sets them, `{abstract}` under the name of an abstract class, a
boundary, control or entity stereotype icon on every class, a multiplicity and role name at
the end of every association, and notes that point out encapsulation, polymorphism and
composition. The three classes Stage 2 added (`VIPCustomer`, `CriticCustomer` and
`CustomerType`) have an orange border. `docs/class-diagram-stage-2.drawio` is the editable
version (open it in draw.io or at diagrams.net) and `docs/class-diagram-stage-2.svg` a
scalable copy.

## Development harnesses

`tools/` is not part of the game. `tools.AutoRun` plays the game with an
earliest-deadline-first policy and prints the script it chose; `tools.FuzzRun` plays
thousands of games choosing at random among the legal tasks and fails if any state
transition throws or if any task the controller offered is then refused.

```bash
javac -d out -sourcepath src tools/AutoManager.java tools/AutoRun.java tools/FuzzRun.java
java -cp out tools.FuzzRun 4000
```
