package lando.systems.ld44.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.Game;
import lando.systems.ld44.accessors.ColorAccessor;
import lando.systems.ld44.accessors.RectangleAccessor;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.CallbackListener;

public class TitleScreen extends BaseScreen {

    private Vector3 mousePos;

    private Texture background;
    private Texture subtitle;
    private Texture couch;
    private Animation<Texture> titleAnimation;
    private Texture titleKeyFrame;
    private float stateTime;

    private Rectangle titleBoundsStart;
    private Rectangle subtitleBoundsStart;
    private Rectangle couchBoundsStart;

    private Rectangle titleBoundsEnd;
    private Rectangle subtitleBoundsEnd;
    private Rectangle couchBoundsEnd;

    private Rectangle titleBounds;
    private Rectangle subtitleBounds;
    private Rectangle couchBounds;

    private Color backgroundColor;
    private MutableFloat subtitleAlpha;
    private boolean tweenComplete;

    public TitleScreen(Game game, Assets assets) {
        super(game, assets);
        this.mousePos = new Vector3();
        Gdx.input.setInputProcessor(this);
        audio.playMusic(Audio.Musics.Title);

        background = assets.mgr.get(assets.titleBackgroundTextureAsset);
        subtitle   = assets.mgr.get(assets.titleSubtitleTextureAsset);
        couch      = assets.mgr.get(assets.titleCouchTextureAsset);
        background = assets.mgr.get(assets.titleBackgroundTextureAsset);
        titleAnimation = assets.titleAnimation;
        titleKeyFrame = titleAnimation.getKeyFrame(0f);
        stateTime = 0f;

        float halfScreenWidth = hudCamera.viewportWidth / 2f;
        titleBoundsEnd = new Rectangle(halfScreenWidth - titleKeyFrame.getWidth() / 2f, hudCamera.viewportHeight - titleKeyFrame.getHeight(), titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsEnd = new Rectangle(halfScreenWidth - subtitle.getWidth() / 2f - 5f, 10f + couch.getHeight() - 35f, subtitle.getWidth(), subtitle.getHeight());
        couchBoundsEnd = new Rectangle(halfScreenWidth - couch.getWidth() / 2f, 10f, couch.getWidth(), couch.getHeight());

        titleBoundsStart = new Rectangle(titleBoundsEnd.x, hudCamera.viewportHeight + 10f, titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsStart = new Rectangle(0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
        couchBoundsStart = new Rectangle(-couch.getWidth() - 10f, couchBoundsEnd.y, couch.getWidth(), couch.getHeight());

        titleBounds = new Rectangle();
        subtitleBounds = new Rectangle();
        couchBounds = new Rectangle();

        backgroundColor = new Color();
        subtitleAlpha = new MutableFloat(0f);

        startTweens();
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        stateTime += dt;
        titleKeyFrame = titleAnimation.getKeyFrame(stateTime);

        if (Gdx.input.justTouched() && tweenComplete) {
            game.setScreen(new GameScreen(game, assets), assets.stereoShader, 2f,
                           new CallbackListener() {
                               @Override
                               public void callback() {
                                   assets.unloadTitleAssets();
                               }
                           });
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(backgroundColor);
            batch.draw(background, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);

            batch.draw(titleKeyFrame, titleBounds.x, titleBounds.y, titleBounds.width, titleBounds.height);

            batch.setColor(1f, 1f, 1f, subtitleAlpha.floatValue());
            batch.draw(subtitle, subtitleBounds.x, subtitleBounds.y, subtitleBounds.width, subtitleBounds.height);
            batch.setColor(Color.WHITE);

            batch.draw(couch, couchBounds.x, couchBounds.y, couchBounds.width, couchBounds.height);

            // TODO: draw characters
            // TODO: if tweenComplete, show 'touch to play' or whatever
        }
        batch.end();
    }

    private void startTweens() {
        tweenComplete = false;

        titleBounds.set(titleBoundsStart);
        subtitleBounds.set(subtitleBoundsStart);
        couchBounds.set(couchBoundsStart);

        backgroundColor.set(0f, 0f, 0f, 1f);
        subtitleAlpha.setValue(0f);

        Timeline.createSequence()
                .push(// fade in background color from black
                        Tween.to(backgroundColor, ColorAccessor.RGB, 0.75f)
                             .target(1f, 1f, 1f)
                )
                .push(// slide in couch
                        Tween.to(couchBounds, RectangleAccessor.XYWH, 1.0f)
                             .target(couchBoundsEnd.x, couchBoundsEnd.y, couchBoundsEnd.width, couchBoundsEnd.height)
                             .ease(Elastic.OUT)
                )
                .push(// bounce in title
                        Tween.to(titleBounds, RectangleAccessor.XYWH, 1.5f)
                             .target(titleBoundsEnd.x, titleBoundsEnd.y, titleBoundsEnd.width, titleBoundsEnd.height)
                             .ease(Bounce.OUT)
                )
                .push(// squanch in subtitle
                        Timeline.createParallel()
                        .push(// Alpha from transparent to opaque
                                Tween.to(subtitleAlpha, -1, 0.75f).target(1f)
                        )
                        .push(
                                Tween.to(subtitleBounds, RectangleAccessor.XYWH, 0.8f)
                                     .target(subtitleBoundsEnd.x, subtitleBoundsEnd.y, subtitleBoundsEnd.width, subtitleBoundsEnd.height)
                                     .ease(Back.OUT)
                        )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        tweenComplete = true;
                    }
                })
                // TODO: characters bouncing around & couchy mouth talking
                .start(game.tween);
    }

}
