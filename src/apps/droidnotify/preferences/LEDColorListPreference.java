package apps.droidnotify.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import apps.droidnotify.R;
import apps.droidnotify.common.Constants;

/**
 * A custom ListPreference class that handles custom LED color selection/creation.
 * 
 * @author Camille Sévigny
 */
public class LEDColorListPreference extends ListPreference implements OnSeekBarChangeListener {
	
	//================================================================================
    // Properties
    //================================================================================

    private Context _context = null;
    private SharedPreferences _preferences = null;
    private int _notificationType = -2;
	private SeekBar redSeekBar = null;
	private SeekBar greenSeekBar = null;
	private SeekBar blueSeekBar = null;
	private TextView redTextView = null;
	private TextView greenTextView = null;
	private TextView blueTextView = null;
	private ImageView previewImageView = null;

	//================================================================================
	// Constructors
	//================================================================================
    
	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 */
	public LEDColorListPreference(Context context) {
		super(context);
		_context = context;
		_preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	}
   
	/**
	 * ListPreference constructor.
	 * 
	 * @param context - The application context.
	 */
	public LEDColorListPreference(Context context, AttributeSet attrs) {
		  super(context, attrs);
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
		if (result) {
			if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_SMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_MMS;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_PHONE;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_CALENDAR;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_GMAIL;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_TWITTER;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_FACEBOOK;
				showDialog();
			}else if (_preferences.getString(this.getKey(), Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT).equals(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY)) {
				_notificationType = Constants.NOTIFICATION_TYPE_K9;
				showDialog();
			}
		}
	}
		
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Display the dialog window that allows the user to enter the led color they wish to have.
	 */
	private void showDialog() {
	    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.ledcolordialog, null);
	    
		//Get Current Custom Color
	    int ledCustomColor = 0;
	    try {
	    	switch(_notificationType){
			    case Constants.NOTIFICATION_TYPE_SMS:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_MMS:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_PHONE:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_CALENDAR:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_GMAIL:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_TWITTER:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_FACEBOOK:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
			    case Constants.NOTIFICATION_TYPE_K9:{
			    	ledCustomColor = Color.parseColor(_preferences.getString(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT));
			    }
		    }
	    } catch (Exception ex) {
	    	ledCustomColor = Color.parseColor(Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT);
	    }
	    int red = Color.red(ledCustomColor);
	    int green = Color.green(ledCustomColor);
	    int blue = Color.blue(ledCustomColor);
	
	    //Initialize Views
	    redSeekBar = (SeekBar) view.findViewById(R.id.redSeekBar);
	    greenSeekBar = (SeekBar) view.findViewById(R.id.greenSeekBar);
	    blueSeekBar = (SeekBar) view.findViewById(R.id.blueSeekBar);
	    previewImageView = (ImageView) view.findViewById(R.id.previewImageView);  
	    redTextView = (TextView) view.findViewById(R.id.redTextView);
	    greenTextView = (TextView) view.findViewById(R.id.greenTextView);
	    blueTextView = (TextView) view.findViewById(R.id.blueTextView);
	
		redSeekBar.setProgress(red);
		greenSeekBar.setProgress(green);
		blueSeekBar.setProgress(blue);
		
	    redSeekBar.setOnSeekBarChangeListener(this);
	    greenSeekBar.setOnSeekBarChangeListener(this);
	    blueSeekBar.setOnSeekBarChangeListener(this);
	
	    updateTextView(redTextView, redSeekBar.getProgress());
	    updateTextView(greenTextView, greenSeekBar.getProgress());
	    updateTextView(blueTextView, blueSeekBar.getProgress());
		updateColorImageView();
	
		//Build and display the Dialog window
		AlertDialog.Builder ledColorAlertBuilder = new AlertDialog.Builder(_context);
		ledColorAlertBuilder.setIcon(android.R.drawable.ic_dialog_info);
		ledColorAlertBuilder.setTitle(R.string.preference_led_color_title);
		ledColorAlertBuilder.setView(view);
		ledColorAlertBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				int customColor = Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
				String customLEDColor = "#" + Integer.toHexString(customColor);
				SharedPreferences.Editor editor = _preferences.edit();
	        	switch(_notificationType){
				    case Constants.NOTIFICATION_TYPE_SMS:{
				    	editor.putString(Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_MMS:{
				    	editor.putString(Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_PHONE:{
				    	editor.putString(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_CALENDAR:{
				    	editor.putString(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_GMAIL:{
				    	editor.putString(Constants.GMAIL_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_TWITTER:{
				    	editor.putString(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				    	editor.putString(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
				    case Constants.NOTIFICATION_TYPE_K9:{
				    	editor.putString(Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY, customLEDColor);
				    }
			    }
		        editor.commit();
				Toast.makeText(_context, _context.getString(R.string.preference_led_color_set), Toast.LENGTH_LONG).show();
			}
		});
		ledColorAlertBuilder.show();
	}

	/**
	 * Call back function when a seekbar value was changed.
	 * 
	 * @param seekbar - The seekbar that was changed.
	 * @param progress - The value of the seekbar.
	 * @param fromTouch - True if the value was changed by a touch event.
	 */
	public void onProgressChanged(SeekBar seekbar, int progress, boolean fromTouch) {
		TextView textView = null;
		if(seekbar.equals(redSeekBar)){
			textView = redTextView;
		}else if(seekbar.equals(greenSeekBar)){
			textView = greenTextView;
		}else{
			textView = blueTextView;
		}
		updateTextView(textView, progress);
		updateColorImageView();
	}

	/**
	 * Update the TextView that displays the value of the seekbar.
	 * 
	 * @param seekbar - The seekbar who's value 
	 * @param progress
	 */
	private void updateTextView(TextView textView, int progress) {
	    textView.setText(String.valueOf(progress));
	}

	/**
	 * Updater the color ImageView preview window using the values of the current seek bars.
	 */
  	private void updateColorImageView() {
  		previewImageView.setBackgroundColor(Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress()));
  	}

	public void onStartTrackingTouch(SeekBar seekBar) {}
	public void onStopTrackingTouch(SeekBar seekBar) {}

}
