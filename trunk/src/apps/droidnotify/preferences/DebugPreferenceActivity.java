package apps.droidnotify.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "Debug" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class DebugPreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _context = this;
	    _debug = Log.getDebug();
	    if (_debug) Log.v("DebugPreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.debug_preferences);
	    this.setContentView(R.layout.debug_preferences);
	    setupCustomPreferences();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	private void setupCustomPreferences(){
		//Debug Button
		Preference sendDebugLogsPreference = (Preference)this.findPreference("send_debug_logs");
		sendDebugLogsPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	Log.collectAndSendLog(_context);
		    	return true;
        	}
		});	
	}
	
}