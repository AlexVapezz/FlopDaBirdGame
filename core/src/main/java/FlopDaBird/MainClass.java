package FlopDaBird;

import com.badlogic.gdx.Game;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainClass extends Game {

	GameScreen gameScreen;

	public static Random random = new Random();

	@Override
	public void create() {
		gameScreen = new GameScreen(); //Cuando la aplicacion se inicie, creará la pantalla principal
		setScreen(gameScreen); //Luego mostrará esa pantalla inicial
	}

	@Override
	public void dispose() {
		gameScreen.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
	}

}