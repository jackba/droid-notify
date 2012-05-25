package apps.droidnotify.services;

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

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class allows us to aquire a WakeLock, do work on an Intent, and then releases the WakeLock.
 * 
 * @author CommonsWare edited by Camille Sévigny
 */
abstract public class WakefulIntentService extends IntentService {
	
	abstract protected void doWakefulWork(Intent intent);

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;

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
		_debug = Log.getDebug();
		if(_debug) Log.v("WakefulIntentService.WakefulIntentService()");
	    setIntentRedelivery(true);
	}

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	public static void sendWakefulWork(Context context, Intent intent) {
		if(_debug) Log.v("WakefulIntentService.sendWakefulWork()");
		Common.acquirePartialWakeLock(context);
		context.startService(intent);
	}
	
	/**
	 * 
	 * 
	 * @param context - Application Context.
	 * @param clsService
	 */
	public static void sendWakefulWork(Context context, Class<?> clsService) {
		if(_debug) Log.v("WakefulIntentService.sendWakefulWork()");
		sendWakefulWork(context, new Intent(context, clsService));
	}
	
	/**
	 * 
	 * 
	 * @param startId - The ID.
	 * @param flags - The flags.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(_debug) Log.v("WakefulIntentService.onStartCommand()");
		if ((flags & START_FLAG_REDELIVERY) != 0) { // if crash restart...
			Common.acquirePartialWakeLock(this.getApplicationContext());  // ...then quick grab the lock
	    }
	    super.onStartCommand(intent, flags, startId);
	    return(START_REDELIVER_INTENT);
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
		if(_debug) Log.v("WakefulIntentService.onHandleIntent()");
		try {
			doWakefulWork(intent);
		}catch(Exception ex){
			if(_debug) Log.e("WakefulIntentService.onHandleIntent() ERROR: " + ex.toString());
		}finally {
			if(!Common.isFullWakelockInUse()){
				Common.clearWakeLock();
			}
		}
	}
	
}