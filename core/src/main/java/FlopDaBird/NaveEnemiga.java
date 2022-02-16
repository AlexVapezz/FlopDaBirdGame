package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class NaveEnemiga extends Nave{

    Vector2 vectorDeDireccion;
    float tiempoDesdeUltMovimiento = 0; //Esto nos servira para calcular el tiempo desde el ultimo movimiento
    float frecuenciaCambioDireccion = 0.75f; //Y aqui le indicamos cada cuanto queremos que cambie de direccion

    public NaveEnemiga(float movimientoSpeed, int escudo,
                       float xCentro, float yCentro,
                       float ancho, float alto,
                       float anchoLaser, float altoLaser,
                       float laserMovimientoSpeed, float tiempoEntreDisparo,
                       TextureRegion texturaAvion,
                       TextureRegion texturaEscudo,
                       TextureRegion texturaLaser) {
        super(movimientoSpeed, escudo, xCentro, yCentro, ancho, alto, anchoLaser, altoLaser, laserMovimientoSpeed, tiempoEntreDisparo, texturaAvion, texturaEscudo, texturaLaser);

        vectorDeDireccion = new Vector2(-1, 0);
    }

    public Vector2 getVectorDeDireccion() {
        return vectorDeDireccion;
    }

    private void direccionVectorRandom(){
        double bearing = MainClass.random.nextDouble()*6.28318; //Esto nos dara un numero entre 0 y 2 por PI
        vectorDeDireccion.x = (float)Math.sin(bearing); //Al hacer el seno obtenemos la direccion de x
        vectorDeDireccion.y = (float)Math.cos(bearing); //Al hacer el coseno obtenemos la direccion de y
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime); //Esto llamara al metodo update padre y hara que se ejecute este tambien
        tiempoDesdeUltMovimiento = tiempoDesdeUltTiro + deltaTime; //Vamos sumandole al tiempo desde el ultimo movimiento de la nave secundaria el tiempo que va transcurriendo
        if (tiempoDesdeUltMovimiento > frecuenciaCambioDireccion){ //Si el tiempo desde el ultimo movimiento de la nave enemiga es mayor al tiempo de cambio de direccion...
            direccionVectorRandom(); //Movemos el avion enemigo
            tiempoDesdeUltMovimiento = tiempoDesdeUltMovimiento - frecuenciaCambioDireccion; //Lo reducimos para que se realice de manera infinita
        }
    }

    //El siguiente método nos permitirá dispara y generar los nuevos objetos láser
    @Override
    public Laser[] dispararLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.36f, boundingBox.y + boundingBox.height * 0.65f,anchoLaser, altoLaser, texturaLaser);
        lasers[1] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.10f, boundingBox.y + boundingBox.height * 0.95f,anchoLaser, altoLaser, texturaLaser);

        tiempoDesdeUltTiro = 0;

        return lasers;
    }
}
