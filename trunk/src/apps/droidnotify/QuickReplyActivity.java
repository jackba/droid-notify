package apps.droidnotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
	 * @param bundle - Bundle for this activity.
	 */
	public void setBundle(Bundle bundle) {
		if (Log.getDebug()) Log.v("NotificationActivity.setBundle()");
	    _bundle = bundle;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return bundle - Bundle for this activity.
	 */
	public Bundle getBundle() {
		if (Log.getDebug()) Log.v("NotificationActivity.getBundle()");
	    return _bundle;
	} 
	
	/**
	 * Set the context property.
	 * 
	 * @param context - Application's Context.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("NotificationActivity.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 * 
	 * @return context - Application's Context.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("NotificationActivity.getContext()");
	    return _context;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Called when the activity is created. Set up views and notifications.
	 * 
	 * @param bundle - Activity bundle.
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onCreate()");
	    Context context = getApplicationContext();
	    setBundle(bundle);
	    setContext(context);
	    setContentView(R.layout.smsreply);
	    
	    _sendButton = (Button)findViewById(SEND_BUTTON);
	    _cancelButton = (Button)findViewById(CANCEL_BUTTON);
	    _toEditText  = (EditText)findViewById(TO_EDIT_TEXT);
	    _messageEditText = (EditText)findViewById(MESSAGE_EDIT_TEXT);
 
	    EditText toEditText  = _toEditText;
	    EditText messageEditText = _messageEditText;
	    
	    //Initialize the Activities 
	    //Setup Activities buttons.
	    setupButtons();
	    
	}
	/**
	 * Handle the result passed back from other Activity.
	 * 
	 * @param requestCode - Int value indicating the request it is returning from.
	 * @param resultCode - Int value indicating the result returned.
	 * @param intent - Intent data passed as part of the result.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (Log.getDebug()) Log.v("QuickReplyActivity.onActivityResult()");
//		if (requestCode == PICK_CONTACT_REQUEST) {
//            if (resultCode == RESULT_OK) {
//            	
//            }
//		}
	}
	
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onResume()");
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onPause()");
	}
	  
	/**
	 * Activity was stopped due to a new Activity being started or other reason.
	 */
	@Override
	protected void onStop() {
	    super.onStop();
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onStop()");
	}
	  
	/**
	 * Activity was stopped and closed out completely.
	 */
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (Log.getDebug()) Log.v("QuickReplyActivity.onDestroy()");
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Customized activity finish.
	 * This closes this activity screen.
	 */
	public void finishActivity() {
		if (Log.getDebug()) Log.v("QuickReplyActivity.finishActivity()");
	    // Finish the activity.
	    finish();
	}
	
	/**
	 * Setup the Quick Reply buttons.
	 */
	private void setupButtons(){
		if (Log.getDebug()) Log.v("QuickReplyActivity.setupButtons()");
	    Button sendButton = _sendButton; 
	    sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {     
            	

                
            }
        });
	    Button cancelButton = _cancelButton; 
	    cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {     
            	finishActivity();                
            }
        });
	}
}
