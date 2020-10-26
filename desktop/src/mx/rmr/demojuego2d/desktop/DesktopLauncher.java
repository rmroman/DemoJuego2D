package mx.rmr.demojuego2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.particleeditor.ParticleEditor;

import mx.rmr.demojuego2d.Juego;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280/2;
		config.height = 720/2;
		new LwjglApplication(new Juego(), config);


		//ParticleEditor.main(arg);
		// api "com.badlogicgames.gdx:gdx-tools:$gdxVersion" en build.gradle
	}
}
