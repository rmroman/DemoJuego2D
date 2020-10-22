package mx.rmr.demojuego2d.plataformas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import mx.rmr.demojuego2d.Alien;
import mx.rmr.demojuego2d.BolaFuego;
import mx.rmr.demojuego2d.Juego;
import mx.rmr.demojuego2d.Pantalla;
import mx.rmr.demojuego2d.Texto;

public class PantallaMapas extends Pantalla
{
    // Mapa
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer rendererMapa;

    // timer
    private float timerBloque;

    // PAUSA
    private EstadoJuego estado = EstadoJuego.INICIANDO;     // JUGANDO, PAUSADO, PIERDE, GANA
    private EscenaPausa escenaPausa;

    // Cámara/Vista HUD
    private OrthographicCamera camaraHUD;
    private Viewport vistaHUD;

    // INTRO
    private float timerPausa;   // 3 seg
    private Texto texto;


    public PantallaMapas(Juego juego) {
    }

    @Override
    public void show() {
        crearMapa();
        crearHUD();
        texto = new Texto("runner/game.fnt");
        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    private void crearHUD() {
        camaraHUD = new OrthographicCamera(ANCHO, ALTO);
        camaraHUD.position.set(ANCHO/2, ALTO/2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO, ALTO, camaraHUD);
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
        if (estado == EstadoJuego.JUGANDO) {
            actualizarCamara();
            quitarBloques(delta);
        }
        actualizarTimer(delta);

        borrarPantalla(0,1,0);

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();

        // INICIANDO
        if (estado==EstadoJuego.INICIANDO) {
            Gdx.app.log("INICIANDO", "Tiempo: " + (int)(timerPausa));
            batch.begin();
            texto.mostrarMensaje(batch, ""+(int)(3-timerPausa), ANCHO/2, ALTO/2);
            batch.end();
        }

        // USAR otra CÁMARA/VISTA
        if (estado == EstadoJuego.PAUSADO) {
            batch.setProjectionMatrix(camaraHUD.combined);
            escenaPausa.draw();
            batch.begin();
            escenaPausa.sprite.draw(batch);
            batch.end();

            escenaPausa.sprite.rotate(5);
        }
    }

    private void actualizarTimer(float delta) {
        timerPausa += delta;
        if (estado == EstadoJuego.INICIANDO && timerPausa>=3) {

            estado = EstadoJuego.JUGANDO;
        }
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

    class ProcesadorEntrada implements InputProcessor
    {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (estado == EstadoJuego.JUGANDO) {
                estado = EstadoJuego.PAUSADO;
                // Crear escenaPausa
                if (escenaPausa==null) {
                    escenaPausa = new EscenaPausa(vistaHUD, batch); // vistaHUD (no se mueve)
                }
                Gdx.input.setInputProcessor(escenaPausa);
                Gdx.app.log("PAUSA", "Cambia a pausado....");
            } else if (estado == EstadoJuego.PAUSADO) {
                estado = EstadoJuego.JUGANDO;
                Gdx.app.log("PAUSA", "Cambia a jugando....");
            }
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }



        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

    // Estados del juego
    private enum EstadoJuego
    {
        JUGANDO,
        PAUSADO,
        INICIANDO
    }

    private class EscenaPausa extends Stage
    {
        Sprite sprite;

        public EscenaPausa(Viewport vista, SpriteBatch batch) {
            super(vista, batch);

            Texture texturaSprite = new Texture("runner/bolaFuego.png");
            sprite = new Sprite(texturaSprite);
            sprite.setPosition(ANCHO/2, ALTO*0.6f);

            Pixmap pixmap = new Pixmap((int)(ANCHO*0.75f), (int)(0.8f*ALTO),
                    Pixmap.Format.RGBA8888);
            pixmap.setColor(0,0,0,0.5f);
            //pixmap.fillCircle(300,300,250);
            pixmap.fillRectangle(0,0,pixmap.getWidth(), pixmap.getHeight());

            Texture textura = new Texture(pixmap);
            Image imgPausa = new Image(textura);
            imgPausa.setPosition(ANCHO/2 - pixmap.getWidth()/2,
                    ALTO/2 - pixmap.getHeight()/2);

            this.addActor(imgPausa);        // Fondo

            // Botón(es)
            // Botón de disparo
            Texture texturaDisparo = new Texture("space/disparo.png");
            TextureRegionDrawable region = new TextureRegionDrawable(new TextureRegion(
                    texturaDisparo));

            ImageButton btnDisparo = new ImageButton(region);
            btnDisparo.setPosition(ANCHO/2, ALTO/3);
            // Programar listener del botón
            btnDisparo.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    // QUITAR Pausa
                    estado = EstadoJuego.JUGANDO;
                    Gdx.app.log("PAUSA", "Reanuda por el botón de la pausa");
                    Gdx.input.setInputProcessor(new ProcesadorEntrada());
                }
            });

            this.addActor(btnDisparo);
        }
    }
}











