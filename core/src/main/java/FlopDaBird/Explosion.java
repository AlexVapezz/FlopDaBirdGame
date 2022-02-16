package FlopDaBird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Explosion {

    private Animation<TextureRegion> animacionExplosion;
    private float timerExplosion;

    private Rectangle boundingBox;

    Explosion(Texture textura, Rectangle boundingBox, float totalTiempoAnimacion){
        this.boundingBox = boundingBox;

        //Separamos y convertimos la textura de la explosion (que son varias) en un array
        TextureRegion[][] textureRegion2D = TextureRegion.split(textura, 64, 64);
        TextureRegion[] textureRegion1D = new TextureRegion[16];
        int index = 0;
        //Recorremos el array bidimensional y lo almacenamos en el array de 1 dimension
        for (int i = 0; i<4; i++){
            for (int j = 0; j<4; j++){
                textureRegion1D[index] = textureRegion2D[i][j];
                index++;
            }
        }

        animacionExplosion = new Animation<TextureRegion>(totalTiempoAnimacion/16, textureRegion1D);
        timerExplosion = 0;
    }

    public void update(float deltaTime){
        timerExplosion = timerExplosion + deltaTime;
    }

    public void pintar(SpriteBatch batch){
        batch.draw(animacionExplosion.getKeyFrame(timerExplosion), boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    public boolean consultarFinal(){
        return animacionExplosion.isAnimationFinished(timerExplosion);
    }
}
