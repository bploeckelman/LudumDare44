package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Penny extends Enemy {
    public Penny(GameScreen screen) {
        super(screen, screen.assets.pennyAnimation, screen.assets.pennyPickupAnimation, 40, 0.01f);
        this.collisionBoundsOffsets.set(20, 0, 24, 24);

    }
}
