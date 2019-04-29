package lando.systems.ld44.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.world.Level;

public class Bunny extends Enemy {

    private float randomizeThreshold;
    private float randomizeTimer = 0f;

    public Bunny(GameScreen screen) {
        super(screen, screen.assets.dustBunnyAnimation, 30f);
        randomizeThreshold = MathUtils.random(2.5f, 3.5f);
        bounceVelocity = 500f;
        jumpVelocity = 500f;
        this.collisionBoundsOffsets.set(0, 0, width, height);
    }

    @Override
    public void updateEntity(float dt) {
        // Check for about to fall off a platform
        if (grounded && Math.abs(velocity.x) > 0f) {
            Level level = screen.level;

            // get cells in front of and under entity
            int y = (int) (position.y / level.collisionLayer.getTileHeight()) - 1;
            int x = 0;
            float sign = Math.signum(velocity.x);
            if (sign == -1f) { // moving left
                float centerX = position.x + collisionBoundsOffsets.x + collisionBoundsOffsets.width / 2f;
                x = (int) (centerX / level.collisionLayer.getTileWidth()) - 1;
            } else if (sign == 1f) { // moving right
                float centerX = position.x + collisionBoundsOffsets.x + collisionBoundsOffsets.width / 2f;
                x = (int) (centerX / level.collisionLayer.getTileWidth()) + 1;
            }
            TiledMapTileLayer.Cell cell = level.collisionLayer.getCell(x, y);
            if (cell == null) {
                changeDirection();
            }
        }

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
