package enemies_orbiters;

import interfaces.MovingObject;
import android.content.Context;


public class Orbiter_Circle_IncSpeedOnHitView extends Orbiter_CircleView implements MovingObject {
	
	public static final int ANGULAR_INCREMENT=3;
	
	public Orbiter_Circle_IncSpeedOnHitView(Context context,int score,double speedY, double speedX,double collisionDamage, 
			double health, double bulletFreq,
			double bulletDamage,double bulletVerticalSpeed,double probSpawnBeneficialObjecyUponDeath,
			int circleRadius,int angularVelocityInDegrees,int width,int height,int imageId) {
		super( context, score, speedY,  speedX, collisionDamage, 
				 health,  probSpawnBeneficialObjecyUponDeath,
				 circleRadius, angularVelocityInDegrees, width, height, imageId);
	}
	
	/**
	 * increase absolute value of angular velocity by 1
	 */
	@Override
	public boolean takeDamage(double amountOfDamage){
		this.setAngularVelocity(this.getAngularVelocity() + this.getAngularVelocity()/(Math.abs(this.getAngularVelocity())));
		return super.takeDamage(amountOfDamage);
	}
}
