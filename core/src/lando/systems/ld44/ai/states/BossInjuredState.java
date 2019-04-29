package lando.systems.ld44.ai.states;

import com.badlogic.gdx.graphics.Color;
import lando.systems.ld44.entities.Boss;

public class BossInjuredState implements State {

    Boss boss;
    float timer;

    public BossInjuredState(Boss boss){
        this.boss = boss;
    }

    @Override
    public void update(float dt) {
        timer -= dt;
        if (timer % .5f < .25f) {
            boss.tint.set(Color.GRAY);
        } else {
            boss.tint.set(Color.WHITE);
        }

        if (timer <= 0) boss.actionCompleted = true;

    }

    @Override
    public void onEnter() {
        timer = 3f;
        boss.actionCompleted = false;
        boss.tookDamage = false;
    }

    @Override
    public void onExit() {
        boss.actionCompleted = false;
        boss.tint.set(Color.WHITE);

    }
}
