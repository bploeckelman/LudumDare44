package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Nickel extends CoinEnemy {
    public Nickel(GameScreen screen) {
        super(screen, screen.assets.nickelAnimation, screen.assets.nickelPickupAnimation, 30, 0.05f);
        this.collisionBoundsOffsets.set(20, 0, 24, 30);

    }
}
