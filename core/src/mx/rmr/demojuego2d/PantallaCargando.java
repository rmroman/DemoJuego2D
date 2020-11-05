package mx.rmr.demojuego2d;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class PantallaCargando extends Pantalla
{
    // Animación cargando (espera...)
    private Sprite spriteCargando;
    public static final float TIEMPO_ENTRE_FRAMES = 0.05f;
    private float timerAnimacion = TIEMPO_ENTRE_FRAMES;
    private Texture texturaCargando;

    // AssetManager
    private AssetManager manager;

    // Referencia al juego
    private Juego juego;    // Para hacer setScreen

    // Identifica las pantallas del juego
    private Pantallas siguientePantalla;

    // % de carga
    private int avance;

    // Para mostrar mensajes
    private Texto texto;

    public PantallaCargando(Juego juego, Pantallas siguientePantalla) {
        this.juego = juego;
        this.siguientePantalla = siguientePantalla;
    }

    @Override
    public void show() {
        texturaCargando = new Texture("cargando/loading.png");
        spriteCargando = new Sprite(texturaCargando);
        spriteCargando.setPosition(ANCHO/2 - spriteCargando.getWidth()/2,
                ALTO/2 - spriteCargando.getHeight()/2);
        cargarRecursos(siguientePantalla);
        texto = new Texto("runner/game.fnt");
    }

    private void cargarRecursos(Pantallas siguientePantalla) {
        manager = juego.getManager();
        avance = 0; // % de carga
        switch (siguientePantalla) {
            case MENU:
                cargarRecursosMenu();
                break;
            case RUNNER:
                cargarRecursosRunner();
                break;
            // Agregar otras pantallas
        }
    }

    private void cargarRecursosMenu() {
        manager.load("fondoMenu.jpg", Texture.class);
        // Botones/escena
        manager.load("botonesMenu/btnJugar.png", Texture.class);
        manager.load("botonesMenu/btnJugarInverso.png", Texture.class);
    }

    private void cargarRecursosRunner() {
            }

    @Override
    public void render(float delta) {
        borrarPantalla(0.5f, 0.2f, 0.5f);

        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        spriteCargando.draw(batch);
        texto.mostrarMensaje(batch, avance + "%", ANCHO/2, ALTO/2);
        batch.end();

        // Actualizar
        timerAnimacion -= delta;
        if (timerAnimacion<=0) {
            timerAnimacion = TIEMPO_ENTRE_FRAMES;
            spriteCargando.rotate(20);
        }

        // Actualizar carga
        actualizarCarga();
    }

    private void actualizarCarga() {
        // ¿Cómo va la carga de recursos?
        if (manager.update()) {
            // Terminó!
            switch (siguientePantalla) {
                case MENU:
                    juego.setScreen(new PantallaMenu(juego));
                    break;
                // Agregar las otras pantallas
            }
        }

        avance = (int)(manager.getProgress()*100);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        texturaCargando.dispose();
    }
}
