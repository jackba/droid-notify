package apps.droidnotify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class is the view which the ViewFlipper displays for each notification.
 * 
 * @author Camille Sévigny
 */
public class QuickReplyView extends LinearLayout {

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private QuickReplyActivityNEW _quickReplyActivity = null;
	private int _notificationType = -1;
	private int _notificationSubType = -1;
	private String _themePackageName = null;
	private Resources _resources = null;
	private String _sendTo = null;
	private String _name = null;
	private String _message = null;
	
	private LinearLayout _replyWindowLinearLayout = null;
	
	private TextView _titleTextView = null;
	private TextView _sendToTextView = null;	
	private TextView _charactersRemainingTextView = null;
	
	private EditText _messageEditText = null;

	private Button _sendButton = null;
	private Button _cancelButton = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * Class Constructor.
     */	
	public QuickReplyView(Context context, int notificationType, int notificationSubType, String sendTo, String name, String message) {
	    super(context);
	    _debug = Log.getDebug();;
	    if (_debug) Log.v("QuickReplyView.QuickReplyView()");
	    _context = context;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    _quickReplyActivity = (QuickReplyActivityNEW) context;
	    _notificationType = notificationType;
	    _notificationSubType = notificationSubType;
	    _sendTo = sendTo;
		_name = name;
		_message = message;
		View.inflate(context, R.layout.reply, this);
	    initLayoutItems();
		setLayoutProperties();
		setupLayoutTheme();
	    setupViewButtons();
	    setupEditTextFilter();
	    setupTextWatcher();
	    populateViewInfo();
	}
	
