package lando.systems.ld44.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.Game;
import lando.systems.ld44.entities.*;
import lando.systems.ld44.particles.ParticleManager;
import lando.systems.ld44.ui.Hud;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.Utils;
import lando.systems.ld44.utils.screenshake.ScreenShakeCameraController;
import lando.systems.ld44.world.Exit;
import lando.systems.ld44.world.Level;
import lando.systems.ld44.world.backgrounds.ParallaxBackground;
import lando.systems.ld44.world.backgrounds.TextureRegionParallaxLayer;

public class GameScreen extends BaseScreen {
    enum LevelIndex {Level1, Level2, Boss}

    public Level level;
    public Player player;
    public Array<GameEntity> gameEntities = new Array<GameEntity>();
    public Boss boss;
    public ScreenShakeCameraController shaker;
    public ParticleManager particleManager;
    public ParallaxBackground background;
    public Hud hud;
    public boolean firstRun;

    private float cameraHorMargins = 100;
    private float cameraVertMargins = 20;
    private float cameraVertJumpMargin = 150;

    private float tempStateTime = 0f;
    public LevelIndex levelIndex;
    public Rectangle tempRect;

    public GameScreen(Game game, Assets assets, LevelIndex levelIndex) {
        super(game, assets);
        this.levelIndex = levelIndex;
        shaker = new ScreenShakeCameraController(worldCamera);
        loadLevel();
        this.particleManager = new ParticleManager(assets);
        TextureRegionParallaxLayer layer;
        TextureRegion region = new TextureRegion(assets.mgr.get(assets.titleCouchWake3TextureAsset));
        layer = new TextureRegionParallaxLayer(region, level.collisionLayer.getHeight() * level.collisionLayer.getTileHeight(), new Vector2(.5f, .9f), Utils.WH.height);
        if (level.isBossLevel) {
            // scoot it to better fit on screen
            layer.setPadBottom(-190f);
            layer.setPadLeft(-region.getRegionWidth() / 2f - 64f);
        }
        background = new ParallaxBackground(layer);
        hud = new Hud(this);
        firstRun = true;
        tempRect = new Rectangle();
    }

    private void loadLevel(){
        switch(levelIndex){
            case Level1:
                level = new Level("maps/level1.tmx", assets, this);
                audio.playMusic(Audio.Musics.Level1);
                break;
            case Level2:
                level = new Level("maps/level2.tmx", assets, this);
                audio.playMusic(Audio.Musics.Level2);
                break;
            case Boss:
                level = new Level("maps/boss-arena.tmx", assets, this);
                audio.playMusic(Audio.Musics.Boss);
                boss = new Boss(this);
                break;
        }
        player = new Player(this, level.spawnPlayer.pos.x, level.spawnPlayer.pos.y);
    }

    public void nextLevel(){
        game.audio.playSound(Audio.Sounds.ChangeLevel);
        switch (levelIndex) {
            case Level1:
                game.setScreen(new GameScreen(game, assets, LevelIndex.Level2), assets.stereoShader, 2f, null);
                break;
            case Level2:
                game.setScreen(new GameScreen(game, assets, LevelIndex.Boss), assets.stereoShader, 2f, null);
                break;
            case Boss:
                break;
        }
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
         && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        //TODO REMOVE THIS
        if (Gdx.app.getType() == Application.ApplicationType.Desktop &&
        Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            nextLevel();
            return;
        }

        handleCameraConstraints();
        shaker.update(dt);

        if (!firstRun && !allowInput) return;
        firstRun = false;

        game.stats.secondsToWin += dt;

        player.update(dt);
        // Find Exits
        for (Exit exit : level.exits){
            if (exit.bounds.overlaps(player.bounds)){
                allowInput = false;
                nextLevel();
                return;
            }
        }

        // shitty collision - checking on projectile, not coin in case we add non coin projectiles
        // probably should make projectile base class and handle if it's active.
        for (GameEntity ge : gameEntities) {
            if (ge instanceof Projectile) {
                Projectile projectile = (Projectile)ge;
                if (!projectile.isActive()) { continue; }
                for (int i = 0; i < gameEntities.size; i++) {
                    GameEntity ge2 = gameEntities.get(i);
                    if (!ge2.isDying() && projectile.hasHit(ge2)) {
                        if (ge2.isStunned()) {
                            ge2.kill();
                        } else {
                            ge2.stun();
                        }
                        projectile.markHit();
                    }
                }
            } else if (ge.hasHit(player, tempRect)) {
                if (player.hurtTime <= 0 &&
                        player.jumpState != GameEntity.JumpState.POUND &&
                        player.groundPoundDelay <= 0) {
                    player.getHurt(tempRect);
                    ge.changeDirection();
                }
            }
        }

        for (int i = gameEntities.size; i > 0; i--) {
            GameEntity ge = gameEntities.get(i - 1);
            ge.update(dt);
            if (ge.remove) {
                gameEntities.removeIndex(i - 1);
            }
        }
        if (boss != null) boss.update(dt);
        level.update(dt);
        particleManager.update(dt);
        hud.update(dt);

        tempStateTime += dt;
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(shaker.getCombinedMatrix());

        level.renderBackground(shaker.getViewCamera());

        batch.begin();
        {
            background.draw(shaker.getViewCamera(), batch);
            for(GameEntity ge : gameEntities) {
                ge.render(batch);
            }
            player.render(batch);
            if (boss != null){
                boss.render(batch);
            }
            particleManager.render(batch);
        }
        batch.end();

        level.renderForeground(shaker.getViewCamera());

        batch.begin();
        {
            level.renderObjects(batch, shaker.getViewCamera());
        }
        batch.end();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            hud.render(batch, hudCamera);
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

    public void spawn(Coin coin) {
        coin.pound();
        add(coin);
    }

    public void add(GameEntity ge) {
        ge.remove = false;
        gameEntities.add(ge);
    }
}
