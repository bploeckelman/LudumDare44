package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class GameEntity {

    public Assets assets;
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();

    public float width;
    public float height;

    public TextureRegion image;

    public GameEntity(Assets assets){
        this.assets = assets;
    }

    public void update(float dt) {
        position.add(velocity.x * dt, velocity.y * dt);
    }

    public void render(SpriteBatch batch) {
        if (image != null) {
            batch.draw(image, position.x, position.y);
        }
    }
}
