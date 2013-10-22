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
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.texture.region.ITextureRegion;
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
    private BitmapTextureAtlas mFontTexture;
    private Font mScoreFont;
    private Text mScoreText;

    private Font mCountDownFont;
    private Text mCountDownText;

    private ArrayList<Sound> rebuildSounds;

    private BuildableBitmapTextureAtlas mBitmapTextureAtlas;

    Camera camera;
    private Scene mScene;

    private Scene pauseScene;
    private ITextureRegion pauseBackground;

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

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        gameField.loadResources(this.getTextureManager());

        this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 512, 512, prefs.getBoolean(SETTINGS_GRAPHICS, true) ? TextureOptions.BILINEAR_PREMULTIPLYALPHA : TextureOptions.DEFAULT);
        this.mBitmapTextureAtlas.clearTextureAtlasSources();
        Character.loadResources(this.mBitmapTextureAtlas, this);
        Block.loadResources(this.mBitmapTextureAtlas, this);
        pauseBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "pauseBackground.png");

        try {
            this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.mBitmapTextureAtlas.load();
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            e.printStackTrace();
        }

        this.mFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, prefs.getBoolean(SETTINGS_GRAPHICS, true) ? TextureOptions.BILINEAR_PREMULTIPLYALPHA : TextureOptions.DEFAULT);
        FontFactory.setAssetBasePath("font/");
        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);

        this.mScoreFont = FontFactory.createFromAsset(this.getFontManager(), this.mFontTexture, this.getAssets(), "Indie_Flower.ttf", 36, true, Color.WHITE.getABGRPackedInt());
        this.mCountDownFont = FontFactory.createFromAsset(this.getFontManager(), this.mFontTexture, this.getAssets(), "Indie_Flower.ttf", 100, true, Color.WHITE.getABGRPackedInt());

        this.getFontManager().loadFont(this.mScoreFont);
        this.getFontManager().loadFont(this.mCountDownFont);

        rebuildSounds = new ArrayList<Sound>(5);
        SoundFactory.setAssetBasePath("sfx/");
        try {
            rebuildSounds.add(SoundFactory.createSoundFromAsset(getSoundManager(), this, "rebuild1.ogg"));
            rebuildSounds.add(SoundFactory.createSoundFromAsset(getSoundManager(), this, "rebuild2.ogg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playRebuildSound() {
        if (playSoundEffects)
            rebuildSounds.get(new Random().nextInt(rebuildSounds.size())).play();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        pauseScene = new CameraScene(camera);
        /* Make the 'PAUSED'-label centered on the camera. */
        final int x = (int)(CAMERA_WIDTH / 2 - pauseBackground.getWidth() / 2);
        final int y = (int)(CAMERA_HEIGHT / 2 - pauseBackground.getHeight() / 2);
        final Sprite pausedSprite = new Sprite(x, y, pauseBackground, this.getVertexBufferObjectManager());
        this.pauseScene.attachChild(pausedSprite);
                /* Makes the paused Game look through. */
        this.pauseScene.setBackgroundEnabled(false);

        final float centerX = (CAMERA_WIDTH - Character.getTexture().getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - Character.getTexture().getHeight()) / 2;

        mScene = new Scene();

        gameField.setScene(mScene);
        gameField.createField();

        mScene.setBackground(new Background(0.17f, 0.61f, 0f));

        character = new Character(centerX, centerY, this.getVertexBufferObjectManager());
        character.setPosition(gameField.getActiveBlock().getX() + (Block.SIZE / 2) - (Character.SIZE_X / 2), gameField.getActiveBlock().getY() + (Block.SIZE / 2) - (Character.SIZE_Y / 2));
        character.setRotation(gameField.getActiveBlock().getOutDirection().getDegree());

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
