package entity;

/**
 * Entity: the Chef.
 *
 * Advances one order by one preparation unit per turn.
 */
public class Chef extends StaffMember {

    public static final int PREP_UNITS_PER_TURN = 1;

    public Chef() {
        super("Chef", PREP_UNITS_PER_TURN);
    }

    public void prepare(Order order, int turn) {
        order.advancePreparation(turn);
        consumeTask();
    }
}
