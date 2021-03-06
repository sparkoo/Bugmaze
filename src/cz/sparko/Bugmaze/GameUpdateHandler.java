package cz.sparko.Bugmaze;

import cz.sparko.Bugmaze.Block.HasAction;
import cz.sparko.Bugmaze.Character.Character;
import cz.sparko.Bugmaze.Helper.Coordinate;
import cz.sparko.Bugmaze.Level.Level;
import cz.sparko.Bugmaze.Manager.GameManager;
import cz.sparko.Bugmaze.PowerUp.PowerUpNextBlockListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.util.modifier.IModifier;

public class GameUpdateHandler implements IUpdateHandler, PowerUpNextBlockListener {
    public static final int START_DELAY_SECONDS = 3;

    private GameManager gameManager;
    private GameField gameField;
    private cz.sparko.Bugmaze.Character.Character character;
    private boolean running = false;
    float timeCounter = 0;
    private PowerUpNextBlockListener powerUpNextBlockListener;
    private Level level;
    public GameUpdateHandler(GameField gameField, Character character, Level level) {
        this.gameManager = GameManager.getInstance();
        this.gameField = gameField;
        this.character = character;
        this.powerUpNextBlockListener = this;
        this.level = level;
    }

    public void setPowerUpNextBlockListener(PowerUpNextBlockListener powerUpNextBlockListener) {
        this.powerUpNextBlockListener = powerUpNextBlockListener;
    }
    public void unsetPowerUpNextBlockListener() {
        this.powerUpNextBlockListener = this;
    }

    @Override
    public void onUpdate(float pSecondsElapsed) {
        if (!GameManager.getInstance().getRunning())
            return;

        timeCounter += pSecondsElapsed;
        if (running)
            gameManager.increaseTime(pSecondsElapsed);

        if (gameField.isNeedRefreshField() && running) {
            gameManager.countScore();
            gameField.refreshField(level);
            gameManager.playRebuildSound();
        }
        if (running && timeCounter > character.getSpeed()) {

            timeCounter = 0;
            int currentX = gameField.getActiveBlock().getCoordinate().getX() + gameField.getActiveBlock().getOutCoordinate().getX();
            int currentY = gameField.getActiveBlock().getCoordinate().getY() + gameField.getActiveBlock().getOutCoordinate().getY();
            if (currentX < 0) currentX = GameField.FIELD_SIZE_X - 1;
            if (currentX >= GameField.FIELD_SIZE_X) currentX = 0;
            if (currentY < 0) currentY = GameField.FIELD_SIZE_Y - 1;
            if (currentY >= GameField.FIELD_SIZE_Y) currentY = 0;
            Coordinate nCoordinate = new Coordinate(currentX, currentY);

            if (gameField.getBlock(nCoordinate.getX(), nCoordinate.getY()).canGetInFrom(gameField.getActiveBlock().getCoordinate())) {
                powerUpNextBlockListener.reachedNextBlock();
                level.reachedNextBlock(gameField.getBlock(nCoordinate.getX(), nCoordinate.getY()), this);
                gameField.setActiveBlock(gameField.getBlock(nCoordinate.getX(), nCoordinate.getY()));

                if (gameField.getActiveBlock() instanceof HasAction)
                    ((HasAction) gameField.getActiveBlock()).doActionBefore();

                character.registerEntityModifier(gameField.getActiveBlock().getMoveHandler(character));
                gameField.getActiveBlock().delete();
                gameManager.increaseScore();
            } else {
                gameOver(false);
            }
        } else {
            if (!running && timeCounter > START_DELAY_SECONDS) { //first step after start delay
                running = true;
                character.animate(new long[]{300, 300}, 0, 1, true);
                character.registerEntityModifier(gameField.getActiveBlock().getMoveHandler(character));
                gameField.getActiveBlock().delete();
                timeCounter = 0;
            }
        }
    }

    public void gameOver(final boolean completed) {
        final IEntityModifier gameOverProcedure = new DelayModifier(2, new IEntityModifier.IEntityModifierListener() {

            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                if (completed) {
                    character.animate(new long[]{150, 150}, 0, 1, true);
                    gameManager.countScore();
                } else {
                    character.animate(new long[]{100, 100}, 2, 3, true);
                }
                running = false;
                gameManager.stopRunning();
                //gameManager.saveScore();
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                gameManager.showResultScreen(completed);
            }
        });
        character.registerEntityModifier(gameOverProcedure);
        }

    @Override
    public void reset() {
    }

    @Override
    public void reachedNextBlock() {
    }
}
