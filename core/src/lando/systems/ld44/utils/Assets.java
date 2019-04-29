package lando.systems.ld44.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld44.entities.AnimationGameEntity;

public class Assets implements Disposable {

    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<TextureAtlas>("images/sprites.atlas", TextureAtlas.class);
    public final AssetDescriptor<Texture> titleBackgroundTextureAsset = new AssetDescriptor<Texture>("images/title/title-background.png", Texture.class);
    public final AssetDescriptor<Texture> titleCouchTextureAsset = new AssetDescriptor<Texture>("images/title/title-couch.png", Texture.class);
    public final AssetDescriptor<Texture> titleSubtitleTextureAsset = new AssetDescriptor<Texture>("images/title/title-subtitle.png", Texture.class);
    public final AssetDescriptor<Texture> titleTitle0TextureAsset = new AssetDescriptor<Texture>("images/title/title-title_0.png", Texture.class);
    public final AssetDescriptor<Texture> titleTitle1TextureAsset = new AssetDescriptor<Texture>("images/title/title-title_1.png", Texture.class);
    public final AssetDescriptor<Texture> titleTitle2TextureAsset = new AssetDescriptor<Texture>("images/title/title-title_2.png", Texture.class);
    public final AssetDescriptor<Texture> titleTitle3TextureAsset = new AssetDescriptor<Texture>("images/title/title-title_3.png", Texture.class);

    private final AssetDescriptor<Texture> arcadeTextureAsset = new AssetDescriptor<Texture>("images/arcade.png", Texture.class);
    private final AssetDescriptor<Texture> couchTextureAsset = new AssetDescriptor<Texture>("images/couch.png", Texture.class);
    private final AssetDescriptor<Texture> pixelTextureAsset = new AssetDescriptor<Texture>("images/pixel.png", Texture.class);

    private final AssetDescriptor<BitmapFont> pixelFont16Asset = new AssetDescriptor<BitmapFont>("fonts/emulogic-16pt.fnt", BitmapFont.class);

    private final ShaderProgramLoader.ShaderProgramParameter defaultVertParam = new ShaderProgramLoader.ShaderProgramParameter() {{ vertexFile = "shaders/default.vert"; }};

    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public Texture arcadeTexture;
    public Texture couchTexture;
    public Texture pixelTexture;

    public TextureRegion testTexture;
    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion handFlat;
    public TextureRegion handFist;
    public TextureRegion handPointer;

    public TextureRegion player;
    public Animation<Texture> titleAnimation;
    public Animation<Texture> couchAnimation;
    public Animation<TextureRegion> playerShootAnimation;
    public Animation<TextureRegion> playerOpenAnimation;
    public Animation<TextureRegion> pennyAnimation;
    public Animation<TextureRegion> nickelAnimation;
    public Animation<TextureRegion> dimeAnimation;
    public Animation<TextureRegion> quarterAnimation;
    public Animation<TextureRegion> chickenAnimation;
    public Animation<TextureRegion> dustBunnyAnimation;
    public Animation<TextureRegion> pennyPickupAnimation;
    public Animation<TextureRegion> nickelPickupAnimation;
    public Animation<TextureRegion> dimePickupAnimation;
    public Animation<TextureRegion> quarterPickupAnimation;
    public Animation<TextureRegion> springAnimationUp;
    public Animation<TextureRegion> springAnimationDown;
    public Animation<TextureRegion> springAnimationLeft;
    public Animation<TextureRegion> springAnimationRight;
    public Animation<TextureRegion> tackAnimationUp;
    public Animation<TextureRegion> tackAnimationDown;
    public Animation<TextureRegion> tackAnimationLeft;
    public Animation<TextureRegion> tackAnimationRight;
    public Animation<TextureRegion> stunStarsAnimation;
    public Animation<TextureRegion> explosionAnimation;

    public NinePatch ninePatch;

    public BitmapFont font;
    public BitmapFont fontPixel16;

