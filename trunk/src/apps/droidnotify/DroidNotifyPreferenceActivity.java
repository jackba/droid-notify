package apps.droidnotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

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
//	//private CheckBoxPreference _appEnabledCheckbox = null;
//	private PreferenceScreen _notificationPreferenceScreen = null;
//	//private CheckBoxPreference _smsEnabledCheckbox = null;
//	private PreferenceScreen _smsButtonPreferenceScreen = null;
//	//private CheckBoxPreference _mmsEnabledCheckbox = null;
//	private PreferenceScreen _mmsButtonPreferenceScreen = null;
//	//private CheckBoxPreference _missedCallEnabledCheckbox = null;
//	private PreferenceScreen _missedCallButtonPreferenceScreen = null;
//	private CheckBoxPreference _screenDimCheckBoxPreference = null; 

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
	    addPreferencesFromResource(R.xml.preferences);
//	    //Load the preference screens and items. 
//	    //_appEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(APP_ENABLED_KEY);
//	    _notificationPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(NOTIFICATIONS_ENABLED_SETTINGS);
//	    //_smsEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(SMS_NOTIFICATIONS_ENABLED_KEY);
//	    _smsButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(SMS_BUTTON_SETTINGS);
//	    //_mmsEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(MMS_NOTIFICATIONS_ENABLED_KEY);
//	    _mmsButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(MMS_BUTTON_SETTINGS);
//	    //_missedCallEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY);
//	    _missedCallButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(MISSED_CALL_BUTTON_SETTINGS);
//	    _screenDimCheckBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference(SCREEN_DIM_ENABLED_KEY);
//	    //Disable any preference screens depending on the current preference settings.
//	    SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//	    updatePreferenceAccesibility(sharedPreferences, APP_ENABLED_KEY);
//	    updatePreferenceAccesibility(sharedPreferences, SMS_NOTIFICATIONS_ENABLED_KEY);
//	    updatePreferenceAccesibility(sharedPreferences, MMS_NOTIFICATIONS_ENABLED_KEY);
//	    updatePreferenceAccesibility(sharedPreferences, MISSED_CALL_NOTIFICATIONS_ENABLED_KEY);
//	    // Register the SharedPreferenceChanged listener.            
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
	    // Register the SharedPreferenceChanged listener.            
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onPause()");
        // Unregister the SharedPreferenceChanged listener.            
//        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); 
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
//		updatePreferenceAccesibility(sharedPreferences, key);
	}
//	
//	//================================================================================
//	// Private Methods
//	//================================================================================
//	
//	/**
//	 * Updates the preference screens to disable or enable certain options based on the current preference settings.
//	 * 
//	 * @param sharedPreferences
//	 * @param key
//	 */
//	private void updatePreferenceAccesibility(SharedPreferences sharedPreferences, String key){
//		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.updatePreferenceAccesibility() Key: " + key);
//	
//	}
	
}