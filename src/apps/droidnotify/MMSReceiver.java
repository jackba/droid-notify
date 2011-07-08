package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import apps.droidnotify.log.Log;

/**
 * This class listens for incoming MMS messages.
 * 
 * @author Camille Sévigny
 */
public class MMSReceiver extends BroadcastReceiver{

	//================================================================================
    // Constants
    //================================================================================
	
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	private static final String MMS_TIMEOUT_KEY = "mms_timeout_settings";

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming MMS message. The MMS message is located within the Intent object.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		if (Log.getDebug()) Log.v("MMSReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("MMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if MMS notifications are disabled.
	    if(!preferences.getBoolean(MMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("MMSReceiver.onReceive() MMS Notifications Disabled. Exiting...");
			return;
		}
		//Schedule mms task x seconds after the broadcast.
		//This time is set by the users advanced preferences. 5 seconds is the default value.
		//This should allow enough time to pass for the phone log to be written to.
		long timeoutInterval = Long.parseLong(preferences.getString(MMS_TIMEOUT_KEY, "40")) * 1000;
		//Schedule the phone service.
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent newIntent = new Intent(context, MMSAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeoutInterval, pendingIntent);		
	}

}