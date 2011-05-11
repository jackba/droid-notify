package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
* @author Camille Sevigny
 *
 */
public class CalendarNotificationAlarmReceiverService extends WakefulIntentService {
	
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
	 */
	public CalendarNotificationAlarmReceiverService() {
		super("CalendarNotificationAlarmReceiverService");
		if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiverService.CalendarNotificationAlarmReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Calendar Event.
	 * 
	 * @param intent
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (Log.getDebug()) Log.v("CalendarNotificationAlarmReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
    	Intent calendarNotificationIntent = new Intent(context, NotificationActivity.class);
    	Bundle bundle = intent.getExtras();
    	calendarNotificationIntent.putExtras(bundle);
    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(calendarNotificationIntent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
