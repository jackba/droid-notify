package apps.droidnotify;

/***
	Copyright (c) 2008-2011 CommonsWare, LLC
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain	a copy
	of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
	by applicable law or agreed to in writing, software distributed under the
	License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
	OF ANY KIND, either express or implied. See the License for the specific
	language governing permissions and limitations under the License.
	
	From _The Busy Coder's Guide to Advanced Android Development_ http://commonsware.com/AdvAndroid
*/

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * This class allows us to aquire a WakeLock, do work on an Intent, and then releases the WakeLock.
 * 
 * @author CommonsWare edited by Camille Sévigny
 */
abstract public class WakefulIntentService extends IntentService {
	
	abstract void doWakefulWork(Intent intent);

	//================================================================================
    // Properties
    //================================================================================
	
	public static final String LOCK_NAME_STATIC="app.droidnotify.android.syssvc.AppService.Static";
	private static PowerManager.WakeLock lockStatic = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
	 * Class Constructor.
	 * 
	 * @param name - String name of the service.
	 */
	public WakefulIntentService(String name) {
		super(name);
		if (Log.getDebug()) Log.v("WakefulIntentService.WakefulIntentService()");
	}

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Aquire the WakeLock.
	 * 
	 * @param context - Application Context.
	 */
	public static void acquireStaticLock(Context context) {
		if (Log.getDebug()) Log.v("WakefulIntentService.acquireStaticLock()");
		getLock(context).acquire();
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Handles the intent that we are working with.
	 * 
	  * @param intent - Intent object that we are working with.
	 */
	@Override
	final protected void onHandleIntent(Intent intent) {
		if (Log.getDebug()) Log.v("WakefulIntentService.onHandleIntent()");
		try {
			doWakefulWork(intent);
		}
		finally {
			getLock(this).release();
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Instantiates the WakeLock and returns in.
	 * 
	 * @param context - Application Context.
	 * 
	 * @return WakeLock - Returns the instantiated WakeLock.
	 */
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (Log.getDebug()) Log.v("WakefulIntentService.getLock()");
		if (lockStatic==null) {
			PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
			lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return(lockStatic);
	}
	
}