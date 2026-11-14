package tools;

import boundary.GameDisplay;
import boundary.ManagerInput;
import control.StaffCommand;
import control.StaffTask;
import entity.Customer;
import entity.GameResult;
import entity.MenuItem;
import entity.Order;
import entity.Promotion;
import entity.Restaurant;
import entity.StaffMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Development tool, not part of the game.
 *
 * Plays the game by itself, most urgent customer first, and records the commands it
 * chose so they can be replayed as a script.
 */
public class AutoManager implements ManagerInput, GameDisplay {

    private final Restaurant restaurant;
    private final GameDisplay delegate;
    private final int maxTurns;
    private final List<String> recorded = new ArrayList<String>();
    private int turn;

    public AutoManager(Restaurant restaurant, GameDisplay delegate, int maxTurns) {
        this.restaurant = restaurant;
        this.delegate = delegate;
        this.maxTurns = maxTurns;
    }

    public List<String> getRecorded() {
        return recorded;
    }

    private int serveDeadline() {
        return maxTurns - 2;
    }

    private int orderDeadline(int units) {
        return serveDeadline() - units;
    }

    private int seatDeadline(int units) {
        return orderDeadline(units) - 1;
    }

    @Override
    public StaffCommand askCommand(StaffMember staff, List<StaffCommand> legal) {
        StaffCommand chosen = staff.getRole().equals("Chef") ? chooseChef(legal) : chooseWaiter(legal);
        recorded.add(chosen.toString());
        return chosen;
    }

    @Override
    public boolean askActivateHappyHour() {
        boolean activate = turn == happyHourTurn;
        if (activate) {
            recorded.add("y");
        }
        return activate;
    }

    private int happyHourTurn = -1;

    public void setHappyHourTurn(int t) {
        this.happyHourTurn = t;
    }

    private StaffCommand chooseWaiter(List<StaffCommand> legal) {
        StaffCommand best = StaffCommand.waitTurn();
        int bestDeadline = Integer.MAX_VALUE;
        int bestStage = -1;
        for (StaffCommand command : legal) {
            int deadline;
            int stage;
            if (command.getTask() == StaffTask.SERVE) {
                deadline = serveDeadline();
                stage = 3;
            } else if (command.getTask() == StaffTask.TAKE_ORDER) {
                Customer customer = restaurant.findCustomer(command.getTargetId());
                deadline = orderDeadline(unitsFor(customer));
                stage = 2;
            } else if (command.getTask() == StaffTask.SEAT) {
                Customer customer = restaurant.findCustomer(command.getTargetId());
                deadline = seatDeadline(unitsFor(customer));
                stage = 1;
            } else {
                continue;
            }
            if (deadline < turn) {
                continue;
            }
            if (deadline < bestDeadline || (deadline == bestDeadline && stage > bestStage)) {
                bestDeadline = deadline;
                bestStage = stage;
                best = command;
            }
        }
        return best;
    }

    private int unitsFor(Customer customer) {
        MenuItem item = customer.chooseFrom(restaurant.getMenu());
        return item.getPrepUnits();
    }

    private StaffCommand chooseChef(List<StaffCommand> legal) {
        StaffCommand best = StaffCommand.waitTurn();
        int bestSlack = Integer.MAX_VALUE;
        for (StaffCommand command : legal) {
            if (command.getTask() != StaffTask.PREPARE) {
                continue;
            }
            Order order = restaurant.findOrder(command.getTargetId());
            int slack = (serveDeadline() - turn) - order.getRemainingPrepUnits();
            if (slack < bestSlack) {
                bestSlack = slack;
                best = command;
            }
        }
        return best;
    }

    @Override
    public void showGameIntro() {
        delegate.showGameIntro();
    }

    @Override
    public void showTurnStart(int currentTurn, Promotion promotion) {
        this.turn = currentTurn;
        delegate.showTurnStart(currentTurn, promotion);
    }

    @Override
    public void showInfo(String message) {
        delegate.showInfo(message);
    }

    @Override
    public void showError(String message) {
        delegate.showError(message);
    }

    @Override
    public void showTurnEnd(int currentTurn) {
        delegate.showTurnEnd(currentTurn);
    }

    @Override
    public void showFinalResult(GameResult result) {
        delegate.showFinalResult(result);
    }
}
