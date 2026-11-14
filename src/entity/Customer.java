package entity;

import java.util.List;

/**
 * Entity: a customer, from arrival until they pay or walk out.
 *
 * Abstract: each subclass sets how much satisfaction it loses per waiting turn. Its
 * appetite decides whether it orders a combo or a single dish. Its state changes only
 * through methods that refuse an illegal step.
 */
public abstract class Customer {

    public static final int MAX_SATISFACTION = 100;
    public static final int MIN_SATISFACTION = 0;

    private final int id;
    private final String name;
    private final int arrivalTurn;
    private final int tasteIndex;
    private final Appetite appetite;

    private int satisfaction = MAX_SATISFACTION;
    private CustomerState state = CustomerState.WAITING;
    private Table table;
    private Order order;
    private int servedAtTurn = -1;
    private int readyToPayAtTurn = -1;

    protected Customer(int id, String name, int arrivalTurn, int tasteIndex, Appetite appetite) {
        if (tasteIndex < 0) {
            throw new IllegalArgumentException("tasteIndex must not be negative: " + tasteIndex);
        }
        this.id = id;
        this.name = name;
        this.arrivalTurn = arrivalTurn;
        this.tasteIndex = tasteIndex;
        this.appetite = appetite;
    }

    public abstract int getSatisfactionLossPerWaitingTurn();

    public abstract String getTypeLabel();

    public int applyCustomerDiscount(int grossCents) {
        return grossCents;
    }

    public int satisfactionToRecord() {
        return satisfaction;
    }

    public MenuItem chooseFrom(Menu menu) {
        List<MenuItem> choices = menu.itemsFor(appetite);
        if (choices.isEmpty()) {
            throw new IllegalStateException("The menu has no dishes, so " + name + " cannot order");
        }
        return choices.get(tasteIndex % choices.size());
    }

    public void seatAt(Table newTable) {
        if (state != CustomerState.WAITING) {
            throw new IllegalStateException(name + " is " + state + ", not waiting for a table");
        }
        newTable.occupy(this);
        this.table = newTable;
        this.state = CustomerState.SEATED;
    }

    public void attachOrder(Order newOrder) {
        if (state != CustomerState.SEATED) {
            throw new IllegalStateException(name + " is " + state + ", so no order can be taken");
        }
        this.order = newOrder;
        this.state = CustomerState.ORDERED;
    }

    public void markServed(int turn) {
        if (state != CustomerState.ORDERED) {
            throw new IllegalStateException(name + " is " + state + ", so nothing can be served");
        }
        this.state = CustomerState.SERVED;
        this.servedAtTurn = turn;
    }

    public void beginReadyToPay(int turn) {
        if (state != CustomerState.SERVED) {
            throw new IllegalStateException(name + " is " + state + ", so eating cannot finish");
        }
        this.state = CustomerState.READY_TO_PAY;
        this.readyToPayAtTurn = turn;
    }

    public void markPaid() {
        if (state != CustomerState.READY_TO_PAY) {
            throw new IllegalStateException(name + " is " + state + ", so the bill cannot be settled");
        }
        this.state = CustomerState.PAID;
        leaveTable();
    }

    public void leaveDissatisfied() {
        if (order != null && order.isCancellable()) {
            order.cancel();
        }
        this.state = CustomerState.LEFT;
        leaveTable();
    }

    private void leaveTable() {
        if (table != null) {
            table.release();
            this.table = null;
        }
    }

    public void applyWaitingLoss() {
        if (!isAwaitingService()) {
            return;
        }
        satisfaction -= getSatisfactionLossPerWaitingTurn();
        if (satisfaction < MIN_SATISFACTION) {
            satisfaction = MIN_SATISFACTION;
        }
    }

    public void recover(int points) {
        satisfaction += points;
        if (satisfaction > MAX_SATISFACTION) {
            satisfaction = MAX_SATISFACTION;
        }
    }

    public boolean isAwaitingService() {
        return state == CustomerState.WAITING
                || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    public boolean hasRunOutOfPatience() {
        return isAwaitingService() && satisfaction <= MIN_SATISFACTION;
    }

    public boolean hasFinishedEatingBy(int turn) {
        // eats for one full turn after the turn it was served
        return state == CustomerState.SERVED && servedAtTurn < turn;
    }

    public boolean isPayableOn(int turn) {
        // pays on the turn after it became ready to pay, at the earliest
        return state == CustomerState.READY_TO_PAY && readyToPayAtTurn < turn;
    }

    public boolean isAwaitingOrder() {
        return state == CustomerState.SEATED;
    }

    public boolean hasFinished() {
        return state == CustomerState.PAID || state == CustomerState.LEFT;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public Appetite getAppetite() {
        return appetite;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerState getState() {
        return state;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    public int getServedAtTurn() {
        return servedAtTurn;
    }

    public int getReadyToPayAtTurn() {
        return readyToPayAtTurn;
    }

    @Override
    public String toString() {
        return name + " (" + getTypeLabel() + ")";
    }
}
