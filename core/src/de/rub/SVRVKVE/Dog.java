package de.rub.SVRVKVE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Dog extends Sprite {

	boolean moveFwrd = false, moveBwrd, rotateLeft = false, rotateRight = false;
	
	float rotation = 0, moveDistance = 0;

	public final static int MOVESPEED = 500;
	public final static int ROTATIONSPEED = 300;

	public static final Texture image = new Texture(
			Gdx.files.internal("dogTopView.png"));

	InputListener inputListener = new InputListener() {
		public boolean keyDown(InputEvent event, int keycode) {
			switchKeyActiveState(keycode);
			return true;
		};

		public boolean keyUp(InputEvent event, int keycode) {
			switchKeyActiveState(keycode);
			return true;
		};

	};

	private void switchKeyActiveState(int keycode) {
		switch (keycode) {
		case 19:
			// Forward
			moveFwrd = !moveFwrd;			
			break;
		case 20:
			moveBwrd=!moveBwrd;			
			break;
		case 21:
			// Right
			rotateRight = !rotateRight;			
			break;
		case 22:
			// Left
			rotateLeft = !rotateLeft;			
			break;
		default:
			break;
		}
	}

	public Dog(GridPoint2 startPosition, int width, int height) {
		super(image);

		image.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		setSize(width, height);
		setPosition(startPosition.x, startPosition.y);
		setOriginCenter();
	}
	
	public void move() {
		float moveValue = MOVESPEED * Gdx.graphics.getDeltaTime();
		float rotationValue = ROTATIONSPEED * Gdx.graphics.getDeltaTime();

		moveDistance=0;
		rotation=0;
		
		if (moveFwrd)
			moveDistance = moveValue;
		if (moveBwrd)
			moveDistance = -moveValue;
		if (rotateLeft)
			rotation = -rotationValue;
		if (rotateRight)
			rotation = rotationValue;

		this.rotate(rotation);
		setNewPosition(moveDistance);
	}

	private void setNewPosition(float moveValue) {
		float directionX = (float) Math.cos(Math.toRadians(getRotation()));
		float directionY = (float) Math.sin(Math.toRadians(getRotation()));
		
		setX(getX() + directionX * moveValue);
		setY(getY() + directionY * moveValue);
		
		int gameWidth=Gdx.graphics.getWidth();
		int gameHeight=Gdx.graphics.getHeight();		
				
		if (getX() > gameWidth- getWidth())
			setX(gameWidth- getWidth());
		if (getX() < 0)
			setX(0);
		if (getY() > gameHeight- getHeight())
			setY(gameHeight- getHeight());
		if (getY() < 0)
			setY(0);
	}

	public void render(SpriteBatch batch) {
		move();
		draw(batch);		
	}

	public InputListener getInputListener() {
		return inputListener;
	}

}
