package FlopDaBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

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
    private Texture texturaExplosion;

    //TIMING:
    private float[] backgroundOffsets = {0};
    private float backgroundScrollSpeed;
    private float tiempoEntreSpawnEnemigo = 2.5f;
    private float timerSpawnEnemigo = 0;

    //PARAMETROS DEL MUNDO:
    private final float WORLD_HEIGHT = 72;
    private final float WORLD_WIDTH = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f; //Esta medida nos ayudará a que cuando pulsemos cerca del elemento en la pantalla no tiemble el objeto

    //OBJETOS DEL JUEGO:
    private NaveJugador playerShip;
    private LinkedList<NaveEnemiga> enemyShipLista;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> listaExplosion;
    private int puntuacion = 0;

    //Aqui vamos a declarar el HUD del juego
    BitmapFont font;
    float hudMargenVertical, hudMargenIzq, hudMargenDer, hudCentro, hudFila1Y, hudFila2Y, hudAnchoSeccion;


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

        texturaExplosion = new Texture("explosion.png");

        //Establecemos los elementos del juego
        playerShip = new NaveJugador(40, 3, WORLD_WIDTH/4, WORLD_HEIGHT/2, 17, 5, 5, 0.65f, 45, 0.5f, playerShipTextureReg, playerShieldTextureReg, playerLaserTextureReg);

        enemyShipLista = new LinkedList<>();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        listaExplosion = new LinkedList<>();

        batch = new SpriteBatch(); //Esto tomará todos los cambios gráficos que hagamos en el juego y los mostrará  la vez

        prepararHUD();
    }

    //Vamos a crear una funcion encargada de preparar el HUD del juego
    private void prepararHUD(){
        //Creamos el bitmap de la fuente que vamos a usar
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/8-bit Arcade In.ttf")); //Creamos la fuente usando el archivo
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72; //Le añadimos el tamaño pero este no sera el tamaño final
        fontParameter.borderWidth = 3.6f; //El borde
        fontParameter.color = new Color(1,1,1,0.3f); //Le ponemos el color a la fuente siendo este blanco medio transparente
        fontParameter.borderColor = new Color(0,0,0,0.3f); //Y lo mismo al borde pero en negro

        font = fontGenerator.generateFont(fontParameter);

        //Ajustamos la fuente para encajarla en nuestro juego
        font.getData().setScale(0.095f); //Lo escalamos a un tamaño razonable

        //Calculamos los margenes (Aqui guardaremos la informacion referida a los margenes y posicionamiento del HUD dle jugador)
        hudMargenVertical = font.getCapHeight() / 2;
        hudMargenIzq = hudMargenVertical;
        hudMargenDer = WORLD_WIDTH * 2 / 3 - hudMargenIzq;
        hudCentro = WORLD_WIDTH / 3;
        hudFila1Y = WORLD_HEIGHT - hudMargenVertical;
        hudFila2Y = hudFila1Y - hudMargenVertical -font.getCapHeight();
        hudAnchoSeccion = WORLD_WIDTH / 3;
    }

    @Override
    public void show() {

    }

    //El metodo render se utiliza tanto para pintar elementos en pantalla como para realizar movimientos
    @Override
    public void render(float deltaTime) {
        //Mostramos todos los cambios por pantalla
        batch.begin();

        //Creamos una funcion encargada del fondo del juego
        renderBackground(deltaTime);

        //Con este método detectamos las entradas de teclado
        detectarInput(deltaTime);
        playerShip.update(deltaTime);

        spawnearAvionesEnemigos(deltaTime);

        //Vamos a declarar un iterador para recorrer la lista de objetos Naves enemigas
        ListIterator<NaveEnemiga> iteradorNaveEnemiga = enemyShipLista.listIterator();
        while (iteradorNaveEnemiga.hasNext()){ //Por cada nave enemiga de la lista...
            NaveEnemiga enemyShip = iteradorNaveEnemiga.next();
            //Dotaremos de movimiento a los enemigos
            moverEnemigo(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            //Pintaremos las naves enemigas
            enemyShip.pintar(batch);
        }

        //Seccion de la nave del jugador
        playerShip.pintar(batch);

        //Creamos una funcion encargada de los láseres (pintarlo, movimientos...etc)
        renderLasers(deltaTime);

        //Debemos detectar las colisiones entre los mismos laseres y aviones
        detectarColisiones();

        //Creamos una funcion encargada de las explosiones
        renderExplosions(deltaTime);

        //Creamos una funcion encargada del HUD
        renderHUD();

        batch.end();
    }

    private void renderHUD(){
        //Aqui pintamos solo los nombres correspondientes pero no los valores, usando las medidas que hemos declarado anteriormente
        font.draw(batch, "Puntuacion", hudMargenIzq, hudFila1Y, hudAnchoSeccion, Align.left, false);
        font.draw(batch, "Escudo", hudCentro, hudFila1Y, hudAnchoSeccion, Align.center, false);
        font.draw(batch, "Vidas", hudMargenDer, hudFila1Y, hudAnchoSeccion, Align.right, false);
        //Aqui pintamos la segunda fila donde estará la puntuacion, los escudos y la vida
        font.draw(batch, String.format(Locale.getDefault(), "%06d", puntuacion), hudMargenIzq, hudFila2Y, hudAnchoSeccion, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.escudo), hudCentro, hudFila2Y, hudAnchoSeccion, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.vidas), hudMargenDer, hudFila2Y, hudAnchoSeccion, Align.right, false);
    }

    //Declaramos una funcion para spawnear nuevos enemigos
    private void spawnearAvionesEnemigos(float deltaTime){
        timerSpawnEnemigo = timerSpawnEnemigo + deltaTime; //Aqui vamos contando el tiempo que tardan en spawnear

        if (timerSpawnEnemigo > tiempoEntreSpawnEnemigo) { //Si ese tiempo es menor que el indicado como limite, spawneamos un nuevo enemigo
            enemyShipLista.add(new NaveEnemiga(12, 1, WORLD_WIDTH - 17, MainClass.random.nextFloat() * (WORLD_HEIGHT - 5) + 10, 17, 5, 5, 0.65f, 40, 0.9f, enemyShipTextureReg, enemyShieldTextureReg, enemyLaserTextureReg));
            timerSpawnEnemigo = timerSpawnEnemigo - tiempoEntreSpawnEnemigo;
        }
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

    private void moverEnemigo(NaveEnemiga enemyShip, float deltaTime){
        //Indicamos la distancia máxima de movimiento posible del avion

        float limiteIzq, limiteDer, limiteArriba, limiteAbajo;
        limiteIzq = (float) WORLD_WIDTH/2 -enemyShip.boundingBox.x; //Limite a la izquierda (negativo en eje x)
        limiteAbajo = -enemyShip.boundingBox.y; //Lo mismo que arriba pero en eje y
        limiteDer = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width; //Limite a la derecha (le ponemos como límite la mitad del width del mundo)
        limiteArriba = WORLD_HEIGHT -enemyShip.boundingBox.y - enemyShip.boundingBox.height; //Limite hacia arriba

        float xMovimiento = enemyShip.getVectorDeDireccion().x * enemyShip.movimientoSpeed * deltaTime;
        float yMovimiento = enemyShip.getVectorDeDireccion().y * enemyShip.movimientoSpeed * deltaTime;

        //Impedimos que el avion enemigo salga de los limites establecidos en el eje x
        if (xMovimiento > 0) xMovimiento = Math.min(xMovimiento, limiteDer);
        else xMovimiento = Math.max(xMovimiento, limiteIzq);

        //Y lo mismo con el eje y
        if (yMovimiento > 0) yMovimiento = Math.min(yMovimiento, limiteArriba);
        else yMovimiento = Math.max(yMovimiento, limiteAbajo);

        //Realizamos la transicion teniendo en cuenta estos movimientos
        enemyShip.transicion(xMovimiento, yMovimiento);
    }

    private void detectarColisiones(){
        //Para cada laser del jugador, comprobamos cuando impacta el avion enemigo
        ListIterator<Laser> iteratorLaser = playerLaserList.listIterator();
        while (iteratorLaser.hasNext()) {
            Laser laser = iteratorLaser.next();
            ListIterator<NaveEnemiga> iteradorNavesEnemigas = enemyShipLista.listIterator(); //Necesitamos recorrer la lista de naves enemigas
            while (iteradorNavesEnemigas.hasNext()){
                NaveEnemiga enemyShip = iteradorNavesEnemigas.next(); //Declaramos cada una de las naves enemigas

                if (enemyShip.interceptar(laser.boundingBox)) { //Si el laser intercepta la nave enemiga...
                    if (enemyShip.impacto(laser)){ //Impacto ¡BOOM!
                        iteradorNavesEnemigas.remove(); //Eliminamos la nave enemiga
                        listaExplosion.add(new Explosion(texturaExplosion, new Rectangle(enemyShip.boundingBox), 0.7f)); //Creamos la explosion una vez impacte
                        puntuacion = puntuacion + 100; //Por cada avion enemigo que destruyamos se añaden 100 puntos a la puntuacion
                    }
                    iteratorLaser.remove(); //Borramos el laser al impactar
                    break; //Si impactamos con una nave no queremos continuar comprobando
                }
            }
        }

        //Para cada laser enemigo, detectamos cuando impacta con el avion del jugador
        iteratorLaser = enemyLaserList.listIterator();
        while (iteratorLaser.hasNext()) {
            Laser laser = iteratorLaser.next();
            if (playerShip.interceptar(laser.boundingBox)){ //Si el laser intercepta la nave del jugador...
                if (playerShip.impacto(laser)){
                    listaExplosion.add(new Explosion(texturaExplosion, new Rectangle(playerShip.boundingBox), 1.6f)); //Creamos la explosion una vez impacte
                    playerShip.escudo = 10; //En cuanto el jugador muera
                    if (playerShip.vidas > 0){
                        playerShip.vidas--;
                    }else {

                    }
                }
                iteratorLaser.remove(); //Borramos el laser al impactar
            }
        }
    }

    private void renderExplosions(float deltaTime){
        ListIterator<Explosion> iteradorExplosiones = listaExplosion.listIterator();
        while (iteradorExplosiones.hasNext()){
            Explosion explosion = iteradorExplosiones.next();
            explosion.update(deltaTime);
            if (explosion.consultarFinal()){
                iteradorExplosiones.remove();
            }else {
                explosion.pintar(batch);
            }
        }
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
        ListIterator<NaveEnemiga> iteradorNavesEnemigas = enemyShipLista.listIterator();
        while (iteradorNavesEnemigas.hasNext()){
            NaveEnemiga enemyShip = iteradorNavesEnemigas.next();
            if (enemyShip.consultaDisparo()){
                Laser[] lasers = enemyShip.dispararLasers();
                for (Laser laser: lasers){
                    enemyLaserList.add(laser);
                }
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
