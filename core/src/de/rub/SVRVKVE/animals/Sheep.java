package de.rub.SVRVKVE.animals;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.rub.SVRVKVE.simulation.HerdSimulation;
import de.rub.fuzzy.Catalog;

public class Sheep extends Sprite {

	private static final Texture image 	  = new Texture(Gdx.files.internal("sheepWithEyes.png"));
	private static final Sound sheepSound = Gdx.audio.newSound(Gdx.files.internal("sheepSound.mp3")); 
	private Array<Sheep> herd;
	private Dog dog;
	private BitmapFont font;
	private Random rand = new Random();
	
	Pixmap pixmapCircle;
	Texture pixmapCircleTexture;
	
	public static final int SIGHT_DISTANCE 		 	= 50;
	public static final float MAX_MOVE_SPEED  		= 1f;
	public static final float MAX_ROTATION_SPEED 	= 0.01f;
	
	private Vector2 centerPosition;
	private Vector2 currentVelocity;
	private Vector2 desiredVelocity;
	private Vector2 steering;

	public Sheep(Array<Sheep> herd, Dog dog, int x, int y, int width, int height) {
		super(image);
		
		this.setPosition(x, y);
		this.setSize(width, height);
		this.setOriginCenter();
		this.font = new BitmapFont();
		this.font.setColor(1, 1, 0.5f, 0.5f);
		this.herd = herd;
		this.dog = dog;
		
        pixmapCircle = new Pixmap(Sheep.SIGHT_DISTANCE * 2, Sheep.SIGHT_DISTANCE * 2, Format.RGBA4444);
        Pixmap.setBlending(Blending.None);
        pixmapCircle.setColor(1, 0, 0, 0.5f);
        pixmapCircle.drawCircle(Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE);
        pixmapCircleTexture = new Texture(pixmapCircle, Format.RGBA4444, false);
        
        // initialize movement parameters
        centerPosition	= getCenterPosition();
        currentVelocity = new Vector2(0, 0);
        desiredVelocity = new Vector2(0, 0);
        steering		= new Vector2(0, 0);
   	}
	
	/**
	 * Calculates sheeps behavior and draws it to the batch.
	 * @param batch SpriteBatch where the sheep should be drawn
	 */
	public void render(SpriteBatch batch, ShapeRenderer shapeRen) {
		move();
		draw(batch);
		//drawProperties(batch, shapeRen);
		//playSound();
	}

	/**
	 * Calculates sheeps movement and sets its new location.
	 */
	private void move() {
		
		centerPosition.set(getCenterPosition());
		
		// seek pattern (move towards the target)
		desiredVelocity = dog.getCenterPosition().sub(centerPosition);
				
		// flee pattern (move away from target)
		//desiredVelocity = getCenterPosition().sub(dog.getCenterPosition());
		
		// arrival pattern (slowing down when entering the slow-down-radius)
		float slowingRadius = 150;
		float distance 		= desiredVelocity.len();
		if (distance < slowingRadius) {
			desiredVelocity.nor().scl(MAX_MOVE_SPEED).scl(distance/slowingRadius);
		} else {
			desiredVelocity.nor().scl(MAX_MOVE_SPEED);
		}
		
		steering = desiredVelocity.sub(currentVelocity).nor().scl(MAX_ROTATION_SPEED);
		
		currentVelocity.add(steering);
		centerPosition.add(currentVelocity);
		
		setPosition(centerPosition.x - getWidth()/2, centerPosition.y - getHeight()/2);
		setRotation(currentVelocity.angle() - 90);
		
		// avoid sheep to get out of screen
		if (getCenterPosition().x >= HerdSimulation.WINDOW_X) setX(HerdSimulation.WINDOW_X - getWidth()/2);
		if (getCenterPosition().x <= 0) setX(0 - getWidth()/2);
		if (getCenterPosition().y >= HerdSimulation.WINDOW_Y) setY(HerdSimulation.WINDOW_Y - getHeight()/2);
		if (getCenterPosition().y <= 0) setY(0 - getHeight()/2);
		
//		float distance = (float) (STEP_SPEED * getMovementSpeed() * Gdx.graphics.getDeltaTime());
//		distance *= rand.nextFloat(); // change the speed randomly to simulate natural movement
//		
//		setRotation(getDirection().angle() - 90);
//		float directionX = (float) Math.sin(Math.toRadians(getRotation()));
//		float directionY = (float) Math.cos(Math.toRadians(getRotation()));
//		
//		this.setX(this.getX() - directionX * distance);
//		this.setY(this.getY() + directionY * distance);
//		
//		if (this.getX() >= HerdSimulation.WINDOW_X - this.getWidth())
//			this.setX(HerdSimulation.WINDOW_X - this.getWidth());
//		if (this.getX() <= 0)
//			this.setX(0);
//		if (this.getY() >= HerdSimulation.WINDOW_Y - this.getHeight())
//			this.setY(HerdSimulation.WINDOW_Y - this.getHeight());
//		if (this.getY() <= 0)
//			this.setY(0);
	}
	
