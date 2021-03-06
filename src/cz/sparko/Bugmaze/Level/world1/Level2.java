package cz.sparko.Bugmaze.Level.World1;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.Corner;
import cz.sparko.Bugmaze.Block.Cross;
import cz.sparko.Bugmaze.Block.Line;
import cz.sparko.Bugmaze.Block.LineOneWay;
import cz.sparko.Bugmaze.Level.LevelMinScore;

public class Level2 extends LevelMinScore {
    public Level2(Game game) {
        super(game);
    }

    @Override
    protected Class[] getBlockTypes() {
        return new Class[]{Corner.class, Line.class, Cross.class, LineOneWay.class};
    }

    @Override
    protected float[] getBlockProbabilities() {
        return new float[]{0.7f, 0.2f, 0.05f, 0.05f};
    }

    @Override
    public float getSpeed() {
        return 1.4f;
    }

    @Override
    public int getTargetScore() {
        return 200;
    }

    @Override
    protected void initNextLevel() {
        this.nextLevel = new Level3(game);
    }
}
