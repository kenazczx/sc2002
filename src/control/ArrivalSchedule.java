package control;

import entity.Appetite;
import entity.CriticCustomer;
import entity.Customer;
import entity.CustomerType;
import entity.RegularCustomer;
import entity.VIPCustomer;

import java.util.Random;

/**
 * Control: who arrives, and on which turn.
 *
 * The only class that creates customers, and the only one that names a customer type:
 * the line-up says whether each arrival is a RegularCustomer, VIPCustomer or
 * CriticCustomer. Each customer's taste and appetite come from a Random seeded with
 * the game seed, so the same seed always gives the same customers.
 */
public class ArrivalSchedule {

    private static final CustomerType[] BASE_LINE_UP = {
        CustomerType.REGULAR, CustomerType.CRITIC, CustomerType.REGULAR,
        CustomerType.VIP, CustomerType.REGULAR, CustomerType.REGULAR
    };

    public static final int LARGE_APPETITE_IN = 3;

    private final int[] arrivalTurns;
    private final CustomerType[] lineUp;
    private final Random random;
    private int nextCustomerId = 1;

    public ArrivalSchedule(int[] arrivalTurns, long seed) {
        this(arrivalTurns, BASE_LINE_UP, seed);
    }

    public ArrivalSchedule(int[] arrivalTurns, CustomerType[] lineUp, long seed) {
        this.arrivalTurns = arrivalTurns.clone();
        this.lineUp = lineUp.clone();
        this.random = new Random(seed);
    }

    public Customer arrivalFor(int turn) {
        int index = indexOf(turn);
        if (index < 0) {
            return null;
        }
        int id = nextCustomerId++;
        int tasteIndex = random.nextInt(1000);
        Appetite appetite = random.nextInt(LARGE_APPETITE_IN) == 0
                ? Appetite.LARGE : Appetite.SMALL;
        return create(lineUp[index], id, "C" + id, turn, tasteIndex, appetite);
    }

    private Customer create(CustomerType type, int id, String name, int turn, int tasteIndex,
                            Appetite appetite) {
        switch (type) {
            case VIP:
                return new VIPCustomer(id, name, turn, tasteIndex, appetite);
            case CRITIC:
                return new CriticCustomer(id, name, turn, tasteIndex, appetite);
            case REGULAR:
            default:
                return new RegularCustomer(id, name, turn, tasteIndex, appetite);
        }
    }

    private int indexOf(int turn) {
        for (int i = 0; i < arrivalTurns.length; i++) {
            if (arrivalTurns[i] == turn) {
                return i;
            }
        }
        return -1;
    }

    public int getTotalArrivals() {
        return arrivalTurns.length;
    }
}
