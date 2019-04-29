package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.world.Spring;

public class Projectile extends AnimationGameEntity {

    public float poundVelocity = 1000;

    private float animTimer;
    private boolean active;

    protected Vector2 initialVelocity = new Vector2(400, 500);
    private float initialYVelocity;

    public Projectile(GameScreen screen, Animation<TextureRegion> animation) {
        super(screen, animation);

        poundable = true;
        this.collisionBoundsOffsets.set(0,0, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void reset() {
        active = true;
    }

    public boolean hasHit(GameEntity entity) {
        return (entity instanceof Projectile) ? false : bounds.overlaps(entity.bounds);
    }

    public void markHit() {
        velocity.set(0, 300);
        active = false;
    }

    public void shoot(float x, float y, float speedX) {
        reset();
        position.set(x, y);

        float vx = ((speedX > 0) ? initialVelocity.x : -initialVelocity.x) + speedX;
        initialYVelocity = initialVelocity.y;

        velocity.set(vx, initialYVelocity);
        screen.add(this);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        animTimer += dt;
        image = animation.getKeyFrame(animTimer, true);

        if (velocity.y == 0 && Math.abs(velocity.x) > 0) {
            initialYVelocity *= 0.75f;
            velocity.y = initialYVelocity;
        }
        // Slow the coin down
        velocity.x *= 0.98f;
        if (MathUtils.isEqual(velocity.x, 0f, 10.0f)) {
            velocity.x = 0f;
            active = false;
        }
    }

    @Override
    public void pound() {
        velocity.set(0, poundVelocity);
        active = false;
    }

    @Override
    public void changeDirection() {
        velocity.x = 0;
        active = false;
    }

    @Override
    public void bounce(float multiplier, Spring.Orientation springOrientation) {
        super.bounce(multiplier, springOrientation);
        if (springOrientation == Spring.Orientation.DOWN) {
            initialYVelocity = velocity.y;
        }
    }
}
