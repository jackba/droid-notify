package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
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
		Context context = getApplicationContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);	
	    Bundle bundle = intent.getExtras();
		int rescheduleNumber = bundle.getInt("rescheduleNumber");
		//Determine if the notification should be rescheduled or not.
		boolean displayNotification = true;
		if(preferences.getBoolean(Constants.REMINDERS_ENABLED_KEY, false)){
			int maxRescheduleAttempts = Integer.parseInt(preferences.getString(Constants.REMINDER_FREQUENCY_KEY, Constants.REMINDER_FREQUENCY_DEFAULT));
			if(maxRescheduleAttempts < 0){
				//Infinite Attempts.
				displayNotification = true;
			}else if(rescheduleNumber > maxRescheduleAttempts){
				displayNotification = false;
			}
		}
		if(!displayNotification){
			if (_debug) Log.v("RescheduleService.doWakefulWork() Rescheduling Disabled or Max reschedule attempts made. Exiting...");
			return;
		}
    	Common.startNotificationActivity(context, bundle);
	}
		
}