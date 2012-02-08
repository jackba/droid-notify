package apps.droidnotify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.sms.SMSCommon;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This is the quick reply activity that is used to send sms messages.
 * 
 * @author Camille Sévigny
 */
public class QuickReplyActivityNEW extends Activity {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private long _sendToID = -1;
	private String _sendTo = null;
	private String _name = null;
	private String _message = null;
	private int _notificationType = -1;
	private int _notificationSubType = -1;
	private boolean _messageSent = false;
	private QuickReplyView _quickReplyView = null;

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
	
	/**
	 * Send simple SMS message.
	 * 
	 * @return boolean - Returns true if the message was sent.
	 */
	public boolean sendQuickReply(String message){
		if (_debug) Log.v("QuickReplyActivity.sendQuickReply()");
		switch(_notificationType){
	    	case Constants.NOTIFICATION_TYPE_SMS:{
	            if(_sendTo.length()>0 && message.length()>0){                
	                if(SMSCommon.sendSMS(_context, _sendTo, message)){
        				_messageSent = true;
        				return true;
            		}else{
            			return false;
            		}
	            }else{
	            	if(_sendTo.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.phone_number_error_text), Toast.LENGTH_LONG).show();
	            	}else if(message.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.message_error_text), Toast.LENGTH_LONG).show();
	            	}
	            	return false;
	            }
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
	            if(_sendTo.length()>0 && message.length()>0){ 
	            	if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
	            		if(TwitterCommon.sendTwitterDirectMessage(_context, _sendToID, message)){
	        				_messageSent = true;
	        				return true;
	            		}else{
	            			return false;
	            		}
	            	}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
	            		if(TwitterCommon.sendTweet(_context, _sendToID, message, true)){
	        				_messageSent = true;
	        				return true;
	            		}else{
	            			return false;
	            		}
	            	}else{
	            		return false;
	            	}
	            }else{
	            	if(_sendTo.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.address_error_text), Toast.LENGTH_LONG).show();
	            	}else if(message.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.message_error_text), Toast.LENGTH_LONG).show();
	            	}
	            	return false;
	            }
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				if(_sendTo.length()>0 && message.length()>0){  
					if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						

		                return true;
					}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						

		                return true;
					} else{
	            		return false;
	            	}
	            }else{
	            	if(_sendTo.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.address_error_text), Toast.LENGTH_LONG).show();
	            	}else if(message.length()<= 0){
	            		Toast.makeText(getBaseContext(), getString(R.string.message_error_text), Toast.LENGTH_LONG).show();
	            	}
	            	return false;
	            }
			}
		}
        return false;
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
	    _context = getApplicationContext();
		_debug = Log.getDebug();
	    if (_debug) Log.v("QuickReplyActivity.onCreate()");
	    Common.setInQuickReplyAppFlag(_context, true);
	    Common.setApplicationLanguage(_context, this);
	    _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
	    //Don't rotate the Activity when the screen rotates based on the user preferences.
	    if(!_preferences.getBoolean(Constants.LANDSCAPE_SCREEN_ENABLED_KEY, false)){
	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	    //Get main window for this Activity.
	    Window mainWindow = getWindow(); 
	    //Set Blur 
	    if(_preferences.getBoolean(Constants.QUICK_REPLY_BLUR_SCREEN_BACKGROUND_ENABLED_KEY, false)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	    }
	    //Set Dim
	    if(_preferences.getBoolean(Constants.QUICK_REPLY_DIM_SCREEN_BACKGROUND_ENABLED_KEY, true)){
	    	mainWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); 
		    WindowManager.LayoutParams params = mainWindow.getAttributes(); 
		    int dimAmt = Integer.parseInt(_preferences.getString(Constants.QUICK_REPLY_DIM_SCREEN_BACKGROUND_AMOUNT_KEY, "50"));
		    params.dimAmount = dimAmt / 100f; 
		    mainWindow.setAttributes(params); 
	    }	    //Get name and phone number from the Bundle.
	    Bundle extrasBundle = getIntent().getExtras();
	    parseQuickReplyParameters(extrasBundle);
	    _quickReplyView = new QuickReplyView(_context, _notificationType, _notificationSubType, _sendTo, _name, _message);
	    this.setContentView(_quickReplyView);
	    //Set focus to appropriate field.
	    setFocus();
	}
	
	/**
	 * Activity was started after it stopped or for the first time.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	    if (_debug) Log.v("QuickReplyActivity.onStart()");
	}
	  
	/**
	 * Activity was resumed after it was stopped or paused.
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    if (_debug) Log.v("QuickReplyActivity.onResume()");
	    setFocus();
	    Common.setInQuickReplyAppFlag(_context, true);
	}
	  
	/**
	 * Activity was paused due to a new Activity being started or other reason.
	 */
	@Override
	protected void onPause() {
	    super.onPause();
	    if (_debug) Log.v("QuickReplyActivity.onPause()");
	    showSoftKeyboard(false, (EditText) findViewById(R.id.message_edit_text));
	    Common.setInQuickReplyAppFlag(_context, false);
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
	    Common.setInQuickReplyAppFlag(_context, false);
	    saveMessageDraft();
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Gets the passed in parameters for this Activity and loads them into the text fields.
	 * 
	 * @param bundle - The bundle passed into this Activity.
	 */
	private void parseQuickReplyParameters(Bundle bundle){
		if (_debug) Log.v("QuickReplyActivity.parseQuickReplyParameters()");
		_notificationType = bundle.getInt("notificationType");
		_notificationSubType = bundle.getInt("notificationSubType");
		switch(_notificationType){
	    	case Constants.NOTIFICATION_TYPE_SMS:{
	    		_sendTo = bundle.getString("sendTo");
	    		_name = bundle.getString("name");
	    		_message = bundle.getString("message");
		    	break;
		    }
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				_sendToID = bundle.getLong("sendToID");
				_sendTo = bundle.getString("sendTo");
	    		_name = bundle.getString("name");
	    		_message = bundle.getString("message");
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				_sendToID = bundle.getLong("sendToID");
				_sendTo = bundle.getString("sendTo");
	    		_name = bundle.getString("name");
	    		_message = bundle.getString("message");
				break;
			}
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
			if(inputMethodManager != null){
				if(showKeyboard){
					inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
				}else{
					inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		}catch(Exception ex){
			Log.e("QuickReplyActivity.showSoftKeyboard() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Save the message as a draft.
	 */
	private void saveMessageDraft(){
		if (_debug) Log.v("QuickReplyActivity.saveMessageDraft()");
		if(_messageSent){
			return;
		}else{
			if(_notificationType == Constants.NOTIFICATION_TYPE_SMS || _notificationType == Constants.NOTIFICATION_TYPE_MMS){
				if(_preferences.getBoolean(Constants.SAVE_MESSAGE_DRAFT_KEY, true)){
					try{
						SMSCommon.saveMessageDraft(_context, _sendTo, _quickReplyView.getSendMessage());
					}catch(Exception ex){
						Log.e("QuickReplyActivity.sendSMS() Insert Into Sent Foler ERROR: " + ex.toString());
					}
				}
			}
		}
	}
	
}