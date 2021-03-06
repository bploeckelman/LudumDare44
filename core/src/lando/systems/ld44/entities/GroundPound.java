package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Config;

public class GroundPound extends GameEntity {

    private Vector2 position;
    private float distance;

    private float spread = 0;
    private float min, max;

    private Rectangle bounds = new Rectangle();

    public GroundPound(GameScreen screen, float x, float y, float offset, float distance) {
        super(screen);

        position = new Vector2(x, y);
        this.distance = distance;
        min = max = -1;

        Array<Rectangle> tiles = new Array<Rectangle>();
        screen.level.getTiles(x, y - 2, x + distance, y - 2, tiles);
        for (Rectangle r : tiles) {
            if (max == -1 || r.x == max) {
                max = r.x + r.width;
            } else {
                break;
            }
        }
        if (max == -1) {
            max = x;
        }

        screen.level.getTiles(x - distance, y - 2, x, y - 2, tiles);
          for (int i = tiles.size; i > 0; i--) {
            Rectangle r = tiles.get(i - 1);
            if (min == -1 || (r.x + r.width) == min) {
                min = r.x;
            } else {
                break;
            }
        }
        if (min == -1) {
            min = x;
        }
        screen.particleManager.addGroundPoundDust(x, y, min, max);

    }

    @Override
    public void update(float dt) {
        spread += 400 * dt;

        float x1 = position.x - spread;
        float x2 = position.x + spread;
        if (x1 < min) {
            x1 = min;
        }
        if (x2 > max) {
            x2 = max;
        }

        bounds.set(x1, position.y, x2 - x1, 1);

        if (spread >= distance) {
            remove = true;
        }

        for (GameEntity ge : screen.gameEntities) {
            if (isInPoundRange(ge.bounds)) {
                ge.pound();
            }
        }
    }

    private boolean isInPoundRange(Rectangle entityBounds) {
        // needs to be on same level
        if (entityBounds.y != bounds.y) {
            return false;
        }

        float x = entityBounds.x + entityBounds.width / 2;
        return x >= bounds.x && x <= bounds.x + bounds.width;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (Config.debug) {
            assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }
}
