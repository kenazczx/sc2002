package control;

import entity.Customer;
import entity.Menu;
import entity.MenuItem;
import entity.Order;

/**
 * Control: creates each order and fixes its price.
 *
 * The customer chooses the dish, never the manager. Orders are numbered in the order
 * they are taken.
 */
public class OrderMgr {

    private final Menu menu;
    private int nextOrderId = 1;

    public OrderMgr(Menu menu) {
        this.menu = menu;
    }

    public Order createOrderFor(Customer customer, int turn) {
        MenuItem chosen = customer.chooseFrom(menu);
        int price = customer.applyCustomerDiscount(chosen.getPriceCents());
        return new Order(nextOrderId++, customer, chosen, price, turn);
    }
}
