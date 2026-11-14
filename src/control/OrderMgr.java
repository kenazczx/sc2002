package control;

import entity.Customer;
import entity.Menu;
import entity.MenuItem;
import entity.Order;
import entity.Promotion;

/**
 * Control: creates each order and fixes its price.
 *
 * The customer chooses the dish, never the manager. The customer's own discount is
 * applied first and the promotion's second, and the price never changes after that.
 * Orders are numbered in the order they are taken.
 */
public class OrderMgr {

    private final Menu menu;
    private int nextOrderId = 1;

    public OrderMgr(Menu menu) {
        this.menu = menu;
    }

    public Order createOrderFor(Customer customer, int turn, Promotion promotion) {
        MenuItem chosen = customer.chooseFrom(menu);
        int afterCustomerDiscount = customer.applyCustomerDiscount(chosen.getPriceCents());
        int lockedPrice = promotion.applyTo(afterCustomerDiscount);
        return new Order(nextOrderId++, customer, chosen, lockedPrice, turn);
    }
}