    public boolean initialized;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram stereoShader;
    public ShaderProgram circleCropShader;

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
        mgr.load(titleBackgroundTextureAsset);
        mgr.load(titleCouchTextureAsset);
        mgr.load(titleSubtitleTextureAsset);
        mgr.load(titleTitle0TextureAsset);
        mgr.load(titleTitle1TextureAsset);
        mgr.load(titleTitle2TextureAsset);
        mgr.load(titleTitle3TextureAsset);
        mgr.load(arcadeTextureAsset);
        mgr.load(couchTextureAsset);
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

        handFist = atlas.findRegion("hand-fist");
        handFlat = atlas.findRegion("hand-flat");
        handPointer = atlas.findRegion("hand-point");

        player = atlas.findRegion("purse_image");

        Array<Texture> titleFrames = new Array<Texture>();
        titleFrames.addAll(
                mgr.get(titleTitle0TextureAsset),
                mgr.get(titleTitle1TextureAsset),
                mgr.get(titleTitle2TextureAsset),
                mgr.get(titleTitle3TextureAsset)
        );
        titleAnimation = new Animation<Texture>(0.15f, titleFrames, Animation.PlayMode.LOOP_PINGPONG);

        Array<Texture> couchFrames = new Array<Texture>();
        couchFrames.addAll(
                mgr.get(titleCouchTextureAsset)
        );
        couchAnimation = new Animation<Texture>(0.2f, couchFrames, Animation.PlayMode.LOOP);

        Array playerShoot = atlas.findRegions("purse_spit");
        playerShootAnimation = new Animation<TextureRegion>(0.3f, playerShoot, Animation.PlayMode.NORMAL);

        Array openPurse = atlas.findRegions("purse_open");
        playerOpenAnimation = new Animation<TextureRegion>(0.1f, openPurse, Animation.PlayMode.NORMAL);

        Array pennies = atlas.findRegions("penny_walk");
        Array nickels = atlas.findRegions("nickel_walk");
        Array dimes = atlas.findRegions("dime_walk");
        Array quarters = atlas.findRegions("quarter_walk");
        pennyAnimation = new Animation<TextureRegion>(0.1f, pennies, Animation.PlayMode.LOOP);
        nickelAnimation = new Animation<TextureRegion>(0.1f, nickels, Animation.PlayMode.LOOP);
        dimeAnimation = new Animation<TextureRegion>(0.1f, dimes, Animation.PlayMode.LOOP);
        quarterAnimation = new Animation<TextureRegion>(0.1f, quarters, Animation.PlayMode.LOOP);

        Array pennyPickup = atlas.findRegions("pickup-penny");
        Array nickelPickup = atlas.findRegions("pickup-nickel");
        Array dimePickup = atlas.findRegions("pickup-dime");
        Array quarterPickup = atlas.findRegions("pickup-quarter");
        pennyPickupAnimation = new Animation<TextureRegion>(0.075f, pennyPickup, Animation.PlayMode.LOOP_PINGPONG);
        nickelPickupAnimation = new Animation<TextureRegion>(0.075f, nickelPickup, Animation.PlayMode.LOOP_PINGPONG);
        dimePickupAnimation = new Animation<TextureRegion>(0.075f, dimePickup, Animation.PlayMode.LOOP_PINGPONG);
        quarterPickupAnimation = new Animation<TextureRegion>(0.075f, quarterPickup, Animation.PlayMode.LOOP_PINGPONG);

        Array chickenFrames = atlas.findRegions("chicken");
        chickenAnimation = new Animation<TextureRegion>(0.2f, chickenFrames, Animation.PlayMode.LOOP);

        Array dustBunnyFrames = atlas.findRegions("bunny");
        dustBunnyAnimation = new Animation<TextureRegion>(0.2f, dustBunnyFrames, Animation.PlayMode.LOOP);

        Array springUp    = atlas.findRegions("spring-up");
        Array springDown  = atlas.findRegions("spring-down");
        Array springLeft  = atlas.findRegions("spring-left");
        Array springRight = atlas.findRegions("spring-right");
        springAnimationUp    = new Animation<TextureRegion>(0.075f, springUp,    Animation.PlayMode.NORMAL);
        springAnimationDown  = new Animation<TextureRegion>(0.075f, springDown,  Animation.PlayMode.NORMAL);
        springAnimationLeft  = new Animation<TextureRegion>(0.075f, springLeft,  Animation.PlayMode.NORMAL);
        springAnimationRight = new Animation<TextureRegion>(0.075f, springRight, Animation.PlayMode.NORMAL);

