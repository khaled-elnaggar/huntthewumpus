package acceptance.support;

import presenter.WumpusPresenter;
import presenter.WumpusPresenterImpl;
import utilities.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private final RandomNumberGeneratorBuilder RNGBuilder = new RandomNumberGeneratorBuilder();
    private WumpusPresenter wumpusPresenter;

    private final List<Action> actionsToExecute = new ArrayList<>();

    private boolean pendingActions = true;


    public void queueAction(Action action) {
        addDummyActionsForEnemyPlayerAndWumpusIfTheyHaveNoActions();
        this.actionsToExecute.add(action);
        pendingActions = true;
    }

    private void addDummyActionsForEnemyPlayerAndWumpusIfTheyHaveNoActions() {
        final int numberOfAllPlayerActions = actionsToExecute.size();
        this.RNGBuilder.makeEnemyMoveIfEnemyHasNoEnoughActions(numberOfAllPlayerActions);
        final int numberOfShootActions = (int) actionsToExecute.stream().filter(shootAction -> shootAction instanceof ShootAction).count();
        this.RNGBuilder.makeWumpusSleepIfWumpusHasNoEnoughAction(numberOfShootActions);
    }

    public void executeActions() {
        Action.setWumpusPresenter(getWumpusPresenter());
        this.RNGBuilder.updateTeleportCavesList();
        addDummyActionsForEnemyPlayerAndWumpusIfTheyHaveNoActions();

        actionsToExecute.forEach(Action::execute);
        actionsToExecute.clear();
        pendingActions = false;
    }

    public WumpusPresenter executeActionsAndGetWumpusPresenter() {
        final WumpusPresenter wumpusPresenter = getWumpusPresenter();
        this.executeActions();
        return wumpusPresenter;
    }

    public WumpusPresenter getWumpusPresenter() {
        initializePresenter();
        return this.wumpusPresenter;
    }

    private void initializePresenter() {
        if (this.wumpusPresenter == null) {
            RandomNumberGenerator randomNumberGenerator = RNGBuilder.build();
            this.wumpusPresenter = new WumpusPresenterImpl(randomNumberGenerator);
            this.wumpusPresenter.startNewGame();
        }
    }

    public RandomNumberGeneratorBuilder getRNGBuilder() {
        return RNGBuilder;
    }

    public boolean hasPendingActions() {
        return this.pendingActions;
    }

    public List<Action> getActionsToExecute() {
        return actionsToExecute;
    }
}

