package boundary;

import control.StaffCommand;

/**
 * Boundary helper: turns a typed line into a StaffCommand.
 *
 * Returns null when the line cannot be read. Whether the command is allowed is
 * decided by GameController, not here.
 *
 * Usage:
 *   StaffCommand command = CommandParser.parse("seat 4");
 */
public final class CommandParser {

    public static final String SYNTAX =
            "wait | seat <customerId> | order <customerId> | serve <orderId> | prep <orderId>";

    private CommandParser() {
    }

    public static StaffCommand parse(String line) {
        if (line == null) {
            return null;
        }
        String[] parts = line.trim().toLowerCase().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return null;
        }
        String verb = parts[0];
        if (verb.equals("wait") || verb.equals("w")) {
            if (parts.length != 1) {
                return null;
            }
            return StaffCommand.waitTurn();
        }
        if (parts.length != 2) {
            return null;
        }
        int target;
        try {
            target = Integer.parseInt(parts[1]);
        } catch (NumberFormatException notANumber) {
            return null;
        }
        if (target <= 0) {
            return null;
        }
        switch (verb) {
            case "seat":
            case "s":
                return StaffCommand.seat(target);
            case "order":
            case "o":
                return StaffCommand.takeOrder(target);
            case "serve":
            case "v":
                return StaffCommand.serve(target);
            case "prep":
            case "p":
                return StaffCommand.prepare(target);
            default:
                return null;
        }
    }
}
