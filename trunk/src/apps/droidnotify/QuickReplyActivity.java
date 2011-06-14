package apps.droidnotify;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the quick reply activity that is used to send sms messages.
 * 
 * @author Camille Sévigny
 */
public class QuickReplyActivity extends Activity {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int SEND_BUTTON = R.id.quick_reply_send_button;
	private final int CANCEL_BUTTON = R.id.quick_reply_cancel_button;
	private final int TO_EDIT_TEXT = R.id.send_to_edit_text;
	private final int MESSAGE_EDIT_TEXT = R.id.message_edit_text;

	private final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	private final String LANDSCAPE_SCREEN_ENABLED_KEY = "landscape_screen_enabled";
	
	private final String APP_THEME_KEY = "app_theme";
	private final String ANDROID_THEME = "android";
	private final String ANDROID_DARK_THEME = "android_dark";
	private final String IPHONE_THEME = "iphone";
	private final String DARK_TRANSLUCENT_THEME = "dark_translucent";
	private final String DARK_TRANSLUCENT_V2_THEME = "dark_translucent_v2";
	private final String DARK_TRANSLUCENT_V3_THEME = "dark_translucent_v3";
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug;
	private Bundle _bundle = null;
	private Context _context = null;
	private Button _sendButton = null;
	private Button _cancelButton = null;
	private EditText _toEditText  = null;
	private EditText _messageEditText = null;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Accessors
	//================================================================================
	  
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param bundle - The bundle passed into this Activity.
	 */
	public void setBundle(Bundle bundle) {
		if (_debug) Log.v("QuickReplyActivity.setBundle()");
	    _bundle = bundle;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return Bundle - The bundle passed into this Activity.
	 */
	public Bundle getBundle() {
		if (_debug) Log.v("QuickReplyActivity.getBundle()");
	    return _bundle;
	} 
	
	/**
	 * Set the context property.
	 * 
	 * @param context - Application's Context.
	 */
	public void setContext(Context context) {
		if (_debug) Log.v("QuickReplyActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return Context - Application's Context.
	 */
	public Context getContext() {
		if (_debug) Log.v("QuickReplyActivity.getContext()");
	    return _context;
	}
	
	/**
	 * Set the sendButton property.
	 * 
	 * @param sendButton - Send Button.
	 */
	public void setSendButton(Button sendButton) {
		if (_debug) Log.v("QuickReplyActivity.setSendButton()");
	    _sendButton = sendButton;
	}
	
	/**
	 * Get the sendButton property.
	 * 
	 * @return Button - Send Button.
	 */
	public Button getSendButton() {
		if (_debug) Log.v("QuickReplyActivity.getSendButton()");
	    return _sendButton;
	}

	/**
	 * Set the cancelButton property.
	 * 
	 * @param cancelButton - Cancel Button.
	 */
	public void setCancelButton(Button cancelButton) {
		if (_debug) Log.v("QuickReplyActivity.setCancelButton()");
	    _cancelButton = cancelButton;
	}
	
	/**
	 * Get the cancelButton property.
	 * 
	 * @return Button - Cancel Button.
	 */
	public Button getCancelButton() {
		if (_debug) Log.v("QuickReplyActivity.getCancelButton()");
	    return _cancelButton;
	}
	
	/**
	 * Set the toEditText property.
	 * 
	 * @param toEditText - To Edit Text.
	 */
	public void setToEditText(EditText toEditText) {
		if (_debug) Log.v("QuickReplyActivity.setToEditText()");
	    _toEditText = toEditText;
	}
	
	/**
	 * Get the toEditText property.
	 * 
	 * @return EditText - To Edit Text.
	 */
	public EditText getToEditText() {
		if (_debug) Log.v("QuickReplyActivity.getToEditText()");
	    return _toEditText;
	}
	
	/**
	 * Set the toEditText property.
	 * 
	 * @param toEditText - To Edit Text.
	 */
	public void setMessageEditText(EditText messageEditText) {
		if (_debug) Log.v("QuickReplyActivity.setMessageEditText()");
	    _messageEditText = messageEditText;
	}
	
	/**
	 * Get the messageEditText property.
	 * 
	 * @return EditText - To Edit Text.
	 */
	public EditText getMessageEditText() {
		if (_debug) Log.v("QuickReplyActivity.getMessageEditText()");
	    return _messageEditText;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Handles the activity when the configuration changes (e.g. The phone switches from portrait view to landscape view).
	 */
	public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (_debug) Log.v("QuickReplyActivity.onConfigurationChanged()");
        //Do Nothing.
	}
	
	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle - The bundle passed into this Activity.
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onCreate()");
	    Context context = getApplicationContext();
	    setBundle(bundle);
	    setContext(context);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(preferences.getBoolean(LANDSCAPE_SCREEN_ENABLED_KEY, true)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    //Set based on the theme. This is set in the user preferences.
		String applicationThemeSetting = preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		int themeResource = R.layout.android_theme_notification;
		if(applicationThemeSetting.equals(ANDROID_THEME)) themeResource = R.layout.android_theme_smsreply;
		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)) themeResource = R.layout.android_dark_theme_smsreply;
		if(applicationThemeSetting.equals(IPHONE_THEME)) themeResource = R.layout.iphone_theme_smsreply;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_THEME)) themeResource = R.layout.dark_translucent_theme_smsreply;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V2_THEME)) themeResource = R.layout.dark_translucent_v2_theme_smsreply;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V3_THEME)) themeResource = R.layout.dark_translucent_v3_theme_smsreply;		
	    setContentView(themeResource);  
	    setSendButton((Button)findViewById(SEND_BUTTON));
	    setCancelButton((Button)findViewById(CANCEL_BUTTON));
	    setToEditText((EditText)findViewById(TO_EDIT_TEXT));
	    setMessageEditText((EditText)findViewById(MESSAGE_EDIT_TEXT));
	    Bundle extrasBundle = getIntent().getExtras();
	    parseQuickReplyParameters(extrasBundle);
	    //Setup Activities buttons.
	    setupButtons();
	    //Set focus to appropriate field.
	    setFocus();
	}
	
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		_debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onStart()");
	    setFocus();
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onResume()");
	    setFocus();
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("QuickReplyActivity.onPause()");
	    showSoftKeyboard(false, (EditText) findViewById(R.id.message_edit_text));
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (_debug) Log.v("QuickReplyActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (_debug) Log.v("QuickReplyActivity.onDestroy()");
	    showSoftKeyboard(false, (EditText) findViewById(R.id.message_edit_text));
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	private void finishActivity() {
		if (_debug) Log.v("QuickReplyActivity.finishActivity()");
	    // Finish the activity.
	    finish();
	}

	/**
	 * Gets the passed in parameters for this Activity and loads them into the text fields.
	 * 
	 * @param bundle - The bundle passed into this Activity.
	 */
	private void parseQuickReplyParameters(Bundle bundle){
		if (_debug) Log.v("QuickReplyActivity.parseQuickReplyParameters()");
		String phoneNumber = bundle.getString("smsPhoneNumber");
		String message = bundle.getString("smsMessage");
		EditText toEditText = getToEditText();
		EditText messageEditText = getMessageEditText();
		if(phoneNumber != null){
			toEditText.setText(phoneNumber);
		}
		if(message != null){
			messageEditText.setText(message);
		}
	}
	
	/**
	 * Setup the Quick Reply buttons.
	 */
	private void setupButtons(){
		if (_debug) Log.v("QuickReplyActivity.setupButtons()");
	    Button sendButton = getSendButton(); 
	    sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
            	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            	sendSMSMessage(); 
            }
        });
	    Button cancelButton = getCancelButton(); 
	    cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
            	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            	finishActivity();                
            }
        });
	}
	
	/**
	 * Send simple SMS message.
	 */
	private void sendSMSMessage(){
		if (_debug) Log.v("QuickReplyActivity.sendSMSMessage()");
		EditText toEditText = getToEditText();
		EditText messageEditText = getMessageEditText();
		String phoneNumber = toEditText.getText().toString();
        String message = messageEditText.getText().toString();                 
        if(phoneNumber.length()>0 && message.length()>0){                
            sendSMS(phoneNumber, message);                
        }else{
        	if(phoneNumber.length()<= 0){
        		Toast.makeText(getBaseContext(), getString(R.string.phone_number_error_text), Toast.LENGTH_SHORT).show();
        	}else if(message.length()<= 0){
        		Toast.makeText(getBaseContext(), getString(R.string.message_error_text), Toast.LENGTH_SHORT).show();
        	}
        }
	}
	
	/**
	 * Send SMS message.
	 * 
	 * @param phoneNumber - The phone number we are sending the message to.
	 * @param message - The message we are sending.
	 */
	private void sendSMS(String phoneNumber, String message){   
		if (_debug) Log.v("QuickReplyActivity.sendSMS()");
        final String SMS_SENT = "SMS_SENT";
        final String SMS_DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);
        //When the SMS has been sent.
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_sent_text), Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_sent_error_generic_failure_text), Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_sent_error_no_service_text), Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_sent_error_null_pdu_text), Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_sent_error_radio_off_text), Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SMS_SENT));
        //When the SMS has been delivered.
