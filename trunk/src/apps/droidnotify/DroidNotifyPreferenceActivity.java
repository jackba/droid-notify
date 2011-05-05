package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
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
	 *
	 */
	private void runOnceAlarmManager(){
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean runOnce = preferences.getBoolean("runOnce", true);
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() RunOnce?" + runOnce);
		if(runOnce) {
			if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Alarm Code Running");
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("runOnce", false);
			editor.commit();
			//Schedule event.
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
	    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 30 * 1000, pendingIntent);
       }
	
	}
	
}