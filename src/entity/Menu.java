package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity: everything a customer can order.
 *
 * itemsFor(appetite) gives a large appetite the combos and a small one the single
 * dishes, or the whole menu when it has none of that kind. mostDemandingItem() is
 * what a Critic orders: the item with the most preparation units, the dearer one on a
 * tie.
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

    public List<MenuItem> itemsFor(Appetite appetite) {
        List<MenuItem> matching = new ArrayList<MenuItem>();
        for (MenuItem item : items) {
            boolean wantsMore = item.getPortions() > 1;
            if (wantsMore == (appetite == Appetite.LARGE)) {
                matching.add(item);
            }
        }
        if (matching.isEmpty()) {
            return getOrderableItems();
        }
        return Collections.unmodifiableList(matching);
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
