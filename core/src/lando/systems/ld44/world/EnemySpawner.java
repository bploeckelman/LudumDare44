package lando.systems.ld44.world;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.entities.Dime;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Nickel;
import lando.systems.ld44.entities.Penny;
import lando.systems.ld44.screens.GameScreen;

public class EnemySpawner {

    public enum EnemyType { penny, nickel, dime }

    public Vector2 pos;
    public EnemyType enemyType;
    public GameEntity.Direction direction;

    public EnemySpawner(float x, float y, EnemyType type, GameEntity.Direction direction) {
        this.pos = new Vector2(x, y);
        this.enemyType = type;
        this.direction = direction;
    }

    public void spawnEnemy(GameScreen screen) {
        GameEntity entity = null;
        switch (enemyType) {
            case penny:  entity = new Penny(screen); break;
            case nickel: entity = new Nickel(screen); break;
            case dime:   entity = new Dime(screen); break;
        }
        if (entity != null) {
            entity.position.set(pos.x, pos.y);
            entity.direction = direction;
            screen.gameEntities.add(entity);
        }
    }

}
