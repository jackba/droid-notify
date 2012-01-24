package apps.droidnotify.services;

import android.content.Intent;

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

public class RescheduleService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public RescheduleService() {
		super("RescheduleService");
		_debug = Log.getDebug();
		if (_debug) Log.v("RescheduleService.RescheduleService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("RescheduleService.doWakefulWork()");
    	Common.startNotificationActivity(getApplicationContext(), intent.getExtras());
	}
		
}