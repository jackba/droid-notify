package apps.droidnotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.TextView;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class DroidNotifyPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//================================================================================
    // Constants
    //================================================================================
	
	final String APP_ENABLED_KEY = "app_enabled_settings";
	final String NOTIFICATIONS_ENABLED_SETTINGS = "notifications_enabled_settings";
	final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	final String SMS_BUTTON_SETTINGS = "sms_button_settings";
	final String MMS_NOTIFICATIONS_ENABLED_KEY = "mms_notifications_enabled";
	final String MMS_BUTTON_SETTINGS = "mms_button_settings";
	final String MISSED_CALL_NOTIFICATIONS_ENABLED_KEY = "missed_call_notifications_enabled";
	final String MISSED_CALL_BUTTON_SETTINGS = "missed_call_button_settings";
	
	//================================================================================
    // Properties
    //================================================================================

	private Context _context;
	//private CheckBoxPreference _appEnabledCheckbox = null;
	private PreferenceScreen _notificationPreferenceScreen = null;
	//private CheckBoxPreference _smsEnabledCheckbox = null;
	private PreferenceScreen _smsButtonPreferenceScreen = null;
	//private CheckBoxPreference _mmsEnabledCheckbox = null;
	private PreferenceScreen _mmsButtonPreferenceScreen = null;
	//private CheckBoxPreference _missedCallEnabledCheckbox = null;
	private PreferenceScreen _missedCallButtonPreferenceScreen = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * 
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onCreate()");	    
	    addPreferencesFromResource(R.xml.preferences);
	    //Load the preference screens and items. 
	    //_appEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(APP_ENABLED_KEY);
	    _notificationPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(NOTIFICATIONS_ENABLED_SETTINGS);
	    //_smsEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(SMS_NOTIFICATIONS_ENABLED_KEY);
	    _smsButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(SMS_BUTTON_SETTINGS);
	    //_mmsEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(MMS_NOTIFICATIONS_ENABLED_KEY);
	    _mmsButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(MMS_BUTTON_SETTINGS);
	    //_missedCallEnabledCheckbox = (CheckBoxPreference)getPreferenceScreen().findPreference(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY);
	    _missedCallButtonPreferenceScreen = (PreferenceScreen)getPreferenceScreen().findPreference(MISSED_CALL_BUTTON_SETTINGS);
	    //Disable any preference screens depending on the current preference settings.
	    SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
	    updatePreferenceAccesibility(sharedPreferences, APP_ENABLED_KEY);
	    updatePreferenceAccesibility(sharedPreferences, SMS_NOTIFICATIONS_ENABLED_KEY);
	    updatePreferenceAccesibility(sharedPreferences, MMS_NOTIFICATIONS_ENABLED_KEY);
	    updatePreferenceAccesibility(sharedPreferences, MISSED_CALL_NOTIFICATIONS_ENABLED_KEY);
	    // Register the SharedPreferenceChanged listener.            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * 
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onResume()");
	    // Register the SharedPreferenceChanged listener.            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * 
	 */
    @Override
    protected void onPause() {
        super.onPause();
        if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onPause()");
        // Unregister the SharedPreferenceChanged listener.            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this); 
    }
	
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences
	 * @param key
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		updatePreferenceAccesibility(sharedPreferences, key);
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * 
	 * 
	 * @param sharedPreferences
	 * @param key
	 */
	private void updatePreferenceAccesibility(SharedPreferences sharedPreferences, String key){
		if (Log.getDebug()) Log.v("DroidNotifyPreferenceActivity.updatePreferenceAccesibility() Key: " + key);
        //App enable preference changed.
		if (key.equals(APP_ENABLED_KEY)) {
        	if(sharedPreferences.getBoolean(key, true)){
        		//App is enabled so enable all other preference items related to it.
        		_notificationPreferenceScreen.setEnabled(true);
        	}else{
        		//App is disabled so disable all other preference items related to it.
        		_notificationPreferenceScreen.setEnabled(false);
        	}
        }
        //SMS notifications enable preference changed.
		if (key.equals(SMS_NOTIFICATIONS_ENABLED_KEY)) {
        	if(sharedPreferences.getBoolean(key, true)){
        		//SMS notifications are enabled so enable all other preference items related to it.
        		_smsButtonPreferenceScreen.setEnabled(true);
        	}else{
        		//SMS notifications are disabled so disable all other preference items related to it.
        		_smsButtonPreferenceScreen.setEnabled(false);
        	}
        }
        //MMS notifications enable preference changed.
		if (key.equals(MMS_NOTIFICATIONS_ENABLED_KEY)) {
        	if(sharedPreferences.getBoolean(key, true)){
        		//MMS notifications are enabled so enable all other preference items related to it.
        		_mmsButtonPreferenceScreen.setEnabled(true);
        	}else{
        		//MMS notifications are disabled so disable all other preference items related to it.
        		_mmsButtonPreferenceScreen.setEnabled(false);
        	}
        }
        //Missed Call notifications enable preference changed.
		if (key.equals(MISSED_CALL_NOTIFICATIONS_ENABLED_KEY)) {
        	if(sharedPreferences.getBoolean(key, true)){
        		//Missed Call notifications are enabled so enable all other preference items related to it.
        		_missedCallButtonPreferenceScreen.setEnabled(true);
        	}else{
        		//Missed Call notifications are disabled so disable all other preference items related to it.
        		_missedCallButtonPreferenceScreen.setEnabled(false);
        	}
        }	
	}
	
}