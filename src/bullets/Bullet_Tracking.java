package bullets;

import interfaces.Shooter;

import java.util.ArrayList;

import support.ConditionalHandler;
import abstract_parents.MovingView;

import com.jtronlabs.to_the_moon.MainActivity;
  

// 					NOT WORKING			
public abstract class Bullet_Tracking extends Bullet{

	public static final double MAX_TRACKING_SPEED=5*MainActivity.getScreenDens();
	public static final double DEFAULT_TRACKING_SPEED=4*MainActivity.getScreenDens();
	
	private double trackingSpeed;
	private MovingView viewTracking;
	private Shooter shooter;
	
	
	public Bullet_Tracking(double trackSpeed, MovingView viewToTrack,Shooter shooterWithTrackingBullets) {
		
		shooter = shooterWithTrackingBullets;
		final double trackSpeedDPI  = trackSpeed*MainActivity.getScreenDens();
		trackingSpeed = (trackSpeedDPI>MAX_TRACKING_SPEED) ? MAX_TRACKING_SPEED * 
				MainActivity.getScreenDens() : trackSpeedDPI;
		viewTracking=viewToTrack;
		shooter.post(trackingRunnable);
	}
	
	public Bullet_Tracking(MovingView viewToTrack,Shooter shooterWithTrackingBullets) {
		
		shooter = shooterWithTrackingBullets;
		trackingSpeed = DEFAULT_TRACKING_SPEED;
		
		viewTracking=viewToTrack;
		shooter.post(trackingRunnable);
	}

	Runnable trackingRunnable = new Runnable(){
    	@Override
        public void run() {
    		final float objectTrackingMidPoint = (2* viewTracking.getX()+viewTracking.getWidth() ) /2;
    		
			ArrayList<BulletView> bullets = shooter.getMyBullets();
			
			for(BulletView bullet : bullets){
	    		final float bulletXMidPos = (2 * bullet.getX()+bullet.getWidth() ) / 2; 
				final float diff = bulletXMidPos - objectTrackingMidPoint;
    			
				//if bullet is approximately at tracking destination, don't move it and set rotation to 0
	    		if( Math.abs(diff) < shooter.getWidth() ){
	    			bullet.setSpeedX(Bullet.BULLET_TRAVELS_STRAIGHT);
	    		}else{
	    			trackingSpeed = -diff/Math.abs(diff) * Math.abs(trackingSpeed);//track in direction of difference
	    			 
	    			//set speed and move sideways
        			bullet.setSpeedX(trackingSpeed);
        			bullet.moveDirection(MovingView.SIDEWAYS);    
	    		}
	    		
    			bullet.setBulletRotation();
			}
			ConditionalHandler.postIfAlive(this,MovingView.HOW_OFTEN_TO_MOVE*3,shooter);//I could use the default time and lower the speed, but having a higher speed and a less often movement time is less computationally intense
    	}
	};

	public void stopBulletThreads(){
		shooter.removeCallbacks(trackingRunnable);
	}
		
		
}
