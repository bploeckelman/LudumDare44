package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class Player {

    public enum JumpState {ONGROUND, JUMP, POUND}

    public Vector2 position;
    public Vector2 velocity;
    public float horizontalSpeed = 200;
    public float jumpVelocity = 1000;
    public float width = 90;
    public float height = 60;
    public Assets assets;
    public JumpState jumpState;
    public GameScreen screen;

    public Player(GameScreen screen, Assets assets, float x, float y){
        this.screen = screen;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.assets = assets;
        this.jumpState = JumpState.ONGROUND;
    }

    public void update(float dt) {
        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched();

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.add(-horizontalSpeed, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.add(horizontalSpeed, 0);
        }
        velocity.x *= .85f;
        velocity.x = MathUtils.clamp(velocity.x, -300, 300);
//        velocity.clamp(-200, 200);

        if  (jumpPressed){
            if (jumpState == JumpState.ONGROUND){
                velocity.y = jumpVelocity;
                jumpState = JumpState.JUMP;
            } else if (jumpState == JumpState.JUMP){
                velocity.y = - 3000;
                velocity.x = 0;
                jumpState = JumpState.POUND;
            }
        }
        velocity.y -= 3000 * dt;

        position.add(velocity.x * dt, velocity.y * dt);


        // TODO make this based on geometry of the world
        if (position.y <= 0) {
            if (jumpState == JumpState.POUND){
                screen.shaker.addDamage(.5f);
            }
            jumpState = JumpState.ONGROUND;
            position.y = 0;
            velocity.y = 0;
        } else {
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(assets.whitePixel, position.x, position.y, width, height);
    }
}
