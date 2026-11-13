package control;

import boundary.GameDisplay;
import boundary.ManagerInput;
import entity.Cashier;
import entity.Chef;
import entity.Customer;
import entity.GameObjective;
import entity.GameResult;
import entity.Money;
import entity.Order;
import entity.PaymentRecord;
import entity.Restaurant;
import entity.ScoreBoard;
import entity.StaffMember;
import entity.Table;
import entity.Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Control: runs the game.
 *
 * Plays the turns in the order of the brief's turn sequence and is the only class
 * that decides what may happen next. It talks to the manager only through GameDisplay
 * and ManagerInput.
 */
public class GameController {

    private final GameConfig config;
    private final Restaurant restaurant;
    private final ScoreBoard scoreBoard;
    private final GameObjective objective;
    private final ArrivalSchedule schedule;
    private final OrderMgr orderMgr;
    private final GameDisplay display;
    private final ManagerInput input;

    public GameController(GameConfig config, Restaurant restaurant, ScoreBoard scoreBoard,
                          ArrivalSchedule schedule, GameDisplay display, ManagerInput input) {
        this.config = config;
        this.restaurant = restaurant;
        this.scoreBoard = scoreBoard;
        this.objective = config.getObjective();
        this.schedule = schedule;
        this.orderMgr = new OrderMgr(restaurant.getMenu());
        this.display = display;
        this.input = input;
    }

    public GameResult play() {
        display.showGameIntro();

        for (int turn = 1; turn <= config.getMaxTurns(); turn++) {
            restaurant.beginTurn();

            handleArrivals(turn);
            display.showTurnStart(turn);

            handleWaiter(turn);     // the Waiter acts before the Chef
            handleChef(turn);

            handleEatingAndPayment(turn);
            handleWaitingAndAbandonment(turn);

            display.showTurnEnd(turn);
        }

        // judged once, after the last turn: all three targets must be met
        GameResult result = objective.isMet(scoreBoard) ? GameResult.VICTORY : GameResult.DEFEAT;
        display.showFinalResult(result);
        return result;
    }

    private void handleArrivals(int turn) {
        Customer arriving = schedule.arrivalFor(turn);
        if (arriving == null) {
            return;
        }
        restaurant.admit(arriving);
        display.showInfo(arriving + " arrives and joins the queue with satisfaction "
                + arriving.getSatisfaction() + ".");
    }

    private void handleWaiter(int turn) {
        Waiter waiter = restaurant.getWaiter();
        List<StaffCommand> legal = legalWaiterCommands(turn);
        StaffCommand command = askUntilLegal(waiter, legal, c -> checkWaiterCommand(c, turn));
        executeWaiterCommand(command, turn);
    }

    private void handleChef(int turn) {
        Chef chef = restaurant.getChef();
        List<StaffCommand> legal = legalChefCommands();
        StaffCommand command = askUntilLegal(chef, legal, c -> checkChefCommand(c, turn));
        executeChefCommand(command, turn);
    }

    private StaffCommand askUntilLegal(StaffMember staff, List<StaffCommand> legal,
                                       Function<StaffCommand, ActionResult> check) {
        if (legal.size() == 1 && legal.get(0).getTask() == StaffTask.WAIT) {
            display.showInfo("No task is available for the " + staff.getRole() + " this turn.");
            return StaffCommand.waitTurn();
        }
        while (true) {
            StaffCommand command = input.askCommand(staff, legal);
            ActionResult result = check.apply(command);
            if (result.isSuccess()) {
                return command;
            }
            display.showError(result.getMessage() + " The " + staff.getRole()
                    + "'s task is still available, so choose again.");
        }
    }

    private List<StaffCommand> legalWaiterCommands(int turn) {
        List<StaffCommand> legal = new ArrayList<StaffCommand>();
        legal.add(StaffCommand.waitTurn());
        if (restaurant.hasFreeTable()) {
            for (Customer customer : restaurant.getWaitingQueue().asList()) {
                legal.add(StaffCommand.seat(customer.getId()));
            }
        }
        for (Customer customer : restaurant.getCustomersAwaitingOrder()) {
            legal.add(StaffCommand.takeOrder(customer.getId()));
        }
        for (Order order : restaurant.getServableOrders(turn)) {
            legal.add(StaffCommand.serve(order.getId()));
        }
        return legal;
    }

