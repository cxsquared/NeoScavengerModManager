package com.cxsquared.nsmm.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cxsquared.nsmm.NeoScavengerModManager;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1;
		config.height = 1;
		new LwjglApplication(new NeoScavengerModManager(), config);
	}
}
