package lando.systems.ld44.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class Spring {
    public enum Orientation { UP, DOWN, LEFT, RIGHT }

    public Vector2 pos;
    public Rectangle bounds;
    public TextureRegion keyframe;
    public Animation<TextureRegion> animation;
    public Orientation orientation;
    public float stateTime;
    public boolean springing;

    public Spring(float x, float y, Orientation orientation, Assets assets) {
        this.pos = new Vector2(x, y);
        this.orientation = orientation;
        switch (orientation) {
            default:
            case UP:    this.animation = assets.springAnimationUp;    break;
            case DOWN:  this.animation = assets.springAnimationDown;  break;
            case LEFT:  this.animation = assets.springAnimationLeft;  break;
            case RIGHT: this.animation = assets.springAnimationRight; break;
        }
        this.keyframe = animation.getKeyFrames()[0];
        this.bounds = new Rectangle(pos.x, pos.y, keyframe.getRegionWidth(), keyframe.getRegionHeight());
        setBoundsBasedOnOrientation();
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
            setBoundsBasedOnOrientation();
        }
    }

    public void trigger() {
        if (springing) return;
        springing = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void setBoundsBasedOnOrientation() {
        switch (orientation) {
            case UP:
                bounds.height = keyframe.getRegionHeight();
                bounds.y = pos.y + Level.TILE_SIZE - bounds.height;
                break;
            case DOWN:
                bounds.height = keyframe.getRegionHeight();
                break;
            case LEFT:
                bounds.width = keyframe.getRegionWidth();
                break;
            case RIGHT:
                bounds.width = keyframe.getRegionWidth();
                bounds.x = pos.x + Level.TILE_SIZE - bounds.width;
                break;
        }
    }

}
