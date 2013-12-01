package cz.sparko.Bugmaze.Level;

import cz.sparko.Bugmaze.Activity.Game;

public class Level1 extends LevelMinScore {
    public Level1(Game game) {
        super(game);
    }

    @Override
    protected int getMinScore() {
        return 500;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level2(game);
    }
}
