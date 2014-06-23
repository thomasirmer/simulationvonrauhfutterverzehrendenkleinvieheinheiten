package de.rub.SVRVKVE.simulation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

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
	Pixmap pixmapCircle;
	Texture pixmapCircleTexture;
	
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
        pixmapCircle = new Pixmap(Sheep.SIGHT_DISTANCE * 2, Sheep.SIGHT_DISTANCE * 2, Format.RGBA4444);
        Pixmap.setBlending(Blending.None);
        pixmapCircle.setColor(1, 0, 0, 0.5f);
        pixmapCircle.drawCircle(Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE);
        pixmapCircleTexture = new Texture(pixmapCircle, Format.RGBA4444, false);

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
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		playSounds();
		
		// begin drawing object to batch
		batch.begin();
		
		// draw texture
		batch.draw(backgroundTexture, 0, 0, WINDOW_X, WINDOW_Y);

		// draw dog
		dog.render(batch);
		
		// draw sheeps
		for (int i=0; i<sheepHerd.size; i++) {
			sheepHerd.get(i).render(batch);
			batch.draw(pixmapCircleTexture, sheepHerd.get(i).getX() - Sheep.SIGHT_DISTANCE/2, sheepHerd.get(i).getY() - Sheep.SIGHT_DISTANCE/2);
		}
		
		// display sheep's current mood
		DecimalFormat df = new DecimalFormat("##.###");
		for (int i=0; i<sheepHerd.size; i++) {
			Sheep s = sheepHerd.get(i);
			String ext = df.format(s.evaluateExcitation());
			s.getFont().draw(batch, ext, s.getX(), s.getY());
			//System.out.println("Sheep # "+i+"\'s excitation: "+sheepHerd.get(i).evaluateExcitation());
		}

		batch.end();
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
