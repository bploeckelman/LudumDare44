package lando.systems.ld44.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Config;

public abstract class BaseScreen extends InputAdapter {
    public final Game game;
    public final Assets assets;

    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;

    public BaseScreen(Game game, Assets assets) {
        super();
        this.game = game;
        this.assets = assets;

        float aspect = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.window_width, Config.window_width / aspect);
        this.worldCamera.update();

        float hudScale = 1f;
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, hudScale * Config.window_width, hudScale * Config.window_width / aspect);
        this.hudCamera.update();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
}
