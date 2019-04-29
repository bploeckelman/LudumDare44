package lando.systems.ld44.ai.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.entities.Boss;
import lando.systems.ld44.entities.Player;

public class BossPoundState implements State {

    enum PoundState {Patrol, Locked, Attacking, Waiting, Return}

    Boss boss;
    private float timer;
    private PoundState poundState;

    public BossPoundState(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void update(float dt) {
        timer = Math.max(timer -dt, 0);
        switch (poundState){
            case Patrol:
                boss.patrol(dt);
                checkForPlayer();
                break;
            case Locked:
                if (timer % .25f < .125f) boss.tint.set(1f,.5f,.5f,1f);
                else boss.tint.set(Color.WHITE);
                if (timer <= 0) {
                    poundState = PoundState.Attacking;
                }
                break;
            case Attacking:
                boss.tint.set(Color.WHITE);
                float distance = Math.min(300 * (4 - boss.hitpoints) * dt, boss.position.y - 32);
                boss.position.y -= distance;

                if (MathUtils.isEqual(boss.position.y, 32, 1f)){
                    boss.gameScreen.shaker.addDamage(.5f);
                    poundState = PoundState.Waiting;
                    timer = boss.hitpoints;
                }
                break;
            case Waiting:
                if (timer <= 0){
                    poundState = PoundState.Return;
                }
                break;
            case Return:
                if (boss.position.y < 400) {
                    boss.position.y = Math.min(boss.position.y + 400 * dt, 400);
                } else {
                    boss.actionCompleted = true;
                }
                break;
        }
        // Magic Numbers, must be the last day of the jam
        boss.bounds.set(boss.position.x + 25, boss.position.y, 90, 100);
    }

    @Override
    public void onEnter() {
        boss.texture = boss.gameScreen.assets.handFist;
        boss.actionCompleted = false;
        poundState = PoundState.Patrol;
    }

    @Override
    public void onExit() {
        boss.actionCompleted = false;
        boss.bounds.x = -1000;
    }

    private void checkForPlayer(){
        Player player = boss.gameScreen.player;;
        if (boss.position.x + 65 > player.position.x && boss.position.x + 65 < player.position.x + player.bounds.width) {
            timer = 1f;
            poundState = PoundState.Locked;
        }
    }
}
