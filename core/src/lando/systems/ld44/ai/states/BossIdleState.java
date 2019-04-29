package lando.systems.ld44.ai.states;

import lando.systems.ld44.entities.Boss;

public class BossIdleState implements State {

    private Boss boss;
    public float timeInState;

    public BossIdleState(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void update(float dt) {
        timeInState += dt;

        if (boss.position.y < 400) {
            boss.position.y = Math.min(boss.position.y + 400 * dt, 400);
        }

        boss.patrol(dt);

        if (timeInState > 1) boss.actionCompleted = true;
    }

    @Override
    public void onEnter() {
        timeInState = 0;
        boss.actionCompleted = false;

    }

    @Override
    public void onExit() {
        timeInState = 0;
        boss.actionCompleted = false;

    }
}
