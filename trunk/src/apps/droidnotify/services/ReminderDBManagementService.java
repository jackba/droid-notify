package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;

import apps.droidnotify.log.Log;
import apps.droidnotify.reminder.ReminderCommon;

public class ReminderDBManagementService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public ReminderDBManagementService() {
		super("ReminderDBManagementService");
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
		Context context = getApplicationContext();
		try{
			ReminderCommon.cleanDB(getApplication());
		}catch(Exception ex){
			Log.e(context, "ReminderDBManagementService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}