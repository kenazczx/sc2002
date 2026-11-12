package entity;

/**
 * Entity: one dish on the menu.
 *
 * Holds its name, price in cents and preparation units, none of which change once the
 * menu is built.
 */
public class MenuItem {

    private final String name;
    private final int priceCents;
    private final int prepUnits;

    public MenuItem(String name, int priceCents, int prepUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public String toString() {
        return name + " " + Money.format(priceCents) + ", " + prepUnits
                + (prepUnits == 1 ? " unit" : " units");
    }
}
