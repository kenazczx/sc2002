package app;

import boundary.ConsoleUI;
import boundary.ManagerInput;
import boundary.ScriptedInput;
import control.ArrivalSchedule;
import control.GameConfig;
import control.GameController;
import entity.Cashier;
import entity.Chef;
import entity.Menu;
import entity.MenuItem;
import entity.Restaurant;
import entity.ScoreBoard;
import entity.Waiter;

import java.io.IOException;

/**
 * Entry point: builds the game objects and starts the game.
 *
 * Creates the menu, the restaurant and its staff, and the screen or the script, then
 * calls GameController.play().
 *
 * Usage:
 *   java -cp out app.RestaurantRush
 *   java -cp out app.RestaurantRush --script runs/victory.txt
 *   java -cp out app.RestaurantRush --seed 7
 */
public class RestaurantRush {

    public static void main(String[] args) {
        long seed = GameConfig.DEFAULT_SEED;
        String scriptPath = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--seed") && i + 1 < args.length) {
                try {
                    seed = Long.parseLong(args[++i]);
                } catch (NumberFormatException notANumber) {
                    System.out.println("Ignoring --seed: \"" + args[i] + "\" is not a number.");
                }
            } else if (args[i].equals("--script") && i + 1 < args.length) {
                scriptPath = args[++i];
            } else {
                System.out.println("Usage: java app.RestaurantRush [--seed <number>]"
                        + " [--script <file>]");
                return;
            }
        }

        GameConfig config = GameConfig.baseSetting(seed);

        Menu menu = buildMenu();
        Restaurant restaurant = new Restaurant(config.getTableCount(), menu,
                new Waiter(), new Chef(), new Cashier());
        ScoreBoard scoreBoard = new ScoreBoard();
        ArrivalSchedule schedule = new ArrivalSchedule(config.getArrivalTurns(), seed);

        ConsoleUI ui = new ConsoleUI(config, restaurant, scoreBoard);
        ManagerInput input = ui;
        if (scriptPath != null) {
            try {
                input = ScriptedInput.fromFile(scriptPath);
            } catch (IOException cannotRead) {
                System.out.println("Cannot read script \"" + scriptPath + "\": "
                        + cannotRead.getMessage());
                return;
            }
        }

        new GameController(config, restaurant, scoreBoard, schedule, ui, input).play();
    }

    private static Menu buildMenu() {
        Menu menu = new Menu();
        menu.add(new MenuItem("Salad", 900, 1));
        menu.add(new MenuItem("Burger", 1500, 1));
        menu.add(new MenuItem("Pasta", 1800, 2));
        return menu;
    }
}
