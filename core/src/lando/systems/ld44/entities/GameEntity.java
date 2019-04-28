package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class GameEntity {
    public enum Direction {RIGHT, LEFT}
    public enum JumpState {ONGROUND, JUMP, POUND}

    public Assets assets;
    public GameScreen screen;

    public float width;
    public float height;

    public Vector2 position = new Vector2();
    private Vector2 tempPos = new Vector2();
    public Vector2 velocity = new Vector2();

    public Direction direction = Direction.RIGHT;
    public JumpState jumpState = JumpState.ONGROUND;
    public float jumpVelocity = 1000;

    public TextureRegion image;
    public Array<Rectangle> tiles;

    public GameEntity(GameScreen screen){
        this.assets = screen.assets;
        this.screen = screen;
        this.position = new Vector2();
        this.tempPos = new Vector2();
        this.tiles = new Array<Rectangle>();
    }


    public void setImage(TextureRegion image) {
        this.image = image;
        width = image.getRegionWidth();
        height = image.getRegionHeight();
    }

    public void jump() {
        if (jumpState == JumpState.ONGROUND){
            velocity.y = jumpVelocity;
            jumpState = JumpState.JUMP;
        } else if (jumpState == JumpState.JUMP){
            velocity.y = -3000;
            velocity.x = 0;
            jumpState = JumpState.POUND;
        }
    }

    public void update(float dt) {
        if (jumpState != JumpState.ONGROUND) {
            velocity.y -= 3000 * dt;

//            if (position.y < currentY) {
//                jumpState = JumpState.ONGROUND;
//                position.y = currentY;
//                velocity.y = 0;
//            }
        }
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


        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x, tempPos.y, width, height);
            if (entityRect.overlaps(tile)) {
                // Up
                if (velocity.y > 0) {

                } else {
                    if (jumpState == JumpState.POUND) {
                        // TODO groundpound
                    }
                    jumpState = JumpState.ONGROUND;
                    tempPos.y = Math.max(tempPos.y, tile.y + tile.height);
                }
                velocity.y = 0;
            }
        }

        screen.level.rectPool.free(entityRect);
        position.set(tempPos);

    }

    public void render(SpriteBatch batch) {
        if (image != null) {
            float scaleX = (direction == Direction.RIGHT) ? 1 : -1;
            batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, 1, 0);
        }
    }
}
