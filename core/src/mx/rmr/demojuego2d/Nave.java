package mx.rmr.demojuego2d;

import com.badlogic.gdx.graphics.Texture;

public class Nave extends Objeto
{
    private final float DX_NAVE = 10;

    public Nave(Texture textura, float x, float y) {
        super(textura, x, y);
    }

    public void moverIzquierda() {
        sprite.setX(sprite.getX() - DX_NAVE);
    }

    public void moverDerecha() {
        sprite.setX(sprite.getX() + DX_NAVE);
    }
}