        Array tackUp    = atlas.findRegions("tack-up");
        Array tackDown  = atlas.findRegions("tack-down");
        Array tackLeft  = atlas.findRegions("tack-left");
        Array tackRight = atlas.findRegions("tack-right");
        tackAnimationUp    = new Animation<TextureRegion>(0.1f, tackUp,    Animation.PlayMode.LOOP);
        tackAnimationDown  = new Animation<TextureRegion>(0.1f, tackDown,  Animation.PlayMode.LOOP);
        tackAnimationLeft  = new Animation<TextureRegion>(0.1f, tackLeft,  Animation.PlayMode.LOOP);
        tackAnimationRight = new Animation<TextureRegion>(0.1f, tackRight, Animation.PlayMode.LOOP);

        Array stunStars = atlas.findRegions("stars");
        stunStarsAnimation = new Animation<TextureRegion>(.1f, stunStars, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> explosion = atlas.findRegions("explosion");
        explosionAnimation = new Animation<TextureRegion>(.1f, explosion, Animation.PlayMode.NORMAL);

        arcadeTexture = mgr.get(arcadeTextureAsset);
        arcadeTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        couchTexture = mgr.get(couchTextureAsset);
        couchTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixelTexture = mgr.get(pixelTextureAsset);

        ninePatch = new NinePatch(atlas.findRegion("ninepatch-screws"), 6, 6, 6, 6);

        fontPixel16 = mgr.get(pixelFont16Asset);
        font = fontPixel16;

        randomTransitions = new Array<ShaderProgram>();
        blindsShader = loadShader("shaders/default.vert", "shaders/blinds.frag");
        fadeShader = loadShader("shaders/default.vert", "shaders/dissolve.frag");
        radialShader = loadShader("shaders/default.vert", "shaders/radial.frag");
        doomShader = loadShader("shaders/default.vert", "shaders/doomdrip.frag");
        pizelizeShader = loadShader("shaders/default.vert", "shaders/pixelize.frag");
        doorwayShader = loadShader("shaders/default.vert", "shaders/doorway.frag");
        crosshatchShader = loadShader("shaders/default.vert", "shaders/crosshatch.frag");
        rippleShader = loadShader("shaders/default.vert", "shaders/ripple.frag");
        heartShader = loadShader("shaders/default.vert", "shaders/heart.frag");
        stereoShader = loadShader("shaders/default.vert", "shaders/stereo.frag");
        circleCropShader = loadShader("shaders/default.vert", "shaders/circlecrop.frag");



//        randomTransitions.add(blindsShader);
        randomTransitions.add(fadeShader);
        randomTransitions.add(radialShader);
        randomTransitions.add(rippleShader);
//        randomTransitions.add(pizelizeShader);

        return 1f;
    }

    public void unloadTitleAssets() {
        mgr.unload(titleBackgroundTextureAsset.fileName);
        mgr.unload(titleCouchTextureAsset.fileName);
        mgr.unload(titleSubtitleTextureAsset.fileName);
        mgr.unload(titleTitle0TextureAsset.fileName);
        mgr.unload(titleTitle1TextureAsset.fileName);
        mgr.unload(titleTitle2TextureAsset.fileName);
        mgr.unload(titleTitle3TextureAsset.fileName);
        // TODO: unload couch talking frames
    }

    @Override
    public void dispose() {
        mgr.clear();
        font.dispose();
        shapes.dispose();
        batch.dispose();
    }

    private static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(vertSourcePath),
                Gdx.files.internal(fragSourcePath));

        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("LoadShader", "compilation failed:\n" + shaderProgram.getLog());
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + shaderProgram.getLog());
        } else {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log:\n" + shaderProgram.getLog());
        }

        return shaderProgram;
    }

}
