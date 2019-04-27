package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class Player {

    public Vector2 position;
    public Vector2 velocity;
    public float horizontalSpeed = 200;
    public float jumpVelocity = 1000;
    public float width = 90;
    public float height = 60;
    public Assets assets;
    public boolean onGround;

    public Player(Assets assets, float x, float y){
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.assets = assets;
        this.onGround = true;
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

        if (onGround && jumpPressed){
            velocity.y = jumpVelocity;
        }
        velocity.y -= 3000 * dt;

        position.add(velocity.x * dt, velocity.y * dt);


        // TODO make this based on geometry of the world
        if (position.y <= 0) {
            onGround = true;
            position.y = 0;
            velocity.y = 0;
        } else {
            onGround = false;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(assets.whitePixel, position.x, position.y, width, height);
    }
}
