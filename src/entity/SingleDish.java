package entity;

/**
 * Entity: one dish on the menu.
 *
 * Holds its price in cents and its preparation units, neither of which changes once
 * the menu is built.
 */
public class SingleDish extends MenuItem {

    private final int priceCents;
    private final int prepUnits;

    public SingleDish(String name, int priceCents, int prepUnits) {
        super(name);
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public int getPortions() {
        return 1;
    }
}
