package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Nave {

    //Vamos a declarar las caractericticas de la nave/avion
    float movimientoSpeed; //Declaramos la velocidad del avion
    int escudo;

    //Vamos a declarar las posiciones y las dimension
    float xPosition, yPosition; //La posicion del avion
    float ancho, alto; //El ancho y alto del avion

    //Vamos a declarar la parte gráfica de la nave
    TextureRegion texturaAvion;
    TextureRegion texturaEscudo;
    TextureRegion texturaLaser;

    //Generamos el constructor
    public Nave(float movimientoSpeed, int escudo, float xCentro, float yCentro, float ancho, float alto, TextureRegion texturaAvion, TextureRegion texturaEscudo) {
        this.movimientoSpeed = movimientoSpeed;
        this.escudo = escudo;
        this.xPosition = xCentro - ancho/2;
        this.yPosition = yCentro - alto/2;
        this.ancho = ancho;
        this.alto = alto;
        this.texturaAvion = texturaAvion;
        this.texturaEscudo = texturaEscudo;
    }

    //Pintamos el avion y el escudo
    public void pintar(Batch batch){
        batch.draw(texturaAvion, xPosition, yPosition, ancho, alto);
        if (escudo > 0){ //Si el escudo sigue activo lo pintamos
            batch.draw(texturaEscudo, xPosition, yPosition, ancho, alto);
        }
    }
}