    private ActionResult checkWaiterCommand(StaffCommand command, int turn) {
        if (command == null) {
            return ActionResult.failure("That is not a Waiter task.");
        }
        if (!restaurant.getWaiter().hasCapacity()) {
            return ActionResult.failure("The Waiter has already completed a task this turn.");
        }
        switch (command.getTask()) {
            case WAIT:
                return ActionResult.ok();
            case SEAT:
                return checkSeat(command.getTargetId());
            case TAKE_ORDER:
                return checkTakeOrder(command.getTargetId());
            case SERVE:
                return checkServe(command.getTargetId(), turn);
            default:
                return ActionResult.failure("The Waiter cannot "
                        + command.getTask().toString().toLowerCase() + ".");
        }
    }

    private ActionResult checkSeat(int customerId) {
        Customer customer = restaurant.getWaitingQueue().findById(customerId);
        if (customer == null) {
            return ActionResult.failure("Customer #" + customerId
                    + " is not in the waiting queue.");
        }
        if (!restaurant.hasFreeTable()) {
            return ActionResult.failure("Every table is occupied, so nobody can be seated"
                    + " until a customer pays or leaves.");
        }
        return ActionResult.ok();
    }

    private ActionResult checkTakeOrder(int customerId) {
        Customer customer = restaurant.findCustomer(customerId);
        if (customer == null) {
            return ActionResult.failure("There is no customer #" + customerId + ".");
        }
        if (!customer.isAwaitingOrder()) {
            return ActionResult.failure("Customer #" + customerId + " is " + customer.getState()
                    + ", so no order can be taken.");
        }
        return ActionResult.ok();
    }

    private ActionResult checkServe(int orderId, int turn) {
        Order order = restaurant.findOrder(orderId);
        if (order == null) {
            return ActionResult.failure("There is no order #" + orderId + ".");
        }
        if (order.isServableOn(turn)) {
            return ActionResult.ok();
        }
        if (order.needsPreparation()) {
            return ActionResult.failure("Order #" + orderId + " is " + order.describeProgress()
                    + ", so it is not ready to serve.");
        }
        if (order.getReadyAtTurn() == turn) {
            return ActionResult.failure("Order #" + orderId + " only became ready this turn,"
                    + " so it can be served from turn " + (turn + 1) + " onwards.");
        }
        return ActionResult.failure("Order #" + orderId + " is " + order.getStatus()
                + ", so it cannot be served.");
    }

    private void executeWaiterCommand(StaffCommand command, int turn) {
        Waiter waiter = restaurant.getWaiter();
        switch (command.getTask()) {
            case WAIT:
                display.showInfo("The Waiter waits.");
                break;
            case SEAT: {
                Customer customer = restaurant.getWaitingQueue().findById(command.getTargetId());
                Table table = restaurant.firstFreeTable();
                restaurant.removeFromQueue(customer);
                waiter.seat(customer, table);
                display.showInfo("Waiter seats " + customer + " at Table " + table.getNumber()
                        + ".");
                break;
            }
            case TAKE_ORDER: {
                Customer customer = restaurant.findCustomer(command.getTargetId());
                Order order = orderMgr.createOrderFor(customer, turn);
                waiter.takeOrder(customer, order);
                restaurant.registerOrder(order);
                display.showInfo("Waiter takes order #" + order.getId() + ": " + customer
                        + " chooses " + order.getItem().getName() + " for "
                        + Money.format(order.getLockedPriceCents()) + " ("
                        + order.getItem().getPrepUnits() + " preparation unit(s)).");
                break;
            }
            case SERVE: {
                Order order = restaurant.findOrder(command.getTargetId());
                waiter.serve(order, turn);
                scoreBoard.recordServing();
                display.showInfo("Waiter serves order #" + order.getId() + " ("
                        + order.getItem().getName() + ") to " + order.getCustomer()
                        + ", who starts eating.");
                break;
            }
            default:
                break;
        }
    }

