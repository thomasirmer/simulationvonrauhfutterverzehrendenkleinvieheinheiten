package de.rub.SVRVKVE.animals;

import java.text.DecimalFormat;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.rub.SVRVKVE.simulation.HerdSimulation;
import de.rub.fuzzy.Catalog;

public class Sheep extends Sprite {

	private static final Texture image 	  = new Texture(Gdx.files.internal("sheepWithEyes.png"));
	private static final Sound sheepSound = Gdx.audio.newSound(Gdx.files.internal("sheepSound.mp3")); 
	private Array<Sheep> herd;
	private BitmapFont font;
	private Random rand = new Random();
	
	Pixmap pixmapCircle;
	Texture pixmapCircleTexture;
	
	public static final int SIGHT_DISTANCE = 50;
	public static final int MOVE_SPEED = 1;

	public Sheep(Array<Sheep> herd, int x, int y, int width, int height) {
		super(image);
		this.setPosition(x, y);
		this.setSize(width, height);
		this.setOriginCenter();
		this.font = new BitmapFont();
		this.font.setColor(Color.RED);
		this.herd = herd;
		
        pixmapCircle = new Pixmap(Sheep.SIGHT_DISTANCE * 2, Sheep.SIGHT_DISTANCE * 2, Format.RGBA4444);
        Pixmap.setBlending(Blending.None);
        pixmapCircle.setColor(1, 0, 0, 0.5f);
        pixmapCircle.drawCircle(Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE, Sheep.SIGHT_DISTANCE);
        pixmapCircleTexture = new Texture(pixmapCircle, Format.RGBA4444, false);
	}
	
	/**
	 * Calculates sheeps behavior and draws it to the batch.
	 * @param batch SpriteBatch where the sheep should be drawn
	 */
	public void render(SpriteBatch batch) {
		move();
		draw(batch);
		drawProperties(batch);
		//playSound();
	}

	private void move() {
		
		float distance = (float) (MOVE_SPEED * getMovementSpeed() * Gdx.graphics.getDeltaTime());
		
		setRotation(getDirection().angle());
		float directionX = (float) Math.sin(Math.toRadians(getRotation()));
		float directionY = (float) Math.cos(Math.toRadians(getRotation()));
		
		this.setX(this.getX() - directionX * distance);
		this.setY(this.getY() + directionY * distance);
		
		if (this.getX() >= HerdSimulation.WINDOW_X - this.getWidth())
			this.setX(HerdSimulation.WINDOW_X - this.getWidth());
		if (this.getX() <= 0)
			this.setX(0);
		if (this.getY() >= HerdSimulation.WINDOW_Y - this.getHeight())
			this.setY(HerdSimulation.WINDOW_Y - this.getHeight());
		if (this.getY() <= 0)
			this.setY(0);
	}
	
	private void drawProperties(SpriteBatch batch) {
		
		batch.draw(pixmapCircleTexture, getX() - Sheep.SIGHT_DISTANCE/2, getY() - Sheep.SIGHT_DISTANCE/2);
		
		DecimalFormat df = new DecimalFormat("##.###");
		String speed = df.format(getMovementSpeed());
		String angle = df.format(getRotation());
		font.draw(batch, "spd " + speed, getX(), getY());
		font.draw(batch, "rot " + angle, getX(), getY() - 15);
	}
	
	private Array<Sheep> sheepsAround(int sight) {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (distanceTo(neighbour) < sight
					&& distanceTo(neighbour) >= 0)
				result.add(neighbour);
		}
		return result;
	}
	
	private Vector2 getDirection() {
		
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE * 4);
		
		Vector2 direction = new Vector2(0,0);
		for (Sheep neighbour : neighbours) {
			direction.add(directionTo(neighbour));
		}
		return direction.nor();
	}
	
	private double distanceTo(Sheep target) {
		return Math.sqrt(Math.pow(Math.abs(target.getX() - this.getX()), 2)
				+ Math.pow(Math.abs(target.getY() - this.getY()), 2));
	}

	private Vector2 directionTo(Sheep target) {
		return new Vector2((target.getX() - this.getX()), (target.getY() - this.getY()));
	}

	private double getMovementSpeed() {
		
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE);
		double excitation = 0.0;
		for (Sheep neighbour : neighbours) {
			excitation += distanceTo(neighbour) / neighbours.size;
		}
		
		Catalog.set("Excitation", excitation);
		Catalog.evalAllRules();
		double movement = Catalog.get("Movement");
		return movement;
	}
	
	private void playSound() {
		if (rand.nextInt(16000) == 1) {
			sheepSound.play();
		}
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
