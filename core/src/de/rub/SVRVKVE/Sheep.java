package de.rub.SVRVKVE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Sheep extends Rectangle {
	
	public static Texture image = new Texture(Gdx.files.internal("sheep3.png"));
	
	public Sheep() {
		// TODO Auto-generated constructor stub
		System.out.println("OLOLOL");
	}

	public Sheep(Rectangle rect) {
		super(rect);
		// TODO Auto-generated constructor stub
	}

	public Sheep(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}

}
