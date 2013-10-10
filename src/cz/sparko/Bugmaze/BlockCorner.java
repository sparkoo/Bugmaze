package cz.sparko.Bugmaze;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;

public class BlockCorner extends Block {
    public BlockCorner(Coordinate coordinate, float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int walkThroughs) {
        super(coordinate, pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, walkThroughs);
    }

    @Override
    public SequenceEntityModifier getMoveHandler(Ant ant) {
        final float centerX = this.getX() + (Block.SIZE / 2) - (Ant.SIZE_X / 2);
        final float centerY = this.getY() + (Block.SIZE / 2) - (Ant.SIZE_Y / 2);

        final float sourcePositionX = centerX + ((Block.SIZE / 2) * sourceWays.get(wayNo).getCoordinate().getX());
        final float sourcePositionY = centerY + ((Block.SIZE / 2) * sourceWays.get(wayNo).getCoordinate().getY());
        final float outPositionX = centerX + ((Block.SIZE / 2) * outWays.get(wayNo).getCoordinate().getX());
        final float outPositionY = centerY + ((Block.SIZE / 2) * outWays.get(wayNo).getCoordinate().getY());

        return new SequenceEntityModifier(
                new MoveModifier(ant.getSpeed() * 0.4f, sourcePositionX, centerX, sourcePositionY, centerY),
                new RotationModifier(ant.getSpeed() * 0.2f, ant.getRotation(), wayNo == 0 ? ant.getRotation() + 90f : ant.getRotation() - 90f),
                new MoveModifier(ant.getSpeed() * 0.4f, centerX, outPositionX, centerY, outPositionY));
    }

    @Override
    public void setPossibleSourceWays() {
        sourceWays = new ArrayList<Direction>(2);
        sourceWays.add(0, Direction.DOWN);
        sourceWays.add(1, Direction.RIGHT);
    }

    @Override
    public void setOutWays() {
        outWays = new ArrayList<Direction>(2);
        outWays.add(0, Direction.RIGHT);
        outWays.add(1, Direction.DOWN);
    }
}