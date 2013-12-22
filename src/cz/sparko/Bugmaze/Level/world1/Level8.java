package cz.sparko.Bugmaze.Level.World1;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.*;
import cz.sparko.Bugmaze.Level.LevelMinScore;

public class Level8 extends LevelMinScore {
    public Level8(Game game) {
        super(game);
    }

    @Override
    protected Class[] getBlockTypes() {
        return new Class[]{Corner.class, Line.class, Cross.class, CornerMine.class, LineMine.class, LineOneWay.class};
    }

    @Override
    protected float[] getBlockProbabilities() {
        return new float[]{0.65f, 0.10f, 0.1f, 0.05f, 0.05f, 0.05f};
    }

    @Override
    public float getSpeed() {
        return 1f;
    }

    @Override
    public int getTargetScore() {
        return 1500;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level9(game);
    }
}
