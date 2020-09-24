package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mario extends Objeto
{
    private Animation<TextureRegion> animacion;
    private float timerAnimacion;

    // SALTO
    private float yBase;        // y del piso
    private float tAire;        // tiempo de simulación < tVuelo
    private final float V0 = 100;
    private final float G = 20;
    private float tVuelo;
    private EstadoMario estado;     // CAMINANDO, SALTANDO, SALTO_ARRIBA, SALTO_ABAJO

    public Mario(Texture textura, float x, float y) {
        TextureRegion region = new TextureRegion(textura);
        TextureRegion[][] texturasFrame = region.split(32, 64);
        // Quieto
        sprite = new Sprite(texturasFrame[0][0]);
        sprite.setPosition(x, y);

        // Animación
        TextureRegion[] arrFrames = { texturasFrame[0][3], texturasFrame[0][2],
                texturasFrame[0][1] };
        animacion = new Animation<TextureRegion>(0.1f, arrFrames);
        animacion.setPlayMode(Animation.PlayMode.LOOP);
        timerAnimacion = 0;

        // SALTO
        yBase = y;
        estado = EstadoMario.CAMINANDO;
    }

    public void saltar() {
        estado = EstadoMario.SALTANDO;
        tAire = 0;
        tVuelo = 2*V0/G;        // Permanece en el aire
    }

    public EstadoMario getEstado() {
        return estado;
    }

    public void render(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();  // 1/60
        if (estado==EstadoMario.CAMINANDO) {
            timerAnimacion += delta;        // Acumula
            TextureRegion frame = animacion.getKeyFrame(timerAnimacion);
            batch.draw(frame, sprite.getX(), sprite.getY());
        } else {
            //Gdx.app.log("SALTA", "tAire: " + tAire);
            tAire += 10*delta;
            float y = yBase + V0*tAire - 0.5f*G*tAire*tAire;
            sprite.setY(y);
            super.render(batch);
            if (tAire>=tVuelo) {
                sprite.setY(yBase);
                estado = EstadoMario.CAMINANDO;
            }
        }
    }
}
