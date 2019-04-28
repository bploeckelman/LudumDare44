package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class GameEntity {
    public enum Direction {RIGHT, LEFT}
    public enum JumpState {NONE, JUMP, POUND}

    public Assets assets;
    public GameScreen screen;

    public float width;
    public float height;

    public Vector2 position = new Vector2();
    private Vector2 tempPos = new Vector2();
    public Vector2 velocity = new Vector2();

    public Direction direction = Direction.RIGHT;
    public JumpState jumpState = JumpState.NONE;
    public boolean grounded;
    public float jumpVelocity = 800;
    public float gravity = 2000;
    public float groundPoundDelay = 0;

    public boolean poundable;
    public boolean stunned;
    public boolean remove;

    public TextureRegion image;
    public Array<Rectangle> tiles;
    public Rectangle bounds = new Rectangle();

    public GameEntity(GameScreen screen){
        this.assets = screen.assets;
        this.screen = screen;
        this.tiles = new Array<Rectangle>();
        grounded = true;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
        width = image.getRegionWidth();
        height = image.getRegionHeight();
    }

    public void jump() {
        jump(1f);
    }

    public void jump(float velocityMultiplier) {
        if (grounded){
            velocity.y = jumpVelocity * velocityMultiplier;
            jumpState = JumpState.JUMP;
        } else if (jumpState == JumpState.JUMP){
            velocity.y = -1000;
            velocity.x = 0;
            jumpState = JumpState.POUND;
        }
    }

    public void stun() {
        stunned = true;
        velocity.x = 0;
        position.y += 20;
    }

    public void update(float dt) {
        groundPoundDelay = Math.max(groundPoundDelay -dt, 0);
        velocity.y -= gravity * dt;

        tempPos.set(position);
        tempPos.add(velocity.x * dt, velocity.y * dt);

        Rectangle entityRect = screen.level.rectPool.obtain();
        entityRect.set(tempPos.x, position.y, width, height);
        float startX, startY, endX, endY;

        // Check Horizontal
        if (velocity.x > 0) {
            startX = endX = entityRect.x + entityRect.width;
        } else {
            startX = endX = entityRect.x;
        }
        startY = entityRect.y;
        endY = entityRect.y + entityRect.height;
        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x, position.y, width, height);
            if (entityRect.overlaps(tile)){
                tempPos.x = position.x;
//                velocity.x *= -.5f;
                break;
            }
        }

        entityRect.set(tempPos.x, tempPos.y, width, height);

        // Check vertical
        if (velocity.y > 0){ // above?
            startY = endY = entityRect.y + entityRect.height;
        } else {
            startY = position.y;
            endY = entityRect.y;
        }
        startX = entityRect.x;
        endX = entityRect.x + entityRect.width;

        grounded = false;
        boolean pounded = false;
        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x, tempPos.y, width, height);
            if (entityRect.overlaps(tile)) {
                // Up
                if (velocity.y > 0) {
                    tempPos.y = Math.min(tempPos.y, tile.y - height);
                } else {
                    tempPos.y = Math.max(tempPos.y, tile.y + tile.height);
                    if (jumpState == JumpState.POUND) {
                        groundPoundDelay = .5f;
                        velocity.x = 0;
                        pounded = true;
                    }
                    jumpState = JumpState.NONE;
                    grounded = true;
                }
                velocity.y = 0;
            }
        }

        screen.level.handleObjectInteractions(this);

        screen.level.rectPool.free(entityRect);
        position.set(tempPos);
        if (pounded) {
            groundPound(tempPos);
        }
        bounds.set(position.x, position.y, width, height);
    }

    protected void groundPound(Vector2 poundPosition) {
        screen.shaker.addDamage(.5f);
    }

    public void render(SpriteBatch batch) {
        if (image != null) {
            float scaleX = (direction == Direction.RIGHT) ? 1 : -1;
            float scaleY = 1;
            if (!grounded){
                if (jumpState == JumpState.POUND) {
                    scaleX *= .6f;
                    scaleY = 1.3f;
                } else {
                    scaleX *= .85f;
                    scaleY = 1.15f;
                }
            }
            if (groundPoundDelay > 0){
                scaleX *= 1 + groundPoundDelay/2f;
                scaleY = 1 - groundPoundDelay/3f;
            }
            batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, scaleY, 0);
            assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
