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

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Constants;
import apps.droidnotify.facebook.FacebookAuthenticationActivity;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.linkedin.LinkedInAuthenticationActivity;
import apps.droidnotify.linkedin.LinkedInCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterAuthenticationActivity;
import apps.droidnotify.twitter.TwitterCommon;
import apps.droidnotify.common.Common;
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
    private boolean _appProVersion = false;
	
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
	    _context = MainPreferenceActivity.this;
	    _debug = Log.getDebug();
	    _appProVersion = Log.getAppProVersion();
	    if (_debug) Log.v("MainPreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    checkSystemDateTimeFormat();
	    addPreferencesFromResource(R.xml.preferences);
	    _appVersion = Common.getApplicationVersion(_context);
	    setupCustomPreferences();
	    runOnceCalendarAlarmManager();
	    setupRateAppPreference();
	    setupAppVersion(_appProVersion);
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
		if(key.equals(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY, false)){
				//Setup Calendar recurring alarm.
				CalendarCommon.startCalendarAlarmManager(_context, SystemClock.currentThreadTimeMillis());
			}else{
				//Cancel the Calendar recurring alarm.
				CalendarCommon.cancelCalendarAlarmManager(_context);
			}
		}else if(key.equals(Constants.CALENDAR_POLLING_FREQUENCY_KEY)){
			//The polling time for the calendars was changed. Run the alarm manager with the updated polling time.
			startCalendarAlarmManager(SystemClock.currentThreadTimeMillis());
		}else if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_SMS);
		}else if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_MMS);
		}else if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_PHONE);
		}else if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_CALENDAR);
		}else if(key.equals(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_TWITTER);
		}else if(key.equals(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_FACEBOOK);
		}else if(key.equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY)){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_K9);
		}else if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_SMS);
		}else if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_MMS);
		}else if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_PHONE);
		}else if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_CALENDAR);
		}else if(key.equals(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_TWITTER);
		}else if(key.equals(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_FACEBOOK);
		}else if(key.equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY)){
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_K9);
		}else if(key.equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY)){
			updateClearStatusBarNotifications();
		}else if(key.equals(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, false)){
				//Setup Twitter recurring alarm.
				checkTwitterAuthentication();
			}else{
				//Cancel the Twitter recurring alarm.
				TwitterCommon.cancelTwitterAlarmManager(_context);
			}
		}else if(key.equals(Constants.TWITTER_POLLING_FREQUENCY_KEY)){
			//The polling time for Twitter was changed. Run the alarm manager with the updated polling time.
			TwitterCommon.startTwitterAlarmManager(_context, SystemClock.currentThreadTimeMillis());
		}else if(key.equals(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, false)){
				//Setup Facebook recurring alarm.
				checkFacebookAuthentication();
			}else{
				//Cancel the Facebook recurring alarm.
				FacebookCommon.cancelFacebookAlarmManager(_context);
			}
		}else if(key.equals(Constants.FACEBOOK_POLLING_FREQUENCY_KEY)){
			//The polling time for Facebook was changed. Run the alarm manager with the updated polling time.
			FacebookCommon.startFacebookAlarmManager(_context, SystemClock.currentThreadTimeMillis());
		}else if(key.equals(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY)){
			if(_preferences.getBoolean(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY, false)){
				//Setup LinkedIn recurring alarm.
				checkLinkedInAuthentication();
			}else{
				//Cancel the LinkedIn recurring alarm.
				LinkedInCommon.cancelLinkedInAlarmManager(_context);
			}
		}else if(key.equals(Constants.LINKEDIN_POLLING_FREQUENCY_KEY)){
			//The polling time for LinkedIn was changed. Run the alarm manager with the updated polling time.
			LinkedInCommon.startLinkedInAlarmManager(_context, SystemClock.currentThreadTimeMillis());
		}else if(key.equals(Constants.DEBUG)){
			Log.setDebug(_preferences.getBoolean(Constants.DEBUG, false));
		}else if(key.equals(Constants.LANGUAGE_KEY)){
			reloadPreferenceActivity();
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
			case Constants.DIALOG_UPGRADE:{
				if (_debug) Log.v("MainPreferenceActivity.onCreateDialog() DIALOG_DONATE");
				LayoutInflater factory = getLayoutInflater();
		        final View upgradeToProView = factory.inflate(R.layout.upgrade_to_pro, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(upgradeToProView);
				builder.setIcon(R.drawable.ic_launcher_droidnotify);
				if(Log.getShowAndroidRateAppLink()){
					builder.setTitle(_context.getString(R.string.upgrade_to_droid_notify_pro_text));
		        }else if(Log.getShowAmazonRateAppLink()){
					builder.setTitle(_context.getString(R.string.upgrade_to_droid_notify_pro_text));
		        }else{
					builder.setTitle(_context.getString(R.string.donate_to_droid_notify_text));
		        }
				final AlertDialog alertDialog = builder.create();
				TextView contentTextView = (TextView) upgradeToProView.findViewById(R.id.content_text_view);
		        Button contentButton = (Button) upgradeToProView.findViewById(R.id.content_button);
		        if(Log.getShowAndroidRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
			        	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_ANDROID_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
			        	}
			        });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_direct_description_text));
		        }else if(Log.getShowAmazonRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
				    	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_AMAZON_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
				    	}
				    });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_direct_description_text));
		        }else{
		        	contentButton.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PAYPAL_URL));			    	
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
							startActivity(intent);
							alertDialog.dismiss();
						}
			        });
		        	contentButton.setText(_context.getString(R.string.donate_via_paypal_text));
			        contentTextView.setText(_context.getString(R.string.donate_description_text));
		        }
				return alertDialog;
			}
			case Constants.DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_TWITTER:{
				if (_debug) Log.v("MainPreferenceActivity.onCreateDialog() DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_TWITTER");
				LayoutInflater factory = getLayoutInflater();
		        final View upgradeToProView = factory.inflate(R.layout.upgrade_to_pro, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(upgradeToProView);
				builder.setIcon(R.drawable.twitter);
				builder.setTitle(_context.getString(R.string.upgrade_to_droid_notify_pro_text));
				final AlertDialog alertDialog = builder.create();
				TextView contentTextView = (TextView) upgradeToProView.findViewById(R.id.content_text_view);
		        Button contentButton = (Button) upgradeToProView.findViewById(R.id.content_button);
		        if(Log.getShowAndroidRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
			        	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_ANDROID_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
			        	}
			        });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_description_text));
		        }else if(Log.getShowAmazonRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
				    	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_AMAZON_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
				    	}
				    });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_description_text));
		        }else{
		        	contentButton.setVisibility(View.GONE);
			        contentTextView.setText(_context.getString(R.string.upgrade_no_market_description_text));
		        }
				return alertDialog;
			}
			case Constants.DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_FACEBOOK:{
				if (_debug) Log.v("MainPreferenceActivity.onCreateDialog() DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_FACEBOOK");
				LayoutInflater factory = getLayoutInflater();
		        final View upgradeToProView = factory.inflate(R.layout.upgrade_to_pro, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setView(upgradeToProView);
				builder.setIcon(R.drawable.facebook);
				builder.setTitle(_context.getString(R.string.upgrade_to_droid_notify_pro_text));
				final AlertDialog alertDialog = builder.create();
				TextView contentTextView = (TextView) upgradeToProView.findViewById(R.id.content_text_view);
		        Button contentButton = (Button) upgradeToProView.findViewById(R.id.content_button);
		        if(Log.getShowAndroidRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
			        	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_ANDROID_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
			        	}
			        });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_description_text));
		        }else if(Log.getShowAmazonRateAppLink()){
		        	contentButton.setOnClickListener(new OnClickListener(){
				    	public void onClick(View view) {
			        		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_PRO_AMAZON_URL));			    	
					    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
				    		startActivity(intent);
				    		alertDialog.dismiss();
				    	}
				    });
		        	contentButton.setText(_context.getString(R.string.upgrade_now_text));
			        contentTextView.setText(_context.getString(R.string.upgrade_description_text));
		        }else{
		        	contentButton.setVisibility(View.GONE);
			        contentTextView.setText(_context.getString(R.string.upgrade_no_market_description_text));
		        }
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
	 * Start the Calendar Alarm Manager.
	 * 
	 * @param alarmStartTime - The time to start the alarm.
	 */
	private void startCalendarAlarmManager(long alarmStartTime){
		if (_debug) Log.v("MainPreferenceActivity.startCalendarAlarmManager()");
		//Make sure that this user preference has been set and initialized.
		initUserCalendarsPreference();
		//Schedule the reading of the calendar events.
		CalendarCommon.startCalendarAlarmManager(_context, alarmStartTime);
	}
	
	/**
	 * This displays the EULA to the user the first time the app is run.
	 */
	private void runOnce(){
		if (_debug) Log.v("MainPreferenceActivity.runOnce()");
		if(!_appProVersion && _preferences.getBoolean(Constants.RUN_ONCE_EULA, true)) {
			try{
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putBoolean(Constants.RUN_ONCE_EULA, false);
				editor.commit();
				displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info, _context.getString(R.string.eula_text));
			}catch(Exception ex){
 	    		Log.e("MainPreferenceActivity.runOnceEula() EULA ERROR: " + ex.toString());
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
		    	if (_debug) Log.v("MainPreferenceActivity() Test Notifications Button Clicked()");
		    	Bundle bundle = new Bundle();
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_TEST);
		    	Intent intent = new Intent(_context, NotificationActivity.class);
		    	intent.putExtras(bundle);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Test Notifications Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_test_app_error), Toast.LENGTH_LONG).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Calendar Refresh Button
		Preference calendarRefreshPref = (Preference)findPreference(Constants.CALENDAR_REFRESH_KEY);
		calendarRefreshPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Calendar Refresh Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new calendarRefreshAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Calendar Refresh Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Quiet Time Button
		Preference quietTimePref = (Preference)findPreference(Constants.QUIET_TIME_BLACKOUT_PERIOD_SETTINGS_KEY);
		quietTimePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Quiet Time Button Clicked()");
		    	try{
		    		showQuietTimePeriodDialog();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Quiet Time Button ERROR: " + ex.toString());
	 	    		//Toast.makeText(_context, _context.getString(R.string.app_preference_quiet_time_error), Toast.LENGTH_LONG).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Clear Twitter Authentication Data Preference/Button
		Preference clearTwitterAuthenticationDataPref = (Preference)findPreference(Constants.TWITTER_CLEAR_AUTHENTICATION_DATA_KEY);
		clearTwitterAuthenticationDataPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Clear Twitter Authentication Data Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new clearTwitterAuthenticationDataAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Clear Twitter Authentication Data Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Clear Facebook Authentication Data Preference/Button
		Preference clearFacebookAuthenticationDataPref = (Preference)findPreference(Constants.FACEBOOK_CLEAR_AUTHENTICATION_DATA_KEY);
		clearFacebookAuthenticationDataPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Clear Facebook Authentication Data Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new clearFacebookAuthenticationDataAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Clear Facebook Authentication Data Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
//		//Clear LinkedIn Authentication Data Preference/Button
//		Preference clearLinkedInAuthenticationDataPref = (Preference)findPreference(Constants.LINKEDIN_CLEAR_AUTHENTICATION_DATA_KEY);
//		clearLinkedInAuthenticationDataPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//        	public boolean onPreferenceClick(Preference preference) {
//		    	if (_debug) Log.v("MainPreferenceActivity() Clear LinkedIn Authentication Data Button Clicked()");
//		    	try{
//			    	//Run this process in the background in an AsyncTask.
//			    	new clearLinkedInAuthenticationDataAsyncTask().execute();
//		    	}catch(Exception ex){
//	 	    		Log.e("MainPreferenceActivity() Clear LinkedIn Authentication Data Button ERROR: " + ex.toString());
//	 	    		return false;
//		    	}
//	            return true;
//           }
//		});
		//Rate This App Preference/Button
		Preference rateAppPref = (Preference)findPreference(Constants.PREFERENCE_RATE_APP_KEY);
		rateAppPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Rate This App Button Clicked()");
		    	try{
			    	String rateAppURL = "";
			    	if(Log.getShowAndroidRateAppLink()){
			    		if(_appProVersion){
			    			rateAppURL = Constants.APP_PRO_ANDROID_URL;
			    		}else{
			    			rateAppURL = Constants.APP_ANDROID_URL;
			    		}
			    	}else if(Log.getShowAmazonRateAppLink()){
			    		if(_appProVersion){
			    			rateAppURL = Constants.APP_PRO_AMAZON_URL;
			    		}else{
			    			rateAppURL = Constants.APP_AMAZON_URL;
			    		}
			    	}else{
			    		rateAppURL = "";
			    	}
			    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rateAppURL));			    	
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Rate This App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_LONG).show();
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Upgrade Preference/Button
		Preference upgradePreference = (Preference)findPreference(Constants.UPGRADE_TO_PRO_PREFERENCE_KEY);
		upgradePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Upgrade Button Clicked()");
		    	try{
		    		showDialog(Constants.DIALOG_UPGRADE);
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Upgrade Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Email Developer Preference/Button
		Preference emailDeveloperPref = (Preference)findPreference("email_developer");
		emailDeveloperPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Email Developer Button Clicked()");
		    	try{
			    	Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	sendEmailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			    	sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Droid Notify App Feedback");
		    		startActivity(sendEmailIntent);
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Email Developer Button ERROR: " + ex.toString());
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
		    	if (_debug) Log.v("MainPreferenceActivity() About Button Clicked()");
		    	if(Log.getAppProVersion()){
		    		return displayHTMLAlertDialog(_context.getString(R.string.app_name_pro_formatted_version, _appVersion), R.drawable.ic_launcher_droidnotify, _context.getString(R.string.preference_about_text) + _context.getString(R.string.preference_translated_by_text) + _context.getString(R.string.preference_copyright_text));
		    	}else{
		    		return displayHTMLAlertDialog(_context.getString(R.string.app_name_basic_formatted_version, _appVersion), R.drawable.ic_launcher_droidnotify, _context.getString(R.string.preference_about_text) + _context.getString(R.string.preference_translated_by_text) + _context.getString(R.string.preference_copyright_text));
		    	}
        	}
		});
		//License Preference/Button
		Preference licensePreferencesPref = (Preference)findPreference("application_license");
		licensePreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() License Button Clicked()");
	            return displayHTMLAlertDialog(_context.getString(R.string.app_license),R.drawable.ic_dialog_info,_context.getString(R.string.eula_text));
           }
		});
		//Export Preferences Preference/Button
		Preference exportPreferencesPref = (Preference)findPreference("export_preferences");
		exportPreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Export Preferences Button Clicked()");
		    	try{
			    	//Run this process in the background in an AsyncTask.
			    	new exportPreferencesAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Export Preferences Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Import Preferences Preference/Button
		Preference importPreferencesPref = (Preference)findPreference("import_preferences");
		importPreferencesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Import Preferences Button Clicked()");
		    	try{
		    		//Unregister SharedPreferenceChange Listener.
		    		_preferences.unregisterOnSharedPreferenceChangeListener(MainPreferenceActivity.this);
			    	//Run this process in the background in an AsyncTask.
			    	new importPreferencesAsyncTask().execute();
		    	}catch(Exception ex){
	 	    		Log.e("MainPreferenceActivity() Import Preferences Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
	            return true;
           }
		});
		//Send Debug Logs Preference/Button
		Preference sendDebugLogsPreference = (Preference)findPreference("send_debug_logs");
		sendDebugLogsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("MainPreferenceActivity() Send Debug Logs Button Clicked()");
		    	Log.collectAndSendLog(_context);
	            return true;
           }
		});
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
			PreferenceCategory appFeedbackPreferenceCategory = (PreferenceCategory) findPreference(Constants.PREFERENCE_CATEGORY_APP_FEEDBACK_KEY);
			Preference rateAppPreference = (Preference) findPreference(Constants.PREFERENCE_RATE_APP_KEY);
			appFeedbackPreferenceCategory.removePreference(rateAppPreference);
		}
	}
	
	/**
	 * Set up the app vased on whether or not this is the Pro Version or not.
	 * 
	 * @param appProVersion
	 */
	private void setupAppVersion(boolean appProVersion){
		if (_debug) Log.v("MainPreferenceActivity.setupAppVersion()");
		PreferenceScreen mainPreferences = this.getPreferenceScreen();
		PreferenceScreen advancedPreferences = (PreferenceScreen) findPreference(Constants.ADVANCED_PREFERENCE_SCREEN_KEY);
		Preference upgradeToProPreference = (Preference) findPreference(Constants.UPGRADE_TO_PRO_PREFERENCE_KEY);		
		PreferenceCategory appLicensePreferenceCategory = (PreferenceCategory) findPreference(Constants.PREFERENCE_CATEGORY_APP_LICENSE_KEY);
		PreferenceCategory appFeedbackPreferenceCategory = (PreferenceCategory) findPreference(Constants.PREFERENCE_CATEGORY_APP_FEEDBACK_KEY);
		PreferenceCategory advancedTwitterSettingsPreferenceCategory = (PreferenceCategory) findPreference(Constants.TWITTER_ADVANCED_SETTINGS_CATEGORY_KEY);
		PreferenceCategory advancedFacebookSettingsPreferenceCategory = (PreferenceCategory) findPreference(Constants.FACEBOOK_ADVANCED_SETTINGS_CATEGORY_KEY);
//		PreferenceCategory advancedLinkedInSettingsPreferenceCategory = (PreferenceCategory) findPreference(Constants.LINKEDIN_ADVANCED_SETTINGS_CATEGORY_KEY);
		//Twitter
		PreferenceCategory twitterNotificationPreferenceCategory = (PreferenceCategory) findPreference(Constants.TWITTER_PRO_PREFERENCE_CATEGORY_KEY);
		Preference twitterProPlaceholderPreference = (Preference) findPreference(Constants.TWITTER_PRO_PLACEHOLDER_PREFERENCE_KEY);
		PreferenceScreen twitterProPreferenceScreen = (PreferenceScreen) findPreference(Constants.TWITTER_PRO_PREFERENCE_SCREEN_KEY);
		//Facebook
		PreferenceCategory facebookNotificationPreferenceCategory = (PreferenceCategory) findPreference(Constants.FACEBOOK_PRO_PREFERENCE_CATEGORY_KEY);
		Preference facebookProPlaceholderPreference = (Preference) findPreference(Constants.FACEBOOK_PRO_PLACEHOLDER_PREFERENCE_KEY);
		PreferenceScreen facebookProPreferenceScreen = (PreferenceScreen) findPreference(Constants.FACEBOOK_PRO_PREFERENCE_SCREEN_KEY);
		//LinkedIn
//		PreferenceCategory linkedInNotificationPreferenceCategory = (PreferenceCategory) findPreference(Constants.LINKEDIN_PRO_PREFERENCE_CATEGORY_KEY);
//		Preference linkedInProPlaceholderPreference = (Preference) findPreference(Constants.LINKEDIN_PRO_PLACEHOLDER_PREFERENCE_KEY);
//		PreferenceScreen linkedInProPreferenceScreen = (PreferenceScreen) findPreference(Constants.LINKEDIN_PRO_PREFERENCE_SCREEN_KEY);
		if(appProVersion){
			//Remove the Twitter placeholder preference category.
			twitterNotificationPreferenceCategory.removePreference(twitterProPlaceholderPreference);
			//Remove the Facebook placeholder preference category.
			facebookNotificationPreferenceCategory.removePreference(facebookProPlaceholderPreference);
			//Remove the LinkedIn placeholder preference category.
//			linkedInNotificationPreferenceCategory.removePreference(linkedInProPlaceholderPreference);
			//Remove the Upgrade button.
			appFeedbackPreferenceCategory.removePreference(upgradeToProPreference);
			//Remove the Liscense button.
			mainPreferences.removePreference(appLicensePreferenceCategory);
		}else{
			//Remove the Twitter preference preference category.
			twitterNotificationPreferenceCategory.removePreference(twitterProPreferenceScreen);
			advancedPreferences.removePreference(advancedTwitterSettingsPreferenceCategory);
			//Setup the Twitter placeholder preference button.
			twitterProPlaceholderPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	public boolean onPreferenceClick(Preference preference) {
			    	if (_debug) Log.v("Twitter Pro Placeholder Button Clicked()");
			    	try{
				    	//Display Pro Version Only Popup
			    		showDialog(Constants.DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_TWITTER);
			    	}catch(Exception ex){
		 	    		Log.e("MainPreferenceActivity() Twitter Pro Placeholder Button ERROR: " + ex.toString());
		 	    		return false;
			    	}
		            return true;
	           }
			});
			//Remove the Facebook preference preference category.
			facebookNotificationPreferenceCategory.removePreference(facebookProPreferenceScreen);
			advancedPreferences.removePreference(advancedFacebookSettingsPreferenceCategory);
			//Setup the Facebook placeholder preference button.
			facebookProPlaceholderPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	        	public boolean onPreferenceClick(Preference preference) {
			    	if (_debug) Log.v("Facebook Pro Placeholder Button Clicked()");
			    	try{
				    	//Display Pro Version Only Popup
			    		showDialog(Constants.DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_FACEBOOK);
			    	}catch(Exception ex){
		 	    		Log.e("MainPreferenceActivity() Facebook Pro Placeholder Button ERROR: " + ex.toString());
		 	    		return false;
			    	}
		            return true;
	           }
			});
