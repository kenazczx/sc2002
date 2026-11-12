package boundary;

import control.GameConfig;
import control.StaffCommand;
import entity.Customer;
import entity.GameObjective;
import entity.GameResult;
import entity.Menu;
import entity.MenuItem;
import entity.Money;
import entity.Order;
import entity.PaymentRecord;
import entity.Restaurant;
import entity.ScoreBoard;
import entity.StaffMember;
import entity.Table;

import java.util.List;
import java.util.Scanner;

/**
 * Boundary: the command-line interface.
 *
 * Reads the manager's commands from the keyboard and prints the game. It decides
 * nothing: the controller tells it what to show and which tasks are on offer.
 */
public class ConsoleUI implements GameDisplay, ManagerInput {

    private static final String RULE =
            "==================================================================";
    private static final String THIN =
            "------------------------------------------------------------------";

    private final GameConfig config;
    private final Restaurant restaurant;
    private final ScoreBoard scoreBoard;
    private final GameObjective objective;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(GameConfig config, Restaurant restaurant, ScoreBoard scoreBoard) {
        this.config = config;
        this.restaurant = restaurant;
        this.scoreBoard = scoreBoard;
        this.objective = config.getObjective();
    }

    @Override
    public void showGameIntro() {
        System.out.println(RULE);
        System.out.println(" RESTAURANT RUSH");
        System.out.println(RULE);
        System.out.println(" You are the manager. Staff time is limited, so you decide who is");
        System.out.println(" served first.");
        System.out.println();
        System.out.println(" Setting : " + config.getMaxTurns() + " turns, "
                + restaurant.getTableCount() + " tables, seed " + config.getSeed());
        System.out.println(" Staff   : Waiter 1 task per turn, Chef 1 preparation unit per turn,"
                + " Cashier 1 payment per turn");
        System.out.println(" Targets : at least " + objective.getMinPaidCustomers()
                + " paid customers, revenue at least "
                + Money.format(objective.getMinRevenueCents())
                + ", average satisfaction at least "
                + formatSatisfaction(objective.getMinAverageSatisfaction()));
        System.out.println();
        System.out.println(" Menu:");
        Menu menu = restaurant.getMenu();
        for (MenuItem item : menu.getOrderableItems()) {
            System.out.printf("   %-24s %8s   %d preparation unit(s)%n",
                    item.getName(), Money.format(item.getPriceCents()), item.getPrepUnits());
        }
        System.out.println();
        System.out.println(" Commands: " + CommandParser.SYNTAX);
        System.out.println(RULE);
    }

    @Override
    public void showTurnStart(int turn) {
        System.out.println();
        System.out.println(RULE);
        System.out.printf(" TURN %d of %d%n", turn, config.getMaxTurns());
        System.out.println(THIN);
        System.out.printf(" Revenue %s   Paid customers %d   Average satisfaction %s%n",
                Money.format(scoreBoard.getRevenueCents()),
                scoreBoard.getPaidCustomerCount(),
                formatAverage());
        System.out.println(THIN);
        showQueue();
        showTables();
        showFloor(turn);
        showStaff(turn);
    }

    private void showQueue() {
        List<Customer> queue = restaurant.getWaitingQueue().asList();
        if (queue.isEmpty()) {
            System.out.println(" Waiting queue : empty");
            return;
        }
        StringBuilder line = new StringBuilder(" Waiting queue : ");
        for (int i = 0; i < queue.size(); i++) {
            Customer customer = queue.get(i);
            if (i > 0) {
                line.append(", ");
            }
            line.append("#").append(customer.getId()).append(" ").append(customer.getName())
                    .append(" ").append(customer.getTypeLabel())
                    .append(" sat ").append(customer.getSatisfaction());
        }
        System.out.println(line);
    }

    private void showTables() {
        for (Table table : restaurant.getTables()) {
            Customer occupant = table.getOccupant();
            if (occupant == null) {
                System.out.printf(" Table %-7d free%n", table.getNumber());
            } else {
                System.out.printf(" Table %-7d #%d %s %s, sat %d, %s%n",
                        table.getNumber(), occupant.getId(), occupant.getName(),
                        occupant.getTypeLabel(), occupant.getSatisfaction(), occupant.getState());
            }
        }
    }

    private void showFloor(int turn) {
        System.out.println(THIN);
        System.out.println(" Customers on the premises:");
        List<Customer> onPremises = restaurant.getCustomersOnPremises();
        if (onPremises.isEmpty()) {
            System.out.println("   none");
        }
        for (Customer customer : onPremises) {
            Order order = customer.getOrder();
            String orderText = "no order yet";
            if (order != null) {
                orderText = "order #" + order.getId() + " " + order.getItem().getName() + " "
                        + Money.format(order.getLockedPriceCents()) + " "
                        + order.describeProgress();
                if (order.isServableOn(turn)) {
                    orderText = orderText + " (can be served now)";
                }
            }
            System.out.printf("   #%-3d %-4s %-8s sat %3d  %-13s %s%n",
                    customer.getId(), customer.getName(), customer.getTypeLabel(),
                    customer.getSatisfaction(), customer.getState(), orderText);
        }
    }

