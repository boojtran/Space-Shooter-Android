package com.jtronlabs.to_the_moon.misc;

public interface GameObjectInterface {

	/**
	 * Manage the execution of threads. onPause all callbacks will be 
	 * removed from Views, but onResume it may be that not every thread needs restarted
	 */
	public void restartThreads();
	
	/**
	 * Cover the special circumstances when removing a view
	 * @param showExplosion
	 * @return
	 */
	public int removeView(boolean showExplosion);
}
