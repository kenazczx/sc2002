package entity;

/**
 * Entity: one settled bill.
 *
 * Kept exactly as it was at the moment of payment, so a later event cannot change it.
 */
public class PaymentRecord {

    private final int customerId;
    private final String customerName;
    private final String customerType;
    private final String itemName;
    private final int amountCents;
    private final int satisfaction;
    private final int turn;
    private final int tableNumber;

    public PaymentRecord(int customerId, String customerName, String customerType, String itemName,
                         int amountCents, int satisfaction, int turn, int tableNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerType = customerType;
        this.itemName = itemName;
        this.amountCents = amountCents;
        this.satisfaction = satisfaction;
        this.turn = turn;
        this.tableNumber = tableNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAmountCents() {
        return amountCents;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public int getTurn() {
        return turn;
    }

    public int getTableNumber() {
        return tableNumber;
    }
}
