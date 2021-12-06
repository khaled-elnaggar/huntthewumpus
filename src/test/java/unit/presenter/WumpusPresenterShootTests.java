package unit.presenter;

import model.game.GameInitialConfigurations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import presenter.WumpusPresenter;
import unit.support.MockedRandomNumberGeneratorWorld;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WumpusPresenterShootTests {
    MockedRandomNumberGeneratorWorld mockedRNGWorld;

    @BeforeEach
    public void setUp() {
        mockedRNGWorld = new MockedRandomNumberGeneratorWorld();
    }

    @Test
    public void testKillingTheWumpus() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        WumpusPresenter wumpusPresenter = mockedRNGWorld.getWumpusPresenter();

        final int[] journeyPath = {1, 9, 10};
        for (int caveNumber : journeyPath) {
            wumpusPresenter.move(caveNumber);
        }

        final int shootToCave = 18;
        wumpusPresenter.shoot(shootToCave);

        final boolean actualGameState = wumpusPresenter.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerShootsAnArrowThatMissesTheWumpusAndWumpusRemainsSleeping() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        final int maximumNumberForCalculatingWumpusWakeupProbability = 4;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(maximumNumberForCalculatingWumpusWakeupProbability)).thenReturn(
                GameInitialConfigurations.WUMPUS_SLEEP_NUMBER);

        WumpusPresenter wumpusPresenter = mockedRNGWorld.getWumpusPresenter();

        final int[] journeyPath = {1, 9, 10};
        for (int caveNumber : journeyPath) {
            wumpusPresenter.move(caveNumber);
        }

        final int shootToCave = 11;
        wumpusPresenter.shoot(shootToCave);

        final int wumpusCaveLocation = wumpusPresenter.getWumpusCaveIndex();
        assertEquals(wumpusCaveLocation, MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX);

        final boolean actualGameState = wumpusPresenter.isGameOver();
        final boolean gameIsOver = false;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerShootsAnArrowThatMissesTheWumpusAndWumpusMoves() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        final int maximumNumberForCalculatingWumpusWakeupProbability = 4;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(maximumNumberForCalculatingWumpusWakeupProbability)).thenReturn(
                GameInitialConfigurations.WUMPUS_WAKEUP_NUMBER);

        final int numberOfLinkedCaves = 3;
        final int wumpusLinkedCaveIndex = 2;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(numberOfLinkedCaves)).thenReturn(
                wumpusLinkedCaveIndex);

        WumpusPresenter wumpusPresenter = mockedRNGWorld.getWumpusPresenter();

        final int[] journeyPath = {1, 9, 10};
        for (int caveNumber : journeyPath) {
            wumpusPresenter.move(caveNumber);
        }

        final int shootToCave = 11;
        wumpusPresenter.shoot(shootToCave);

        final int wumpusCaveLocation = wumpusPresenter.getWumpusCaveIndex();
        final int wumpusCaveCurrLocation = 17;
        assertEquals(wumpusCaveCurrLocation, wumpusCaveLocation);

        final boolean actualGameState = wumpusPresenter.isGameOver();
        final boolean gameIsOver = false;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerRunsOutOfArrowsWithoutKillingWumpus() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();

        final int maximumNumberForCalculatingWumpusWakeupProbability = 4;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(maximumNumberForCalculatingWumpusWakeupProbability)).thenReturn(
                GameInitialConfigurations.WUMPUS_SLEEP_NUMBER);
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        WumpusPresenter wumpusPresenter = mockedRNGWorld.getWumpusPresenter();

        final int caveToShoot = 7;
        for (int i = 0; i < GameInitialConfigurations.NUMBER_OF_ARROWS; i++) {
            wumpusPresenter.shoot(caveToShoot);
        }

        final String runningOutOfArrowsMessage = "You ran out of arrows";
        final List<String> actualGameMessages = wumpusPresenter.getWarnings();
        assertTrue(actualGameMessages.contains(runningOutOfArrowsMessage));

        final boolean expectedGameStateGameIsOver = true;
        final boolean actualGameState = wumpusPresenter.isGameOver();
        assertEquals(expectedGameStateGameIsOver, actualGameState);
    }

    @Test
    public void testThatPlayerShootsAnArrowMissesWumpusAndWumpusWakesUpAndMoveToEatThePlayer() {
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();

        final int maximumNumberForCalculatingWumpusWakeupProbability = 4;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(maximumNumberForCalculatingWumpusWakeupProbability)).thenReturn(
                GameInitialConfigurations.WUMPUS_WAKEUP_NUMBER);

        final int numberOfLinkedCaves = 3;
        final int wumpusLinkedCaveIndex = 1;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(numberOfLinkedCaves)).thenReturn(
                wumpusLinkedCaveIndex);

        WumpusPresenter wumpusPresenter = mockedRNGWorld.getWumpusPresenter();

        final int[] journeyPath = {1, 9, 10};
        for (int caveNumber : journeyPath) {
            wumpusPresenter.move(caveNumber);
        }

        final int shootToCave = 11;
        wumpusPresenter.shoot(shootToCave);

        final int wumpusCaveLocation = wumpusPresenter.getWumpusCaveIndex();
        final int wumpusCaveCurrLocation = wumpusPresenter.getPlayerCaveIndex();
        assertEquals(wumpusCaveCurrLocation, wumpusCaveLocation);

        final String wokeTheWumpusMessage = "Wumpus woke up & moved";
        final List<String> actualGameMessages = wumpusPresenter.getWarnings();
        assertTrue(actualGameMessages.containsAll(Arrays.asList(wokeTheWumpusMessage, "Wumpus ate you")));

        final boolean expectedGameStateGameIsOver = true;
        final boolean actualGameState = wumpusPresenter.isGameOver();
        assertEquals(expectedGameStateGameIsOver, actualGameState);
    }
}
