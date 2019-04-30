package lando.systems.ld44.screens;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.Game;
import lando.systems.ld44.accessors.ColorAccessor;
import lando.systems.ld44.accessors.RectangleAccessor;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.CallbackListener;

import java.sql.Time;

public class TitleScreen extends BaseScreen {

    enum State {
          intro
        , dialog_story
        , dialog_controls
        , dialog_gameplay
        , outro
        , screen_transition
    }
    private State state = State.intro;
    private boolean stateTweening = false;

    private final String storyText = "Hello [YELLOW]Pursey[],\n\nI'm [CORAL]King Sofa[]\nof the [ORANGE]Sofa Kingdom[].\n\nSomeone has stuffed their grubby hands down in our cushions in\nflagrant violation of the\n[RED]No Touchy Act[]\nof 1997.\n\n....\n\nNow get inside me, [YELLOW]Pursey[], and collect coins to fight your way to the hands!";
    private final String storyShadowText = "Hello Pursey,\n\nI'm King Sofa\nof the Sofa Kingdom.\n\nSomeone has stuffed their grubby hands down in our cushions in\nflagrant violation of the\nNo Touchy Act\nof 1997.\n\n....\n\nNow get inside me, Pursey, and collect coins to fight your way to the hand!";
    private final String controlsText = "CONTROLS:\n\n\n\n([GREEN]MOVE[])\n\n\n\n\n\n([YELLOW]JUMP[]/[ORANGE]POUND[])\n\nPress once\nto ([YELLOW]JUMP[])\n\nPress in air\nto ([ORANGE]POUND[])\n\n\n\n\n\n\n([MAGENTA]SHOOT COINS[])";
    private final String controlsShadowText = "CONTROLS:\n\n\n\n(MOVE)\n\n\n\n\n\n(JUMP/POUND)\n\nPress once\nto (JUMP)\n\nPress in air\nto (POUND)\n\n\n\n\n\n\n(SHOOT COINS)";
    private final String gameplayText = "GAMEPLAY:\n\n\n[PINK]stun enemies[]:\n\n[ORANGE]POUND[] nearby\nor [RED]SHOOT[] to\n[PINK]stun enemies[]\n\n\n[RED]kill enemies[]:\n\n[RED]SHOOT[] to\nkill [PINK]stunned enemies[]\n\n\n[YELLOW]collect coins[]:\n\n[ORANGE]POUND[] to launch coins in the air,\n\nthen [GREEN]MOVE[] underneath to [YELLOW]collect bouncing coins[]";
    private final String gameplayShadowText = "GAMEPLAY:\n\n\nstun enemies:\n\nPOUND nearby\nor SHOOT to\nstun enemies\n\n\nkill enemies:\n\nSHOOT to\nkill stunned enemies\n\n\ncollect coins:\n\nPOUND to launch coins in the air,\n\nthen MOVE underneath to collect bouncing coins";

    private GlyphLayout layout;

    private Texture background;
    private Texture subtitle;
    private Texture titleKeyFrame;
    private Texture couchKeyFrame;
    private Animation<Texture> titleAnimation;
    private Animation<Texture> couchAnimation;
    private Animation<Texture> couchWakeAnimation;
    private Animation<Texture> couchTalkAnimation;
    private Texture couchClosed;
    private Texture couchDone;
    private TextureRegion coinPurseKeyFrame;
    private Animation<TextureRegion> coinPurseAnimation;
    private float stateTime;
    private float couchyTalkStateTime;

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
    private Rectangle coinPurseBounds;

    private Rectangle dialogBounds;
    private Rectangle dialogBoundsMin;
    private Rectangle dialogBoundsMax;
    private MutableFloat dialogAlpha;

    private TextureRegion controlsMoveTexture;
    private TextureRegion controlsJumpTexture;
    private TextureRegion controlsShootTexture;
    private Animation<TextureRegion> controlsShootAnimation;
    private Rectangle controlsMoveBounds;
    private Rectangle controlsJumpBounds;
    private Rectangle controlsShootBounds;

