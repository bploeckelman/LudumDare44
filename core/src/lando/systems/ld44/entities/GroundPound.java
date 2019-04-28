package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;

public class GroundPound extends GameEntity {

    private Vector2 position;
    private float distance;

    private float spread = 0;

    private Rectangle bounds = new Rectangle();

    public GroundPound(GameScreen screen, float x, float y, float distance) {
        super(screen);

        position = new Vector2(x, y);
        this.distance = distance;
    }

    @Override
    public void update(float dt) {
        spread += 400 * dt;

        bounds.set(position.x - spread, position.y, spread* 2, 1);

        if (spread >= distance) {
            remove = true;
        }

        for (GameEntity ge : screen.gameEntities) {
            if (!ge.poundable || ge.stunned) { continue; }
            // same level
            if (ge.position.y == bounds.y && ge.position.x >= bounds.x && ge.position.x <= (bounds.x + bounds.width)) {
                ge.stun();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
