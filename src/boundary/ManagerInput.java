package boundary;

import control.StaffCommand;
import entity.StaffMember;

import java.util.List;

/**
 * Boundary interface: where the manager's decisions come from.
 *
 * ConsoleUI reads them from the keyboard and ScriptedInput replays them from a file,
 * so the same game can be played live or replayed exactly.
 */
public interface ManagerInput {

    StaffCommand askCommand(StaffMember staff, List<StaffCommand> legal);

    boolean askActivateHappyHour();
}
