package lando.systems.ld44.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class Tack {
    public enum Facing { UP, DOWN, LEFT, RIGHT }

    public Vector2 pos;
    public Rectangle bounds;
    public Facing facing;
    public TextureRegion keyframe;
    public Animation<TextureRegion> animation;
    public float stateTime;

    public Tack(float x, float y, Facing facing, Assets assets) {
        this.pos = new Vector2(x, y);
        this.facing = facing;
        switch (facing) {
            default:
            case UP:    this.animation = assets.tackAnimationUp;    break;
            case DOWN:  this.animation = assets.tackAnimationDown;  break;
            case LEFT:  this.animation = assets.tackAnimationLeft;  break;
            case RIGHT: this.animation = assets.tackAnimationRight; break;
        }
        this.keyframe = animation.getKeyFrames()[0];
        this.bounds = new Rectangle(pos.x, pos.y, keyframe.getRegionWidth(), keyframe.getRegionHeight());
        setBoundsBasedOnOrientation();
        this.stateTime = 0f;
    }

    public void update(float dt) {
        stateTime += dt;
        keyframe = animation.getKeyFrame(stateTime);
        setBoundsBasedOnOrientation();
    }

    public void render(SpriteBatch batch) {
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void setBoundsBasedOnOrientation() {
        switch (facing) {
            case UP:
                bounds.height = keyframe.getRegionHeight();
                bounds.x = pos.x + Level.TILE_SIZE / 2f - bounds.width / 2f;
                break;
            case DOWN:
                bounds.height = keyframe.getRegionHeight();
                bounds.x = pos.x + Level.TILE_SIZE / 2f - bounds.width / 2f;
                bounds.y = pos.y + Level.TILE_SIZE - bounds.height;
                break;
            case LEFT:
                bounds.width = keyframe.getRegionWidth();
                bounds.x = pos.x + Level.TILE_SIZE - bounds.width;
                bounds.y = pos.y + Level.TILE_SIZE / 2f - bounds.height / 2f;
                break;
            case RIGHT:
                bounds.width = keyframe.getRegionWidth();
                bounds.y = pos.y + Level.TILE_SIZE / 2f - bounds.height / 2f;
                break;
        }
    }

}
