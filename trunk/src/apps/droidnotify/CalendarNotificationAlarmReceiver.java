package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class CalendarNotificationAlarmReceiver extends BroadcastReceiver {

	//================================================================================
    // Constants
    //================================================================================
	
	private final long INTERVAL_ONE_MINUTE = (1 * 60 * 1000);
	
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
		if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiver.onReceive()");
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
			// Set alarm to go off 1 minute from the current time.
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_ONE_MINUTE, pendingIntent);
	    }
	}
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
}