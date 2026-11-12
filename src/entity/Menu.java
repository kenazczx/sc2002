package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity: the dishes a customer can choose from.
 */
public class Menu {

    private final List<MenuItem> items = new ArrayList<MenuItem>();

    public void add(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getOrderableItems() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }
}
