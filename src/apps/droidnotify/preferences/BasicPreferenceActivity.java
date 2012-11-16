package apps.droidnotify.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.quickreply.QuickReplyPreferenceActivity;

public class BasicPreferenceActivity extends PreferenceActivity{
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
	
	//================================================================================
	// Protected Methods
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
	    this.addPreferencesFromResource(R.xml.basic_preferences);
	    this.setContentView(R.layout.basic_preferences);
	    setupCustomPreferences();
	}
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    super.onResume();
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(_context).edit();
		editor.putBoolean(Constants.BLOCKING_APPS_ENABLED_KEY, false);
		editor.commit();
		@SuppressWarnings("deprecation")
		CheckBoxPreference blockingAppsPref = (CheckBoxPreference)findPreference("blocking_apps_enabled");
		if(blockingAppsPref != null) blockingAppsPref.setChecked(false);
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Reschedule Preference/Button
		Preference reschedulePref = (Preference)findPreference("reschedule_preference");
		reschedulePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		startActivity(new Intent(_context, ReschedulePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Reschedule Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Reminder Preference/Button
		Preference remindersPref = (Preference)findPreference("reminders_preference");
		remindersPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		startActivity(new Intent(_context, RemindersPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Reminders Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Quiet Time Preference/Button
		Preference quietTimePref = (Preference)findPreference("quiet_time_preference");
		quietTimePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		startActivity(new Intent(_context, QuietTimePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Quiet Time Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Quick Reply Preference/Button
		Preference quickReplyPref = (Preference)findPreference("quick_reply_preference");
		quickReplyPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
		    		startActivity(new Intent(_context, QuickReplyPreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Quick Reply Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Blocking Apps Preference/Button
		CheckBoxPreference blockingAppsPref = (CheckBoxPreference)findPreference("blocking_apps_enabled");
		blockingAppsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
	    			Bundle bundle = new Bundle();
	    			bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_FEATURE_PRO_ONLY);
			    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
			    	upgradeActivityIntent.putExtras(bundle);
		    		startActivity(upgradeActivityIntent);
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Blocking Apps Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Customize Contacts Preference/Button
		Preference customizeContactsPref = (Preference)findPreference("customize_contacts_preference");
		customizeContactsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
	    			Bundle bundle = new Bundle();
	    			bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_FEATURE_PRO_ONLY);
			    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
			    	upgradeActivityIntent.putExtras(bundle);
		    		startActivity(upgradeActivityIntent);
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Customize Contacts Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Blacklist Preference/Button
		Preference blacklistPref = (Preference)findPreference("blacklist_preference");
		blacklistPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
		    	try{
	    			Bundle bundle = new Bundle();
	    			bundle.putInt(Constants.DIALOG_UPGRADE_TYPE, Constants.DIALOG_FEATURE_PRO_ONLY);
			    	Intent upgradeActivityIntent = new Intent(_context, UpgradePreferenceActivity.class);
			    	upgradeActivityIntent.putExtras(bundle);
		    		startActivity(upgradeActivityIntent);
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "BasicPreferenceActivity() Blacklist Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
	}
	
}