package model.game;

import model.gamemap.Cave;
import model.gamemap.GameMap;
import model.gameobject.Player;
import model.gameobject.hazard.Bat;
import model.gameobject.hazard.Hazard;
import model.gameobject.hazard.Pit;
import model.gameobject.hazard.Wumpus;
import utilities.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.function.Predicate;

public class NewGameInitializer {
    private Player player;
    private Wumpus wumpus;
    private final RandomNumberGenerator randomNumberGenerator;
    private ArrayList<Bat> bats;
    private ArrayList<Pit> pits;
    private final GameMap gameMap;
    private Player enemyPlayer;

    public NewGameInitializer(RandomNumberGenerator randomNumberGenerator, GameMap gameMap) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.gameMap = gameMap;
        initializePlayer();
        initializeEnemyPlayer();
        initializeWumpus();
        initializeBats();
        initializePits();
    }

    private void initializePlayer() {
        player = new Player(GameInitialConfigurations.NUMBER_OF_ARROWS);
        player.setId(GameInitialConfigurations.PLAYER_ID);
        player.setPlayerKillMessage("you shot yourself");
        player.setWumpusKillMessage("you shot the wumpus!");
        Cave cave1 = gameMap.getACaveThatMeetsCondition(cave -> true);
        cave1.addPlayer(player);
        player.setCave(cave1);
    }

    private void initializeEnemyPlayer() {
        enemyPlayer = new Player(GameInitialConfigurations.NUMBER_OF_ARROWS);
        enemyPlayer.setId("Enemy " + GameInitialConfigurations.PLAYER_ID);
        enemyPlayer.setPlayerKillMessage("enemy player shot you!");
        enemyPlayer.setWumpusKillMessage("enemy player shot the wumpus!");
        Cave startingCave = gameMap.getACaveThatMeetsCondition(cave -> true);
        startingCave.addPlayer(enemyPlayer);
        enemyPlayer.setCave(startingCave);
    }

    private void initializeWumpus() {
        wumpus = new Wumpus(randomNumberGenerator);
        wumpus.setId(GameInitialConfigurations.WUMPUS_ID);

        final Predicate<Cave> cavePredicate = Cave::containsNoPlayerNorLinkedCavePlayer;
        initializeHazardInCaveAccordingToCondition(wumpus, cavePredicate);
    }

    private void initializeBats() {
        bats = new ArrayList<>();
        for (int index = 0; index < GameInitialConfigurations.NUMBER_OF_BATS; index++) {
            Bat bat = new Bat(gameMap);
            bats.add(bat);
            bat.setId(GameInitialConfigurations.BAT_ID_PREFIX + index);
            final Predicate<Cave> cavePredicate = cave -> cave.containsNoPlayerNorLinkedCavePlayer() && !cave.containsAny(bats);
            initializeHazardInCaveAccordingToCondition(bat, cavePredicate);
        }
    }

    private void initializePits() {
        pits = new ArrayList<>();
        for (int index = 0; index < GameInitialConfigurations.NUMBER_OF_PITS; index++) {
            Pit pit = new Pit();
            pits.add(pit);
            pit.setId(GameInitialConfigurations.PITS_ID_PREFIX + index);
            final Predicate<Cave> cavePredicate = cave -> cave.containsNoPlayerNorLinkedCavePlayer() && !cave.containsAny(pits);
            initializeHazardInCaveAccordingToCondition(pit, cavePredicate);
        }
    }

    private void initializeHazardInCaveAccordingToCondition(Hazard hazard, Predicate<Cave> cavePredicate) {
        Cave cave = gameMap.getACaveThatMeetsCondition(cavePredicate);
        cave.addHazard(hazard);
        hazard.setCave(cave);
    }

    public Player getPlayer() {
        return player;
    }

    public Player getEnemyPlayer() {
        return this.enemyPlayer;
    }

    public Wumpus getWumpus() {
        return wumpus;
    }

    public ArrayList<Bat> getBats() {
        return bats;
    }

    public ArrayList<Pit> getPits() {
        return pits;
    }
}
