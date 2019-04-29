package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Coin extends Projectile {
    public float value;

    public Coin(GameScreen screen, Animation<TextureRegion> coinAnimation, float value) {
        super(screen, coinAnimation);
        this.value = value;
    }

    @Override
    public void reset() {
        super.reset();
        consuming = false;
    }
}
