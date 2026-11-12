package entity;

/**
 * Entity: the three targets that decide victory.
 *
 * isMet needs all three: paid customers, revenue and average satisfaction.
 */
public class GameObjective {

    private final int minPaidCustomers;
    private final int minRevenueCents;
    private final double minAverageSatisfaction;

    public GameObjective(int minPaidCustomers, int minRevenueCents, double minAverageSatisfaction) {
        this.minPaidCustomers = minPaidCustomers;
        this.minRevenueCents = minRevenueCents;
        this.minAverageSatisfaction = minAverageSatisfaction;
    }

    public boolean isPaidCountMet(ScoreBoard scoreBoard) {
        return scoreBoard.getPaidCustomerCount() >= minPaidCustomers;
    }

    public boolean isRevenueMet(ScoreBoard scoreBoard) {
        return scoreBoard.getRevenueCents() >= minRevenueCents;
    }

    public boolean isSatisfactionMet(ScoreBoard scoreBoard) {
        return scoreBoard.hasPayments()
                && scoreBoard.getAverageSatisfaction() >= minAverageSatisfaction;
    }

    public boolean isMet(ScoreBoard scoreBoard) {
        return isPaidCountMet(scoreBoard)
                && isRevenueMet(scoreBoard)
                && isSatisfactionMet(scoreBoard);
    }

    public int getMinPaidCustomers() {
        return minPaidCustomers;
    }

    public int getMinRevenueCents() {
        return minRevenueCents;
    }

    public double getMinAverageSatisfaction() {
        return minAverageSatisfaction;
    }
}
