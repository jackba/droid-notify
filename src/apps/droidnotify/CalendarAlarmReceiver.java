package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 
 * 
 * @author Camille Sevigny
 */
public class CalendarAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	
	//================================================================================
    // Properties
    //================================================================================

	//================================================================================
	// Constructors
	//================================================================================
		
	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives a notification of a Calendar Event.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("CalendarAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("SMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!preferences.getBoolean(CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		WakefulIntentService.acquireStaticLock(context);
		context.startService(new Intent(context, CalendarAlarmReceiverService.class));
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}