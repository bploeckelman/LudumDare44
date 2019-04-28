package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.screens.GameScreen;

public class Player extends GameEntity {
    public float horizontalSpeed = 100;

    public Player(GameScreen screen, float x, float y) {
        super(screen);

        this.position.set(x, y);
        setImage(assets.player);
        jumpState = JumpState.JUMP;
    }

    public void update(float dt) {
        super.update(dt);
        if (groundPoundDelay > 0) return;
        if (jumpState != JumpState.POUND) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocity.add(-horizontalSpeed, 0);
                direction = Direction.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocity.add(horizontalSpeed, 0);
                direction = Direction.RIGHT;
            }
        }
        velocity.x *= .85f;
        velocity.x = MathUtils.clamp(velocity.x, -300, 300);

        if (Math.abs(velocity.x) < 20) velocity.x = 0;

        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched();
        if (jumpPressed){
            jump();
        }
    }
}
