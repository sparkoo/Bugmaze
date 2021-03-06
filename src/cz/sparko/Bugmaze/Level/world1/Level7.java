package cz.sparko.Bugmaze.Level.World1;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.*;
import cz.sparko.Bugmaze.Level.LevelMinScore;

public class Level7 extends LevelMinScore {
    public Level7(Game game) {
        super(game);
    }

    @Override
    protected Class[] getBlockTypes() {
        return new Class[]{Corner.class, Line.class, Cross.class, CrossT.class, LineOneWay.class, LineMine.class, LineSpeedDown.class, LineSpeedUp.class};
    }

    @Override
    protected float[] getBlockProbabilities() {
        return new float[]{0.68f, 0.10f, 0.05f, 0.02f, 0.05f, 0.05f, 0.025f, 0.025f};
    }

    @Override
    public float getSpeed() {
        return 1.1f;
    }

    @Override
    public int getTargetScore() {
        return 700;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level8(game);
    }
}
