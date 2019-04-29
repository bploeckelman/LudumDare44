package lando.systems.ld44.ai.states;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld44.entities.Boss;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Quarter;

public class SpawnCoinsState implements State {
    Boss boss;
    float spawnDelay;

    public SpawnCoinsState(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void update(float dt) {
        spawnDelay -= dt;
        if (spawnDelay <= 0){
            spawnDelay += 2f;
            Quarter quarter = new Quarter(boss.gameScreen);
            quarter.spawn(GameEntity.Direction.LEFT, boss.position.x + 40, boss.position.y);
            boss.gameScreen.gameEntities.add(quarter);
        }

        boss.patrol(dt);

    }

    @Override
    public void onEnter() {
        spawnDelay = 3f;
        boss.texture = boss.gameScreen.assets.handFlat;
        boss.direction = -1f;
    }

    @Override
    public void onExit() {

    }
}
