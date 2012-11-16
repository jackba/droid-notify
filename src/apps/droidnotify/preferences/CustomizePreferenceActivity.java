package apps.droidnotify.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.Display;
import android.view.HapticFeedbackConstants;

import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.preferences.theme.ThemePreferenceActivity;

public class CustomizePreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	//================================================================================
    // Properties
    //================================================================================

	private Context _context = null;
	private SharedPreferences _preferences = null;
	
	//================================================================================
	// Public Methods
	//================================================================================
    
	/**
	 * When a SharedPreference is changed this registered function is called.
	 * 
	 * @param sharedPreferences - The Preference object who's key was changed.
	 * @param key - The String value of the preference Key who's preference value was changed.
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(Constants.AUTO_POPUP_WIDTH_KEY)){
			checkPopupWidthPreferences();
		}
	}

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
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    Common.setApplicationLanguage(_context, this);
	    setPopupWidthDefaults();
	    this.addPreferencesFromResource(R.xml.customize_preferences);
	    this.setContentView(R.layout.customize_preferences);
	    setupCustomPreferences();
	    checkPopupWidthPreferences();
	}
	
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume(){
	    super.onResume();
	    _preferences.registerOnSharedPreferenceChangeListener(this);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause(){
	    super.onPause();
	    _preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Set the default value of the popup width user preferences.
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setPopupWidthDefaults(){
		Display display = getWindowManager().getDefaultDisplay(); 
        Point size = new Point();
        int screenWidth;
        int screenHeight;
        if(Common.getDeviceAPILevel() >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
	        display.getSize(size);
	        screenWidth = size.x;
	        screenHeight = size.y;
        }else{
	        screenWidth = display.getWidth();
	        screenHeight = display.getHeight();
        }
        Log.v(_context, "CustomizePreferenceActivity.setPopupWidthDefaults() Screen Width: " + screenWidth);
        Log.v(_context, "CustomizePreferenceActivity.setPopupWidthDefaults() Screen Height: " + screenHeight);
        //Swap the values if they are backwards. Screen width should always be the smaller value.
        if(screenHeight < screenWidth){
        	int tempSize = screenHeight;
        	screenHeight = screenWidth;
        	screenWidth = tempSize;
        }
		if(_preferences.getString(Constants.PORTRAIT_POPUP_WIDTH_KEY, null) == null){
			SharedPreferences.Editor editor = _preferences.edit();
    		editor.putString(Constants.PORTRAIT_POPUP_WIDTH_KEY, String.valueOf(screenWidth));
    		editor.commit();
		}
		if(_preferences.getString(Constants.LANDSCAPE_POPUP_WIDTH_KEY, null) == null){
			SharedPreferences.Editor editor = _preferences.edit();
    		editor.putString(Constants.LANDSCAPE_POPUP_WIDTH_KEY, String.valueOf(screenHeight));
    		editor.commit();
		}
	}

	/**
	 * Setup click events on custom preferences.
	 */
	@SuppressWarnings("deprecation")
	private void setupCustomPreferences(){
		//Notification Preview Preference/Button
		Preference previewPref = (Preference)findPreference("notification_preview_preference");
		previewPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	try{
		    		customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    		runNotificationPreviews();
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "CustomizePreferenceActivity() Notification Preview Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});
		//Theme Preference/Button
		Preference themePref = (Preference)findPreference("theme_preference");
		themePref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference){
		    	try{
		    		startActivity(new Intent(_context, ThemePreferenceActivity.class));
		    		return true;
		    	}catch(Exception ex){
	 	    		Log.e(_context, "CustomizePreferenceActivity() Theme Button ERROR: " + ex.toString());
	 	    		return false;
		    	}
        	}
		});	
	}
	
	/**
	 * Fire off a single notification of all types as a preview.
	 */
	private void runNotificationPreviews(){
		
		if(!Common.isDeviceWiFiOnly(_context)){
			
			//Missed Call
			Bundle missedCallNotificationBundleSingle = new Bundle();
			missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_CALL_LOG_ID, -1);
			missedCallNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, "1234567890");
			missedCallNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, Common.convertGMTToLocalTime(_context, System.currentTimeMillis(), true));
			missedCallNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_PHONE);
			Bundle missedCallNotificationBundle = new Bundle();
			missedCallNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", missedCallNotificationBundleSingle);
			missedCallNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
			Bundle missedCallBundle = new Bundle();
			missedCallBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_PHONE);
			missedCallBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, missedCallNotificationBundle);
			Common.startNotificationActivity(_context, missedCallBundle);
	
			//SMS
			Bundle smsNotificationBundleSingle = new Bundle();
			smsNotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, "1234567890");			
			smsNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, "SMS message.");
			smsNotificationBundleSingle.putLong(Constants.BUNDLE_MESSAGE_ID, -1);
			smsNotificationBundleSingle.putLong(Constants.BUNDLE_THREAD_ID, -1);
			smsNotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, Common.convertGMTToLocalTime(_context, System.currentTimeMillis(), true));
			smsNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_SMS);
			Bundle smsNotificationBundle = new Bundle();
			smsNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", smsNotificationBundleSingle);
			smsNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
			Bundle smsBundle = new Bundle();
			smsBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_SMS);
			smsBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, smsNotificationBundle);
			Common.startNotificationActivity(_context, smsBundle);
		
		}

		//Calendar
		Bundle calendarNotificationBundleSingle = new Bundle();
		calendarNotificationBundleSingle.putString(Constants.BUNDLE_TITLE, "Calendar Event");
		calendarNotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, "Calendar event.");
		calendarNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_START_TIME, Common.convertGMTToLocalTime(_context, System.currentTimeMillis(), true));
		calendarNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_END_TIME, Common.convertGMTToLocalTime(_context, System.currentTimeMillis() + (10 * 60 * 1000), true));
		calendarNotificationBundleSingle.putBoolean(Constants.BUNDLE_ALL_DAY, false);
		calendarNotificationBundleSingle.putString(Constants.BUNDLE_CALENDAR_NAME, "Calendar");
		calendarNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_ID, -1);
		calendarNotificationBundleSingle.putLong(Constants.BUNDLE_CALENDAR_EVENT_ID, -1);
		calendarNotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_CALENDAR);
		Bundle calendarNotificationBundle = new Bundle();
		calendarNotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", calendarNotificationBundleSingle);
		calendarNotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
		Bundle calendarBundle = new Bundle();
		calendarBundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_CALENDAR);
		calendarBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, calendarNotificationBundle);
		Common.startNotificationActivity(_context, calendarBundle);

		//K9
		Bundle k9NotificationBundleSingle = new Bundle();
		k9NotificationBundleSingle.putString(Constants.BUNDLE_SENT_FROM_ADDRESS, "email@email.com");
		k9NotificationBundleSingle.putString(Constants.BUNDLE_MESSAGE_BODY, "K-9 email message.");
		k9NotificationBundleSingle.putLong(Constants.BUNDLE_MESSAGE_ID, -1);
		k9NotificationBundleSingle.putLong(Constants.BUNDLE_TIMESTAMP, Common.convertGMTToLocalTime(_context, System.currentTimeMillis(), true));
		k9NotificationBundleSingle.putString(Constants.BUNDLE_K9_EMAIL_URI, null);
		k9NotificationBundleSingle.putString(Constants.BUNDLE_K9_EMAIL_DEL_URI, null);
		k9NotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_K9);
		k9NotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, Constants.NOTIFICATION_TYPE_KAITEN_MAIL);
		k9NotificationBundleSingle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_K9);
		Bundle k9NotificationBundle = new Bundle();
		k9NotificationBundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1", k9NotificationBundleSingle);
		k9NotificationBundle.putInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT, 1);
		Bundle k9Bundle = new Bundle();
		k9Bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_PREVIEW_K9);
		k9Bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, k9NotificationBundle);
		Common.startNotificationActivity(_context, k9Bundle);

	}
	
	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void checkPopupWidthPreferences(){
		EditTextPreference portraitPopupwidthListPreference = (EditTextPreference)findPreference(Constants.PORTRAIT_POPUP_WIDTH_KEY);
		EditTextPreference landscapePopupwidthListPreference = (EditTextPreference)findPreference(Constants.LANDSCAPE_POPUP_WIDTH_KEY);
		if (portraitPopupwidthListPreference != null) portraitPopupwidthListPreference.setEnabled(!_preferences.getBoolean(Constants.AUTO_POPUP_WIDTH_KEY, true));
		if (landscapePopupwidthListPreference != null) landscapePopupwidthListPreference.setEnabled(!_preferences.getBoolean(Constants.AUTO_POPUP_WIDTH_KEY, true));
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if(_preferences.getBoolean(Constants.HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				vibrator.vibrate(50);
			}
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				vibrator.vibrate(100);
			}
		}
	}
	
}
