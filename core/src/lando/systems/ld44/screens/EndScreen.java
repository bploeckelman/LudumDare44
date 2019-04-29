package lando.systems.ld44.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;

public class EndScreen extends BaseScreen {

    public EndScreen(Game game, Assets assets) {
        super(game, assets);
        game.audio.playMusic(Audio.Musics.EndScreen);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.draw(assets.testTexture, 20, 20, 300, 300);

        batch.end();
    }
}
