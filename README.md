# SC2002 Restaurant Rush Group Assignment

Restaurant Rush: a turn-based restaurant
management game with a command-line interface. The player is the manager. Staff time is
limited, so the manager decides which customers and orders come first.

**Repository:** https://github.com/kenazczx/sc2002

This tag (`stage-3`) is the final version, it includes everything in Stages 1 and 2, plus the Host,
combo meals and Happy Hour. Each stage is a tagged, runnable commit:

| Tag | Adds | Run it |
| --- | --- | --- |
| `stage-1` | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings | `git checkout stage-1` |
| `stage-2` | VIP and Critic customers | `git checkout stage-2` |
| `stage-3` | Host, ComboMeal, Happy Hour | `git checkout stage-3` |

Every tag builds and runs with the same two commands (below), and each has its own README,
runs and class diagram.

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
| `--seed <number>` | changes what the six customers order (default 42) |
| `--script <file>` | replays a recorded list of decisions instead of reading the keyboard |

## Playing

At the start of each turn, until it has been used, the game asks whether to switch Happy
Hour on (`y` or `n`). Then it asks the Host, the Waiter and the Chef for one task each,
printing the tasks that are currently legal:

```
wait | seat <customerId> | order <customerId> | serve <orderId> | prep <orderId>
```

An illegal command is explained and the same staff member is asked again, without the
staff task being used up. A staff member whose only legal task is to wait is not asked
at all. The Cashier is not a manager choice: it takes the customer who has been ready to pay
the longest.

Customer identifiers and order identifiers are numbered independently: orders are
numbered in the order they are taken, so if a customer's order is never taken, later
order numbers do not line up with customer numbers. The floor listing shows both.

## The base setting

18 turns and 2 tables. Arrivals at the start of turns 1, 4, 7, 10, 13 and 16, in the line-up
C1 Regular, C2 Critic, C3 Regular, C4 VIP, C5 Regular, C6 Regular. Victory needs all three
of: at least 4 paid customers, revenue of at least $45.00, and an average satisfaction of at
least 60 over the customers who paid.

| Staff | Per turn |
| --- | --- |
| Host | seats at most one queued customer, before the Waiter acts |
| Waiter | one task: seat, take an order or serve |
| Chef | one preparation unit on one order |
| Cashier | one payment |

| Menu item | Price | Preparation units |
| --- | ---: | ---: |
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |
| Salad + Burger | $21.60 | 2 |
| Salad + Pasta | $24.30 | 3 |
| Burger + Pasta | $29.70 | 3 |

| Customer | Loss per waiting turn | Bill | Special behaviour |
| --- | --- | --- | --- |
| Regular | 8 | full price | - |
| VIP | 5 | 10% off | - |
| Critic | 12 | full price | always orders the most demanding item; the satisfaction recorded at payment swings by 10 points, up if the Critic was served within 3 turns of arriving and down otherwise |

### Stage 3 rules

* **Host.** Seats at most one queued customer per turn, in addition to the Waiter's task.
  The Host acts before the Waiter, so the Waiter can take that customer's order in the same
  turn. The Waiter can still seat customers too.
* **Combo meals.** A combo is two menu items sold together: its price is 90% of their
  combined price, rounded to the nearest cent, and its preparation units are their sum.
* **How a customer chooses (our documented rule).** The manager never chooses. Each
  customer gets a taste index and an appetite from a `java.util.Random` seeded with the
  game seed, so a seed always gives the same six orders. Each arrival has a one in three
  chance of a large appetite. A large appetite chooses among the combos and a small one
  among the single dishes, taking item number `tasteIndex mod n` of that group, where `n`
  is the number of items in it. A Critic ignores both and orders the most demanding item:
  the most preparation units, the dearer one on a tie, which on this menu is Burger + Pasta.
* **Happy Hour.** Can be switched on once per game, at the start of a turn, and lasts that
  turn and the next. The start of each turn shows its remaining duration. An order taken
  while it runs costs 20% less, applied after the VIP's 10%, and the price is fixed when
  the order is taken. At the start of each active turn, every customer still waiting for a
  table or for food recovers 15 satisfaction, up to 100.

