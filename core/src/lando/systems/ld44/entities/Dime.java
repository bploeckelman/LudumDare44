package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Dime extends Enemy {
    public Dime(GameScreen screen) {
        super(screen, screen.assets.dimeAnimation, screen.assets.dimePickupAnimation,75, 0.25f);
        this.collisionBoundsOffsets.set(20, 0, 24, 24);
    }
}
