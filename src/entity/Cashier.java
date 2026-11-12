package entity;

/**
 * Entity: the Cashier.
 *
 * Collects at most one payment per turn. This is not a manager choice: the controller
 * hands it the customer who has been ready to pay the longest.
 */
public class Cashier extends StaffMember {

    public static final int PAYMENTS_PER_TURN = 1;

    public Cashier() {
        super("Cashier", PAYMENTS_PER_TURN);
    }

    public PaymentRecord collect(Customer customer, int turn) {
        Order order = customer.getOrder();
        int amountCents = order.getLockedPriceCents();
        int satisfaction = customer.satisfactionToRecord();
        int tableNumber = customer.getTable().getNumber();

        order.markPaid();
        customer.markPaid();
        consumeTask();

        return new PaymentRecord(customer.getId(), customer.getName(), customer.getTypeLabel(),
                order.getItem().getName(), amountCents, satisfaction, turn, tableNumber);
    }
}
