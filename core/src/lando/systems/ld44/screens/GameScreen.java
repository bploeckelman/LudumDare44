package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.Game;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.GroundPound;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.particles.ParticleManager;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Config;
import lando.systems.ld44.utils.Utils;
import lando.systems.ld44.utils.screenshake.ScreenShakeCameraController;
import lando.systems.ld44.world.Level;
import lando.systems.ld44.world.backgrounds.ParallaxBackground;
import lando.systems.ld44.world.backgrounds.TextureRegionParallaxLayer;

public class GameScreen extends BaseScreen {

    public boolean debug = Config.debug;
    public Level level;
    public Player player;
    public Array<GameEntity> gameEntities = new Array<GameEntity>();
    public ScreenShakeCameraController shaker;
    public ParticleManager particleManager;
    public ParallaxBackground background;

    private float cameraHorMargins = 100;
    private float cameraVertMargins = 20;
    private float cameraVertJumpMargin = 150;

    private float tempStateTime = 0f;

    public GameScreen(Game game, Assets assets) {
        super(game, assets);
        shaker = new ScreenShakeCameraController(worldCamera);
        level = new Level("maps/demo.tmx", assets, this);
        player = new Player(this, level.spawnPlayer.pos.x, level.spawnPlayer.pos.y);
        this.particleManager = new ParticleManager(assets);
        TextureRegionParallaxLayer layer = new TextureRegionParallaxLayer(new TextureRegion(assets.arcadeTexture), level.collisionLayer.getHeight() * level.collisionLayer.getTileHeight(), new Vector2(.5f, .9f), Utils.WH.height);
        background = new ParallaxBackground(layer);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        player.update(dt);
        for (int i = gameEntities.size; i > 0; i--) {
            GameEntity ge = gameEntities.get(i - 1);
            ge.update(dt);
            if (ge.remove) {
                gameEntities.removeIndex(i - 1);
            }
        }
        shaker.update(dt);
        level.update(dt);
        particleManager.update(dt);

        handleCameraConstraints();

        tempStateTime += dt;
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(shaker.getCombinedMatrix());
        batch.begin();
        {
            background.draw(shaker.getViewCamera(), batch);
            for(GameEntity ge : gameEntities) {
                ge.render(batch);
            }

            player.render(batch);
            particleManager.render(batch);
        }
        batch.end();

        level.render(shaker.getViewCamera());

        batch.begin();
        {
            level.renderObjects(batch, shaker.getViewCamera());

            // TODO: remove these, they're for testing purposes only
            TextureRegion pennyAnimKeyframe = assets.pennyPickupAnimation.getKeyFrame(tempStateTime);
            TextureRegion nickelAnimKeyframe = assets.nickelPickupAnimation.getKeyFrame(tempStateTime);
            TextureRegion dimeAnimKeyframe = assets.dimePickupAnimation.getKeyFrame(tempStateTime);
            batch.draw(pennyAnimKeyframe,  Level.TILE_SIZE * 10, Level.TILE_SIZE * 3);
            batch.draw(nickelAnimKeyframe, Level.TILE_SIZE * 13, Level.TILE_SIZE * 3);
            batch.draw(dimeAnimKeyframe,   Level.TILE_SIZE * 16, Level.TILE_SIZE * 3);
        }
        batch.end();
    }

    public void handleCameraConstraints() {
        float playerX = player.position.x + player.width / 2f;
        if (playerX < cameraTargetPos.x - cameraHorMargins) cameraTargetPos.x = playerX + cameraHorMargins;
        if (playerX > cameraTargetPos.x + cameraHorMargins) cameraTargetPos.x = playerX - cameraHorMargins;

        float playerY = player.position.y + player.height / 2f;
        if (playerY < cameraTargetPos.y - cameraVertMargins) cameraTargetPos.y = playerY + cameraVertMargins;
        if (player.grounded) {
            if (playerY > cameraTargetPos.y + cameraVertMargins) cameraTargetPos.y = playerY - cameraVertMargins;
        } else {
            if (playerY > cameraTargetPos.y + cameraVertJumpMargin) cameraTargetPos.y = playerY - cameraVertJumpMargin;
        }


        float cameraLeftEdge = worldCamera.viewportWidth / 2f;
        cameraTargetPos.x = MathUtils.clamp(cameraTargetPos.x, cameraLeftEdge, level.collisionLayer.getWidth() * level.collisionLayer.getTileWidth() - cameraLeftEdge);

        float cameraVertEdge = worldCamera.viewportHeight / 2f;
        cameraTargetPos.y = MathUtils.clamp(cameraTargetPos.y, cameraVertEdge, level.collisionLayer.getHeight() * level.collisionLayer.getTileHeight() - cameraVertEdge);

//        targetZoom.setValue(1 + Math.abs(player.velocity.y / 1000f));

        updateCamera();
    }

    public void groundPound(float x, float y, float offset, float distance) {
        gameEntities.add(new GroundPound(this, x, y, offset, distance));
    }
}
