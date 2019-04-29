package lando.systems.ld44.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.entities.Boss;
import lando.systems.ld44.screens.EndScreen;

public class BossDeadState implements State {

    Boss boss;
    float timer;
    Vector2 deadPosition;
    Vector2 tempVec2;

    public BossDeadState(Boss boss) {
        this.boss = boss;
        deadPosition = new Vector2(400 - 65, 250);
        tempVec2 = new Vector2();
    }

    @Override
    public void update(float dt) {
        timer -= dt;
        if (timer < 0){
            boss.gameScreen.game.setScreen(new EndScreen(boss.gameScreen.game, boss.gameScreen.assets), boss.gameScreen.assets.heartShader, 3f, null);
            timer = 1000;
        }

        float dist = tempVec2.set(deadPosition).sub(boss.position).len();
        float move = 400 * dt;
        if (move > dist){
            boss.position.set(deadPosition);
            boss.gameScreen.particleManager.addExplosion(MathUtils.random(boss.position.x, boss.position.x + 130),
                                                         MathUtils.random(boss.position.y, boss.position.y + 100),
                                                            80, 80);
        } else {
            boss.position.add(tempVec2.nor().scl(move));
        }


    }

    @Override
    public void onEnter() {
        timer = 5;
    }

    @Override
    public void onExit() {

    }
}
