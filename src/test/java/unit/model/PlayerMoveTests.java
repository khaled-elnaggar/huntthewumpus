package unit.model;

import model.game.GameInitialConfigurations;
import model.game.NewGame;
import model.gamemap.Cave;
import model.gameobject.Player;
import model.gameobject.hazard.Bat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import unit.support.MockedRandomNumberGeneratorWorld;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerMoveTests {
    MockedRandomNumberGeneratorWorld mockedRNGWorld;

    @BeforeEach
    public void setUp(){
        mockedRNGWorld = new MockedRandomNumberGeneratorWorld();
    }

    @Test
    public void testThatThePlayerCanMoveToALinkedCave() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        final int caveIndexToMoveTo = 1;
        game.playerMovesToCave(caveIndexToMoveTo);

        final int actualPlayerCaveIndex = game.getPlayerCaveIndex();
        assertEquals(caveIndexToMoveTo, actualPlayerCaveIndex);

        Cave currentPlayerCave = game.getGameMap().getCaves().get(caveIndexToMoveTo);
        Player player = game.getPlayer();
        assertTrue(currentPlayerCave.containsAny(player));

        Cave pastPlayerCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.PLAYER_STARTING_CAVE_INDEX);
        assertFalse(pastPlayerCave.containsAny(player));
    }

    @Test
    public void testMoveToNonConnectedCave() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        final int caveIndexToMoveTo = 17;
        game.playerMovesToCave(caveIndexToMoveTo);

        final int actualPlayerCaveIndex = game.getPlayerCaveIndex();
        assertNotEquals(caveIndexToMoveTo, actualPlayerCaveIndex);
        assertEquals(MockedRandomNumberGeneratorWorld.PLAYER_STARTING_CAVE_INDEX, actualPlayerCaveIndex);

        Cave currentPlayerCave = game.getGameMap().getCaves().get(caveIndexToMoveTo);
        Player player = game.getPlayer();
        assertFalse(currentPlayerCave.containsAny(player));

        Cave pastPlayerCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.PLAYER_STARTING_CAVE_INDEX);
        assertTrue(pastPlayerCave.containsAny(player));

        assertEquals(mockedRNGWorld.ENEMY_PLAYER_STARTING_CAVE_INDEX, game.getEnemyPlayerCaveIndex());
        assertEquals(GameInitialConfigurations.NUMBER_OF_ARROWS, game.getEnemyRemainingArrows());
    }

    @Test
    public void testMovingPlayerToCaveThatHasAWumups() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        int[] journeyPath = new int[]{10, 18};

        for (int cave : journeyPath) {
            game.playerMovesToCave(cave);
        }


        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(gameIsOver, actualGameState);

        List<String> messages = game.getWarnings();
        assertTrue(messages.contains(game.getWumpus().getPlayerKillMessage()));
    }

    @Test
    public void testMovingPlayerToACaveNearAWumpusAndSensingTheWumpus() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        int[] journeyPath = new int[]{10};

        for (int cave : journeyPath) {
            game.playerMovesToCave(cave);
        }


        final boolean actualGameState = game.isGameOver();
        final boolean gameIsNotOver = false;
        assertEquals(gameIsNotOver, actualGameState);

        List<String> messages = game.getWarnings();
        assertTrue(messages.contains(game.getWumpus().getWarningInTheLinkedCave()));
    }

    @Test
    public void testThatPlayerEnterRoomWithPit() {
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();

        NewGame game = mockedRNGWorld.getGame();

        final int[] journeyPath = {10, 11, 2, 3};
        for (int caveNumber : journeyPath) {
            game.playerMovesToCave(caveNumber);
        }

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);

        List<String> messages = game.getWarnings();
        assertTrue(messages.contains(game.getPits().get(0).getPlayerKillMessage()));
    }

    @Test
    public void testThatPlayerEnterRoomWithBatTwice() {
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();
        final int enemyPlayerStartingCave = 9;

        final int playerFirstDropDownCaveIndex = 15;
        final int firstBatFinalCaveIndex = 2;

        final int playerSecondDropDownCaveIndex = 6;
        final int secondBatFinalCaveIndex = 0;

        final int secondBatStartingCaveIndex = 14;
        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(GameInitialConfigurations.NUMBER_OF_CAVES)).thenReturn(
                MockedRandomNumberGeneratorWorld.PLAYER_STARTING_CAVE_INDEX,
                enemyPlayerStartingCave,
                MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX,
                secondBatStartingCaveIndex,
                MockedRandomNumberGeneratorWorld.FIRST_PIT_CAVE,
                MockedRandomNumberGeneratorWorld.SECOND_PIT_CAVE,
                playerFirstDropDownCaveIndex,
                firstBatFinalCaveIndex,
                playerSecondDropDownCaveIndex,
                secondBatFinalCaveIndex
        );


        NewGame game = mockedRNGWorld.getGame();

        final int[] journeyPath = {10, 11, 12, MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX};
        for (int cave : journeyPath) {
            game.playerMovesToCave(cave);
        }

        final int actualPlayerCaveIndex = game.getPlayerCaveIndex();
        assertEquals(playerFirstDropDownCaveIndex, actualPlayerCaveIndex);


        final Bat firstBat = game.getBats().get(0);
        final Cave firstBatActualCave = firstBat.getCave();
        assertEquals(firstBatFinalCaveIndex, firstBatActualCave.getNumber());

        final Cave previousCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX);
        assertFalse(previousCave.containsAny(game.getPlayer()));
        assertFalse(previousCave.containsAny(firstBat));

        game.playerMovesToCave(secondBatStartingCaveIndex);

        final int actualPlayerSecondCaveIndex = game.getPlayerCaveIndex();
        assertEquals(playerSecondDropDownCaveIndex, actualPlayerSecondCaveIndex);


        final Bat secondBat = game.getBats().get(1);
        final Cave secondBatActualCave = secondBat.getCave();
        assertEquals(secondBatFinalCaveIndex, secondBatActualCave.getNumber());

        final Cave secondBatPreviousCave = game.getGameMap().getCaves().get(secondBatStartingCaveIndex);
        assertFalse(secondBatPreviousCave.containsAny(game.getPlayer()));
        assertFalse(secondBatPreviousCave.containsAny(secondBat));

    }

    @Test
    public void testThatPlayerEnterRoomWithBat() {
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();
        final int playerDropDownCaveIndex = 8;
        final int firstBatFinalCaveIndex = 5;

        Mockito.when(mockedRNGWorld.getRandomNumberGenerator().generateNumber(GameInitialConfigurations.NUMBER_OF_CAVES)).thenReturn(
                MockedRandomNumberGeneratorWorld.PLAYER_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.ENEMY_PLAYER_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.WUMPUS_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.SECOND_BAT_STARTING_CAVE_INDEX,
                MockedRandomNumberGeneratorWorld.FIRST_PIT_CAVE,
                MockedRandomNumberGeneratorWorld.SECOND_PIT_CAVE,
                playerDropDownCaveIndex,
                firstBatFinalCaveIndex
        );


        NewGame game = mockedRNGWorld.getGame();

        final int[] journeyPath = {10, 11, 12, MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX};
        for (int cave : journeyPath) {
            game.playerMovesToCave(cave);
        }

        final int actualPlayerCaveIndex = game.getPlayerCaveIndex();
        assertEquals(playerDropDownCaveIndex, actualPlayerCaveIndex);


        final Bat firstBat = game.getBats().get(0);
        final Cave firstBatActualCave = firstBat.getCave();
        assertEquals(firstBatFinalCaveIndex, firstBatActualCave.getNumber());

        final Cave previousCave = game.getGameMap().getCaves().get(MockedRandomNumberGeneratorWorld.FIRST_BAT_STARTING_CAVE_INDEX);
        assertFalse(previousCave.containsAny(game.getPlayer()));
        assertFalse(previousCave.containsAny(firstBat));
    }

    @Test
    public void testThatPlayerEnterRoomWithPitAndBat() {
        mockedRNGWorld.makeEnemyPlayerGoBackAndForth();
        mockedRNGWorld.configureMockingBasedOnDefaultLocationOfGameObjectsOnMap();

        NewGame game = mockedRNGWorld.getGame();

        final int[] journeyPath = {10, 11, 12, 13};
        for (int caveNumber : journeyPath) {
            game.playerMovesToCave(caveNumber);
        }

        final boolean actualGameState = game.isGameOver();
        final boolean gameIsOver = true;
        assertEquals(actualGameState, gameIsOver);
    }


}
