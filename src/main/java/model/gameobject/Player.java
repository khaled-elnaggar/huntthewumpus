package model.gameobject;

import model.Arrow;
import model.gamemap.Cave;
import model.gameobject.hazard.Hazard;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Killable, Killer {
    private boolean dead;
    private final Arrow arrow;
    private final List<String> warnings = new ArrayList<>();
    private List<Cave> lastShotCaves = new ArrayList<>();
    private Killer killer;
    private String playerKillMessage;
    private String wumpusKillMessage;

    @Override
    public void kill(Killer killer) {
        this.dead = true;
        this.killer = killer;
        warnings.add(killer.getPlayerKillMessage());
    }

    public Player(int numberOfArrows) {
        arrow = new Arrow(numberOfArrows);
    }

    public boolean move(Cave caveToMoveTo) {
        if (!this.isMyCurrentCaveLinkedTo(caveToMoveTo)) {
            return false;
        }
        changeMyCaveLocationTo(caveToMoveTo);
        executePostMoveActions();
        senseWarnings();
        return true;
    }

    private boolean isMyCurrentCaveLinkedTo(Cave caveToMoveTo) {
        return this.getCave().isLinkedTo(caveToMoveTo);
    }

    public void teleport(Cave caveToMoveTo) {
        changeMyCaveLocationTo(caveToMoveTo);
        addAWarning("a bat dropped you in a random cave");
        executePostMoveActions();
        senseWarnings();
    }

    private void changeMyCaveLocationTo(Cave caveToMoveTo) {
        this.warnings.clear();
        this.getCave().removeGameObject(this);
        this.setCave(caveToMoveTo);
        caveToMoveTo.addPlayer(this);
    }

    private void executePostMoveActions() {
        for (Hazard hazard : getCave().getHazards()) {
            hazard.executeActionOnPlayer(this);
            if (isDead()) {
                break;
            }
        }
    }

    private void senseWarnings() {
        List<Cave> linkedCaves = this.getCave().getLinkedCaves();

        linkedCaves.stream()
                .flatMap(cave -> cave.getHazards().stream())
                .map(Hazard::getWarningInTheLinkedCave)
                .forEach(warnings::add);
    }

    public void addAWarning(String warning) {
        warnings.add(warning);
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean wasKilledBy(GameObject killer) {
        return this.killer != null && this.killer.equals(killer);
    }

    public void shoot(List<Cave> caves) {
        lastShotCaves = caves;
        this.warnings.clear();
        caves.forEach(this::shootSingle);
        arrow.decrementByOne();
        if (hasNoArrows()) {
            warnings.add("You ran out of arrows");
        }
    }

    public void shootSingle(Cave caveToShoot) {
        caveToShoot.getKillables()
                .forEach(killable -> killable.kill(this));
    }

    public boolean hasNoArrows() {
        return arrow.getNumber() == 0;
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public Arrow getArrows() {
        return this.arrow;
    }

    @Override
    public int getPrecedence() {
        return 4;
    }

    public int[] getLastShotCaves() {
        int[] caves = lastShotCaves.stream().mapToInt(Cave::getNumber).toArray();
        lastShotCaves.clear();
        return caves;
    }
    public void setPlayerKillMessage(String killMessage) {
        this.playerKillMessage = killMessage;
    }

    @Override
    public String getPlayerKillMessage() {
        return this.playerKillMessage;
    }
    public String getWumpusKilledMessage(){
        return this.wumpusKillMessage;
    }

    public void setWumpusKillMessage(String wumpusKillMessage) {
        this.wumpusKillMessage = wumpusKillMessage;
    }
}
