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
 * This class listens for the OnBoot event from the users phone. Then it schedules the users calendars to be checked.
 * 
 * @author Camille Sévigny
 */
public class OnBootReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================

	private static final String CALENDAR_POLLING_FREQUENCY_KEY = "calendar_polling_frequency";

	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
    
	/**
	 * Receives a notification that the phone was restarted.
	 * This function starts the service that will handle the work to setup calendar event notifications.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("OnBootReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
			if (_debug) Log.v("OnBootReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("OnBootReceiver.onReceive() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent newIntent = new Intent(context, CalendarAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
		// Set alarm to go off 5 minutes from the current time.
		long pollingFrequency = Long.parseLong(preferences.getString(CALENDAR_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 60 * 1000), pollingFrequency, pendingIntent);
	}

}