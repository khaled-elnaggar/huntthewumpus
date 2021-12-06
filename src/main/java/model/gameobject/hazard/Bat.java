package model.gameobject.hazard;

import model.gamemap.Cave;
import model.gamemap.GameMap;
import model.gameobject.Player;

public class Bat extends Hazard {

    final String warningInTheSameCave = "a bat dropped you in a random cave";
    final String warningInTheLinkedCave = "you hear a rustling";

    private final GameMap gameMap;

    public Bat(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void teleport() {
        Cave caveToMoveTo = getRandomCaveWithNoPlayerOrBatInsideIt();
        this.getCave().removeGameObject(this);
        this.setCave(caveToMoveTo);
        caveToMoveTo.addHazard(this);
    }

    @Override
    public void executeActionOnPlayer(Player player) {
        player.teleport(getRandomCaveWithNoPlayerOrBatInsideIt());
        this.teleport();
    }

    private Cave getRandomCaveWithNoPlayerOrBatInsideIt() {
        return gameMap.getACaveThatMeetsCondition(Cave::containsNoPlayerNorBat);
    }

    @Override
    public String getWarningInTheLinkedCave() {
        return this.warningInTheLinkedCave;
    }

    @Override
    public int getPrecedence() {
        return 3;
    }
}
