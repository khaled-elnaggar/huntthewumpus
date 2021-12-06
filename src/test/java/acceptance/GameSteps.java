package acceptance;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import model.game.GameInitialConfigurations;
import org.junit.jupiter.api.Assertions;
import presenter.WumpusPresenter;
import acceptance.support.Action;
import acceptance.support.GameWorld;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameSteps {
    private final GameWorld gameWorld;

    public GameSteps(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @After
    public void afterScenario(Scenario scenario) throws Exception {
        boolean scenarioPassed = !scenario.isFailed();
        if (scenarioPassed && gameWorld.hasPendingActions()) {
            throwSanityError(scenario);
        }
    }

    private void throwSanityError(Scenario scenario) throws Exception {
        String causeOfFailure = getCauseOfFailure(gameWorld.getActionsToExecute().isEmpty());
        String remainingActions = getRemainingActions(gameWorld.getActionsToExecute().isEmpty());
        String errorMessage =
                "\n\nScenario \"" + scenario.getName() + "\"" + " passed " + causeOfFailure
                        + remainingActions
                        + "\n\tfind it at @" + scenario.getUri() + ":" + scenario.getLine()
                        + "\n";

        throw new Exception(errorMessage);
    }

    private String getRemainingActions(boolean empty) {
        if (empty) return "";
        return "\n\tThe remaining actions are:"
                + gameWorld.getActionsToExecute().stream()
                .map(Action::toString)
                .collect(Collectors.joining("\n\t\t -", "\n\t\t -", ""));
    }

    private String getCauseOfFailure(boolean empty) {
        if (empty) return "while you did not start any action! You setup but did not take any move";
        return "while some actions remain queued and did not execute!";
    }


    @And("pit {int} starts in cave {int}")
    public void pitStartsInCave(int pitNumber, int cave) {
        gameWorld.getRNGBuilder().setPitStartingCave(pitNumber, cave);
    }

    @And("game is still on")
    public void gameIsStillOn() {
        WumpusPresenter wumpusPresenter = gameWorld.executeActionsAndGetWumpusPresenter();
        final boolean expectedStatusOfGameIsOver = false;
        final boolean isGameOver = wumpusPresenter.isGameOver();
        assertEquals(isGameOver, expectedStatusOfGameIsOver);
    }

    @And("game is won")
    public void gameIsWon() {
        assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().isGameWon());
    }

    @And("game is lost")
    public void gameIsLost() {
        assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().isGameLost());
    }

    @But("cave {int} is not linked to {int}, so arrow goes to {int} instead")
    public void caveIsNotLinkedToSoArrowGoesToInstead(int wrongCave, int currentCave, int nextRandomCave) throws Exception {
        int caveIndex = GameInitialConfigurations.getCaveIndexOutOfCave(currentCave, nextRandomCave);
        gameWorld.getRNGBuilder().setNextRandomCaveForArrow(caveIndex);
    }

    @Given("game starts")
    public void gameStarts() {
        gameWorld.executeActions();
    }

    @And("pit {int} will be at cave {int}")
    public void pitWillBeAtCave(int pitNumber, int expectedCave) {
        assertTrue("Wrong pit index " + pitNumber + " .indexes start from 1", pitNumber > 0);
        final int pitsCave = gameWorld.executeActionsAndGetWumpusPresenter().getPitsCaves()[pitNumber - 1];
        Assertions.assertEquals(expectedCave, pitsCave);
    }

    @Then("there is/are {int} {string}")
    public void thereIs(int expectedNumber, String objectName) {
        if (objectName.endsWith("s")) {
            objectName = objectName.substring(0, objectName.length() - 1);
        }

        switch (objectName) {
            case "player":
                assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().getPlayerCaveIndex() >= 0);
                break;
            case "enemy player":
                assertTrue(gameWorld.executeActionsAndGetWumpusPresenter().getEnemyPlayerCave() >= 0);
                break;
            case "bat":
                assertEquals("Please update the game configuration NUMBER_OF_BATS first!", GameInitialConfigurations.NUMBER_OF_BATS, expectedNumber);
                final int actualBatsCount = gameWorld.executeActionsAndGetWumpusPresenter().getBatsCaves().length;
                assertEquals(expectedNumber, actualBatsCount);
                break;
            case "pit":
                assertEquals("Please update the game configuration NUMBER_OF_PITS first!", GameInitialConfigurations.NUMBER_OF_PITS, expectedNumber);
                final int actualPitsCount = gameWorld.executeActionsAndGetWumpusPresenter().getPitsCaves().length;
                assertEquals(expectedNumber, actualPitsCount);
                break;
            default:
                throw new Error("Unknown new game object type! " + objectName);
        }
    }
}
