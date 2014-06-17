package de.rub.SVRVKVE.simulation;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import de.rub.SVRVKVE.animals.Dog;
import de.rub.SVRVKVE.animals.Sheep;
import de.rub.SVRVKVE.input.InputHandler;

public class HerdSimulation extends ApplicationAdapter {
	
	// window size
	public static final int WINDOW_X = 1280;
	public static final int WINDOW_Y = 720;

	// graphics
	SpriteBatch batch;
	OrthographicCamera camera;
	
	// everything about the sheeps
	Array<Sheep> sheepHerd;
	Sound sheepSound;
	long lastUpdateTime = 0;
	int sheepX 			= 50;
	int sheepY 			= 50;
	int numberOfSheeps 	= 64;
	
	// everything about the dog
	Dog dog;
	GridPoint2 dogStartPosition = new GridPoint2(50, 50);
	int dogHeight = 100, dogWidth = 35;

	// utilities
	Random rand = new Random();
	InputHandler inputHandler = new InputHandler();
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(inputHandler);
		
		// set up camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WINDOW_X, WINDOW_Y);

		// initialize sheep herd
		sheepSound = Gdx.audio.newSound(Gdx.files.internal("sheepSound.mp3"));
		sheepHerd = new Array<Sheep>(numberOfSheeps);
		for (int i = 0; i < numberOfSheeps; i++) {
			sheepHerd.add(new Sheep(rand.nextInt(WINDOW_X), rand
					.nextInt(WINDOW_Y), sheepX, sheepY));
		}

		// initialize dog
		dog = new Dog(dogStartPosition,dogHeight, dogWidth);
		
		// utilities
		inputHandler.addInputListener(dog.getInputListener());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// move sheeps
		if (TimeUtils.millis() - lastUpdateTime > 25) {
			Iterator<Sheep> sheeperator = sheepHerd.iterator();
			while (sheeperator.hasNext()) {
				Sheep currentSheep = sheeperator.next();
				currentSheep.x += rand.nextInt(3) - 1;
				currentSheep.y += rand.nextInt(3) - 1;
				if (currentSheep.x >= WINDOW_X - sheepX)
					currentSheep.x = WINDOW_X - sheepX;
				if (currentSheep.x <= 0)
					currentSheep.x = 0;
				if (currentSheep.y >= WINDOW_Y - sheepY)
					currentSheep.y = WINDOW_Y - sheepY;
				if (currentSheep.y <= 0)
					currentSheep.y = 0;
			}
			if (rand.nextInt(80) == 1) {
				sheepSound.play();
			}
			lastUpdateTime = TimeUtils.millis();
		}
		
		// camera update
		batch.setProjectionMatrix(camera.combined);
		camera.update();

		batch.begin();

		// draw sheeps
		Iterator<Sheep> sheeperator = sheepHerd.iterator();
		while (sheeperator.hasNext()) {
			Sheep currentSheep = sheeperator.next();
			batch.draw(Sheep.image, currentSheep.x, currentSheep.y,
					sheepX, sheepY);
		}

		// draw dog
		dog.render(batch);

		batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		super.dispose();
	}
}
