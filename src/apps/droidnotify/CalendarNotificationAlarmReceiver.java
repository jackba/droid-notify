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
 * This class listens for scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
    
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	private final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	
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
	 * Receives a notification that the Calendar should be checked.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiver.onReceive()");
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
	    //Check the state of the users phone.
		TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    if (Log.getDebug()) Log.v("PhoneReceiver.onReceive() Current Call State: " + telemanager.getCallState());
	    if (callStateIdle) {
			WakefulIntentService.acquireStaticLock(context);
			Intent calendarIntent = new Intent(context, CalendarNotificationAlarmReceiverService.class);
			calendarIntent.putExtras(intent.getExtras());
			context.startService(calendarIntent);
	    }else{
	    	if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiver.onReceive() Phone Call In Progress. Rescheduling notification.");
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent calendarIntent = new Intent(context, CalendarNotificationAlarmReceiver.class);
			calendarIntent.putExtras(intent.getExtras());
			calendarIntent.setAction("apps.droidnotify.VIEW/CalendarReschedule/" + System.currentTimeMillis());
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, calendarIntent, 0);
			// Set alarm to go off x minutes from the current timeas defined by the user preferences.
			long rescheduleInterval = Long.parseLong(preferences.getString(RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, pendingIntent);
	    }
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}