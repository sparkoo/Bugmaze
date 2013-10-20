package cz.sparko.Bugmaze.Menu;

import cz.sparko.Bugmaze.GameActivity;
import cz.sparko.Bugmaze.MenuActivity;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.util.color.Color;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public abstract class Menu implements MenuScene.IOnMenuItemClickListener {
    private final float MENU_SWITCH_SPEED = 0.2f;
    protected final int MENU_SWITCH_NEXT = 1;
    protected final int MENU_SWITCH_PREV = -1;

    protected Menu prev;
    protected MenuScene menuScene;
    protected MenuActivity menuActivity;
    protected ArrayList<IMenuItem> menuItems;

    protected final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f);
    protected final Color TEXT_COLOR_SELECTED = new Color(0.2f, 0.2f, 0.2f);

    public Menu(Menu prev, MenuActivity menuActivity) {
        this.prev = prev;
        this.menuActivity = menuActivity;
        setMenuItems();
        createMenuScene();
    }

    private void createMenuScene() {
        menuScene = new MenuScene(menuActivity.getCamera());
        menuScene.setBackgroundEnabled(false);

        int yPosition = 150;
        for (IMenuItem menuItem : menuItems) {
            menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            menuItem.setPosition(180, yPosition);
            yPosition += 70;
            menuScene.addMenuItem(menuItem);
        }

        menuScene.setOnMenuItemClickListener(this);
    }

    public MenuScene getMenuScene() {
        return menuScene;
    }

    protected void goToMenu(final Menu newMenu) {
        goToMenu(newMenu, MENU_SWITCH_NEXT);
    }

    protected void goBack() {
        if (prev != null) {
            goToMenu(prev, MENU_SWITCH_PREV);
        }
    }

    protected void goToMenu(final Menu newMenu, final int direction) {
        this.getMenuScene().registerEntityModifier(new MoveModifier(MENU_SWITCH_SPEED, 0, -GameActivity.CAMERA_WIDTH * direction, 0, 0));
        IUpdateHandler moveMenuHandler = new IUpdateHandler() {
            float timeElapsed = 0;
            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (timeElapsed < MENU_SWITCH_SPEED)
                    timeElapsed += pSecondsElapsed;
                else {
                    menuActivity.getScene().clearChildScene();
                    menuActivity.getScene().setChildScene(newMenu.getMenuScene());
                    newMenu.getMenuScene().setPosition(GameActivity.CAMERA_WIDTH * direction, 0);
                    newMenu.getMenuScene().registerEntityModifier(new MoveModifier(MENU_SWITCH_SPEED, GameActivity.CAMERA_WIDTH * direction, 0, 0, 0));
                    getMenuScene().unregisterUpdateHandler(this);
                }
            }
            @Override
            public void reset() {
            }
        };
        this.getMenuScene().registerUpdateHandler(moveMenuHandler);
    }

    protected abstract void setMenuItems();
}
