package mx.rmr.demojuego2d;

import com.badlogic.gdx.graphics.Texture;

public class Bala extends Objeto
{
    // Física
    private final float VELOCIDAD_Y = Pantalla.ALTO/2;  // '360' pixeles/segundo

    public Bala(Texture textura, float x, float y) {
        super(textura, x, y);
    }

    public void mover(float tiempo) {
        float distancia = VELOCIDAD_Y * tiempo; // [.01]
        sprite.setY(sprite.getY() + distancia);
    }
}
