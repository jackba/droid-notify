package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class handles scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class CalendarNotificationAlarmReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public CalendarNotificationAlarmReceiverService() {
		super("CalendarNotificationAlarmReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("CalendarNotificationAlarmReceiverService.CalendarNotificationAlarmReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Display the notification for this Calendar Event.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		if (_debug) Log.v("CalendarNotificationAlarmReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
	    	Intent calendarIntent = new Intent(context, NotificationActivity.class);
	    	Bundle bundle = intent.getExtras();
	    	calendarIntent.putExtras(bundle);
	    	calendarIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquireWakeLock(context);
	    	context.startActivity(calendarIntent);
		}catch(Exception ex){
			if (_debug) Log.e("CalendarNotificationAlarmReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}