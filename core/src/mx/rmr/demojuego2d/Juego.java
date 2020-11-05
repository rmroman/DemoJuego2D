package mx.rmr.demojuego2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
El juego principal (aplicación)
Autor: Roberto Mtz. Román
 */
public class Juego extends Game
{
	private AssetManager manager;

	@Override
	public void create () {
		manager = new AssetManager();
		// La primer ventana
		//setScreen(new PantallaMenu(this));	// Pasamos el controlador (.setScreen)
		setScreen(new PantallaCargando(this, Pantallas.MENU));
	}

	public AssetManager getManager() {
		return manager;
	}

	@Override
	public void render () {
		super.render();
	}

}
