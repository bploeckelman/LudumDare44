package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Quarter extends CoinEnemy {
    public Quarter(GameScreen screen) {
        super(screen, screen.assets.quarterAnimation, screen.assets.quarterPickupAnimation, 20, 0.25f);
        this.collisionBoundsOffsets.set(17, 0, 26, 40);
    }
}
