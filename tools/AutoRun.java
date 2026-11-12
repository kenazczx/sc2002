package tools;

import boundary.ConsoleUI;
import control.ArrivalSchedule;
import control.GameConfig;
import control.GameController;
import entity.Cashier;
import entity.Chef;
import entity.GameResult;
import entity.Menu;
import entity.MenuItem;
import entity.Restaurant;
import entity.ScoreBoard;
import entity.Waiter;

/**
 * Development tool, not part of the game.
 *
 * Runs AutoManager and prints the script it played.
 */
public class AutoRun {

    public static void main(String[] args) {
        long seed = args.length > 0 ? Long.parseLong(args[0]) : GameConfig.DEFAULT_SEED;
        GameConfig config = GameConfig.baseSetting(seed);

        Menu menu = new Menu();
        menu.add(new MenuItem("Salad", 900, 1));
        menu.add(new MenuItem("Burger", 1500, 1));
        menu.add(new MenuItem("Pasta", 1800, 2));

        Restaurant restaurant = new Restaurant(config.getTableCount(), menu,
                new Waiter(), new Chef(), new Cashier());
        ScoreBoard scoreBoard = new ScoreBoard();
        ArrivalSchedule schedule = new ArrivalSchedule(config.getArrivalTurns(), seed);

        ConsoleUI ui = new ConsoleUI(config, restaurant, scoreBoard);
        AutoManager auto = new AutoManager(restaurant, ui, config.getMaxTurns());

        GameResult result = new GameController(config, restaurant, scoreBoard, schedule,
                auto, auto).play();

        System.out.println();
        System.out.println("### RESULT " + result + " seed " + seed);
        System.out.println("### RECORDED SCRIPT");
        for (String line : auto.getRecorded()) {
            System.out.println(line);
        }
    }
}
