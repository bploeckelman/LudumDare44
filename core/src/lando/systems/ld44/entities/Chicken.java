package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Chicken extends Enemy {
    public Chicken(GameScreen screen) {
        super(screen, screen.assets.chickenAnimation, 80f);
        this.collisionBoundsOffsets.set(0, 0, width, height);

    }

}
