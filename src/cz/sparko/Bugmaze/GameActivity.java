package cz.sparko.Bugmaze;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.example.games.basegameutils.GBaseGameActivityAND;
import cz.sparko.Bugmaze.Block.Block;
import cz.sparko.Bugmaze.Model.ScoreDTO;
import cz.sparko.Bugmaze.Model.ScoreModel;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.LoopModifier;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

//TODO: make some universal gameactivity
//TODO: make GameHandler which will handle logic
public class GameActivity extends GBaseGameActivityAND {
    public static final int CAMERA_WIDTH = 800;
    public static final int CAMERA_HEIGHT = 480;

    private ScoreModel scoreModel;

    private SharedPreferences prefs;
    private final String SHARED_PREFS_KEY = "settings";
    public static final String SETTINGS_MUSIC = "music";
    public static final String SETTINGS_EFFECTS = "effects";
    public static final String SETTINGS_GRAPHICS = "graphics";
    private boolean playSoundEffects = true;

    private int score = 0;
    private int tmpScore = 0;


    Camera camera;
    private Scene mScene;

    private GamePause pauseScene;

    private static Character character = null;
    private static GameField gameField;

    public GameActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoreModel = new ScoreModel(this);
        gameField = new GameField(this);
        prefs = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        playSoundEffects = prefs.getBoolean(SETTINGS_EFFECTS, true);
        try {
            scoreModel.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            scoreModel.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onResumeGame() {
        if (this.mEngine != null)
            super.onResumeGame();
    }

    @Override
    protected void onPause() {
        scoreModel.close();
        super.onPause();
    }

    //TODO: text handle
    public void increaseScore() {
        tmpScore++;
        //mScoreText.setText(String.format("%09d + %09d", tmpScore * tmpScore, score));
    }

    public void countScore() {
        score += tmpScore * tmpScore;
        tmpScore = 0;
        printScore();
        mScoreText.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.1f, 1f, 1.5f), new ScaleModifier(0.3f, 1.5f, 1f)));
        mScoreText.setScale(1.2f);
    }

    public static Character getCharacter() { return character; }
    public static GameField getGameField() { return gameField; }

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, GameActivity.CAMERA_WIDTH, GameActivity.CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
        engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        return engineOptions;
    }

    public void playRebuildSound() {
        if (playSoundEffects)
            rebuildSounds.get(new Random().nextInt(rebuildSounds.size())).play();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        mScene = new Scene();

        pauseScene = new GamePause(camera, mScene, this.getVertexBufferObjectManager());

        gameField.setScene(mScene);
        gameField.createField();

        mScene.setBackground(new Background(0.17f, 0.61f, 0f));

        character = new Character(0, 0, this.getVertexBufferObjectManager());
        character.setStartPosition(gameField.getActiveBlock());

        mScoreText = new Text((CAMERA_WIDTH - (GameField.FIELD_SIZE_X * Block.SIZE)) / 2, -5, this.mScoreFont, String.format("Score: %020d", score), new TextOptions(HorizontalAlign. RIGHT), this.getVertexBufferObjectManager());
        printScore();
        mScoreText.setZIndex(101);

        mCountDownText = new Text(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mScoreFont, String.format("  "), new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
        mCountDownText.setZIndex(101);
        mCountDownText.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1f, 1f, 10f), GameUpdateHandler.START_DELAY_SECONDS + 1, new LoopEntityModifier.ILoopEntityModifierListener() {
            @Override
            public void onLoopStarted(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount) {
                if (pLoop < pLoopCount - 1) {
                    mCountDownText.setText(String.format("%d", GameUpdateHandler.START_DELAY_SECONDS - pLoop));
                } else {
                    mCountDownText.setText(String.format("GO"));
                }
            }

            @Override
            public void onLoopFinished(LoopModifier<IEntity> pLoopModifier, int pLoop, int pLoopCount) {
                if (pLoop == pLoopCount - 1)
                    mCountDownText.setText("");
            }
        }));

        mScene.setTouchAreaBindingOnActionDownEnabled(true);
        mScene.registerUpdateHandler(new GameUpdateHandler(this, gameField, character));

        mScene.attachChild(mCountDownText);
        mScene.attachChild(mScoreText);
        mScene.attachChild(character);

        mScene.sortChildren();

        return mScene;
    }

    private void printScore() {
        mScoreText.setText(String.format("%s%d", getString(R.string.score_text), score));
    }

    public void saveScore() {
        //SQLite
        scoreModel.insertScore(new ScoreDTO(score, ((Long)System.currentTimeMillis()).toString()));
        if (mHelper.isSignedIn()) {
            getGamesClient().submitScore(getString(R.string.leaderboard_id), score);
        }
    }

    @Override
    public void onSignInFailed() {
        System.out.println("Sign-in failed.");
    }

    @Override
    public void onSignInSucceeded() {
        System.out.println("Sign-in succeeded.");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mScene.hasChildScene()) {
                mScene.setChildScene(pauseScene, false, true, true);
                //mEngine.stop();
            } else {
                mScene.clearChildScene();
                //mEngine.start();
            }
        }
        return false;
    }
}
