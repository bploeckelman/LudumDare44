package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Chicken extends Enemy {
    public Chicken(GameScreen screen) {
        super(screen, screen.assets.chickenAnimation, null, 80f, 0f);
    }
}
