package model.gamemap;

import model.game.GameInitialConfigurations;
import utilities.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GameMap {
    private List<Cave> caves;
    private RandomNumberGenerator randomNumberGenerator;

    public GameMap(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        buildGameMap();
    }

    public void buildGameMap() {
        caves = new ArrayList<>();
        buildCaves();
        buildCaveLinks();
    }

    private void buildCaves() {
        for (int i = 0; i < GameInitialConfigurations.NUMBER_OF_CAVES; i++) {
            caves.add(new Cave(i));
        }
    }

    private void buildCaveLinks() {
        for (int i = 0; i < caves.size(); i++) {
            int[] link = GameInitialConfigurations.CAVE_LINKS[i];
            Cave cave = caves.get(i);
            for (int caveNumber : link) {
                Cave linkedCave = caves.get(caveNumber);
                cave.addLink(linkedCave);
            }
        }
    }

    public Cave getRandomCave() {
        int randomCaveIndex = randomNumberGenerator.generateNumber(GameInitialConfigurations.NUMBER_OF_CAVES);
        return caves.get(randomCaveIndex);
    }

    public Cave getRandomCaveLinkedTo(Cave cave) {
        final int randomCaveIndex = randomNumberGenerator.generateNumber(GameInitialConfigurations.NUMBER_OF_LINKED_CAVES);
        return cave.getLinkedCaves().get(randomCaveIndex);
    }

    public Cave getACaveThatMeetsCondition(Predicate<Cave> condition) {
        Cave caveThatMeetsCondition;
        do {
            caveThatMeetsCondition = getRandomCave();
            } while (!condition.test(caveThatMeetsCondition));

        return caveThatMeetsCondition;
    }

    public List<Cave> getCaves() {
        return this.caves;
    }

    public List<Cave> validateCavesToShootAtAreLinked(Cave arrowStartingCave, int[] cavesArrayToShoot) {
        List<Cave> validCavesToShootAt = new ArrayList<>();

        Cave arrowCurrentCave = arrowStartingCave;
        for (int arrowNextCave : cavesArrayToShoot) {
            if (arrowCurrentCave.isLinkedTo(arrowNextCave)) {
                final Cave validCave = getCaves().get(arrowNextCave);
                validCavesToShootAt.add(validCave);
            } else {
                final int randomLinkedCaveIndex = randomNumberGenerator.generateNumber(GameInitialConfigurations.NUMBER_OF_LINKED_CAVES);
                final Cave randomLiknedCave = arrowCurrentCave.getLinkedCaves().get(randomLinkedCaveIndex);
                validCavesToShootAt.add(randomLiknedCave);
            }
            arrowCurrentCave = validCavesToShootAt.get(validCavesToShootAt.size() - 1);
        }

        return validCavesToShootAt;
    }
}
