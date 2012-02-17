package apps.droidnotify.preferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This is the "Advanced" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class AdvancedPreferenceActivity extends PreferenceActivity{
	
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
	    if (_debug) Log.v("AdvancedPreferenceActivity.onCreate()");
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.advanced_preferences);
	    this.setContentView(R.layout.advanced_preferences);
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
		Preference debugPreference = (Preference)this.findPreference("debug_preference");
		debugPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	try{
			    	Intent aboutActivityIntent = new Intent(_context, DebugPreferenceActivity.class);
			    	aboutActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    		startActivity(aboutActivityIntent);
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Advanced Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});	
	}
	
}