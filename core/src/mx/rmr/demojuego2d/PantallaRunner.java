package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PantallaRunner extends Pantalla
{
    private Juego juego;

    // Personaje / mario
    private Mario mario;
    private Texture texturaMario;

    // Enemigo
    private Goomba goomba;
    private Texture texturaGoomba;

    // Fondo
    private Texture texturaFondo;
    private float xFondo = 0;

    // Enemigos
    private Array<Goomba> arrEnemigos;
    private float timerCrearEnemigo;
    private final float TIEMPO_CREA_ENEMIGO = 1;

    public PantallaRunner(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        crearMario();
        crearFondo();
        crearGoomba();
        crearEnemigos();

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    private void crearEnemigos() {
        texturaGoomba = new Texture("runner/goomba.png");
        arrEnemigos = new Array<>();

    }

    private void crearGoomba() {
        texturaGoomba = new Texture("runner/goomba.png");
        goomba = new Goomba(texturaGoomba, ANCHO*0.75f, 60);
    }

    private void crearFondo() {
        texturaFondo = new Texture("runner/fondoMario_5.jpg");
    }

    private void crearMario() {
        texturaMario = new Texture("runner/marioSprite.png");
        mario = new Mario(texturaMario, ANCHO/2, 60);
    }

    @Override
    public void render(float delta) {
        actualizar();

        borrarPantalla(0, 0, 0.5f);
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        batch.draw(texturaFondo, xFondo, 0);
        batch.draw(texturaFondo, xFondo + texturaFondo.getWidth(), 0);
        mario.render(batch);
        goomba.render(batch);
        dibujarEnemigos();
        batch.end();
    }

    private void dibujarEnemigos() {
        for (Goomba goomba :
                arrEnemigos) {
            goomba.render(batch);
            // NOOOOOOOOOOOOOOOOOO
            goomba.moverIzquierda();
        }
    }

    private void actualizar() {

        xFondo-=5;
        if (xFondo==-texturaFondo.getWidth()) {
            xFondo = 0;
        }

        actualizarMario();
        actualizarCamara();
        actualizarEnemigos();
    }

    private void actualizarEnemigos() {
        timerCrearEnemigo += Gdx.graphics.getDeltaTime();
        if (timerCrearEnemigo>=TIEMPO_CREA_ENEMIGO) {
            timerCrearEnemigo = 0;
            Goomba goomba = new Goomba(texturaGoomba, ANCHO, 60);
            arrEnemigos.add(goomba);
        }
        for (int i = arrEnemigos.size-1; i >= 0; i--) {
            Goomba goomba = arrEnemigos.get(i);
            if (goomba.sprite.getX()<-goomba.sprite.getWidth()) {
                arrEnemigos.removeIndex(i);
            }
        }
    }

    private void actualizarMario() {
        //mario.sprite.setX(mario.sprite.getX()+2);
    }

    private void actualizarCamara() {
        float xCamara = camara.position.x;
        //xCamara++;
        xCamara = mario.sprite.getX();  // Hacemos que la cámara siga al personaje
        camara.position.x = xCamara;
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

    private class ProcesadorEntrada implements InputProcessor
    {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 v = new Vector3(screenX, screenY, 0);
            camara.unproject(v);
            if (v.x < ANCHO/2 && mario.getEstado()==EstadoMario.CAMINANDO) {
                // SALTA!!!!!
                mario.saltar();
                Gdx.app.log("SALTO", "INICIO........");
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
}










