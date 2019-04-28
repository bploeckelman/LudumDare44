package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.Game;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.screenshake.ScreenShakeCameraController;
import lando.systems.ld44.world.Level;

public class GameScreen extends BaseScreen {

    public Level level;
    public Player player;
    public Array<GameEntity> gameEntities = new Array<GameEntity>();
    public ScreenShakeCameraController shaker;

    private float cameraMargins = 100;

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
        shaker = new ScreenShakeCameraController(worldCamera);
        level = new Level("maps/demo.tmx", assets, this);
        player = new Player(this, level.spawnPlayer.pos.x, level.spawnPlayer.pos.y);

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

//        // TEMP
//        float speed = 350f;
//        if      (Gdx.input.isKeyPressed(Input.Keys.LEFT))  worldCamera.translate(-speed * dt, 0f);
//        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) worldCamera.translate( speed * dt, 0f);
//        if      (Gdx.input.isKeyPressed(Input.Keys.DOWN))  worldCamera.translate(0f, -speed * dt);
//        else if (Gdx.input.isKeyPressed(Input.Keys.UP))    worldCamera.translate(0f,  speed * dt);
        float playerX = player.position.x + player.width/2f;
        if (playerX < cameraTargetPos.x - cameraMargins) cameraTargetPos.x = playerX + cameraMargins;
        if (playerX > cameraTargetPos.x + cameraMargins) cameraTargetPos.x = playerX - cameraMargins;

        float playerY = player.position.y + player.height/2f;
        if (playerY < cameraTargetPos.y - cameraMargins) cameraTargetPos.y = playerY + cameraMargins;
        if (playerY > cameraTargetPos.y + cameraMargins) cameraTargetPos.y = playerY - cameraMargins;

        float cameraLeftEdge = worldCamera.viewportWidth/2f;
        cameraTargetPos.x = MathUtils.clamp(cameraTargetPos.x, cameraLeftEdge, level.collisionLayer.getWidth() * level.collisionLayer.getTileWidth() - cameraLeftEdge);

        float cameraVertEdge = worldCamera.viewportHeight/2f;
        cameraTargetPos.y = MathUtils.clamp(cameraTargetPos.y, cameraVertEdge, level.collisionLayer.getHeight()* level.collisionLayer.getTileHeight() - cameraVertEdge);

        updateCamera();
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
