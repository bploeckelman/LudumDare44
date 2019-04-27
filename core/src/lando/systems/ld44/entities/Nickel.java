package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Nickel extends AnimationGameEntity {

    public Nickel(GameScreen screen) {
        super(screen, screen.assets.nickelAnimation);

        // temp
        velocity.x = 50;
        position.x = 100;
        position.y = 100;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        updateDirection();
    }

    private void updateDirection() {
        if (velocity.x > 0 && position.x >= 300 || velocity.x < 0 && position.x <= 100) {
            velocity.x = -velocity.x;
            direction = velocity.x > 0 ? Direction.RIGHT : Direction.LEFT;
            jump();
        }
    }
}
