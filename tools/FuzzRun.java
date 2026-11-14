package tools;

import boundary.GameDisplay;
import boundary.ManagerInput;
import control.ArrivalSchedule;
import control.GameConfig;
import control.GameController;
import control.StaffCommand;
import entity.Cashier;
import entity.Chef;
import entity.ComboMeal;
import entity.GameResult;
import entity.Host;
import entity.Menu;
import entity.MenuItem;
import entity.Promotion;
import entity.Restaurant;
import entity.ScoreBoard;
import entity.SingleDish;
import entity.StaffMember;
import entity.Waiter;

import java.util.List;
import java.util.Random;

/**
 * Development tool, not part of the game.
 *
 * Plays many games, choosing at random among the legal tasks, to check that no legal
 * choice can break the game.
 */
public class FuzzRun {

    public static void main(String[] args) {
        int games = args.length > 0 ? Integer.parseInt(args[0]) : 3000;
        int victories = 0;
        int failures = 0;
        int maxRevenue = 0;
        int maxPaid = 0;

        for (int game = 0; game < games; game++) {
            long seed = game;
            Random chooser = new Random(game * 7919L + 13L);
            try {
                GameConfig config = GameConfig.baseSetting(seed);
                Menu menu = buildMenu();
                Restaurant restaurant = new Restaurant(config.getTableCount(), menu,
                        new Host(), new Waiter(), new Chef(), new Cashier());
                ScoreBoard scoreBoard = new ScoreBoard();
                ArrivalSchedule schedule = new ArrivalSchedule(config.getArrivalTurns(), seed);
                SilentUI ui = new SilentUI(chooser);
                GameResult result = new GameController(config, restaurant, scoreBoard, schedule,
                        ui, ui).play();
                if (result == GameResult.VICTORY) {
                    victories++;
                }
                maxRevenue = Math.max(maxRevenue, scoreBoard.getRevenueCents());
                maxPaid = Math.max(maxPaid, scoreBoard.getPaidCustomerCount());
                if (scoreBoard.getPaidCustomerCount() > 5) {
                    System.out.println("RULE BREACH: more than five customers paid, seed " + seed);
                    failures++;
                }
            } catch (RuntimeException broken) {
                failures++;
                System.out.println("EXCEPTION on seed " + seed + ": " + broken);
                broken.printStackTrace(System.out);
                if (failures > 3) {
                    break;
                }
            }
        }
        System.out.println("games=" + games + " victories=" + victories
                + " failures=" + failures + " mostPaid=" + maxPaid
                + " bestRevenueCents=" + maxRevenue);
    }

    private static Menu buildMenu() {
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
        return menu;
    }

    private static class SilentUI implements ManagerInput, GameDisplay {
        private final Random chooser;

        SilentUI(Random chooser) {
            this.chooser = chooser;
        }

        @Override
        public StaffCommand askCommand(StaffMember staff, List<StaffCommand> legal) {
            return legal.get(chooser.nextInt(legal.size()));
        }

        @Override
        public boolean askActivateHappyHour() {
            return chooser.nextInt(18) == 0;
        }

        @Override
        public void showGameIntro() {
        }

        @Override
        public void showTurnStart(int turn, Promotion promotion) {
        }

        @Override
        public void showInfo(String message) {
        }

        @Override
        public void showError(String message) {
            throw new IllegalStateException("A legal task was refused: " + message);
        }

        @Override
        public void showTurnEnd(int turn) {
        }

        @Override
        public void showFinalResult(GameResult result) {
        }
    }
}
