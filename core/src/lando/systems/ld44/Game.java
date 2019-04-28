package lando.systems.ld44;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld44.accessors.*;
import lando.systems.ld44.screens.BaseScreen;
import lando.systems.ld44.screens.LoadingScreen;
import lando.systems.ld44.screens.TitleScreen;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.Config;

public class Game extends ApplicationAdapter {

	public Assets assets;
	public TweenManager tween;
	public Audio audio;

	private BaseScreen screen;
	private BaseScreen nextScreen;
	private MutableFloat transitionPercent;
	private FrameBuffer transitionFBO;
	private FrameBuffer originalFBO;
	Texture originalTexture;
	Texture transitionTexture;
	ShaderProgram transitionShader;

	@Override
	public void create () {

		if (Gdx.app.getType() == Application.ApplicationType.Desktop){
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
		transitionPercent = new MutableFloat(0);
		transitionFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
		transitionTexture = transitionFBO.getColorBufferTexture();


		originalFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Config.window_width, Config.window_height, false);
		originalTexture = originalFBO.getColorBufferTexture();

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

		if (audio == null) {
			audio = new Audio(this);
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
		if (nextScreen != null) {
			nextScreen.update(dt);
			transitionFBO.begin();
			nextScreen.render(assets.batch);
			transitionFBO.end();

			originalFBO.begin();
			screen.render(assets.batch);
			originalFBO.end();

			assets.batch.setShader(transitionShader);
			assets.batch.begin();
			originalTexture.bind(1);
			transitionShader.setUniformi("u_texture1", 1);
			transitionTexture.bind(0);
			transitionShader.setUniformf("u_percent", transitionPercent.floatValue());
			assets.batch.setColor(Color.WHITE);
			assets.batch.draw(transitionTexture, 0,0, Config.window_width, Config.window_height);
			assets.batch.end();
			assets.batch.setShader(null);
		} else {
			screen.render(assets.batch);
		}

	}
	
	@Override
	public void dispose () {
	    assets.dispose();
	}

	public void setScreen(BaseScreen screen) {
		setScreen(screen, null, 1f);
	}

	public void setScreen(final BaseScreen newScreen, ShaderProgram transitionType, float transitionSpeed) {
		if (nextScreen != null) return;
		if (screen == null) {
			screen = newScreen;
			screen.allowInput = true;
		} else {
			if (transitionType == null){
				transitionShader = assets.randomTransitions.get(MathUtils.random(assets.randomTransitions.size-1));
			} else {
				transitionShader = transitionType;
			}
			screen.allowInput = false;
			transitionPercent.setValue(0);
			Timeline.createSequence()
					.pushPause(.1f)
					.push(Tween.call(new TweenCallback() {
						@Override
						public void onEvent(int i, BaseTween<?> baseTween) {
							nextScreen = newScreen;
						}
					}))
					.push(Tween.to(transitionPercent,1, transitionSpeed)
							.target(1))
					.push(Tween.call(new TweenCallback() {
						@Override
						public void onEvent(int i, BaseTween<?> baseTween) {
							screen = nextScreen;
							nextScreen = null;
							screen.allowInput = true;
						}
					}))
					.start(tween);
		}

	}

}
