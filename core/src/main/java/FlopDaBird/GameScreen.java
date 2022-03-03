package FlopDaBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

import FlopDaBird.NaveEnemiga;
import FlopDaBird.NaveJugador;

class GameScreen implements Screen {

    //Vamos a declarar una serie de variables de pantalla, gráficos, timing, los parametros del mundo y los objetos del juego.

    //PANTALLA:
    private Camera camera;
    private Viewport viewport;
    private String state = "ready";

    //GRÁFICOS:
    private SpriteBatch batch;
    private TextureAtlas textureAtlas; //De esta manera obtenemos el atlas o el pack de texturas
    private TextureRegion[] backgrounds; //De esta manera obtenemos cada una de las texturas del atlas
    private TextureRegion playerShipTextureReg, playerShieldTextureReg, enemyShipTextureReg, enemyShieldTextureReg, playerLaserTextureReg, enemyLaserTextureReg; //Aqui obtenemos cada una de las texturas
    private Texture texturaExplosion; //Obtenemos la textura de la explosion
    private Texture title; //La del titulo
    private Texture tap; //La de press start
    private Texture end; //La de game over

    //SONIDO:
    private Sound explosionSound; //Sonido de la explosion
    private Music backgroundMusic; //Sonido de la musica de fondo
    private Sound gameoverSound; //Sonido que realiza al morir

    //TIMING:
    private float[] backgroundOffsets = {0};
    private float backgroundScrollSpeed;
    private float tiempoEntreSpawnEnemigo = 2.0f;
    private float timerSpawnEnemigo = 0;

    //PARAMETROS DEL MUNDO:
    private final float WORLD_HEIGHT = 72;
    private final float WORLD_WIDTH = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f; //Esta medida nos ayudará a que cuando pulsemos cerca del elemento en la pantalla no tiemble el objeto

    //OBJETOS DEL JUEGO:
    private NaveJugador playerShip; //La nave del jugador
    private LinkedList<NaveEnemiga> enemyShipLista; //Lista de naves enemigas
    private LinkedList<Laser> playerLaserList; //Lista de los laseres que se van generando
    private LinkedList<Laser> enemyLaserList; //Lista de los laseres enemigos que se van generando
    private LinkedList<Explosion> listaExplosion; //Lista de las explosiones que van apareciendo
    private int puntuacion = 0; //Variable de la puntuacion

    //Aqui vamos a declarar el HUD del juego
    BitmapFont font; //La fuente que vamos a usar para el texto del HUD
    float hudMargenVertical, hudMargenIzq, hudMargenDer, hudCentro, hudFila1Y, hudFila2Y, hudAnchoSeccion; //Establecemos los margenes y los elementos a mostrar en el HUD


    //Vamos a crear el constructor del GameScreen
    GameScreen() {

        camera = new OrthographicCamera(); //Esta es la típica cámara 2D sin perspectiva 3D de ningún tipo
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); //Establecemos el viewport sobre el que desarrollaremos nuestro juego

        //Establecemos el atlas de texturas
        textureAtlas = new TextureAtlas("imagenes.atlas");

        //Establecemos los fondos que vayamos a usar, en este caso solo 1
        backgrounds = new TextureRegion[1];
        backgrounds[0] = textureAtlas.findRegion("world_background"); //Cogemos la posicion 0 que es la correspondiente al fondo del juego

        backgroundScrollSpeed = WORLD_WIDTH; //Establecemos la velocidad a la que va a moverse el fondo

