package model.gameobject.hazard;

import model.gameobject.GameObject;
import model.gameobject.Player;

public abstract class Hazard extends GameObject {
    public abstract void executeActionOnPlayer(Player player);

    public abstract String getWarningInTheLinkedCave();
}
