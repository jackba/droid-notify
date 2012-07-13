package apps.droidnotify.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class QuickReplyPreferenceActivity extends PreferenceActivity{
	
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
	    _debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyPreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.quick_reply_preferences);
	    this.setContentView(R.layout.quick_reply_preferences);
    	//Remove deprecated/invalid options based on OS version.
    	if(Common.getOSAPILevel() > android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
			CheckBoxPreference blurScreenCheckBoxPreference = (CheckBoxPreference)findPreference(Constants.QUICK_REPLY_BLUR_SCREEN_BACKGROUND_ENABLED_KEY);
			this.getPreferenceScreen().removePreference(blurScreenCheckBoxPreference);
    	}
	}
	
}
