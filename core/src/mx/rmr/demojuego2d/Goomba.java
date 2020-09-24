package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.lang.reflect.Array;

public class Goomba extends Objeto
{
    private Animation<TextureRegion> animacion;
    private float timerAnimacion;

    public Goomba(Texture textura, float x, float y) {
        TextureRegion region = new TextureRegion(textura);
        TextureRegion[][] texturasFrame = region.split(62, 58);

        animacion = new Animation<TextureRegion>(0.3f, texturasFrame[0][0],
                texturasFrame[0][1]);
        animacion.setPlayMode(Animation.PlayMode.LOOP);
        timerAnimacion = 0;

        sprite = new Sprite(texturasFrame[0][2]);
        sprite.setPosition(x, y);
    }

    public void moverIzquierda() {
        sprite.setX(sprite.getX()-10);
    }


    @Override
    public void render(SpriteBatch batch) {
        timerAnimacion += Gdx.graphics.getDeltaTime();
        TextureRegion frame = animacion.getKeyFrame(timerAnimacion);
        batch.draw(frame, sprite.getX(), sprite.getY());
    }
}
