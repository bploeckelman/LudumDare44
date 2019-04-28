package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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

    private float cameraHorMargins = 100;
    private float cameraVertMargins = 20;

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

        handleCameraConstraints();

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

    public void handleCameraConstraints(){
        float playerX = player.position.x + player.width/2f;
        if (playerX < cameraTargetPos.x - cameraHorMargins) cameraTargetPos.x = playerX + cameraHorMargins;
        if (playerX > cameraTargetPos.x + cameraHorMargins) cameraTargetPos.x = playerX - cameraHorMargins;

        float playerY = player.position.y + player.height/2f;
        if (playerY < cameraTargetPos.y - cameraVertMargins) cameraTargetPos.y = playerY + cameraVertMargins;
        if (player.grounded) {
            if (playerY > cameraTargetPos.y + cameraVertMargins) cameraTargetPos.y = playerY - cameraVertMargins;
        }


        float cameraLeftEdge = worldCamera.viewportWidth/2f;
        cameraTargetPos.x = MathUtils.clamp(cameraTargetPos.x, cameraLeftEdge, level.collisionLayer.getWidth() * level.collisionLayer.getTileWidth() - cameraLeftEdge);

        float cameraVertEdge = worldCamera.viewportHeight/2f;
        cameraTargetPos.y = MathUtils.clamp(cameraTargetPos.y, cameraVertEdge, level.collisionLayer.getHeight()* level.collisionLayer.getTileHeight() - cameraVertEdge);

//        targetZoom.setValue(1 + Math.abs(player.velocity.y / 1000f));

        updateCamera();
    }

}