Seed 42 is the default because it reproduces both worked examples in Section 4.3 of the
brief: C1 orders Salad + Pasta at $24.30 for 3 units, and the VIP arriving on turn 10
orders a Burger, so with Happy Hour on its bill is $15.00 x 0.9 x 0.8 = $10.80.

## Reproducible runs

```bash
java -cp out app.RestaurantRush --script runs/victory.txt            # VICTORY: 5 paid, $88.80, average 83.6
java -cp out app.RestaurantRush --script runs/happy-hour-early.txt   # VICTORY: 5 paid, $85.56, average 91.0
java -cp out app.RestaurantRush --script runs/defeat.txt             # DEFEAT:  2 paid, $34.44, average 88.0
```

* `victory.txt` uses every Stage 3 feature: the Host seats a customer and the Waiter takes
  that order in the same turn, combos are ordered and cooked, the Critic is served within
  3 turns for a favourable review, Happy Hour runs on turns 10 and 11, and the VIP's Burger
  is billed $10.80.
* `happy-hour-early.txt` is the same game with Happy Hour switched on at turn 4 instead:
  the goodwill reaches customers who are still waiting, so average satisfaction rises to
  91.0, at the cost of $3.24 of revenue.
* `defeat.txt` spends Happy Hour on turn 1, when it buys almost nothing, and leaves the
  Critic in the queue until it walks out. Only two customers pay, so two targets are
  missed even though those two are very satisfied.

### Test notes

| Check | How | Result |
| --- | --- | --- |
| Combo price and time (Section 4.3) | `tools.RuleCheck` | Salad + Pasta = $24.30, 3 units |
| VIP discount, then Happy Hour (Section 4.3) | `tools.RuleCheck`, and `runs/victory.txt` turn 10 | $15.00, then $13.50, then $10.80 |
| Happy Hour lasts two turns and shows its duration | `runs/victory.txt` turns 10 to 12 | `Promotion: Happy Hour, 2 turn(s) remaining`, then `1 turn(s) remaining`, then `none` |
| A price is fixed when the order is taken | `runs/victory.txt` | C3's Burger, ordered on turn 8 for $15.00, is paid on turn 11 while Happy Hour runs and still costs $15.00 |
| No illegal state is reachable | `tools.FuzzRun 4000`: random legal play | 4000 games, no exception, and no task the game offered was then refused |
| An illegal choice costs nothing | type `xyz`, `seat 9`, `wait 1` or `seat 0` at turn 1 | refused with a reason, and the same staff member is asked again |
| A bad `--seed` is reported | `--seed` with no number, or `--seed abc` | a message, and the game does not start |
| Rule constants | `tools.RuleCheck` | 31 checks, 0 failures |

## Stage comparisons

Each transition below lists the new files, the existing files that changed or were
deleted, and why. Line counts are from `git diff --numstat` between the two tags.

### Stage 1 → Stage 2: VIP and Critic customers

Compare: https://github.com/kenazczx/sc2002/compare/stage-1...stage-2

New files:

| File | Why |
| --- | --- |
| `src/entity/VIPCustomer.java` | Loses 5 per waiting turn and overrides `applyCustomerDiscount(int)` for 10% off |
| `src/entity/CriticCustomer.java` | Loses 12 per waiting turn, overrides `chooseFrom(Menu)` for its order preference and `satisfactionToRecord()` for its review |
| `src/entity/CustomerType.java` | Lets the arrival line-up be written down as data |
| `runs/critic-first.txt` | A second run that differs from `victory.txt` in one decision |

Changed files:

| File | Lines | Why |
| --- | --- | --- |
| `src/control/ArrivalSchedule.java` | +38 / -9 | Gained the base line-up and creates the right subclass for each `CustomerType`. It is the only class that names a customer type. |
| `src/entity/Menu.java` | +16 / -0 | Gained `mostDemandingItem()`, which the Critic's order preference uses |
| `src/control/GameController.java` | +12 / -2 | The payment line also prints the Critic's review, so the average satisfaction can be checked from the transcript. No rule changed. |
| `runs/victory.txt`, `runs/defeat.txt` | rewritten | The Stage 2 line-up changes who arrives, so the recorded decisions changed |

