package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Laser {

    //Declaramos las caracteristicas del laser
    float movimientoSpeed; //Velocidad del laser

    //Declaramos las posiciones y dimensiones
    float xPosition, yPosition; //Posicion del laser
    float ancho, alto;

    //Declaramos el apartado grafico de texturas
    TextureRegion textureRegion;

    //Declaramos el constructor
    public Laser(float movimientoSpeed, float xCentro, float yCentro, float ancho, float alto, TextureRegion textureRegion) {
        this.movimientoSpeed = movimientoSpeed;
        this.xPosition = xCentro;
        this.yPosition = yCentro - ancho;
        this.ancho = ancho;
        this.alto = alto;
        this.textureRegion = textureRegion;
    }

    //Declaramos una clase para pintar los laseres
    public void pintar(Batch batch){
        batch.draw(textureRegion, xPosition, yPosition, ancho, alto);
    }
}
