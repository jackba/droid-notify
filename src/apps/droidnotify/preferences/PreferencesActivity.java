package apps.droidnotify.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This is the applications preference Activity.
 * 
 * @author Camille Sévigny
 */
public class PreferencesActivity extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;
	private Context _context = null;
    private boolean _appProVersion = false;
	
	private TextView _basicSettingsRow = null;	
	private TextView _localeSettingsRow = null;	
	private TextView _screenSettingsRow = null;	
	private TextView _customizeSettingsRow = null;	
	private TextView _notificationsSettingsRow = null;	
	private TextView _advancedSettingsRow = null;	
	private TextView _rateAppSettingsRow = null;	
	private TextView _emailDeveloperSettingsRow = null;	
	private TextView _aboutSettingsRow = null;

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
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    _context = getApplicationContext();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("PreferencesActivity.onCreate()");
	    _appProVersion = Log.getAppProVersion();
	    Common.setApplicationLanguage(_context, this);
	    this.setContentView(R.layout.preference_activity);
	    initLayoutItems();
	    setupRowAttributes();
	    setupRowActivities();
	}

	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("PreferencesActivity.initLayoutItems()");		
		_basicSettingsRow = (TextView)findViewById(R.id.row_basic);
		_localeSettingsRow = (TextView)findViewById(R.id.row_locale);
		_screenSettingsRow = (TextView)findViewById(R.id.row_screen);
		_customizeSettingsRow = (TextView)findViewById(R.id.row_customize);
		_notificationsSettingsRow = (TextView)findViewById(R.id.row_notifications);
		_advancedSettingsRow = (TextView)findViewById(R.id.row_advanced);
		_rateAppSettingsRow = (TextView)findViewById(R.id.row_rate_app);
		_emailDeveloperSettingsRow = (TextView)findViewById(R.id.row_email_developer);
		_aboutSettingsRow = (TextView)findViewById(R.id.row_about);
	}
	
	/**
	 * Set up each preference row's attributes (background style etc.)
	 */
	private void setupRowAttributes(){
		if (_debug) Log.v("PreferencesActivity.setupRowAttributes()");	
		_basicSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_localeSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_screenSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_customizeSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_notificationsSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_advancedSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_rateAppSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_emailDeveloperSettingsRow.setBackgroundResource(R.drawable.preference_row_click);
		_aboutSettingsRow.setBackgroundResource(R.drawable.preference_row_click);	
	}

	/**
	 * Attach the click events to the preference rows.
	 */
	private void setupRowActivities(){
		if (_debug) Log.v("PreferencesActivity.Activities()");	

		//_basicSettingsRow.setOnTouchListener(onTouchListener);
		//_localeSettingsRow.setOnTouchListener(onTouchListener);
		//_screenSettingsRow.setOnTouchListener(onTouchListener);
		//_customizeSettingsRow.setOnTouchListener(onTouchListener);
		//_notificationsSettingsRow.setOnTouchListener(onTouchListener);
		
		

		//Advaced Button
		_advancedSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	Intent aboutActivityIntent = new Intent(_context, AdvancedPreferenceActivity.class);
			    	aboutActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    		startActivity(aboutActivityIntent);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Advanced Button ERROR: " + ex.toString());
		    	}
        	}
		});
		
		//Rate This App Preference/Button
		_rateAppSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	String rateAppURL = "";
			    	if(Log.getShowAndroidRateAppLink()){
			    		if(_appProVersion){
			    			rateAppURL = Constants.APP_PRO_ANDROID_URL;
			    		}else{
			    			rateAppURL = Constants.APP_ANDROID_URL;
			    		}
			    	}else if(Log.getShowAmazonRateAppLink()){
			    		if(_appProVersion){
			    			rateAppURL = Constants.APP_PRO_AMAZON_URL;
			    		}else{
			    			rateAppURL = Constants.APP_AMAZON_URL;
			    		}
			    	}else{
			    		rateAppURL = "";
			    	}
			    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rateAppURL));			    	
			    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
		    		startActivity(intent);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Rate App Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_rate_app_error), Toast.LENGTH_LONG).show();
		    	}
           }
		});
//		//Upgrade Preference/Button
//		Preference upgradePreference = (Preference)findPreference(Constants.UPGRADE_TO_PRO_PREFERENCE_KEY);
//		upgradePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//        	public boolean onPreferenceClick(Preference preference) {
//		    	if (_debug) Log.v("PreferencesActivity() Upgrade Button Clicked()");
//		    	try{
//		    		showDialog(Constants.DIALOG_UPGRADE);
//		    	}catch(Exception ex){
//	 	    		Log.e("PreferencesActivity() Upgrade Button ERROR: " + ex.toString());
//	 	    		return false;
//		    	}
//	            return true;
//           }
//		});
		//Email Developer Button
		_emailDeveloperSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:droidnotify@gmail.com"));
			    	sendEmailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			    	sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Droid Notify App Feedback");
		    		startActivity(sendEmailIntent);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() Email Developer Button ERROR: " + ex.toString());
	 	    		Toast.makeText(_context, _context.getString(R.string.app_android_email_app_error), Toast.LENGTH_LONG).show();
		    	}
           }
		});
		//About Button
		_aboutSettingsRow.setOnClickListener(new OnClickListener(){
        	public void onClick(View view){
		    	try{
			    	Intent aboutActivityIntent = new Intent(_context, AboutPreferenceActivity.class);
			    	aboutActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    		startActivity(aboutActivityIntent);
		    	}catch(Exception ex){
	 	    		Log.e("PreferencesActivity() About Button ERROR: " + ex.toString());
		    	}
        	}
		});
	}
	
}
