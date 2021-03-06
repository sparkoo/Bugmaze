package cz.sparko.Bugmaze.Menu;

import cz.sparko.Bugmaze.Activity.Game;
import cz.sparko.Bugmaze.Helper.Settings;
import cz.sparko.Bugmaze.Level.Endless;
import cz.sparko.Bugmaze.Manager.GameManager;
import cz.sparko.Bugmaze.Menu.TwoStateMenuButton;
import cz.sparko.Bugmaze.Resource.GamefieldTextureResource;
import cz.sparko.Bugmaze.Resource.MenuOptionsTextureResource;
import cz.sparko.Bugmaze.Resource.ResourceHandler;
import cz.sparko.Bugmaze.Resource.TextureResource;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import java.util.ArrayList;

public class Pause extends MenuScene implements MenuScene.IOnMenuItemClickListener {
    private int positionX;
    private int positionY;

    private Game game;

    ArrayList<IMenuItem> menuItems = new ArrayList<IMenuItem>(3);
    ArrayList<IMenuItem> menuIcons = new ArrayList<IMenuItem>(3);
    AnimatedSpriteMenuItem musicButton;

    public Pause(Camera camera, final Scene gameScene, Game game) {
        super(camera);
        this.game = game;
        TextureResource textureResource = game.getResourceHandler().getTextureResource(ResourceHandler.GAMEFIELD);
        positionX = (int)(camera.getWidth() / 2 - textureResource.getResource(GamefieldTextureResource.PAUSE_BACKGROUND).getWidth() / 2);
        positionY = (int)(camera.getHeight() / 2 - textureResource.getResource(GamefieldTextureResource.PAUSE_BACKGROUND).getHeight() / 2);

        final Sprite pausedSprite = new Sprite(positionX, positionY, textureResource.getResource(GamefieldTextureResource.PAUSE_BACKGROUND), game.getVertexBufferObjectManager());
        this.attachChild(pausedSprite);
        this.setBackgroundEnabled(false);

        menuItems.add(new TwoStateMenuButton(1, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_CONTINUE), game.getVertexBufferObjectManager()));
        menuItems.add(new TwoStateMenuButton(2, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_PLAY_AGAIN), game.getVertexBufferObjectManager()));
        menuItems.add(new TwoStateMenuButton(3, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_RETURN), game.getVertexBufferObjectManager()));

        menuIcons.add(new TwoStateMenuButton(1, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_CONTINUE_ICON), game.getVertexBufferObjectManager()));
        menuIcons.add(new TwoStateMenuButton(2, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_PLAY_AGAIN_ICON), game.getVertexBufferObjectManager()));
        menuIcons.add(new TwoStateMenuButton(3, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_RETURN_ICON), game.getVertexBufferObjectManager()));


        musicButton = new AnimatedSpriteMenuItem(4, (ITiledTextureRegion)textureResource.getResource(GamefieldTextureResource.PAUSE_MUSIC_ICON), game.getVertexBufferObjectManager());
        musicButton.setPosition(positionX + textureResource.getResource(GamefieldTextureResource.PAUSE_BACKGROUND).getWidth() - 76, positionY + 27);
        if (!game.getSettingsBoolean(Settings.EFFECTS)) musicButton.setCurrentTileIndex(1);
        this.addMenuItem(musicButton);

        int posX = positionX + 105;
        int posY = positionY + 30;

        for (IMenuItem menuItem : menuItems) {
            menuItem.setPosition(posX, posY);
            posY += 80;
            this.addMenuItem(menuItem);
        }
        posX = positionX + 30;
        posY = positionY + 40;
        for (IMenuItem menuIcon : menuIcons) {
            menuIcon.setPosition(posX, posY);
            posY += 80;
            this.addMenuItem(menuIcon);
        }
        this.setOnMenuItemClickListener(this);

        Sprite pauseBtn = new Sprite(Game.CAMERA_WIDTH - 64 - 5, 5, textureResource.getResource(GamefieldTextureResource.PAUSE_BUTTON), game.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown() && !gameScene.hasChildScene())
                    GameManager.getInstance().pauseGame();
                return true;
            }
        };
        pauseBtn.setZIndex(200);
        gameScene.registerTouchArea(pauseBtn);
        gameScene.attachChild(pauseBtn);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        switch (pMenuItem.getID()) {
            case 1:
                GameManager.getInstance().unpauseGame();
                break;
            case 2:
                GameManager.getInstance().startGame(GameManager.getInstance().getLevel());
                break;
            case 3:
                GameManager.getInstance().gameOver();
                break;
            case 4:
                if (toogleSettings(Settings.EFFECTS)) musicButton.setCurrentTileIndex(0);
                else musicButton.setCurrentTileIndex(1);

                GameManager.getInstance().setPlaySoundEffects(game.getSettingsBoolean(Settings.EFFECTS));
                break;
        }
        return false;
    }

    private Boolean toogleSettings(Settings key) {
        boolean newValue = !game.getSettingsBoolean(key);
        game.setSettingsBoolean(key, newValue);
        return newValue;
    }
}
