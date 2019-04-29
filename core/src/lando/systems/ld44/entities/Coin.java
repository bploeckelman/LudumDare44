package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Coin extends Projectile {
    public float value;

    public Coin(GameScreen screen, Animation<TextureRegion> coinAnimation, float value) {
        super(screen, coinAnimation);
        this.value = value;

        setInitialVelocity(value);
    }

    private void setInitialVelocity(float value) {
        float vx, vy;
        if (value == 0.01) {
            vx = 400;
            vy = 500;
        } else if (value == 0.05) {
            vx = 350;
            vy = 300;
        } else if (value == 0.10) {
            vx = 600;
            vy = 550;
        } else if (value == 0.25) {
            vx = 300;
            vy = 200;
        } else {
            vx = 400;
            vy = 500;
        }
        this.initialVelocity.set(vx, vy);
    }

    @Override
    public void reset() {
        super.reset();
        consuming = false;
    }
}
