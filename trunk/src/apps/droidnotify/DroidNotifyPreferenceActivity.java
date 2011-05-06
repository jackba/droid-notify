package apps.droidnotify;

import java.sql.Date;
import java.util.HashSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
	
	final String APP_ENABLED_KEY = "app_enabled";
	final String NOTIFICATIONS_ENABLED_SETTINGS = "notifications_enabled_settings";
	final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	final String SMS_BUTTON_SETTINGS = "sms_button_settings";
	final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	final String MMS_BUTTON_SETTINGS = "mms_button_settings";
	final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	final String MISSED_CALL_BUTTON_SETTINGS = "missed_call_button_settings";
	final String SCREEN_ENABLED_KEY = "screen_enabled";
	final String SCREEN_DIM_ENABLED_KEY = "screen_dim_enabled";
	
	//================================================================================
    // Properties
    //================================================================================

	private Context _context;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * The preference Activity was created.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onCreate()");	
	    setContext(getApplicationContext());
	    addPreferencesFromResource(R.xml.preferences);
	    runOnceAlarmManager();
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onResume()");
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onPause()");
    }
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onDestroy()");
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences
	 * @param key
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * This function starts the main AlarmManager that will check the users calendar for events.
	 * This function should run only once when the application is installed.
	 */
	private void runOnceAlarmManager(){
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager()");
		Context context = getContext();
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//		boolean runOnce = preferences.getBoolean("runOnce", true);
//		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Run Once?" + runOnce);
//		if(runOnce) {
//			if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Alarm Code Running");
//			SharedPreferences.Editor editor = preferences.edit();
//			editor.putBoolean("runOnce", false);
//			editor.commit();
//			//Schedule event.
//			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//			Intent intent = new Intent(context, CalendarOnAlarmReceiver.class);
//			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, getStartTimeOfAlarm(), AlarmManager.INTERVAL_DAY, pendingIntent);
//       }
		
		//Just testing out the reading of the calendar.
		//readCalendars(context);
		
	}
	
//	/**
//	 * Calculates the start time of the alarm.
//	 * 
//	 * @return alarmStartTime
//	 */
//	private long getStartTimeOfAlarm(){
//		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.getStartTimeOfAlarm()");
//		long alarmStartTime = SystemClock.elapsedRealtime() + (2 * 60 * 1000); // 2 Minutes From Current Time
//		return alarmStartTime;
//	}
//	
//	public static void readCalendars(Context context) {
//		if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars()");
//		ContentResolver contentResolver = context.getContentResolver();
//		// Fetch a list of all calendars synced with the device, their display names and whether the
//		// user has them selected for display.
//		final Cursor cursor = contentResolver.query(
//				//Uri.parse("content://calendar/calendars"), //Android 2.1
//				Uri.parse("content://com.android.calendar/calendars"), //Android 2.2
//				new String[] { "_id", "displayName", "selected" },
//				null,
//				null,
//				null);
//		// For a full list of available columns see http://tinyurl.com/yfbg76w
//		HashSet<String> calendarIds = new HashSet<String>();
//		if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars() About To Read Calendar");
//		if(cursor != null){
//			while (cursor.moveToNext()) {
//				if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendar() Reading Calendars...");
//				final String _id = cursor.getString(0);
//				final String displayName = cursor.getString(1);
//				final Boolean selected = !cursor.getString(2).equals("0");
//				if (Log.getDebug()) Log.v("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
//				calendarIds.add(_id);
//			}	
//		}else{
//			if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars() ERROR - Cursor Is Null - EXITING!");
//			return;
//		}
//		if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendar() Calendar IDs Read");
//		// For each calendar, display all the events from the previous week to the end of next week.		
//		for (String id : calendarIds) {
//			//Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon(); //Android 2.1
//			Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon(); //Android 2.2
//			long now = System.currentTimeMillis();
//			ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
//			ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
//			Cursor eventCursor = contentResolver.query(builder.build(),
//					new String[] { "title", "begin", "end", "allDay"},
//					"Calendars._id=" + id,
//					null,
//					"startDay ASC, startMinute ASC"); 
//			// For a full list of available columns see http://tinyurl.com/yfbg76w
//			if(cursor != null){
//				while (eventCursor.moveToNext()) {
//					final String title = eventCursor.getString(0);
//					final Date begin = new Date(eventCursor.getLong(1));
//					final Date end = new Date(eventCursor.getLong(2));
//					final Boolean allDay = !eventCursor.getString(3).equals("0");
//					if (Log.getDebug()) Log.v("Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay);
//				}
//			}else{
//				if (Log.getDebug()) Log.v("CalendarReceiverService.readCalendars() ERROR - EventCursor Is Null - EXITING!");
//				return;
//			}
//		}
//	}
	
}