	/**
	 * Save the message as a draft.
	 */
	public String getSendMessage(){
		if (_debug) Log.v("QuickReplyActivity.getSendMessage()");
		return _messageEditText.getText().toString();
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application's Context.
	 */
	private void initLayoutItems() {
		if (_debug) Log.v("QuickReplyView.initLayoutItems()");
		
		_replyWindowLinearLayout = (LinearLayout) findViewById(R.id.reply_linear_layout);
		
		_titleTextView = (TextView) findViewById(R.id.title_text_view);
		_sendToTextView = (TextView) findViewById(R.id.send_to_text_view);
		_charactersRemainingTextView = (TextView) findViewById(R.id.characters_remaining_text_view);
		
		_messageEditText = (EditText) findViewById(R.id.message_edit_text);

		_sendButton = (Button) findViewById(R.id.send_button);
		_cancelButton = (Button) findViewById(R.id.cancel_button);
	}
	
	/**
	 * Set properties on the Notification popup window.
	 */
	private void setLayoutProperties(){
		if (_debug) Log.v("QuickReplyView.setLayoutProperties()");
		//Set the width padding based on the user preferences.
		int windowPaddingTop = 0;
		int windowPaddingBottom = 0;
		int windowPaddingLeft = Integer.parseInt(_preferences.getString(Constants.POPUP_WINDOW_WIDTH_PADDING_KEY, "0"));
		int windowPaddingRight = windowPaddingLeft;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(windowPaddingLeft, windowPaddingTop, windowPaddingRight, windowPaddingBottom);
		_replyWindowLinearLayout.setLayoutParams(layoutParams);
	}
	
	/**
	 * Setup the layout graphical items based on the current theme.
	 */
	private void setupLayoutTheme(){
		if (_debug) Log.v("QuickReplyView.setupLayoutTheme()");
		_themePackageName = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		Drawable layoutBackgroundDrawable = null;
		int textColorID = 0;
		int buttonTextColorID = 0;
		if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
			if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_THEME)){
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
			}else if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_V2_THEME)){
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel_v2);
			}else if(_themePackageName.equals(Constants.DARK_TRANSLUCENT_V3_THEME)){
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel_v3);
			}
			textColorID = _resources.getColor(R.color.text_color);
			buttonTextColorID = _resources.getColor(R.color.button_text_color);	
		}else{	
			try{
				_resources = _context.getPackageManager().getResourcesForApplication(_themePackageName);
				layoutBackgroundDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/background_panel", null, null));
				textColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/text_color", null, null));
				buttonTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/button_text_color", null, null));
			}catch(NameNotFoundException ex){
				Log.e("QuickReplyView.setupLayoutTheme() Loading Theme Package ERROR: " + ex.toString());
				_themePackageName = Constants.DARK_TRANSLUCENT_THEME;
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
				textColorID = _resources.getColor(R.color.text_color);
				buttonTextColorID = _resources.getColor(R.color.button_text_color);
			}
		}
		
		_replyWindowLinearLayout.setBackgroundDrawable(layoutBackgroundDrawable);
		
		_titleTextView.setTextColor(textColorID);
		_charactersRemainingTextView.setTextColor(textColorID);
		
		_sendButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
		_cancelButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));	

		_sendButton.setTextColor(buttonTextColorID);
		_cancelButton.setTextColor(buttonTextColorID);
	}
	
	/**
	 * Get the StateListDrawable object for the certain button types associated with the current theme.
	 * 
	 * @param buttonType - The button type we want to retrieve.
	 * 
	 * @return StateListDrawable - Returns a Drawable that contains the state specific images for this theme.
	 */
	private StateListDrawable getThemeButton(int buttonType){
		if (_debug) Log.v("QuickReplyView.getThemeButton()");
		StateListDrawable stateListDrawable = new StateListDrawable();
		switch(buttonType){
			case Constants.THEME_BUTTON_NORMAL:{
				if(_themePackageName.startsWith(Constants.DARK_TRANSLUCENT_THEME)){
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.btn_dark_translucent_pressed));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.btn_dark_translucent_normal));
				}else{
					stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_pressed", null, null)));
					stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_normal", null, null)));
				}
				return stateListDrawable;
			}
		}
		return null;
	}

	/**
	 * Sets up the QuickReplyView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupViewButtons() {
		if (_debug) Log.v("QuickReplyView.setupViewButtons()");
		try{ 
		    //Disable the Send button initially.
		    _sendButton.setEnabled(false);
		    _sendButton.setOnClickListener(new View.OnClickListener(){
	            public void onClick(View view) {
	            	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	            	if(_quickReplyActivity.sendQuickReply(_messageEditText.getText().toString())){
		                //Set the result for this activity.
	            		_quickReplyActivity.setResult(Activity.RESULT_OK);
		                //Finish Activity.
		                _quickReplyActivity.finish();
	            	}
	            }
	        });
		    if(_preferences.getBoolean(Constants.DISPLAY_QUICK_REPLY_CANCEL_BUTTON_KEY, false)){
		    	_cancelButton.setOnClickListener(new View.OnClickListener(){
		            public void onClick(View view) {
		            	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		            	//Set the result for this activity.
		            	_quickReplyActivity.setResult(Activity.RESULT_CANCELED);
		            	//Finish Activity.
		            	_quickReplyActivity.finish();                
		            }
		        });
	    	}else{
	    		_cancelButton.setVisibility(View.GONE);
	    	}
		}catch(Exception ex){
			Log.e("QuickReplyView.setupViewButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Add filters to the EditText.
	 */
	private void setupEditTextFilter(){
		if (_debug) Log.v("QuickReplyView.setupEditTextFilter()");
	    try{
		    //Update the message size limit based on the reply type.
			InputFilter[] FilterArray = new InputFilter[1];
		    switch(_notificationType){
		    	case Constants.NOTIFICATION_TYPE_SMS:{
			    	break;
			    }
				case Constants.NOTIFICATION_TYPE_TWITTER:{	   
					FilterArray[0] = new InputFilter.LengthFilter(140);
			    	_messageEditText.setFilters(FilterArray);
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{	    		
					if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						//Do Nothing										
					}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						//Do Nothing										
					}
					break;
				}
		    }	
		}catch(Exception ex){
			Log.e("QuickReplyView.setupEditTextFilter() ERROR: " + ex.toString());
		}	
	}
	
	/**
	 * Add a TextWatcher to the EditText.
	 */
	private void setupTextWatcher(){
		if (_debug) Log.v("QuickReplyView.setupTextWatcher()");
	    try{
		    _messageEditText.addTextChangedListener(new TextWatcher() {
		    	public void afterTextChanged(Editable s){
		    		//Do Nothing.
		    	}
		    	public void beforeTextChanged(CharSequence s, int start, int count, int after){
		    		//Do Nothing.
		    	}
		    	public void onTextChanged(CharSequence s, int start, int before, int count){
		    		//Enable the Send button if there is text in the EditText layout.
		    		int maxCharacters = -1;
		    		int characterBundleAmount = 160;
		    		boolean useCharacterBundles = false;
		    		if(s.length() > 0){
		    			_sendButton.setEnabled(true);
		    		}else{
		    			_sendButton.setEnabled(false);
		    		}
		    		switch(_notificationType){
				    	case Constants.NOTIFICATION_TYPE_SMS:{
				    		maxCharacters = -1;
				    		characterBundleAmount = 160;
				    		useCharacterBundles = true;
					    	break;
					    }
						case Constants.NOTIFICATION_TYPE_TWITTER:{	   
							maxCharacters = 140;
							characterBundleAmount = 140;
							useCharacterBundles = false;
							break;
						}
				    }
		    		int numberOfBundles = s.length() / characterBundleAmount;
		    		int charactersRemaining = 0;
		    		if(maxCharacters == -1){
		    			charactersRemaining = characterBundleAmount - (s.length() - (numberOfBundles * characterBundleAmount));
		    		}else{
		    			charactersRemaining = maxCharacters - (s.length() - (numberOfBundles * maxCharacters));
		    		}
		    		String charactersRemainingText = null;
		    		if(useCharacterBundles){
			    		charactersRemainingText = String.valueOf(numberOfBundles) + "/" + String.valueOf(charactersRemaining);
		    		}else{
		    			charactersRemainingText = String.valueOf(charactersRemaining);
		    		}
		    		_charactersRemainingTextView.setText(charactersRemainingText);
		    	}
		    });	
		}catch(Exception ex){
			Log.e("QuickReplyView.setupTextWatcher() ERROR: " + ex.toString());
		}	
	}
	
	/**
	 * Populate the information of this View.
	 */
	private void populateViewInfo(){
		if (_debug) Log.v("QuickReplyView.populateViewInfo()");
	    try{
			switch(_notificationType){
		    	case Constants.NOTIFICATION_TYPE_SMS:{
		    		if(_sendTo == null){
		    			if (_debug) Log.v("QuickReplyActivity.parseQuickReplyParameters() Send To value is null. Exiting...");
		    			return;
		    		}
		    		if(!_name.equals("")){
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _name + " (" + _sendTo + ")");
		    		}else{
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _sendTo);
		    		}		
		    		if(_message != null){
		    			_messageEditText.setText(_message);
		    		}
		    		_titleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply, 0, 0, 0);
			    	break;
			    }
				case Constants.NOTIFICATION_TYPE_TWITTER:{
		    		if(_sendTo == null){
		    			if (_debug) Log.v("QuickReplyActivity.parseQuickReplyParameters() Send To value is null. Exiting...");
		    			return;
		    		}
		    		if(!_name.equals("")){
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _name + " (" + _sendTo + ")");
		    		}else{
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _sendTo);
		    		}		
		    		if(_message != null){
		    			_messageEditText.setText(_message);
		    		}		   
					if(_notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
						//Do Nothing
					}
					_titleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twitter, 0, 0, 0);
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		    		if(_sendTo == null){
		    			if (_debug) Log.v("QuickReplyActivity.parseQuickReplyParameters() Send To value is null. Exiting...");
		    			return;
		    		}
		    		if(!_name.equals("")){
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _name + " (" + _sendTo + ")");
		    		}else{
		    			_sendToTextView.setText(_context.getString(R.string.to_text) + ": " + _sendTo);
		    		}		
		    		if(_message != null){
		    			_messageEditText.setText(_message);
		    		}		    		
					if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
						//Do Nothing										
					}else if(_notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
						//Do Nothing										
					}
					_titleTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.facebook, 0, 0, 0);
					break;
				}
		    }	
		}catch(Exception ex){
			Log.e("QuickReplyView.populateViewInfo() ERROR: " + ex.toString());
		}	
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator)_quickReplyActivity.getSystemService(Context.VIBRATOR_SERVICE);
		//Perform the haptic feedback based on the users preferences.
		if(_preferences.getBoolean(Constants.HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(50);
			}
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(100);
			}
		}
	}
	
}