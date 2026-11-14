package entity;

/**
 * Entity: no promotion running.
 *
 * Charges the full price and gives no goodwill, so GameController always has a
 * promotion and never has to check for one.
 */
public class NoPromotion implements Promotion {

    @Override
    public String getName() {
        return "none";
    }

    @Override
    public int applyTo(int grossCents) {
        return grossCents;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public int getTurnsRemaining() {
        return 0;
    }

    @Override
    public int getGoodwillPerActiveTurn() {
        return 0;
    }

    @Override
    public void endTurn() {
    }
}
