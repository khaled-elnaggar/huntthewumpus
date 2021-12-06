package acceptance;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import model.game.GameInitialConfigurations;
import acceptance.support.GameWorld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WumpusSteps {

    private final GameWorld gameWorld;

    public WumpusSteps(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @And("wumpus starts in cave {int}")
    public void wumpusStartsInCave(int wumpusStartingCave) {
        gameWorld.getRNGBuilder().setWumpusStartingCave(wumpusStartingCave);
    }

    @And("wumpus remains asleep")
    public void wumpusRemainsAsleep() {
        gameWorld.getRNGBuilder().makeWumpusSleep();
    }

    @And("wumpus wakes up and moves from cave {int} to cave {int}")
    public void wumpusWakesUpAndMovesFromCaveToCave(int currentWumpusCave, int cave) throws Exception {
        int caveIndex = GameInitialConfigurations.getCaveIndexOutOfCave(currentWumpusCave, cave);
        gameWorld.getRNGBuilder().makeWumpusMoveTo(caveIndex);
    }

    @Then("wumpus will be at cave {int}")
    public void wumpusWillBeAtCave(int expectedCaveIndex) {
        final int actualWumpusCave = gameWorld.executeActionsAndGetWumpusPresenter().getWumpusCaveIndex();
        assertEquals(expectedCaveIndex, actualWumpusCave);
    }

    @Then("wumpus will be dead")
    public void wumpusWillBeDead() {
        assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().isWumpusDead());
    }
}
