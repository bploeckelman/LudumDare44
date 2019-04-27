package lando.systems.ld44;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.accessors.*;
import lando.systems.ld44.screens.BaseScreen;
import lando.systems.ld44.screens.LoadingScreen;
import lando.systems.ld44.screens.TitleScreen;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Config;

public class Game extends ApplicationAdapter {

	public Assets assets;
	public TweenManager tween;

	private BaseScreen screen;

	@Override
	public void create () {
		if (tween == null) {
			tween = new TweenManager();
			Tween.setWaypointsLimit(4);
			Tween.setCombinedAttributesLimit(4);
			Tween.registerAccessor(Color.class, new ColorAccessor());
			Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
			Tween.registerAccessor(Vector2.class, new Vector2Accessor());
			Tween.registerAccessor(Vector3.class, new Vector3Accessor());
			Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
		}

		if (assets == null) {
			assets = new Assets();
		}

		// Go to bullshit start screen for web
		if (Gdx.app.getType() == Application.ApplicationType.WebGL){
			setScreen(new LoadingScreen(this, assets));
		} else {
			setScreen(new TitleScreen(this, assets));
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(Config.clear_color.r, Config.clear_color.g, Config.clear_color.b, Config.clear_color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
		tween.update(dt);
		screen.update(dt);

		screen.render(assets.batch);
	}
	
	@Override
	public void dispose () {
	    assets.dispose();
	}

	public void setScreen(BaseScreen screen) {
		this.screen = screen;
	}

}
