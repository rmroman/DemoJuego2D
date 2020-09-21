package mx.rmr.demojuego2d;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class PantallaRunner extends Pantalla
{
    private Juego juego;

    // Personaje / mario
    private Mario mario;
    private Texture texturaMario;

    // Fondo
    private Texture texturaFondo;
    private float xFondo = 0;

    public PantallaRunner(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        crearMario();
        crearFondo();
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
        batch.end();
    }

    private void actualizar() {
        /*
        xFondo-=5;
        if (xFondo==-texturaFondo.getWidth()) {
            xFondo = 0;
        }
         */
        actualizarMario();
        actualizarCamara();

    }

    private void actualizarMario() {
        mario.sprite.setX(mario.sprite.getX()+2);
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
}
