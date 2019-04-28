package lando.systems.ld44.entities;

import lando.systems.ld44.screens.GameScreen;

public class Quarter extends Enemy {
    public Quarter(GameScreen screen) {
        super(screen, screen.assets.quarterAnimation, 20);
    }
}
