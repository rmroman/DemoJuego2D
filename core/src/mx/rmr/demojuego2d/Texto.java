package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Texto
{
    private BitmapFont font;

    public Texto(String archivo) {
        font = new BitmapFont(Gdx.files.internal(archivo));
    }

    // Centrado
    public void mostrarMensaje(SpriteBatch batch, String mensaje, float x, float y) {
        GlyphLayout glyph = new GlyphLayout();
        glyph.setText(font, mensaje);
        float anchoTexto = glyph.width;
        font.draw(batch, glyph, x-anchoTexto/2, y);
    }
}
