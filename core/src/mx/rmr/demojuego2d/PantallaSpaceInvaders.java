package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PantallaSpaceInvaders extends Pantalla
{
    private final Juego juego;

    // Aliens - enemigos
    private Array<Alien> arrAliens;

    // Nave
    private Nave nave;
    private Texture texturaNave;

    // Disparo
    private Texture texturaBtnDisparo;
    private Bala bala;
    private Texture texturaBala;

    public PantallaSpaceInvaders(Juego juego) {
        this.juego = juego;
    }

    // INICIALIZAR lso objetos
    @Override
    public void show() {
        crearAliens();
        crearNave();
        crearDisparo();
        crearBala();    // Solo crea al textura
        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    private void crearBala() {
        texturaBala = new Texture("space/bala.png");
    }

    private void crearDisparo() {
        texturaBtnDisparo = new Texture("space/disparo.png");
    }

    private void crearNave() {
        texturaNave = new Texture("space/nave.png");
        nave = new Nave(texturaNave, ANCHO/2, ALTO*0.1f);
    }

    private void crearAliens() {
        Texture texturaAlien = new Texture("space/enemigoArriba.png");
        arrAliens = new Array<>(12*5);
        for (int i=0; i<5; i++) {   // renglones, y
            for (int j=0; j<12; j++) {  // columnas, x
                Alien alien = new Alien(texturaAlien, 280 + j*60, 350 + i*60);
                arrAliens.add(alien);
            }
        }
    }

    @Override
    public void render(float delta) {
        // Actualizar los objetos
        actualizarBala(delta);

        borrarPantalla(0.2f, 0.2f, 0.2f);
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        // Enemigos
        for (Alien alien : arrAliens) {
            alien.render(batch);
        }
        // Nave
        nave.render(batch);
        // Disparo
        batch.draw(texturaBtnDisparo, ANCHO - texturaBtnDisparo.getWidth()*1.5f,
                ALTO*0.2f);
        // Bala
        if (bala != null) {
            bala.render(batch);
        }
        batch.end();
    }

    private void actualizarBala(float tiempo) {
        if (bala != null) {
            bala.mover(tiempo);
        }
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

    private class ProcesadorEntrada implements InputProcessor
    {

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
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 v = new Vector3(screenX, screenY, 0);
            camara.unproject(v);
            // Tocó el botón de disparo?
            float xBoton = ANCHO - texturaBtnDisparo.getWidth()*1.5f;
            float yBoton = ALTO*0.2f;
            float anchBoton = texturaBtnDisparo.getWidth();
            float altoBoton = texturaBtnDisparo.getHeight();
            Rectangle rectBoton = new Rectangle(xBoton, yBoton, anchBoton, altoBoton);

            if (rectBoton.contains(v.x, v.y)) {
                // Disparar!!!!!
                bala = new Bala(texturaBala, nave.sprite.getX()+nave.sprite.getWidth()/2,
                        nave.sprite.getY()+nave.sprite.getHeight());
            } else if (v.x<=ANCHO/2) {
                // izq
                nave.moverIzquierda();
            } else {
                // derecha
                nave.moverDerecha();
            }
            return true;    //////////////////////  **********   ///////////////////
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
}
