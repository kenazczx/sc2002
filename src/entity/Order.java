package entity;

/**
 * Entity: one customer's order.
 *
 * The price is fixed when the order is taken. The status changes only through
 * advancePreparation, markServed, markPaid and cancel, and each one refuses an
 * out-of-order call.
 */
public class Order {

    private final int id;
    private final Customer customer;
    private final MenuItem item;
    private final int lockedPriceCents;
    private final int takenAtTurn;

    private int completedPrepUnits;
    private OrderStatus status = OrderStatus.PLACED;
    private int readyAtTurn = -1;
    private int servedAtTurn = -1;

    public Order(int id, Customer customer, MenuItem item, int lockedPriceCents, int takenAtTurn) {
        this.id = id;
        this.customer = customer;
        this.item = item;
        this.lockedPriceCents = lockedPriceCents;
        this.takenAtTurn = takenAtTurn;
        this.completedPrepUnits = 0;
    }

    public boolean needsPreparation() {
        return status == OrderStatus.PLACED || status == OrderStatus.PREPARING;
    }

    public void advancePreparation(int turn) {
        if (!needsPreparation()) {
            throw new IllegalStateException("Order #" + id + " is " + status
                    + ", so it needs no preparation");
        }
        completedPrepUnits++;
        if (completedPrepUnits >= item.getPrepUnits()) {
            status = OrderStatus.READY;
            readyAtTurn = turn;
        } else {
            status = OrderStatus.PREPARING;
        }
    }

    public boolean isServableOn(int turn) {
        // a dish finished this turn can be served from next turn
        return status == OrderStatus.READY && readyAtTurn < turn;
    }

    public void markServed(int turn) {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Order #" + id + " is " + status + ", not READY");
        }
        status = OrderStatus.SERVED;
        servedAtTurn = turn;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Order #" + id + " is " + status + ", not SERVED");
        }
        status = OrderStatus.PAID;
    }

    public boolean isCancellable() {
        return status == OrderStatus.PLACED
                || status == OrderStatus.PREPARING
                || status == OrderStatus.READY;
    }

    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException("Order #" + id + " is " + status
                    + ", so it cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public int getRemainingPrepUnits() {
        return item.getPrepUnits() - completedPrepUnits;
    }

    public String describeProgress() {
        if (needsPreparation()) {
            return status + " " + completedPrepUnits + "/" + item.getPrepUnits();
        }
        return status.toString();
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getLockedPriceCents() {
        return lockedPriceCents;
    }

    public int getTakenAtTurn() {
        return takenAtTurn;
    }

    public int getCompletedPrepUnits() {
        return completedPrepUnits;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getReadyAtTurn() {
        return readyAtTurn;
    }

    public int getServedAtTurn() {
        return servedAtTurn;
    }

    @Override
    public String toString() {
        return "#" + id + " " + item.getName() + " " + Money.format(lockedPriceCents);
    }
}
