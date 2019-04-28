package lando.systems.ld44.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.Config;

public abstract class BaseScreen extends InputAdapter {
    public final Game game;
    public final Assets assets;
    public final Audio audio;

    public OrthographicCamera worldCamera;
    public OrthographicCamera hudCamera;

    protected static final float MAX_ZOOM = 2f;
    protected static final float MIN_ZOOM = 1.0f;
    private static final float ZOOM_LERP = .02f;
    private static final float PAN_LERP = .1f;
    public Vector3 cameraTargetPos = new Vector3();
    public MutableFloat targetZoom = new MutableFloat(1f);
    public boolean cameraOverride = false;

    public boolean allowInput;

    public BaseScreen(Game game, Assets assets) {
        super();
        this.game = game;
        this.assets = assets;
        this.audio = game.audio;

        float aspect = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, Config.window_width, Config.window_width / aspect);
        this.worldCamera.update();
        this.cameraTargetPos.set(this.worldCamera.position);

        float hudScale = 1f;
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, hudScale * Config.window_width, hudScale * Config.window_width / aspect);
        this.hudCamera.update();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);


    protected void updateCamera() {
        if (!cameraOverride) {
            worldCamera.zoom = MathUtils.lerp(worldCamera.zoom, targetZoom.floatValue(), ZOOM_LERP);
            worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, MIN_ZOOM, MAX_ZOOM);

            worldCamera.position.x = MathUtils.lerp(worldCamera.position.x, cameraTargetPos.x, PAN_LERP);
            worldCamera.position.y = MathUtils.lerp(worldCamera.position.y, cameraTargetPos.y, PAN_LERP);
            worldCamera.update();
        }
    }
}
