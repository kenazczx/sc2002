package entity;

/**
 * Entity: Happy Hour.
 *
 * Runs for 2 turns, starting with the turn it is switched on. An order taken while it
 * runs costs 20% less, and at the start of each of those turns every customer still
 * waiting for a table or for food recovers 15 satisfaction.
 */
public class HappyHourPromotion implements Promotion {

    public static final int BILL_PERCENT = 80;
    public static final int DURATION_TURNS = 2;
    public static final int GOODWILL = 15;

    private int turnsRemaining = DURATION_TURNS;

    @Override
    public String getName() {
        return "Happy Hour";
    }

    @Override
    public int applyTo(int grossCents) {
        return isActive() ? Money.percentOf(grossCents, BILL_PERCENT) : grossCents;
    }

    @Override
    public boolean isActive() {
        return turnsRemaining > 0;
    }

    @Override
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    @Override
    public int getGoodwillPerActiveTurn() {
        return isActive() ? GOODWILL : 0;
    }

    @Override
    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }
}
