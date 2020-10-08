package mx.rmr.demojuego2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    // Texto
    private Texto texto;        // Dibuja textos en la pantalla
    private float puntos;

    // HUD
    private Stage escenaHUD;        // pad, botón disparo, marcador, etc
    private OrthographicCamera camaraHUD;
    private Viewport vistaHUD;

    // Música / Efectos de sonido
    private Music musicaFondo;      // Audios largos
    private Sound efectoSalto;      // Efectos cortos (WAV, MP3)

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
        crearTexto();
        cargarPuntos();
        crearHUD();
        crearAudio();

        //Gdx.input.setInputProcessor(new ProcesadorEntrada());
        Gdx.input.setInputProcessor(escenaHUD);
    }

    private void crearAudio() {
        // PPP
        // cargar preferencias
        // preguntar si la preferencia de sonido es true
        AssetManager manager = new AssetManager();
        manager.load("runner/marioBros.mp3", Music.class);  // programa la carga
        manager.load("runner/moneda.mp3", Sound.class);
        manager.finishLoading();    // ESPERA
        musicaFondo = manager.get("runner/marioBros.mp3");
        musicaFondo.setVolume(0.1f);
        musicaFondo.setLooping(true);
        musicaFondo.play();

        efectoSalto = manager.get("runner/moneda.mp3");

        // No la pref es false
    }

    private void crearHUD() {
        camaraHUD = new OrthographicCamera(ANCHO, ALTO);
        camaraHUD.position.set(ANCHO/2, ALTO/2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO, ALTO, camaraHUD);

        // Escena
        escenaHUD = new Stage(vistaHUD);
        // Crea el pad
        Skin skin = new Skin(); // Texturas para el pad
        skin.add("fondo", new Texture("runner/fondoPad.png"));
        skin.add("boton", new Texture("runner/botonPad.png"));
        // Configura la vista del pad
        Touchpad.TouchpadStyle estilo = new Touchpad.TouchpadStyle();
        estilo.background = skin.getDrawable("fondo");
        estilo.knob = skin.getDrawable("boton");
        // Crea el pad
        Touchpad pad = new Touchpad(64,estilo);     // Radio, estilo
        pad.setBounds(16,16,256,256);               // x,y - ancho,alto
        // Comportamiento del pad
        pad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Touchpad pad = (Touchpad)actor;
                if (pad.getKnobPercentX() > 0.20) { // Más de 20% de desplazamiento DERECHA
                    mario.setEstadoCaminando(EstadoCaminando.DERECHA);
                } else if ( pad.getKnobPercentX() < -0.20 ) {   // Más de 20% IZQUIERDA
                    mario.setEstadoCaminando(EstadoCaminando.IZQUIERDA);
                } else if (pad.getKnobPercentX()==0) {
                    //if (mario.getEstadoCaminando()==EstadoCaminando.DERECHA) {
                        //mario.setEstadoCaminando(EstadoCaminando.QUIETO_DERECHA);
                    //}
                    mario.setEstadoCaminando(EstadoCaminando.QUIETO);
                }
                // Y
                if (pad.getKnobPercentY()>0.5) {
                    if (mario.getEstado()!=EstadoMario.SALTANDO) {
                        mario.saltar();
                        efectoSalto.play();
                    }
                }
            }
        });
        pad.setColor(1,1,1,0.7f);   // Transparente
        // Crea la escena y agrega el pad
        escenaHUD.addActor(pad);

        // Botón de disparo
        Texture texturaDisparo = new Texture("space/disparo.png");
        TextureRegionDrawable region = new TextureRegionDrawable(new TextureRegion(
                texturaDisparo));

        ImageButton btnDisparo = new ImageButton(region);
        btnDisparo.setPosition(ANCHO*0.9f, 60);
        // Programar listener del botón
        btnDisparo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (arrBolasFuego.size < 20) {
                    BolaFuego bolaFuego = new BolaFuego(texturaBolaFuego, mario.sprite.getX(),
                            mario.sprite.getY() + mario.sprite.getHeight()*0.5F);
                    arrBolasFuego.add(bolaFuego);
                }
            }
        });
        escenaHUD.addActor(btnDisparo);
    }

    private void cargarPuntos() {
        Preferences prefs = Gdx.app.getPreferences("marcador");
        puntos = prefs.getFloat("PUNTOS", 0);
    }

    private void crearTexto() {
        texto = new Texto("runner/game.fnt");
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
        dibujarTexto();
        batch.end();

        // HUD ****************************
        batch.setProjectionMatrix(camaraHUD.combined);
        escenaHUD.draw();
    }

    private void dibujarTexto() {
        texto.mostrarMensaje(batch, "Super Mario Tec", ANCHO/2, 0.9f*ALTO);
        //puntos += Gdx.graphics.getDeltaTime();
        int puntosInt = (int)puntos;
        texto.mostrarMensaje(batch, "" + puntosInt, ANCHO/2*0.1f, 0.1f*ALTO);
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

        /*xFondo-=5;
        if (xFondo==-texturaFondo.getWidth()) {
            xFondo = 0;
        }*/

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
                    // Contar puntos
                    puntos += 25;
                    guardarPreferencias();
                    break;
                }
            }
        }
    }

    private void guardarPreferencias() {
        Preferences prefs = Gdx.app.getPreferences("marcador");
        prefs.putFloat("PUNTOS", puntos);
        prefs.flush();  // OBLIGATORIO
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
            Goomba goomba = new Goomba(texturaGoomba, mario.sprite.getX()+ANCHO/2, 60 + MathUtils.random(0,2)*100);  // 60, 120, 180
            arrEnemigos.add(goomba);
        }
        for (int i = arrEnemigos.size-1; i >= 0; i--) {
            Goomba goomba = arrEnemigos.get(i);
            if (goomba.sprite.getX() < mario.sprite.getX() - ANCHO/2 - goomba.sprite.getWidth()) {
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










