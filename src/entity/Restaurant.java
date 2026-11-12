package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Entity: the restaurant floor.
 *
 * Creates its own tables and waiting queue, while the menu and the staff are passed
 * in. It answers questions about the floor but decides nothing.
 */
public class Restaurant {

    private final List<Table> tables = new ArrayList<Table>();
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final Menu menu;

    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;

    private final List<Customer> customers = new ArrayList<Customer>();
    private final List<Order> orders = new ArrayList<Order>();

    public Restaurant(int tableCount, Menu menu, Waiter waiter, Chef chef, Cashier cashier) {
        for (int number = 1; number <= tableCount; number++) {
            tables.add(new Table(number));
        }
        this.menu = menu;
        this.waiter = waiter;
        this.chef = chef;
        this.cashier = cashier;
    }

    public void beginTurn() {
        waiter.beginTurn();
        chef.beginTurn();
        cashier.beginTurn();
    }

    public void admit(Customer customer) {
        customers.add(customer);
        waitingQueue.join(customer);
    }

    public void registerOrder(Order order) {
        orders.add(order);
    }

    public void removeFromQueue(Customer customer) {
        waitingQueue.remove(customer);
    }

    public boolean hasFreeTable() {
        return firstFreeTable() != null;
    }

    public Table firstFreeTable() {
        for (Table table : tables) {
            if (table.isFree()) {
                return table;
            }
        }
        return null;
    }

    public Customer findCustomer(int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    public Order findOrder(int orderId) {
        for (Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public List<Customer> getCustomersAwaitingOrder() {
        List<Customer> result = new ArrayList<Customer>();
        for (Customer customer : customers) {
            if (customer.isAwaitingOrder()) {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Order> getServableOrders(int turn) {
        List<Order> result = new ArrayList<Order>();
        for (Order order : orders) {
            if (order.isServableOn(turn)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> getPreparableOrders() {
        List<Order> result = new ArrayList<Order>();
        for (Order order : orders) {
            if (order.needsPreparation()) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Customer> getPayableCustomers(int turn) {
        List<Customer> result = new ArrayList<Customer>();
        for (Customer customer : customers) {
            if (customer.isPayableOn(turn)) {
                result.add(customer);
            }
        }
        // oldest first
        result.sort(Comparator.comparingInt(Customer::getReadyToPayAtTurn)
                .thenComparingInt(Customer::getId));
        return result;
    }

    public List<Customer> getCustomersOnPremises() {
        List<Customer> result = new ArrayList<Customer>();
        for (Customer customer : customers) {
            if (!customer.hasFinished()) {
                result.add(customer);
            }
        }
        return result;
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public Menu getMenu() {
        return menu;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public Chef getChef() {
        return chef;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public int getTableCount() {
        return tables.size();
    }
}
