package entity;

/**
 * Entity: anything a customer can order.
 *
 * Abstract: a SingleDish stores its price and preparation units, while a ComboMeal
 * works them out from its two parts, so a combo can be used wherever a dish can.
 */
public abstract class MenuItem {

    private final String name;

    protected MenuItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getPriceCents();

    public abstract int getPrepUnits();

    public abstract int getPortions();

    @Override
    public String toString() {
        return getName() + " " + Money.format(getPriceCents()) + ", " + getPrepUnits()
                + (getPrepUnits() == 1 ? " unit" : " units");
    }
}
