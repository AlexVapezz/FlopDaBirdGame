package FlopDaBird;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;

class GameScreen implements Screen {

    //Vamos a declarar una serie de variables de pantalla, gráficos, timing, los parametros del mundo y los objetos del juego.

    //PANTALLA:
    private Camera camera;
    private Viewport viewport;

    //GRÁFICOS:
    private SpriteBatch batch;
    private TextureAtlas textureAtlas; //De esta manera obtenemos el atlas o el pack de texturas
    private TextureRegion[] backgrounds; //De esta manera obtenemos cada una de las texturas del atlas
    private TextureRegion playerShipTextureReg, playerShieldTextureReg, enemyShipTextureReg, enemyShieldTextureReg, playerLaserTextureReg, enemyLaserTextureReg; //Aqui obtenemos cada una de las texturas

    //TIMING:
    private float[] backgroundOffsets = {0};
    private float backgroundScrollSpeed;

    //PARAMETROS DEL MUNDO:
    private final int WORLD_HEIGHT = 72;
    private final int WORLD_WIDTH = 128;

    //OBJETOS DEL JUEGO:
    private Nave playerShip;
    private Nave enemyShip;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;

    //Vamos a crear el constructor del GameScreen
    GameScreen() {

        camera = new OrthographicCamera(); //Esta es la típica cámara 2D sin perspectiva 3D de ningún tipo
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //Establecemos el atlas de texturas
        textureAtlas = new TextureAtlas("imagenes.atlas");

        //Establecemos los fondos que vayamos a usar, en este caso solo 1
        backgrounds = new TextureRegion[1];
        backgrounds[0] = textureAtlas.findRegion("world_background");

        backgroundScrollSpeed = WORLD_WIDTH;

        //Iniciamos las texturas
        playerShipTextureReg = textureAtlas.findRegion("PlayerShip");
        enemyShipTextureReg = textureAtlas.findRegion("EnemyShip");
        playerShieldTextureReg = textureAtlas.findRegion("shield2");
        enemyShieldTextureReg = textureAtlas.findRegion("shield1");
        playerLaserTextureReg = textureAtlas.findRegion("laserGreen");
        enemyLaserTextureReg = textureAtlas.findRegion("laserRed");

        //Establecemos los elementos del juego
        playerShip = new Nave(2, 3, WORLD_WIDTH/4, WORLD_HEIGHT/2, 20, 7, playerShipTextureReg, playerShieldTextureReg);

        enemyShip = new Nave(2, 1, WORLD_WIDTH*3/4, WORLD_HEIGHT/2, 20, 7, enemyShipTextureReg, enemyShieldTextureReg);

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        batch = new SpriteBatch(); //Esto tomará todos los cambios gráficos que hagamos en el juego y los mostrará  la vez

    }

    @Override
    public void show() {

    }

    //El metodo render se utiliza tanto para pintar elementos en pantalla como para realizar movimientos
    @Override
    public void render(float deltaTime) {
        //Mostramos todos los cambios por pantalla
        batch.begin();

        renderBackground(deltaTime);

        //Seccion de las naves enemigas
        enemyShip.pintar(batch);
        //Seccion de la nave del jugador
        playerShip.pintar(batch);
        //Seccion de los laseres

        //Seccion de las explosiones

        batch.end();
    }

    private void renderBackground(float deltaTime){

        //De esta manera le decimos a la velocidad que queremos a la que vaya
        backgroundOffsets[0] += deltaTime * backgroundScrollSpeed / 2;

        //Vamos a crear una función para hacer que la imagen de fondo vuelva a su punto de partida una vez llegue al final
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_WIDTH) { //Si supera el ancho...
                backgroundOffsets[layer] = 0; //...vuelve a cero
            }
            batch.draw(backgrounds[layer], -backgroundOffsets[layer], 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(backgrounds[layer], -backgroundOffsets[layer] + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        //Este método se encarga de cambiar la resolución de pantalla si esta cambia
        viewport.update(width, height, true); //Le decimos que actualice el tamaño y centre la cámara
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
