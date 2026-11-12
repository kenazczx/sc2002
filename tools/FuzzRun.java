package tools;

import boundary.GameDisplay;
import boundary.ManagerInput;
import control.ArrivalSchedule;
import control.GameConfig;
import control.GameController;
import control.StaffCommand;
import entity.Cashier;
import entity.Chef;
import entity.GameResult;
import entity.Menu;
import entity.MenuItem;
import entity.Restaurant;
import entity.ScoreBoard;
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
        int maxPaid = 0;

        for (int game = 0; game < games; game++) {
            long seed = game;
            Random chooser = new Random(game * 7919L + 13L);
            try {
                GameConfig config = GameConfig.baseSetting(seed);
                Menu menu = new Menu();
                menu.add(new MenuItem("Salad", 900, 1));
                menu.add(new MenuItem("Burger", 1500, 1));
                menu.add(new MenuItem("Pasta", 1800, 2));
                Restaurant restaurant = new Restaurant(config.getTableCount(), menu,
                        new Waiter(), new Chef(), new Cashier());
                ScoreBoard scoreBoard = new ScoreBoard();
                ArrivalSchedule schedule = new ArrivalSchedule(config.getArrivalTurns(), seed);
                SilentUI ui = new SilentUI(chooser);
                GameResult result = new GameController(config, restaurant, scoreBoard, schedule,
                        ui, ui).play();
                if (result == GameResult.VICTORY) {
                    victories++;
                }
                maxPaid = Math.max(maxPaid, scoreBoard.getPaidCustomerCount());
                if (scoreBoard.getPaidCustomerCount() > 5) {
                    System.out.println("RULE BREACH: more than five paid, seed " + seed);
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
                + " failures=" + failures + " mostPaid=" + maxPaid);
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
        public void showGameIntro() {
        }

        @Override
        public void showTurnStart(int turn) {
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
