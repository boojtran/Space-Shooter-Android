package com.jtronlabs.to_the_moon.guns;
  
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout.LayoutParams;

import com.jtronlabs.to_the_moon.GameActivity;
import com.jtronlabs.to_the_moon.R;
import com.jtronlabs.to_the_moon.views.Gravity_ShootingView;
import com.jtronlabs.to_the_moon.views.ProjectileView;

public class BulletView extends ProjectileView{

	public final static int BULLET_LEFT=-1,BULLET_MIDDLE=0,BULLET_RIGHT=1;
	
	public static final double DEFAULT_HEALTH=0.1;
	public static final int DEFAULT_SCORE=0;
	
	private int whichSideOfShipBulletIsOn=BULLET_MIDDLE;
	private Gravity_ShootingView theOneWhoShotMe;
	
	private boolean shootingUp,shootingRight;
	
	Runnable moveBulletRunnable = new Runnable(){
    	@Override
        public void run() {
    		boolean atThreshold=false;
    		//move up and down
    		if(shootingUp){
    			atThreshold=BulletView.this.move(ProjectileView.UP);
    		}else{
    			atThreshold=BulletView.this.move(ProjectileView.DOWN);
    		}
    		
    		if(atThreshold || BulletView.this.getHealth()<=0){//off screen
    			BulletView.this.removeView(false);
    			BulletView.this.removeCallbacks(this);
    		}else{
    			//move left and right
    			if(BulletView.this.getSpeedX()>0){
            		if(whichSideOfShipBulletIsOn!=BULLET_MIDDLE && shootingRight){
            			atThreshold=BulletView.this.move(ProjectileView.RIGHT);
            		}else if(whichSideOfShipBulletIsOn!=BULLET_MIDDLE){
            			atThreshold=BulletView.this.move(ProjectileView.LEFT);
            		}
        		}
    			BulletView.this.postDelayed(this,ProjectileView.HOW_OFTEN_TO_MOVE);
    		}
    	}
	};
	
	public BulletView(Context context,Gravity_ShootingView shooter,boolean shootBulletUp,int whichSideIsBulletOn,
			double projectileSpeedVertical,double projectileSpeedX, double projectileDamage) {
		super(context,DEFAULT_SCORE,projectileSpeedVertical,projectileSpeedVertical,
				projectileSpeedX,projectileDamage,DEFAULT_HEALTH,0);
		

		initBullet(shooter,shootBulletUp,whichSideIsBulletOn);
	}
	
	public BulletView(Context context,AttributeSet at,Gravity_ShootingView shooter,boolean shootBulletUp,
			int whichSideIsBulletOn,double projectileSpeedVertical,double projectileSpeedX, 
			double projectileDamage) {
		super(context,at,DEFAULT_SCORE,projectileSpeedVertical,projectileSpeedVertical,
				projectileSpeedX,projectileDamage,DEFAULT_HEALTH,0);

		initBullet(shooter,shootBulletUp,whichSideIsBulletOn);
	}
	private void initBullet(Gravity_ShootingView shooter,boolean shootBulletUp,
			int whichSideIsBulletOn){
		
		//set instance vars
		theOneWhoShotMe=shooter;
		whichSideOfShipBulletIsOn=whichSideIsBulletOn;
		
		//set Y position and the instance boolean for whether or not bullet is traveling up or down,left or right
		shootingUp=shootBulletUp;
		if(shootingUp){
			this.setY(shooter.getY());			
		}else{
			this.setY(shooter.getY()+shooter.getHeight());			
		}
		
		setBulletToLaserOne();
		
		//post the movement runnable
		this.post(moveBulletRunnable);
	}
	
