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

public class Sheep extends GameObject {

	private static final Texture image = new Texture(
			Gdx.files.internal("sheepWithEyes.png"));
	private static final Sound sheepSound = Gdx.audio.newSound(Gdx.files
			.internal("sheepSound.mp3"));
	private Array<Sheep> herd;
	private Dog dog;
	private BitmapFont font;
	private Random rand = new Random();

	Pixmap pixmapCircle;
	Texture pixmapCircleTexture;

	public static final int SIGHT_DISTANCE = 50;
	public static final float MAX_MOVE_SPEED = 0.1f;
	public static final float MAX_ROTATION_SPEED = 0.1f;

	private Vector2 centerPosition;
	private Vector2 currentVelocity;
	private Vector2 desiredVelocity;
	private Vector2 steering;

	// flee from dog
	private Vector2 fleeDirection;
	private float angleToDog;
	private float fleeRotation;
	private float fleeSpeed;

	// seek herd
	private Vector2 seekDirection;
	private float angleToHerd;
	private float seekRotation;
	private float seekSpeed;

	public Sheep(Array<Sheep> herd, Dog dog, int x, int y, int width, int height) {
		super(image);

		this.setPosition(x, y);
		this.setSize(width, height);
		this.setOriginCenter();
		this.font = new BitmapFont();
		this.font.setColor(1, 1, 0.5f, 0.5f);
		this.herd = herd;
		this.dog = dog;

		pixmapCircle = new Pixmap(Sheep.SIGHT_DISTANCE * 2,
				Sheep.SIGHT_DISTANCE * 2, Format.RGBA4444);
		Pixmap.setBlending(Blending.None);
		pixmapCircle.setColor(1, 0, 0, 0.5f);
		pixmapCircle.drawCircle(Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE,
				Sheep.SIGHT_DISTANCE);
		pixmapCircleTexture = new Texture(pixmapCircle, Format.RGBA4444, false);

		// initialize movement parameters
		centerPosition = getCenterPosition();
		currentVelocity = new Vector2(0, 0);

		fleeDirection = new Vector2(1, 1);
		seekDirection = new Vector2(-1, -1);
	}

	/**
	 * Calculates sheeps behavior and draws it to the batch.
	 * 
	 * @param batch
	 *            SpriteBatch where the sheep should be drawn
	 */
	public void render(SpriteBatch batch, ShapeRenderer shapeRen) {

		Array<GameObject> dogArray = new Array<GameObject>();
		dogArray.add(dog);
		Vector2 directionToDog = getDirectionToGObjects(dogArray);

		// calculate dogs distance and angle
		angleToDog = getAngleToTarget(currentVelocity, directionToDog);
		double distanceToDog = getDistanceBetween(getCenterPosition(),
				dog.getCenterPosition());

		// calculate herds distance and angle
		// TODO herd behavior
		float angleToHerd = getDirectionToGroup(this,
				sheepsAround(SIGHT_DISTANCE)).angle();
		float distanceFromHerd = getDistanceToGroup(this,
				sheepsAround(SIGHT_DISTANCE));
		Catalog.set("DirectionToHerd", angleToHerd);
		Catalog.set("DistanceToHerd", distanceFromHerd);

		// set fuzzy inputs
		Catalog.set("DirectionToDog", (double) angleToDog);
		Catalog.set("DistanceToDog", (double) distanceToDog);

		// calculate fuzzy
		Catalog.evalAllRules();

		// get fuzzy outputs
		fleeRotation = (float) Catalog.get("FleeRotationRate");
		fleeSpeed = (float) Catalog.get("FleeSpeedRate");
		seekRotation = (float) Catalog.get("SeekRotationRate");
		seekSpeed = (float) Catalog.get("SeekSpeedRate");

		// caculate flee vector
		fleeDirection.rotate(-fleeRotation * 2);
		fleeDirection.nor().scl(1 + (float) fleeSpeed * 10);

		seekDirection.rotate(-seekRotation * 2);
		seekDirection.nor().scl(1 + (float) seekSpeed * 10);

		// add flee vector to current velocity
		currentVelocity.nor().add(fleeDirection.cpy().sub(fleeDirection.nor()))
				.scl(MAX_MOVE_SPEED);

		currentVelocity.nor().add(seekDirection);

		setRotation(currentVelocity.angle() - 90);
		centerPosition = getCenterPosition();
		centerPosition.add(currentVelocity);
		setPosition(centerPosition.x - getWidth() / 2, centerPosition.y
				- getHeight() / 2);

		// avoid sheep to get out of screen
		if (getCenterPosition().x >= HerdSimulation.WINDOW_X)
			setX(HerdSimulation.WINDOW_X - getWidth() / 2);
		if (getCenterPosition().x <= 0)
			setX(0 - getWidth() / 2);
		if (getCenterPosition().y >= HerdSimulation.WINDOW_Y)
			setY(HerdSimulation.WINDOW_Y - getHeight() / 2);
		if (getCenterPosition().y <= 0)
			setY(0 - getHeight() / 2);

		// addSteering(rotation);
		// rotate(rotation);
		// }

		draw(batch);
		// drawProperties(batch, shapeRen);
		// playSound();
	}

