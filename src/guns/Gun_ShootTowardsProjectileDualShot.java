package guns;
  
import parents.Moving_ProjectileView;
import interfaces.Shooter;
import android.content.Context;
import bullets.Bullet;
import bullets.BulletView;

import com.jtronlabs.to_the_moon.GameActivity;
import com.jtronlabs.to_the_moon.MainActivity;

public  class Gun_ShootTowardsProjectileDualShot extends Gun {
	
	public static double MAX_X_SPEED=2.1*MainActivity.getScreenDens();
	
	private Moving_ProjectileView shootTowardsMe;
	
	public Gun_ShootTowardsProjectileDualShot(Context context,Moving_ProjectileView shootingAtMe,
			Shooter theShooter,Bullet bulletType,double bulletFrequency,double bulletSpeedVertical,double bulletDmg) {
		super(context,theShooter,bulletType, bulletFrequency, bulletSpeedVertical, bulletDmg);
		
		shootTowardsMe = shootingAtMe;
	}

	public Gun_ShootTowardsProjectileDualShot(Context context,
			Shooter theShooter,Bullet bulletType,double bulletFrequency,double bulletSpeedVertical,double bulletDmg) {
		super(context,theShooter,bulletType, bulletFrequency, bulletSpeedVertical, bulletDmg);
		
		shootTowardsMe=null;
	}
	
	public boolean shoot(){		
		//check if there is a target set
		if(shootTowardsMe==null){
			if(GameActivity.enemies.size()>0){
				//find oldest living enemy
				for(int i=GameActivity.enemies.size()-1;i>=0;i--){
					Moving_ProjectileView cast = (Moving_ProjectileView) GameActivity.enemies.get(i);
					//check if an enemy is living. if so, set him as target and stop looping
					if( ! cast.isRemoved()){
						shootTowardsMe= cast;
						break;
					}
				}
			}
		}

		double bulletSpeedX = 0;
		//if there is still no target set, shoot straight. Otherwise calculate horizontal speed such that a bullet shot from the 
		//left side of the ship would hit the View shot at, if it were stationary		
		if(shootTowardsMe!=null){
			
			//find the absolute value of vertical distance between the shooter and shot at
			double diffYAbs;
			if(shooter.isFriendly()){
				diffYAbs = Math.abs(shooter.getY() - ( shootTowardsMe.getY()+shootTowardsMe.getHeight() ) );
			}else{
				diffYAbs = Math.abs( shooter.getY()+shooter.getHeight()- shootTowardsMe.getY() );//bottom of shooter - top of ShotAt
			}
			
			//find horizontal distance between left side of shooter to middle of ShotAt. 
			//All spawned bullets will travel at same horizontal speed
			final double diffX = ( shootTowardsMe.getX()*2 +shootTowardsMe.getWidth() )/2 - shooter.getX();
			
			//set bulletSpeedX to bulletSpeedY scaled by the ratio of the differences of X/Y. 
			//You are forming a similar triangle to the one formed by the differences
			bulletSpeedX = bulletSpeedY *diffX/diffYAbs;
			
			//limit the x speed
			//Otherwise at the bottom of the screen, bullets are ridiculously quick in X direction 
			if( Math.abs(bulletSpeedX) > MAX_X_SPEED ){
				bulletSpeedX = MAX_X_SPEED * ( bulletSpeedX/Math.abs(bulletSpeedX) );
			}
		}

		//create 2 bullets
		BulletView bulletLeft = myBulletType.getBullet(ctx, shooter,bulletSpeedY,bulletDamage);
		BulletView bulletRight = myBulletType.getBullet(ctx, shooter,bulletSpeedY,bulletDamage);
		
		//position bullets on edges of shooter
		bulletLeft.setPositionOnShooterAsAPercentage(0);
		bulletRight.setPositionOnShooterAsAPercentage(100);
		
		//set bullet's X speed 
		bulletLeft.setSpeedX(bulletSpeedX);
		bulletRight.setSpeedY(bulletSpeedX);		
		
		//add bullets to layout
		shooter.getMyScreen().addView(bulletLeft,1);
		shooter.getMyScreen().addView(bulletRight,1);
		
		return false;
	}
}