package boundary;

import entity.GameResult;

/**
 * Boundary interface: everything the game shows the manager.
 *
 * GameController calls these methods instead of printing, so it does not depend on
 * the console.
 */
public interface GameDisplay {

    void showGameIntro();

    void showTurnStart(int turn);

    void showInfo(String message);

    void showError(String message);

    void showTurnEnd(int turn);

    void showFinalResult(GameResult result);
}
