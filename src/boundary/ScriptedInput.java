package boundary;

import control.StaffCommand;
import entity.StaffMember;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Boundary: replays a recorded script of commands, so a run can be repeated exactly.
 *
 * One command per line, and anything after a # is ignored. When the script runs out,
 * every remaining decision is to wait.
 *
 * Usage:
 *   ManagerInput input = ScriptedInput.fromFile("runs/victory.txt");
 */
public class ScriptedInput implements ManagerInput {

    private final List<String> commands;
    private int cursor;
    private boolean exhaustedReported;

    public ScriptedInput(List<String> commands) {
        this.commands = new ArrayList<String>(commands);
        this.cursor = 0;
    }

    public static ScriptedInput fromFile(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        List<String> cleaned = new ArrayList<String>();
        for (String raw : lines) {
            String line = raw;
            int comment = line.indexOf('#');
            if (comment >= 0) {
                line = line.substring(0, comment);
            }
            line = line.trim();
            if (!line.isEmpty()) {
                cleaned.add(line);
            }
        }
        return new ScriptedInput(cleaned);
    }

    @Override
    public StaffCommand askCommand(StaffMember staff, List<StaffCommand> legal) {
        if (cursor >= commands.size()) {
            if (!exhaustedReported) {
                System.out.println("   . The script has ended; every remaining staff task"
                        + " is to wait.");
                exhaustedReported = true;
            }
            System.out.println(" " + staff.getRole() + " > wait   (scripted)");
            return StaffCommand.waitTurn();
        }
        String line = commands.get(cursor++);
        System.out.println(" " + staff.getRole() + " > " + line + "   (scripted)");
        return CommandParser.parse(line);
    }
}