No source file was deleted. `Customer`, `Order`, `Restaurant`, the staff classes, `OrderMgr`
and the boundary classes did not change: `Customer` was already abstract in Stage 1, so the
two new types only add subclasses, and `GameController` never tests a customer's type.

### Stage 2 → Stage 3: Host, ComboMeal and Happy Hour

Compare: https://github.com/kenazczx/sc2002/compare/stage-2...stage-3

This range also contains the input-validation fixes committed after the `stage-2` tag
(`CommandParser`, `RestaurantRush` and `Customer`); they are listed with the changes.

New files:

| File | Why |
| --- | --- |
| `src/entity/Host.java` | The new role: seats at most one queued customer per turn |
| `src/entity/SeatingStaff.java` | Two roles can now seat, so `GameController` has one seating routine for both |
| `src/entity/SingleDish.java` | What `MenuItem` used to be: a dish that stores its own price and preparation units |
| `src/entity/ComboMeal.java` | Two menu items sold together; its price and preparation units come from its parts |
| `src/entity/Appetite.java` | `SMALL` or `LARGE`: the documented rule for choosing a combo |
| `src/entity/Promotion.java` | A restaurant-wide pricing policy |
| `src/entity/NoPromotion.java` | "No promotion" as an object, so the controller never checks for null |
| `src/entity/HappyHourPromotion.java` | 20% off orders taken while it runs, 15 goodwill per active turn, 2 turns |
| `runs/happy-hour-early.txt` | A second victory run, with Happy Hour switched on early |
| `tools/RuleCheck.java` | Development tool: checks the brief's prices and rules |

Changed files:

| File | Lines | Why |
| --- | --- | --- |
| `src/control/GameController.java` | +107 / -14 | Three new steps at the start of a turn: the Happy Hour question, the goodwill for waiting customers, and the Host's seating before the Waiter is asked. Seating became one routine, `performSeating(SeatingStaff, Customer)`, shared by the Host and the Waiter. Orders are priced with the promotion in force. |
| `src/boundary/ConsoleUI.java` | +70 / -6 | Shows the Host's task and the promotion's remaining duration, asks the Happy Hour question, and says which waiting customers its goodwill would reach |
| `src/app/RestaurantRush.java` | +23 / -6 | Builds the three combos and the Host. Fix after `stage-2`: a missing or non-numeric `--seed` now stops with a message instead of being ignored. |
| `src/entity/MenuItem.java` | +11 / -17 | Became abstract, because a combo works out its price and preparation units from its parts instead of storing them; the stored version moved to `SingleDish`. `getName()`, `getPriceCents()` and `getPrepUnits()` kept their signatures, so `Order` and the rest of the game did not change. |
| `src/entity/Menu.java` | +19 / -3 | Gained `itemsFor(Appetite)`, so the combo rule lives with the menu. `mostDemandingItem()` did not change; on the Stage 3 menu it returns Burger + Pasta. |
| `src/boundary/ScriptedInput.java` | +18 / -2 | Answers the Happy Hour question from a `y` or `n` line in the script |
| `src/entity/Customer.java` | +16 / -3 | Gained the `appetite` field and constructor parameter, and `chooseFrom` asks `menu.itemsFor(appetite)`. Fix after `stage-2`: a negative taste index or an empty menu is refused. |
| `src/control/ArrivalSchedule.java` | +13 / -7 | Draws each customer's appetite from the seeded `Random`, after its taste index |
| `src/entity/Restaurant.java` | +9 / -1 | Holds the Host and starts its turn with the other staff |
| `src/control/OrderMgr.java` | +8 / -5 | Applies the promotion after the customer's own discount, the order Section 4.3 sets |
| `src/boundary/CommandParser.java` | +7 / -1 | Fix after `stage-2`: a command with extra words (`wait 1`, `seat 1 2`) or a target of 0 or less is refused |
| `src/entity/CriticCustomer.java` | +6 / -5 | Passes the appetite on; its comment now says the most demanding item is a combo |
| `src/entity/RegularCustomer.java`, `src/entity/VIPCustomer.java` | +3 / -2 each | Pass the appetite on |
| `src/boundary/GameDisplay.java` | +2 / -1 | `showTurnStart` takes the promotion, so its remaining duration can be shown |
| `src/boundary/ManagerInput.java` | +2 / -0 | Added `askActivateHappyHour()` |
| `src/control/GameConfig.java` | +2 / -1 | Default seed 42, which reproduces both worked examples in Section 4.3 |
| `src/entity/Waiter.java` | +2 / -1 | Implements `SeatingStaff`; `seat(...)` itself did not change |
| `runs/victory.txt`, `runs/defeat.txt` | rewritten | The Host, the combos and the Happy Hour question change the recorded decisions |
| `tools/AutoManager.java`, `tools/AutoRun.java`, `tools/FuzzRun.java` | +62 / -14 | The development tools also play the Host and Happy Hour |

