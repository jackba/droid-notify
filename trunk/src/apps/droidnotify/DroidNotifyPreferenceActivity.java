package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================

    private final String CALENDAR_REMINDER_KEY = "calendar_reminder_settings";
	
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean runOnce = preferences.getBoolean("runOnce", true);
		long reminderInterval = Long.parseLong(preferences.getString(CALENDAR_REMINDER_KEY, "15")) * 60 * 1000;
		//if(runOnce) {
			if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Alarm Code Running");
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("runOnce", false);
			editor.commit();
			//Schedule the reading of the calendar events.
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			// Set alarm to go off 5 minutes from the current time.
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (5 * 60 * 1000), AlarmManager.INTERVAL_DAY + reminderInterval, pendingIntent);
       //}
	}
	
}