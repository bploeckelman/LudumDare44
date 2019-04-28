package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;

public class Enemy extends AnimationGameEntity {

    private Animation<TextureRegion> coin;
    public float value;

    public Enemy(GameScreen screen, Animation<TextureRegion> live, float movement) {
        this(screen, live, null, movement, 0f);
    }

    public Enemy(GameScreen screen, Animation<TextureRegion> live, Animation<TextureRegion> coin, float movement, float value) {
        super(screen, live);

        this.coin = coin;
        this.value = value;

        poundable = true;
        initialVelocity = movement + ((int)Math.random()*movement/2);
    }

    public void spawn(Direction direction, float x, float y) {
        position.set(x, y);
        setDirection(direction);
    }

    public void kill() {
        remove = true;
        if (coin != null) {
            Coin spawnCoin = new Coin(screen, coin, value);
            float x = position.x + width / 2 - spawnCoin.width / 2;
            spawnCoin.position.set(x, position.y);
            screen.spawn(spawnCoin);
        }
    }

    @Override
    public void setDirection(Direction direction) {
        setDirection(direction, initialVelocity);
    }
}
