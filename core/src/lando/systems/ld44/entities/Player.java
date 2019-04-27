package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class Player extends GameEntity {
    public float horizontalSpeed = 200;
    public float jumpVelocity = 1000;

    public GameScreen screen;

    public Player(GameScreen screen, Assets assets, float x, float y) {
        super(assets);

        this.screen = screen;

        this.position.set(x, y);
        width = 90;
        height = 60;
        image = assets.whitePixel;
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
