package cz.sparko.Bugmaze.Level.World1;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.*;
import cz.sparko.Bugmaze.Level.LevelMinScore;

public class Level16 extends LevelMinScore {
    public Level16(Game game) {
        super(game);
    }

    @Override
    protected Class[] getBlockTypes() {
        return new Class[]{Corner.class, Line.class, Cross.class, CornerMine.class, LineMine.class, LineOneWay.class, CornerNoRotate.class, LineNoRotate.class, LineSpeedDown.class, LineSpeedUp.class, LineAutoRotate.class};
    }

    @Override
    protected float[] getBlockProbabilities() {
        return new float[]{0.60f, 0.06f, 0.1f, 0.05f, 0.02f, 0.03f, 0.02f, 0.02f, 0.04f, 0.04f, 0.02f};
    }

    @Override
    public float getSpeed() {
        return 0.8f;
    }

    @Override
    public int getTargetScore() {
        return 2250;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level17(game);
    }
}