        //Establecemos los sonidos que usaremos
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sonidos/explosion.ogg"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sonidos/background.m4a"));
        gameoverSound = Gdx.audio.newSound(Gdx.files.internal("sonidos/gameover.m4a"));

        //Iniciamos las texturas
        playerShipTextureReg = textureAtlas.findRegion("PlayerShip");
        enemyShipTextureReg = textureAtlas.findRegion("EnemyShip");
        playerShieldTextureReg = textureAtlas.findRegion("shield2");
        enemyShieldTextureReg = textureAtlas.findRegion("shield1");
        playerLaserTextureReg = textureAtlas.findRegion("laserGreen");
        enemyLaserTextureReg = textureAtlas.findRegion("laserRed");

        texturaExplosion = new Texture("explosion.png");

        //Establecemos los elementos del juego
        playerShip = new NaveJugador(45, 10, WORLD_WIDTH/4, WORLD_HEIGHT/2, 17, 5, 5, 0.65f, 50, 0.30f, playerShipTextureReg, playerShieldTextureReg, playerLaserTextureReg);

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

        //Creamos unos if para que en funcion del estado del juego muestre una cosa u otra en la pantalla
        if (state == "ready"){

            //Establecemos musica de fondo
            backgroundMusic.setVolume(0.8f); //Establecemos volumen
            backgroundMusic.setLooping(true); //Establecemos el looping en true para que se reproduzca en bucle
            backgroundMusic.play(); //Iniciamos la cancion de fondo

            title = new Texture("FLOPDAPLANE.png"); //Establecemos el titulo
            batch.draw(title, WORLD_WIDTH / 6, WORLD_HEIGHT / 2, title.getWidth() / 4, title.getHeight() / 4); //Lo pintamos

            tap = new Texture("PressStart.png"); //Establecemos el press start
            batch.draw(tap, WORLD_WIDTH / 4, WORLD_HEIGHT / 3, title.getWidth() / 5, title.getHeight() / 5); //Lo pintamos

            renderReady(); //Llamamos al metodo encargado de detectar cuando tocamos la pantalla para jugar

        }else if (state == "running"){

            //Con este método detectamos las entradas de teclado y de la pantalla al tocar
            detectarInput(deltaTime); //Este metodo es el encargado de detectar cuando tocamos la pantalla al jugar
            playerShip.update(deltaTime);

            spawnearAvionesEnemigos(deltaTime); //Este metodo se encarga de generar los aviones enemigos

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

        }else if (state == "gameover"){
            backgroundMusic.stop(); //Cuando morimos paramos la musica
            long id = gameoverSound.play(0.3f); //Establecemos el sonido de game over, le damos el volumen y lo reproducimos
            gameoverSound.setPitch(id,3); //Establecemos la pista en la que se va a reproducir
            gameoverSound.setLooping(id, false); //En esta ocasion no se reproduce en bucle
            gameoverSound.dispose(); //Eliminamos el sonido
            gameoverSound = Gdx.audio.newSound(Gdx.files.internal("sonidos/gameover.m4a")); //Volvemos a cargar el sonido para la proxima vez que vayamos a usarlo
            end = new Texture("gameover.png"); //Cargamos la imagen de game over
            batch.draw(end, WORLD_WIDTH / 5, WORLD_HEIGHT / 2, end.getWidth() / 4, end.getHeight() / 4); //Pintamos la imagen

            renderGameOver(); //Metodo encargado de comprobar cuando tocamos la pantalla game over y mandarnos de vuelta al estado ready
        }

        batch.end();
    }

    private void renderGameOver(){
        if (Gdx.input.justTouched()){ //Si tocamos la pantalla
            end.dispose(); //Eliminamos la imagen de game over
            state = "ready"; //Establecemos el estado a ready para volver a la pantalla de inicio
            playerShip.vidas = 3; //Reseteamos las vidas del jugador...
            playerShip.escudo = 10; //Los escudos del mismo...
            puntuacion = 0; //Y la puntuacion de este

            enemyShipLista.clear(); //Limpiamos la lista de enemigos
            enemyLaserList.clear(); //Limpiamos la lista de laseres enemigos
            playerLaserList.clear(); //Limpiamos la lista de laseres del jugador
            listaExplosion.clear(); //Limpiamos la lista de las explosiones
        }
    }

    private void renderReady(){
        if (Gdx.input.justTouched()){ //En cuanto toquemos la pantalla de inicio
            title.dispose(); //Eliminamos el titulo
            tap.dispose(); //Eliminamos el press start
            state = "running"; //Cambiamos el estado para ejecutar el juego
        }
    }

    private void renderHUD(){
        //Aqui pintamos solo los nombres correspondientes pero no los valores, usando las medidas que hemos declarado anteriormente
        font.draw(batch, "Puntuacion", hudMargenIzq, hudFila1Y -2.5f, hudAnchoSeccion, Align.left, false);
        font.draw(batch, "Escudo", hudCentro, hudFila1Y - 2.5f, hudAnchoSeccion, Align.center, false);
        font.draw(batch, "Vidas", hudMargenDer, hudFila1Y - 2.5f, hudAnchoSeccion, Align.right, false);
        //Aqui pintamos la segunda fila donde estará la puntuacion, los escudos y la vida
        font.draw(batch, String.format(Locale.getDefault(), "%06d", puntuacion), hudMargenIzq, hudFila2Y - 2.5f, hudAnchoSeccion, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.escudo), hudCentro, hudFila2Y - 2.5f, hudAnchoSeccion, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.vidas), hudMargenDer, hudFila2Y - 2.5f, hudAnchoSeccion, Align.right, false);
    }

    //Declaramos una funcion para spawnear nuevos enemigos
    private void spawnearAvionesEnemigos(float deltaTime){
        timerSpawnEnemigo = timerSpawnEnemigo + deltaTime; //Aqui vamos contando el tiempo que tardan en spawnear

        if (timerSpawnEnemigo > tiempoEntreSpawnEnemigo) { //Si ese tiempo es menor que el indicado como limite, spawneamos un nuevo enemigo
            enemyShipLista.add(new NaveEnemiga(12, 1, WORLD_WIDTH - 17, MainClass.random.nextFloat() * (WORLD_HEIGHT - 5) + 10, 17, 5, 5, 0.65f, 50, 1.25f, enemyShipTextureReg, enemyShieldTextureReg, enemyLaserTextureReg));
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
                        state = "gameover";
                    }
                }
                iteratorLaser.remove(); //Borramos el laser al impactar
            }
        }
    }

    private void renderExplosions(float deltaTime){
        ListIterator<Explosion> iteradorExplosiones = listaExplosion.listIterator(); //Iniciamos el iterador de explosiones
        while (iteradorExplosiones.hasNext()){ //Establecemos un bucle de explosiones
            Explosion explosion = iteradorExplosiones.next(); //Iniciamos una explosion
            explosion.update(deltaTime); //La actualizamos
            if (explosion.consultarFinal()){ //Cuando acabe
                iteradorExplosiones.remove(); //La eliminamos
            }else { //Si no, la pintamos
                explosion.pintar(batch); //Pintamos textura
                long id = explosionSound.play(0.3f); //Reproducimos sonido de explosion
                explosionSound.setPitch(id,2); // Establecemos la pista en la que se reproducira
                explosionSound.setLooping(id, false); //No queremos reproducirla en bucle
                explosionSound.dispose(); //Eliminamos el sonido
                explosionSound = Gdx.audio.newSound(Gdx.files.internal("sonidos/explosion.ogg")); //Lo volvemos a cargar para tenerlo de nuevo disponible
            }
        }
    }

    private void renderLasers(float deltaTime){
        //Laser del jugador
        if (playerShip.consultaDisparo()){
            Laser[] lasers = playerShip.dispararLasers(); //Iniciamos la accion de disparar laseres y lo añadimos a la lista
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
        batch.setProjectionMatrix(camera.combined); //Llamamos a setProjectionMatrix para tener en cuenta los objetos del mundo y el propio fondo
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
