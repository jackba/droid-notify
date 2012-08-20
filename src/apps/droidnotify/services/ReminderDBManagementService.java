package apps.droidnotify.services;

import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.reminder.ReminderCommon;

public class ReminderDBManagementService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public ReminderDBManagementService() {
		super("ReminderDBManagementService");
		_debug = Log.getDebug();
		if (_debug) Log.v("ReminderDBManagementService.ReminderDBManagementService()");
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
		if (_debug) Log.v("ReminderDBManagementService.doWakefulWork()");
		try{
			ReminderCommon.cleanDB(getApplication());
		}catch(Exception ex){
			Log.e("ReminderDBManagementService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}