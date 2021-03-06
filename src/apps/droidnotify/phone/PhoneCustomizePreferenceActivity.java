package apps.droidnotify.phone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;

/**
 * This is the "Phone Customize" applications preference Activity.
 * 
 * @author Camille S�vigny
 */
public class PhoneCustomizePreferenceActivity extends PreferenceActivity{

	
	
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
	    Common.setApplicationLanguage(getApplicationContext(), this);
	    this.addPreferencesFromResource(R.xml.missed_calls_customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	}
	
}