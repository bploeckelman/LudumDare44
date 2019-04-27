package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;

public class GameScreen extends BaseScreen {

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            batch.draw(assets.testTexture,
                       worldCamera.viewportWidth / 2f - assets.testTexture.getRegionWidth() / 2f,
                       worldCamera.viewportHeight / 2f - assets.testTexture.getRegionHeight() / 2f);
        }
        batch.end();
    }

}
