package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.droidnotify.log.Log;
import apps.droidnotify.reminder.ReminderCommon;
import apps.droidnotify.db.SQLiteHelperBlockingApps;
import apps.droidnotify.db.SQLiteHelperReminder;
import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Constants;

public class OnUpdateService extends WakefulIntentService {
	
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
	public OnUpdateService() {
		super("OnUpdateService.OnUpdateService()");
		_debug = Log.getDebug();
		if (_debug) Log.v("OnUpdateService.OnUpdateService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent){
		if (_debug) Log.v("OnUpdateService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(!preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, false)){
				//Cancel the Calendar recurring alarm.
				CalendarCommon.cancelCalendarAlarmManager(context);
			}
			//Create the reminder database.
			SQLiteHelperReminder reminderDBHelper = new SQLiteHelperReminder(context);
        	reminderDBHelper.getReadableDatabase();
        	reminderDBHelper.close();
			//Create the blocking apps database.
			SQLiteHelperBlockingApps blockingAppsDBHelper = new SQLiteHelperBlockingApps(context);
			blockingAppsDBHelper.getReadableDatabase();
			blockingAppsDBHelper.close();
			//Start Reminder DB Cleanup Alarms
			ReminderCommon.startReminderDBManagementAlarmManager(context, System.currentTimeMillis() + (5 * 60 * 1000));
		}catch(Exception ex){
			Log.e("OnUpdateService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
	
}