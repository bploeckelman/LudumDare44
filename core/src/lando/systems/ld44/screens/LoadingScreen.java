package lando.systems.ld44.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;

/**
 * Even though this will be a web game and assets are loaded before the game
 * using this to have the user click the screen once before starting any sounds for GWT BS
 */
public class LoadingScreen extends BaseScreen {
    private Rectangle startGameButton;
    private final Vector3 touchPos;
    private Vector3 mousePos;
    private NinePatch border;

    public LoadingScreen(Game game, Assets assets) {
        super(game, assets);
        this.startGameButton = new Rectangle();
        this.touchPos = new Vector3(-1f, -1f, 0f);
        this.startGameButton.set(hudCamera.viewportWidth / 2f - 250f,
                hudCamera.viewportHeight / 2f - 50f,
                500f, 100f);
        this.mousePos = new Vector3();
        this.border = assets.ninePatch;
    }

    @Override
    public void update(float dt) {
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudCamera.unproject(mousePos);

        if (Gdx.input.justTouched()) {
            touchPos.set(mousePos);
        }

        if (startGameButton.contains(touchPos.x, touchPos.y)) {
            game.setScreen(new TitleScreen(game, assets));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            batch.draw(assets.whitePixel, startGameButton.x, startGameButton.y, startGameButton.width, startGameButton.height);

            assets.layout.setText(assets.font, "Play Game", Color.DARK_GRAY, startGameButton.width, Align.center, false);
            assets.font.draw(batch, assets.layout, startGameButton.x, startGameButton.y + startGameButton.height / 2f);
            border.draw(batch, startGameButton.x, startGameButton.y, startGameButton.width, startGameButton.height);
        }
        batch.end();
    }

}
