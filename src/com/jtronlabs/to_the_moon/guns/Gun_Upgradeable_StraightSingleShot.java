package com.jtronlabs.to_the_moon.guns;
  
import android.content.Context;
import android.widget.RelativeLayout;

import com.jtronlabs.to_the_moon.views.Gravity_ShootingView;

public class Gun_Upgradeable_StraightSingleShot extends Gun_Upgradeable {
	
	private static final double STRAIGHT_BULLET=-10;
	
	public Gun_Upgradeable_StraightSingleShot(Context context,Gravity_ShootingView theShooter,boolean shootingUpwards,double bulletSpeedVertical,
			double bulletDamage,double bulletFrequency) {
		super(context,theShooter,shootingUpwards,bulletSpeedVertical,bulletDamage,bulletFrequency);
	}
	public Gun_Upgradeable_StraightSingleShot(Context context,Gravity_ShootingView theShooter,Gun_Upgradeable previousGunObject) {
		super(context,theShooter,previousGunObject);
	}
	public boolean spawnMyBullet(){
		BulletView bulletMid= new BulletView(ctx, shooter, shootingUp,BulletView.BULLET_MIDDLE,
				bulletSpeedY, STRAIGHT_BULLET, bulletDamage);
		
		//add bullets to layout
		((RelativeLayout)shooter.getParent()).addView(bulletMid,1);

		//add bullets to shooter's list of bullets
		myBullets.add(bulletMid);
		
		return false;
	}
	/**
	 * Upgrade is a dual shot
	 */
	@Override
	public Gun_Upgradeable upgradeGun() {
		this.stopShooting();
		return new Gun_Upgradeable_StraightDualShot(ctx,this,shooter);
	}
	@Override
	public Gun_Upgradeable downgradeGun() {
		return this;
	}
	
}