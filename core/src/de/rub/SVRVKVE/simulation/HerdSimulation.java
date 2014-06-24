package de.rub.SVRVKVE.simulation;

import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
	ShapeRenderer shapeRen;
	
	// everything about the sheeps
	Array<Sheep> sheepHerd;
	int sheepWidth 		= 46;
	int sheepHeigth 	= 63;
	int numberOfSheeps 	= 64;
	
	// everything about the dog
	Dog dog;
	GridPoint2 dogStartPosition = new GridPoint2(50, 50);
	int dogHeight = 100, dogWidth = 35;

	// utilities
	InputHandler inputHandler = new InputHandler();
	Random rand = new Random();
	
	@Override
	public void create() {
		// set up graphics
		batch = new SpriteBatch();
		backgroundTexture = new Texture(Gdx.files.internal("grassTexture.jpg"));
		shapeRen = new ShapeRenderer();
		
		// set up input handler
		Gdx.input.setInputProcessor(inputHandler);
		
		// set up camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WINDOW_X, WINDOW_Y);

		// initialize dog
		dog = new Dog(dogStartPosition,dogHeight, dogWidth);
		
		// initialize sheep herd
		sheepHerd = new Array<Sheep>(numberOfSheeps);
		for (int i = 0; i < numberOfSheeps; i++) {
			sheepHerd.add(new Sheep(sheepHerd, dog,
						  rand.nextInt(WINDOW_X),
						  rand.nextInt(WINDOW_Y),
						  sheepWidth,
						  sheepHeigth));
		}
		
		// utilities
		inputHandler.addInputListener(dog.getInputListener());
		
		// create catalog
		initFuzzy();
	}

	@Override
	public void render() {
		// clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// camera update
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		// begin drawing object to batch
		batch.begin();
		shapeRen.begin(ShapeType.Line);
		
		// draw texture
		batch.draw(backgroundTexture, 0, 0, WINDOW_X, WINDOW_Y);

		// draw dog
		dog.render(batch);
		
		// draw sheeps
		for (int i=0; i<sheepHerd.size; i++) {
			sheepHerd.get(i).render(batch, shapeRen);
		}

		shapeRen.end();
		batch.end();
	}
	
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