    private void showStaff(int turn) {
        System.out.println(THIN);
        System.out.printf(" Staff tasks available: Waiter %d, Chef %d, Cashier %d%n",
                restaurant.getWaiter().getRemainingTasks(),
                restaurant.getChef().getRemainingTasks(),
                restaurant.getCashier().getRemainingTasks());
        System.out.printf(" Work waiting: %d queueing for a table, %d seated without an order,"
                        + " %d ready to serve, %d still in the kitchen%n",
                restaurant.getWaitingQueue().size(),
                restaurant.getCustomersAwaitingOrder().size(),
                restaurant.getServableOrders(turn).size(),
                restaurant.getPreparableOrders().size());
    }

    @Override
    public void showInfo(String message) {
        System.out.println("   . " + message);
    }

    @Override
    public void showError(String message) {
        System.out.println("   ! " + message);
    }

    @Override
    public void showTurnEnd(int turn) {
        System.out.println(THIN);
        System.out.printf(" End of turn %d: served %d, unhappy departures %d, revenue %s,"
                        + " average satisfaction %s, turns used %d of %d%n",
                turn, scoreBoard.getServedCount(), scoreBoard.getUnhappyDepartures(),
                Money.format(scoreBoard.getRevenueCents()), formatAverage(),
                turn, config.getMaxTurns());
    }

    @Override
    public void showFinalResult(GameResult result) {
        System.out.println();
        System.out.println(RULE);
        System.out.println(" FINAL RESULT after " + config.getMaxTurns() + " turns: " + result);
        System.out.println(THIN);
        System.out.printf(" Paid customers       %-10d target %-10d %s%n",
                scoreBoard.getPaidCustomerCount(), objective.getMinPaidCustomers(),
                verdict(objective.isPaidCountMet(scoreBoard)));
        System.out.printf(" Revenue              %-10s target %-10s %s%n",
                Money.format(scoreBoard.getRevenueCents()),
                Money.format(objective.getMinRevenueCents()),
                verdict(objective.isRevenueMet(scoreBoard)));
        System.out.printf(" Average satisfaction %-10s target %-10s %s%n",
                formatAverage(), formatSatisfaction(objective.getMinAverageSatisfaction()),
                verdict(objective.isSatisfactionMet(scoreBoard)));
        System.out.println(THIN);
        System.out.println(" Dishes served " + scoreBoard.getServedCount()
                + ", unhappy departures " + scoreBoard.getUnhappyDepartures());
        if (scoreBoard.hasPayments()) {
            System.out.println(" Payments taken:");
            for (PaymentRecord record : scoreBoard.getPayments()) {
                System.out.printf("   turn %-3d #%-2d %-4s %-8s %-15s %8s  recorded %3d%n",
                        record.getTurn(), record.getCustomerId(), record.getCustomerName(),
                        record.getCustomerType(), record.getItemName(),
                        Money.format(record.getAmountCents()), record.getSatisfaction());
            }
        }
        System.out.println(RULE);
    }

    @Override
    public StaffCommand askCommand(StaffMember staff, List<StaffCommand> legal) {
        while (true) {
            System.out.println(THIN);
            System.out.println(" " + staff.getRole() + " can: " + describe(legal));
            System.out.print(" " + staff.getRole() + " > ");
            if (!scanner.hasNextLine()) {
                System.out.println("wait   (no more input, so the " + staff.getRole()
                        + " waits)");
                return StaffCommand.waitTurn();
            }
            String line = scanner.nextLine();
            StaffCommand command = CommandParser.parse(line);
            if (command != null) {
                return command;
            }
            showError("\"" + line.trim() + "\" is not a command. Use: "
                    + CommandParser.SYNTAX + ".");
        }
    }

    private String describe(List<StaffCommand> legal) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < legal.size(); i++) {
            if (i > 0) {
                text.append(" | ");
            }
            text.append(legal.get(i).toString());
        }
        return text.toString();
    }

    private String formatAverage() {
        if (!scoreBoard.hasPayments()) {
            return "N/A";
        }
        return formatSatisfaction(scoreBoard.getAverageSatisfaction());
    }

    private String formatSatisfaction(double value) {
        return String.format("%.1f", value);
    }

    private String verdict(boolean met) {
        return met ? "MET" : "MISSED";
    }
}
