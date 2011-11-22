package apps.droidnotify.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import android.app.Dialog;
import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarAlarmReceiver;
import apps.droidnotify.twitter.TwitterAuthenticationActivity;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.R;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class MainPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
    private SharedPreferences _preferences = null;
    private String _appVersion = null;
	
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
	    _context = MainPreferenceActivity.this;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    checkSystemDateTimeFormat();
	    addPreferencesFromResource(R.xml.preferences);
	    _appVersion = getApplicationVersion();
	    setupCustomPreferences();
	    runOnceCalendarAlarmManager();
	    setupAppDebugMode(_debug);
	    setupRateAppPreference();
	    runOnce();
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("MainPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		//The polling time for the calendars was changed. Run the alarm manager with the updated polling time.
		if(key.equals(Constants.CALENDAR_POLLING_FREQUENCY_KEY)){
			startCalendarAlarmManager(SystemClock.elapsedRealtime() + (30 * 1000));
		}
		if(key.equals(Constants.SMS_REPLY_BUTTON_ACTION_KEY)){
			//Quick Reply Settings
			updateQuickReplySettings();
		}
		if(key.equals(Constants.MMS_REPLY_BUTTON_ACTION_KEY)){
			//Quick Reply Settings
			updateQuickReplySettings();
		}	
		//Update Status Bar Notification Preferences
		if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_SMS);
		}
		if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_MMS);
		}
		if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_PHONE);
		}
		if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_CALENDAR);
		}
		if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_SMS);
		}
		if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_MMS);
		}
		if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_PHONE);
		}
		if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_CALENDAR);
		}
		if(key.equals(Constants.TWITTER_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.TWITTER_ENABLED_KEY, false)){
				checkTwitterAuthentication();
			}
		}		
		if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}		
		if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}		
		if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}		
		if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}
		
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
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	    setupImportPreferences();
	    initPreferencesStates();   
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (_debug) Log.v("MainPreferenceActivity.onPause()");
        _preferences.unregisterOnSharedPreferenceChangeListener(this);
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
	 * Create new Dialog.
	 * 
	 * @param id - ID of the Dialog that we want to display.
	 * 
	 * @return Dialog - Popup Dialog created.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		if (_debug) Log.v("MainPreferenceActivity.onCreateDialog()");
		switch (id) {
	        /*
	         * Donate dialog.
	         */
			case Constants.DIALOG_DONATE:{
				if (_debug) Log.v("MainPreferenceActivity.onCreateDialog() DIALOG_DONATE");
				LayoutInflater factory = getLayoutInflater();
		        final View donateView = factory.inflate(R.layout.donate, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(donateView);
				builder.setIcon(R.drawable.ic_launcher_droidnotify);
				builder.setTitle(_context.getString(R.string.donate_to_droid_notify_text));
				final AlertDialog alertDialog = builder.create();
		        Button donateAndroidButton = (Button) donateView.findViewById(R.id.donate_android_market_button);
		        if(Log.getShowAndroidRateAppLink()){
			        donateAndroidButton.setOnClickListener(new OnClickListener(){
			        	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATE_APP_ANDROID_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
			        	}
			        });
		        }else{
		        	donateAndroidButton.setVisibility(View.GONE);
		        }
		        Button donateAmazonButton = (Button) donateView.findViewById(R.id.donate_amazon_app_store_button);
		        if(Log.getShowAmazonRateAppLink()){
			        donateAmazonButton.setOnClickListener(new OnClickListener(){
				    	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATE_APP_AMAZON_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
				    	}
				    });
		        }else{
		        	donateAmazonButton.setVisibility(View.GONE);
		        }
		        Button donatePaypalButton = (Button) donateView.findViewById(R.id.donate_paypal_button);
		        donatePaypalButton.setOnClickListener(new OnClickListener() {
		          public void onClick(View view) {
		        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DONATE_PAYPAL_URL));			    	
				    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
			    		startActivity(intent);
			    		alertDialog.dismiss();
		          }
		        });
				return alertDialog;
			}
		}
		return super.onCreateDialog(id);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Starts the main AlarmManager that will check the users calendar for events.
	 * This should run only once when the application is installed.
	 */
	private void runOnceCalendarAlarmManager(){
		if (_debug) Log.v("MainPreferenceActivity.runOnceCalendarAlarmManager()");
		//Read preferences and exit if app is disabled.
	    if(!_preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceCalendarAlarmManager() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!_preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceCalendarAlarmManager() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		boolean runOnce = _preferences.getBoolean(Constants.RUN_ONCE_CALENDAR_ALARM, true);
		if(runOnce) {
			SharedPreferences.Editor editor = _preferences.edit();
			editor.putBoolean(Constants.RUN_ONCE_CALENDAR_ALARM, false);
			editor.commit();
			startCalendarAlarmManager(System.currentTimeMillis());
		}
	}
	
	/**
	 * Start the calendar Alarm Manager.
	 * 
	 * @param alarmStartTime - The time to start the calendar alarm.
	 */
	private void startCalendarAlarmManager(long alarmStartTime){
		if (_debug) Log.v("MainPreferenceActivity.startCalendarAlarmManager() alarmStartTime: " + String.valueOf(alarmStartTime));
		//Make sure that this user preference has been set and initialized.
		initUserCalendarsPreference();
		//Schedule the reading of the calendar events.
		AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(_context, CalendarAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
		long pollingFrequency = Long.parseLong(_preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
	}
	
	/**
	 * This displays the EULA to the user the first time the app is run.
	 */
	private void runOnce(){
		if (_debug) Log.v("MainPreferenceActivity.runOnce()");
		if(_preferences.getBoolean(Constants.RUN_ONCE_EULA, true)) {
			try{
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.RUN_ONCE_EULA, false);
				editor.commit();
				displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info, _context.getString(R.string.eula_text));
			}catch(Exception ex){
 	    		if (_debug) Log.e("MainPreferenceActivity.runOnceEula() EULA ERROR: " + ex.toString());
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
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_TEST);
		    	Intent intent = new Intent(_context, NotificationActivity.class);
		    	intent.putExtras(bundle);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Test Notifications Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_test_app_error), Toast.LENGTH_LONG).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Quiet Time Button
		Preference quietTimePref = (Preference)findPreference("quiet_time_blackout_period_settings");
		quietTimePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Quiet Time Button Clicked()");
		    	try{
		    		showQuietTimePeriodDialog();
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Quiet Time Button ERROR: " + ex.toString());
	 	    		//Toast.makeText(_context, _context.getString(R.string.app_preference_quiet_time_error), Toast.LENGTH_LONG).show();
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
			    	if(Log.getShowAndroidRateAppLink()){
			    		rateAppURL = Constants.RATE_APP_ANDROID_URL;
			    	}else if(Log.getShowAmazonRateAppLink()){
			    		rateAppURL = Constants.RATE_APP_AMAZON_URL;
			    	}else{
			    		rateAppURL = "";
			    	}
			    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rateAppURL));			    	
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Rate This App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_LONG).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Donate Preference/Button
		Preference donatePref = (Preference)findPreference("donate_to_project");
		donatePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Donate Button Clicked()");
		    	try{
		    		showDialog(Constants.DIALOG_DONATE);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("MainPreferenceActivity.setupCustomPreferences() Donate Button ERROR: " + ex.toString());
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
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_LONG).show();
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
		    	return displayHTMLAlertDialog(_context.getString(R.string.app_name_formatted_version, _appVersion),R.drawable.ic_launcher_droidnotify,_context.getString(R.string.preference_about_text));
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
		//Email Developer Logs Preference/Button
		Preference emailDeveloperLogsPref = (Preference)findPreference("email_logs");
		emailDeveloperLogsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Email Developer Logs Button Clicked()");
		    	try{
			    	Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	intent.putExtra("subject", "Droid Notify App Logs");
			    	intent.putExtra("body", "");
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
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_LONG).show();
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
		if(Log.getShowAndroidRateAppLink()){
			showRateAppCategory = true;
		}else if(Log.getShowAmazonRateAppLink()){
			showRateAppCategory = true;
		}else{
			showRateAppCategory = false;
		}
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
    		//Write each preference to the text file.
			BufferedWriter buf = new BufferedWriter(new FileWriter(preferencesFile, true)); 		
			Map<String, ?> applicationPreferencesMap = _preferences.getAll();
			for (Map.Entry<String, ?> entry : applicationPreferencesMap.entrySet()) {
			    String key = entry.getKey();
			    Object value = entry.getValue();
			    if(value instanceof String){
			    	buf.append(key + "|" + value + "|string");
			    }else if(value instanceof Boolean){
			    	buf.append(key + "|" + value + "|boolean");
			    }else if(value instanceof Integer){
			    	buf.append(key + "|" + value + "|int");
			    }else if(value instanceof Long){
			    	buf.append(key + "|" + value + "|long");
			    }else if(value instanceof Float){
			    	buf.append(key + "|" + value + "|float");
			    }
				buf.newLine();
			}
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
	        reloadPreferenceActivity();
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
    	editor.putString(Constants.CALENDAR_SELECTION_KEY, calendarSelectionPreference.toString());
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
    		contentTextView.setText(Html.fromHtml(content.replace("&lt;", "<")));
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
		if (_debug) Log.v("MainPreferenceActivity.setupImportPreferences()");
		try{
			Preference importPreference = (Preference) findPreference("import_preferences");
			if(importPreference != null) importPreference.setEnabled(checkPreferencesFileExists("Droid Notify/Preferences/", "DroidNotifyPreferences.txt"));
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.setupImportPreferences() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Checks if the user has a preferences file on the SD card.
	 * 
	 * @return boolean - Returns true if the preference file exists.
	 */
	private boolean checkPreferencesFileExists(String directory, String file){
		if (_debug) Log.v("MainPreferenceActivity.checkPreferencesFileExists()");
		try{
			File preferencesFilePath = Environment.getExternalStoragePublicDirectory(directory);
	    	File preferencesFile = new File(preferencesFilePath, file);
	    	if (preferencesFile.exists()){
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * Read the Application info and return the app version number.
	 * 
	 * @return String - The version number of the aplication.
	 */
	private String getApplicationVersion(){
		if (_debug) Log.v("MainPreferenceActivity.getApplicationVersion()");
		PackageInfo packageInfo = null;
		try{
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		}catch(Exception ex){
			return "";
		}
	}
	
	/**
	 * Initialize the state of some preference items that have multiple dependencies.
	 */
	private void initPreferencesStates(){
		if (_debug) Log.v("MainPreferenceActivity.initPreferencesStates()");
		updateQuickReplySettings();
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_SMS);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_MMS);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_PHONE);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_CALENDAR);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_SMS);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_MMS);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_PHONE);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_CALENDAR);
		updateClearStatusBarNotifications();
	}
	
	/**
	 * Updates the availability of the Quick Reply SMS Gateway Setting.
	 */
	private void updateQuickReplySettings(){
		if (_debug) Log.v("MainPreferenceActivity.updateQuickReplySettings()");
		try{
			boolean quickReplySMSGatewayEnabled = false;
			if(_preferences.getString(Constants.SMS_REPLY_BUTTON_ACTION_KEY, "0").equals("1")){
				quickReplySMSGatewayEnabled = true;
			}
			if(_preferences.getString(Constants.MMS_REPLY_BUTTON_ACTION_KEY, "0").equals("1")){
				quickReplySMSGatewayEnabled = true;
			}
			ListPreference quickReplySMSGateway = (ListPreference) findPreference(Constants.SMS_GATEWAY_KEY);
			if(quickReplySMSGateway != null) quickReplySMSGateway.setEnabled(quickReplySMSGatewayEnabled);
			PreferenceScreen quickReplyPreferenceScreen = (PreferenceScreen) findPreference(Constants.QUICK_REPLY_SETTINGS_SCREEN);
			if(quickReplyPreferenceScreen != null) quickReplyPreferenceScreen.setEnabled(quickReplySMSGatewayEnabled);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateQuickReplySettings() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the Ringtone In-Call preference based on the Ringtone Settings.
	 * 
	 * @param notificationType - The notification type.
	 */
	private void updateStatusBarNotificationRingtone(int notificationType){
		if (_debug) Log.v("MainPreferenceActivity.updateStatusBarNotificationRingtone()");
		try{
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateStatusBarNotificationRingtone() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the Vibrate preferences based on the Vibrate Settings.
	 * 
	 * @param notificationType - The notification type.
	 */
	private void updateStatusBarNotificationVibrate(int notificationType){
		if (_debug) Log.v("MainPreferenceActivity.updateStatusBarNotificationVibrate()");
		try{
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateStatusBarNotificationVibrate() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the Clear Status Bar Notofication preference based on the Status Bar Notification Settings.
	 */
	private void updateClearStatusBarNotifications(){
		if (_debug) Log.v("MainPreferenceActivity.updateClearStatusBarNotifications()");
		try{
			boolean enabled = false;
			if(_preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				enabled = true;
			}
			if(_preferences.getBoolean(Constants.MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.MMS_NOTIFICATIONS_ENABLED_KEY, true)){
				enabled = true;
			}	
			if(_preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.PHONE_NOTIFICATIONS_ENABLED_KEY, true)){
				enabled = true;
			}	
			if(_preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
				enabled = true;
			}	
			CheckBoxPreference clearStatusBarNotificationsOnExitCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY);
			if(clearStatusBarNotificationsOnExitCheckBoxPreference != null) clearStatusBarNotificationsOnExitCheckBoxPreference.setEnabled(enabled);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateClearStatusBarNotifications() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * A first time installation check and update of the Date & Time format settings.
	 */
	private void checkSystemDateTimeFormat(){
		if (_debug) Log.v("MainPreferenceActivity.checkSystemDateTimeFormat()");
		if(_preferences.getBoolean(Constants.RUN_ONCE_DATE_TIME_FORMAT, true)){
			try{
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.RUN_ONCE_DATE_TIME_FORMAT, false);
				String systemDateFormat = Settings.System.getString(_context.getContentResolver(), Settings.System.DATE_FORMAT);
			    String systemHourFormat = Settings.System.getString(_context.getContentResolver(), Settings.System.TIME_12_24);
			    if(systemDateFormat != null && !systemDateFormat.equals("")){
			    	if(systemDateFormat.equals("MM-dd-yyyy")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_0));
			    	}else if(systemDateFormat.equals("dd-MM-yyyy")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_6));
			    	}else if(systemDateFormat.equals("yyyy-MM-dd")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_12));
			    	}
			    }else{
			    	systemDateFormat = String.valueOf(DateFormat.getDateFormatOrder(_context));
			    	if(systemDateFormat.equals("Mdy")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_0));
			    	}else if(systemDateFormat.equals("dMy")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_6));
			    	}else if(systemDateFormat.equals("yMd")){
			    		editor.putString(Constants.DATE_FORMAT_KEY, String.valueOf(Constants.DATE_FORMAT_12));
			    	}
			    }
			    if(systemHourFormat.equals("12")){
					editor.putString(Constants.TIME_FORMAT_KEY, String.valueOf(Constants.TIME_FORMAT_12_HOUR));
			    }else{
					editor.putString(Constants.TIME_FORMAT_KEY, String.valueOf(Constants.TIME_FORMAT_24_HOUR));
			    }
				editor.commit();
			}catch(Exception ex){
 	    		if (_debug) Log.e("MainPreferenceActivity.checkSystemDateTimeFormat() ERROR: " + ex.toString());
	    	}
		}		
	}
	
	/**
	 * Check if the user has already authorizes us to use access his twitter account.
	 * Launch authorization activity if not.
	 */
	private void checkTwitterAuthentication(){
		if (_debug) Log.v("MainPreferenceActivity.checkTwitterAuthentication()");
		//Setup User Twitter Authentication
	    Intent intent = new Intent(_context, TwitterAuthenticationActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
	    startActivity(intent);
	}
	
	/**
	 * Reload Preference Activity
	 */
	public void reloadPreferenceActivity() {
		if (_debug) Log.v("MainPreferenceActivity.reloadPreferenceActivity()");
		try{
		    Intent intent = getIntent();
		    overridePendingTransition(0, 0);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    overridePendingTransition(0, 0);
		    startActivity(intent);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.reloadPreferenceActivity() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Display the dialog window that allows the user to set the quiet time hours.
	 */
	private void showQuietTimePeriodDialog() {
		if (_debug) Log.v("MainPreferenceActivity.showQuietTimePeriodDialog()");
	    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.quietimeperiodialog, null);
		final TimePicker startTimePicker = (TimePicker) view.findViewById(R.id.start_time_picker);
		final TimePicker stopTimePicker = (TimePicker) view.findViewById(R.id.stop_time_picker);
		//Sets the view format based on the users time format preference.
		if(_preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT).equals(Constants.TIME_FORMAT_24_HOUR)){
			startTimePicker.setIs24HourView(true);
			stopTimePicker.setIs24HourView(true);
		}else{
			startTimePicker.setIs24HourView(false);
			stopTimePicker.setIs24HourView(false);
		}
		//Initialize the TimePickers
		String startTime = _preferences.getString(Constants.QUIET_TIME_START_TIME_KEY, "");
		String stopTime = _preferences.getString(Constants.QUIET_TIME_STOP_TIME_KEY, "");
		if(!startTime.equals("")){
			String[] startTimeArray = startTime.split("\\|");
			if(startTimeArray.length == 2){
				startTimePicker.setCurrentHour(Integer.parseInt(startTimeArray[0]));
				startTimePicker.setCurrentMinute(Integer.parseInt(startTimeArray[1]));
			}
		}
		if(!stopTime.equals("")){
			String[] stopTimeArray = stopTime.split("\\|");
			if(stopTimeArray.length == 2){
				stopTimePicker.setCurrentHour(Integer.parseInt(stopTimeArray[0]));
				stopTimePicker.setCurrentMinute(Integer.parseInt(stopTimeArray[1]));
			}
		}
		//Build & Display Dialog
		AlertDialog.Builder quietTimePeriodAlertBuilder = new AlertDialog.Builder(_context);
		quietTimePeriodAlertBuilder.setIcon(R.drawable.ic_dialog_info);
		quietTimePeriodAlertBuilder.setTitle(R.string.preference_quiet_time_quiet_period_title);
		quietTimePeriodAlertBuilder.setView(view);
		quietTimePeriodAlertBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				SharedPreferences.Editor editor = _preferences.edit();
	        	editor.putString(Constants.QUIET_TIME_START_TIME_KEY, startTimePicker.getCurrentHour() + "|" + startTimePicker.getCurrentMinute());
	        	editor.putString(Constants.QUIET_TIME_STOP_TIME_KEY, stopTimePicker.getCurrentHour() + "|" + stopTimePicker.getCurrentMinute());
	            editor.commit();
				Toast.makeText(_context, _context.getString(R.string.preference_quiet_time_period_set), Toast.LENGTH_LONG).show();
			}
		});
		quietTimePeriodAlertBuilder.show();
	}

}