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
	
	protected double getAngleToTarget(Vector2 ownDirection, Vector2 targetDirection) {
		Vector2 norOwnDirection    = new Vector2(ownDirection).nor();
		Vector2 norTargetDirection = new Vector2(targetDirection).nor();
		
		double cos   = norOwnDirection.dot(norTargetDirection);
		double rad   = Math.acos(cos);
		double angle = Math.toDegrees(rad);
		
		System.out.println("cos:" + cos + " rad: " + rad + " ang: " + angle);
		
		return angle;
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
				Vector2 targetCenter = target.getCenterPosition().cpy();
				targetCenter.sub(currentCenter);
				direction.add(targetCenter.nor());
			}
		}

		return direction.nor();
	}

	// protected Vector2 getLookDirektion(){
	// getRotation()
	// }

}
