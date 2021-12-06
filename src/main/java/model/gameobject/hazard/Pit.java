package model.gameobject.hazard;

import model.gameobject.Killer;
import model.gameobject.Player;

public class Pit extends Hazard implements Killer {
    final String warningInTheSameCave = "you fell into a pit";
    final String warningInTheLinkedCave = "you feel a draft";

    public String getWarningInTheLinkedCave() {
        return warningInTheLinkedCave;
    }

    public String getWarningInTheSameCave() {
        return warningInTheSameCave;
    }

    @Override
    public void executeActionOnPlayer(Player player) {
        player.kill(this);
    }

    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    public String getPlayerKillMessage() {
        return warningInTheSameCave;
    }
}
