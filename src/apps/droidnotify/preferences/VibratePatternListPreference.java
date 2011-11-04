package apps.droidnotify.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * A custom ListPreference class that handles custom vibrate pattern selection/creation.
 * 
 * @author Camille Sévigny
 */
public class VibratePatternListPreference extends ListPreference {
	
	//================================================================================
    // Properties
    //================================================================================

    private boolean _debug = false;
    private Context _context = null;
    private SharedPreferences _preferences = null;
    private int _notificationType = -2;

	//================================================================================
	// Constructors
	//================================================================================
    
	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 */
	public VibratePatternListPreference(Context context) {
		super(context);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("VibratePatternListPreference.VibratePatternListPreference()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}

	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 * @param attrs
	 */
	public VibratePatternListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("VibratePatternListPreference.VibratePatternListPreference()");
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Override the onDialogClosed method.
	 * 
	 * @param result - A boolean result that indicates how the dialog window was closed.
	 */
	@Override
	protected void onDialogClosed(boolean result) {
		super.onDialogClosed(result);
		if (_debug) Log.v("VibratePatternListPreference.onDialogClosed()");
		if (result) {
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_SMS;
				showDialog();
			}
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the dialog window that allows the user to enter the vibrate pattern they wish to have.
	 */
	private void showDialog() {
		if (_debug) Log.v("VibratePatternListPreference.showDialog()");
	    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.vibratepatterndialog, null);
	    final EditText customVibratePatternEditText = (EditText) view.findViewById(R.id.customVibrateEditText);
	    switch(_notificationType){
		    case Constants.NOTIFICATION_TYPE_SMS:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_MMS:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_PHONE:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_TWITTER:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
		    case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		    	 customVibratePatternEditText.setText(_preferences.getString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT));
		    }
	    }
	    AlertDialog.Builder vibratePatternAlertBuilder = new AlertDialog.Builder(_context);
	    vibratePatternAlertBuilder.setIcon(R.drawable.ic_dialog_info);
	    vibratePatternAlertBuilder.setTitle(R.string.preference_vibrate_pattern_title);
	    vibratePatternAlertBuilder.setView(view);
	    vibratePatternAlertBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		        String customVibratePattern = customVibratePatternEditText.getText().toString();
		        if (validateVibratePattern(customVibratePattern)) {
		        	SharedPreferences.Editor editor = _preferences.edit();
		        	switch(_notificationType){
					    case Constants.NOTIFICATION_TYPE_SMS:{
					    	editor.putString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_MMS:{
					    	editor.putString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_PHONE:{
					    	editor.putString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_CALENDAR:{
					    	editor.putString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_GMAIL:{
					    	editor.putString(Constants.GMAIL_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_TWITTER:{
					    	editor.putString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
					    case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					    	editor.putString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY, customVibratePattern);
					    }
				    }
		            editor.commit();
		            Toast.makeText(_context, _context.getString(R.string.preference_vibrate_pattern_set), Toast.LENGTH_LONG).show();
		        } else {
		        	Toast.makeText(_context, _context.getString(R.string.preference_vibrate_pattern_error), Toast.LENGTH_LONG).show();
		        }
			}
	    });
	    vibratePatternAlertBuilder.show();
	}
	
	/**
	 * Parse a vibration pattern and verify if it's valid or not.
	 * 
	 * @param vibratePattern - The vibrate pattern to verify.
	 * 
	 * @return boolean - Returns True if the vibrate pattern is valid.
	 */
	private boolean validateVibratePattern(String vibratePattern){
		if (_debug) Log.v("VibratePatternListPreference.parseVibratePattern()");
		String[] vibratePatternArray = vibratePattern.split(",");
		int arraySize = vibratePatternArray.length;
	    for (int i = 0; i < arraySize; i++) {
	    	long vibrateLength = -1;
	    	try {
	    		vibrateLength = Long.parseLong(vibratePatternArray[i].trim());
	    	} catch (Exception ex) {
	    		return false;
	    	}
	    	if(vibrateLength < 0){
	    		return false;
	    	}
	    }
		return true;
	}
	
}