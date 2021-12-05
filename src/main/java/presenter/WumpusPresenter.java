package presenter;

import java.util.List;
import java.util.Map;

public interface WumpusPresenter {
    void startNewGame();
    Map<String, int[]> move(int cave);
    int getPlayerCaveIndex();
    boolean isGameOver();
    Map<String, int[]> shoot(int... cave);
    int getNumberOfArrows();
    List<String> getWarnings();
    int getWumpusCaveIndex();
    int getEnemyPlayerCave();

    boolean isWumpusDead();

    boolean hasPlayerWon();

    boolean isPlayerDead();

    boolean isGameLost();

    int[] getBatsCaves();

    int[] getPitsCaves();

    boolean isEnemyPlayerDead();

    int getEnemyRemainingArrows();

    int[] getBatCaves();
}
