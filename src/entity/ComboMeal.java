package entity;

/**
 * Entity: two menu items sold together.
 *
 * Its price is 90% of the two prices added together, rounded to the nearest cent, and
 * its preparation units are the sum of the two items' units. Nothing is stored twice,
 * so it can never disagree with its parts.
 */
public class ComboMeal extends MenuItem {

    public static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(first.getName() + " + " + second.getName());
        this.first = first;
        this.second = second;
    }

    @Override
    public int getPriceCents() {
        return Money.percentOf(first.getPriceCents() + second.getPriceCents(), COMBO_PERCENT);
    }

    @Override
    public int getPrepUnits() {
        return first.getPrepUnits() + second.getPrepUnits();
    }

    @Override
    public int getPortions() {
        return first.getPortions() + second.getPortions();
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }
}
