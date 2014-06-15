package de.rub.SVRVKVE;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import de.rub.SVRVKVE.input.InputHandler;

public class HerdSimulation extends ApplicationAdapter {

	public static final int WINDOW_X = 1440;
	public static final int WINDOW_Y = 900;

	SpriteBatch batch;
	InputHandler inputHandler = new InputHandler();

	Array<Sheep> sheepHerd;
	Dog dog;
	long lastUpdateTime = 0;
	int sheepX = 50;
	int sheepY = 50;
	int numberOfSheeps = 256;
	GridPoint2 dogStartPosition = new GridPoint2(50, 50);
	int dogHeight = 100, dogWidth = 35;

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(inputHandler);

		// initialize sheep herd
		Random rand = new Random();
		sheepHerd = new Array<Sheep>(numberOfSheeps);
		for (int i = 0; i < numberOfSheeps; i++) {
			sheepHerd.add(new Sheep(rand.nextInt(WINDOW_X), rand
					.nextInt(WINDOW_Y), sheepX, sheepY));
		}

		dog = new Dog(dogStartPosition,dogHeight, dogWidth);
		inputHandler.addInputListener(dog.getInputListener());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.4f, 0.5f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// move sheeps
		if (TimeUtils.millis() - lastUpdateTime > 25) {
			Iterator<Sheep> sheeperator = sheepHerd.iterator();
			Random rand = new Random();
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
			lastUpdateTime = TimeUtils.millis();
		}

		batch.begin();

		// draw sheeps
		Iterator<Sheep> sheeperator = sheepHerd.iterator();
		while (sheeperator.hasNext()) {
			Sheep currentSheep = sheeperator.next();
			batch.draw(currentSheep.image, currentSheep.x, currentSheep.y,
					sheepX, sheepY);
		}

		dog.render(batch);

		batch.end();
	}
}
