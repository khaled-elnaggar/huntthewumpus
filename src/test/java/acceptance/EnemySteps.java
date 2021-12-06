package acceptance;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import model.game.GameInitialConfigurations;
import acceptance.support.GameWorld;
import acceptance.support.MoveAction;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnemySteps {
    private final GameWorld gameWorld;

    public EnemySteps(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @And("enemy player starts in cave {int}")
    public void enemyPlayerStartsInCave(int cave) {
        gameWorld.getRNGBuilder().setEnemyPlayerStartingCave(cave);
    }

    @And("enemy player moves from cave {int} to cave {int}")
    public void enemyPlayerMovesFromCaveToCave(int currentCave, int caveToMoveTo) throws Exception {
        int caveIndex = GameInitialConfigurations.getCaveIndexOutOfCave(currentCave, caveToMoveTo);
        gameWorld.getRNGBuilder().makeEnemyPlayerMoveToCave(caveIndex);
    }

    @And("from cave {int}, enemy player shoots caves")
    public void fromCaveEnemyPlayerShootsCaves(int startingCave, @Transpose List<Integer> caves) throws Exception {
        List<Integer> caveIndexes = GameInitialConfigurations.getCaveIndexesOutOfCaveNumbers(startingCave, caves);
        gameWorld.getRNGBuilder().makeEnemyPlayerShootAtCaves(caveIndexes);
    }

    @And("enemy player uses all arrows")
    public void enemyPlayerUsesAllArrows() {
        int enemyRemainingArrows = gameWorld.executeActionsAndGetWumpusPresenter().getEnemyRemainingArrows();
        for (int i = 0; i < enemyRemainingArrows; i++) {
            gameWorld.getRNGBuilder().makeEnemyPlayerShootAtCaves(Collections.singletonList(1));

            final int playerCave = gameWorld.executeActionsAndGetWumpusPresenter().getPlayerCaveIndex();
            final int playerMiddleCave = GameInitialConfigurations.CAVE_LINKS[playerCave][1];
            gameWorld.queueAction(new MoveAction(playerMiddleCave));
        }
    }

    @Then("enemy player will be at cave {int}")
    public void enemyPlayerWillBeAtCave(int expectedCave) {
        final int actualWumpusCave = gameWorld.executeActionsAndGetWumpusPresenter().getEnemyPlayerCave();
        assertEquals(expectedCave, actualWumpusCave);
    }

    @And("enemy player will be dead")
    public void enemyPlayerWillBeDead() {
        assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().isEnemyPlayerDead());
    }

    @And("enemy player will have {int} arrows")
    public void enemyPlayerWillHaveArrows(int remainingArrows) {
        int enemyRemainingArrows = gameWorld.executeActionsAndGetWumpusPresenter().getEnemyRemainingArrows();
        assertEquals(remainingArrows, enemyRemainingArrows);
    }
}
