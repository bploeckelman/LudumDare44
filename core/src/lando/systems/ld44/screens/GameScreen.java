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
import lando.systems.ld44.utils.screenshake.ScreenShakeCameraController;
import lando.systems.ld44.world.Level;

public class GameScreen extends BaseScreen {

    Player player;
    Level level;

    Array<GameEntity> gameEntities = new Array<GameEntity>();

    public ScreenShakeCameraController shaker;

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
        player = new Player(this, assets, 30, 100);
        gameEntities.add(new Nickel(assets));
        shaker = new ScreenShakeCameraController(worldCamera);
        level = new Level("maps/demo.tmx", assets);
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
        shaker.update(dt);
        level.update(dt);

        // TEMP
        float speed = 350f;
        if      (Gdx.input.isKeyPressed(Input.Keys.LEFT))  worldCamera.translate(-speed * dt, 0f);
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) worldCamera.translate( speed * dt, 0f);
        if      (Gdx.input.isKeyPressed(Input.Keys.DOWN))  worldCamera.translate(0f, -speed * dt);
        else if (Gdx.input.isKeyPressed(Input.Keys.UP))    worldCamera.translate(0f,  speed * dt);
        worldCamera.update();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.begin();
        {
            for(GameEntity ge : gameEntities) {
                ge.render(batch);
            }
            player.render(batch);
        }
        batch.end();

        level.render(shaker.getViewCamera());
    }

}
