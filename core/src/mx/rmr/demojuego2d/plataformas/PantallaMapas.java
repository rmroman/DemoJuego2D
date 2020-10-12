package mx.rmr.demojuego2d.plataformas;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

import mx.rmr.demojuego2d.Juego;
import mx.rmr.demojuego2d.Pantalla;

public class PantallaMapas extends Pantalla
{
    // Mapa
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;

    // timer
    private float timerBloque;


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

        // Modificar el mapa
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(0);
        TiledMapTileLayer.Cell celda = capa.getCell(0, 0);
        for (int y = 1; y < capa.getHeight()/2; y++) {
            capa.setCell(0, y, celda);
        }
    }

    @Override
    public void render(float delta) {
        // Actualizar
        //actualizarCamara();
        quitarBloques(delta);

        borrarPantalla(0,1,0);

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();
    }

    private void quitarBloques(float delta) {
        timerBloque += delta;
        if (timerBloque>0.3f) {
            timerBloque = 0;
            TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
            int x = MathUtils.random(0, 40);
            int y = MathUtils.random(0, 21);
            capa.setCell(x, y, null);
        }
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
