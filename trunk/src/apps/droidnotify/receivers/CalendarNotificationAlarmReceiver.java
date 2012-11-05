package apps.droidnotify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.services.CalendarNotificationAlarmBroadcastReceiverService;
import apps.droidnotify.services.WakefulIntentService;
import apps.droidnotify.common.Constants;

/**
 * This class listens for scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification that the Calendar should be checked.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug(context);
		if (_debug) Log.v(context, "CalendarNotificationAlarmReceiver.onReceive()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
			if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "CalendarNotificationAlarmReceiver.onReceive() App Disabled. Exiting...");
				return;
			}
			//Read preferences and exit if calendar notifications are disabled.
		    if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v(context, "CalendarNotificationAlarmReceiver.onReceive() Calendar Notifications Disabled. Exiting... ");
				return;
			}
		    Intent calendarNotificationAlarmBroadcastReceiverServiceIntent = new Intent(context, CalendarNotificationAlarmBroadcastReceiverService.class);
		    calendarNotificationAlarmBroadcastReceiverServiceIntent.putExtras(intent.getExtras());
			WakefulIntentService.sendWakefulWork(context, calendarNotificationAlarmBroadcastReceiverServiceIntent);
		}catch(Exception ex){
			Log.e(context, "CalendarNotificationAlarmReceiver.onReceive() ERROR: " + ex.toString());
		}
	}
	
}