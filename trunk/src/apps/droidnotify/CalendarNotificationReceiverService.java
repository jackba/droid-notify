package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

/**
 * 
* @author Camille Sevigny
 *
 */
public class CalendarNotificationReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
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
	public CalendarNotificationReceiverService() {
		super("CalendarNotificationReceiverService");
		if (Log.getDebug()) Log.v("CalendarNotificationReceiverService.CalendarNotificationReceiverService()");
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
		if (Log.getDebug()) Log.v("CalendarNotificationReceiverService.doWakefulWork()");
		Context context = getApplicationContext();
    	Intent calendarNotificationIntent = new Intent(context, NotificationActivity.class);
    	Bundle calendarNotificationBundle = intent.getExtras();
    	calendarNotificationIntent.putExtras(calendarNotificationBundle);
    	calendarNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    	context.startActivity(calendarNotificationIntent);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
}
