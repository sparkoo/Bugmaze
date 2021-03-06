package cz.sparko.Bugmaze.Block;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Helper.Coordinate;
import cz.sparko.Bugmaze.Resource.GamefieldTextureResource;
import cz.sparko.Bugmaze.Resource.ResourceHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class CornerMine extends Corner implements HasMine {
    public CornerMine(Coordinate coordinate, Game game) {
        super(coordinate, (ITiledTextureRegion)game.getResourceHandler().getTextureResource(ResourceHandler.GAMEFIELD).getResource(GamefieldTextureResource.BLOCK_CORNER_MINE), game);
    }

    @Override
    public boolean canGetInFrom(Coordinate fromCoordinate) {
        return false;
    }

    @Override
    public Block getUnminedBlock(Game game) {
        Block unminedBlock = new Corner(coordinate, game);
        for (int i = 0; i < rotateCount; i++)
            unminedBlock.rotate();
        return unminedBlock;
    }
}
