package entity;

/**
 * Entity interface: a member of staff who can seat a customer.
 *
 * Host and Waiter both implement it, so GameController has one seating routine for
 * both.
 */
public interface SeatingStaff {

    void seat(Customer customer, Table table);

    String getRole();
}
