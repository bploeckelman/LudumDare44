package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class AnimationGameEntity extends GameEntity {

    public Animation<TextureRegion> animation;
    private float anim;

    public AnimationGameEntity(GameScreen screen, Animation<TextureRegion> animation) {
        super(screen);
        setAnimation(animation);
    }

    protected void setAnimation(Animation<TextureRegion> animation) {
        this.setAnimation(animation, -1, -1);
    }

    protected void setAnimation(Animation<TextureRegion> animation, float width, float height) {
        this.animation = animation;
        this.width = (width > 0) ? width : animation.getKeyFrame(0).getRegionWidth();
        this.height = (height > 0) ? height : animation.getKeyFrame(0).getRegionHeight();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        updateDirection();
        anim += dt;
        if (velocity.x == 0) {
            anim = 0;
        }

        image = animation.getKeyFrame(anim, true);
    }

    private void updateDirection() {
        if (velocity.x > 0 && position.x >= 300 || velocity.x < 0 && position.x <= 100) {
            velocity.x = -velocity.x;
            direction = velocity.x > 0 ? Direction.RIGHT : Direction.LEFT;
            jump();
        }
    }
}
