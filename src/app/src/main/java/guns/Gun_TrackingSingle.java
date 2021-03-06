package guns;
  
import helpers.MediaController;
import interfaces.Shooter;
import parents.Moving_ProjectileView;
import android.widget.RelativeLayout;
import bullets.BulletView;
import bullets.Bullet_Interface;

import com.jtronlabs.space_shooter.MainActivity;

public  class Gun_TrackingSingle extends Gun_Tracking {
	
	public static double MAX_X_SPEED=15*MainActivity.getScreenDens();
	
	private Moving_ProjectileView shootTowardsMe;
	
	public Gun_TrackingSingle(RelativeLayout layout, Moving_ProjectileView shootingAtMe,
			Shooter theShooter,Bullet_Interface bulletType,float bulletFrequency,float bulletSpeedVertical,int bulletDmg,int positionOnShooterAsAPercentage) {
		super(layout,theShooter,bulletType, bulletFrequency, bulletSpeedVertical, bulletDmg, positionOnShooterAsAPercentage);
		
		shootTowardsMe = shootingAtMe;
	}

	public Gun_TrackingSingle(RelativeLayout layout,
			Shooter theShooter,Bullet_Interface bulletType,float bulletFrequency,float bulletSpeedVertical,int bulletDmg,int positionOnShooterAsAPercentage) {
		super(layout,theShooter,bulletType, bulletFrequency, bulletSpeedVertical, bulletDmg, positionOnShooterAsAPercentage);
		
		shootTowardsMe=null;
	}
	
	public boolean shoot(){		
		MediaController.playSoundEffect(gameScreen.getContext(), MediaController.SOUND_LASER_SHOOT);
		
		//create 2 bullets
		BulletView bullet = getBullet();

		//set bullet's X speed 
		bullet.setSpeedX(getTrackingXSpeed(shooter,shootTowardsMe,this.bulletSpeedY));
		
		return false;
	}
}
