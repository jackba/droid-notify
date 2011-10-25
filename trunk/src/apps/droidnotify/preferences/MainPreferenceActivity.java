package apps.droidnotify.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
	    //addPreferencesFromResource(R.xml.preferences_new);
	    addPreferencesFromResource(R.xml.preferences);
	    _appVersion = getApplicationVersion();
	    setupCustomPreferences();
	    runOnceCalendarAlarmManager();
	    setupAppDebugMode(_debug);
	    setupRateAppPreference();
	    runOnceEula();
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
		if(key.equals(Constants.SMS_HIDE_CONTACT_PANEL_ENABLED_KEY)){
			//Update SMS Contact Info Display
			updateSMSContactInfoSetting();
		}
		if(key.equals(Constants.MMS_HIDE_CONTACT_PANEL_ENABLED_KEY)){
			//Update MMS Contact Info Display
			updateMMSContactInfoSetting();
		}
		if(key.equals(Constants.PHONE_HIDE_CONTACT_PANEL_ENABLED_KEY)){
			//Update Phone Contact Info Display
			updatePhoneContactInfoSetting();
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
		if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager()");
		//Read preferences and exit if app is disabled.
	    if(!_preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!_preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("MainPreferenceActivity.runOnceAlarmManager() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		boolean runOnce = _preferences.getBoolean(Constants.RUN_ONCE_CALENDAR_ALARM, true);
		if(runOnce) {
			SharedPreferences.Editor editor = _preferences.edit();
			editor.putBoolean(Constants.RUN_ONCE_CALENDAR_ALARM, false);
			editor.commit();
			startCalendarAlarmManager(SystemClock.elapsedRealtime() + (5 * 60 * 1000));
       }
	}
	
	/**
	 * Start the calendar Alarm Manager.
	 * 
	 * @param alarmStartTime - The time to start the calendar alarm.
	 */
	private void startCalendarAlarmManager(long alarmStartTime){
		if (_debug) Log.v("MainPreferenceActivity.startCalendarAlarmManager()");
		//Schedule the reading of the calendar events.
		AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(_context, CalendarAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
		long pollingFrequency = Long.parseLong(_preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		initUserCalendarsPreference();
	}
	
	/**
	 * This displays the EULA to the user the first time the app is run.
	 */
	private void runOnceEula(){
		if (_debug) Log.v("MainPreferenceActivity.runOnceEula()");
		boolean runOnceEula = _preferences.getBoolean(Constants.RUN_ONCE_EULA, true);
		if(runOnceEula) {
			try{
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.RUN_ONCE_EULA, false);
				editor.commit();
				displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info, _context.getString(R.string.eula_text));
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
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_TEST);
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
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_SHORT).show();
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
			
			//General Settings
			buf.append("app_enabled|" + _preferences.getBoolean("app_enabled", true) + "|boolean");
			buf.newLine();

			//Basic Settings
			buf.append("app_theme|" + _preferences.getString("app_theme", "android_default") + "|string");
			buf.newLine();
			buf.append("phone_number_format_settings|" + _preferences.getString("phone_number_format_settings", "1") + "|string");
			buf.newLine();
			buf.append("time_format_settings|" + _preferences.getString("time_format_settings", "0") + "|string");
			buf.newLine();
			buf.append("date_format_settings|" + _preferences.getString("date_format_settings", "0") + "|string");
			buf.newLine();	
			buf.append("button_icons_enabled|" + _preferences.getBoolean("button_icons_enabled", true) + "|boolean");
			buf.newLine();	
			buf.append("hide_single_message_header_enabled|" + _preferences.getBoolean("hide_single_message_header_enabled", false) + "|boolean");
			buf.newLine();	
			buf.append("notification_type_info_icon_enabled|" + _preferences.getBoolean("notification_type_info_icon_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("notification_type_info_font_size|" + _preferences.getString("notification_type_info_font_size", "5") + "|string");
			buf.newLine();
			buf.append("contact_placeholder|" + _preferences.getString("contact_placeholder", "0") + "|string");
			buf.newLine();
			buf.append("contact_photo_background|" + _preferences.getString("contact_photo_background", "0") + "|string");
			buf.newLine();
			buf.append("contact_photo_size|" + _preferences.getString("contact_photo_size", "80") + "|string");
			buf.newLine();
			buf.append("notification_body_font_size|" + _preferences.getString("notification_body_font_size", "14") + "|string");
			buf.newLine();
			buf.append("display_contact_name_enabled|" + _preferences.getBoolean("display_contact_name_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("contact_name_font_size|" + _preferences.getString("contact_name_font_size", "22") + "|string");
			buf.newLine();
			buf.append("display_contact_number_enabled|" + _preferences.getBoolean("display_contact_number_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("contact_number_font_size|" + _preferences.getString("contact_number_font_size", "18") + "|string");
			buf.newLine();
			
			//Quick Reply Settings
			buf.append("quick_reply_save_draft_enabled|" + _preferences.getBoolean("quick_reply_save_draft_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("quick_reply_blur_screen_background_enabled|" + _preferences.getBoolean("quick_reply_blur_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("quick_reply_dim_screen_background_enabled|" + _preferences.getBoolean("quick_reply_dim_screen_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("quick_reply_dim_screen_background_amount_settings|" + _preferences.getString("quick_reply_dim_screen_amount_settings", "50") + "|string");
			buf.newLine();
			buf.append("quick_reply_hide_cancel_button_enabled|" + _preferences.getBoolean("quick_reply_hide_cancel_button_enabled", true) + "|boolean");
			buf.newLine();	
			
			//SMS Notification Settings
			buf.append("sms_notifications_enabled|" + _preferences.getBoolean("sms_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_display_unread_enabled|" + _preferences.getBoolean("sms_display_unread_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("messaging_app_running_action_sms|" + _preferences.getString("messaging_app_running_action_sms", "2") + "|string");
			buf.newLine();

			//SMS Status Bar Notification Settings
			buf.append("sms_status_bar_notifications_enabled|" + _preferences.getBoolean("sms_status_bar_notifications_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("sms_status_bar_notifications_show_when_blocked_enabled|" + _preferences.getBoolean("sms_status_bar_notifications_show_when_blocked_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("notification_icon_sms|" + _preferences.getString("notification_icon_sms", "status_bar_notification_sms_green_preference") + "|string");
			buf.newLine();
			buf.append("sms_notification_sound|" + _preferences.getString("sms_notification_sound", "content://settings/system/notification_sound") + "|string");
			buf.newLine();
			buf.append("sms_notification_vibrate_setting|" + _preferences.getString("sms_notification_vibrate_setting", "0") + "|string");
			buf.newLine();
			buf.append("sms_notification_vibrate_pattern|" + _preferences.getString("sms_notification_vibrate_pattern", "0,1200") + "|string");
			buf.newLine();
			buf.append("sms_notification_vibrate_pattern_custom|" + _preferences.getString("sms_notification_vibrate_pattern_custom", "0,1200") + "|string");
			buf.newLine();
			buf.append("sms_notification_led_enabled|" + _preferences.getBoolean("sms_notification_led_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("sms_notification_led_color|" + _preferences.getString("sms_notification_led_color", "yellow") + "|string");
			buf.newLine();
			buf.append("sms_notification_led_color_custom|" + _preferences.getString("sms_notification_led_color_custom", "yellow") + "|string");
			buf.newLine();
			buf.append("sms_notification_led_pattern|" + _preferences.getString("sms_notification_led_pattern", "1000,1000") + "|string");
			buf.newLine();
			buf.append("sms_notification_led_pattern_custom|" + _preferences.getString("sms_notification_led_pattern_custom", "1000,1000") + "|string");
			buf.newLine();
			buf.append("sms_notification_in_call_sound_enabled|" + _preferences.getBoolean("sms_notification_in_call_sound_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_notification_in_call_vibrate_enabled|" + _preferences.getBoolean("sms_notification_in_call_vibrate_enabled", false) + "|boolean");
			buf.newLine();

			buf.append("sms_hide_notification_body_enabled|" + _preferences.getBoolean("sms_hide_notification_body_enabled", false) + "|boolean");
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
			buf.append("sms_hide_contact_panel_enabled|" + _preferences.getBoolean("sms_hide_contact_panel_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_contact_photo_enabled|" + _preferences.getBoolean("sms_hide_contact_photo_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_contact_name_enabled|" + _preferences.getBoolean("sms_hide_contact_name_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("sms_hide_contact_number_enabled|" + _preferences.getBoolean("sms_hide_contact_number_enabled", false) + "|boolean");
			buf.newLine();
			
			//MMS Notification Settings
			buf.append("mms_notifications_enabled|" + _preferences.getBoolean("mms_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_display_unread_enabled|" + _preferences.getBoolean("mms_display_unread_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("messaging_app_running_action_mms|" + _preferences.getString("messaging_app_running_action_mms", "2") + "|string");
			buf.newLine();
			
			//MMS Status Bar Notification Settings
			buf.append("mms_status_bar_notifications_enabled|" + _preferences.getBoolean("mms_status_bar_notifications_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("mms_status_bar_notifications_show_when_blocked_enabled|" + _preferences.getBoolean("mms_status_bar_notifications_show_when_blocked_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("notification_icon_mms|" + _preferences.getString("notification_icon_mms", "status_bar_notification_sms_green_preference") + "|string");
			buf.newLine();
			buf.append("mms_notification_sound|" + _preferences.getString("mms_notification_sound", "content://settings/system/notification_sound") + "|string");
			buf.newLine();
			buf.append("mms_notification_vibrate_setting|" + _preferences.getString("mms_notification_vibrate_setting", "0") + "|string");
			buf.newLine();
			buf.append("mms_notification_vibrate_pattern|" + _preferences.getString("mms_notification_vibrate_pattern", "0,1200") + "|string");
			buf.newLine();
			buf.append("mms_notification_vibrate_pattern_custom|" + _preferences.getString("mms_notification_vibrate_pattern_custom", "0,1200") + "|string");
			buf.newLine();
			buf.append("mms_notification_led_enabled|" + _preferences.getBoolean("mms_notification_led_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("mms_notification_led_color|" + _preferences.getString("mms_notification_led_color", "yellow") + "|string");
			buf.newLine();
			buf.append("mms_notification_led_color_custom|" + _preferences.getString("mms_notification_led_color_custom", "yellow") + "|string");
			buf.newLine();
			buf.append("mms_notification_led_pattern|" + _preferences.getString("mms_notification_led_pattern", "1000,1000") + "|string");
			buf.newLine();
			buf.append("mms_notification_led_pattern_custom|" + _preferences.getString("mms_notification_led_pattern_custom", "1000,1000") + "|string");
			buf.newLine();
			buf.append("mms_notification_in_call_sound_enabled|" + _preferences.getBoolean("mms_notification_in_call_sound_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_notification_in_call_vibrate_enabled|" + _preferences.getBoolean("mms_notification_in_call_vibrate_enabled", false) + "|boolean");
			buf.newLine();
			
			buf.append("mms_hide_notification_body_enabled|" + _preferences.getBoolean("mms_hide_notification_body_enabled", false) + "|boolean");
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
			buf.append("mms_hide_contact_panel_enabled|" + _preferences.getBoolean("mms_hide_contact_panel_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_contact_photo_enabled|" + _preferences.getBoolean("mms_hide_contact_photo_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_contact_name_enabled|" + _preferences.getBoolean("mms_hide_contact_name_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("mms_hide_contact_number_enabled|" + _preferences.getBoolean("mms_hide_contact_number_enabled", false) + "|boolean");
			buf.newLine();
			
			//Missed Call Notification Settings
			buf.append("missed_call_notifications_enabled|" + _preferences.getBoolean("missed_call_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("missed_call_loading_settings|" + _preferences.getString("missed_call_loading_settings", "0") + "|string");
			buf.newLine();
			buf.append("messaging_app_running_action_missed_call|" + _preferences.getString("messaging_app_running_action_missed_call", "2") + "|string");
			buf.newLine();
			
			//Missed Call Status Bar Notification Settings
			buf.append("missed_call_status_bar_notifications_enabled|" + _preferences.getBoolean("missed_call_status_bar_notifications_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("missed_call_status_bar_notifications_show_when_blocked_enabled|" + _preferences.getBoolean("missed_call_status_bar_notifications_show_when_blocked_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("notification_icon_missed_call|" + _preferences.getString("notification_icon_missed_call", "status_bar_notification_missed_call_black_preference") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_sound|" + _preferences.getString("missed_call_notification_sound", "content://settings/system/notification_sound") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_vibrate_setting|" + _preferences.getString("missed_call_notification_vibrate_setting", "0") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_vibrate_pattern|" + _preferences.getString("missed_call_notification_vibrate_pattern", "0,1200") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_vibrate_pattern_custom|" + _preferences.getString("missed_call_notification_vibrate_pattern_custom", "0,1200") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_led_enabled|" + _preferences.getBoolean("missed_call_notification_led_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("missed_call_notification_led_color|" + _preferences.getString("missed_call_notification_led_color", "yellow") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_led_color_custom|" + _preferences.getString("missed_call_notification_led_color_custom", "yellow") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_led_pattern|" + _preferences.getString("missed_call_notification_led_pattern", "1000,1000") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_led_pattern_custom|" + _preferences.getString("missed_call_notification_led_pattern_custom", "1000,1000") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_in_call_sound_enabled|" + _preferences.getBoolean("missed_call_notification_in_call_sound_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_notification_in_call_vibrate_enabled|" + _preferences.getBoolean("missed_call_notification_in_call_vibrate_enabled", false) + "|boolean");
			buf.newLine();	
			
			buf.append("missed_call_dismiss_button_action|" + _preferences.getString("missed_call_dismiss_button_action", "0") + "|string");
			buf.newLine();
			buf.append("missed_call_notification_count_action|" + _preferences.getString("missed_call_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("missed_call_hide_dismiss_button_enabled|" + _preferences.getBoolean("missed_call_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_hide_call_button_enabled|" + _preferences.getBoolean("missed_call_hide_call_button_enabled", false) + "|boolean");
			buf.newLine();	
			buf.append("missed_call_hide_contact_panel_enabled|" + _preferences.getBoolean("missed_call_hide_contact_panel_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_hide_contact_photo_enabled|" + _preferences.getBoolean("missed_call_hide_contact_photo_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_hide_contact_name_enabled|" + _preferences.getBoolean("missed_call_hide_contact_name_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("missed_call_hide_contact_number_enabled|" + _preferences.getBoolean("missed_call_hide_contact_number_enabled", false) + "|boolean");
			buf.newLine();
			
			//Calendar Notification Settings
			buf.append("calendar_notifications_enabled|" + _preferences.getBoolean("calendar_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_notify_day_of_time|" + _preferences.getString("calendar_notify_day_of_time", "12") + "|string");
			buf.newLine();
			buf.append("messaging_app_running_action_calendar|" + _preferences.getString("messaging_app_running_action_calendar", "2") + "|string");
			buf.newLine();
			buf.append("calendar_polling_frequency|" + _preferences.getString("calendar_polling_frequency", "15") + "|string");
			buf.newLine();
			buf.append("calendar_selection|" + _preferences.getString("calendar_selection", "0") + "|string");
			buf.newLine();		
			buf.append("calendar_labels_enabled|" + _preferences.getBoolean("calendar_labels_enabled", true) + "|boolean");
			buf.newLine();	
			
			//Calendar Status Bar Notification Settings
			buf.append("calendar_status_bar_notifications_enabled|" + _preferences.getBoolean("calendar_status_bar_notifications_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("calendar_status_bar_notifications_show_when_blocked_enabled|" + _preferences.getBoolean("calendar_status_bar_notifications_show_when_blocked_enabled", true) + "|boolean");
			buf.newLine();			
			buf.append("notification_icon_calendar|" + _preferences.getString("notification_icon_calendar", "status_bar_notification_calendar_blue_preference") + "|string");
			buf.newLine();
			buf.append("calendar_notification_sound|" + _preferences.getString("calendar_notification_sound", "content://settings/system/notification_sound") + "|string");
			buf.newLine();
			buf.append("calendar_notification_vibrate_setting|" + _preferences.getString("calendar_notification_vibrate_setting", "0") + "|string");
			buf.newLine();
			buf.append("calendar_notification_vibrate_pattern|" + _preferences.getString("calendar_notification_vibrate_pattern", "0,1200") + "|string");
			buf.newLine();
			buf.append("calendar_notification_vibrate_pattern_custom|" + _preferences.getString("calendar_notification_vibrate_pattern_custom", "0,1200") + "|string");
			buf.newLine();
			buf.append("calendar_notification_led_enabled|" + _preferences.getBoolean("calendar_notification_led_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_notification_led_color|" + _preferences.getString("calendar_notification_led_color", "yellow") + "|string");
			buf.newLine();
			buf.append("calendar_notification_led_color_custom|" + _preferences.getString("calendar_notification_led_color_custom", "yellow") + "|string");
			buf.newLine();
			buf.append("calendar_notification_led_pattern|" + _preferences.getString("calendar_notification_led_pattern", "1000,1000") + "|string");
			buf.newLine();
			buf.append("calendar_notification_led_pattern_custom|" + _preferences.getString("calendar_notification_led_pattern_custom", "1000,1000") + "|string");
			buf.newLine();
			buf.append("calendar_notification_in_call_sound_enabled|" + _preferences.getBoolean("calendar_notification_in_call_sound_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("calendar_notification_in_call_vibrate_enabled|" + _preferences.getBoolean("calendar_notification_in_call_vibrate_enabled", false) + "|boolean");
			buf.newLine();	
			
			buf.append("calendar_hide_notification_body_enabled|" + _preferences.getBoolean("calendar_hide_notification_body_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("calendar_reminders_enabled|" + _preferences.getBoolean("calendar_reminders_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("calendar_reminder_settings|" + _preferences.getString("calendar_reminder_settings", "15") + "|string");
			buf.newLine();
			buf.append("calendar_reminder_all_day_settings|" + _preferences.getString("calendar_reminder_all_day_settings", "6") + "|string");
			buf.newLine();
			buf.append("calendar_dismiss_button_action|" + _preferences.getString("calendar_dismiss_button_action", "") + "|string");
			buf.newLine();
			buf.append("calendar_notification_count_action|" + _preferences.getString("calendar_notification_count_action", "0") + "|string");
			buf.newLine();
			buf.append("calendar_hide_dismiss_button_enabled|" + _preferences.getBoolean("calendar_hide_dismiss_button_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("calendar_hide_view_button_enabled|" + _preferences.getBoolean("calendar_hide_view_button_enabled", false) + "|boolean");
			buf.newLine();

			//Screen Settings
			buf.append("screen_enabled|" + _preferences.getBoolean("screen_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("screen_dim_enabled|" + _preferences.getBoolean("screen_dim_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("keyguard_enabled|" + _preferences.getBoolean("keyguard_enabled", true) + "|boolean");
			buf.newLine();	
			buf.append("landscape_screen_enabled|" + _preferences.getBoolean("landscape_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("blur_screen_background_enabled|" + _preferences.getBoolean("blur_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("dim_screen_background_enabled|" + _preferences.getBoolean("dim_screen_enabled", false) + "|boolean");
			buf.newLine();
			buf.append("dim_screen_background_amount_settings|" + _preferences.getString("dim_screen_amount_settings", "50") + "|string");
			buf.newLine();

			//Advanced Settings
			buf.append("haptic_feedback_enabled|" + _preferences.getBoolean("haptic_feedback_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("screen_timeout_settings|" + _preferences.getString("screen_timeout_settings", "300") + "|string");
			buf.newLine();
			buf.append("sms_timeout_settings|" + _preferences.getString("sms_timeout_settings", "10") + "|string");
			buf.newLine();
			buf.append("sms_loading_settings|" + _preferences.getString("sms_loading_settings", "0") + "|string");
			buf.newLine();
			buf.append("sms_timestamp_adjustment_settings|" + _preferences.getString("sms_timestamp_adjustment_settings", "0") + "|string");
			buf.newLine();
			buf.append("mms_timeout_settings|" + _preferences.getString("mms_timeout_settings", "40") + "|string");
			buf.newLine();
			buf.append("call_log_timeout_settings|" + _preferences.getString("call_log_timeout_settings", "5") + "|string");
			buf.newLine();
			buf.append("quick_reply_sms_gateway_settings|" + _preferences.getString("quick_reply_sms_gateway_settings", "1") + "|string");
			buf.newLine();
			buf.append("reschedule_notifications_enabled|" + _preferences.getBoolean("reschedule_notifications_enabled", true) + "|boolean");
			buf.newLine();
			buf.append("reschedule_notification_timeout_settings|" + _preferences.getString("reschedule_notification_timeout_settings", "5") + "|string");
			buf.newLine();
			buf.append("clear_status_bar_notifications_on_exit_enabled|" + _preferences.getBoolean("clear_status_bar_notifications_on_exit_enabled", false) + "|boolean");
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
			importPreference.setEnabled(checkPreferencesFileExists("Droid Notify/Preferences/", "DroidNotifyPreferences.txt"));
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
		updateSMSContactInfoSetting();
		updateMMSContactInfoSetting();
		updatePhoneContactInfoSetting();
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
	 * Updates the availability of the SMS Contact Info Settings.
	 */
	private void updateSMSContactInfoSetting(){
		if (_debug) Log.v("MainPreferenceActivity.updateSMSContactInfoSetting()");
		try{
			boolean contactInfoDisplaySettingsEnabled = _preferences.getBoolean(Constants.SMS_HIDE_CONTACT_PANEL_ENABLED_KEY, false);
			CheckBoxPreference hideContactPhoto = (CheckBoxPreference) findPreference(Constants.SMS_HIDE_CONTACT_PHOTO_ENABLED_KEY);
			hideContactPhoto.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactName = (CheckBoxPreference) findPreference(Constants.SMS_HIDE_CONTACT_NAME_ENABLED_KEY);
			hideContactName.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactNumber = (CheckBoxPreference) findPreference(Constants.SMS_HIDE_CONTACT_NUMBER_ENABLED_KEY);
			hideContactNumber.setEnabled(!contactInfoDisplaySettingsEnabled);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateSMSContactInfoSetting() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the availability of the MMS Contact Info Settings.
	 */
	private void updateMMSContactInfoSetting(){
		if (_debug) Log.v("MainPreferenceActivity.updateMMSContactInfoSetting()");
		try{
			boolean contactInfoDisplaySettingsEnabled = _preferences.getBoolean(Constants.MMS_HIDE_CONTACT_PANEL_ENABLED_KEY, false);
			CheckBoxPreference hideContactPhoto = (CheckBoxPreference) findPreference(Constants.MMS_HIDE_CONTACT_PHOTO_ENABLED_KEY);
			hideContactPhoto.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactName = (CheckBoxPreference) findPreference(Constants.MMS_HIDE_CONTACT_NAME_ENABLED_KEY);
			hideContactName.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactNumber = (CheckBoxPreference) findPreference(Constants.MMS_HIDE_CONTACT_NUMBER_ENABLED_KEY);
			hideContactNumber.setEnabled(!contactInfoDisplaySettingsEnabled);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updateMMSContactInfoSetting() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the availability of the Phone Contact Info Settings.
	 */
	private void updatePhoneContactInfoSetting(){
		if (_debug) Log.v("MainPreferenceActivity.updatePhoneContactInfoSetting()");
		try{
			boolean contactInfoDisplaySettingsEnabled = _preferences.getBoolean(Constants.PHONE_HIDE_CONTACT_PANEL_ENABLED_KEY, false);
			CheckBoxPreference hideContactPhoto = (CheckBoxPreference) findPreference(Constants.PHONE_HIDE_CONTACT_PHOTO_ENABLED_KEY);
			hideContactPhoto.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactName = (CheckBoxPreference) findPreference(Constants.PHONE_HIDE_CONTACT_NAME_ENABLED_KEY);
			hideContactName.setEnabled(!contactInfoDisplaySettingsEnabled);
			CheckBoxPreference hideContactNumber = (CheckBoxPreference) findPreference(Constants.PHONE_HIDE_CONTACT_NUMBER_ENABLED_KEY);
			hideContactNumber.setEnabled(!contactInfoDisplaySettingsEnabled);
		}catch(Exception ex){
			if (_debug) Log.e("MainPreferenceActivity.updatePhoneContactInfoSetting() ERROR: " + ex.toString());
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
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.v("MainPreferenceActivity.updateStatusBarNotificationRingtone() ERROR: " + ex.toString());
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
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						vibratePatternListPreference.setEnabled(false);
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibratePatternListPreference.setEnabled(true);
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						vibratePatternListPreference.setEnabled(false);
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibratePatternListPreference.setEnabled(true);
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						vibratePatternListPreference.setEnabled(false);
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibratePatternListPreference.setEnabled(true);
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						vibratePatternListPreference.setEnabled(false);
						vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						vibratePatternListPreference.setEnabled(true);
						vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.v("MainPreferenceActivity.updateStatusBarNotificationVibrate() ERROR: " + ex.toString());
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
			clearStatusBarNotificationsOnExitCheckBoxPreference.setEnabled(enabled);
		}catch(Exception ex){
			if (_debug) Log.v("MainPreferenceActivity.updateClearStatusBarNotifications() ERROR: " + ex.toString());
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
	
	//Reload Preference Activity
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

}