package enemies_orbiters;

import guns.Gun;
import guns.Gun_SingleShotStraight;
import interfaces.MovingViewInterface;
import parents.Moving_ProjectileView;
import support.ConditionalHandler;
import support.KillableRunnable;
import android.content.Context;
import bullets.Bullet_Basic_LaserLong;

import com.jtronlabs.to_the_moon.MainActivity;
import com.jtronlabs.to_the_moon.R;


public class Orbiter_TriangleView extends Shooting_OrbiterView implements MovingViewInterface {

	public final static int DEFAULT_ORBIT_LENGTH=10,
			DEFAULT_ANGLE = 30,
			DEFAULT_BACKGROUND=R.drawable.ship_enemy_orbiter_triangle;
	
	private int currentSideOfTriangle, orbitDist;
	
	public Orbiter_TriangleView(Context context) {
		super(context, DEFAULT_SCORE, 
				(int)context.getResources().getDimension(R.dimen.ship_orbit_triangular_width), 
				(int)context.getResources().getDimension(R.dimen.ship_orbit_triangular_height), 
				DEFAULT_BACKGROUND);

		orbitDist=DEFAULT_ORBIT_LENGTH;

		init();
	}
	

	public Orbiter_TriangleView(Context context,int score,float speedY,int collisionDamage, 
			int health,float probSpawnBeneficialObjecyUponDeath,
			int orbitLength, int orbitPixelX, int orbitPixelY,int width,int height,int imageId) {
		super(context, score,speedY,
				collisionDamage, health, probSpawnBeneficialObjecyUponDeath, 
				orbitPixelX, orbitPixelY, width, height, imageId);
		
		orbitDist=orbitLength;

		init();
	}
	
	private void init(){
		currentSideOfTriangle=0;
		
		//default to begin orbit at top of triangle, 1/3 of way through (thus top = moving left. it is not a perfect orbit, but good enough)
		this.setThreshold((int) (orbitY-(orbitDist*Math.abs(this.getSpeedY()) ) / 2 ));
		howManyTimesMoved=(int) (orbitDist * (2/3.0));
		

		//override default gun
		float freq= getShootingFreq();
		this.removeAllGuns();
		Gun g1 = new Gun_SingleShotStraight(getContext(), this, new Bullet_Basic_LaserLong(),
				freq, DEFAULT_BULLET_SPEED_Y, DEFAULT_BULLET_DAMAGE,20);
		Gun g2 = new Gun_SingleShotStraight(getContext(), this, new Bullet_Basic_LaserLong(),
				freq, DEFAULT_BULLET_SPEED_Y, DEFAULT_BULLET_DAMAGE,80);
		this.addGun(g1);
		this.addGun(g2);
		this.startShooting();
	}


	@Override
	protected void reachedGravityPosition() {
		
		reassignMoveRunnable( new KillableRunnable(){
			@Override
			public void doWork() {
						//change side
						if (howManyTimesMoved % orbitDist == 0) {
							currentSideOfTriangle = (currentSideOfTriangle + 1) % 3;

							//triangle is equilateral
							switch (currentSideOfTriangle) {
							case 0:
								Orbiter_TriangleView.this.setSpeedY(0);
								Orbiter_TriangleView.this.setSpeedX( - DEFAULT_SPEED_X * 2);
								break;
							case 1:
								Orbiter_TriangleView.this.setSpeedY(DEFAULT_SPEED_Y);
								Orbiter_TriangleView.this.setSpeedX(DEFAULT_SPEED_X);
								break;
							case 2:
								Orbiter_TriangleView.this.setSpeedY( - DEFAULT_SPEED_Y);
								Orbiter_TriangleView.this.setSpeedX(DEFAULT_SPEED_X);
								break;
							}
						}
						howManyTimesMoved++;
						
						move();
						ConditionalHandler.postIfAlive(this,Moving_ProjectileView.HOW_OFTEN_TO_MOVE,Orbiter_TriangleView.this);
			}
		}); 
	}
	public int orbitLengthX(){
		return (int) ( orbitDist*DEFAULT_SPEED_X * MainActivity.getScreenDens() );
	}
	public int orbitLengthY(){
		return (int) (orbitDist*DEFAULT_SPEED_Y  * MainActivity.getScreenDens() );
	}
}