package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Penny extends AnimationGameEntity {
    public Penny(GameScreen screen) {
        super(screen, screen.assets.pennyAnimation);
        setVelocityX(10 + ((float)Math.random()*10));
    }
}
