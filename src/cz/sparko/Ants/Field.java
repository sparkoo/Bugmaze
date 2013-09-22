package cz.sparko.Ants;

import android.widget.Toast;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class Field extends SimpleBaseGameActivity implements IOnSceneTouchListener {
    private static final int CAMERA_WIDTH = 720;
    private static final int CAMERA_HEIGHT = 480;

    private static final int FIELD_SIZE_X = 10;
    private static final int FIELD_SIZE_Y = 6;


    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TiledTextureRegion mAntTextRegion;

    private Scene mScene;

    private Block[][] blocks;
    private Ant ant;
    private Block activeBlock;

    private boolean running = true;

    public Field() {
    }

    public static int getCameraWidth() {
        return CAMERA_WIDTH;
    }

    public static int getCameraHeight() {
        return CAMERA_HEIGHT;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 224, 64, TextureOptions.BILINEAR);
        this.mAntTextRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "ant.png", 0, 0, 1, 1);
        Block.loadResources(this.mBitmapTextureAtlas, this);
        this.mBitmapTextureAtlas.load();
    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        mScene = new Scene();
        mScene.setBackground(new Background(0.22f, 0.22f, 0.22f));
        mScene.setOnSceneTouchListener(this);

        final float centerX = (CAMERA_WIDTH - this.mAntTextRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mAntTextRegion.getHeight()) / 2;
        ant = new Ant(centerX, centerY, this.mAntTextRegion, this.getVertexBufferObjectManager());


        int startX = CAMERA_WIDTH - (FIELD_SIZE_X * Block.SIZE);
        int startY = (CAMERA_HEIGHT - (FIELD_SIZE_Y * Block.SIZE)) / 2;
        blocks = new Block[FIELD_SIZE_X][FIELD_SIZE_Y];
        for (int x = 0; x < FIELD_SIZE_X; x++) {
            for (int y = 0; y < FIELD_SIZE_Y; y++) {
                try {
                    Block nBlock = Block.createRandomBlockFactory(new Coordinate(x, y) ,startX + (x * Block.SIZE), startY + (y * Block.SIZE), this.getVertexBufferObjectManager());
                    blocks[x][y] = nBlock;
                    mScene.attachChild(nBlock);
                    mScene.registerTouchArea(nBlock);
                } catch(NotDefinedBlockException e) {
                    Toast.makeText(this.getBaseContext(), "load failed", Toast.LENGTH_SHORT);
                }
            }
        }
        mScene.setTouchAreaBindingOnActionDownEnabled(true);

        mScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(float pSecondsElapsed) {
                activeBlock = blocks[0][0];
                /*while (false) {
                    Coordinate currentCoordinate = activeBlock.getOutCoordinate();
                    if (!blocks[currentCoordinate.getX()][currentCoordinate.getY()].canGetIn(activeBlock.getCoordinate())) {
                        running = false;
                        reset();
                    }

                }*/
            }

            @Override
            public void reset() {
                System.out.println("game over");
            }
        });
        mScene.attachChild(ant);

        return mScene;
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        System.out.println("touch x: " + pSceneTouchEvent.getX() + "; y: " + pSceneTouchEvent.getY());
        /*if (pSceneTouchEvent.isActionUp())
            ant.registerEntityModifier(new MoveModifier(0.5f, ant.getX(), pSceneTouchEvent.getX(), ant.getY(), pSceneTouchEvent.getY()));*/
        return false;
    }
}
