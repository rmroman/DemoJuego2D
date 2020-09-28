package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PantallaRunner extends Pantalla
{
    private Juego juego;

    // Personaje / mario
    private Mario mario;
    private Texture texturaMario;

    // Bolas de fuego
    private Texture texturaBolaFuego;
    private Array<BolaFuego> arrBolasFuego;

    // Enemigo
    private Goomba goomba;
    private Texture texturaGoomba;

    // Fondo
    private Texture texturaFondo;
    private float xFondo = 0;

    // Enemigos
    private Array<Goomba> arrEnemigos;
    private float timerCrearEnemigo;
    private float TIEMPO_CREA_ENEMIGO = 1;    // VARIABLE
    private float tiempoBase = 1;



    public PantallaRunner(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        crearMario();
        crearFondo();
        crearGoomba();
        crearEnemigos();
        crearBolasFuego();

        Gdx.input.setInputProcessor(new ProcesadorEntrada());
    }

    private void crearBolasFuego() {
        texturaBolaFuego = new Texture("runner/bolaFuego.png");
        arrBolasFuego = new Array<>();
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
        dibujarBolasFuego();
        batch.end();
    }

    private void dibujarBolasFuego() {
        for (BolaFuego bola :
                arrBolasFuego) {
            bola.render(batch);
        }
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
        actualizarBolasFuego();
        // Colisiones
        verificarColisiones();      // Bolas de fuego y enemigos
        verificarChoqueEnemigosPersonaje();
    }

    private void verificarChoqueEnemigosPersonaje() {
        for (int i=arrEnemigos.size-1; i>=0; i--) {
            Goomba goomba = arrEnemigos.get(i);
            if (mario.sprite.getBoundingRectangle().overlaps(goomba.sprite.getBoundingRectangle())) {
                // PERDIÓ !!!!!!!
                mario.sprite.setY(ALTO);
                arrEnemigos.removeIndex(i);
                break;
            }
        }
    }

    private void verificarColisiones() {
        for (int i=arrBolasFuego.size-1; i>=0; i--) {
            BolaFuego bola = arrBolasFuego.get(i);      // UNA bola de fuego
            for (int j=arrEnemigos.size-1; j>=0; j--) {
                Goomba goomba = arrEnemigos.get(j);     // UN enemigo
                if (bola.sprite.getBoundingRectangle().overlaps(goomba.sprite.getBoundingRectangle())) {
                    // COLISION!!!
                    arrEnemigos.removeIndex(j);
                    arrBolasFuego.removeIndex(i);
                    break;
                }
            }
        }
    }

    private void actualizarBolasFuego() {
        for (int i=arrBolasFuego.size-1; i>=0; i--) {
            BolaFuego bola = arrBolasFuego.get(i);
            bola.moverDerecha();
            if (bola.sprite.getX()>ANCHO) {
                arrBolasFuego.removeIndex(i);
            }
        }
    }

    private void actualizarEnemigos() {
        timerCrearEnemigo += Gdx.graphics.getDeltaTime();
        if (timerCrearEnemigo>=TIEMPO_CREA_ENEMIGO) {
            timerCrearEnemigo = 0;
            TIEMPO_CREA_ENEMIGO = tiempoBase + MathUtils.random()*2;
            if (tiempoBase>0) {
                tiempoBase -= 0.01f;
            }
            Goomba goomba = new Goomba(texturaGoomba, ANCHO, 60 + MathUtils.random(0,2)*100);  // 60, 120, 180
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
            } else if (v.x >= ANCHO/2) {
                // Dispara
                if (arrBolasFuego.size < 20) {
                    BolaFuego bolaFuego = new BolaFuego(texturaBolaFuego, mario.sprite.getX(),
                            mario.sprite.getY() + mario.sprite.getHeight()*0.5F);
                    arrBolasFuego.add(bolaFuego);
                }
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










