package entity;

/**
 * Entity: the Waiter.
 *
 * Seats customers, takes orders and serves ready dishes, one task per turn.
 */
public class Waiter extends StaffMember {

    public static final int TASKS_PER_TURN = 1;

    public Waiter() {
        super("Waiter", TASKS_PER_TURN);
    }

    public void seat(Customer customer, Table table) {
        customer.seatAt(table);
        consumeTask();
    }

    public void takeOrder(Customer customer, Order order) {
        customer.attachOrder(order);
        consumeTask();
    }

    public void serve(Order order, int turn) {
        order.markServed(turn);
        order.getCustomer().markServed(turn);
        consumeTask();
    }
}
