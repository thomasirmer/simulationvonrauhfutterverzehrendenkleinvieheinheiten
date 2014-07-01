package de.rub.SVRVKVE.animals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameObject extends Sprite {

	public GameObject(Texture texture) {
		super(texture);
		// TODO Auto-generated constructor stub
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2);
	}

	protected float convertAngleForFuzzy(float angle) {

		if ((angle >= 0 && angle < 180) || (angle < 0 && angle > -180))
			return angle;
		else if (angle > 180)
			return -360 + angle;
		else if (angle < -180)
			return 360 + angle;
		else
			return 0;
	}

	/**
	 * Returns the normalized Vector to any number of game Objects you throw at
	 * it.
	 * 
	 * @param Targets
	 *            Array of GameObjects
	 * @return Normalized Vector to targets
	 */

	protected Vector2 getDirectionToGObjects(Array<GameObject> targets) {
		Vector2 currentCenter;
		Vector2 direction = new Vector2(0, 0);

		for (GameObject target : targets) {
			if (!this.equals(target)) {
				currentCenter = getCenterPosition();
				currentCenter.sub(target.getCenterPosition());
				direction.add(currentCenter);
				direction.nor();
			}
		}

		return direction;
	}

	// protected Vector2 getLookDirektion(){
	// getRotation()
	// }

}
