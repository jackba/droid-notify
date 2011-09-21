package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class listens for incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSReceiver extends BroadcastReceiver{
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
			if (_debug) Log.v("MMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if MMS notifications are disabled.
	    if(!preferences.getBoolean(Constants.MMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("MMSReceiver.onReceive() MMS Notifications Disabled. Exiting...");
			return;
		}
		//Schedule mms task x seconds after the broadcast.
		//This time is set by the users advanced preferences. 40 seconds is the default value.
		//This should allow enough time to pass for the mms inbox to be written to.
		long timeoutInterval = Long.parseLong(preferences.getString(Constants.MMS_TIMEOUT_KEY, "40")) * 1000;
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent mmsIntent = new Intent(context, MMSAlarmReceiver.class);
		PendingIntent mmsPendingIntent = PendingIntent.getBroadcast(context, 0, mmsIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeoutInterval, mmsPendingIntent);	
	}

}