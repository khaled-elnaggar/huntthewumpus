package unit.model;

import model.game.GameInitialConfigurations;
import model.game.NewGame;
import model.gamemap.Cave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import unit.support.MockedRandomNumberGeneratorWorld;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerShootTests {
    MockedRandomNumberGeneratorWorld mockedRNGWorld;

    @BeforeEach
    public void setUp(){
        mockedRNGWorld = new MockedRandomNumberGeneratorWorld();
    }

    @Test
    public void testKillingTheWumpus() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        game.playerMovesToCave(10);

        final int caveToShootTo = MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX;
        game.playerShootsToCave(caveToShootTo);

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerShootsAnArrowThatMissesTheWumpusAndWumpusRemainsSleeping() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        mockedRNGWorld.makeWumpusSleep();

        NewGame game = mockedRNGWorld.getGame();
        game.playerMovesToCave(10);

        final int caveToShootTo = 11;
        game.playerShootsToCave(caveToShootTo);

        final int wumpusCaveLocation = game.getWumpusCaveIndex();
        assertEquals(wumpusCaveLocation, MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX);

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsNotOver = false;
        assertEquals(actualGameState, gameIsNotOver);
    }

    @Test
    public void testThatPlayerShootsAnArrowThatMissesTheWumpusAndWumpusMoves() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();
        mockedRNGWorld.makeWumpusWakeUp();

        final int numberOfLinkedCaves = 3;
        final int wumpusLinkedCaveIndex = 2;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(numberOfLinkedCaves)).thenReturn(
                wumpusLinkedCaveIndex, MockedRandomNumberGeneratorWorld.ENEMY_PLAYER_MIDDLE_CAVE_INDEX);

        NewGame game = mockedRNGWorld.getGame();

        final int caveToShootTo = 1;
        game.playerShootsToCave(caveToShootTo);

        final int actualWumpusCaveIndex = game.getWumpusCaveIndex();
        final int expectedWumpusCaveIndex = 17;
        assertEquals(expectedWumpusCaveIndex, actualWumpusCaveIndex);

        final Cave initialWumpusCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX);
        assertFalse(initialWumpusCave.containsAny(game.getWumpus()));

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsNotOver = false;
        assertEquals(actualGameState, gameIsNotOver);
    }

    @Test
    public void testThatPlayerRunsOutOfArrowsWithoutKillingWumpus() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();

        mockedRNGWorld.makeWumpusSleep();

        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        final int caveToShoot = 1;
        for (int i = 0; i < GameInitialConfigurations.NUMBER_OF_ARROWS; i++) {
            game.playerShootsToCave(caveToShoot);
        }

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerShootsAnArrowMissesWumpusAndWumpusWakesUpAndMoveToEatThePlayer() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerMove();

        mockedRNGWorld.makeWumpusWakeUp();

        final int numberOfLinkedCaves = 3;
        final int wumpusLinkedCaveIndex = 1;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(numberOfLinkedCaves)).thenReturn(
                wumpusLinkedCaveIndex,
                MockedRandomNumberGeneratorWorld.ENEMY_PLAYER_MIDDLE_CAVE_INDEX);

        NewGame game = mockedRNGWorld.getGame();

        game.playerMovesToCave(10);


        final int caveToShootTo = 11;
        game.playerShootsToCave(caveToShootTo);

        final int actualWumpusCaveIndex = game.getWumpusCaveIndex();
        final int expectedWumpusCaveIndex = 10;
        assertEquals(expectedWumpusCaveIndex, actualWumpusCaveIndex);

        final Cave initialWumpusCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX);
        assertFalse(initialWumpusCave.containsAny(game.getWumpus()));

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);
    }

    @Test
    public void testThatPlayerShootsMultipleLinkedCavesWithOneArrow() {
        final int playerStartingCave = 0;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(GameInitialConfigurations.NUMBER_OF_CAVES)).thenReturn(
                playerStartingCave,
                MockedRandomNumberGeneratorWorld.ENEMY_PLAYER_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.SECOND_BAT_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.FIRST_PIT_CAVE,
                MockedRandomNumberGeneratorWorld.SECOND_PIT_CAVE
        );
        NewGame game = mockedRNGWorld.getGame();

        int[] cavesToShootAt = new int[]{7, 8, 9, 10, 18};
        game.playerShootsToCave(cavesToShootAt);

        assertTrue(game.getWumpus().isDead());

        final int expectedRemainingArrows = GameInitialConfigurations.NUMBER_OF_ARROWS - 1;
        assertEquals(expectedRemainingArrows, game.getNumberOfArrows());
    }

    @Test
    public void testThatPlayerShootsMultipleNonLinkedCavesWithOneArrow() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        NewGame game = mockedRNGWorld.getGame();

        int[] cavesToShootAt = new int[]{15, 2, 11, 10, 3};
        final int cave1IndexFrom0 = 1;
        final int cave18IndexFrom10 = 1;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(GameInitialConfigurations.NUMBER_OF_LINKED_CAVES)).thenReturn(
                cave1IndexFrom0,
                cave18IndexFrom10);

        game.playerShootsToCave(cavesToShootAt);

        assertTrue(game.getWumpus().isDead());
    }

    @Test
    public void testThatPlayerShootsMultipleNonLinkedCavesWithOneArrowAndDies() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        NewGame game = mockedRNGWorld.getGame();

        int[] cavesToShootAt = new int[]{1, 15};
        final int cave9IndexFrom1 = 1;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(GameInitialConfigurations.NUMBER_OF_LINKED_CAVES)).thenReturn(
                cave9IndexFrom1);

        game.playerShootsToCave(cavesToShootAt);

        assertTrue(game.getPlayer().isDead());
    }

}
