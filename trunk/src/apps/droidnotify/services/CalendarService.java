package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;

import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class handles scheduled Calendar Event notifications that we want to display.
 * 
 * @author Camille Sévigny
 */
public class CalendarService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public CalendarService() {
		super("CalendarService");
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
		Context context = getApplicationContext();
		try{
	    	Common.startNotificationActivity(getApplicationContext(), intent.getExtras());
		}catch(Exception ex){
			Log.e(context, "CalendarService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}