package entity;

/**
 * Entity: a member of staff with a fixed number of tasks per turn.
 *
 * Every action calls consumeTask(), which refuses a task already used this turn.
 */
public abstract class StaffMember {

    private final String role;
    private final int tasksPerTurn;
    private int tasksUsedThisTurn;

    protected StaffMember(String role, int tasksPerTurn) {
        this.role = role;
        this.tasksPerTurn = tasksPerTurn;
        this.tasksUsedThisTurn = 0;
    }

    public void beginTurn() {
        tasksUsedThisTurn = 0;
    }

    public boolean hasCapacity() {
        return tasksUsedThisTurn < tasksPerTurn;
    }

    public int getRemainingTasks() {
        return tasksPerTurn - tasksUsedThisTurn;
    }

    protected void consumeTask() {
        if (!hasCapacity()) {
            throw new IllegalStateException("The " + role + " has no task left this turn");
        }
        tasksUsedThisTurn++;
    }

    public String getRole() {
        return role;
    }

    public int getTasksPerTurn() {
        return tasksPerTurn;
    }
}
