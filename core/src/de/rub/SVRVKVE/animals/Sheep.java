package de.rub.SVRVKVE.animals;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.rub.fuzzy.Catalog;
import de.rub.fuzzy.FuzzyInputException;
import de.rub.fuzzy.FuzzyNoThenPartException;

public class Sheep extends Rectangle {
<<<<<<< Updated upstream:core/src/de/rub/SVRVKVE/Sheep.java
	
<<<<<<< HEAD:core/src/de/rub/SVRVKVE/Sheep.java
	public static final Texture image = new Texture(Gdx.files.internal("sheep3.png"));
=======

	public static final Texture image = new Texture(
			Gdx.files.internal("sheepImage.png"));
>>>>>>> Stashed changes:core/src/de/rub/SVRVKVE/animals/Sheep.java
=======
	public static final Texture image = new Texture(Gdx.files.internal("sheepImage.png"));
>>>>>>> origin/master:core/src/de/rub/SVRVKVE/animals/Sheep.java
	private GridPoint2 destination;
	private Array<Sheep> herd;
	private BitmapFont font;
	
	public Sheep(Array<Sheep> herd, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.font = new BitmapFont();
		this.font.setColor(Color.RED);
		this.herd = herd;
	}

	// Is being called by renderer to update movement. Result is saved in
	// destination attribute
	public void move() {

	}

	// Helper method to evaluate the next destination Point
	private GridPoint2 evaluateMovement() {
		return null;
	}

	public Vector2 getHeading() {
		// Return the directional vector between current position and
		// destination
		return null;
	}

	private double measureDistance(Sheep target) {
		return Math.sqrt(Math.pow(
				Math.abs(target.x - this.x), 2)
				+ Math.pow(Math.abs(target.x - this.x), 2));
	}
	
	private Vector2 directionToSheep(Sheep target) {
		return new Vector2((target.x-this.x), (target.y-this.y));
	}
	
	private Array<Sheep> sheepInNearDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if(measureDistance(neighbour)<25.0)
				result.add(neighbour);
		}
		return result;
	}
	
	private Array<Sheep> sheepInMediumDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if(measureDistance(neighbour)<75.0 && measureDistance(neighbour)>=25)
				result.add(neighbour);
		}
		return result;
	}
	
	private Array<Sheep> sheepInFarDistance() {
		Array<Sheep> result = new Array<Sheep>();
		for (Sheep neighbour : this.herd) {
			if(measureDistance(neighbour)<150.0 && measureDistance(neighbour)>=75)
				result.add(neighbour);
		}
		return result;
	}

	public double evaluateExcitation() {
		Catalog.set("close", sheepInNearDistance().size);
		Catalog.set("inreach", sheepInMediumDistance().size);
		Catalog.set("distant", sheepInFarDistance().size);
		Catalog.evalAllRules();
		return Catalog.get("Excitation");
	}
	
	public BitmapFont getFont() {
		return this.font;
	}
}
