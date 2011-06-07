package apps.droidnotify;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
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
		if (_debug) Log.v("NotificationActivity.setBundle()");
	    _bundle = bundle;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return Bundle - The bundle passed into this Activity.
	 */
	public Bundle getBundle() {
		if (_debug) Log.v("NotificationActivity.getBundle()");
	    return _bundle;
	} 
	
	/**
	 * Set the context property.
	 * 
	 * @param context - Application's Context.
	 */
	public void setContext(Context context) {
		if (_debug) Log.v("NotificationActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return Context - Application's Context.
	 */
	public Context getContext() {
		if (_debug) Log.v("NotificationActivity.getContext()");
	    return _context;
	}
	
	/**
	 * Set the sendButton property.
	 * 
	 * @param sendButton - Send Button.
	 */
	public void setSendButton(Button sendButton) {
		if (_debug) Log.v("NotificationActivity.setSendButton()");
	    _sendButton = sendButton;
	}
	
	/**
	 * Get the sendButton property.
	 * 
	 * @return Button - Send Button.
	 */
	public Button getSendButton() {
		if (_debug) Log.v("NotificationActivity.getSendButton()");
	    return _sendButton;
	}

	/**
	 * Set the cancelButton property.
	 * 
	 * @param cancelButton - Cancel Button.
	 */
	public void setCancelButton(Button cancelButton) {
		if (_debug) Log.v("NotificationActivity.setCancelButton()");
	    _cancelButton = cancelButton;
	}
	
	/**
	 * Get the cancelButton property.
	 * 
	 * @return Button - Cancel Button.
	 */
	public Button getCancelButton() {
		if (_debug) Log.v("NotificationActivity.getCancelButton()");
	    return _cancelButton;
	}
	
	/**
	 * Set the toEditText property.
	 * 
	 * @param toEditText - To Edit Text.
	 */
	public void setToEditText(EditText toEditText) {
		if (_debug) Log.v("NotificationActivity.setToEditText()");
	    _toEditText = toEditText;
	}
	
	/**
	 * Get the toEditText property.
	 * 
	 * @return EditText - To Edit Text.
	 */
	public EditText getToEditText() {
		if (_debug) Log.v("NotificationActivity.getToEditText()");
	    return _toEditText;
	}
	
	/**
	 * Set the toEditText property.
	 * 
	 * @param toEditText - To Edit Text.
	 */
	public void setMessageEditText(EditText messageEditText) {
		if (_debug) Log.v("NotificationActivity.setMessageEditText()");
	    _messageEditText = messageEditText;
	}
	
	/**
	 * Get the messageEditText property.
	 * 
	 * @return EditText - To Edit Text.
	 */
	public EditText getMessageEditText() {
		if (_debug) Log.v("NotificationActivity.getMessageEditText()");
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
        //Do Nothing (For Now).
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
	    //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	    //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.quickreplytitlebar);
	    requestWindowFeature(Window.FEATURE_LEFT_ICON);
	    setContentView(R.layout.smsreply);
	    setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_menu_start_conversation);
	    setTitle("Quick Reply");    
	    setSendButton((Button)findViewById(SEND_BUTTON));
	    setCancelButton((Button)findViewById(CANCEL_BUTTON));
	    setToEditText((EditText)findViewById(TO_EDIT_TEXT));
	    setMessageEditText((EditText)findViewById(MESSAGE_EDIT_TEXT));
	    Bundle extrasBundle = getIntent().getExtras();
	    parseQuickReplyParameters(extrasBundle);
	    //Setup Activities buttons.
	    setupButtons();
	}
	
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		_debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    _debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onResume()");
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("QuickReplyActivity.onPause()");
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
            	sendSMSMessage(); 
            }
        });
	    Button cancelButton = getCancelButton(); 
	    cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {     
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
        		Toast.makeText(getBaseContext(), "Please enter a number to send the message to.", Toast.LENGTH_SHORT).show();
        	}else if(message.length()<= 0){
        		Toast.makeText(getBaseContext(), "Please enter a message to send.", Toast.LENGTH_SHORT).show();
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
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Message not sent: Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Message not sent: No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Message not sent: Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Message not sent: Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
                //Finish Activity.
                finishActivity();
            }
        }, new IntentFilter(SMS_SENT));
 
        //When the SMS has been delivered.
        //registerReceiver(new BroadcastReceiver(){
        //    @Override
        //    public void onReceive(Context arg0, Intent arg1) {
        //        switch (getResultCode())
        //        {
        //            case Activity.RESULT_OK:
        //                Toast.makeText(getBaseContext(), "Message delivered", Toast.LENGTH_SHORT).show();
        //                break;
        //            case Activity.RESULT_CANCELED:
        //                Toast.makeText(getBaseContext(), "Message not delivered", Toast.LENGTH_SHORT).show();
        //                break;                        
        //        }
        //    }
        //}, new IntentFilter(SMS_DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }
	
}
