package entity;

/**
 * Entity: the Host.
 *
 * Seats at most one queued customer per turn. The Host acts before the Waiter, so the
 * Waiter can take that customer's order in the same turn.
 */
public class Host extends StaffMember implements SeatingStaff {

    public static final int SEATINGS_PER_TURN = 1;

    public Host() {
        super("Host", SEATINGS_PER_TURN);
    }

    @Override
    public void seat(Customer customer, Table table) {
        customer.seatAt(table);
        consumeTask();
    }
}
