package lando.systems.ld44.world;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.entities.*;
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
        Enemy entity = null;
        switch (enemyType) {
            case penny:  entity = new Penny(screen); break;
            case nickel: entity = new Nickel(screen); break;
            case dime:   entity = new Dime(screen); break;
        }
        if (entity != null) {
            entity.spawn(direction, pos.x, pos.y);
            screen.gameEntities.add(entity);
        }
    }

}