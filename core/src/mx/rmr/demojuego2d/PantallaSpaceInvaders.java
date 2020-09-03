package mx.rmr.demojuego2d;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class PantallaSpaceInvaders extends Pantalla
{
    private final Juego juego;

    // Aliens - enemigos
    private Array<Alien> arrAliens;

    public PantallaSpaceInvaders(Juego juego) {
        this.juego = juego;
    }

    // INICIALIZAR lso objetos
    @Override
    public void show() {
        crearAliens();
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
        borrarPantalla(0.2f, 0.2f, 0.2f);
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        for (Alien alien : arrAliens) {
            alien.render(batch);
        }
        batch.end();
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
