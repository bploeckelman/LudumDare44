package lando.systems.ld44.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class Spring {

    public Vector2 pos;
    public Rectangle bounds;
    public TextureRegion keyframe;
    public Animation<TextureRegion> animation;
    public float stateTime;
    public boolean springing;

    public Spring(float x, float y, Assets assets) {
        this.pos = new Vector2(x, y);
        this.animation = assets.springAnimation;
        this.keyframe = animation.getKeyFrames()[0];
        this.bounds = new Rectangle(pos.x - keyframe.getRegionWidth() / 2f, pos.y,
                                    keyframe.getRegionWidth(), keyframe.getRegionHeight());
        this.stateTime = 0f;
        this.springing = false;
    }

    public void update(float dt) {
        if (springing) {
            stateTime += dt;
            if (stateTime > animation.getAnimationDuration()) {
                stateTime = 0f;
                springing = false;
            }

            keyframe = animation.getKeyFrame(stateTime);
            bounds.height = keyframe.getRegionHeight();
        }
    }

    public void trigger() {
        if (springing) return;
        springing = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
