package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;

public class TitleScreen extends BaseScreen {

    private Vector3 mousePos;

    public TitleScreen(Game game, Assets assets) {
        super(game, assets);
        this.mousePos = new Vector3();
        Gdx.input.setInputProcessor(this);
        audio.playMusic(Audio.Musics.Title);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
                && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game, assets), assets.stereoShader, 2f);
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
            batch.draw(assets.titleTexture, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
        }
        batch.end();
    }

}
