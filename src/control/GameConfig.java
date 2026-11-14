package control;

import entity.GameObjective;

/**
 * Control: the setting a game is played under.
 *
 * The number of turns and tables, the arrival turns, the targets and the seed.
 */
public class GameConfig {

    // seed 42 reproduces both worked examples in Section 4.3 of the brief
    public static final long DEFAULT_SEED = 42L;

    private final int maxTurns;
    private final int tableCount;
    private final int[] arrivalTurns;
    private final GameObjective objective;
    private final long seed;

    private GameConfig(int maxTurns, int tableCount, int[] arrivalTurns,
                       GameObjective objective, long seed) {
        this.maxTurns = maxTurns;
        this.tableCount = tableCount;
        this.arrivalTurns = arrivalTurns.clone();
        this.objective = objective;
        this.seed = seed;
    }

    public static GameConfig baseSetting(long seed) {
        return new GameConfig(18, 2, new int[] {1, 4, 7, 10, 13, 16},
                new GameObjective(4, 4500, 60.0), seed);
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getTableCount() {
        return tableCount;
    }

    public int[] getArrivalTurns() {
        return arrivalTurns.clone();
    }

    public GameObjective getObjective() {
        return objective;
    }

    public long getSeed() {
        return seed;
    }
}
