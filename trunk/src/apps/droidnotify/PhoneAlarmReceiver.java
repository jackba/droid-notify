package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PhoneAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	
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
	 * 
	 * 
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("PhoneAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("PhoneAlarmReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if missed call notifications are disabled.
	    if(!preferences.getBoolean(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("PhoneAlarmReceiver.onReceive() Missed Call Notifications Disabled. Exiting... ");
			return;
		}
		WakefulIntentService.acquireStaticLock(context);
		context.startService(new Intent(context, PhoneAlarmReceiverService.class));
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}