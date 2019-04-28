package lando.systems.ld44.entities;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.screens.GameScreen;

public class Bunny extends Enemy {

    private float randomizeThreshold;
    private float randomizeTimer = 0f;

    public Bunny(GameScreen screen) {
        super(screen, screen.assets.dustBunnyAnimation, 30f);
        randomizeThreshold = MathUtils.random(2.5f, 3.5f);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        randomizeTimer += dt;
        if (randomizeTimer >= randomizeThreshold) {
            randomizeTimer -= randomizeThreshold;

            Direction direction = this.direction;
            float changeDirChance = MathUtils.random(0.4f, 0.7f);
            boolean doChangeDirection = MathUtils.randomBoolean(changeDirChance);
            if (doChangeDirection) {
                direction = MathUtils.randomBoolean() ? Direction.LEFT : Direction.RIGHT;
            }

            float movement = 100f;
            float newSpeed = MathUtils.random(movement - (1f / 4f) * movement,
                                              movement + (1f / 4f) * movement);

            setDirection(direction, newSpeed);

            float jumpChance = MathUtils.random(0.5f, 0.8f);
            boolean doJump = MathUtils.randomBoolean(jumpChance);
            if (doJump) {
                jump();
            }
        }
    }

}