    private Color backgroundColor;
    private MutableFloat subtitleAlpha;
    private MutableFloat clickTextBounceY;
    private MutableFloat coinPurseAlpha;

    private boolean introTweenComplete;
    private boolean storyTweenComplete;
    private boolean controlsTweenComplete;
    private boolean gameplayTweenComplete;
    private boolean outroTweenComplete;
    private boolean couchyIsTalking;
    private boolean couchyIsWaking;
    private boolean drawDialogText;
    private boolean drawCoinPurse;
    private boolean isTransitioningToGameScreen;

    private float clickToPlayYBaseline;
    private String clickToPlay = "Click to Play";

    public TitleScreen(Game game, Assets assets) {
        super(game, assets);
        layout = new GlyphLayout();
        Gdx.input.setInputProcessor(this);
        audio.playMusic(Audio.Musics.Title);

        background = assets.mgr.get(assets.titleBackgroundTextureAsset);
        subtitle   = assets.mgr.get(assets.titleSubtitleTextureAsset);

        titleAnimation = assets.titleAnimation;
        titleKeyFrame = titleAnimation.getKeyFrame(0f);
        couchTalkAnimation = assets.couchAnimation;
        couchWakeAnimation = assets.couchWakeAnimation;
        couchAnimation = assets.couchAnimation;
        couchClosed = assets.mgr.get(assets.titleCouchTextureAsset);
        couchDone = assets.mgr.get(assets.titleCouchTalk7TextureAsset);
        couchKeyFrame = couchAnimation.getKeyFrame(0f);
        stateTime = 0f;
        couchyTalkStateTime = 0f;

        coinPurseAnimation = assets.playerAnimation;
        coinPurseKeyFrame = coinPurseAnimation.getKeyFrame(0f);

        float halfScreenWidth = hudCamera.viewportWidth / 2f;
        titleBoundsEnd = new Rectangle(halfScreenWidth - titleKeyFrame.getWidth() / 2f, hudCamera.viewportHeight - titleKeyFrame.getHeight(), titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsEnd = new Rectangle(halfScreenWidth - subtitle.getWidth() / 2f - 5f, 10f + couchKeyFrame.getHeight() - 35f, subtitle.getWidth(), subtitle.getHeight());
        couchBoundsEnd = new Rectangle(halfScreenWidth - couchKeyFrame.getWidth() / 2f, 10f, couchKeyFrame.getWidth(), couchKeyFrame.getHeight());

        float offscreenAmount = (1f / 6f) * couchKeyFrame.getWidth();
        float couchStoryWidth = hudCamera.viewportWidth + 2f * offscreenAmount;
        float couchStoryHeight = couchKeyFrame.getHeight() * (couchStoryWidth / couchKeyFrame.getWidth());
        couchBoundsStory = new Rectangle(hudCamera.viewportWidth / 2f - couchStoryWidth / 2f, couchBoundsEnd.y, couchStoryWidth, couchStoryHeight);

        titleBoundsStart = new Rectangle(titleBoundsEnd.x, hudCamera.viewportHeight + 10f, titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBoundsStart = new Rectangle(0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
        couchBoundsStart = new Rectangle(-couchKeyFrame.getWidth() - 10f, couchBoundsEnd.y, couchKeyFrame.getWidth(), couchKeyFrame.getHeight());

        titleBounds = new Rectangle();
        subtitleBounds = new Rectangle();
        couchBounds = new Rectangle();
        coinPurseBounds = new Rectangle(100f, -coinPurseKeyFrame.getRegionHeight() - 10f, coinPurseKeyFrame.getRegionWidth(), coinPurseKeyFrame.getRegionHeight());

        float margin = 10f;
        float dialogWidth = (1f / 3f) * hudCamera.viewportWidth - 2f * margin;
        float dialogHeight = hudCamera.viewportHeight - 2f * margin;
        dialogBoundsMax = new Rectangle(hudCamera.viewportWidth - margin - dialogWidth, margin, dialogWidth, dialogHeight);
        dialogBoundsMin = new Rectangle(dialogBoundsMax.x + dialogWidth / 2f, dialogBoundsMax.y + dialogHeight / 2f, 0f, 0f);
        dialogBounds = new Rectangle(dialogBoundsMin);
        dialogAlpha = new MutableFloat(0f);
        drawDialogText = false;

        controlsMoveTexture  = assets.atlas.findRegion("control-icon-move");
        controlsJumpTexture  = assets.atlas.findRegion("control-icon-jump");
        Array shootFrames = assets.atlas.findRegions("control-icon-shoot");
        controlsShootAnimation = new Animation<TextureRegion>(0.15f, shootFrames, Animation.PlayMode.LOOP);
        controlsShootTexture = controlsShootAnimation.getKeyFrame(0f);
        controlsMoveBounds  = new Rectangle(dialogBoundsMax.x + margin + dialogBoundsMax.width / 2f - controlsMoveTexture.getRegionWidth() / 2f - margin,
                                            dialogBoundsMax.y + dialogBoundsMax.height - margin - controlsMoveTexture.getRegionHeight() - 60f,
                                            controlsMoveTexture.getRegionWidth(), controlsMoveTexture.getRegionHeight());
        controlsJumpBounds  = new Rectangle(dialogBoundsMax.x + margin + dialogBoundsMax.width / 2f - controlsJumpTexture.getRegionWidth() / 2f - margin,
                                            dialogBoundsMax.y + dialogBoundsMax.height - margin - 210f,
                                            controlsJumpTexture.getRegionWidth(), controlsJumpTexture.getRegionHeight());
        controlsShootBounds = new Rectangle(dialogBoundsMax.x + margin + dialogBoundsMax.width / 2f - controlsShootTexture.getRegionWidth() / 2f - margin,
                                            dialogBoundsMax.y + dialogBoundsMax.height - margin - 500f,
                                            controlsShootTexture.getRegionWidth(), controlsShootTexture.getRegionHeight());

        backgroundColor = new Color();
        subtitleAlpha = new MutableFloat(0f);

        clickToPlayYBaseline = hudCamera.viewportHeight / 2f - 50f;
        clickTextBounceY = new MutableFloat(clickToPlayYBaseline);
        coinPurseAlpha = new MutableFloat(1f);

        isTransitioningToGameScreen = false;
        couchyIsTalking = false;
        couchyIsWaking = false;

        startIntroTween();
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        stateTime += dt;
        titleKeyFrame = titleAnimation.getKeyFrame(stateTime);
        controlsShootTexture = controlsShootAnimation.getKeyFrame(stateTime);

        if (couchyIsTalking || couchyIsWaking) {
            couchyTalkStateTime += dt;
            couchKeyFrame = couchAnimation.getKeyFrame(couchyTalkStateTime);
            if (couchyIsWaking && couchyTalkStateTime >= couchWakeAnimation.getAnimationDuration()) {
                couchyIsWaking = false;
                couchAnimation = couchTalkAnimation;
            }
        } else {
            couchKeyFrame = (state == State.intro) ? couchClosed : couchDone;
        }

        switch (state) {
            case intro: {
                if (!stateTweening && introTweenComplete && Gdx.input.justTouched()) {
                    startStorySetupTween();
                }
            }
            break;
            case dialog_story: {
                if (!stateTweening && storyTweenComplete && Gdx.input.justTouched()) {
                    startDialogControlsTween();
                }
            }
            break;
            case dialog_controls: {
                if (!stateTweening && controlsTweenComplete && Gdx.input.justTouched()) {
                    startDialogGameplayTween();
                }
            }
            break;
            case dialog_gameplay: {
                if (!stateTweening && gameplayTweenComplete && Gdx.input.justTouched()) {
                    startOutroTween();
                }
            }
            break;
            case outro: {
                if (!stateTweening && !isTransitioningToGameScreen && outroTweenComplete && Gdx.input.justTouched()) {
//                    Gdx.app.log("STATE", "triggered gamescreen transition");
                    isTransitioningToGameScreen = true;
                    state = State.screen_transition;
                    game.setScreen(new GameScreen(game, assets, GameScreen.LevelIndex.Level1), assets.stereoShader, 2f,
                                   new CallbackListener() {
                                       @Override
                                       public void callback() {
                                           assets.unloadTitleAssets();
                                       }
                                   });
                }
            }
            break;
            case screen_transition: {/*nop*/} break;
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

            if (drawCoinPurse) {
                batch.setColor(1f, 1f, 1f, coinPurseAlpha.floatValue());
                batch.draw(coinPurseKeyFrame, coinPurseBounds.x, coinPurseBounds.y, coinPurseBounds.width, coinPurseBounds.height);
                batch.setColor(Color.WHITE);
            }

            // Draw dialog panel and (optionally) text
            if (state == State.dialog_controls || state == State.dialog_story || state == State.dialog_gameplay) {
                batch.setColor(0.4f, 0.4f, 0.4f, dialogAlpha.floatValue());
                batch.draw(assets.whitePixel, dialogBounds.x, dialogBounds.y, dialogBounds.width, dialogBounds.height);
                batch.setColor(Color.WHITE);
                assets.ninePatch.draw(batch, dialogBounds.x, dialogBounds.y, dialogBounds.width, dialogBounds.height);

                if (drawDialogText) {
                    if (state == State.dialog_controls) {
                        batch.draw(controlsMoveTexture, controlsMoveBounds.x, controlsMoveBounds.y, controlsMoveBounds.width, controlsMoveBounds.height);
                        batch.draw(controlsJumpTexture, controlsJumpBounds.x, controlsJumpBounds.y, controlsJumpBounds.width, controlsJumpBounds.height);
                        batch.draw(controlsShootTexture, controlsShootBounds.x, controlsShootBounds.y, controlsShootBounds.width, controlsShootBounds.height);
                    }

                    String text = "wtf";
                    String shadowText = "wtf";
                    float scale = 1f;
                    int align = Align.left;
                    if      (state == State.dialog_story)    { text = storyText;    shadowText = storyShadowText;    scale = 0.9f; }
                    else if (state == State.dialog_controls) { text = controlsText; shadowText = controlsShadowText; align = Align.center; }
                    else if (state == State.dialog_gameplay) { text = gameplayText; shadowText = gameplayShadowText; scale = 0.9f; }
                    float margin = 20f;
                    float scaleX = assets.font.getData().scaleX;
                    float scaleY = assets.font.getData().scaleY;
                    assets.font.getData().setScale(scale);
                    layout.setText(assets.font, shadowText, Color.BLACK, dialogBounds.width - 2f * margin, align, true);
                    assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, dialogBounds.y + dialogBounds.height - margin - 2f);
                    layout.setText(assets.font, text, Color.WHITE, dialogBounds.width - 2f * margin, align, true);
                    assets.font.draw(batch, layout, dialogBounds.x + margin, dialogBounds.y + dialogBounds.height - margin);
                    assets.font.getData().setScale(scaleX, scaleY);
                }
            }

            // Draw 'Click to Play' text
            if (outroTweenComplete) {
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

    private void startIntroTween() {
//        Gdx.app.log("STATE", "startIntroTween: prevState = '" + state.name() + "'");
        state = State.intro;
        stateTweening = true;

        introTweenComplete = false;
        storyTweenComplete = false;
        controlsTweenComplete = false;
        gameplayTweenComplete = false;
        outroTweenComplete = false;
        drawCoinPurse = false;
        drawDialogText = false;

        // Set initial values for things we're tweening here
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
                        Tween.to(couchBounds, RectangleAccessor.XYWH, 0.8f)
                             .target(couchBoundsEnd.x, couchBoundsEnd.y, couchBoundsEnd.width, couchBoundsEnd.height)
                             .ease(Back.OUT)
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
                        stateTweening = false;
                        introTweenComplete = true;
                    }
                })
                .start(game.tween);
    }

    private void startStorySetupTween() {
//        Gdx.app.log("STATE", "startStorySetupTween: prevState = '" + state.name() + "'");
        stateTweening = true;
        drawCoinPurse = true;

        couchAnimation = couchWakeAnimation;
        couchyIsWaking = true;

        float offscreenDuration = 0.75f;
        Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                        .push(// Title moves offscreen
                                Tween.to(titleBounds, RectangleAccessor.XYWH, offscreenDuration)
                                     .target(titleBoundsStart.x, titleBoundsStart.y, titleBoundsStart.width, titleBoundsStart.height)
                                     .ease(Quint.IN)
                        )
                        .push(// Subtitle fades offscreen
                                Tween.to(subtitleAlpha, -1, offscreenDuration)
                                     .target(0f)
                        )
                )
                .push(
                        // Couch expands to fill viewport
                        Tween.to(couchBounds, RectangleAccessor.XYWH, 1.5f)
                             .target(couchBoundsStory.x, couchBoundsStory.y, couchBoundsStory.width, couchBoundsStory.height)
                )
                .push(
                        // Character bounces onto the couch
                        Timeline.createParallel()
                        .push(
                                Tween.to(coinPurseBounds, RectangleAccessor.XY, 0.5f)
                                     .target(80f, 190f)
                                     .ease(Bounce.OUT)
                        )
                )
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                couchyIsTalking = true;
                                couchyIsWaking = false;
                                couchAnimation = couchTalkAnimation;
                                stateTweening = false;
                                startDialogStoryTween();
                            }
                        })
                )
                .start(game.tween);
    }

    private void startDialogStoryTween() {
//        Gdx.app.log("STATE", "startDialogStoryTween: prevState = '" + state.name() + "'");
        state = State.dialog_story;
        stateTweening = true;
        drawDialogText = false;
        drawCoinPurse = true;
        dialogBounds.set(dialogBoundsMin);

        float growDuration = 0.2f;
        Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                        .push(// make dialog visible
                                Tween.to(dialogAlpha, -1, growDuration).target(0.8f)
                        )
                        .push(// grow the dialog
                                Tween.to(dialogBounds, RectangleAccessor.XYWH, growDuration)
                                     .target(dialogBoundsMax.x, dialogBoundsMax.y, dialogBoundsMax.width, dialogBoundsMax.height)
                        )
                )
                .push(
                        // set the 'draw text now' flag
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                stateTweening = false;
                                drawDialogText = true;
                                storyTweenComplete = true;
                            }
                        })
                )
                .start(game.tween);
    }

    private void startDialogControlsTween() {
//        Gdx.app.log("STATE", "startDialogControlsTween: prevState = '" + state.name() + "'");
        state = State.dialog_controls;
        stateTweening = true;
        drawDialogText = false;
        drawCoinPurse = true;

        float shrinkDuration = 0.1f;
        float growDuration = 0.2f;
        Timeline.createSequence()
                .push(
                        // Hide dialog
                        Timeline.createParallel()
                                .push(// make dialog invisible
                                      Tween.to(dialogAlpha, -1, shrinkDuration).target(0f)
                                )
                                .push(// shrink the dialog
                                      Tween.to(dialogBounds, RectangleAccessor.XYWH, shrinkDuration)
                                           .target(dialogBoundsMin.x, dialogBoundsMin.y, dialogBoundsMin.width, dialogBoundsMin.height)
                                )
                )
                .push(
                        // Show dialog
                        Timeline.createParallel()
                                .push(// make dialog visible
                                      Tween.to(dialogAlpha, -1, growDuration).target(0.8f)
                                )
                                .push(// grow the dialog
                                      Tween.to(dialogBounds, RectangleAccessor.XYWH, growDuration)
                                           .target(dialogBoundsMax.x, dialogBoundsMax.y, dialogBoundsMax.width, dialogBoundsMax.height)
                                )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        stateTweening = false;
                        drawDialogText = true;
                        controlsTweenComplete = true;
                    }
                })
                .start(game.tween);
    }

    private void startDialogGameplayTween() {
//        Gdx.app.log("STATE", "startDialogGameplayTween: prevState = '" + state.name() + "'");
        state = State.dialog_gameplay;
        stateTweening = true;
        drawDialogText = false;
        drawCoinPurse = true;

        float shrinkDuration = 0.1f;
        float growDuration = 0.2f;
        Timeline.createSequence()
                .push(
                        // Hide dialog
                        Timeline.createParallel()
                                .push(// make dialog invisible
                                      Tween.to(dialogAlpha, -1, shrinkDuration).target(0f)
                                )
                                .push(// shrink the dialog
                                      Tween.to(dialogBounds, RectangleAccessor.XYWH, shrinkDuration)
                                           .target(dialogBoundsMin.x, dialogBoundsMin.y, dialogBoundsMin.width, dialogBoundsMin.height)
                                )
                )
                .push(
                        // Show dialog
                        Timeline.createParallel()
                                .push(// make dialog visible
                                      Tween.to(dialogAlpha, -1, growDuration).target(0.8f)
                                )
                                .push(// grow the dialog
                                      Tween.to(dialogBounds, RectangleAccessor.XYWH, growDuration)
                                           .target(dialogBoundsMax.x, dialogBoundsMax.y, dialogBoundsMax.width, dialogBoundsMax.height)
                                )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        stateTweening = false;
                        drawDialogText = true;
                        gameplayTweenComplete = true;
                    }
                })
                .start(game.tween);
    }

    private void startOutroTween() {
//        Gdx.app.log("STATE", "startOutroTween: prevState = '" + state.name() + "'");
        state = State.outro;
        stateTweening = true;
        drawDialogText = false;
        couchyIsTalking = false;
        coinPurseAlpha.setValue(1f);

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

        float shrinkDuration = 0.1f;
        Timeline.createSequence()
                .push( // Hide dialog
                        Timeline.createParallel()
                                .push(// make dialog invisible
                                      Tween.to(dialogAlpha, -1, shrinkDuration).target(0f)
                                )
                                .push(// shrink the dialog
                                      Tween.to(dialogBounds, RectangleAccessor.XYWH, shrinkDuration)
                                           .target(dialogBoundsMin.x, dialogBoundsMin.y, dialogBoundsMin.width, dialogBoundsMin.height)
                                )
                )
                .pushPause(0.2f)
                .push(// Character dives into couch
                        Timeline.createSequence()
                        .push(
                                // Move up to top of arc
                                Tween.to(coinPurseBounds, RectangleAccessor.XY, 1.0f)
                                     .target(hudCamera.viewportWidth / 2f - coinPurseBounds.width / 2f, couchBounds.y + couchBounds.height)
                        )
                        .push(// Character disappears
                                Timeline.createParallel()
                                .push(// Character fades out
                                        Tween.to(coinPurseAlpha, -1, 0.5f).target(0f).ease(Quint.IN)
                                )
                                .push(// Squanch into the couch
                                        Tween.to(coinPurseBounds, RectangleAccessor.XYWH, 0.5f)
                                             .target(hudCamera.viewportWidth / 2f, couchBounds.y + couchBounds.height * (1f / 3f), 0f, 0f)
                                )
                        )
                )
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        stateTweening = false;
                        storyTweenComplete = true;
                        outroTweenComplete = true;
                        drawCoinPurse = false;
                    }
                })
                .start(game.tween);
    }

}
