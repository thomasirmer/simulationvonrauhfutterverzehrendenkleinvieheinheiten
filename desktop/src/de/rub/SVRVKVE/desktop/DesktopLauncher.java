package de.rub.SVRVKVE.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.rub.SVRVKVE.HerdSimulation;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width  = HerdSimulation.WINDOW_X;
		config.height = HerdSimulation.WINDOW_Y;
		new LwjglApplication(new HerdSimulation(), config);
	}
}
