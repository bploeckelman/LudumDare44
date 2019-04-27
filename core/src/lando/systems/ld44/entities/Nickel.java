package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Nickel extends AnimationGameEntity {

    public Nickel(GameScreen screen) {
        super(screen, screen.assets.nickelAnimation);
        setVelocityX(20 + ((float)Math.random()*20));
    }
}
