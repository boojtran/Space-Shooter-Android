package friendlies;

import helpers.MediaController;

import com.jtronlabs.to_the_moon.GameActivity;
import com.jtronlabs.to_the_moon.MainActivity;
import com.jtronlabs.to_the_moon.R;

import parents.Moving_ProjectileView;
import levels.LevelSystem;
import android.content.Context;

public class FriendlyView extends Moving_ProjectileView{
	
	public FriendlyView(Context context,float projectileSpeedY
			,float projectileSpeedX, 
			int projectileDamage,int projectileHealth,int width,int height,int imageId) {
		super( context, projectileSpeedY,projectileSpeedX, 
				 projectileDamage, projectileHealth, width, height, imageId);
		
		LevelSystem.friendlies.add(this);
	}
	

	@Override
	public void removeGameObject() {
		LevelSystem.friendlies.remove(this);
		deaultCleanupOnRemoval();//needs to be called last for all pending callbacks to 'this' to be removed
	}
 
	@Override
	public boolean takeDamage(int howMuchDamage){
		MediaController.playSoundEffect(getContext(), MediaController.SOUND_FRIENDLY_HIT);
		return super.takeDamage(howMuchDamage);
	}
}
