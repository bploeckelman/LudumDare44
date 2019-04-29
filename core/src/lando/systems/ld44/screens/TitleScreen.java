package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.Game;
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
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        stateTime += dt;
        titleKeyFrame = titleAnimation.getKeyFrame(stateTime);

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game, assets), assets.stereoShader, 2f,
                           new CallbackListener() {
                               @Override
                               public void callback() {
                                   assets.unloadTitleAssets();
                               }
                           });
        }

        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudCamera.unproject(mousePos);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            float halfScreenWidth = hudCamera.viewportWidth / 2f;
            batch.draw(background, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.draw(titleKeyFrame, halfScreenWidth - titleKeyFrame.getWidth() / 2f, hudCamera.viewportHeight - titleKeyFrame.getHeight());
            batch.draw(subtitle, halfScreenWidth - subtitle.getWidth() / 2f - 5f, 10f + couch.getHeight() - 35f);
            batch.draw(couch, halfScreenWidth - couch.getWidth() / 2f, 10f);
        }
        batch.end();
    }

}
