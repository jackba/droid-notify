package apps.droidnotify.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.common.Constants;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "Debug" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class DebugPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
    private SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
    /**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (_debug) Log.v("DebugPreferenceActivity.onSharedPreferenceChanged() Key: " + key);
		if(key.equals(Constants.DEBUG)){
			Log.setDebug(_preferences.getBoolean(Constants.DEBUG, false));
		}
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("DebugPreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    this.addPreferencesFromResource(R.xml.debug_preferences);
	    this.setContentView(R.layout.debug_preferences);
	    setupCustomPreferences();
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    if(_debug) Log.v("DebugPreferenceActivity.onResume()");
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onPause(){
	    if(_debug) Log.v("DebugPreferenceActivity.onPause()");
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	private void setupCustomPreferences(){
	    if (_debug) Log.v("DebugPreferenceActivity.setupCustomPreferences()");
	    //Debug Mode CheckBox
	    boolean inDebugMode = _preferences.getBoolean(Constants.DEBUG, false) || Log.getDebug();
	    CheckBoxPreference debugModeCheckBoxPreference = (CheckBoxPreference)this.findPreference(Constants.DEBUG);
	    debugModeCheckBoxPreference.setChecked(inDebugMode);
		//Debug Button
		Preference sendDebugLogsPreference = (Preference)this.findPreference("send_debug_logs");
		sendDebugLogsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	Log.collectAndSendLog(_context);
		    	return true;
        	}
		});	
		//Clear Logs Button
		Preference clearDebugLogsPreference = (Preference)this.findPreference("clear_debug_logs");
		clearDebugLogsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	Log.clearLogs(_context);
		    	return true;
        	}
		});
	}
	
}