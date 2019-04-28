package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Penny extends Enemy {
    public Penny(GameScreen screen) {
        super(screen, screen.assets.pennyAnimation, 40);
    }
}
