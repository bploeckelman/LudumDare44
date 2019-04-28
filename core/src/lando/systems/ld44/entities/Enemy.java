package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.screens.GameScreen;

public class Enemy extends AnimationGameEntity {

    public Enemy(GameScreen screen, Animation<TextureRegion> animation, float movement) {
        super(screen, animation);
        poundable = true;
        initialVelocity = movement + ((int)Math.random()*movement/2);
    }

    public void spawn(Direction direction, float x, float y) {
        position.set(x, y);
        setDirection(direction);
    }

    @Override
    public void setDirection(Direction direction) {
        setDirection(direction, initialVelocity);
    }
}
