package entity;

/**
 * Entity: one table, free or occupied by one customer.
 */
public class Table {

    private final int number;
    private Customer occupant;

    public Table(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public Customer getOccupant() {
        return occupant;
    }

    void occupy(Customer customer) {
        if (occupant != null) {
            throw new IllegalStateException("Table " + number + " is already occupied");
        }
        this.occupant = customer;
    }

    void release() {
        this.occupant = null;
    }
}
