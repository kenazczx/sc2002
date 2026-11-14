package entity;

/**
 * Entity interface: a restaurant-wide pricing policy the manager can switch on.
 *
 * GameController holds one at all times and never asks which: NoPromotion leaves
 * prices as they are and HappyHourPromotion takes 20% off.
 */
public interface Promotion {

    String getName();

    int applyTo(int grossCents);

    boolean isActive();

    int getTurnsRemaining();

    int getGoodwillPerActiveTurn();

    void endTurn();
}
