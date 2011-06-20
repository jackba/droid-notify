package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

/**
 * This class listens for the phone state to change.
 * 
 * @author Camille Sévigny
 */
public class PhoneReceiver extends BroadcastReceiver{

	//================================================================================
    // Constants
    //================================================================================
    
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
    private static final String CALL_LOG_TIMEOUT_KEY = "calendar_reminder_settings";
	
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
	 * Receives a notification that the phone state changed.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		if (Log.getDebug()) Log.v("PhoneReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("PhoneReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if missed call notifications are disabled.
	    if(!preferences.getBoolean(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("PhoneReceiver.onReceive() Missed Call Notifications Disabled. Exiting... ");
			return;
		}
	    //Check the state of the users phone.
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if(callStateIdle){
			//Schedule phone task x seconds after the broadcast.
			//This time is set by the users advanced preferences. 5 seconds is the default value.
			//This should allow enough time to pass for the phone log to be written to.
			long timeoutInterval = Long.parseLong(preferences.getString(CALL_LOG_TIMEOUT_KEY, "5")) * 1000;
			//Schedule the phone service.
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent newIntent = new Intent(context, PhoneAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeoutInterval, pendingIntent);		
	    }else{
	    	if (Log.getDebug()) Log.v("PhoneReceiver.onReceive() Phone Call In Progress. Exiting...");
	    }
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}
