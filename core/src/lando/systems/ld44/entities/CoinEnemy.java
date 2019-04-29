package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.world.Level;

public class CoinEnemy extends Enemy {

    private Animation<TextureRegion> coin;
    public float value;

    public CoinEnemy(GameScreen screen, Animation<TextureRegion> enemy, Animation<TextureRegion> coin, float movement, float value) {
        super(screen, enemy, movement);

        this.coin = coin;
        this.value = value;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        Coin spawnCoin = new Coin(screen, coin, value);
        float x = position.x + width / 2 - spawnCoin.width / 2;
        spawnCoin.position.set(x, position.y);
        screen.spawn(spawnCoin);
    }

    @Override
    public void updateEntity(float dt) {
        keepOnPlatform(dt);
    }
}
