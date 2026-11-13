package entity;

/**
 * Entity: a food critic.
 *
 * Loses 12 satisfaction per waiting turn and always orders the most demanding dish.
 * The satisfaction recorded at payment moves 10 points up if it was served within 3
 * turns of arriving, and 10 points down otherwise.
 */
public class CriticCustomer extends Customer {

    public static final int SATISFACTION_LOSS = 12;
    public static final int PROMPT_SERVICE_TURNS = 3;
    public static final int REVIEW_SWING = 10;

    public CriticCustomer(int id, String name, int arrivalTurn, int tasteIndex) {
        super(id, name, arrivalTurn, tasteIndex);
    }

    @Override
    public int getSatisfactionLossPerWaitingTurn() {
        return SATISFACTION_LOSS;
    }

    @Override
    public String getTypeLabel() {
        return "Critic";
    }

    @Override
    public MenuItem chooseFrom(Menu menu) {
        return menu.mostDemandingItem();
    }

    @Override
    public int satisfactionToRecord() {
        if (getServedAtTurn() < 0) {
            return getSatisfaction();
        }
        int turnsWaited = getServedAtTurn() - getArrivalTurn();
        int swing = turnsWaited <= PROMPT_SERVICE_TURNS ? REVIEW_SWING : -REVIEW_SWING;
        int reviewed = getSatisfaction() + swing;
        if (reviewed > MAX_SATISFACTION) {
            reviewed = MAX_SATISFACTION;
        }
        if (reviewed < MIN_SATISFACTION) {
            reviewed = MIN_SATISFACTION;
        }
        return reviewed;
    }
}
