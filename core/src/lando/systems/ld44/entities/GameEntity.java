package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.utils.Assets;

public class GameEntity {

    public Assets assets;
    public Vector2 position;

    public float width;
    public float height;

    public TextureRegion image;

    public GameEntity(Assets assets){
        this.position = new Vector2();
        this.assets = assets;
    }

    public void update(float dt) { }

    public void render(SpriteBatch batch) {
        if (image != null) {
            batch.draw(image, position.x, position.y, width, height);
        }
    }
}
