package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Dime extends AnimationGameEntity {
    public Dime(GameScreen screen) {
        super(screen, screen.assets.dimeAnimation);
        setVelocityX(30 + (float)Math.random()*5);
    }
}
