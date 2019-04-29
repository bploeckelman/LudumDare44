package lando.systems.ld44.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld44.Game;
import lando.systems.ld44.accessors.ColorAccessor;
import lando.systems.ld44.accessors.RectangleAccessor;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.CallbackListener;

public class TitleScreen extends BaseScreen {

    private GlyphLayout layout;

    private Texture background;
    private Texture subtitle;
    private Texture titleKeyFrame;
    private Texture couchKeyFrame;
    private Animation<Texture> titleAnimation;
    private Animation<Texture> couchAnimation;
    private float stateTime;

    private Rectangle titleBoundsStart;
    private Rectangle subtitleBoundsStart;
    private Rectangle couchBoundsStart;
    private Rectangle couchBoundsStory;

    private Rectangle titleBoundsEnd;
    private Rectangle subtitleBoundsEnd;
    private Rectangle couchBoundsEnd;

    private Rectangle titleBounds;
    private Rectangle subtitleBounds;
    private Rectangle couchBounds;

    private Color backgroundColor;
    private MutableFloat subtitleAlpha;
    private MutableFloat clickTextBounceY;

    private boolean startupTweenComplete;
    private boolean storyTweenComplete;
    private boolean isTransitioning;

    private float clickToPlayYBaseline;
    private String clickToPlay = "Click to Play";

    public TitleScreen(Game game, Assets assets) {
        super(game, assets);
        layout = new GlyphLayout();
        Gdx.input.setInputProcessor(this);
        audio.playMusic(Audio.Musics.Title);

        background = assets.mgr.get(assets.titleBackgroundTextureAsset);
        subtitle   = assets.mgr.get(assets.titleSubtitleTextureAsset);
        background = assets.mgr.get(assets.titleBackgroundTextureAsset);

        titleAnimation = assets.titleAnimation;
        titleKeyFrame = titleAnimation.getKeyFrame(0f);
        couchAnimation = assets.couchAnimation;
        couchKeyFrame = couchAnimation.getKeyFrame(0f);
        stateTime = 0f;

        float halfScreenWidth = hudCamera.viewportWidth / 2f;
        titleBoundsEnd = new Rectangle(halfScreenWidth - titleKeyFrame.getWidth() / 2f, hudCamera.viewportHeight - titleKeyFrame.getHeight(), titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsEnd = new Rectangle(halfScreenWidth - subtitle.getWidth() / 2f - 5f, 10f + couchKeyFrame.getHeight() - 35f, subtitle.getWidth(), subtitle.getHeight());
        couchBoundsEnd = new Rectangle(halfScreenWidth - couchKeyFrame.getWidth() / 2f, 10f, couchKeyFrame.getWidth(), couchKeyFrame.getHeight());

        float offscreenAmount = (1f / 5f) * couchKeyFrame.getWidth();
        float couchStoryWidth = hudCamera.viewportWidth + 2f * offscreenAmount;
        float couchStoryHeight = couchKeyFrame.getHeight() * (couchStoryWidth / couchKeyFrame.getWidth());
        couchBoundsStory = new Rectangle(hudCamera.viewportWidth / 2f - couchStoryWidth / 2f, couchBoundsEnd.y, couchStoryWidth, couchStoryHeight);

        titleBoundsStart = new Rectangle(titleBoundsEnd.x, hudCamera.viewportHeight + 10f, titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsStart = new Rectangle(0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
        couchBoundsStart = new Rectangle(-couchKeyFrame.getWidth() - 10f, couchBoundsEnd.y, couchKeyFrame.getWidth(), couchKeyFrame.getHeight());

        titleBounds = new Rectangle();
        subtitleBounds = new Rectangle();
        couchBounds = new Rectangle();

        backgroundColor = new Color();
        subtitleAlpha = new MutableFloat(0f);

        clickToPlayYBaseline = hudCamera.viewportHeight / 2f - 50f;
        clickTextBounceY = new MutableFloat(clickToPlayYBaseline);

        isTransitioning = false;

        startStartupTween();
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        stateTime += dt;
        titleKeyFrame = titleAnimation.getKeyFrame(stateTime);

        if (startupTweenComplete && Gdx.input.justTouched()) {
            startStoryTween();

            if (storyTweenComplete && !isTransitioning) {
                isTransitioning = true;
                game.setScreen(new GameScreen(game, assets), assets.stereoShader, 2f,
                               new CallbackListener() {
                                   @Override
                                   public void callback() {
                                       assets.unloadTitleAssets();
                                   }
                               });
            }
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

            batch.draw(couchKeyFrame, couchBounds.x, couchBounds.y, couchBounds.width, couchBounds.height);

            // TODO: draw characters

            if (storyTweenComplete) {
                float scaleX = assets.font.getData().scaleX;
                float scaleY = assets.font.getData().scaleY;
                assets.font.getData().setScale(3.5f);
                layout.setText(assets.font, clickToPlay, Color.BLACK, hudCamera.viewportWidth, Align.center, false);
                assets.font.draw(batch, layout, 3f, clickTextBounceY.floatValue() - 3f);
                layout.setText(assets.font, clickToPlay, Color.YELLOW, hudCamera.viewportWidth, Align.center, false);
                assets.font.draw(batch, layout, 0f, clickTextBounceY.floatValue());
                assets.font.getData().setScale(scaleX, scaleY);
            }
        }
        batch.end();
    }

    private void startStartupTween() {
        Timeline.createSequence()
                .push(
                        Tween.to(clickTextBounceY, -1, 0.5f)
                             .target(clickToPlayYBaseline + 200f)
                             .ease(Quint.OUT)
                )
                .push(
                        Tween.to(clickTextBounceY, -1, 0.5f)
                             .target(clickToPlayYBaseline)
                             .ease(Bounce.OUT)
                )
                .repeat(-1, 0.5f)
                .start(game.tween);

        startupTweenComplete = false;
        storyTweenComplete = false;

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
                        startupTweenComplete = true;
                    }
                })
                .start(game.tween);
    }

    private void startStoryTween() {
        float offscreenDuration = 0.75f;
        Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                        .push(// Title offscreen (bounce in)
                                Tween.to(titleBounds, RectangleAccessor.XYWH, offscreenDuration)
                                     .target(titleBoundsStart.x, titleBoundsStart.y, titleBoundsStart.width, titleBoundsStart.height)
                                     .ease(Back.IN)
                        )
                        .push(// Subtitle offscreen (fade alpha)
                                Tween.to(subtitleAlpha, -1, offscreenDuration)
                                     .target(0f)
                        )
                )
                .push(
                        // Expand couch
                        Tween.to(couchBounds, RectangleAccessor.XYWH, 1.5f)
                             .target(couchBoundsStory.x, couchBoundsStory.y, couchBoundsStory.width, couchBoundsStory.height)
                )
//                .push(
//                        // Start bouncing characters and showing text
//                        Timeline.createParallel()
//                        .push(
//
//                        )
//                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        storyTweenComplete = true;
                    }
                })
                .start(game.tween);

    }

}
