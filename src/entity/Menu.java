package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity: the dishes a customer can choose from.
 *
 * mostDemandingItem() is what a Critic orders: the dish with the most preparation
 * units, the dearer one on a tie.
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

    public MenuItem mostDemandingItem() {
        MenuItem best = null;
        for (MenuItem item : items) {
            if (best == null
                    || item.getPrepUnits() > best.getPrepUnits()
                    || (item.getPrepUnits() == best.getPrepUnits()
                        && item.getPriceCents() > best.getPriceCents())) {
                best = item;
            }
        }
        return best;
    }
}
