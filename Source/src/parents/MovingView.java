package parents;

import interfaces.GameActivityInterface;
import interfaces.MovingViewInterface;
import support.ConditionalHandler;
import support.KillableRunnable;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jtronlabs.to_the_moon.GameActivity;
import com.jtronlabs.to_the_moon.MainActivity;

/**
 * 
 * @author JAMES LOWREY
 *
 */
public abstract class MovingView extends ImageView implements MovingViewInterface{

	public static final float
		DEFAULT_SPEED_Y = 20,
		DEFAULT_SPEED_X = DEFAULT_SPEED_Y; //Gravity looks good at about 20 DPI. This can be overridden in a child class
	
	public static final int HOW_OFTEN_TO_MOVE=100;
	private float speedX,speedY;
	
	boolean isRemoved;
	
	private KillableRunnable moveRunnable;
	
	public MovingView(Context context,float movingSpeedY,float movingSpeedX,int width,int height,int imageId) {
		super(context);

		this.setLayoutParams( new RelativeLayout.LayoutParams(width,height) );
		this.setImageResource(imageId);

		((GameActivityInterface)context).addToForeground(this);
		
		moveRunnable = new KillableRunnable(){
			@Override
			public void doWork() {
				move();
				ConditionalHandler.postIfAlive(this, HOW_OFTEN_TO_MOVE,MovingView.this);
			}
		};

		speedY = ( Math.abs(movingSpeedY)*MainActivity.getScreenDens() );
		speedX = ( movingSpeedX*MainActivity.getScreenDens() );
		
		isRemoved=false;
		
		this.post(moveRunnable);
	}
	
	@Override
	public void reassignMoveRunnable(KillableRunnable r){
		moveRunnable.kill();;
		moveRunnable = r;
		this.post(r);
	}
	
	@Override 
	public void killMoveRunnable(){
		if(moveRunnable!=null){
			moveRunnable.kill();
		}
	}
	@Override 
	public void reviveMoveRunnable(){
		if(moveRunnable!=null){
			moveRunnable.revive();
			this.post(moveRunnable);
		}
	}
	
	@Override
	public void restartThreads(){
		ConditionalHandler.postIfAlive(moveRunnable, HOW_OFTEN_TO_MOVE,MovingView.this);		
	}
	
	public boolean RectToRectCollisionDetection(View two){
		float left1,right1,top1,bottom1;
		float left2,right2,top2,bottom2;
		
		//find the values of the x,y positions of the two views
		left1=getX();
		right1=getX()+getWidth();
		top1=getY();
		bottom1=getY()+getHeight();

		left2=two.getX();
		right2=two.getX()+two.getWidth();
		top2=two.getY();
		bottom2=two.getY()+two.getHeight();
		
		//Simple collision detection - determine if the two rectangular areas intersect
		//http://devmag.org.za/2009/04/13/basic-collision-detection-in-2d-part-1/
		return !((bottom1 < top2) ||(top1 > bottom2) || (left1>right2) || (right1<left2));
	}
	
	/**
	 * Move the View on the screen according to is speedY or speedX.
	 * @param direction-whichDirection the View should move. Input needs to be ProjectileView.UP, ProjectileView.RIGHT,ProjectileView.DOWN, or ProjectileView.LEFT
	 * @return Always returns false, overwrite for different behavior
	 */
	public void move(){
		//Move by setting this instances X or Y position to its current position plus its respective speed.
		float x = this.getX();
		float y = this.getY();
		y+=this.getSpeedY();
		x+=this.getSpeedX();
		
		//check that object is still within screen bounds
		if(y < -getHeight() || y > GameActivity.getBottomScreen() || x < -this.getWidth() || x > (MainActivity.getWidthPixels() + this.getWidth()) ){
			this.removeGameObject();
		}else{
			this.setY(y);
			this.setX(x);
		}
	}
	
	public void move(int boundLeft,int boundRight, int boundTop, int boundBottom){
		//Move by setting this instances X or Y position to its current position plus its respective speed.
		float x = this.getX();
		float y = this.getY();
		y+=getSpeedY();
		x+=getSpeedX();
		
		//check that object is still within screen bounds
		if(y < -getHeight() || y > GameActivity.getBottomScreen() || x < -this.getWidth() || x > (MainActivity.getWidthPixels() + this.getWidth()) ){
			this.removeGameObject();
		}else{
			if(y>boundTop && y<boundBottom){this.setY(y);}
			if(x>boundLeft && x<boundRight){this.setX(x);}

//			if(y<boundTop){this.setY(boundTop);this.setX(x);}//if outside of bounds, set position to bound (this algorithm does not currently work)
//			else if(y>boundBottom){this.setY(boundBottom-this.getHeight());this.setX(x);}
//			else if(x<boundLeft){this.setX(boundLeft);this.setY(y);}
//			else if( x>boundRight){this.setX(boundRight-this.getWidth());this.setY(y);}
//			else{
//				this.setY(y);
//				this.setX(x);
//			}
		}
	}

	/**
	 * 
	 * @param newSpeed
	 * 		Speed in DPI units
	 */
	public void setSpeedX(float newSpeed){
		speedX = newSpeed*MainActivity.getScreenDens();
	}
	/**
	 * 
	 * @param newSpeed
	 * 		Speed in DPI units
	 */
	public float getSpeedX(){
		return speedX/MainActivity.getScreenDens();
	}
	/**
	 * 
	 * @param newSpeed
	 * 		Speed in DPI units
	 */
	public void setSpeedY(float newSpeed){
		speedY = newSpeed*MainActivity.getScreenDens();
	}
	/**
	 * 
	 * @return 
	 * 		Speed in DPI units
	 */
	public float getSpeedY(){
		return speedY/MainActivity.getScreenDens();
	}
	public boolean isRemoved(){
		return isRemoved;
	}
	public abstract void removeGameObject();
	 
	/**
	 * To be called on every implementation of removeGameObject();
	 */
	protected void deaultCleanupOnRemoval(){
		isRemoved=true;
		this.removeCallbacks(null);	


		((GameActivityInterface)getContext()).removeView(this);
	}
	
	protected void createExplosion(int width,int height,int explosionImgId,long[] vibrationPattern){
		if(vibrationPattern!=null){
			 //vibrate the phone  
	        Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
	        vibrator.vibrate(vibrationPattern, -1);
		}

		final ImageView exp = new ImageView(getContext());
		exp.setImageResource( explosionImgId );
		exp.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
		
		exp.setX(this.getX());
		exp.setY(this.getY());

		((GameActivityInterface)getContext()).addToForeground(exp);
		
		exp.postDelayed(new KillableRunnable(){
			@Override
			public void doWork() {
				((GameActivityInterface)MovingView.this.getContext()).removeView(exp);
			}
		},500);	
	}
	
	protected void createExplosion(int width,int height,int explosionId){
		createExplosion(width,height,explosionId,null);
	}
		
}