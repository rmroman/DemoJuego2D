package mx.rmr.demojuego2d.plataformas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
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

    public static final float ANCHO_MAPA = 80*32;

    // PERSONAJE
    private Texture texturaPersonaje;       // Aquí cargamos la imagen marioSprite.png con varios frames
    private Personaje mario;
    public static final int TAM_CELDA = 16; // PERSONAJE

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

    // MAnager
    private AssetManager manager;

    // Partículas
    private ParticleEffect pe;
    private ParticleEmitter emisor;     // Simulación


    public PantallaMapas(Juego juego) {
    }

    @Override
    public void show() {
        manager = new AssetManager();
        crearMapa();
        crearHUD();
        crearPersonaje();

        crearSistemaParticulas();

        texto = new Texto("runner/game.fnt");
        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    private void crearSistemaParticulas() {
        pe = new ParticleEffect();
        pe.load(Gdx.files.internal("lluvia.pe"), Gdx.files.internal(""));
        emisor = pe.getEmitters().get(0);
        emisor.setPosition(0, ALTO);
        pe.start();
    }

    private void crearPersonaje() {
        // Cargar frames
        texturaPersonaje = new Texture("mapas/marioSprite_32.png");
        // Crear el personaje
        mario = new Personaje(texturaPersonaje);
        // Posición inicial del personaje
        mario.getSprite().setPosition(Pantalla.ANCHO / 10, Pantalla.ALTO * 0.90f);

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
        manager.load("mapas/Mapa.tmx", TiledMap.class);
        manager.finishLoading();
        mapa = manager.get("mapas/Mapa.tmx");
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
            //actualizarCamara();
            //quitarBloques(delta);
            moverPersonaje();
        }
        actualizarTimer(delta);

        borrarPantalla(0,1,0);

        batch.setProjectionMatrix(camara.combined);

        rendererMapa.setView(camara);
        rendererMapa.render();
        

        batch.begin();
        mario.render(batch);    // Dibuja el personaje
        pe.draw(batch);
        pe.update(delta);
        batch.end();

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

    private void moverPersonaje() {
        // Prueba caída libre inicial o movimiento horizontal
        switch (mario.getEstadoMovimiento()) {
            case INICIANDO:     // Mueve el personaje en Y hasta que se encuentre sobre un bloque
                // Los bloques en el mapa son de 16x16
                // Calcula la celda donde estaría después de moverlo
                int celdaX = (int) (mario.getX() / TAM_CELDA);
                int celdaY = (int) ((mario.getY() + mario.VELOCIDAD_Y) / TAM_CELDA);
                // Recuperamos la celda en esta posición
                // La capa 0 es el fondo
                TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(1);
                TiledMapTileLayer.Cell celda = capa.getCell(celdaX, celdaY);
                // probar si la celda está ocupada
                if (celda == null) {
                    // Celda vacía, entonces el personaje puede avanzar
                    mario.caer();
                } else if (!esEstrella(celda)) {  // Las estrellas no lo detienen :)
                    // Dejarlo sobre la celda que lo detiene
                    mario.setPosicion(mario.getX(), (celdaY + 1) * TAM_CELDA);
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
                }
                break;
            case MOV_DERECHA:       // Se mueve horizontal
            case MOV_IZQUIERDA:
                probarChoqueParedes();      // Prueba si debe moverse
                break;
        }

        // Prueba si debe caer por llegar a un espacio vacío
        if (mario.getEstadoMovimiento() != Personaje.EstadoMovimiento.INICIANDO
                && (mario.getEstadoSalto() != Personaje.EstadoSalto.SUBIENDO)) {
            // Calcula la celda donde estaría después de moverlo
            int celdaX = (int) (mario.getX() / TAM_CELDA);
            int celdaY = (int) ((mario.getY() + mario.VELOCIDAD_Y) / TAM_CELDA);
            // Recuperamos la celda en esta posición
            // La capa 0 es el fondo
            TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(1);
            TiledMapTileLayer.Cell celdaAbajo = capa.getCell(celdaX, celdaY);
            TiledMapTileLayer.Cell celdaDerecha = capa.getCell(celdaX + 1, celdaY);
            // probar si la celda está ocupada
            if ((celdaAbajo == null && celdaDerecha == null) || esEstrella(celdaAbajo) || esEstrella(celdaDerecha)) {
                // Celda vacía, entonces el personaje puede avanzar
                mario.caer();
                mario.setEstadoSalto(Personaje.EstadoSalto.CAIDA_LIBRE);
            } else {
                // Dejarlo sobre la celda que lo detiene
                mario.setPosicion(mario.getX(), (celdaY + 1) * TAM_CELDA);
                mario.setEstadoSalto(Personaje.EstadoSalto.EN_PISO);
            }
        }

        // Saltar
        switch (mario.getEstadoSalto()) {
            case SUBIENDO:
            case BAJANDO:
                mario.actualizarSalto();    // Actualizar posición en 'y'
                break;
        }
    }

    // Verifica si esta casilla tiene una estrella (simplificar con la anterior)
    private boolean esEstrella(TiledMapTileLayer.Cell celda) {
        if (celda==null) {
            return false;
        }
        Object propiedad = celda.getTile().getProperties().get("tipo");
        return "estrella".equals(propiedad);
    }

    // Verifica si esta casilla tiene un hongo (simplificar con las anteriores)
    private boolean esHongo(TiledMapTileLayer.Cell celda) {
        if (celda==null) {
            return false;
        }
        Object propiedad = celda.getTile().getProperties().get("tipo");
        return "hongo".equals(propiedad);
    }

    // Verifica si esta casilla tiene una moneda
    private boolean esMoneda(TiledMapTileLayer.Cell celda) {
        if (celda==null) {
            return false;
        }
        Object propiedad = celda.getTile().getProperties().get("tipo");

        return "moneda".equals(propiedad);
    }

    // Prueba si puede moverse a la izquierda o derecha
    private void probarChoqueParedes() {
        Personaje.EstadoMovimiento estado = mario.getEstadoMovimiento();
        // Quitar porque este método sólo se llama cuando se está moviendo
        if ( estado!= Personaje.EstadoMovimiento.MOV_DERECHA && estado!=Personaje.EstadoMovimiento.MOV_IZQUIERDA){
            return;
        }
        float px = mario.getX();    // Posición actual
        // Posición después de actualizar
        px = mario.getEstadoMovimiento()==Personaje.EstadoMovimiento.MOV_DERECHA? px+Personaje.VELOCIDAD_X:
                px-Personaje.VELOCIDAD_X;
        int celdaX = (int)(px/TAM_CELDA);   // Casilla del personaje en X
        if (mario.getEstadoMovimiento()== Personaje.EstadoMovimiento.MOV_DERECHA) {
            celdaX++;   // Casilla del lado derecho
        }
        int celdaY = (int)(mario.getY()/TAM_CELDA); // Casilla del personaje en Y
        TiledMapTileLayer capaPlataforma = (TiledMapTileLayer) mapa.getLayers().get(1);
        if ( capaPlataforma.getCell(celdaX,celdaY) != null || capaPlataforma.getCell(celdaX,celdaY+1) != null ) {
            // Colisionará, dejamos de moverlo
            if ( esEstrella(capaPlataforma.getCell(celdaX,celdaY)) ) {
                // Borrar esta estrella y contabilizar
                capaPlataforma.setCell(celdaX,celdaY,null);
            } else if ( esEstrella(capaPlataforma.getCell(celdaX,celdaY+1)) ) {
                // Borrar esta estrella y contabilizar
                capaPlataforma.setCell(celdaX,celdaY+1,null);
            } else if ( esHongo( capaPlataforma.getCell(celdaX,celdaY) ) ) {

            } else {
                mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
            }
        } else {
            mario.actualizar();
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
            /*if (estado == EstadoJuego.JUGANDO) {
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
             */

            Vector3 v = new Vector3(screenX, screenY, 0);
            camaraHUD.unproject(v);
            if (estado==EstadoJuego.JUGANDO) {
                if (v.y > ALTO/2) {
                    // Saltar
                    mario.saltar();
                    Gdx.app.log("MARIO", "Inciia salto mario");
                } else if (v.x > ANCHO/2 && mario.getEstadoMovimiento() != Personaje.EstadoMovimiento.INICIANDO) {
                    // Derecha, hacer que el personaje se mueva a la derecha
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);
                } else if (v.x <= ANCHO/2 && mario.getEstadoMovimiento() != Personaje.EstadoMovimiento.INICIANDO) {
                    // Izquierda, hacer que el personaje se mueva a la izquierda
                    mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
                }
            }
            return true;    // Indica que ya procesó el evento
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