	private void setBulletXPositionAndThresholds(int bulletWidth,int bulletHeight){
		//set thresholds to off screen
		this.highestPositionThreshold=-bulletHeight;
		this.lowestPositionThreshold=(int) (heightPixels+bulletHeight);
		
		final float leftOfShooter=theOneWhoShotMe.getX(),rightOfShooter=leftOfShooter+theOneWhoShotMe.getWidth();
		final float middleofShooter =(leftOfShooter+rightOfShooter)/2-bulletWidth/2;
		
		//set rotation values and direction of movement as a function of bullet being a LEFT,RIGHT,or MIDDLE bullet
		switch(whichSideOfShipBulletIsOn){
		case BULLET_LEFT:
			if(this.getSpeedX()>0){//bullets are angled
				this.setX(middleofShooter);
				
				shootingRight=false;
				final double arcTanLeft = Math.atan(-this.getSpeedX()/this.getSpeedY());//Use trig to find rotation values of bullets
				final float rotValLeft = (float) Math.toDegrees(arcTanLeft);
				this.setRotation(rotValLeft);
			}else{
				this.setX(leftOfShooter-bulletWidth/2);
			}
			break;
		case BULLET_RIGHT:
			if(this.getSpeedX()>0){//bullets are angled
				this.setX(middleofShooter);
				
				shootingRight=true;
				final double arcTanRight = Math.atan(this.getSpeedX()/this.getSpeedY());//Use trig to find rotation values of bullets
				final float rotValRight = (float) Math.toDegrees(arcTanRight);
				this.setRotation(rotValRight);
			}else{
				this.setX(rightOfShooter-bulletWidth/2);				
			}
			break;
		default:
			this.setX(middleofShooter);
			shootingRight=true;//not needed, but set just in case
			break;
		}
	}
	/**
	 * Clean up threads. Remove bullet from Shooter's list of bullets. Check if Shooter is dead and all bullets are gone, if so remove Shooter from GameActivity.enemies. call super
	 */
	public int removeView(boolean showExplosion){
		cleanUpThreads();
		theOneWhoShotMe.myGun.myBullets.remove(this);
		if(theOneWhoShotMe.getHealth()<=0 && theOneWhoShotMe.myGun.myBullets.size()==0){
			GameActivity.enemies.remove(theOneWhoShotMe);
		}
		return super.removeView(showExplosion);
	}
	public void cleanUpThreads(){
		this.removeCallbacks(moveBulletRunnable);
		super.cleanUpThreads();
	}
	
	public void restartThreads(){
		this.post(moveBulletRunnable);
		super.restartThreads();
	}
	
	public void setBulletToLaserOne(){
		//give bullet a width and height, set 
		final int width=(int) ctx.getResources().getDimension(R.dimen.bullet_width);
		final int height=(int) ctx.getResources().getDimension(R.dimen.bullet_height);		
		setBulletXPositionAndThresholds(width,height);
		this.setLayoutParams(new LayoutParams(width,height));
		
		int backgroundId=R.drawable.laser1_enemy;
		if(shootingUp){backgroundId = R.drawable.laser1_friendly;}
		
		this.setBackgroundResource(backgroundId);
	}
//	/**
//	 * Create a bullet and add it to the screen
//	 */
//	public void spawnLevelOneCenteredBullet(){
//		ProjectileView bullet = createBullet(ctx,BULLET_ONE_SPEED,BULLET_ONE_DMG);
//		
//		//initially position the Bullet View to avg of shooter's left & right, and at top if shooting up (or at bottom if shooting down)
//		float xAvg = (BulletView.this.getX()+BulletView.this.getX()+BulletView.this.getWidth())/2;
//		android.view.ViewGroup.LayoutParams params = this.getLayoutParams();//bullet.getWidth() returns 0 as it is not yet drawn. The params are set though, and thus this method works to find the width
//		bullet.setX(xAvg-params.width/2);
//		
//		if(shootingUp){
//			bullet.setBackgroundResource(R.drawable.laser1_hero);
//			bullet.setY(BulletView.this.getY());//middle and top of ShootingView
//		}else{
//			bullet.setBackgroundResource(R.drawable.laser1_enemy);
//			bullet.setY(BulletView.this.getY()+BulletView.this.getHeight());//middle and bottom of ShootingView
//		}
//		
//		((RelativeLayout)this.getParent()).addView(bullet,1);
//
//	}	
}