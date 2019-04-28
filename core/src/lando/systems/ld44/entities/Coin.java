package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Coin extends AnimationGameEntity {
    public float value;

    private float anim;
    public float poundVelocity = 1000;

    public Coin(GameScreen screen, Animation<TextureRegion> coinAnimation, float value) {
        super(screen, coinAnimation);

        poundable = true;
        this.value = value;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        anim += dt;
        image = animation.getKeyFrame(anim, true);
    }

    @Override
    public void pound() {
        // float dx = -100 + ((float)Math.random() * 200);
        velocity.set(0, poundVelocity);
    }
}
