package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.utils.Assets;

public class Nickel extends GameEntity {

    public Animation<TextureRegion> animation;
    private float anim;

    public Nickel(Assets assets) {
        super(assets);
        setAnimation(assets.nickelAnimation);
        velocity.x = 50;
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
        image = animation.getKeyFrame(anim, true);
    }

    private void updateDirection() {
        if (velocity.x > 0 && position.x >= 100 || velocity.x < 0 && position.x <= 0) {
            velocity.x = -velocity.x;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float scaleX = (velocity.x >= 0) ? 1 : -1;
        if (image != null) {
            batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, 1, 0);
        }
    }

}
