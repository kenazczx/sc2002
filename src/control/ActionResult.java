package control;

/**
 * Control: the result of checking a decision.
 *
 * Either allowed, or refused with a reason that the screen shows before asking again.
 */
public class ActionResult {

    private final boolean success;
    private final String message;

    private ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ActionResult ok() {
        return new ActionResult(true, "");
    }

    public static ActionResult failure(String reason) {
        return new ActionResult(false, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
