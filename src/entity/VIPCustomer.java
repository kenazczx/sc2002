package entity;

/**
 * Entity: a VIP customer.
 *
 * Loses 5 satisfaction per waiting turn and gets 10% off every order.
 */
public class VIPCustomer extends Customer {

    public static final int SATISFACTION_LOSS = 5;
    public static final int BILL_PERCENT = 90;

    public VIPCustomer(int id, String name, int arrivalTurn, int tasteIndex,
                     Appetite appetite) {
        super(id, name, arrivalTurn, tasteIndex, appetite);
    }

    @Override
    public int getSatisfactionLossPerWaitingTurn() {
        return SATISFACTION_LOSS;
    }

    @Override
    public String getTypeLabel() {
        return "VIP";
    }

    @Override
    public int applyCustomerDiscount(int grossCents) {
        return Money.percentOf(grossCents, BILL_PERCENT);
    }
}