	private float getDistanceToGroup(GameObject subject, Array<GameObject> group) {
		float distance = 0;
		for (GameObject gameObject : group) {
			distance += gameObject.getCenterPosition()
					.sub(subject.getCenterPosition()).len();
		}
		distance /= group.size;
		return distance;
	}

	private Vector2 getDirectionToGroup(GameObject subject,
			Array<GameObject> group) {
		Vector2 result = new Vector2();
		for (GameObject gameObject : group) {
			result.add(gameObject.getCenterPosition().sub(
					subject.getCenterPosition()));
		}
		return result.nor();
	}

	private Vector2 getSteeringTowards(Vector2 target) {
		centerPosition.set(getCenterPosition());

		// seek pattern (move towards the target)
		desiredVelocity = target.sub(centerPosition);

		// flee pattern (move away from target)
		// desiredVelocity = getCenterPosition().sub(target);

		// arrival pattern (slowing down when entering the slow-down-radius)
		float slowingRadius = 150;
		float distance = desiredVelocity.len();
		if (distance < slowingRadius) {
			desiredVelocity.nor().scl(MAX_MOVE_SPEED)
					.scl(distance / slowingRadius);
		} else {
			desiredVelocity.nor().scl(MAX_MOVE_SPEED);
		}

		steering = desiredVelocity.sub(currentVelocity).nor()
				.scl(MAX_ROTATION_SPEED);

		return steering;
	}

	private Vector2 getFleeFrom(Vector2 hunter) {
		centerPosition.set(getCenterPosition());

		// flee pattern (move away from target)
		desiredVelocity = getCenterPosition().sub(hunter);

		steering = desiredVelocity.sub(currentVelocity).nor()
				.scl(MAX_ROTATION_SPEED);

		return steering;
	}

	private void addSteering(Vector2 steering) {
		currentVelocity.add(steering);
		centerPosition.add(currentVelocity);

		setPosition(centerPosition.x - getWidth() / 2, centerPosition.y
				- getHeight() / 2);
		setRotation(currentVelocity.angle() - 90);
	}

	/**
	 * Draws information about the sheeps mood and its supervised area
	 * 
	 * @param batch
	 */
	private void drawProperties(SpriteBatch batch, ShapeRenderer shapeRen) {

		font.draw(batch, String.valueOf(currentVelocity.len()), getX()
				- getWidth() / 2, getY() - getHeight() / 2);

		shapeRen.setColor(0, 0, 1, 0.5f);
		shapeRen.line(centerPosition, dog.getCenterPosition());

		shapeRen.setColor(1, 0, 0, 0.5f);
		shapeRen.line(getCenterPosition(),
				getCenterPosition().add(new Vector2(currentVelocity).scl(250)));

		// batch.draw(pixmapCircleTexture, getX() - Sheep.SIGHT_DISTANCE/2,
		// getY() - Sheep.SIGHT_DISTANCE/2);
		//
		// DecimalFormat df = new DecimalFormat("##.###");
		// String speed = df.format(getMovementSpeed());
		// String angle = df.format(getRotation());
		// font.draw(batch, "spd " + speed, getX(), getY());
		// font.draw(batch, "rot " + angle, getX(), getY() - 15);
		// font.draw(batch, "nbs" + sheepsAround(SIGHT_DISTANCE).size, getX(),
		// getY() - 30);
		//
		// shapeRen.setColor(0, 0, 1, 0.5f);
		// Vector2 direction = getDirection().scl(100.0f);
		// shapeRen.line(getX() + getWidth()/2, getY() + getHeight()/2,
		// getX() + direction.x + getWidth()/2, getY() + direction.y +
		// getHeight()/2);
	}

