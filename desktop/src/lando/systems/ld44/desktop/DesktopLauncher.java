package lando.systems.ld44.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.window_width;
		config.height = Config.window_height;
		config.resizable = Config.resizable;
		config.title = Config.title;
		config.useHDPI = true;
		new LwjglApplication(new Game(), config);
	}
}
