package apps.droidnotify.preferences.blockingapps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

public class BlockingAppsPreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Called when the activity is created. Set up views and buttons.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.blocking_apps_settings);
	    this.setContentView(R.layout.blocking_apps_settings);
	    setupCustomPreferences();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Blocking Apps Preference/Button
		Preference selectBlockingAppsPref = (Preference)findPreference("select_blocking_apps_preference");
		selectBlockingAppsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		startActivity(new Intent(_context, SelectBlockingAppsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e("BlockingAppsPreferenceActivity() Select Blocking Apps Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
}