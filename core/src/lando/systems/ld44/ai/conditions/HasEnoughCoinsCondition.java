package lando.systems.ld44.ai.conditions;

import lando.systems.ld44.entities.Coin;
import lando.systems.ld44.entities.Enemy;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.screens.GameScreen;

public class HasEnoughCoinsCondition implements Condition{

    private GameScreen screen;

    public HasEnoughCoinsCondition(GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean isTrue() {
        int count = screen.player.coinPurse.size;
        for (GameEntity entity : screen.gameEntities){
            if (entity instanceof Enemy) count++;
            if (entity instanceof Coin) count++;
        }

        return count >= 10 ;
    }
}
