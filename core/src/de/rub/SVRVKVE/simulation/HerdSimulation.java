package de.rub.SVRVKVE.simulation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import de.rub.SVRVKVE.animals.Dog;
import de.rub.SVRVKVE.animals.Sheep;
import de.rub.SVRVKVE.input.InputHandler;
import de.rub.fuzzy.Catalog;
import de.rub.fuzzy.FuzzyInputException;
import de.rub.fuzzy.FuzzyNoThenPartException;

public class HerdSimulation extends ApplicationAdapter {
	
	// window size
	public static final int WINDOW_X = 1280;
	public static final int WINDOW_Y = 720;

	// graphics
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture backgroundTexture;
	BitmapFont font;
	
	// everything about the sheeps
	Array<Sheep> sheepHerd;
	Sound sheepSound;
	int sheepWidth 		= 50;
	int sheepHeigth 	= 50;
	int numberOfSheeps 	= 64;
	
	// everything about the dog
	Dog dog;
	GridPoint2 dogStartPosition = new GridPoint2(50, 50);
	int dogHeight = 100, dogWidth = 35;

	// utilities
	InputHandler inputHandler = new InputHandler();
	Random rand = new Random();
	long lastUpdateTime = 0;
	
	@Override
	public void create() {
		// set up graphics
		batch = new SpriteBatch();
		font = new BitmapFont();
        font.setColor(Color.RED);

		backgroundTexture = new Texture(Gdx.files.internal("grassTexture.jpg"));
		
		// set up input handler
		Gdx.input.setInputProcessor(inputHandler);
		
		// set up camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WINDOW_X, WINDOW_Y);

		// initialize sheep herd
		sheepSound = Gdx.audio.newSound(Gdx.files.internal("sheepSound.mp3"));
		sheepHerd = new Array<Sheep>(numberOfSheeps);
		for (int i = 0; i < numberOfSheeps; i++) {
			sheepHerd.add(new Sheep(sheepHerd, rand.nextInt(WINDOW_X),
						  rand.nextInt(WINDOW_Y),
						  sheepWidth,
						  sheepHeigth));
		}

		// initialize dog
		dog = new Dog(dogStartPosition,dogHeight, dogWidth);
		
		// utilities
		inputHandler.addInputListener(dog.getInputListener());
		
		// create Catalog
		initFuzzy();
	}

	@Override
	public void render() {
		// clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// camera update
		batch.setProjectionMatrix(camera.combined);
		camera.update();

		playSounds();
		
		moveSheeps();
		
		// begin drawing object to batch
		batch.begin();
		
		// draw texture
		batch.draw(backgroundTexture, 0, 0, WINDOW_X, WINDOW_Y);

		drawSheeps();
		
		// display sheep's current mood
		for (int i=0; i<sheepHerd.size; i++) {
			Sheep s = sheepHerd.get(i);
//			s.getFont().draw(batch, String.valueOf(s.evaluateSatisfaction()), s.x, s.y);
			System.out.println("Sheep # "+i+"\'s excitation: "+sheepHerd.get(i).evaluateExcitation());
		}

		// draw dog
		dog.render(batch);

		batch.end();
	}

	private void drawSheeps() {

		Iterator<Sheep> sheeperator = sheepHerd.iterator();

		while (sheeperator.hasNext()) {
			Sheep currentSheep = sheeperator.next();
			batch.draw(Sheep.image, currentSheep.x, currentSheep.y, sheepWidth,
					sheepHeigth);
		}
	}

	private void moveSheeps() {
		
		if (TimeUtils.millis() - lastUpdateTime > 25) {
			
			Iterator<Sheep> sheeperator = sheepHerd.iterator();
			
			while (sheeperator.hasNext()) {
				Sheep currentSheep = sheeperator.next();				
				currentSheep.x += rand.nextInt(3) - 1;;
				currentSheep.y += rand.nextInt(3) - 1;;
				
				if (currentSheep.x >= WINDOW_X - sheepWidth)
					currentSheep.x = WINDOW_X - sheepWidth;
				if (currentSheep.x <= 0)
					currentSheep.x = 0;
				if (currentSheep.y >= WINDOW_Y - sheepHeigth)
					currentSheep.y = WINDOW_Y - sheepHeigth;
				if (currentSheep.y <= 0)
					currentSheep.y = 0;
			}
			lastUpdateTime = TimeUtils.millis();
		}
	}
	
	private void playSounds() {
		if (rand.nextInt(256) == 1) {
			sheepSound.play();
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		super.dispose();
	}
	
	private void initFuzzy() {
		String workingDir = System.getProperty("user.dir");
		   System.out.println("Current working directory : " + workingDir);
		try {
            Catalog.readFile("../core/assets/sheepRules.fzy");
        } catch (FuzzyNoThenPartException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FuzzyInputException e) {
            e.printStackTrace();
        }
	}
}
