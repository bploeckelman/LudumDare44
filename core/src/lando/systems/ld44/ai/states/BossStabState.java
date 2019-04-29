package lando.systems.ld44.ai.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.entities.Boss;
import lando.systems.ld44.entities.Player;

public class BossStabState implements State {

    enum StabState {Patrol, Locked, Attacking, Waiting, Return}
    private Boss boss;
    private float timer;
    private StabState stabState;

    public BossStabState(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void update(float dt) {
        timer = Math.max(timer -dt, 0);
        switch (stabState) {
            case Patrol:
                boss.patrol(dt);
                checkForPlayer();
                break;
            case Locked:
                if (timer % .25f < .125f) boss.tint.set(1f,.5f,.5f,1f);
                else boss.tint.set(Color.WHITE);
                if (timer <= 0) {
                    stabState = StabState.Attacking;
                }
                break;
            case Attacking:
                boss.tint.set(Color.WHITE);
                float distance = Math.min(300 * (4 - boss.hitpoints) * dt, boss.position.y - 32);
                boss.position.y -= distance;
                boss.position.x += distance * boss.direction / 2f;

                if (MathUtils.isEqual(boss.position.y, 32, 1f)){
                    boss.gameScreen.shaker.addDamage(.2f);
                    stabState = StabState.Waiting;
                    timer = boss.hitpoints;
                }

                break;
            case Waiting:
                if (timer <= 0){
                    stabState = StabState.Return;
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
        boss.bounds.set(boss.position.x, boss.position.y, 130, 100);
    }

    @Override
    public void onEnter() {
        boss.texture = boss.gameScreen.assets.handPointer;
        boss.actionCompleted = false;
        stabState = StabState.Patrol;
    }

    @Override
    public void onExit() {
        boss.actionCompleted = false;
        boss.bounds.x = -1000;

    }

    private void checkForPlayer(){
        Player player = boss.gameScreen.player;
        // Magic Numbers, must be the last day of the jam
        float targetX = boss.direction * ((boss.position.y - player.position.y)/2f) + (boss.position.x + 65);
        if (targetX > player.position.x && targetX < player.position.x + player.bounds.width) {
            timer = 1f;
            stabState = StabState.Locked;
        }
    }
}
