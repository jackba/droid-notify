package apps.droidnotify.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import apps.droidnotify.CalendarAlarmReceiver;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class MainPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
	
	//Google Market URL
	private static final String RATE_APP_ANDROID_URL = "http://market.android.com/details?id=apps.droidnotify";
	//Amazon Appstore URL
	private static final String RATE_APP_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify";
	
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	private static final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";
    private static final String CALENDAR_SELECTION_KEY = "calendar_selection";
	
	private static final int NOTIFICATION_TYPE_TEST = -1;
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
    private boolean _debugCalendar = false;
    private SharedPreferences _preferences = null;

	//================================================================================
	// Constructors
	//================================================================================
	
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
	    _debug = Log.getDebug();
	    if (_debug) Log.v("MainPreferenceActivity.onCreate()");
	    _debugCalendar = Log.getDebugCalendar();
	    _context = MainPreferenceActivity.this;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    addPreferencesFromResource(R.xml.preferences);
	    setupCustomPreferences();
	    runOnceAlarmManager();
	    setupAppDebugMode(_debug);
	    setupRateAppPreference();
	    setupImportPreferences();
	    runOnceEula();
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
	    _debug = Log.getDebug();
	    if (_debug) Log.v("MainPreferenceActivity.onResume()");
	    setupImportPreferences();
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (_debug) Log.v("MainPreferenceActivity.onPause()");
    }
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("MainPreferenceActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("MainPreferenceActivity.onDestroy()");
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("MainPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Starts the main AlarmManager that will check the users calendar for events.
	 * This should run only once when the application is installed.
	 */
	private void runOnceAlarmManager(){
		if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager()");
		//Read preferences and exit if app is disabled.
	    if(!_preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!_preferences.getBoolean(CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		boolean runOnce = _preferences.getBoolean("runOnce_v_2_0", true);
		if(runOnce || _debugCalendar) {
			SharedPreferences.Editor editor = _preferences.edit();
			editor.putBoolean("runOnce_v_2_0", false);
			editor.commit();
			//Schedule the reading of the calendar events.
			AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(_context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
			if(_debugCalendar){
				//--------------------------------
				//Set alarm to go off 30 seconds from the current time.
				//This line of code is for testing.
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (30 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
				//--------------------------------
			}else{
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (5 * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
			}
			initUserCalendarsPreference();
       }
	}
	
	/**
	 * This displays the EULA to the user the first time the app is run.
	 */
	private void runOnceEula(){
		if (_debug) Log.v("MainPreferenceActivity.runOnceEula()");
		boolean runOnceEula = _preferences.getBoolean("runOnceEula", true);
		if(runOnceEula) {
			try{
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean("runOnceEula", false);
				editor.commit();
				displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info,_context.getString(R.string.eula_text));
			}catch(Exception ex){
 	    		if (_debug) Log.e("MainPreferenceActivity.runOnceEula() ERROR: " + ex.toString());
	    	}
		}
	}
	
	/**
	 * Setup the custom Preference buttons.
	 */
	private void setupCustomPreferences(){
		if (_debug) Log.v("MainPreferenceActivity.setupCustomPreferences()");
		//Test Notifications Preference/Button
		Preference testAppPref = (Preference)findPreference("test_app");
		testAppPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Test Notifications Button Clicked()");
		    	Bundle bundle = new Bundle();
				bundle.putInt("notificationType", NOTIFICATION_TYPE_TEST);
		    	Intent intent = new Intent(_context, NotificationActivity.class);
		    	intent.putExtras(bundle);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Test Notifications Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_test_app_error), Toast.LENGTH_SHORT).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Rate This App Preference/Button
		Preference rateAppPref = (Preference)findPreference("rate_app");
		rateAppPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Rate This App Button Clicked()");
		    	try{
			    	String rateAppURL = "";
			    	if(Log.getShowAndroidRateAppLink()) rateAppURL = RATE_APP_ANDROID_URL;
			    	if(Log.getShowAmazonRateAppLink()) rateAppURL = RATE_APP_AMAZON_URL;
			    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rateAppURL));			    	
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Rate This App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_SHORT).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Email Developer Preference/Button
		Preference emailDeveloperPref = (Preference)findPreference("email_developer");
		emailDeveloperPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Email Developer Button Clicked()");
		    	try{
			    	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	intent.putExtra("subject", "Droid Notify App Feedback");
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Email Developer Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_SHORT).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//About Preference/Button
		Preference aboutPreferencesPref = (Preference)findPreference("application_about");
		aboutPreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("About Button Clicked()");
		    	return displayHTMLAlertDialog(_context.getString(R.string.app_name_formatted_version, _context.getString(R.string.app_version)),R.drawable.ic_launcher_droidnotify,_context.getString(R.string.preference_about_text));
           }
		});
		//License Preference/Button
		Preference licensePreferencesPref = (Preference)findPreference("application_license");
		licensePreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("License Button Clicked()");
	            return displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info,_context.getString(R.string.eula_text));
           }
		});
		//Email Developer Logs Preference/Button
		Preference emailDeveloperLogsPref = (Preference)findPreference("email_logs");
		emailDeveloperLogsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Email Developer Logs Button Clicked()");
		    	try{
			    	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	intent.putExtra("subject", "Droid Notify App Logs");
			    	intent.putExtra("body", "What went wrong? What is the reason for emailing the log files: ");
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					File logFilePathV = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/V");
					File logFileV = new File(logFilePathV, "DroidNotifyLog.txt");
					File logFilePathD = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/D");
					File logFileD = new File(logFilePathD, "DroidNotifyLog.txt");
					File logFilePathI = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/I");
					File logFileI = new File(logFilePathI, "DroidNotifyLog.txt");
					File logFilePathW = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/W");
					File logFileW = new File(logFilePathW, "DroidNotifyLog.txt");
					File logFilePathE = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/E");
					File logFileE = new File(logFilePathE, "DroidNotifyLog.txt");
			    	if(logFileV.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt"));
			    	if(logFileD.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt"));
			    	if(logFileI.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt"));
			    	if(logFileW.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt"));
			    	if(logFileE.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt"));
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Email Developer Logs Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_SHORT).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Clear Developer Logs Preference/Button
		Preference clearDeveloperLogsPref = (Preference)findPreference("clear_logs");
		clearDeveloperLogsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Clear Developer Logs Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new clearDeveloperLogAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Clear Developer Logs Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Export Preferences Preference/Button
		Preference exportPreferencesPref = (Preference)findPreference("export_preferences");
		exportPreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Export Preferences Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new exportPreferencesAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Export Preferences Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Import Preferences Preference/Button
		Preference importPreferencesPref = (Preference)findPreference("import_preferences");
		importPreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Import Preferences Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new importPreferencesAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Import Preferences Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
	}
	
	/**
	 * Sets up some options if the app is in debug mode.
	 * Hides or shows some tools on the preference screen to assist the developer debug the app.
	 * 
	 * @param inDebugMode
	 */
	private void setupAppDebugMode(boolean inDebugMode){
		if (_debug) Log.v("MainPreferenceActivity.setupAppDebugMode()");
		if(!inDebugMode){
			PreferenceScreen mainPreferences = this.getPreferenceScreen();
			PreferenceCategory debugPreferenceCategory = (PreferenceCategory) findPreference("app_debug_category");
			mainPreferences.removePreference(debugPreferenceCategory);
		}
	}
	
	/**
	 * Removes the "Rate App" link from the application if not in the Android or Amazon stores.
	 */
	private void setupRateAppPreference(){
		if (_debug) Log.v("MainPreferenceActivity.setupRateAppPreference()");
		boolean showRateAppCategory = false;
		if(Log.getShowAndroidRateAppLink()) showRateAppCategory = true;
		if(Log.getShowAmazonRateAppLink()) showRateAppCategory = true;
		if(!showRateAppCategory){
			PreferenceScreen mainPreferences = this.getPreferenceScreen();
			PreferenceCategory rateAppCategory = (PreferenceCategory) findPreference("rate_app_category");
			mainPreferences.removePreference(rateAppCategory);
		}
	}
	
	/**
	 * Clear the developer logs as a background task.
	 * 
	 * @author Camille Sévigny
	 */
	private class clearDeveloperLogAsyncTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("MainPreferenceActivity.clearDeveloperLogAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", "Clearing Application Logs...", true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.clearDeveloperLogAsyncTask.doInBackground()");
	    	clearDeveloperLogs();
	    	return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res) {
			if (_debug) Log.v("MainPreferenceActivity.clearDeveloperLogAsyncTask.onPostExecute()");
	        dialog.dismiss();
	    	Toast.makeText(_context, "The application logs have been cleared.", Toast.LENGTH_LONG).show();
	    }
	}
	
	/**
	 * Clear the developer logs on the SD card.
	 */
	private void clearDeveloperLogs(){
		if (_debug) Log.v("MainPreferenceActivity.clearDeveloperLogs()");
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    //We can read and write the media. Do nothing.
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media.
			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() External Storage Read Only State");
		    return;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() External Storage Can't Write Or Read State");
		    return;
		}
		try{
			File logFilePathV = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/V");
			File logFileV = new File(logFilePathV, "DroidNotifyLog.txt");
			File logFilePathD = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/D");
			File logFileD = new File(logFilePathD, "DroidNotifyLog.txt");
			File logFilePathI = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/I");
			File logFileI = new File(logFilePathI, "DroidNotifyLog.txt");
			File logFilePathW = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/W");
			File logFileW = new File(logFilePathW, "DroidNotifyLog.txt");
			File logFilePathE = Environment.getExternalStoragePublicDirectory("Droid Notify/Logs/E");
			File logFileE = new File(logFilePathE, "DroidNotifyLog.txt");
	    	if(logFileV.exists()){
	    		try{
	    			logFileV.delete();
	    		}catch (Exception ex){
	    			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() LogFileV ERROR: " + ex.toString());
				}
	    	}
	    	if(logFileD.exists()){
	    		try{
	    			logFileD.delete();
	    		}catch (Exception ex){
	    			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() LogFileD ERROR: " + ex.toString());
				}
	    	}
	    	if(logFileI.exists()){
	    		try{
	    			logFileI.delete();
	    		}catch (Exception ex){
	    			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() LogFileI ERROR: " + ex.toString());
				}
	    	}
	    	if(logFileW.exists()){
	    		try{
	    			logFileW.delete();
	    		}catch (Exception ex){
	    			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() LogFileW ERROR: " + ex.toString());
				}
	    	}
	    	if(logFileE.exists()){
	    		try{
	    			logFileE.delete();
	    		}catch (Exception ex){
	    			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() LogFileE ERROR: " + ex.toString());
				}
	    	}
    	}catch (Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Export application preferences.
	 * 
	 * @author Camille Sévigny
	 */
	private class exportPreferencesAsyncTask extends AsyncTask<Void, Void, Boolean> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("MainPreferenceActivity.exportPreferencesAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.preference_export_preferences_progress_text), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Boolean doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.exportPreferencesAsyncTask.doInBackground()");
	    	return exportApplicationPreferences();
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Boolean successful) {
			if (_debug) Log.v("MainPreferenceActivity.exportPreferencesAsyncTask.onPostExecute()");
	        dialog.dismiss();
	        if(successful){
	    		setupImportPreferences();
	        	Toast.makeText(_context, _context.getString(R.string.preference_export_preferences_finish_text), Toast.LENGTH_LONG).show();
	        }else{
	        	Toast.makeText(_context, _context.getString(R.string.preference_export_preferences_error_text), Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	/**
	 * Export the application preferences to the SD card.
	 * 
	 * @return boolean - True if the operation was successful, false otherwise.
	 */
	private boolean exportApplicationPreferences(){
		if (_debug) Log.v("MainPreferenceActivity.exportApplicationPreferences()");
		//Check state of external storage.
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    //We can read and write the media. Do nothing.
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media.
			if (_debug) Log.e("MainPreferenceActivity.exportApplicationPreferences() External Storage Read Only State");
		    return false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			if (_debug) Log.e("MainPreferenceActivity.exportApplicationPreferences() External Storage Can't Write Or Read State");
		    return false;
		}
    	File preferencesFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Preferences");
    	File preferencesFile = new File(preferencesFilePath, "DroidNotifyPreferences.txt");
    	try{
    		preferencesFilePath.mkdirs();
    		//Delete previous file if it exists.
    		if(preferencesFile.exists()){
    			preferencesFile.delete();   			
    		}
    		preferencesFile.createNewFile();
    		
			BufferedWriter buf = new BufferedWriter(new FileWriter(preferencesFile, true)); 
			
			//Write each preference to the text file.
			
			//General Settings
			buf.append("app_enabled|" + _preferences.getBoolean("app_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("haptic_feedback_enabled|" + _preferences.getBoolean("haptic_feedback_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("app_vibrations_enabled|" + _preferences.getBoolean("app_vibrations_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("app_ringtones_enabled|" + _preferences.getBoolean("app_ringtones_enabled", false) + "|boolean");
			buf.newLine();

			//Basic Settings
			buf.append("app_theme|" + _preferences.getString("app_theme", "android") + "|string");
			buf.newLine();
			buf.append("phone_number_format_settings|" + _preferences.getString("phone_number_format_settings", "1") + "|string");
			buf.newLine();
			buf.append("contact_placeholder|" + _preferences.getString("contact_placeholder", "0") + "|string");
			buf.newLine();
			buf.append("button_icons_enabled|" + _preferences.getBoolean("button_icons_enabled", true) + "|boolean");
			buf.newLine();		
			
			//Notification Settings
			buf.append("sms_notifications_enabled|" + _preferences.getBoolean("sms_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_display_unread_enabled|" + _preferences.getBoolean("sms_display_unread_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_message_body_enabled|" + _preferences.getBoolean("sms_hide_message_body_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("confirm_sms_deletion_enabled|" + _preferences.getBoolean("confirm_sms_deletion_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_dismiss_button_action|" + _preferences.getString("sms_dismiss_button_action", "0") + "|string");
			buf.newLine();
			buf.append("sms_delete_button_action|" + _preferences.getString("sms_delete_button_action", "0") + "|string");
			buf.newLine();
			buf.append("sms_reply_button_action|" + _preferences.getString("sms_reply_button_action", "0") + "|string");
			buf.newLine();
			buf.append("sms_notification_count_action|" + _preferences.getString("sms_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("sms_hide_dismiss_button_enabled|" + _preferences.getBoolean("sms_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_delete_button_enabled|" + _preferences.getBoolean("sms_hide_delete_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_reply_button_enabled|" + _preferences.getBoolean("sms_hide_reply_button_enabled", false) + "|boolean");
			buf.newLine();	
			buf.append("sms_vibrate_enabled|" + _preferences.getBoolean("sms_vibrate_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_ringtone_enabled|" + _preferences.getBoolean("sms_ringtone_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_ringtone_audio|" + _preferences.getString("sms_ringtone_audio", "DEFAULT_SOUND") + "|string");
			buf.newLine();
			
			buf.append("mms_notifications_enabled|" + _preferences.getBoolean("mms_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_display_unread_enabled|" + _preferences.getBoolean("mms_display_unread_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_message_body_enabled|" + _preferences.getBoolean("mms_hide_message_body_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("confirm_mms_deletion_enabled|" + _preferences.getBoolean("confirm_mms_deletion_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_dismiss_button_action|" + _preferences.getString("mms_dismiss_button_action", "0") + "|string");
			buf.newLine();
			buf.append("mms_delete_button_action|" + _preferences.getString("mms_delete_button_action", "0") + "|string");
			buf.newLine();
			buf.append("mms_reply_button_action|" + _preferences.getString("mms_reply_button_action", "0") + "|string");
			buf.newLine();
			buf.append("mms_notification_count_action|" + _preferences.getString("mms_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("mms_hide_dismiss_button_enabled|" + _preferences.getBoolean("mms_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_delete_button_enabled|" + _preferences.getBoolean("mms_hide_delete_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_reply_button_enabled|" + _preferences.getBoolean("mms_hide_reply_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_vibrate_enabled|" + _preferences.getBoolean("mms_vibrate_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_ringtone_enabled|" + _preferences.getBoolean("mms_ringtone_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_ringtone_audio|" + _preferences.getString("mms_ringtone_audio", "DEFAULT_SOUND") + "|string");
			buf.newLine();
			
			buf.append("missed_call_notifications_enabled|" + _preferences.getBoolean("missed_call_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("missed_call_dismiss_button_action|" + _preferences.getString("missed_call_dismiss_button_action", "0") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_count_action|" + _preferences.getString("missed_call_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("missed_call_hide_dismiss_button_enabled|" + _preferences.getBoolean("missed_call_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_hide_call_button_enabled|" + _preferences.getBoolean("missed_call_hide_call_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_vibrate_enabled|" + _preferences.getBoolean("missed_call_vibrate_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("missed_call_ringtone_enabled|" + _preferences.getBoolean("missed_call_ringtone_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("missed_call_ringtone_audio|" + _preferences.getString("missed_call_ringtone_audio", "DEFAULT_SOUND") + "|string");
			buf.newLine();
			
			buf.append("calendar_notifications_enabled|" + _preferences.getBoolean("calendar_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_selection|" + _preferences.getString("calendar_selection", "0") + "|string");
			buf.newLine();
			buf.append("calendar_labels_enabled|" + _preferences.getBoolean("calendar_labels_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_dismiss_button_action|" + _preferences.getString("calendar_dismiss_button_action", "") + "|string");
			buf.newLine();
			buf.append("calendar_notification_count_action|" + _preferences.getString("calendar_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("calendar_hide_dismiss_button_enabled|" + _preferences.getBoolean("calendar_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("calendar_hide_view_button_enabled|" + _preferences.getBoolean("calendar_hide_view_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("calendar_reminder_settings|" + _preferences.getString("calendar_reminder_settings", "15") + "|string");
			buf.newLine();
			buf.append("calendar_reminder_all_day_settings|" + _preferences.getString("calendar_reminder_all_day_settings", "6") + "|string");
			buf.newLine();
			buf.append("calendar_vibrate_enabled|" + _preferences.getBoolean("calendar_vibrate_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_ringtone_enabled|" + _preferences.getBoolean("calendar_ringtone_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_ringtone_audio|" + _preferences.getString("calendar_ringtone_audio", "DEFAULT_SOUND") + "|string");
			buf.newLine();

			//Screen Settings
			buf.append("screen_enabled|" + _preferences.getBoolean("screen_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("screen_dim_enabled|" + _preferences.getBoolean("screen_dim_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("keyguard_enabled|" + _preferences.getBoolean("keyguard_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("blur_screen_background_enabled|" + _preferences.getBoolean("blur_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("dim_screen_background_enabled|" + _preferences.getBoolean("dim_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("dim_screen_background_amount_settings|" + _preferences.getString("dim_screen_amount_settings", "50") + "|string");
			buf.newLine();	
			buf.append("landscape_screen_enabled|" + _preferences.getBoolean("landscape_screen_enabled", false) + "|boolean");
			buf.newLine();

			//Advanced Settings
			buf.append("reschedule_notification_timeout_settings|" + _preferences.getString("reschedule_notification_timeout_settings", "5") + "|string");
			buf.newLine();
			buf.append("wakelock_timeout_settings|" + _preferences.getString("wakelock_timeout_settings", "300") + "|string");
			buf.newLine();
			buf.append("keyguard_timeout_settings|" + _preferences.getString("keyguard_timeout_settings", "300") + "|string");
			buf.newLine();
			buf.append("quick_reply_sms_gateway_settings|" + _preferences.getString("quick_reply_sms_gateway_settings", "1") + "|string");
			buf.newLine();
			buf.append("mms_timeout_settings|" + _preferences.getString("mms_timeout_settings", "40") + "|string");
			buf.newLine();
			buf.append("call_log_timeout_settings|" + _preferences.getString("call_log_timeout_settings", "5") + "|string");
			buf.newLine();
			buf.append("ringtone_length_settings|" + _preferences.getString("ringtone_length_settings", "3") + "|string");
			buf.newLine();
			
			buf.close();
		}catch (Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.exportApplicationPreferences() Wrtie File ERROR: " + ex.toString());
			return false;
		}
		return true;
	}
	
	/**
	 * Import application preferences.
	 * 
	 * @author Camille Sévigny
	 */
	private class importPreferencesAsyncTask extends AsyncTask<Void, Void, Boolean> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("MainPreferenceActivity.importPreferencesAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.preference_import_preferences_progress_text), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Boolean doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.importPreferencesAsyncTask.doInBackground()");
	    	return importApplicationPreferences();
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Boolean successful) {
			if (_debug) Log.v("MainPreferenceActivity.importPreferencesAsyncTask.onPostExecute()");
	        dialog.dismiss();
	        if(successful){
	        	Toast.makeText(_context, _context.getString(R.string.preference_import_preferences_finish_text), Toast.LENGTH_LONG).show();
	        }else{
	        	Toast.makeText(_context, _context.getString(R.string.preference_import_preferences_error_text), Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	/**
	 * Import the application preferences from the SD card.
	 * 
	 * @return boolean - True if the operation was successful, false otherwise.
	 */
	private boolean importApplicationPreferences(){
		if (_debug) Log.v("MainPreferenceActivity.importApplicationPreferences()");
		//Check state of external storage.
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    //We can read and write the media. Do nothing.
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media. Do nothing.
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			if (_debug) Log.e("MainPreferenceActivity.importApplicationPreferences() External Storage Can't Write Or Read State");
		    return false;
		}
    	if (!checkPreferencesFileExists("Droid Notify/Preferences/", "DroidNotifyPreferences.txt")){
    		if (_debug) Log.v("MainPreferenceActivity.importApplicationPreferences() Preference file does not exist.");
			return false;
		}
    	try {
    		File preferencesFilePath = Environment.getExternalStoragePublicDirectory("Droid Notify/Preferences/");
        	File preferencesFile = new File(preferencesFilePath, "DroidNotifyPreferences.txt");
    		SharedPreferences.Editor editor = _preferences.edit();
    	    BufferedReader br = new BufferedReader(new FileReader(preferencesFile));
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	    	String[] preferenceInfo = line.split("\\|");
    	        if(preferenceInfo[2].toLowerCase().equals("boolean")){
    	        	editor.putBoolean(preferenceInfo[0], Boolean.parseBoolean(preferenceInfo[1])); 
	    	    }else if(preferenceInfo[2].toLowerCase().equals("string")){
	    	    	editor.putString(preferenceInfo[0], preferenceInfo[1]); 
	    	    }else if(preferenceInfo[2].toLowerCase().equals("int")){
	    	    	editor.putInt(preferenceInfo[0], Integer.parseInt(preferenceInfo[1])); 
	    	    }else if(preferenceInfo[2].toLowerCase().equals("long")){
	    	    	editor.putLong(preferenceInfo[0], Long.parseLong(preferenceInfo[1])); 
	    	    }else if(preferenceInfo[2].toLowerCase().equals("float")){
	    	    	editor.putFloat(preferenceInfo[0], Float.parseFloat(preferenceInfo[1])); 
	    	    }
    	    }
    		editor.commit();
    	}catch (IOException ex) {
    		if (_debug) Log.e("MainPreferenceActivity.importApplicationPreferences() ERROR: " + ex.toString());
    		return false;
    	}
		return true;
	}
	
	/**
	 * Initializes the calendars which will be checked for event notifications.
	 * This sets the user preference to check all available calendars.
	 */
	private void initUserCalendarsPreference(){
		if (_debug) Log.v("MainPreferenceActivity.initUserCalendarsPreference()");
    	String availableCalendarsInfo = Common.getAvailableCalendars(_context);
    	if(availableCalendarsInfo == null){
    		return;
    	}
    	String[] calendarsInfo = availableCalendarsInfo.split(",");
    	StringBuilder calendarSelectionPreference = new StringBuilder();
    	for(String calendarInfo : calendarsInfo){
    		String[] calendarInfoArray = calendarInfo.split("\\|");
    		if(!calendarSelectionPreference.toString().equals("")) calendarSelectionPreference.append("|");
    		calendarSelectionPreference.append(calendarInfoArray[0]);
    	}
    	if (_debug) Log.v("MainPreferenceActivity.initUserCalendarsPreference() calendarSelectionPreference: " + calendarSelectionPreference.toString());
    	SharedPreferences.Editor editor = _preferences.edit();
    	editor.putString(CALENDAR_SELECTION_KEY, calendarSelectionPreference.toString());
    	editor.commit();
	}
	
	/**
	 * Display an HTML AletDialog.
	 */
	private boolean displayHTMLAlertDialog(String title, int iconResource, String content){
		if (_debug) Log.v("MainPreferenceActivity.displayHTMLAlertDialog()");
		try{
    		LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View view = layoutInflater.inflate(R.layout.html_alert_dialog, (ViewGroup) findViewById(R.id.content_scroll_view));		    		
    		TextView contentTextView = (TextView) view.findViewById(R.id.content_text_view);
    		contentTextView.setText(Html.fromHtml(content));
    		contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
    		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
    		builder.setIcon(iconResource);
    		builder.setTitle(title);
    		builder.setView(view);
    		builder.setNegativeButton(R.string.ok_text, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
    		AlertDialog alertDialog = builder.create();
    		alertDialog.show();
    	}catch(Exception ex){
	    		if (_debug) Log.e("MainPreferenceActivity.displayHTMLAlertDialog() ERROR: " + ex.toString());
	    		return false;
    	}
		return true;
	}
	
	/**
	 * Sets up the import preference button. Disables if there is no import file.
	 */
	private void setupImportPreferences(){
		Preference importPreference = (Preference) findPreference("import_preferences");
		importPreference.setEnabled(checkPreferencesFileExists("Droid Notify/Preferences/", "DroidNotifyPreferences.txt"));
	}
	
	/**
	 * Checks if the user has a preferences file on the SD card.
	 * 
	 * @return boolean - Returns true if the preference file exists.
	 */
	private boolean checkPreferencesFileExists(String directory, String file){
		if (_debug) Log.v("MainPreferenceActivity.checkPreferencesFileExists()");
		File preferencesFilePath = Environment.getExternalStoragePublicDirectory(directory);
    	File preferencesFile = new File(preferencesFilePath, file);
    	if (preferencesFile.exists()){
			return true;
		}else{
			return false;
		}
	}
	
}