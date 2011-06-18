package apps.droidnotify;

import java.io.File;
import java.io.FileOutputStream;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
	
	//Google Market URL
	private final String RATE_APP_ANDROID_URL = "http://market.android.com/details?id=apps.droidnotify";
	//Amazon Appstore URL
	private final String RATE_APP_AMAZON_URL = "http://www.amazon.com/gp/mas/dl/android?p=apps.droidnotify";
	
	private final String APP_ENABLED_KEY = "app_enabled";
	private final String CALENDAR_NOTIFICATIONS_ENABLED_KEY = "calendar_notifications_enabled";
	private final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";
	
	private final int NOTIFICATION_TYPE_TEST = -1;
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context;
    private boolean _debug;
    private boolean _debugCalendar;

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
		if (_debug) Log.v("DroidNotifyPreferenceActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context - Application Context.
	 */
	public Context getContext() {
		if (_debug) Log.v("DroidNotifyPreferenceActivity.getContext()");
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
	    _debug = Log.getDebug();
	    _debugCalendar = Log.getDebugCalendar();
	    if (_debug) Log.v("DroidNotifyPreferenceActivity.onCreate()");
	    Context context = DroidNotifyPreferenceActivity.this;
	    setContext(context);
	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!preferences.getBoolean(LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    addPreferencesFromResource(R.xml.preferences);
	    setupCustomPreferences();
	    runOnceAlarmManager();
	    runOnceEula();
	    setupAppDebugMode(_debug);
	    setupRateAppPreference();
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
	    if (_debug) Log.v("DroidNotifyPreferenceActivity.onResume()");
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (_debug) Log.v("DroidNotifyPreferenceActivity.onPause()");
    }
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("DroidNotifyPreferenceActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("DroidNotifyPreferenceActivity.onDestroy()");
	}
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Starts the main AlarmManager that will check the users calendar for events.
	 * This should run only once when the application is installed.
	 */
	private void runOnceAlarmManager(){
		if (_debug) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		//Read preferences and exit if app is disabled.
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (_debug) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if calendar notifications are disabled.
	    if(!preferences.getBoolean(CALENDAR_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("DroidNotifyPreferenceActivity.runOnceAlarmManager() Calendar Notifications Disabled. Exiting... ");
			return;
		}
		boolean runOnce = preferences.getBoolean("runOnce", true);
		if(runOnce || _debugCalendar) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("runOnce", false);
			editor.commit();
			//Schedule the reading of the calendar events.
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			//--------------------------------
			//Set alarm to go off 30 seconds from the current time.
			//This line of code is for testing.
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (30 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
			//--------------------------------
			//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + (5 * 60 * 1000), AlarmManager.INTERVAL_DAY, pendingIntent);
       }
	}
	
	/**
	 * This displays the EULA to the user the first time the app is run.
	 */
	private void runOnceEula(){
		if (_debug) Log.v("DroidNotifyPreferenceActivity.runOnceEula()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean runOnceEula = preferences.getBoolean("runOnceEula", true);
		if(runOnceEula) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("runOnceEula", false);
			editor.commit();
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setIcon(R.drawable.ic_dialog_info);
			alertDialog.setTitle(R.string.app_license);
			alertDialog.setMessage(R.string.eula_text);
		    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialogInterface, int id) {
		    		//Action on dialog close. Do nothing.
		       }
		     });
		    alertDialog.show();
		}
	}
	
	/**
	 * Setup the custom Preference buttons.
	 */
	private void setupCustomPreferences(){
		if (_debug) Log.v("DroidNotifyPreferenceActivity.setupCustomPreferences()");
		//Test Notifications Preference/Button
		Preference testAppPref = (Preference)findPreference("test_app");
		testAppPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	if (_debug) Log.v("Test Notifications Button Clicked()");
		    	Context context = getContext();
		    	Bundle bundle = new Bundle();
				bundle.putInt("notificationType", NOTIFICATION_TYPE_TEST);
		    	Intent intent = new Intent(context, NotificationActivity.class);
		    	intent.putExtras(bundle);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("DroidNotifyPreferenceActivity.setupCustomPreferences() Test Notifications Button ERROR: " + ex.toString());
	 	    		Toast.makeText(context, context.getString(R.string.app_android_test_app_error), Toast.LENGTH_SHORT).show();
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
		    	Context context = getContext();
		    	Intent intent = new Intent(Intent.ACTION_VIEW);
		    	//Direct To Market App
		    	//intent.setData(Uri.parse("market://details?id=apps.droidnotify"));
		    	//URL of website. Turns out that this will prompt the user to choose Market or Web.
		    	//This is preferred as a choice is always better.
		    	String rateAppURL = "";
		    	if(Log.getShowAndroidRateAppLink()) rateAppURL = RATE_APP_ANDROID_URL;
		    	if(Log.getShowAmazonRateAppLink()) rateAppURL = RATE_APP_AMAZON_URL;
		    	intent.setData(Uri.parse(rateAppURL));
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("DroidNotifyPreferenceActivity.setupCustomPreferences() Rate This App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(context, context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_SHORT).show();
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
		    	Context context = getContext();
		    	Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		    	intent.setType("plain/text");
		    	intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "droidnotify@gmail.com"});
		    	intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Droid Notify App Feedback");
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("DroidNotifyPreferenceActivity.setupCustomPreferences() Email Developer Button ERROR: " + ex.toString());
	 	    		Toast.makeText(context, context.getString(R.string.app_android_email_app_error), Toast.LENGTH_SHORT).show();
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
		    	Context context = getContext();
		    	Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		    	intent.setType("plain/text");
		    	intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "droidnotify@gmail.com"});
		    	intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Droid Notify App Logs");
		    	intent.putExtra(android.content.Intent.EXTRA_TEXT, "What went wrong? What is the reason for emailing the log files: ");
		    	File logFileV = new File("sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt");
		    	File logFileD = new File("sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt");
		    	File logFileI = new File("sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt");
		    	File logFileW = new File("sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt");
		    	File logFileE = new File("sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt");
		    	if(logFileV.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt"));
		    	if(logFileD.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt"));
		    	if(logFileI.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt"));
		    	if(logFileW.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt"));
		    	if(logFileE.exists()) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt"));
		    	try{
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		if (_debug) Log.e("DroidNotifyPreferenceActivity.setupCustomPreferences() Email Developer Logs Button ERROR: " + ex.toString());
	 	    		Toast.makeText(context, context.getString(R.string.app_android_email_app_error), Toast.LENGTH_SHORT).show();
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
		    	clearDeveloperLogs();
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
		if (_debug) Log.v("DroidNotifyPreferenceActivity.setupAppDebugMode()");
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
		if (_debug) Log.v("DroidNotifyPreferenceActivity.setupRateLink()");
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
	 * Clear the developer logs on the SD card.
	 */
	private void clearDeveloperLogs(){
		if (_debug) Log.v("DroidNotifyPreferenceActivity.clearDeveloperLogs()");
		File logFileV = new File("sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt");
    	File logFileD = new File("sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt");
    	File logFileI = new File("sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt");
    	File logFileW = new File("sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt");
    	File logFileE = new File("sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt");
    	if(logFileV.exists()){
    		try{
    			new FileOutputStream("sdcard/Droid Notify/Logs/V/DroidNotifyLog.txt").close();
    		}catch (Exception ex){
    			if (_debug) Log.e("DroidNotifyPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
			}
    	}
    	if(logFileD.exists()){
    		try{
    			new FileOutputStream("sdcard/Droid Notify/Logs/D/DroidNotifyLog.txt").close();
    		}catch (Exception ex){
    			if (_debug) Log.e("DroidNotifyPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
			}
    	}
    	if(logFileI.exists()){
    		try{
    			new FileOutputStream("sdcard/Droid Notify/Logs/I/DroidNotifyLog.txt").close();
    		}catch (Exception ex){
    			if (_debug) Log.e("DroidNotifyPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
			}
    	}
    	if(logFileW.exists()){
    		try{
    			new FileOutputStream("sdcard/Droid Notify/Logs/W/DroidNotifyLog.txt").close();
    		}catch (Exception ex){
    			if (_debug) Log.e("DroidNotifyPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
			}
    	}
    	if(logFileE.exists()){
    		try{
    			new FileOutputStream("sdcard/Droid Notify/Logs/E/DroidNotifyLog.txt").close();
    		}catch (Exception ex){
    			if (_debug) Log.e("DroidNotifyPreferenceActivity.clearDeveloperLogs() ERROR: " + ex.toString());
			}
    	}
    	Toast.makeText(getContext(), "The application logs have been cleared.", Toast.LENGTH_SHORT).show();
	}
	
}