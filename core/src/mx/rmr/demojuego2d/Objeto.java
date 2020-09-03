package mx.rmr.demojuego2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
Representa un elemento gráfico en la pantalla
 */
public class Objeto
{
    protected Sprite sprite;    // Las subclases pueden acceder/modificar directamente a sprite

    public Objeto(Texture textura, float x, float y) {
        sprite = new Sprite(textura);
        sprite.setPosition(x, y);
    }

    public Objeto() {
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
