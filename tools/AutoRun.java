package tools;

import boundary.ConsoleUI;
import control.ArrivalSchedule;
import control.GameConfig;
import control.GameController;
import entity.Cashier;
import entity.Chef;
import entity.GameResult;
import entity.ComboMeal;
import entity.Host;
import entity.Menu;
import entity.MenuItem;
import entity.SingleDish;
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

        MenuItem salad = new SingleDish("Salad", 900, 1);
        MenuItem burger = new SingleDish("Burger", 1500, 1);
        MenuItem pasta = new SingleDish("Pasta", 1800, 2);
        Menu menu = new Menu();
        menu.add(salad);
        menu.add(burger);
        menu.add(pasta);
        menu.add(new ComboMeal(salad, burger));
        menu.add(new ComboMeal(salad, pasta));
        menu.add(new ComboMeal(burger, pasta));

        Restaurant restaurant = new Restaurant(config.getTableCount(), menu,
                new Host(), new Waiter(), new Chef(), new Cashier());
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
