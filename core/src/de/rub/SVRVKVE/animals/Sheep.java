package de.rub.SVRVKVE.animals;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import de.rub.SVRVKVE.simulation.HerdSimulation;
import de.rub.fuzzy.Catalog;

public class Sheep extends Sprite {

	public static final Texture image = new Texture(
			Gdx.files.internal("sheepImage.png"));
	private GridPoint2 destination;
	private Array<Sheep> herd;
	private BitmapFont font;
	
	public static final int SIGHT_DISTANCE = 50;
	public static final int MOVE_SPEED = 50;

	public Sheep(Array<Sheep> herd, int x, int y, int width, int height) {
//		super(x, y, width, height);
		super(image);
		this.setPosition(x, y);
		this.setSize(width, height);
		this.setOriginCenter();
		this.font = new BitmapFont();
		this.font.setColor(Color.RED);
		this.herd = herd;
	}

	// Is being called by renderer to update movement. Result is saved in
	// destination attribute
	public void move() {
		
		float distance = MOVE_SPEED * Gdx.graphics.getDeltaTime();
		
		setRotation(getDirection().angle());
		float directionX = (float) Math.cos(Math.toRadians(getRotation()));
		float directionY = (float) Math.sin(Math.toRadians(getRotation()));
		
		this.setX(this.getX() + directionX * distance);
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

	// Helper method to evaluate the next destination Point
	public Vector2 getDirection() {
		
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE * 4);
		
		Vector2 direction = new Vector2(0,0);
		for (Sheep neighbour : neighbours) {
			direction.add(directionToSheep(neighbour));
		}
		return direction.nor();
	}

	private Vector2 getHeading() {
		// Return the directional vector between current position and
		// destination
		return null;
	}

	private double measureDistance(Sheep target) {
		return Math.sqrt(Math.pow(Math.abs(target.getX() - this.getX()), 2)
				+ Math.pow(Math.abs(target.getY() - this.getY()), 2));
	}

	private Vector2 directionToSheep(Sheep target) {
		return new Vector2((target.getX() - this.getX()), (target.getY() - this.getY()));
	}
	
	private Array<Sheep> sheepInNearDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (measureDistance(neighbour) < 25.0)
				result.add(neighbour);
		}
		return result;
	}

	private Array<Sheep> sheepInMediumDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (measureDistance(neighbour) < 75.0
					&& measureDistance(neighbour) >= 25)
				result.add(neighbour);
		}
		return result;
	}

	private Array<Sheep> sheepInFarDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (measureDistance(neighbour) < 150.0
					&& measureDistance(neighbour) >= 75)
				result.add(neighbour);
		}
		return result;
	}
	
	private Array<Sheep> sheepsAround(int sight) {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if (measureDistance(neighbour) < sight
					&& measureDistance(neighbour) >= 0)
				result.add(neighbour);
		}
		return result;
	}
	
	public void render(SpriteBatch batch) {
		move();
		draw(batch);
	}

	public double evaluateExcitation() {
		
		Array<Sheep> neighbours = sheepsAround(SIGHT_DISTANCE);
		double excitation = 0.0;
		for (Sheep neighbour : neighbours) {
			excitation += measureDistance(neighbour) / neighbours.size;
		}
		
		Catalog.set("Excitation", excitation);
		Catalog.evalAllRules();
		return Catalog.get("Movement");
		
//		return excitation / neighbours.size;
		
//		Catalog.set("close", sheepInNearDistance().size);
//		Catalog.set("inreach", sheepInMediumDistance().size);
//		Catalog.set("distant", sheepInFarDistance().size);
//		Catalog.evalAllRules();
//		return Catalog.get("Excitation");
	}

	public BitmapFont getFont() {
		return this.font;
	}
}
