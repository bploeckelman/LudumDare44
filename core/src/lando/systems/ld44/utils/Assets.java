package lando.systems.ld44.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<TextureAtlas>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<Texture> titleTextureAsset = new AssetDescriptor<Texture>("images/title.png", Texture.class);
    private final AssetDescriptor<Texture> pixelTextureAsset = new AssetDescriptor<Texture>("images/pixel.png", Texture.class);

    private final AssetDescriptor<BitmapFont> pixelFont16Asset = new AssetDescriptor<BitmapFont>("fonts/emulogic-16pt.fnt", BitmapFont.class);

    private final ShaderProgramLoader.ShaderProgramParameter defaultVertParam = new ShaderProgramLoader.ShaderProgramParameter() {{ vertexFile = "shaders/default.vert"; }};

    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public Texture titleTexture;
    public Texture pixelTexture;

    public TextureRegion testTexture;
    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;

    public TextureRegion player;
    public TextureRegion playerShoot;
    public Animation<TextureRegion> playerOpenAnimation;
    public Animation<TextureRegion> pennyAnimation;
    public Animation<TextureRegion> nickelAnimation;
    public Animation<TextureRegion> dimeAnimation;
    public Animation<TextureRegion> pennyPickupAnimation;
    public Animation<TextureRegion> nickelPickupAnimation;
    public Animation<TextureRegion> dimePickupAnimation;
    public Animation<TextureRegion> springAnimationUp;
    public Animation<TextureRegion> springAnimationDown;
    public Animation<TextureRegion> springAnimationLeft;
    public Animation<TextureRegion> springAnimationRight;

    public NinePatch ninePatch;

    public BitmapFont font;
    public BitmapFont fontPixel16;

    public boolean initialized;

    public Assets() {
        this(Loading.SYNC);
    }

    public Assets(Loading loading) {
        // Let us write shitty shader programs
        ShaderProgram.pedantic = false;

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        initialized = false;

        mgr = new AssetManager();
        mgr.load(atlasAsset);
        mgr.load(titleTextureAsset);
        mgr.load(pixelTextureAsset);
        mgr.load(pixelFont16Asset);

        if (loading == Loading.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        // Cache TextureRegions from TextureAtlas in fields for quicker access
        atlas = mgr.get(atlasAsset);
        testTexture = atlas.findRegion("dogsuit");
        whitePixel = atlas.findRegion("white-pixel");
        whiteCircle = atlas.findRegion("white-circle");

        player = atlas.findRegion("purse_image");
        playerShoot = atlas.findRegion("purse_spit");

        Array openPurse = atlas.findRegions("player_open");
        playerOpenAnimation = new Animation<TextureRegion>(0.1f, openPurse, Animation.PlayMode.NORMAL);

        Array pennies = atlas.findRegions("penny_walk");
        Array nickels = atlas.findRegions("nickel_walk");
        Array dimes = atlas.findRegions("dime_walk");
        pennyAnimation = new Animation<TextureRegion>(0.1f, pennies, Animation.PlayMode.LOOP);
        nickelAnimation = new Animation<TextureRegion>(0.1f, nickels, Animation.PlayMode.LOOP);
        dimeAnimation = new Animation<TextureRegion>(0.1f, dimes, Animation.PlayMode.LOOP);

        Array pennyPickup = atlas.findRegions("pickup-penny");
        Array nickelPickup = atlas.findRegions("pickup-nickel");
        Array dimePickup = atlas.findRegions("pickup-dime");
        pennyPickupAnimation = new Animation<TextureRegion>(0.075f, pennyPickup, Animation.PlayMode.LOOP_PINGPONG);
        nickelPickupAnimation = new Animation<TextureRegion>(0.075f, nickelPickup, Animation.PlayMode.LOOP_PINGPONG);
        dimePickupAnimation = new Animation<TextureRegion>(0.075f, dimePickup, Animation.PlayMode.LOOP_PINGPONG);

        Array springUp    = atlas.findRegions("spring-up");
        Array springDown  = atlas.findRegions("spring-down");
        Array springLeft  = atlas.findRegions("spring-left");
        Array springRight = atlas.findRegions("spring-right");
        springAnimationUp    = new Animation<TextureRegion>(0.075f, springUp,    Animation.PlayMode.NORMAL);
        springAnimationDown  = new Animation<TextureRegion>(0.075f, springDown,  Animation.PlayMode.NORMAL);
        springAnimationLeft  = new Animation<TextureRegion>(0.075f, springLeft,  Animation.PlayMode.NORMAL);
        springAnimationRight = new Animation<TextureRegion>(0.075f, springRight, Animation.PlayMode.NORMAL);

        titleTexture = mgr.get(titleTextureAsset);
        pixelTexture = mgr.get(pixelTextureAsset);

        ninePatch = new NinePatch(atlas.findRegion("ninepatch-screws"), 6, 6, 6, 6);

        fontPixel16 = mgr.get(pixelFont16Asset);
        font = fontPixel16;

        return 1f;
    }

    @Override
    public void dispose() {
        mgr.clear();
        font.dispose();
        shapes.dispose();
        batch.dispose();
    }

}
