package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class NaveJugador extends Nave{


    public NaveJugador(float movimientoSpeed, int escudo,
                       float xCentro, float yCentro,
                       float ancho, float alto,
                       float anchoLaser, float altoLaser,
                       float laserMovimientoSpeed, float tiempoEntreDisparo,
                       TextureRegion texturaAvion,
                       TextureRegion texturaEscudo,
                       TextureRegion texturaLaser) {
        super(movimientoSpeed, escudo, xCentro, yCentro, ancho, alto, anchoLaser, altoLaser, laserMovimientoSpeed, tiempoEntreDisparo, texturaAvion, texturaEscudo, texturaLaser);
    }

    @Override
    public Laser[] dispararLasers() {
        Laser[] lasers = new Laser[2];
        lasers[0] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.60f, boundingBox.y + boundingBox.height * 0.65f,anchoLaser, altoLaser, texturaLaser);
        lasers[1] = new Laser(laserMovimientoSpeed,boundingBox.x + boundingBox.width * 0.90f, boundingBox.y + boundingBox.height * 0.95f,anchoLaser, altoLaser, texturaLaser);

        tiempoDesdeUltTiro = 0;

        return lasers;
    }
}
