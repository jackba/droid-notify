package apps.droidnotify.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the "Phone Notifications" applications preference Activity.
 * 
 * @author Camille S�vigny
 */
public class PhonePreferenceActivity extends PreferenceActivity{
	
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
	    this.addPreferencesFromResource(R.xml.missed_calls_preferences);
	    this.setContentView(R.layout.missed_calls_preferences);
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
		//Status Bar Notification Settings Preference/Button
		Preference statusBarNotificationSettingsPref = (Preference)findPreference(Constants.SETTINGS_STATUS_BAR_NOTIFICATIONS_PREFERENCE);
		statusBarNotificationSettingsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, PhoneStatusBarNotificationsPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "PhonePreferenceActivity() Status Bar Notifications Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Preference/Button
		Preference customizePref = (Preference)findPreference(Constants.SETTINGS_CUSTOMIZE_PREFERENCE);
		customizePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, PhoneCustomizePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "PhonePreferenceActivity() Customize Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//More Options Preference/Button
		Preference moreOptionsPref = (Preference)findPreference(Constants.MORE_OPTIONS_KEY);
		moreOptionsPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
        	public boolean onPreferenceClick(Preference preference){
		    	try{
		    		if(Common.packageExists(_context, "com.missedcallmessenger.pro")){
		    			Intent intent = _context.getPackageManager().getLaunchIntentForPackage("com.missedcallmessenger.pro");
		    			if(intent == null){
		    				Log.e(_context, "PhonePreferenceActivity() More Options Button - Launching 'com.missedcallmessenger.pro' Failed. Exiting...");
		    				return false;
		    			}
		    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			startActivity(intent);
		    		}else if(Common.packageExists(_context, "com.missedcallmessenger.lite")){
		    			Intent intent = _context.getPackageManager().getLaunchIntentForPackage("com.missedcallmessenger.lite");
		    			if(intent == null){
		    				Log.e(_context, "PhonePreferenceActivity() More Options Button - Launching 'com.missedcallmessenger.lite' Failed. Exiting...");
		    				return false;
		    			}
		    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			startActivity(intent);
		    		}else{
		    			startActivity(new Intent(_context, MoreMissedCallOptionsActivity.class));
		    		}
		    		return true;
		    	}catch(Exception ex){
		    		Log.e(_context, "PhonePreferenceActivity() More Options Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
}