package cz.sparko.Ants;

import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;

public class BlockLine extends Block{
    public BlockLine(Coordinate coordinate, float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(coordinate ,pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
    }

    @Override
    public void moveAnt(Ant ant) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pastToNextBlock(Ant ant, Block[][] blocks) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPossibleSourceWays() {
        sourceWays = new ArrayList<Integer>(2);
        sourceWays.add(0, Block.DOWN);
        sourceWays.add(1, Block.UP);
    }

    @Override
    public void setOutWays() {
        outWays = new ArrayList<Integer>(2);
        outWays.add(0, Block.UP);
        outWays.add(1, Block.DOWN);
    }
}
