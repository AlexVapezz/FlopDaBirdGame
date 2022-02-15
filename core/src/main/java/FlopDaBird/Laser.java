package FlopDaBird;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Laser {

    //Declaramos las caracteristicas del laser
    float movimientoSpeed; //Velocidad del laser

    //Declaramos las posiciones y dimensiones
    Rectangle boundingBox;

    //Declaramos el apartado grafico de texturas
    TextureRegion textureRegion;

    //Declaramos el constructor
    public Laser(float movimientoSpeed, float xCentro, float yCentro, float ancho, float alto, TextureRegion textureRegion) {
        this.movimientoSpeed = movimientoSpeed;
        this.boundingBox = new Rectangle(xCentro - ancho/2, yCentro - alto * 5, ancho, alto);
        this.textureRegion = textureRegion;
    }

    //Declaramos una clase para pintar los laseres
    public void pintar(Batch batch){
        batch.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }
}
