package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class RescheduleReceiverService extends WakefulIntentService {
	
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
	public RescheduleReceiverService() {
		super("RescheduleReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("RescheduleReceiverService.RescheduleReceiverService()");
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
		if (_debug) Log.v("RescheduleReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Bundle bundle = intent.getExtras();
		int maxRescheduleAttempts = Integer.parseInt(preferences.getString(Constants.REMINDER_FREQUENCY_KEY, Constants.REMINDER_FREQUENCY_DEFAULT));
		int rescheduleNumber = bundle.getInt("rescheduleNumber");
		//Determine if the notification should be rescheduled or not.
		boolean displayNotification = true;
		if(maxRescheduleAttempts < 0){
			//Infinite Attempts.
			displayNotification = true;
		}else if(rescheduleNumber > maxRescheduleAttempts){
			displayNotification = false;
		}
		if(displayNotification){
	    	Intent rescheduleNotificationIntent = new Intent(context, NotificationActivity.class);
	    	rescheduleNotificationIntent.putExtras(bundle);
	    	rescheduleNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquirePartialWakeLock(context);
	    	context.startActivity(rescheduleNotificationIntent);
		}
	}
		
}