//			//Remove the LinkedIn preference preference category.
//			linkedInNotificationPreferenceCategory.removePreference(linkedInProPreferenceScreen);
//			advancedPreferences.removePreference(advancedLinkedInSettingsPreferenceCategory);
//			//Setup the LinkedIn placeholder preference button.
//			linkedInProPlaceholderPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//	        	public boolean onPreferenceClick(Preference preference) {
//			    	if (_debug) Log.v("LinkedIn Pro Placeholder Button Clicked()");
//			    	try{
//				    	//Display Pro Version Only Popup
//			    		showDialog(Constants.DIALOG_FEATURE_AVAILABLE_WITH_PRO_VERSION_LINKEDIN);
//			    	}catch(Exception ex){
//		 	    		Log.e("MainPreferenceActivity() LinkedIn Pro Placeholder Button ERROR: " + ex.toString());
//		 	    		return false;
//			    	}
//		            return true;
//	           }
//			});
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
			Log.e("MainPreferenceActivity.exportApplicationPreferences() External Storage Read Only State");
		    return false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			Log.e("MainPreferenceActivity.exportApplicationPreferences() External Storage Can't Write Or Read State");
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
			Log.e("MainPreferenceActivity.exportApplicationPreferences() Wrtie File ERROR: " + ex.toString());
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
			Log.e("MainPreferenceActivity.importApplicationPreferences() External Storage Can't Write Or Read State");
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
    		Log.e("MainPreferenceActivity.importApplicationPreferences() ERROR: " + ex.toString());
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
    	String availableCalendarsInfo = CalendarCommon.getAvailableCalendars(_context);
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
	    		Log.e("MainPreferenceActivity.displayHTMLAlertDialog() ERROR: " + ex.toString());
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
			Log.e("MainPreferenceActivity.setupImportPreferences() ERROR: " + ex.toString());
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
	 * Initialize the state of some preference items that have multiple dependencies.
	 */
	private void initPreferencesStates(){
		if (_debug) Log.v("MainPreferenceActivity.initPreferencesStates()");
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_SMS);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_MMS);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_PHONE);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_CALENDAR);
		updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_K9);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_SMS);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_MMS);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_PHONE);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_CALENDAR);
		updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_K9);
		updateClearStatusBarNotifications();
		if(_appProVersion){
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_TWITTER);
			updateStatusBarNotificationVibrate(Constants.NOTIFICATION_TYPE_FACEBOOK);
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_TWITTER);
			updateStatusBarNotificationRingtone(Constants.NOTIFICATION_TYPE_FACEBOOK);
			updateTwitterPreferences();
			updateFacebookPreferences();
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
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					CheckBoxPreference  vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY);
					if(_preferences.getString(Constants.K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_SILENT_VALUE)){
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
			}
		}catch(Exception ex){
			Log.e("MainPreferenceActivity.updateStatusBarNotificationRingtone() ERROR: " + ex.toString());
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
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(false);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(false);
					}else{
						if(vibratePatternListPreference != null) vibratePatternListPreference.setEnabled(true);
						if(vibrateInCallCheckBoxPreference != null) vibrateInCallCheckBoxPreference.setEnabled(true);
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					ListPreference vibratePatternListPreference = (ListPreference) findPreference(Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY);
					CheckBoxPreference vibrateInCallCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY);
					if(_preferences.getString(Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_DEFAULT).equals(Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_NEVER_VALUE)){
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
			Log.e("MainPreferenceActivity.updateStatusBarNotificationVibrate() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Updates the Clear Status Bar Notification preference based on the Status Bar Notification Settings.
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
			if(_preferences.getBoolean(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, false)){
				enabled = true;
			}	
			if(_preferences.getBoolean(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, false)){
				enabled = true;
			}	
			if(_preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY, true) && _preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				enabled = true;
			}
			CheckBoxPreference clearStatusBarNotificationsOnExitCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.CLEAR_STATUS_BAR_NOTIFICATIONS_ON_EXIT_KEY);
			if(clearStatusBarNotificationsOnExitCheckBoxPreference != null) clearStatusBarNotificationsOnExitCheckBoxPreference.setEnabled(enabled);
		}catch(Exception ex){
			Log.e("MainPreferenceActivity.updateClearStatusBarNotifications() ERROR: " + ex.toString());
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
 	    		Log.e("MainPreferenceActivity.checkSystemDateTimeFormat() ERROR: " + ex.toString());
	    	}
		}		
	}
	
	/**
	 * Check if the user has already authorized us to use access his Twitter account.
	 * Launch authorization activity if not.
	 */
	private void checkTwitterAuthentication(){
		if (_debug) Log.v("MainPreferenceActivity.checkTwitterAuthentication()");		
		if(Common.isOnline(_context)){
			//Setup User Twitter Account
		    Intent intent = new Intent(_context, TwitterAuthenticationActivity.class);
		    startActivity(intent);
		}else{
			Toast.makeText(_context, _context.getString(R.string.not_online_error), Toast.LENGTH_LONG).show();
			CheckBoxPreference twitterEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY);
			if(twitterEnabledCheckBoxPreference != null) twitterEnabledCheckBoxPreference.setChecked(false);
		}
	}
	
	/**
	 * Check if the user has already authorized us to use access his Facebook account.
	 * Launch authorization activity if not.
	 */
	private void checkFacebookAuthentication(){
		if (_debug) Log.v("MainPreferenceActivity.checkFacebookAuthentication()");
		if(Common.isOnline(_context)){
			//Setup User Facebook Account
		    Intent intent = new Intent(_context, FacebookAuthenticationActivity.class);
		    startActivity(intent);
		}else{
			Toast.makeText(_context, _context.getString(R.string.not_online_error), Toast.LENGTH_LONG).show();
			CheckBoxPreference facebookEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY);
			if(facebookEnabledCheckBoxPreference != null) facebookEnabledCheckBoxPreference.setChecked(false);
		}
	}
	
	/**
	 * Check if the user has already authorized us to use access his LinkedIn account.
	 * Launch authorization activity if not.
	 */
	private void checkLinkedInAuthentication(){
		if (_debug) Log.v("MainPreferenceActivity.checkLinkedInAuthentication()");
		if(Common.isOnline(_context)){
			//Setup User LinkedInAccount
		    Intent intent = new Intent(_context, LinkedInAuthenticationActivity.class);
		    startActivity(intent);
		}else{
			Toast.makeText(_context, _context.getString(R.string.not_online_error), Toast.LENGTH_LONG).show();
			//CheckBoxPreference linkedInEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY);
			//if(linkedInEnabledCheckBoxPreference != null) linkedInEnabledCheckBoxPreference.setChecked(false);
		}
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
			Log.e("MainPreferenceActivity.reloadPreferenceActivity() ERROR: " + ex.toString());
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
	
	/**
	 * Update the Twitter preferences.
	 */
	private void updateTwitterPreferences(){
		if (_debug) Log.v("MainPreferenceActivity.updateTwitterPreferences()");
		if(TwitterCommon.isTwitterAuthenticated(_context)){
			//Do Nothing
		}else{
			CheckBoxPreference twitterEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY);
			if(twitterEnabledCheckBoxPreference != null) twitterEnabledCheckBoxPreference.setChecked(false);
			//Cancel any Twitter alarms that are still out there.
			TwitterCommon.cancelTwitterAlarmManager(_context);
		}
	}

	/**
	 * Update the Facebook preferences.
	 */
	private void updateFacebookPreferences(){
		if (_debug) Log.v("MainPreferenceActivity.updateFacebookPreferences()");
		if(FacebookCommon.isFacebookAuthenticated(_context)){
			//Do Nothing
		}else{
			CheckBoxPreference facebookEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY);
			if(facebookEnabledCheckBoxPreference != null) facebookEnabledCheckBoxPreference.setChecked(false);
			//Cancel any Facebook alarms that are still out there.
			FacebookCommon.cancelFacebookAlarmManager(_context);
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
			if (_debug) Log.v("MainPreferenceActivity.calendarRefreshAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.reading_calendar_data), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.calendarRefreshAsyncTask.doInBackground()");
			CalendarCommon.readCalendars(_context);
	    	return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res) {
			if (_debug) Log.v("MainPreferenceActivity.calendarRefreshAsyncTask.onPostExecute()");
	        dialog.dismiss();
	    	Toast.makeText(_context, _context.getString(R.string.calendar_data_refreshed), Toast.LENGTH_LONG).show();
	    }
	}
		
	/**
	 * Clear the Twitter authentication data as a background task.
	 * 
	 * @author Camille Sévigny
	 */
	private class clearTwitterAuthenticationDataAsyncTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("MainPreferenceActivity.clearTwitterAuthenticationDataAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.reset_data), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.clearTwitterAuthenticationDataAsyncTask.doInBackground()");
			SharedPreferences.Editor editor = _preferences.edit();
			editor.putString(Constants.TWITTER_OAUTH_TOKEN, null);
			editor.putString(Constants.TWITTER_OAUTH_TOKEN_SECRET, null);
			editor.commit();
            CheckBoxPreference twitterEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY);
			if(twitterEnabledCheckBoxPreference != null) twitterEnabledCheckBoxPreference.setChecked(false);
	    	return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res) {
			if (_debug) Log.v("MainPreferenceActivity.clearTwitterAuthenticationDataAsyncTask.onPostExecute()");
	        dialog.dismiss();
	    	Toast.makeText(_context, _context.getString(R.string.twitter_authentication_data_cleared), Toast.LENGTH_LONG).show();
	    }
	}
	
	/**
	 * Clear the Facebook authentication data as a background task.
	 * 
	 * @author Camille Sévigny
	 */
	private class clearFacebookAuthenticationDataAsyncTask extends AsyncTask<Void, Void, Void> {
		//ProgressDialog to display while the task is running.
		private ProgressDialog dialog;
		/**
		 * Setup the Progress Dialog.
		 */
	    protected void onPreExecute() {
			if (_debug) Log.v("MainPreferenceActivity.clearFacebookAuthenticationDataAsyncTask.onPreExecute()");
	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.reset_data), true);
	    }
	    /**
	     * Do this work in the background.
	     * 
	     * @param params
	     */
	    protected Void doInBackground(Void... params) {
			if (_debug) Log.v("MainPreferenceActivity.clearFacebookAuthenticationDataAsyncTask.doInBackground()");
			SharedPreferences.Editor editor = _preferences.edit();
            editor.putString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
            editor.putLong(Constants.FACEBOOK_ACCESS_EXPIRES_KEY, 0);
            editor.commit();
            CheckBoxPreference facebookEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY);
			if(facebookEnabledCheckBoxPreference != null) facebookEnabledCheckBoxPreference.setChecked(false);
	    	return null;
	    }
	    /**
	     * Stop the Progress Dialog and do any post background work.
	     * 
	     * @param result
	     */
	    protected void onPostExecute(Void res) {
			if (_debug) Log.v("MainPreferenceActivity.clearFacebookAuthenticationDataAsyncTask.onPostExecute()");
	        dialog.dismiss();
	    	Toast.makeText(_context, _context.getString(R.string.facebook_authentication_data_cleared), Toast.LENGTH_LONG).show();
	    }
	}
	