	/**
	 * Draws information about the sheeps mood and its supervised area
	 * @param batch
	 */
	private void drawProperties(SpriteBatch batch, ShapeRenderer shapeRen) {
		
		font.draw(batch, String.valueOf(currentVelocity.len()), getX() - getWidth()/2, getY() - getHeight()/2);
		
		shapeRen.setColor(0, 0, 1, 0.5f);
		shapeRen.line(centerPosition, dog.getCenterPosition());
		
		shapeRen.setColor(1, 0, 0, 0.5f);
		shapeRen.line(getCenterPosition(), getCenterPosition().add(new Vector2(currentVelocity).scl(250)));
		
//		batch.draw(pixmapCircleTexture, getX() - Sheep.SIGHT_DISTANCE/2, getY() - Sheep.SIGHT_DISTANCE/2);
//		
//		DecimalFormat df = new DecimalFormat("##.###");
//		String speed = df.format(getMovementSpeed());
//		String angle = df.format(getRotation());
//		font.draw(batch, "spd " + speed, getX(), getY());
//		font.draw(batch, "rot " + angle, getX(), getY() - 15);
//		font.draw(batch, "nbs" + sheepsAround(SIGHT_DISTANCE).size, getX(), getY() - 30);
//		
//		shapeRen.setColor(0, 0, 1, 0.5f);
//		Vector2 direction = getDirection().scl(100.0f);
//		shapeRen.line(getX() + getWidth()/2, getY() + getHeight()/2,
//				getX() + direction.x + getWidth()/2, getY() + direction.y + getHeight()/2);
	}
	
	/**
	 * Selects all sheeps in "sight" distance
	 * @param sight the radius of the supervised area
	 * @return the sheeps within sight
	 */
	private Array<Sheep> sheepsAround(int sight) {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (distanceTo(neighbour) < sight && distanceTo(neighbour) > 0)
				result.add(neighbour);
		}
		return result;
	}
	
	/**
	 * Calculates the direction of the sheeps movement based on the surrounding sheeps and the dog.
	 * @return the direction
	 */
	private Vector2 getDirection() {
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE * 4);
		
		Vector2 direction = new Vector2(0,0);
		
		// get direction based on other sheeps
		for (Sheep neighbour : neighbours) {
			direction.add(directionTo(neighbour).nor().scl((float) (1 / distanceTo(neighbour))));
		}
		direction.nor();
		
		// get direction based on doggy doggy dog
		direction.sub(directionTo(dog).nor());
		
		return direction.nor();
	}
	
	/**
	 * Calculates the distance to the target.
	 * @param target
	 * @return distance
	 */
	private double distanceTo(Sprite target) {
		return Math.sqrt(Math.pow(Math.abs(target.getX() + target.getWidth()/2 - this.getX() + getWidth()/2), 2)
				+ Math.pow(Math.abs(target.getY() + target.getHeight()/2 - this.getY() + getHeight()/2), 2));
	}

	/**
	 * Calculates the direction to the target.
	 * @param target
	 * @return direction
	 */
	private Vector2 directionTo(Sprite target) {
		return new Vector2((target.getX() + target.getWidth()/2 - this.getX() + getWidth()/2),
				(target.getY() + target.getHeight()/2 - this.getY() + getHeight()/2));
	}

	/**
	 * Calculates the movement speed based on the FUZZY LOGIC
	 * @return movement speed
	 */
	private double getMovementSpeed() {
		
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE);
		double excitation = 0.0;
		
		for (Sheep neighbour : neighbours) {
			excitation += distanceTo(neighbour) / neighbours.size;
		}
		
		if (neighbours.size == 0) {
			excitation = 35;
		}
		
		Catalog.set("Excitation", excitation);
		Catalog.evalAllRules();
		double movement = Catalog.get("Movement");
		return movement;
	}
	
	/**
	 * Määäääh!
	 */
	private void playSound() {
		if (rand.nextInt(16000) == 1) {
			sheepSound.play();
		}
	}
	
	public Vector2 getCenterPosition() {
		return new Vector2(getX() + getWidth()/2, getY() + getHeight()/2);
	}
	
//	private Array<Sheep> sheepInNearDistance() {
//	Array<Sheep> result = new Array<Sheep>();
//	for (Sheep neighbour : this.herd) {
//		if (distanceTo(neighbour) < 25.0)
//			result.add(neighbour);
//	}
//	return result;
//}
//
//private Array<Sheep> sheepInMediumDistance() {
//	Array<Sheep> result = new Array<Sheep>();
//	for (Sheep neighbour : this.herd) {
//		if (distanceTo(neighbour) < 75.0
//				&& distanceTo(neighbour) >= 25)
//			result.add(neighbour);
//	}
//	return result;
//}
//
//private Array<Sheep> sheepInFarDistance() {
//	Array<Sheep> result = new Array<Sheep>();
//	for (Sheep neighbour : this.herd) {
//		if (distanceTo(neighbour) < 150.0
//				&& distanceTo(neighbour) >= 75)
//			result.add(neighbour);
//	}
//	return result;
//}
}
