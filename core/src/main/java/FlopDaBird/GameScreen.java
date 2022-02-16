package FlopDaBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;

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
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f; //Esta medida nos ayudará a que cuando pulsemos cerca del elemento en la pantalla no tiemble el objeto

    //OBJETOS DEL JUEGO:
    private NaveJugador playerShip;
    private NaveEnemiga enemyShip;
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
        playerShip = new NaveJugador(60, 3, WORLD_WIDTH/4, WORLD_HEIGHT/2, 17, 5, 5, 0.65f, 45, 0.5f, playerShipTextureReg, playerShieldTextureReg, playerLaserTextureReg);

        enemyShip = new NaveEnemiga(55, 1, MainClass.random.nextFloat()*(WORLD_WIDTH - 17) + 5, WORLD_HEIGHT - 5, 17, 5, 5, 0.65f, 50, 1.2f, enemyShipTextureReg, enemyShieldTextureReg, enemyLaserTextureReg);

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

        //Con este método detectamos las entradas de teclado
        detectarInput(deltaTime);

        //Mediante el siguiente metodo dotaremos de movimiento a los enemigos
        moverEnemigos(deltaTime);

        playerShip.update(deltaTime);
        enemyShip.update(deltaTime);

        //Creamos una funcion encargada del fondo del juego
        renderBackground(deltaTime);

        //Seccion de las naves enemigas
        enemyShip.pintar(batch);

        //Seccion de la nave del jugador
        playerShip.pintar(batch);

        //Creamos una funcion encargada de los láseres (pintarlo, movimientos...etc)
        renderLasers(deltaTime);

        //Debemos detectar las colisiones entre los mismos laseres y aviones
        detectarColisiones();

        //Creamos una funcion encargada de las explosiones
        renderExplosions(deltaTime);

        batch.end();
    }

    private void detectarInput(float deltaTime){
        //Entradas de teclado

        //Indicamos la distancia máxima de movimiento posible del avion

        float limiteIzq, limiteDer, limiteArriba, limiteAbajo;
        limiteIzq = -playerShip.boundingBox.x; //Limite a la izquierda (negativo en eje x)
        limiteAbajo = -playerShip.boundingBox.y; //Lo mismo que arriba pero en eje y
        limiteDer = (float) WORLD_WIDTH/2 - playerShip.boundingBox.x - playerShip.boundingBox.width; //Limite a la derecha (le ponemos como límite la mitad del width del mundo)
        limiteArriba = WORLD_HEIGHT -playerShip.boundingBox.y - playerShip.boundingBox.height; //Limite hacia arriba

        //----------------------------------------EXPLICACION DE LO DE ABAJO--------------------------------------------------------------------------------------------------------------------

        //  Vamos a poner como ejemplo el primero:
        //
        //  if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && limiteDer > 0){ //Si estamos pulsando la recla hacia la derecha y no estamos en el limite...
        //            float xCambio = playerShip.movimientoSpeed*deltaTime; //Calculamos la posicion con respecto al eje x
        //            xCambio = Math.min(xCambio, limiteDer); //Obtenemos el valor minimo del limite y la posicion para que nunca pase de este
        //            playerShip.transicion(xCambio, 0f); //Y llamamos a la funcion transicion para que aplique el movimiento sobre el objeto

        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && limiteDer > 0){ //Si estamos pulsando la recla hacia la derecha y no estamos en el limite...
            float xCambio = playerShip.movimientoSpeed*deltaTime;
            xCambio = Math.min(xCambio, limiteDer);
            playerShip.transicion(xCambio, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && limiteArriba > 0){ //Si estamos pulsando la recla hacia la derecha y no estamos en el limite...
            float yCambio = playerShip.movimientoSpeed*deltaTime;
            yCambio = Math.min(yCambio, limiteArriba);
            playerShip.transicion(0f, yCambio);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && limiteIzq < 0){ //Si estamos pulsando la recla hacia la derecha y no estamos en el limite...
            float xCambio = -playerShip.movimientoSpeed*deltaTime;
            xCambio = Math.max(xCambio, limiteIzq);
            playerShip.transicion(xCambio, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && limiteAbajo < 0){ //Si estamos pulsando la recla hacia la derecha y no estamos en el limite...
            float yCambio = -playerShip.movimientoSpeed*deltaTime;
            yCambio = Math.max(yCambio, limiteAbajo);
            playerShip.transicion(0f, yCambio);
        }

        //Entradas táctiles (o del raton)
        if (Gdx.input.isTouched()){
            //Primero debemos obtener la posicion en la que tocamos en la pantalla
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //Despues convertimos esa posicion dentro de los parametros del mundo
            Vector2 puntoContacto = new Vector2(xTouchPixels, yTouchPixels); //Vector2 obtiene dos puntos float, de esta manera podemos localizarlo
            puntoContacto = viewport.unproject(puntoContacto); //Esta clase obtiene los puntos (vector) de la pantalla y los transforma a los puntos o coordenadas del mundo

            //Calculamos las diferencias de x e y
            Vector2 centroAvionJugador = new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width/2, playerShip.boundingBox.y + playerShip.boundingBox.height/2);
            float distanciaContacto = puntoContacto.dst(centroAvionJugador); //Esto lo que hace es calcularnos el teorema de pitagoras automaticamente entre el punto x de la nave y el punto y donde tocamos

            if (distanciaContacto > TOUCH_MOVEMENT_THRESHOLD){
                float xDiferenciaContacto = puntoContacto.x - centroAvionJugador.x;
                float yDiferenciaContacto = puntoContacto.y - centroAvionJugador.y;

                //Lo adaptamos a la velocidad máxima del avion
                float xMovimiento = xDiferenciaContacto / distanciaContacto * playerShip.movimientoSpeed * deltaTime;
                float yMovimiento = yDiferenciaContacto / distanciaContacto * playerShip.movimientoSpeed * deltaTime;

                if (xMovimiento > 0) xMovimiento = Math.min(xMovimiento, limiteDer);
                else xMovimiento = Math.max(xMovimiento, limiteIzq);

                if (yMovimiento > 0) yMovimiento = Math.min(yMovimiento, limiteArriba);
                else yMovimiento = Math.max(yMovimiento, limiteAbajo);

                playerShip.transicion(xMovimiento, yMovimiento);
            }
        }
    }

    private void moverEnemigos(float deltaTime){
        //Indicamos la distancia máxima de movimiento posible del avion

        float limiteIzq, limiteDer, limiteArriba, limiteAbajo;
        limiteIzq = (float) WORLD_WIDTH/2 -enemyShip.boundingBox.x; //Limite a la izquierda (negativo en eje x)
        limiteAbajo = -enemyShip.boundingBox.y; //Lo mismo que arriba pero en eje y
        limiteDer = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width; //Limite a la derecha (le ponemos como límite la mitad del width del mundo)
        limiteArriba = WORLD_HEIGHT -enemyShip.boundingBox.y - enemyShip.boundingBox.height; //Limite hacia arriba

        float xMovimiento = enemyShip.getVectorDeDireccion().x * enemyShip.movimientoSpeed * deltaTime;
        float yMovimiento = enemyShip.getVectorDeDireccion().y * enemyShip.movimientoSpeed * deltaTime;

        if (xMovimiento > 0) xMovimiento = Math.min(xMovimiento, limiteDer);
        else xMovimiento = Math.max(xMovimiento, limiteIzq);

        if (yMovimiento > 0) yMovimiento = Math.min(yMovimiento, limiteArriba);
        else yMovimiento = Math.max(yMovimiento, limiteAbajo);

        enemyShip.transicion(xMovimiento, yMovimiento);
    }

    private void detectarColisiones(){
        //Para cada laser del jugador, comprobamos cuando impacta el avion enemigo
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (enemyShip.interceptar(laser.boundingBox)){ //Si el laser intercepta la nave enemiga...
                enemyShip.impacto(laser);
                iterator.remove(); //Borramos el laser al impactar
            }
        }

        //Para cada laser enemigo, detectamos cuando impacta con el avion del jugador
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            if (playerShip.interceptar(laser.boundingBox)){ //Si el laser intercepta la nave del jugador...
                playerShip.impacto(laser);
                iterator.remove(); //Borramos el laser al impactar
            }
        }
    }

    private void renderExplosions(float deltaTime){

    }

    private void renderLasers(float deltaTime){
        //Laser del jugador
        if (playerShip.consultaDisparo()){
            Laser[] lasers = playerShip.dispararLasers();
            for (Laser laser: lasers){
                playerLaserList.add(laser);
            }
        }
        //Laser del enemigo
        if (enemyShip.consultaDisparo()){
            Laser[] lasers = enemyShip.dispararLasers();
            for (Laser laser: lasers){
                enemyLaserList.add(laser);
            }
        }
        //Pintamos los laseres y eliminamos los laseres antiguos
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()){
            Laser laser = iterator.next();
            laser.pintar(batch); //Pintamos el laser
            laser.boundingBox.x += laser.movimientoSpeed*deltaTime; //Aplicamos el movimiento del laser para que vaya a la derecha
            if (laser.boundingBox.x > WORLD_WIDTH){ //En cuanto pase la pantalla...
                iterator.remove(); //...eliminamos el laser
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()){
            Laser laser = iterator.next();
            laser.pintar(batch); //Pintamos el laser
            laser.boundingBox.x -= laser.movimientoSpeed*deltaTime; //Aplicamos el movimiento igual que en el anterior pero a la izquierda
            if (laser.boundingBox.x + laser.boundingBox.width < 0){ //En cuanto pase la pantalla...
                iterator.remove(); //...eliminamos el laser
            }
        }
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
