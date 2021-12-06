package acceptance;

import io.cucumber.java.en.And;
import acceptance.support.GameWorld;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatSteps {
    private final GameWorld gameWorld;

    public BatSteps(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @And("bat {int} starts in cave {int}")
    public void aBatIsInCave(int batNumber, int cave) {
        gameWorld.getRNGBuilder().setBatStartingCave(batNumber, cave);
    }

    @And("bat teleports (enemy )player to cave {int} and itself to cave {int}")
    public void batTeleportsPlayerToCaveAndItselfToCave(int playerCave, int batCave) {
        gameWorld.getRNGBuilder().addCaveToTeleportTo(playerCave);
        gameWorld.getRNGBuilder().addCaveToTeleportTo(batCave);
    }

    @And("bat {int} will be at cave {int}")
    public void batWillBeAtCave(int batNumber, int expectedCave) {
        assertTrue("Wrong bat index " + batNumber + " .indexes start from 1", batNumber > 0);
        final int batCave = gameWorld.executeActionsAndGetWumpusPresenter().getBatsCaves()[batNumber - 1];
        assertEquals(expectedCave, batCave);
    }
}
