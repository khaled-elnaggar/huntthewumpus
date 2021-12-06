package model.gameobject;

public interface Killable {
    void kill(Killer killer);
    boolean wasKilledBy(GameObject killer);
    boolean isDead();
}
