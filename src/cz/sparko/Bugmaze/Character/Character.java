package cz.sparko.Bugmaze.Character;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Block.Block;
import cz.sparko.Bugmaze.PowerUp.PowerUp;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import java.util.ArrayList;

public abstract class Character extends AnimatedSprite {

    public static final int SIZE_X = 32;
    public static final int SIZE_Y = 32;

    public static final int Z_INDEX = 100;

    private float baseSpeed;
    private float speed;
    
    protected ArrayList<PowerUp> powerUps;

    protected Game game;

    public Character(float pX, float pY, ITiledTextureRegion texture, Game game, float speed) {
        super(pX, pY, texture, game.getVertexBufferObjectManager());
        baseSpeed = speed;
        this.speed = speed;
        this.game = game;
        this.setZIndex(Z_INDEX);
        powerUps = new ArrayList<PowerUp>();
        setPowerUps();
    }

    protected abstract void setPowerUps();

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);
    }

    public ArrayList<PowerUp> getPowerUps() { return powerUps; }

    public void switchSpeed(float multiple) {
        speed = baseSpeed * multiple;
    }

    public void increaseSpeed(float increaseBy) {
        speed -= increaseBy;    //it's time which spend character on one block, that's why minus
    }

    public float getSpeed() { return speed; }

    public void setStartPosition(Block activeBlock) {
        this.setPosition(activeBlock.getX() + (Block.SIZE / 2) - (Character.SIZE_X / 2), activeBlock.getY() + (Block.SIZE / 2) - (Character.SIZE_Y / 2));
        this.setRotation(activeBlock.getOutDirection().getDegree());
    }
}