    private List<StaffCommand> legalChefCommands() {
        List<StaffCommand> legal = new ArrayList<StaffCommand>();
        legal.add(StaffCommand.waitTurn());
        for (Order order : restaurant.getPreparableOrders()) {
            legal.add(StaffCommand.prepare(order.getId()));
        }
        return legal;
    }

    private ActionResult checkChefCommand(StaffCommand command, int turn) {
        if (command == null) {
            return ActionResult.failure("That is not a Chef task.");
        }
        if (!restaurant.getChef().hasCapacity()) {
            return ActionResult.failure("The Chef has already worked on an order this turn.");
        }
        switch (command.getTask()) {
            case WAIT:
                return ActionResult.ok();
            case PREPARE: {
                Order order = restaurant.findOrder(command.getTargetId());
                if (order == null) {
                    return ActionResult.failure("There is no order #" + command.getTargetId()
                            + ".");
                }
                if (!order.needsPreparation()) {
                    return ActionResult.failure("Order #" + order.getId() + " is "
                            + order.getStatus() + ", so it needs no preparation.");
                }
                return ActionResult.ok();
            }
            default:
                return ActionResult.failure("The Chef can only prepare an order or wait.");
        }
    }

    private void executeChefCommand(StaffCommand command, int turn) {
        if (command.getTask() == StaffTask.WAIT) {
            display.showInfo("The Chef waits.");
            return;
        }
        Order order = restaurant.findOrder(command.getTargetId());
        restaurant.getChef().prepare(order, turn);
        if (order.isServableOn(turn + 1)) {
            display.showInfo("Chef finishes order #" + order.getId() + " ("
                    + order.getItem().getName() + "): it is READY, and can be served from turn "
                    + (turn + 1) + ".");
        } else {
            display.showInfo("Chef works on order #" + order.getId() + " ("
                    + order.getItem().getName() + "): " + order.describeProgress() + " units.");
        }
    }

    private void handleEatingAndPayment(int turn) {
        for (Customer customer : restaurant.getCustomers()) {
            if (customer.hasFinishedEatingBy(turn)) {
                customer.beginReadyToPay(turn);
                display.showInfo(customer + " finishes eating and is ready to pay "
                        + Money.format(customer.getOrder().getLockedPriceCents()) + ".");
            }
        }

        Cashier cashier = restaurant.getCashier();
        List<Customer> payable = restaurant.getPayableCustomers(turn);
        if (!cashier.hasCapacity() || payable.isEmpty()) {
            return;
        }
        Customer next = payable.get(0);
        int heldSatisfaction = next.getSatisfaction();
        PaymentRecord record = cashier.collect(next, turn);
        scoreBoard.recordPayment(record);
        display.showInfo("Cashier collects " + Money.format(record.getAmountCents()) + " from "
                + next + "; Table " + record.getTableNumber() + " is free; "
                + describeRecordedSatisfaction(heldSatisfaction, record.getSatisfaction()) + ".");
    }

    private String describeRecordedSatisfaction(int held, int recorded) {
        if (held == recorded) {
            return "satisfaction " + recorded + " is recorded";
        }
        int swing = recorded - held;
        return "satisfaction " + held + " with a review of " + (swing > 0 ? "+" : "") + swing
                + " is recorded as " + recorded;
    }

    private void handleWaitingAndAbandonment(int turn) {
        for (Customer customer : restaurant.getCustomers()) {
            customer.applyWaitingLoss();
        }
        for (Customer customer : restaurant.getCustomers()) {
            if (!customer.hasRunOutOfPatience()) {
                continue;
            }
            Table table = customer.getTable();
            restaurant.removeFromQueue(customer);
            customer.leaveDissatisfied();
            scoreBoard.recordUnhappyDeparture();
            display.showInfo(customer + " runs out of patience and leaves without paying"
                    + (table == null ? "." : "; Table " + table.getNumber() + " is free."));
        }
    }
}