//	/**
//	 * Clear the LinkedIn authentication data as a background task.
//	 * 
//	 * @author Camille Sévigny
//	 */
//	private class clearLinkedInAuthenticationDataAsyncTask extends AsyncTask<Void, Void, Void> {
//		//ProgressDialog to display while the task is running.
//		private ProgressDialog dialog;
//		/**
//		 * Setup the Progress Dialog.
//		 */
//	    protected void onPreExecute() {
//			if (_debug) Log.v("MainPreferenceActivity.clearLinkedInAuthenticationDataAsyncTask.onPreExecute()");
//	        dialog = ProgressDialog.show(MainPreferenceActivity.this, "", _context.getString(R.string.reset_data), true);
//	    }
//	    /**
//	     * Do this work in the background.
//	     * 
//	     * @param params
//	     */
//	    protected Void doInBackground(Void... params) {
//			if (_debug) Log.v("MainPreferenceActivity.clearLinkedInAuthenticationDataAsyncTask.doInBackground()");
//			SharedPreferences.Editor editor = _preferences.edit();
//			editor.putString(Constants.LINKEDIN_OAUTH_TOKEN, null);
//			editor.putString(Constants.LINKEDIN_OAUTH_TOKEN_SECRET, null);
//			editor.commit();
//            CheckBoxPreference linkedInEnabledCheckBoxPreference = (CheckBoxPreference) findPreference(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY);
//			if(linkedInEnabledCheckBoxPreference != null) linkedInEnabledCheckBoxPreference.setChecked(false);
//	    	return null;
//	    }
//	    /**
//	     * Stop the Progress Dialog and do any post background work.
//	     * 
//	     * @param result
//	     */
//	    protected void onPostExecute(Void res) {
//			if (_debug) Log.v("MainPreferenceActivity.clearLinkedInAuthenticationDataAsyncTask.onPostExecute()");
//	        dialog.dismiss();
//	    	Toast.makeText(_context, _context.getString(R.string.linkedin_authentication_data_cleared), Toast.LENGTH_LONG).show();
//	    }
//	}
	
}