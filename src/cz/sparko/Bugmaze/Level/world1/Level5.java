package cz.sparko.Bugmaze.Level.World1;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.*;
import cz.sparko.Bugmaze.Level.LevelMinScore;

public class Level5 extends LevelMinScore {
    public Level5(Game game) {
        super(game);
    }

    @Override
    protected Class[] getBlockTypes() {
        return new Class[]{Corner.class, Line.class, Cross.class, CornerMine.class, LineMine.class};
    }

    @Override
    protected float[] getBlockProbabilities() {
        return new float[]{0.65f, 0.15f, 0.1f, 0.05f, 0.05f};
    }

    @Override
    public float getSpeed() {
        return 1.1f;
    }

    @Override
    public int getTargetScore() {
        return 750;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level6(game);
    }
}