//        registerReceiver(new BroadcastReceiver(){
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode())
//                {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_delivered_text), Toast.LENGTH_SHORT).show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), getString(R.string.message_not_delivered_text), Toast.LENGTH_SHORT).show();
//                        break;                        
//                }
//            }
//        }, new IntentFilter(SMS_DELIVERED));        
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        //Finish Activity.
        finishActivity();
    }
	
	/**
	 * Function that performs custom haptic feedback.
	 * This function performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		if (_debug) Log.v("QuickReplyActivity.customPerformHapticFeedback()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		Vibrator vibrator = null;
		try{
			vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			//Perform the haptic feedback based on the users preferences.
			if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
				if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
					if(vibrator != null) vibrator.vibrate(50);
				}
			}
			if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
				if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
					if(vibrator != null) vibrator.vibrate(100);
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("QuickReplyActivity.customPerformHapticFeedback() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Sets the focus to the body EditText field.
	 */
	private void setFocus(){
		if (_debug) Log.v("QuickReplyActivity.setFocus()");
		EditText quickReplyMessageEditText = (EditText) findViewById(R.id.message_edit_text);
		quickReplyMessageEditText.requestFocus();
		showSoftKeyboard(true, quickReplyMessageEditText);
	}
	
	/**
	 * Shows or hides the soft keyboard on the Message EditText view.
	 * 
	 * @param showKeyboard - Boolean to either show or hide the soft keyboard.
	 */
	private void showSoftKeyboard(boolean showKeyboard, View view){
		if (_debug) Log.v("QuickReplyActivity.showSoftKeyboard()");
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// This will only trigger it if no physical keyboard is open.
		try{
			//if(inputMethodManager != null){
				if(showKeyboard){
					inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
				}else{
					inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			//}
		}catch(Exception ex){
			if (_debug) Log.e("QuickReplyActivity.showSoftKeyboard() ERROR: " + ex.toString());
		}
	}
	
}
