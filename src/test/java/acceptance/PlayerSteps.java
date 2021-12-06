package acceptance;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.game.GameInitialConfigurations;
import acceptance.support.GameWorld;
import acceptance.support.MoveAction;
import acceptance.support.ShootAction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerSteps {
    private final GameWorld gameWorld;

    public PlayerSteps(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Given("player starts in cave {int}")
    public void playerStartsInCave(int playerStartingCave) {
        gameWorld.getRNGBuilder().setPlayerStartingCave(playerStartingCave);
    }

    @When("player moves to cave {int}")
    public void playerMovesToCave(Integer caveToMoveTo) {
        gameWorld.queueAction(new MoveAction(caveToMoveTo));
    }

    @When("player moves to caves")
    public void playerMovesToCaves(@Transpose List<Integer> cavesToMoveTo) {
        cavesToMoveTo.forEach(this::playerMovesToCave);
    }

    @And("player shoots at cave {int}")
    public void playerShootsAtCave(int cave) {
        gameWorld.queueAction(new ShootAction(cave));
    }

    @When("player shoots an arrow at caves")
    public void playerShootsAnArrowAtCaves(@Transpose List<Integer> cavesToShoot) {
        int[] caves = cavesToShoot.stream().mapToInt(i -> i).toArray();
        gameWorld.queueAction(new ShootAction(caves));
    }

    @Then("player will sense that {string}")
    public void playerWillSenseThat(String warning) {
        List<String> warnings = gameWorld.executeActionsAndGetWumpusPresenter().getWarnings();
        assertTrue(warnings.contains(warning));
    }

    @And("player has {int} arrow(s) remaining")
    public void playerHasArrowRemaining(int remainingAroows) throws Exception {
        int playerCurrentArrows = gameWorld.executeActionsAndGetWumpusPresenter().getNumberOfArrows();
        if (remainingAroows > playerCurrentArrows) {
            throw new Exception("Logical error, remaining arrows " + remainingAroows + " can not be greater than player's current arrows " + playerCurrentArrows);
        }

        final int playerCave = gameWorld.executeActionsAndGetWumpusPresenter().getPlayerCaveIndex();
        final int middleCave = GameInitialConfigurations.CAVE_LINKS[playerCave][1];
        for (int i = 0; i < playerCurrentArrows - remainingAroows; i++) {
            gameWorld.queueAction(new ShootAction(middleCave));
        }
    }

    @Then("player will be at cave {int}")
    public void playerWillBeAtCave(int expectedPlayerCave) {
        final int actualPlayerCave = gameWorld.executeActionsAndGetWumpusPresenter().getPlayerCaveIndex();
        assertEquals(expectedPlayerCave, actualPlayerCave);
    }

    @Then("player will be dead")
    public void playerWillBeDead() {
        assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().isPlayerDead());
    }
}
