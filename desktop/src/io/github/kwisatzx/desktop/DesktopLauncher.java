package io.github.kwisatzx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.kwisatzx.Constants;
import io.github.kwisatzx.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = (int) Constants.HEIGHT;
		config.width = (int) Constants.WIDTH;
		config.resizable = false;
		config.title = "Sorting";
		config.foregroundFPS = 144;
		config.backgroundFPS = 144;
		new LwjglApplication(new Main(), config);
	}
}