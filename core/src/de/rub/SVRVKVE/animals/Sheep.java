package de.rub.SVRVKVE.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Sheep extends Rectangle {
	
	public static final Texture image = new Texture(Gdx.files.internal("sheep3.png"));
	private GridPoint2 destination;
	private GridPoint2 position;
	
	public Sheep() {
		// TODO Auto-generated constructor stub
	}

	public Sheep(Rectangle rect) {
		super(rect);
		// TODO Auto-generated constructor stub
	}

	public Sheep(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}
	
	// Is being called by renderer to initiate movement. Result is saved in destination attribute
	public void move() {
		
	}
	
	// Helper method to evaluate the next destination Point
	private GridPoint2 evaluateMovement() {
		return null;
	}
	
	public Vector2 getHeading() {
		// Return the directional vector between current position and destination
		return null;
	}

}
