package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.Game;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Nickel;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.utils.Assets;

public class GameScreen extends BaseScreen {
    Player player;

    Array<GameEntity> gameEntities = new Array<GameEntity>();

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
        player = new Player(assets, 30, 100);
        gameEntities.add(new Nickel(assets));
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        player.update(dt);
        for (GameEntity ge : gameEntities) {
            ge.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            for(GameEntity ge : gameEntities) {
                ge.render(batch);
            }
            player.render(batch);
        }
        batch.end();
    }

}
