package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.screens.GameScreen;

public class Projectile extends AnimationGameEntity {

    public float poundVelocity = 1000;

    private float animTimer;
    private boolean active;

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
        pound(); // or remove
    }


    @Override
    public void update(float dt) {
        super.update(dt);
        animTimer += dt;
        image = animation.getKeyFrame(animTimer, true);

        // Slow the coin down
        velocity.x *= 0.975f;
        if (MathUtils.isEqual(velocity.x, 0f, 1.0f)) {
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
}
