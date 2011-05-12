package apps.droidnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SMSAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	
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
		if (Log.getDebug()) Log.v("SMSAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("SMSAlarmReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if SMS notifications are disabled.
	    if(!preferences.getBoolean(SMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("SMSAlarmReceiver.onReceive() SMS Notifications Disabled. Exiting...");
			return;
		}
	    //Check the state of the users phone.
		WakefulIntentService.acquireStaticLock(context);
		Intent smsIntent = new Intent(context, SMSReceiverService.class);
		smsIntent.putExtras(intent.getExtras());
		context.startService(smsIntent);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
}