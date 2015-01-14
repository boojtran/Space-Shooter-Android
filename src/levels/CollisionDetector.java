package levels;

import parents.MovingView;
import interfaces.Shooter;
import android.os.Handler;
import android.util.Log;
import bonuses.BonusView;
import bullets.BulletView;
import enemies.EnemyView;
import friendlies.FriendlyView;

public class CollisionDetector {
	
	private LevelSystem myLevelSystem;
	
	public CollisionDetector(LevelSystem aLevelSystem){
		myLevelSystem=aLevelSystem;
	}
	
	private Handler gameHandler = new Handler();
    private Runnable collisionDetectionRunnable = new Runnable() { 

        @Override
        public void run() {
        	if( ! myLevelSystem.isLevelPaused() && ! myLevelSystem.areLevelWavesCompleted() || LevelSystem.enemies.size() !=0){
        		
        		try{
	        		detectAnyFriendlyHasCollidedWithAnyEnemy();
	        		detectAnyFriendlyHasHitAnyEnemyBullet();
	        		detectAnyFriendlyHasHitAnyBonus();
	        		detectAnyEnemyHasHitAnyFriendlyBullets();
        		}catch(IndexOutOfBoundsException e){
        			//it is possible for enemy to be removed after beginning a for(enemies) loop, but before calling enemies.get(i)
        			//in that case, just catch the error and don't worry about it, it was already processed
        			//...i think
        		}
        		
	            gameHandler.postDelayed(this, MovingView.HOW_OFTEN_TO_MOVE);
	            
        	}else{
        		if( myLevelSystem.getLevel() == myLevelSystem.highestLevel() ){
        			Log.d("lowrey","beatGame");
        			myLevelSystem.getInteractivityInterface().beatGame();
        		}else{ 
        			myLevelSystem.getInteractivityInterface().openStore();
        		}
        	}
        } 
    };
    
    private void detectAnyFriendlyHasCollidedWithAnyEnemy(){		
    	for(int k=LevelSystem.friendlies.size()-1;k>=0;k--){
    		FriendlyView friendly = LevelSystem.friendlies.get(k);
    		
	    	for(int i=LevelSystem.enemies.size()-1;i>=0;i--){
	    		EnemyView enemy = LevelSystem.enemies.get(i);
	    		if(friendly.collisionDetection(enemy)){
	    			
	    			friendly.takeDamage(enemy.getDamage());
	    			enemy.removeGameObject();
	    		}
	    	}
    	}
    }
    
    private void detectAnyFriendlyHasHitAnyEnemyBullet(){
    	for(int k=LevelSystem.friendlies.size()-1;k>=0;k--){
			FriendlyView friendly = LevelSystem.friendlies.get(k);
			
			for(int j=LevelSystem.enemyBullets.size()-1;j>=0;j--){
				BulletView bullet = LevelSystem.enemyBullets.get(j);
				if( friendly.collisionDetection(bullet)){

	    			friendly.takeDamage(bullet.getDamage());
	    			bullet.removeGameObject();
	    		}
			}
    	}
    }

    private void detectAnyFriendlyHasHitAnyBonus(){
    	for(int k=LevelSystem.friendlies.size()-1;k>=0;k--){
			FriendlyView friendly = LevelSystem.friendlies.get(k);
			
			//check if friendly has hit a bonus
			if(friendly instanceof Shooter){
				Shooter friendlyShooter= (Shooter)LevelSystem.friendlies.get(k);
		    	for(int i=LevelSystem.bonuses.size()-1;i>=0;i--){
		    		
		    		BonusView bonus = LevelSystem.bonuses.get(i);
		    		if( /*! bonus.isRemoved() &&*/ friendly.collisionDetection(bonus)){//game object could be removed in previous loop iteration
		    			bonus.applyBenefit(friendlyShooter);
		    			bonus.removeGameObject();
		    		}
		    	}
			}
    	}
    }
    
    private void detectAnyEnemyHasHitAnyFriendlyBullets(){
    	for(int i=LevelSystem.enemies.size()-1;i>=0;i--){
    		EnemyView enemy = LevelSystem.enemies.get(i);
    		
	    		for(int j=LevelSystem.friendlyBullets.size()-1;j>=0;j--){
	    			
					BulletView bullet = LevelSystem.friendlyBullets.get(j);
					if( bullet.collisionDetection(enemy)){
						
	        			enemy.takeDamage(bullet.getDamage());
	        			bullet.removeGameObject();
	        		}
	        	}
    	}
    }
    
    public void startDetecting(){
    	gameHandler.post(collisionDetectionRunnable);
    }
    public void stopDetecting(){
    	gameHandler.removeCallbacks(collisionDetectionRunnable);
    }
}