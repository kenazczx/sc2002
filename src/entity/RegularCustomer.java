package entity;

/**
 * Entity: a regular customer.
 *
 * Loses 8 satisfaction per waiting turn and pays full price.
 */
public class RegularCustomer extends Customer {

    public static final int SATISFACTION_LOSS = 8;

    public RegularCustomer(int id, String name, int arrivalTurn, int tasteIndex) {
        super(id, name, arrivalTurn, tasteIndex);
    }

    @Override
    public int getSatisfactionLossPerWaitingTurn() {
        return SATISFACTION_LOSS;
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
