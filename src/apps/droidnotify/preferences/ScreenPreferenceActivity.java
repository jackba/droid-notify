package apps.droidnotify.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;

public class ScreenPreferenceActivity extends PreferenceActivity{
	
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
	    this.addPreferencesFromResource(R.xml.screen_preferences);
	    this.setContentView(R.layout.screen_preferences);
    	//Remove deprecated/invalid options based on OS version.
    	if(Common.getDeviceAPILevel() > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
    		PreferenceCategory notificationsPreferenceCategory = (PreferenceCategory)findPreference("notifications_category");
    		if(notificationsPreferenceCategory != null){
	    		CheckBoxPreference blurScreenCheckBoxPreference = (CheckBoxPreference)findPreference(Constants.BLUR_SCREEN_BACKGROUND_ENABLED_KEY);
	    		notificationsPreferenceCategory.removePreference(blurScreenCheckBoxPreference);
    		}
    	}
	}
	
}
