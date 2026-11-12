package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity: the customers waiting for a table, in arrival order.
 *
 * The manager may seat any of them, not only the first.
 */
public class WaitingQueue {

    private final List<Customer> queue = new ArrayList<Customer>();

    public void join(Customer customer) {
        queue.add(customer);
    }

    public boolean remove(Customer customer) {
        return queue.remove(customer);
    }

    public Customer findById(int customerId) {
        for (Customer customer : queue) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public List<Customer> asList() {
        return Collections.unmodifiableList(queue);
    }
}
