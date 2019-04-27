package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld44.Game;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.utils.Assets;

public class GameScreen extends BaseScreen {
    Player player;

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
        player = new Player(assets, 30, 100);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        player.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            player.render(batch);
        }
        batch.end();
    }

}