	/**
	 * Selects all sheeps in "sight" distance
	 * 
	 * @param sight
	 *            the radius of the supervised area
	 * @return the sheeps within sight
	 */
	private Array<GameObject> sheepsAround(int sight) {
		Array<GameObject> result = new Array<GameObject>();
		for (Sheep neighbour : this.herd) {
			if (getDistanceBetween(this.centerPosition,
					neighbour.getCenterPosition()) < sight
					&& getDistanceBetween(this.centerPosition,
							neighbour.getCenterPosition()) > 0)
				result.add(neighbour);
		}
		return result;
	}

	/**
	 * Calculates the direction of the sheeps movement based on the surrounding
	 * sheeps and the dog.
	 * 
	 * @return the direction
	 */
	private Vector2 getDirection() {
		// Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE * 4);
		//
		// Vector2 direction = new Vector2(0,0);
		//
		// // get direction based on other sheeps
		// for (Sheep neighbour : neighbours) {
		// direction.add(directionTo(neighbour).nor().scl((float) (1 /
		// distanceTo(neighbour))));
		// }
		// direction.nor();
		//
		// // get direction based on doggy doggy dog
		// direction.sub(directionTo(dog).nor());
		//
		// return direction.nor();
		return null;
	}

	/**
	 * Calculates the distance to the target.
	 * 
	 * @param target
	 * @return distance
	 */
	private double getDistanceBetween(Vector2 position1, Vector2 position2) {
		Vector2 posToPos = position1.cpy().sub(position2);
		return posToPos.len();
	}

	/**
	 * Calculates the direction to the target.
	 * 
	 * @param target
	 * @return direction
	 */
	private Vector2 directionTo(Sprite target) {
		return new Vector2(
				(target.getX() + target.getWidth() / 2 - this.getX() + getWidth() / 2),
				(target.getY() + target.getHeight() / 2 - this.getY() + getHeight() / 2));
	}

	/**
	 * Calculates the movement speed based on the FUZZY LOGIC
	 * 
	 * @return movement speed
	 */
	private double getMovementSpeed() {

		// Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE);
		// double excitation = 0.0;
		//
		// for (Sheep neighbour : neighbours) {
		// excitation += distanceTo(neighbour) / neighbours.size;
		// }
		//
		// if (neighbours.size == 0) {
		// excitation = 35;
		// }
		//
		// Catalog.set("Excitation", excitation);
		// Catalog.evalAllRules();
		// double movement = Catalog.get("Movement");
		// return movement;
		return 0;
	}

	/**
	 * Määäääh!
	 */
	private void playSound() {
		if (rand.nextInt(16000) == 1) {
			sheepSound.play();
		}
	}

	// private Array<Sheep> sheepInNearDistance() {
	// Array<Sheep> result = new Array<Sheep>();
	// for (Sheep neighbour : this.herd) {
	// if (distanceTo(neighbour) < 25.0)
	// result.add(neighbour);
	// }
	// return result;
	// }
	//
	// private Array<Sheep> sheepInMediumDistance() {
	// Array<Sheep> result = new Array<Sheep>();
	// for (Sheep neighbour : this.herd) {
	// if (distanceTo(neighbour) < 75.0
	// && distanceTo(neighbour) >= 25)
	// result.add(neighbour);
	// }
	// return result;
	// }
	//
	// private Array<Sheep> sheepInFarDistance() {
	// Array<Sheep> result = new Array<Sheep>();
	// for (Sheep neighbour : this.herd) {
	// if (distanceTo(neighbour) < 150.0
	// && distanceTo(neighbour) >= 75)
	// result.add(neighbour);
	// }
	// return result;
	// }
}
