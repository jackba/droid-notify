package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import apps.droidnotify.log.Log;

/**
 * This class listens for scheduled MMS notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class MMSAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;

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
	public void onReceive(Context context, Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("MMSAlarmReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (_debug) Log.v("MMSAlarmReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if MMS notifications are disabled.
	    if(!preferences.getBoolean(MMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("MMSAlarmReceiver.onReceive() MMS Notifications Disabled. Exiting...");
			return;
		}
	    //Check the state of the users phone.
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (callStateIdle) {
			WakefulIntentService.acquireStaticLock(context);
			context.startService(new Intent(context, MMSReceiverService.class));
	    }else{
	    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
	    	int rescheduleInterval = Integer.parseInt(preferences.getString(RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
	    	if(rescheduleInterval > 0){
		    	if (_debug) Log.v("SMSReceiver.onReceive() Phone Call In Progress. Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent phoneIntent = new Intent(context, MMSAlarmReceiver.class);
				phoneIntent.setAction("apps.droidnotify.VIEW/MMSReschedule/" + System.currentTimeMillis());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, phoneIntent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, pendingIntent);
	    	}
	    }
	}

}