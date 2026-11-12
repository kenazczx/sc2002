package control;

/**
 * Control: one decision for one member of staff, such as seating customer 4.
 *
 * Built only through the factory methods, so it always describes a real task.
 */
public class StaffCommand {

    private final StaffTask task;
    private final int targetId;

    private StaffCommand(StaffTask task, int targetId) {
        this.task = task;
        this.targetId = targetId;
    }

    public static StaffCommand waitTurn() {
        return new StaffCommand(StaffTask.WAIT, 0);
    }

    public static StaffCommand seat(int customerId) {
        return new StaffCommand(StaffTask.SEAT, customerId);
    }

    public static StaffCommand takeOrder(int customerId) {
        return new StaffCommand(StaffTask.TAKE_ORDER, customerId);
    }

    public static StaffCommand serve(int orderId) {
        return new StaffCommand(StaffTask.SERVE, orderId);
    }

    public static StaffCommand prepare(int orderId) {
        return new StaffCommand(StaffTask.PREPARE, orderId);
    }

    public StaffTask getTask() {
        return task;
    }

    public int getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        switch (task) {
            case WAIT:
                return "wait";
            case SEAT:
                return "seat " + targetId;
            case TAKE_ORDER:
                return "order " + targetId;
            case SERVE:
                return "serve " + targetId;
            case PREPARE:
                return "prep " + targetId;
            default:
                return task.toString().toLowerCase();
        }
    }
}
