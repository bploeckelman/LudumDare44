package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.screens.GameScreen;

public class Player extends GameEntity {
    public float horizontalSpeed = 200;

    public Player(GameScreen screen, float x, float y) {
        super(screen);

        this.position.set(x, y);
        setImage(assets.player);
        jumpState = JumpState.JUMP;
    }

    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.add(-horizontalSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.add(horizontalSpeed, 0);
        }
        velocity.x *= .85f;
        velocity.x = MathUtils.clamp(velocity.x, -300, 300);

        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched();
        if (jumpPressed){
            jump();
        }
    }
}
