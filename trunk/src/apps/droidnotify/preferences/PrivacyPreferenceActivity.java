package apps.droidnotify.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

public class PrivacyPreferenceActivity extends PreferenceActivity{
	
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
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle){
	    super.onCreate(bundle);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("PrivacyPreferenceActivity.onCreate()");
	    _context = this;
	    Common.setApplicationLanguage(_context, this);
	    this.addPreferencesFromResource(R.xml.privacy_preferences);
	    this.setContentView(R.layout.privacy_preferences);
	    if(Common.isDeviceWiFiOnly(_context)){
	    	CheckBoxPreference smsPrivacyPreference = (CheckBoxPreference)this.findPreference(Constants.SMS_MESSAGE_PRIVACY_ENABLED_KEY);
	    	CheckBoxPreference missedCallPrivacyPreference = (CheckBoxPreference)this.findPreference(Constants.MISSED_CALL_PRIVACY_ENABLED_KEY);
	    	smsPrivacyPreference.setEnabled(false);
	    	missedCallPrivacyPreference.setEnabled(false);
		}
	}
	
}
