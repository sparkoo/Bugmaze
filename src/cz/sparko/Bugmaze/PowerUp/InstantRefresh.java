package cz.sparko.Bugmaze.PowerUp;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Manager.Manager;
import cz.sparko.Bugmaze.Resource.GamefieldTextureResource;
import cz.sparko.Bugmaze.Resource.ResourceHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class InstantRefresh extends PowerUp {
    private static final int TIMER = 2;

    public InstantRefresh(Game game) {
        super((ITiledTextureRegion)game.getResourceHandler().getTextureResource(ResourceHandler.GAMEFIELD).getResource(GamefieldTextureResource.POWER_UP_BUTTON));
    }

    @Override
    public void action() {
        gameField.refreshField();
    }

    @Override
    protected int getTimeToRefresh() {
        return TIMER;
    }
}