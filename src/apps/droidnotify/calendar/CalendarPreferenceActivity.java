package apps.droidnotify.calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Calendar Notifications" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class CalendarPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
    private SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    this.addPreferencesFromResource(R.xml.calendar_preferences);
	    this.setContentView(R.layout.calendar_preferences);
	    setupCustomPreferences();
	    updateReminderSettings();
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, false)){
				//Setup Calendar recurring alarm.
				startCalendarAlarmManager(SystemClock.currentThreadTimeMillis() + (60 * 1000));
			}else{
				//Cancel the Calendar recurring alarm.
				CalendarCommon.cancelCalendarAlarmManager(_context);
			}
		}else if(key.equals(Constants.CALENDAR_POLLING_FREQUENCY_KEY)){
			//The polling time for the calendars was changed. Run the alarm manager with the updated polling time.
			startCalendarAlarmManager(SystemClock.currentThreadTimeMillis() + (10 * 1000));
		}else if(key.equals(Constants.CALENDAR_USE_CALENDAR_REMINDER_SETTINGS_KEY)){
			updateReminderSettings();
		}
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Calendar Refresh Button
		Preference calendarRefreshPref = (Preference)findPreference(Constants.CALENDAR_REFRESH_KEY);
		calendarRefreshPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new calendarRefreshAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e(_context, "CalendarPreferenceActivity() Calendar Refresh Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Status Bar Notification Settings Preference/Button
		Preference statusBarNotificationSettingsPref = (Preference)findPreference(Constants.SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
		statusBarNotificationSettingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, CalendarStatusBarNotificationsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "CalendarPreferenceActivity() Status Bar Notifications Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Preference/Button
		Preference customizePref = (Preference)findPreference(Constants.SETTINGS_CUSTOMIZE_PREFERENCE);
		customizePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, CalendarCustomizePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "CalendarPreferenceActivity() Customize Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
	/**
	 * Start the Calendar Alarm Manager.
	 * 
	 * @param alarmStartTime - The time to start the alarm.
	 */
	private void startCalendarAlarmManager(long alarmStartTime){
		//Make sure that this user preference has been set and initialized.
		initUserCalendarsPreference();
		//Schedule the reading of the calendar events.
		CalendarCommon.startCalendarAlarmManager(_context, alarmStartTime);
	}
	
	/**
	 * Initializes the calendars which will be checked for event notifications.
	 * This sets the user preference to check all available calendars.
	 */
	private void initUserCalendarsPreference(){
    	String availableCalendarsInfo = CalendarCommon.getAvailableCalendars(_context);
    	if(availableCalendarsInfo == null){
    		return;
    	}
    	//Only initialize the calendars if the user preference doesn't exist yet.
    	if(_preferences.getString(Constants.CALENDAR_SELECTION_KEY, null) == null){
	    	String[] calendarsInfo = availableCalendarsInfo.split(",");
	    	StringBuilder calendarSelectionPreference = new StringBuilder();
	    	for(String calendarInfo : calendarsInfo){
	    		String[] calendarInfoArray = calendarInfo.split("\\|");
	    		if(!calendarSelectionPreference.toString().equals("")) calendarSelectionPreference.append("|");
	    		calendarSelectionPreference.append(calendarInfoArray[0]);
	    	}
	    	SharedPreferences.Editor editor = _preferences.edit();
	    	editor.putString(Constants.CALENDAR_SELECTION_KEY, calendarSelectionPreference.toString());
	    	editor.commit();
    	}
	}

	/**
	 * Refresh the Calendar event alarms as a background task.
	 * 
	 * @author Camille Sévigny
	 */
	private class calendarRefreshAsyncTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
	        dialog = ProgressDialog.show(_context, "", _context.getString(R.string.reading_calendar_data), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params) {
			CalendarCommon.readCalendars(_context);
	    	return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res) {
	        dialog.dismiss();
	    	Toast.makeText(_context, _context.getString(R.string.calendar_data_refreshed), Toast.LENGTH_LONG).show();
	    }
	}
	
	/**
	 * Update the reminder settings based on their current settings.
	 */
	@SuppressWarnings("deprecation")
	private void updateReminderSettings(){
		ListPreference eventReminderListPref = (ListPreference)findPreference(Constants.CALENDAR_REMINDER_KEY);
		ListPreference allDayEventReminderListPref = (ListPreference)findPreference(Constants.CALENDAR_REMINDER_ALL_DAY_KEY);
		if(_preferences.getBoolean(Constants.CALENDAR_USE_CALENDAR_REMINDER_SETTINGS_KEY, true)){
			if(eventReminderListPref != null) eventReminderListPref.setEnabled(false);
			if(allDayEventReminderListPref != null) allDayEventReminderListPref.setEnabled(false);
		}else{
			if(eventReminderListPref != null) eventReminderListPref.setEnabled(true);
			if(allDayEventReminderListPref != null) allDayEventReminderListPref.setEnabled(true);
		}	
	}
	
}