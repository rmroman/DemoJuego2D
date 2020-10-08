package mx.rmr.demojuego2d.plataformas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import mx.rmr.demojuego2d.Juego;
import mx.rmr.demojuego2d.Pantalla;

public class PantallaMapas extends Pantalla
{
    // Mapa
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;


    public PantallaMapas(Juego juego) {
    }

    @Override
    public void show() {
        crearMapa();
    }

    private void crearMapa() {
        AssetManager manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load("mapas/mapa_mario.tmx", TiledMap.class);
        manager.finishLoading();
        mapa = manager.get("mapas/mapa_mario.tmx");
        rendererMapa = new OrthogonalTiledMapRenderer(mapa);
    }

    @Override
    public void render(float delta) {
        // Actualizar
        actualizarCamara();

        borrarPantalla(0,1,0);

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();
    }

    private void actualizarCamara() {
        camara.position.x = camara.position.x + 2;
        camara.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