Deleted files:

| File | Why |
| --- | --- |
| `runs/critic-first.txt` | Written for the Stage 2 seed and staff, so it does not replay in Stage 3 |
| `.Rhistory` | An empty editor file committed by mistake; now in `.gitignore` |

No game rule from Stages 1 and 2 was rewritten. `Order`, `Table`, `WaitingQueue`,
`ScoreBoard`, `PaymentRecord`, `GameObjective`, `StaffMember`, `Chef`, `Cashier`,
`CustomerType`, `CustomerState`, `OrderStatus`, `GameResult`, `Money`, `StaffCommand`,
`StaffTask` and `ActionResult` are byte-for-byte the same as at `stage-2`.

## Diagrams

`docs/` holds one class diagram per stage and the sequence diagram:

| File | Shows |
| --- | --- |
| `docs/class-diagram-stage-1.*` | Stage 1: all 32 classes |
| `docs/class-diagram-stage-2.*` | Stage 2: all 35 classes; the 3 added in Stage 2 have an orange border |
| `docs/class-diagram-stage-3.*` | Stage 3: all 43 classes; the 8 added in Stage 3 have an orange border |
| `docs/sequence-diagram-stage-3.*` | One complete scenario from `runs/victory.txt`: the VIP C4 from arrival to payment, with Happy Hour pricing, then the result check |

The class diagrams use the notation of the Chapter 6 lecture slides: every field,
constructor and method, a boundary, control or entity stereotype icon on every class, a
multiplicity and role name at the end of every association, and notes that point out
encapsulation, polymorphism and composition. Each one was checked against the compiled
classes of its own stage. The `.drawio` files are editable (open them in draw.io or at
diagrams.net), and the `.svg` files are scalable copies.

## Video timestamps

| # | Segment | Timestamp | Speaker |
| --- | --- | --- | --- |
| 1 | The game launching from the submitted project | `00:00` | |
| 2 | Meaningful manager choices, including a decision where staff are constrained | `00:00` | |
| 3 | A dish going READY → SERVED → payment | `00:00` | |
| 4 | A VIP or Critic behaviour | `00:00` | |
| 5 | A Stage 3 feature | `00:00` | |
| 6 | One victory and one defeat (separate reproducible runs) | `00:00` | |
| 7 | The stage tag comparisons and the diagrams | `00:00` | |

## Development harnesses

`tools/` is not part of the game, and nothing in `src/` depends on it.

```bash
javac -d out -sourcepath src tools/AutoManager.java tools/AutoRun.java tools/FuzzRun.java tools/RuleCheck.java
java -cp out tools.RuleCheck       # 31 checks of the brief's prices and rules
java -cp out tools.FuzzRun 4000    # 4000 games of random legal play: no exception, no refused task
java -cp out tools.AutoRun         # plays the game itself and prints the script it chose
```
