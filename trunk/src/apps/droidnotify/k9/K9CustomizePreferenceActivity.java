package apps.droidnotify.k9;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;

/**
 * This is the "K9 Customize" applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class K9CustomizePreferenceActivity extends PreferenceActivity{
	
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
	    this.addPreferencesFromResource(R.xml.k9_customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	}
	
}