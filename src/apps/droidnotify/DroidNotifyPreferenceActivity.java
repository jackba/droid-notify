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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
    
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	private final int NOTIFICATION_TYPE_TEST = -1;
	
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
	 * @param context - Application Context.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context - Application Context.
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
	 * @param savedInstanceState - Information about the current state of the PreferenceActivity.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onCreate()");
	    setContext(DroidNotifyPreferenceActivity.this);
	    addPreferencesFromResource(R.xml.preferences);
	    setContentView(R.xml.preferenceswrapper);
	    runOnceAlarmManager();
	    setupTestnotificationsButton();
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
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set up the "Test Notifications" button. 
	 * When clicked, this button will display some fake notifications to the user using their current preference options.
	 */
	private void setupTestnotificationsButton(){
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.setupTestnotificationsButton()");
		final Button testNotificationsButton = (Button) findViewById(R.id.test_notifications_button);
		testNotificationsButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (Log.getDebug()) Log.v("Test Notifications Button Clicked()");
		    	Context context = getContext();
		    	Bundle bundle = new Bundle();
				bundle.putInt("notificationType", NOTIFICATION_TYPE_TEST);
		    	Intent testIntent = new Intent(context, NotificationActivity.class);
		    	testIntent.putExtras(bundle);
		    	testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	DroidNotifyPreferenceActivity.this.startActivity(testIntent);
		    }
		});
	}
	
	/**
	 * Starts the main AlarmManager that will check the users calendar for events.
	 * This should run only once when the application is installed.
	 */
	private void runOnceAlarmManager(){
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//Read preferences and exit if app is disabled.
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("SMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!preferences.getBoolean(CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (Log.getDebug()) Log.v("NotificationActivity.onCreate() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		boolean runOnce = preferences.getBoolean("runOnce", true);
		if(runOnce) {
			if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Alarm Code Running");
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("runOnce", false);
			editor.commit();
			//Schedule the reading of the calendar events.
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			// Set alarm to go off 5 minutes from the current time.
			//This line of code is for testing.
			//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (30 * 1000), AlarmManager.INTERVAL_DAY + reminderInterval, pendingIntent);
			//--------------------------------
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (5 * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
       }
	}
	
}