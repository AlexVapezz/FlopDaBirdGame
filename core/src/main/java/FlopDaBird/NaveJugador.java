package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class NaveJugador extends Nave{

    int vidas;

    public NaveJugador(float movimientoSpeed, int escudo,
                       float xCentro, float yCentro,
                       float ancho, float alto,
                       float anchoLaser, float altoLaser,
                       float laserMovimientoSpeed, float tiempoEntreDisparo,
                       TextureRegion texturaAvion,
                       TextureRegion texturaEscudo,
                       TextureRegion texturaLaser) {
        super(movimientoSpeed, escudo, xCentro, yCentro, ancho, alto, anchoLaser, altoLaser, laserMovimientoSpeed, tiempoEntreDisparo, texturaAvion, texturaEscudo, texturaLaser);
        vidas = 3;
    }

    //El siguiente método nos permitirá dispara y generar los nuevos objetos láser

    @Override
    public Laser[] dispararLasers() {
        Laser[] lasers = new Laser[2]; //Creamos dos nuevos laseres
        lasers[0] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.60f, boundingBox.y + boundingBox.height * 0.65f,anchoLaser, altoLaser, texturaLaser); //Primer laser
        lasers[1] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.90f, boundingBox.y + boundingBox.height * 0.95f,anchoLaser, altoLaser, texturaLaser); //Segundo laser

        tiempoDesdeUltTiro = 0; //Reiniciamos el contador para decirle que acabamos de disparar

        return lasers;
    }
}
