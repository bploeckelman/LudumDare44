package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
    public Vector2 velocity = new Vector2();

    public Direction direction = Direction.RIGHT;
    public JumpState jumpState = JumpState.ONGROUND;
    public float jumpVelocity = 1000;
    private float currentY;

    public TextureRegion image;

    public GameEntity(GameScreen screen){
        this.assets = screen.assets;
        this.screen = screen;
    }

    public void jump() {
        if (jumpState == JumpState.ONGROUND){
            velocity.y = jumpVelocity;
            jumpState = JumpState.JUMP;
            currentY = position.y;
        } else if (jumpState == JumpState.JUMP){
            velocity.y = -3000;
            velocity.x = 0;
            jumpState = JumpState.POUND;
        }
    }

    public void update(float dt) {
        if (jumpState != JumpState.ONGROUND) {
            velocity.y -= 3000 * dt;

            if (position.y < currentY) {
                jumpState = JumpState.ONGROUND;
                position.y = currentY;
                velocity.y = 0;
            }
        }
        position.add(velocity.x * dt, velocity.y * dt);
    }



    public void render(SpriteBatch batch) {
        if (image != null) {
            float scaleX = (direction == Direction.RIGHT) ? 1 : -1;
            batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, 1, 0);
        }
    }
}
