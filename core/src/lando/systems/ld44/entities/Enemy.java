package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Enemy extends AnimationGameEntity {

    public Enemy(GameScreen screen, Animation<TextureRegion> animation, float movement) {
        super(screen, animation);

        poundable = true;
        initialVelocity = movement + (float) Math.random() * movement / 2;
    }

    public void spawn(Direction direction, float x, float y) {
        position.set(x, y);
        setDirection(direction);
    }

    @Override
    public void setDirection(Direction direction) {
        setDirection(direction, initialVelocity);
    }


    @Override
    public void renderDying(SpriteBatch batch, float scaleX, float scaleY) {
        batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX * scale, scaleY * scale , dyingRotation);
    }

    private float scale = 1.0f;
    private float dyingRotation = 0;
    @Override
    public void handleDying(float dt) {
        super.handleDying(dt);
        dyingRotation += dt*200;
        scale -= dt/3;
    }
}
