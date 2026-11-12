package control;

import entity.Customer;
import entity.RegularCustomer;

import java.util.Random;

/**
 * Control: who arrives, and on which turn.
 *
 * The only class that creates customers. Each customer's taste comes from a Random
 * seeded with the game seed, so the same seed always gives the same customers.
 */
public class ArrivalSchedule {

    private final int[] arrivalTurns;
    private final Random random;
    private int nextCustomerId = 1;

    public ArrivalSchedule(int[] arrivalTurns, long seed) {
        this.arrivalTurns = arrivalTurns.clone();
        this.random = new Random(seed);
    }

    public Customer arrivalFor(int turn) {
        if (!arrivesOn(turn)) {
            return null;
        }
        int id = nextCustomerId++;
        int tasteIndex = random.nextInt(1000);
        return new RegularCustomer(id, "C" + id, turn, tasteIndex);
    }

    private boolean arrivesOn(int turn) {
        for (int arrivalTurn : arrivalTurns) {
            if (arrivalTurn == turn) {
                return true;
            }
        }
        return false;
    }

    public int getTotalArrivals() {
        return arrivalTurns.length;
    }
}
