package apps.droidnotify;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;

import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.emoji.EmojiCommon;
import apps.droidnotify.k9.K9Common;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;
import apps.droidnotify.sms.SMSCommon;

/**
 * This class is the view which the ViewFlipper displays for each notification.
 * 
 * @author Camille Sévigny
 */
public class NotificationView extends LinearLayout {

	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;
	private Context _context = null;
	private SharedPreferences _preferences = null;
	private NotificationActivity _notificationActivity = null;
	private NotificationViewFlipper _notificationViewFlipper = null;
	private Notification _notification = null;
	private int _notificationType = -1;
	private int _notificationSubType = -1;
	private String _themePackageName = null;
	private Resources _resources = null;
	
	private LinearLayout _notificationWindowLinearLayout = null;	
	private LinearLayout _contactLinearLayout = null;
	private LinearLayout _buttonLinearLayout = null;
	private LinearLayout _imageButtonLinearLayout = null;
	private LinearLayout _quickReplyLinearLayout = null;
	private LinearLayout _notificationInfoLinearLayout = null;
	
	private RelativeLayout _headerRelativeLayout = null;
	
	private TextView _contactNameTextView = null;
	private TextView _contactNumberTextView = null;
	private TextView _notificationCountTextView = null;
	private TextView _notificationInfoTextView = null;
	private TextView _notificationDetailsTextView = null;
	private TextView _privacyLinkTextView = null;
	private TextView _calendarSnoozeTextView = null;
	
	private Spinner _calendarSnoozeSpinner = null;
	
	private ImageView _notificationIconImageView = null;
	private ImageView _photoImageView = null;
	
	private Button _previousButton = null;
	private Button _nextButton = null;
	
	private Button _dismissButton = null;
	private Button _deleteButton = null;
	private Button _callButton = null;
	private Button _replyButton = null;
	private Button _viewButton = null;
	
	private ImageButton _dismissImageButton = null;
	private ImageButton _deleteImageButton = null;
	private ImageButton _callImageButton = null;
	private ImageButton _replyImageButton = null;
	private ImageButton _viewImageButton = null;
	
	private ProgressBar _photoProgressBar = null;
	
	private int _listSelectorBackgroundColorResourceID = 0;
	private int _listSelectorBackgroundTransitionColorResourceID = 0;
	private Drawable _listSelectorBackgroundDrawable = null;
	private TransitionDrawable _listSelectorBackgroundTransitionDrawable = null;

	private ImageButton _snoozeImageButton = null;
	
	private EditText _messageEditText = null;

	private ImageButton _rescheduleImageButton = null;
	private ImageButton _ttsImageButton = null;
	
	private ImageButton _sttImageButton = null;
	private ImageButton _sendImageButton = null;
	
	boolean _softKeyboardOnScreen = false;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * Class Constructor.
     */	
	public NotificationView(Context context) {
		super(context);
	}
	
	/**
     * Class Constructor.
     */	
	public NotificationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
     * Class Constructor.
     */	
	public NotificationView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	/**
     * Class Constructor.
     */	
	public NotificationView(Context context, Notification notification){
		super(context);
	    _debug = Log.getDebug(context);;
	    if (_debug) Log.v(_context, "NotificationView.NotificationView()");
	    _context = context;
	    try{
		    _preferences = PreferenceManager.getDefaultSharedPreferences(context);
		    _notificationActivity = (NotificationActivity) context;
		    _notification = notification;
		    _notificationType = notification.getNotificationType();
			//Adjust for Preview notifications.
			if(_notificationType > 1999){
				_notificationType -= 2000;
			}
		    _notificationSubType = notification.getNotificationSubType();
		    View.inflate(context, R.layout.notification_reply, this);
		    initLayoutItems();
			setLayoutProperties();
			setupLayoutTheme();
		    setupQuickReply();
		    initLongPressView();
		    setupViewHeaderButtons();
		    setupViewButtons();
		    populateViewInfo();
		    setKeyboardListener();
	    }catch(Exception ex){
	    	Log.e(_context, "NotificationView.NotificationView() ERROR: " + ex.toString());
	    }
	}
	
	/**
	 * Get the Notification object.
	 * 
	 * @return Notification - The Notification object associated with this view.
	 */
	public Notification getNotification(){
		return _notification;
	}
	
	/**
	 * Reschedule a notification.
	 */
	public void rescheduleNotification(){
		if (_debug) Log.v(_context, "NotificationView.rescheduleNotification()");
		long rescheduleInterval = Long.parseLong(_preferences.getString(Constants.RESCHEDULE_TIME_KEY, Constants.RESCHEDULE_TIME_DEFAULT)) * 60 * 1000;
		_notification.reschedule(System.currentTimeMillis() + rescheduleInterval, Constants.PENDING_INTENT_TYPE_RESCHEDULE);
		dismissNotification(true);
	}
	
  	/**
	 * Set the text in the message edit text of the active notification view.
	 * 
	 * @param recognizedTextResults - An array of strings of the text that was returned from the voice recognizer.
	 */
	public void setQuickReplyText(ArrayList<String> recognizedTextResults){
		if (_debug) Log.v(_context, "NotificationView.setQuickReplyText()");
		try{
	 		String currentText = _messageEditText.getText().toString();
	 		String endCharacter = "";
	 		int currentTextLength = currentText.length();
	 		if(currentTextLength > 0){
	 			endCharacter = _messageEditText.getText().toString().substring(currentTextLength - 1);
	 		}
	 		if(recognizedTextResults.size() > 0){
	 			if(endCharacter.equals(" ")){
	 				_messageEditText.append(recognizedTextResults.get(0));
	 			}else if(endCharacter.equals("\n")){
	 				_messageEditText.append(recognizedTextResults.get(0));
	 			}else{
	 				//Append a space before the newly inserted text.
	 				_messageEditText.append(" " + recognizedTextResults.get(0));
	 			}
	 		}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setQuickReplyText() ERROR: " + ex.toString());
		}
	}

  	/**
  	 * Set the max lines property of the notification.
  	 */
  	public void setNotificationBodyMaxLines(int maxLines){
		try{
			_notificationDetailsTextView.setMaxLines(maxLines);
    		_notificationDetailsTextView.setMovementMethod(new ScrollingMovementMethod());
  		}catch(Exception ex){
  			Log.e(_context, "NotificationView.setNotificationBodyMaxLines() ERROR: " + ex.toString());
  		}
  	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application's Context.
	 */
	private void initLayoutItems(){
		if (_debug) Log.v(_context, "NotificationView.initLayoutItems()");
		
		_notificationWindowLinearLayout = (LinearLayout) findViewById(R.id.notification_linear_layout);
		_contactLinearLayout = (LinearLayout) findViewById(R.id.contact_wrapper_linear_layout);
	    _buttonLinearLayout = (LinearLayout) findViewById(R.id.button_linear_layout);
	    _imageButtonLinearLayout = (LinearLayout) findViewById(R.id.image_button_linear_layout);
	    _quickReplyLinearLayout = (LinearLayout) findViewById(R.id.quickreply_linear_layout);
	    _notificationInfoLinearLayout = (LinearLayout) findViewById(R.id.notification_info_linear_layout);
	    
	    _headerRelativeLayout = (RelativeLayout) findViewById(R.id.header_relative_layout);
		
		_contactNameTextView = (TextView) findViewById(R.id.contact_name_text_view);
		_contactNumberTextView = (TextView) findViewById(R.id.contact_number_text_view);
		_notificationCountTextView = (TextView) findViewById(R.id.notification_count_text_view);
		_notificationInfoTextView = (TextView) findViewById(R.id.notification_info_text_view);	    
		_notificationDetailsTextView = (TextView) findViewById(R.id.notification_details_text_view);
		_privacyLinkTextView = (TextView) findViewById(R.id.link_text_view);
		_calendarSnoozeTextView = (TextView) findViewById(R.id.calendar_snooze_text_view);
		
		_calendarSnoozeSpinner = (Spinner) findViewById(R.id.calendar_snooze_spinner);
		
		_notificationIconImageView = (ImageView) findViewById(R.id.notification_type_icon_image_view);
		_photoImageView = (ImageView) findViewById(R.id.contact_photo_image_view);

		_rescheduleImageButton = (ImageButton) findViewById(R.id.reschedule_button_image_button);
		_ttsImageButton = (ImageButton) findViewById(R.id.tts_button_image_button);
		
    	_previousButton = (Button) findViewById(R.id.previous_button);
		_nextButton = (Button) findViewById(R.id.next_button);		

		_dismissButton = (Button) findViewById(R.id.dismiss_button);
		_deleteButton = (Button) findViewById(R.id.delete_button);
		_callButton = (Button) findViewById(R.id.call_button);
		_replyButton = (Button) findViewById(R.id.reply_button);
		_viewButton = (Button) findViewById(R.id.view_button);
		
		_dismissImageButton = (ImageButton) findViewById(R.id.dismiss_image_button);
		_deleteImageButton = (ImageButton) findViewById(R.id.delete_image_button);
		_callImageButton = (ImageButton) findViewById(R.id.call_image_button);
		_replyImageButton = (ImageButton) findViewById(R.id.reply_image_button);
		_viewImageButton = (ImageButton) findViewById(R.id.view_image_button);
		
		_photoProgressBar = (ProgressBar) findViewById(R.id.contact_photo_progress_bar);
		
		_snoozeImageButton = (ImageButton) findViewById(R.id.snooze_image_button);

		_messageEditText = (EditText) findViewById(R.id.message_edit_text);
		_sttImageButton  = (ImageButton) findViewById(R.id.stt_image_button);
		_sendImageButton  = (ImageButton) findViewById(R.id.send_image_button);
	    
		_notificationViewFlipper = _notificationActivity.getNotificationViewFlipper();
	}
	
	/**
	 * Set properties on the Notification popup window.
	 */
	private void setLayoutProperties(){
		if (_debug) Log.v(_context, "NotificationView.setLayoutProperties()");
		//Initialize The Button Views
		_buttonLinearLayout.setVisibility(View.GONE);
    	_imageButtonLinearLayout.setVisibility(View.GONE);
    	//Remove the clickable attribute to the notification header.
		if(_preferences.getBoolean(Constants.CONTEXT_MENU_DISABLED_KEY, false)){
			_contactLinearLayout.setClickable(false);
		}
		//Set the width padding based on the user preferences.
		int windowPaddingTop = 0;
		int windowPaddingBottom = 0;
		int windowPaddingLeft = Integer.parseInt(_preferences.getString(Constants.POPUP_WINDOW_WIDTH_PADDING_KEY, Constants.POPUP_WINDOW_WIDTH_PADDING_DEFAULT));
		int windowPaddingRight = windowPaddingLeft;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(windowPaddingLeft, windowPaddingTop, windowPaddingRight, windowPaddingBottom);
		_notificationWindowLinearLayout.setLayoutParams(layoutParams);
	}
	
	/**
	 * Setup the layout graphical items based on the current theme.
	 */
	private void setupLayoutTheme(){
		if (_debug) Log.v(_context, "NotificationView.setupLayoutTheme()");
		_themePackageName = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		if (_debug) Log.v(_context, "NotificationView.setupLayoutTheme() ThemePackageName: " + _themePackageName);
		final Resources localRresources = _context.getResources();
		Drawable layoutBackgroundDrawable = null;
		Drawable rescheduleDrawable = null;
		Drawable ttsDrawable = null;
		Drawable sttDrawable = null;
		Drawable sendDrawable = null;
		int notificationCountTextColorID = 0;
		int headerInfoTextcolorID = 0;
		int contactNameTextColorID = 0;
		int contactNumberTextColorID = 0;
		int bodyTextColorID = 0;
		int buttonTextColorID = 0;
		int quickReplyTextColorID = 0;
		int spinnerTextColorID = 0;
		if(!_themePackageName.startsWith(Constants.APP_THEME_PREFIX)){
			_themePackageName = Constants.APP_THEME_DEFAULT;
		}
		if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME)){
			_resources = _context.getResources();
			layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
			rescheduleDrawable = _resources.getDrawable(R.drawable.ic_reschedule);
			ttsDrawable = _resources.getDrawable(R.drawable.ic_tts);
			sttDrawable = _resources.getDrawable(R.drawable.ic_stt);
			sendDrawable = _resources.getDrawable(R.drawable.ic_send);
			notificationCountTextColorID = _resources.getColor(R.color.notification_count_text_color);	
			headerInfoTextcolorID = _resources.getColor(R.color.header_info_text_color);	
			contactNameTextColorID = _resources.getColor(R.color.contact_name_text_color);	
			contactNumberTextColorID = _resources.getColor(R.color.contact_number_text_color);	
			bodyTextColorID = _resources.getColor(R.color.body_text_color);
			quickReplyTextColorID = _resources.getColor(R.color.quick_reply_text_color);
			buttonTextColorID = _resources.getColor(R.color.button_text_color);	
			spinnerTextColorID = _resources.getColor(R.color.spinner_text_color);
		}else if(_themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
			_resources = _context.getResources();
			layoutBackgroundDrawable = _resources.getDrawable(android.R.drawable.dialog_frame);
			rescheduleDrawable = _resources.getDrawable(R.drawable.ic_reschedule);
			ttsDrawable = _resources.getDrawable(R.drawable.ic_tts);
			sttDrawable = _resources.getDrawable(R.drawable.ic_stt);
			sendDrawable = _resources.getDrawable(R.drawable.ic_send);
		}else{	
			try{
				_resources = _context.getPackageManager().getResourcesForApplication(_themePackageName);
				layoutBackgroundDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/background_panel", null, null));
				try{
					rescheduleDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_reschedule", null, null));
				}catch(NotFoundException nfe){
					rescheduleDrawable = localRresources.getDrawable(R.drawable.ic_reschedule);
				}
				try{
					ttsDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_tts", null, null));
				}catch(NotFoundException nfe){
					ttsDrawable = localRresources.getDrawable(R.drawable.ic_tts);
				}
				try{
					sttDrawable =_resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_stt", null, null));
				}catch(NotFoundException nfe){
					sttDrawable = localRresources.getDrawable(R.drawable.ic_stt);
				}
				try{
					sendDrawable = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_send", null, null));
				}catch(NotFoundException nfe){
					sendDrawable = localRresources.getDrawable(R.drawable.ic_send);
				}
				notificationCountTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/notification_count_text_color", null, null));
				headerInfoTextcolorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/header_info_text_color", null, null));	
				contactNameTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/contact_name_text_color", null, null));	
				contactNumberTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/contact_number_text_color", null, null));
				bodyTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/body_text_color", null, null));
				buttonTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/button_text_color", null, null));
				try{
					quickReplyTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/quick_reply_text_color", null, null));
					spinnerTextColorID = _resources.getColor(_resources.getIdentifier(_themePackageName + ":color/spinner_text_color", null, null));
				}catch(NotFoundException nfe){
					quickReplyTextColorID = bodyTextColorID;
					spinnerTextColorID = bodyTextColorID;
				}
			}catch(NameNotFoundException ex){
				Log.e(_context, "NotificationView.setupLayoutTheme() Loading Theme Package ERROR: " + ex.toString());
				_themePackageName = Constants.APP_THEME_DEFAULT;
				_resources = _context.getResources();
				layoutBackgroundDrawable = _resources.getDrawable(R.drawable.background_panel);
				rescheduleDrawable = _resources.getDrawable(R.drawable.ic_reschedule);
				ttsDrawable = _resources.getDrawable(R.drawable.ic_tts);
				sttDrawable = _resources.getDrawable(R.drawable.ic_stt);
				sendDrawable = _resources.getDrawable(R.drawable.ic_send);
				notificationCountTextColorID = _resources.getColor(R.color.notification_count_text_color);	
				headerInfoTextcolorID = _resources.getColor(R.color.header_info_text_color);	
				contactNameTextColorID = _resources.getColor(R.color.contact_name_text_color);	
				contactNumberTextColorID = _resources.getColor(R.color.contact_number_text_color);	
				bodyTextColorID = _resources.getColor(R.color.body_text_color);
				buttonTextColorID = _resources.getColor(R.color.button_text_color);
				quickReplyTextColorID = _resources.getColor(R.color.quick_reply_text_color);
				spinnerTextColorID = _resources.getColor(R.color.spinner_text_color);
			}
		}
		
		_notificationWindowLinearLayout.setBackgroundDrawable(layoutBackgroundDrawable);			

		_previousButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NAV_PREV));
		_nextButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NAV_NEXT));
		
		if(!_themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
			
			_notificationCountTextView.setTextColor(notificationCountTextColorID);
			_notificationInfoTextView.setTextColor(headerInfoTextcolorID);
			_contactNameTextView.setTextColor(contactNameTextColorID);
			_contactNumberTextView.setTextColor(contactNumberTextColorID);
			
			_notificationDetailsTextView.setTextColor(bodyTextColorID);
			_notificationDetailsTextView.setLinkTextColor(bodyTextColorID);
			_privacyLinkTextView.setTextColor(bodyTextColorID);
			_calendarSnoozeTextView.setTextColor(bodyTextColorID);
					
			_dismissButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_deleteButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_callButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_replyButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_viewButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));

			_dismissButton.setTextColor(buttonTextColorID);
			_deleteButton.setTextColor(buttonTextColorID);
			_callButton.setTextColor(buttonTextColorID);
			_replyButton.setTextColor(buttonTextColorID);
			_viewButton.setTextColor(buttonTextColorID);
			
			_dismissImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_deleteImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_callImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_replyImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			_viewImageButton.setBackgroundDrawable(getThemeButton(Constants.THEME_BUTTON_NORMAL));
			
			_calendarSnoozeSpinner.setBackgroundDrawable(getThemeButton(Constants.THEME_SPINNER));
			_messageEditText.setBackgroundDrawable(getThemeButton(Constants.THEME_EDIT_TEXT));
			_messageEditText.setTextColor(quickReplyTextColorID);
			_messageEditText.addTextChangedListener(new TextWatcher(){				
				public void afterTextChanged(Editable s){
					Log.v(_context, "EditText afterTextChanged()");
				}				
				public void beforeTextChanged(CharSequence s, int start, int count, int after){
					Log.v(_context, "EditText beforeTextChanged()");
				}				
				public void onTextChanged(CharSequence s, int start, int before, int count){
					Log.v(_context, "EditText onTextChanged()");
				}
			});

			final int finalSpinnerTextColorID = spinnerTextColorID;
			_calendarSnoozeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
	            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	            	try{
	            		((TextView)parentView.getChildAt(0)).setTextColor(finalSpinnerTextColorID);
	            	}catch(Exception ex){
	            		Log.e(_context, "CalendarSpinner.onItemSelected() ERROR: " + ex.toString());
	            	}
	            }
				public void onNothingSelected(AdapterView<?> arg0){
					
				}
			});
			
		}
		
		_rescheduleImageButton.setImageDrawable(rescheduleDrawable);
		_ttsImageButton.setImageDrawable(ttsDrawable);
		
		_sttImageButton.setImageDrawable(sttDrawable);
		_sendImageButton.setImageDrawable(sendDrawable);
		
	}
	
	/**
	 * Setup the quick reply layout.
	 */
	private void setupQuickReply(){
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				_quickReplyLinearLayout.setVisibility(View.GONE);
				return;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				setuptQuickReplySMS();
				return;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				setuptQuickReplySMS();
				return;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				_quickReplyLinearLayout.setVisibility(View.GONE);
				return;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				_quickReplyLinearLayout.setVisibility(View.GONE);
				return;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				_quickReplyLinearLayout.setVisibility(View.GONE);
				return;
			}
		}
	
	}
	
	/**
	 * Setup the quick reply layout for SMS/MMS messages.
	 */
	private void setuptQuickReplySMS(){
		boolean quickReplyEnabled = _preferences.getBoolean(Constants.QUICK_REPLY_ENABLED_KEY, false);
		boolean smsQuickReplyEnabled = _preferences.getBoolean(Constants.SMS_QUICK_REPLY_ENABLED_KEY, false);
		if(quickReplyEnabled || smsQuickReplyEnabled){
			_quickReplyLinearLayout.setVisibility(View.VISIBLE);
			setupQuickReplyEditText();
			setupQuickReplyButtons(_notificationType, _notificationSubType);
		}else{
			_quickReplyLinearLayout.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Setup the quick reply EditText properties.
	 */
	private void setupQuickReplyEditText(){
		_messageEditText.addTextChangedListener(new TextWatcher(){
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				//Poke the screen timeout when user types.
				_notificationActivity.setScreenTimeoutAlarm();
			}				
			public void onTextChanged(CharSequence s, int start, int before, int count){
			}				
			public void afterTextChanged(Editable s){
			}				
		});
	}
	
	/**
	 * Setup the quick reply buttons.
	 * 
	 * @param notificationType - The notification type.
	 * @param notificationSubType - The notification sub type.
	 */
	private void setupQuickReplyButtons(int notificationType, int notificationSubType){		
		//Speech To Text (STT) Button.
		_sttImageButton.setOnClickListener(
			new OnClickListener(){
			    public void onClick(View view){
			    	if (_debug) Log.v(_context, "STT Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					//Cancel the reminder.
					_notification.cancelReminder();
			    	_notificationActivity.startStt();
			    }
			}
		);
		//Send Button - Notification Dependent.
		_sendImageButton.setOnClickListener(
			new OnClickListener(){
				public void onClick(View view){
			    	if (_debug) Log.v(_context, "Quick Reply Send Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	sendQuickReply();
			    }
			}
		);	
	}
	
	/**
	 * Get the StateListDrawable object for the certain button types associated with the current theme.
	 * 
	 * @param buttonType - The button type we want to retrieve.
	 * 
	 * @return StateListDrawable - Returns a Drawable that contains the state specific images for this theme.
	 */
	private StateListDrawable getThemeButton(int buttonType){
		if (_debug) Log.v(_context, "NotificationView.getThemeButton() ButtonType: " + buttonType);
		try{
			Resources localRresources = _context.getResources();
			StateListDrawable stateListDrawable = new StateListDrawable();
			switch(buttonType){
				case Constants.THEME_BUTTON_NORMAL:{
					if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME)){
						stateListDrawable.addState(new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_pressed));
						stateListDrawable.addState(new int[] {android.R.attr.state_enabled}, _resources.getDrawable(R.drawable.button_normal));
						stateListDrawable.addState(new int[] {}, _resources.getDrawable(R.drawable.button_disabled));
					}else{
						try{
							stateListDrawable.addState(new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_pressed", null, null)));
							stateListDrawable.addState(new int[] {android.R.attr.state_enabled}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_normal", null, null)));
							stateListDrawable.addState(new int[] {}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_disabled", null, null)));
						}catch(Exception ex){
							Log.e(_context, "NotificationView.getThemeButton() NORMAL BUTTON THEME ERROR: " + ex.toString());
							stateListDrawable.addState(new int[] {android.R.attr.state_enabled, android.R.attr.state_pressed}, localRresources.getDrawable(R.drawable.button_pressed));
							stateListDrawable.addState(new int[] {android.R.attr.state_enabled}, localRresources.getDrawable(R.drawable.button_normal));
							stateListDrawable.addState(new int[] {}, localRresources.getDrawable(R.drawable.button_disabled));
						}
					}
					return stateListDrawable;
				}
				case Constants.THEME_BUTTON_NAV_PREV:{
					if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME) || _themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
						stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_navigate_prev_pressed));
						stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.button_navigate_prev_normal));
					}else{
						try{
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_prev_pressed", null, null)));
							stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_prev_normal", null, null)));
						}catch(Exception ex){
							Log.e(_context, "NotificationView.getThemeButton() NAV PREV BUTTON THEME ERROR: " + ex.toString());
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, localRresources.getDrawable(R.drawable.button_navigate_prev_pressed));
							stateListDrawable.addState(new int[] { }, localRresources.getDrawable(R.drawable.button_navigate_prev_normal));
						}
					}
					return stateListDrawable;
				}
				case Constants.THEME_BUTTON_NAV_NEXT:{
					if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME) || _themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
						stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.button_navigate_next_pressed));
						stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.button_navigate_next_normal));
					}else{
						try{
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_next_pressed", null, null)));
							stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/button_navigate_next_normal", null, null)));
						}catch(Exception ex){
							Log.e(_context, "NotificationView.getThemeButton() NAV NEXT BUTTON THEME ERROR: " + ex.toString());
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, localRresources.getDrawable(R.drawable.button_navigate_next_pressed));
							stateListDrawable.addState(new int[] { }, localRresources.getDrawable(R.drawable.button_navigate_next_normal));
						}
					}
					return stateListDrawable;
				}
				case Constants.THEME_SPINNER:{
					if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME) || _themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
						stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(R.drawable.spinner_pressed));
						stateListDrawable.addState(new int[] {android.R.attr.state_focused}, _resources.getDrawable(R.drawable.spinner_focused));
						stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.spinner_normal));
					}else{
						try{
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/spinner_pressed", null, null)));
							stateListDrawable.addState(new int[] {android.R.attr.state_focused}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/spinner_focused", null, null)));
							stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/spinner_normal", null, null)));
						}catch(Exception ex){
							Log.e(_context, "NotificationView.getThemeButton() SPINNER THEME ERROR: " + ex.toString());
							stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, localRresources.getDrawable(R.drawable.spinner_pressed));
							stateListDrawable.addState(new int[] {android.R.attr.state_focused}, localRresources.getDrawable(R.drawable.spinner_focused));
							stateListDrawable.addState(new int[] { }, localRresources.getDrawable(R.drawable.spinner_normal));
						}
					}
					return stateListDrawable;
				}
				case Constants.THEME_EDIT_TEXT:{
					if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME) || _themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
						stateListDrawable.addState(new int[] {android.R.attr.state_activated}, _resources.getDrawable(R.drawable.textfield_activated));
						stateListDrawable.addState(new int[] {android.R.attr.state_focused}, _resources.getDrawable(R.drawable.textfield_focused));
						stateListDrawable.addState(new int[] { }, _resources.getDrawable(R.drawable.textfield_normal));
					}else{
						try{
							stateListDrawable.addState(new int[] {android.R.attr.state_activated}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/textfield_activated", null, null)));
							stateListDrawable.addState(new int[] {android.R.attr.state_focused}, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/textfield_focused", null, null)));
							stateListDrawable.addState(new int[] { }, _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/textfield_normal", null, null)));
						}catch(Exception ex){
							Log.e(_context, "NotificationView.getThemeButton() EDIT TEXT THEME ERROR: " + ex.toString());
							stateListDrawable.addState(new int[] {android.R.attr.state_activated}, localRresources.getDrawable(R.drawable.textfield_activated));
							stateListDrawable.addState(new int[] {android.R.attr.state_focused}, localRresources.getDrawable(R.drawable.textfield_focused));
							stateListDrawable.addState(new int[] { }, localRresources.getDrawable(R.drawable.textfield_normal));
						}
					}
					return stateListDrawable;
				}
			}
			return null;
		}catch(Exception ex){
			Log.e(_context, "NotificationView.getThemeButton() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Creates and sets up the animation event when a long press is performed on the contact wrapper View.
	 */
	private void initLongPressView(){
		if (_debug) Log.v(_context, "NotificationView.initLongPressView()");	
		try{
			if(_preferences.getBoolean(Constants.CONTEXT_MENU_DISABLED_KEY, false)){
				return;
			}
			//Load theme resources.
			String themePackageName = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
			if (_debug) Log.v(_context, "NotificationView.initLongPressView() ThemePackageName: " + themePackageName);
			if(themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME)){
				try{
					_listSelectorBackgroundDrawable = _resources.getDrawable(R.drawable.list_selector_background);
				}catch(Exception ex){
					_listSelectorBackgroundDrawable = null;
					Log.e(_context, "NotificationView.initLongPressView() List Selector Background Drawable ERROR: " + ex.toString());
				}
				try{
					_listSelectorBackgroundTransitionDrawable = (TransitionDrawable) _resources.getDrawable(R.drawable.list_selector_background_transition);
				}catch(ClassCastException classCastException){
					//This shouldn't happen, but on the emulator it does sometimes.
					_listSelectorBackgroundTransitionDrawable = null;
					Log.e(_context, "NotificationView.initLongPressView() Transition Drawable Class Cast ERROR: " + classCastException.toString());
				}
				_listSelectorBackgroundColorResourceID = _resources.getColor(R.color.list_selector_text_color);
				_listSelectorBackgroundTransitionColorResourceID = _resources.getColor(R.color.list_selector_transition_text_color);
			}else if(themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
				try{
					_listSelectorBackgroundDrawable = _resources.getDrawable(android.R.drawable.list_selector_background);
				}catch(Exception ex){
					_listSelectorBackgroundDrawable = null;
					Log.e(_context, "NotificationView.initLongPressView() List Selector Background Drawable ERROR: " + ex.toString());
				}
				_listSelectorBackgroundTransitionDrawable = null;
				_listSelectorBackgroundColorResourceID = 0;
				_listSelectorBackgroundTransitionColorResourceID = 0;
			}else{
				try{
					_listSelectorBackgroundDrawable = _resources.getDrawable(_resources.getIdentifier(themePackageName + ":drawable/list_selector_background", null, null));
				}catch(Exception ex){
					_listSelectorBackgroundDrawable = null;
					Log.e(_context, "NotificationView.initLongPressView() List Selector Background Drawable ERROR: " + ex.toString());
				}
				try{
					_listSelectorBackgroundTransitionDrawable = (TransitionDrawable) _resources.getDrawable(_resources.getIdentifier(themePackageName + ":drawable/list_selector_background_transition", null, null));
				}catch(ClassCastException classCastException){
					//This shouldn't happen, but on the emulator it does sometimes.
					_listSelectorBackgroundTransitionDrawable = null;
					Log.e(_context, "NotificationView.initLongPressView() Transition Drawable Class Cast ERROR: " + classCastException.toString());
				}
				_listSelectorBackgroundColorResourceID = _resources.getColor(_resources.getIdentifier(themePackageName + ":color/list_selector_text_color", null, null));
				_listSelectorBackgroundTransitionColorResourceID = _resources.getColor(_resources.getIdentifier(themePackageName + ":color/list_selector_transition_text_color", null, null));
			}
			//Create touch event actions.
			LinearLayout contactWrapperLinearLayout = (LinearLayout) findViewById(R.id.contact_wrapper_linear_layout);
			contactWrapperLinearLayout.setOnTouchListener( new OnTouchListener(){
					public boolean onTouch(View view, MotionEvent motionEvent){
			     		switch (motionEvent.getAction()){
				     		case MotionEvent.ACTION_DOWN:{
				     			if(_listSelectorBackgroundTransitionDrawable != null){
					        		TransitionDrawable transition = _listSelectorBackgroundTransitionDrawable;
				        			view.setBackgroundDrawable(transition);
					                transition.setCrossFadeEnabled(true);
					                transition.startTransition(300);
				     			}
				     			if(_listSelectorBackgroundTransitionColorResourceID != 0){
				                	_notificationInfoTextView.setTextColor(_listSelectorBackgroundTransitionColorResourceID);
				                	_contactNameTextView.setTextColor(_listSelectorBackgroundTransitionColorResourceID);
				                	_contactNumberTextView.setTextColor(_listSelectorBackgroundTransitionColorResourceID);
				     			}
				                break;
					        }
				     		case MotionEvent.ACTION_UP:{
				         		if(_listSelectorBackgroundDrawable != null) view.setBackgroundDrawable(_listSelectorBackgroundDrawable);
				                if(_listSelectorBackgroundColorResourceID != 0){
				                	_notificationInfoTextView.setTextColor(_listSelectorBackgroundColorResourceID);
				                	_contactNameTextView.setTextColor(_listSelectorBackgroundColorResourceID);
				                	_contactNumberTextView.setTextColor(_listSelectorBackgroundColorResourceID);
				     			}
				                break;
				     		}
				     		case MotionEvent.ACTION_CANCEL:{
				     			if(_listSelectorBackgroundDrawable != null) view.setBackgroundDrawable(_listSelectorBackgroundDrawable);
				         		if(_listSelectorBackgroundColorResourceID != 0){
					                _notificationInfoTextView.setTextColor(_listSelectorBackgroundColorResourceID);
					                _contactNameTextView.setTextColor(_listSelectorBackgroundColorResourceID);
					                _contactNumberTextView.setTextColor(_listSelectorBackgroundColorResourceID);
				         		}
				                break;
				     		}
			     		}
			     		return false;
					}
			     }
		     );
		}catch(Exception ex){
			Log.e(_context, "NotificationView.initLongPressView() ERROR: " + ex.toString());	
		}
	}
	
	/**
	 * 
	 */
	private void setupViewHeaderButtons(){
		if (_debug) Log.v(_context, "NotificationView.setupViewHeaderButtons()");
		try{
			//Previous Button
			_previousButton.setOnClickListener(
				new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v(_context, "Previous Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	_notificationViewFlipper.showPrevious();
				    }
				}
			);
			//Next Button
			_nextButton.setOnClickListener(
				new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v(_context, "Next Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	_notificationViewFlipper.showNext();
				    }
				}
			);
			//TTS Button
			if(_preferences.getBoolean(Constants.DISPLAY_TEXT_TO_SPEECH_KEY, false)){
				_ttsImageButton.setVisibility(View.VISIBLE);
				_ttsImageButton.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "TTS Image Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	_notificationActivity.speak();
					    }
					}
				);
			}else{
				_ttsImageButton.setVisibility(View.GONE);
			}
			//Reschedule Button
			if(_preferences.getBoolean(Constants.DISPLAY_RESCHEDULE_BUTTON_KEY, false)){
				_rescheduleImageButton.setVisibility(View.VISIBLE);
				_rescheduleImageButton.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Reschedule Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	rescheduleNotification();
					    }
					}
				);
			}else{
				_rescheduleImageButton.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewHeaderButtons() ERROR: " + ex.toString());
		}
	}

	/**
	 * Sets up the NotificationView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupViewButtons(){
		try{
			boolean usingImageButtons = false;
			String buttonDisplayStyle = _preferences.getString(Constants.BUTTON_DISPLAY_STYLE_KEY, Constants.BUTTON_DISPLAY_STYLE_DEFAULT);
			//Show the LinearLayout of the specified button style (ImageButton vs Button)
			if(_notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
				//Special case for generic notifications
				usingImageButtons = false;
				_buttonLinearLayout.setVisibility(View.VISIBLE);
		    	_imageButtonLinearLayout.setVisibility(View.GONE);
			}else{
				if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_ICON_ONLY)){
					usingImageButtons = true;
					_buttonLinearLayout.setVisibility(View.GONE);
			    	_imageButtonLinearLayout.setVisibility(View.VISIBLE);
				}else{
					usingImageButtons = false;
					_buttonLinearLayout.setVisibility(View.VISIBLE);
			    	_imageButtonLinearLayout.setVisibility(View.GONE);
				}
			}
			//Default all buttons to be hidden.
			_dismissButton.setVisibility(View.GONE);
			_deleteButton.setVisibility(View.GONE);
			_callButton.setVisibility(View.GONE);
			_replyButton.setVisibility(View.GONE);
			_viewButton.setVisibility(View.GONE);
			_dismissImageButton.setVisibility(View.GONE);
			_deleteImageButton.setVisibility(View.GONE);
			_callImageButton.setVisibility(View.GONE);
			_replyImageButton.setVisibility(View.GONE);
			_viewImageButton.setVisibility(View.GONE);
			if(!usingImageButtons){
				//Set button font size.
				float buttonTextSize = Float.parseFloat(_preferences.getString(Constants.BUTTON_FONT_SIZE_KEY, Constants.BUTTON_FONT_SIZE_DEFAULT));
				_dismissButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
				_deleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
				_callButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
				_replyButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
				_viewButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize);
				//Set button font to bold.
				if(_preferences.getBoolean(Constants.BUTTON_BOLD_TEXT_KEY, false)){
					_dismissButton.setTypeface(null, Typeface.BOLD);
					_deleteButton.setTypeface(null, Typeface.BOLD);
					_callButton.setTypeface(null, Typeface.BOLD);
					_replyButton.setTypeface(null, Typeface.BOLD);
					_viewButton.setTypeface(null, Typeface.BOLD);
				}else{
					_dismissButton.setTypeface(null, Typeface.NORMAL);
					_deleteButton.setTypeface(null, Typeface.NORMAL);
					_callButton.setTypeface(null, Typeface.NORMAL);
					_replyButton.setTypeface(null, Typeface.NORMAL);
					_viewButton.setTypeface(null, Typeface.NORMAL);
				}
			}
			//Setup the views buttons based on the notification type.
			switch(_notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					setupViewPhoneButtons(usingImageButtons);
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					setupViewSMSButtons(usingImageButtons, _notificationType);
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					setupViewSMSButtons(usingImageButtons, _notificationType);
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					setupViewCalendarButtons(usingImageButtons);
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					setupViewK9Buttons(usingImageButtons);
					break;
				}
				case Constants.NOTIFICATION_TYPE_GENERIC:{
					setupViewGenericButtons();
					break;
				}
			}
			setupButtonIcons(usingImageButtons, _notificationType, buttonDisplayStyle);
			setupDismissButtonLongClickAction(usingImageButtons);
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the view buttons for Phone notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 */
	private void setupViewPhoneButtons(boolean usingImageButtons){
		if (_debug) Log.v(_context, "NotificationView.setupViewPhoneButtons()");
		try{
			// Notification Count Text Button
			int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.PHONE_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
			if(notificationCountAction == 0){	
				//Do Nothing.
			}else{				
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	PhoneCommon.startCallLogViewActivity(_context, _notificationActivity, Constants.VIEW_CALL_LOG_ACTIVITY);
					    }
					}
				);			
			}
			if(_preferences.getBoolean(Constants.MISSED_CALL_PRIVACY_ENABLED_KEY, false)){
				_privacyLinkTextView.setText(R.string.click_to_view_log);
				_privacyLinkTextView.setOnClickListener(new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v(_context, "Notification Missed Call Link Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						//Cancel the reminder.
						_notification.cancelReminder();
				    	PhoneCommon.startCallLogViewActivity(_context, _notificationActivity, Constants.VIEW_CALL_LOG_ACTIVITY);
				    }
				});
			}
			if(usingImageButtons){
				//Dismiss Button
		    	if(_preferences.getBoolean(Constants.PHONE_DISPLAY_DISMISS_BUTTON_KEY, true)){
		    		_dismissImageButton.setVisibility(View.VISIBLE);
		    		_dismissImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View v){
						    	if (_debug) Log.v(_context, "Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
		    		);
		    	}else{
		    		_dismissImageButton.setVisibility(View.GONE);
		    	}
				// Call Button
				if(_preferences.getBoolean(Constants.PHONE_DISPLAY_CALL_BUTTON_KEY, true)){
					_callImageButton.setVisibility(View.VISIBLE);
		    		_callImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View v){
						    	if (_debug) Log.v(_context, "Call Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	callMissedCall();
						    }
						}
		    		);
		    	}else{
					_callImageButton.setVisibility(View.GONE);
		    	}			
			}else{
				//Dismiss Button
		    	if(_preferences.getBoolean(Constants.PHONE_DISPLAY_DISMISS_BUTTON_KEY, true)){
		    		_dismissButton.setVisibility(View.VISIBLE);
					_dismissButton.setOnClickListener(
						new OnClickListener(){
						    public void onClick(View v){
						    	if (_debug) Log.v(_context, "Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
					);
		    	}else{
		    		_dismissButton.setVisibility(View.GONE);
		    	}
				// Call Button
				if(_preferences.getBoolean(Constants.PHONE_DISPLAY_CALL_BUTTON_KEY, true)){
					_callButton.setVisibility(View.VISIBLE);
		    		_callButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View v){
						    	if (_debug) Log.v(_context, "Call Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	callMissedCall();
						    }
						}
    				);
		    	}else{
					_callButton.setVisibility(View.GONE);
		    	}
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewPhoneButtons() ERROR: " + ex.toString());
		}
	}

	/**
	 * Setup the view buttons for SMS & MMS notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 * @param notificationType - The notification type.
	 */
	private void setupViewSMSButtons(boolean usingImageButtons, int notificationType){
		if (_debug) Log.v(_context, "NotificationView.setupViewSMSButtons()");
		try{
			//Setup SMS/MMS Link
			if(notificationType == Constants.NOTIFICATION_TYPE_SMS){
				if(_preferences.getBoolean(Constants.SMS_MESSAGE_PRIVACY_ENABLED_KEY, false)){
					_privacyLinkTextView.setText(R.string.click_to_view_message);
					_privacyLinkTextView.setOnClickListener(new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification SMS Link Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	SMSCommon.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getThreadID(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
					    }
					});
				}
			}else if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
				if(_preferences.getBoolean(Constants.DISPLAY_NOTIFICATION_BODY_KEY, true)){
					_privacyLinkTextView.setText(R.string.mms_click_here_to_view);
					_privacyLinkTextView.setOnClickListener(new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification MMS Link Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	SMSCommon.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getThreadID(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
					    }
					});
				}
			}
			// Notification Count Text Button
			int notificationCountAction = -1;
			notificationCountAction = Integer.parseInt(_preferences.getString(Constants.SMS_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
			if(notificationCountAction == 0){
				//Do Nothing.
			}else if(notificationCountAction == 1){
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	SMSCommon.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getThreadID(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
					    }
					}
				);	
			}else if(notificationCountAction == 2){
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	SMSCommon.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getThreadID(), Constants.VIEW_SMS_THREAD_ACTIVITY);
					    }
					}
				);	
			}else if(notificationCountAction == 3){
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	SMSCommon.startMessagingAppViewInboxActivity(_context, _notificationActivity, Constants.MESSAGING_ACTIVITY);
					    }
					}
				);		
			}			
			boolean displayReplyButton = true;
			if(_preferences.getBoolean(Constants.SMS_DISPLAY_REPLY_BUTTON_KEY, true)){		
				boolean quickReplyEnabled = _preferences.getBoolean(Constants.QUICK_REPLY_ENABLED_KEY, false);
				boolean smsQuickReplyEnabled = _preferences.getBoolean(Constants.SMS_QUICK_REPLY_ENABLED_KEY, false);
				displayReplyButton = !quickReplyEnabled && !smsQuickReplyEnabled;
			}else{
				displayReplyButton = false;
			}
			if(usingImageButtons){
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){		
		    		_dismissImageButton.setVisibility(View.VISIBLE);
		    		_dismissImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
    				);
		    	}else{		
		    		_dismissImageButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
		    		_deleteImageButton.setVisibility(View.VISIBLE);
		    		_deleteImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Delete Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	showDeleteDialog();
						    }
						}
    				);
		    	}else{
		    		_deleteImageButton.setVisibility(View.GONE);
		    	}
				// Reply Button;
				if(displayReplyButton){
		    		_replyImageButton.setVisibility(View.VISIBLE);
		    		_replyImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Reply Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	replyToMessage(_notificationType);
						    }
						}
    				);
		    	}else{
		    		_replyImageButton.setVisibility(View.GONE);
		    	}
			}else{
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){		
		    		_dismissButton.setVisibility(View.VISIBLE);
		    		_dismissButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
    				);
		    	}else{		
		    		_dismissButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
		    		_deleteButton.setVisibility(View.VISIBLE);
		    		_deleteButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Delete Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	showDeleteDialog();
						    }
						}
    				);
		    	}else{
		    		_deleteButton.setVisibility(View.GONE);
		    	}
				// Reply Button
				if(displayReplyButton){
		    		_replyButton.setVisibility(View.VISIBLE);
		    		_replyButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "SMS Reply Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	replyToMessage(_notificationType);
						    }
						}
    				);
		    	}else{
		    		_replyButton.setVisibility(View.GONE);
		    	}
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewSMSButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the view buttons for Calendar notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 */
	private void setupViewCalendarButtons(boolean usingImageButtons){
		if (_debug) Log.v(_context, "NotificationView.setupViewCalendarButtons()");
		try{
			// Notification Count Text Button
			int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.CALENDAR_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
			if(notificationCountAction == 0){
				//Do Nothing.
			}else if(notificationCountAction == 1){
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	CalendarCommon.startViewCalendarActivity(_context, _notificationActivity, Constants.CALENDAR_ACTIVITY);
					    }
					}
				);			
			}
			// Snooze Button
	    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_SNOOZE_BUTTON_KEY, true)){
				_calendarSnoozeTextView.setVisibility(View.VISIBLE);
				_calendarSnoozeSpinner.setVisibility(View.VISIBLE);
				//Set calendar snooze default value.
				setCalendarSnoozeSpinner();
	    		_snoozeImageButton.setVisibility(View.VISIBLE);
	    		_snoozeImageButton.setOnClickListener(
    				new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Calendar Snooze Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	snoozeCalendarEvent();
					    }
					}
				);
	    	}else{
				_calendarSnoozeTextView.setVisibility(View.GONE);
				_calendarSnoozeSpinner.setVisibility(View.GONE);
	    		_snoozeImageButton.setVisibility(View.GONE);
	    	}
			if(usingImageButtons){
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_DISMISS_BUTTON_KEY, true)){	
		    		_dismissImageButton.setVisibility(View.VISIBLE);
		    		_dismissImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "Calendar Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
    				);
		    	}else{
		    		_dismissImageButton.setVisibility(View.GONE);
		    	}
				// View Button
		    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_VIEW_BUTTON_KEY, true)){
		    		_viewImageButton.setVisibility(View.VISIBLE);
		    		_viewImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "Calendar View Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	viewNotification(Constants.NOTIFICATION_TYPE_CALENDAR);
						    }
						}
    				);
		    	}else{
		    		_viewImageButton.setVisibility(View.GONE);
		    	}
			}else{
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_DISMISS_BUTTON_KEY, true)){
		    		_dismissButton.setVisibility(View.VISIBLE);
		    		_dismissButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "Calendar Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
		    		);
		    	}else{
		    		_dismissButton.setVisibility(View.GONE);
		    	}
				// View Button
		    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_VIEW_BUTTON_KEY, true)){
		    		_viewButton.setVisibility(View.VISIBLE);
		    		_viewButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "Calendar View Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	viewNotification(Constants.NOTIFICATION_TYPE_CALENDAR);
						    }
	    				}
		    		);
		    	}else{
		    		_viewButton.setVisibility(View.GONE);
		    	}
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewCalendarButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the view buttons for K9 notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 */
	private void setupViewK9Buttons(boolean usingImageButtons){
		if (_debug) Log.v(_context, "NotificationView.setupViewK9Buttons()");
		try{
			// Notification Count Text Button
			int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.K9_NOTIFICATION_COUNT_ACTION_KEY, Constants.K9_NOTIFICATION_COUNT_ACTION_K9_INBOX));
			if(notificationCountAction == 0){
				//Do Nothing.
			}else{
				_notificationCountTextView.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Notification Count Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	K9Common.startK9EmailAppViewInboxActivity(_context, _notificationActivity, _notificationSubType, Constants.K9_VIEW_INBOX_ACTIVITY);
					    }
					}
				);		
			}
			if(_preferences.getBoolean(Constants.EMAIL_MESSAGE_PRIVACY_ENABLED_KEY, false)){
				_privacyLinkTextView.setText(R.string.click_to_view_message);
				_privacyLinkTextView.setOnClickListener(new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v(_context, "Notification Email Link Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						//Cancel the reminder.
						_notification.cancelReminder();
				    	K9Common.startK9EmailAppViewInboxActivity(_context, _notificationActivity, _notificationSubType, Constants.K9_VIEW_EMAIL_ACTIVITY);
				    }
				});
			}
			if(usingImageButtons){
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.K9_DISPLAY_DISMISS_BUTTON_KEY, true)){
		    		_dismissImageButton.setVisibility(View.VISIBLE);
		    		_dismissImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
		    		);	
		    	}else{
		    		_dismissImageButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.K9_DISPLAY_DELETE_BUTTON_KEY, true)){
					_deleteImageButton.setVisibility(View.VISIBLE);
		    		_deleteImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Delete Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	showDeleteDialog();
						    }
						}
		    		);
		    	}else{
					_deleteImageButton.setVisibility(View.GONE);
		    	}
				// Reply Button
				if(_preferences.getBoolean(Constants.K9_DISPLAY_REPLY_BUTTON_KEY, true)){
					_replyImageButton.setVisibility(View.VISIBLE);
		    		_replyImageButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Reply Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	replyToMessage(Constants.NOTIFICATION_TYPE_K9);
						    }
						}
		    		);
		    	}else{
					_replyImageButton.setVisibility(View.GONE);
		    	}
			}else{
				// Dismiss Button
		    	if(_preferences.getBoolean(Constants.K9_DISPLAY_DISMISS_BUTTON_KEY, true)){
		    		_dismissButton.setVisibility(View.VISIBLE);
		    		_dismissButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Dismiss Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	dismissNotification(false);
						    }
						}
		    		);	
		    	}else{
		    		_dismissButton.setVisibility(View.GONE);
		    	}
				// Delete Button
				if(_preferences.getBoolean(Constants.K9_DISPLAY_DELETE_BUTTON_KEY, true)){
					_deleteButton.setVisibility(View.VISIBLE);
		    		_deleteButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Delete Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	showDeleteDialog();
						    }
						}
		    		);
		    	}else{
					_deleteButton.setVisibility(View.GONE);
		    	}
				// Reply Button
				if(_preferences.getBoolean(Constants.K9_DISPLAY_REPLY_BUTTON_KEY, true)){
					_replyButton.setVisibility(View.VISIBLE);
		    		_replyButton.setOnClickListener(
	    				new OnClickListener(){
						    public void onClick(View view){
						    	if (_debug) Log.v(_context, "K9 Reply Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								//Cancel the reminder.
								_notification.cancelReminder();
						    	replyToMessage(Constants.NOTIFICATION_TYPE_K9);
						    }
						}
		    		);
		    	}else{
					_replyButton.setVisibility(View.GONE);
		    	}
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewK9Buttons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the view buttons for K9 notifications.
	 * 
	 * @param usingImageButtons - True if the user wants to use buttons with icons only.
	 */
	private void setupViewGenericButtons(){
		if (_debug) Log.v(_context, "NotificationView.setupViewGenericButtons()");
		try{
			// Dismiss Button
			final PendingIntent dismissPendingIntent = _notification.getDismissPendingIntent();
    		_dismissButton.setVisibility(View.VISIBLE);
    		_dismissButton.setOnClickListener(
				new OnClickListener(){
				    public void onClick(View view){
				    	if (_debug) Log.v(_context, "Generic Dismiss Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						//Cancel the reminder.
						_notification.cancelReminder();
				    	dismissNotification(false);
				    	if(dismissPendingIntent != null){
					    	try{
					    		dismissPendingIntent.send();
					    	}catch(CanceledException ex){
					    		
					    	}
				    	}
				    }
				}
    		);
			// Delete Button
			final PendingIntent deletePendingIntent = _notification.getDeletePendingIntent();
			if(deletePendingIntent != null){
				_deleteButton.setVisibility(View.VISIBLE);
	    		_deleteButton.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Generic Delete Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	dismissNotification(false);
					    	try{
					    		deletePendingIntent.send();
					    	}catch(CanceledException ex){
					    		
					    	}
					    }
					}
	    		);
			}else{
				_deleteButton.setVisibility(View.GONE);
			}
			//View Button
			final PendingIntent viewPendingIntent = _notification.getViewPendingIntent();
			if(viewPendingIntent != null){
				_viewButton.setVisibility(View.VISIBLE);
	    		_viewButton.setOnClickListener(
					new OnClickListener(){
					    public void onClick(View view){
					    	if (_debug) Log.v(_context, "Generic View Button Clicked()");
					    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							//Cancel the reminder.
							_notification.cancelReminder();
					    	dismissNotification(false);
					    	try{
					    		viewPendingIntent.send();
					    	}catch(CanceledException ex){
					    		
					    	}
					    }
					}
	    		);
			}else{
				_viewButton.setVisibility(View.GONE);
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupViewGenericButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup and load the notification specific button icons.
	 */
	private void setupButtonIcons(boolean usingImageButtons, int notificationType, String buttonDisplayStyle){
		if (_debug) Log.v(_context, "NotificationView.setupButtonIcons()");
		try{
			if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_TEXT_ONLY)){
				return;
			}
			if(notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
				return;
			}
			Drawable dismissButtonIcon = null;
			Drawable deleteButtonIcon = null;
			Drawable callButtonIcon = null;
			Drawable replySMSButtonIcon = null;
			Drawable replyEmailButtonIcon = null;
			Drawable viewCalendarButtonIcon = null;
			Drawable snoozeCalendarButtonIcon = null;
			//Load the theme specific icons.
			if(_themePackageName.equals(Constants.NOTIFY_DEFAULT_THEME) || _themePackageName.equals(Constants.PHONE_DEFAULT_THEME)){
				dismissButtonIcon = _resources.getDrawable(R.drawable.ic_dismiss);
				deleteButtonIcon = _resources.getDrawable(R.drawable.ic_delete);
				callButtonIcon = _resources.getDrawable(R.drawable.ic_call);
				replySMSButtonIcon = _resources.getDrawable(R.drawable.ic_conversation);
				replyEmailButtonIcon = _resources.getDrawable(R.drawable.ic_envelope);
				viewCalendarButtonIcon = _resources.getDrawable(R.drawable.ic_calendar);
				snoozeCalendarButtonIcon = _resources.getDrawable(R.drawable.ic_calendar_snooze);
			}else{
				dismissButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_dismiss", null, null));
				deleteButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_delete", null, null));
				callButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_call", null, null));
				replySMSButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_conversation", null, null));
				replyEmailButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_envelope", null, null));
				viewCalendarButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_calendar", null, null));
				snoozeCalendarButtonIcon = _resources.getDrawable(_resources.getIdentifier(_themePackageName + ":drawable/ic_calendar_snooze", null, null));
			}
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if(usingImageButtons){
						_dismissImageButton.setImageDrawable(dismissButtonIcon);
						_callImageButton.setImageDrawable(callButtonIcon);
					}else{
						_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
						_callButton.setCompoundDrawablesWithIntrinsicBounds(callButtonIcon, null, null, null);
					}		
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					if(usingImageButtons){
						_dismissImageButton.setImageDrawable(dismissButtonIcon);
						_deleteImageButton.setImageDrawable(deleteButtonIcon);
						_replyImageButton.setImageDrawable(replySMSButtonIcon);
					}else{
						_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
						_deleteButton.setCompoundDrawablesWithIntrinsicBounds(deleteButtonIcon, null, null, null);
						_replyButton.setCompoundDrawablesWithIntrinsicBounds(replySMSButtonIcon, null, null, null);
					}		
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if(usingImageButtons){
						_dismissImageButton.setImageDrawable(dismissButtonIcon);
						_deleteImageButton.setImageDrawable(deleteButtonIcon);
						_replyImageButton.setImageDrawable(replySMSButtonIcon);
					}else{
						_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
						_deleteButton.setCompoundDrawablesWithIntrinsicBounds(deleteButtonIcon, null, null, null);
						_replyButton.setCompoundDrawablesWithIntrinsicBounds(replySMSButtonIcon, null, null, null);
					}		
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if(usingImageButtons){
						_dismissImageButton.setImageDrawable(dismissButtonIcon);
						_viewImageButton.setImageDrawable(viewCalendarButtonIcon);
					}else{
						_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
						_viewButton.setCompoundDrawablesWithIntrinsicBounds(viewCalendarButtonIcon, null, null, null);
					}
					_snoozeImageButton.setImageDrawable(snoozeCalendarButtonIcon);
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if(usingImageButtons){
						_dismissImageButton.setImageDrawable(dismissButtonIcon);
						_deleteImageButton.setImageDrawable(deleteButtonIcon);
						_replyImageButton.setImageDrawable(replyEmailButtonIcon);
					}else{
						_dismissButton.setCompoundDrawablesWithIntrinsicBounds(dismissButtonIcon, null, null, null);
						_deleteButton.setCompoundDrawablesWithIntrinsicBounds(deleteButtonIcon, null, null, null);
						_replyButton.setCompoundDrawablesWithIntrinsicBounds(replyEmailButtonIcon, null, null, null);
					}	
					break;
				}
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setupButtonIcons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Setup the long click event action for the dismiss button.
	 * 
	 * @param usingImageButtons - Boolean value indicating if the image buttons are being used.
	 */
	private void setupDismissButtonLongClickAction(boolean usingImageButtons){
		if (_debug) Log.v(_context, "NotificationView.setupDismissButtonLongClickAction()");
		if(usingImageButtons){
			_dismissImageButton.setOnLongClickListener(new OnLongClickListener(){ 
		        public boolean onLongClick(View view){
		        	if(_preferences.getString(Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_KEY, Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_DISMISS_ALL).equals(Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_DISMISS_ALL)){
			        	AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				        try{
				        	builder.setIcon(android.R.drawable.ic_dialog_alert);
				        }catch(Exception ex){
				        	//Don't set the icon if this fails.
				        }
						builder.setTitle(_context.getString(R.string.dismiss_all));
						builder.setMessage(_context.getString(R.string.dismiss_all_dialog_text));	
						builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
									_notificationActivity.dismissAllNotifications();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					            	dialog.cancel();
								}
							});
						builder.create().show();
		        	}else{
		        		_notificationActivity.openOptionsMenu();
		        	}
		            return true;
		        }
		    });
		}else{
			_dismissButton.setOnLongClickListener(new OnLongClickListener(){ 
		        public boolean onLongClick(View view){
		        	if(_preferences.getString(Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_KEY, Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_DISMISS_ALL).equals(Constants.DISMISS_BUTTON_LONG_PRESS_ACTION_DISMISS_ALL)){
			        	AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				        try{
				        	builder.setIcon(android.R.drawable.ic_dialog_alert);
				        }catch(Exception ex){
				        	//Don't set the icon if this fails.
				        }
						builder.setTitle(_context.getString(R.string.dismiss_all));
						builder.setMessage(_context.getString(R.string.dismiss_all_dialog_text));	
						builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
									_notificationActivity.dismissAllNotifications();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					            	dialog.cancel();
								}
							});
						builder.create().show();
		        	}else{
		        		_notificationActivity.openOptionsMenu();
		        	}
		            return true;
		        }
		    });
		}		
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void populateViewInfo(){
		if (_debug) Log.v(_context, "NotificationView.populateViewInfo()");
		boolean loadContactPhoto = true;
		String notificationTitle = _notification.getTitle();
		String contactName = _notification.getContactName();
		long contactID = _notification.getContactID();
		String sentFromAddress = _notification.getSentFromAddress();
    	if(notificationTitle == null || notificationTitle.equals("")){
    		notificationTitle = "No Title";
    	}
    	//Show/Hide the notification body.
		if(_preferences.getBoolean(Constants.DISPLAY_NOTIFICATION_BODY_KEY, true)){
			_notificationDetailsTextView.setVisibility(View.VISIBLE);
		    //Load the notification message.
		    setNotificationMessage();
			//Set the max lines property of the notification body.
			setNotificationBodyMaxLines(Integer.parseInt(_preferences.getString(Constants.NOTIFICATION_BODY_MAX_LINES_KEY, Constants.NOTIFICATION_BODY_MAX_LINES_DEFAULT)));
			//Set the font size property of the notification body.
			_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
		}else{
			_notificationDetailsTextView.setVisibility(View.GONE);
		}
	    // Set from, number, message etc. views.
		if(_notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
			_contactNameTextView.setText(notificationTitle);
			_contactNumberTextView.setVisibility(View.GONE);
			_photoImageView.setVisibility(View.GONE);
			_photoProgressBar.setVisibility(View.GONE);
			loadContactPhoto = false;			
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_GENERIC 
					&& contactID == -1 
					&& (contactName == null || contactName.equals("") || contactName.equals(_context.getString(android.R.string.unknownName)))
					&& (sentFromAddress == null || sentFromAddress.equals("") || sentFromAddress.equals(_context.getString(android.R.string.unknownName))) ){
				_contactNameTextView.setVisibility(View.GONE);
				_contactNumberTextView.setVisibility(View.GONE);
				_photoImageView.setVisibility(View.GONE);
				_photoProgressBar.setVisibility(View.GONE);
				_calendarSnoozeTextView.setVisibility(View.GONE);
				_calendarSnoozeSpinner.setVisibility(View.GONE);
				_snoozeImageButton.setVisibility(View.GONE);
				loadContactPhoto = false;
		}else{
			//Hide Calendar Specific Items
			_calendarSnoozeTextView.setVisibility(View.GONE);
			_calendarSnoozeSpinner.setVisibility(View.GONE);
			_snoozeImageButton.setVisibility(View.GONE);
			//Show/Hide Contact Name
			boolean displayContactNameText = true;
			if(_preferences.getBoolean(Constants.CONTACT_NAME_DISPLAY_KEY, true)){
				if(_preferences.getBoolean(Constants.CONTACT_NAME_HIDE_UNKNOWN_KEY, false)){
					if(contactName.equals(_context.getString(android.R.string.unknownName))){
						displayContactNameText = false;
					}else{
						displayContactNameText = true;
					}
				}else{
					displayContactNameText = true;
				}
			}else{
				displayContactNameText = false;
			}
			if(displayContactNameText){
				_contactNameTextView.setText(contactName);
				_contactNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NAME_SIZE_KEY, Constants.CONTACT_NAME_SIZE_DEFAULT)));
				_contactNameTextView.setVisibility(View.VISIBLE);
				if(_preferences.getBoolean(Constants.CONTACT_NAME_BOLD_KEY, false)){
					_contactNameTextView.setTypeface(null, Typeface.BOLD);
			    }else{
			    	_contactNameTextView.setTypeface(null, Typeface.NORMAL);
			    }
			}else{
				_contactNameTextView.setVisibility(View.GONE);
			}
			//Show/Hide Contact Number
			boolean displayContactNumber = true;
			if(sentFromAddress == null || sentFromAddress.equals("") || sentFromAddress.equals(_context.getString(android.R.string.unknownName))){
				displayContactNumber = false;
			}else if(sentFromAddress.contains("@")){
		    	_contactNumberTextView.setText(sentFromAddress);
		    	displayContactNumber = true;
		    }else{
	    		_contactNumberTextView.setText(PhoneCommon.formatPhoneNumber(_context, sentFromAddress));
	    		displayContactNumber = true;	    	
		    }
			if(!_preferences.getBoolean(Constants.CONTACT_NUMBER_DISPLAY_KEY, true)){
				if(_preferences.getBoolean(Constants.CONTACT_NUMBER_DISPLAY_UNKNOWN_KEY, true)){
					if((contactName == null || contactName.equals("") || contactName.equals(_context.getString(android.R.string.unknownName)))){
			    		displayContactNumber = true;
					}else{
			    		displayContactNumber = false;
					}
				}else{
		    		displayContactNumber = false;
				}
			}
			if(displayContactNumber){
			    _contactNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NUMBER_SIZE_KEY, Constants.CONTACT_NUMBER_SIZE_DEFAULT)));
				_contactNumberTextView.setVisibility(View.VISIBLE);	
				if(_preferences.getBoolean(Constants.CONTACT_NUMBER_BOLD_KEY, false)){
					_contactNumberTextView.setTypeface(null, Typeface.BOLD);
			    }else{
			    	_contactNumberTextView.setTypeface(null, Typeface.NORMAL);
			    }
			}else{
				_contactNumberTextView.setVisibility(View.GONE);
			}
			//Show/Hide Contact Photo
			if(loadContactPhoto && _preferences.getBoolean(Constants.CONTACT_PHOTO_DISPLAY_KEY, true)){
				//Set Contact Photo Background
				int contactPhotoBackground = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_BACKGKROUND_KEY, "0"));
				if(contactPhotoBackground == 1){
					_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_froyo);
				}else if(contactPhotoBackground == 2){
					_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_gingerbread);
				}else if(contactPhotoBackground == 3){
					_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_blue_steel);
				}else if(contactPhotoBackground == 4){
					_photoImageView.setBackgroundResource(R.drawable.contact_photo_background_ics_blue);
					_photoImageView.setPadding(2, 2, 2, 2);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(5, 5, 5, 5);
					_photoImageView.setLayoutParams(layoutParams);
				}else if(contactPhotoBackground == 5){
					_photoImageView.setBackgroundResource(R.drawable.contact_photo_background_htc_green);
					_photoImageView.setPadding(2, 2, 2, 2);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(5, 5, 5, 5);
					_photoImageView.setLayoutParams(layoutParams);
				}else{
					_photoImageView.setBackgroundResource(R.drawable.quickcontact_badge_white);
				}
			}else{
				_photoImageView.setVisibility(View.GONE);
				_photoProgressBar.setVisibility(View.GONE);
				loadContactPhoto = false;
			}
		    //Add the Quick Contact Android Widget to the Contact Photo.
		    if(loadContactPhoto){
		    	setupQuickContact();
		    }
		}
		//Contact Name & Number Alignment
		//Contact Name Alignment
	    int contactNameAlignment = Gravity.LEFT;
	    if(_preferences.getBoolean(Constants.CONTACT_NAME_CENTER_ALIGN_KEY, false)){
	    	contactNameAlignment = Gravity.CENTER_HORIZONTAL;
		}else{
			contactNameAlignment = Gravity.LEFT;
		}
	    _contactNameTextView.setGravity(contactNameAlignment);
	    //Contact Number Alignment
	    int contactNumberAlignment = Gravity.LEFT;
	    if(_preferences.getBoolean(Constants.CONTACT_NUMBER_CENTER_ALIGN_KEY, false)){
	    	contactNumberAlignment = Gravity.CENTER_HORIZONTAL;
		}else{
			contactNumberAlignment = Gravity.LEFT;
		}
	    _contactNumberTextView.setGravity(contactNumberAlignment);
		//Display SMS/MMS Link
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS){
			if(_preferences.getBoolean(Constants.SMS_MESSAGE_PRIVACY_ENABLED_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
				_privacyLinkTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_privacyLinkTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}else{
				_privacyLinkTextView.setVisibility(View.GONE);
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_MMS){
			_notificationDetailsTextView.setVisibility(View.GONE);
			if(_preferences.getBoolean(Constants.DISPLAY_NOTIFICATION_BODY_KEY, true)){
				_privacyLinkTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_privacyLinkTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}else{
				_privacyLinkTextView.setVisibility(View.GONE);
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_PHONE){
			if(_preferences.getBoolean(Constants.MISSED_CALL_PRIVACY_ENABLED_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
				_privacyLinkTextView.setVisibility(View.VISIBLE);
				_contactNumberTextView.setVisibility(View.GONE);
			}else{
				_notificationDetailsTextView.setVisibility(View.GONE);
				_privacyLinkTextView.setVisibility(View.GONE);
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_K9){
			if(_preferences.getBoolean(Constants.EMAIL_MESSAGE_PRIVACY_ENABLED_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
				_privacyLinkTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_privacyLinkTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}else{
				_privacyLinkTextView.setVisibility(View.GONE);
			}
		}
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo();
	    //Add context menu items.
	    setupContextMenus();
	    //Load the image from the users contacts.
    	if(loadContactPhoto){
    		new setNotificationContactImageAsyncTask().execute(_notification.getContactID());
    	}
	}
	
	/**
	 * Add the QuickContact widget to the Contact Photo. This is added to the OnClick event of the photo.
	 */
	private void setupQuickContact(){
		if (_debug) Log.v(_context, "NotificationView.setupQuickContact()");
		if(_preferences.getBoolean(Constants.QUICK_CONTACT_DISABLED_KEY, false)){
			return;
		}
		final String lookupKey = _notification.getLookupKey();
		if(lookupKey != null && !lookupKey.equals("")){
			_photoImageView.setOnClickListener(new OnClickListener(){
			    public void onClick(View view){
			    	if (_debug) Log.v(_context, "Contact Photo Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					//Cancel the reminder.
					_notification.cancelReminder();
			    	try{
			    		ContactsContract.QuickContact.showQuickContact(_context, _photoImageView, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey), ContactsContract.QuickContact.MODE_MEDIUM, null);
			    	}catch(Exception ex){
			    		Log.e(_context, "Contact Photo Clicked ContactsContract.QuickContact.showQuickContact() Error: " + ex.toString());
			    	}
			    }
			});
		}
	}
	
	/**
	 * Set the notification message. 
	 * This is specific to the type of notification that was received.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationMessage(){
		if (_debug) Log.v(_context, "NotificationView.setNotificationMessage()");
		String notificationText = "";
		int notificationAlignment = Gravity.LEFT;
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				return;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				notificationText = _notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				notificationText = _notification.getMessageBody();	
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	notificationText = _notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				notificationText = _notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				notificationText = _notification.getMessageBody();
				break;
			}
		}
		try{
			if(_preferences.getBoolean(Constants.EMOTICONS_ENABLED, true)){
				_notificationDetailsTextView.setText(Html.fromHtml(EmojiCommon.convertTextToEmoji(_context, notificationText), EmojiCommon.emojiGetter, null));
			}else{
				_notificationDetailsTextView.setText(Html.fromHtml(notificationText));
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.setNotificationMessage() EMOJI LOADING ERROR: " + ex.toString());
			_notificationDetailsTextView.setText(Html.fromHtml(notificationText));
		}
		if(_preferences.getBoolean(Constants.NOTIFICATION_BODY_CENTER_ALIGN_TEXT_KEY, false)){
			notificationAlignment = Gravity.CENTER_HORIZONTAL;
		}else{
			notificationAlignment = Gravity.LEFT;
		}
	    _notificationDetailsTextView.setGravity(notificationAlignment);
	}
	
	/**
	 * Set notification specific details into the header of the Notification.
	 * This is specific to the type of notification that was received.
	 * Details include:
	 * 		Icon,
	 * 		Icon Text,
	 * 		Date & Time,
	 * 		Etc...
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationTypeInfo(){
		if (_debug) Log.v(_context, "NotificationView.setNotificationTypeInfo()");
		Bitmap iconBitmap = null;
		// Update TextView that contains the image, contact info/calendar info, and timestamp for the Notification.
	    String receivedAtText = "";
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				String formattedTimestamp = Common.formatTimestamp(_context, _notification.getTimeStamp());
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_missed_call);
		    	receivedAtText = _context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _notification.getTimeStamp());
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_sms);
		    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				String formattedTimestamp = Common.formatTimestamp(_context, _notification.getTimeStamp());
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_sms);
		    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_calendar);
		    	receivedAtText = _context.getString(R.string.calendar_event_text);
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				String formattedTimestamp = Common.formatTimestamp(_context, _notification.getTimeStamp());
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.notification_type_email);
		    	receivedAtText = _context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				String formattedTimestamp = Common.formatTimestamp(_context, _notification.getTimeStamp());
				iconBitmap = Common.getPackageIcon(_context, _notification.getPackageName());
		    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
				break;
			}
		}
		if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_ICON_DISPLAY_KEY, true)){
		    if(iconBitmap != null){
		    	_notificationIconImageView.setImageBitmap(iconBitmap);
		    	_notificationIconImageView.setVisibility(View.VISIBLE);
		    }
		}else{
			_notificationIconImageView.setVisibility(View.GONE);
		}
		_notificationInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_KEY, Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_DEFAULT)));
	    _notificationInfoTextView.setText(receivedAtText);
	    if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_DISPLAY_KEY, true)){
	    	_notificationInfoLinearLayout.setVisibility(View.VISIBLE);
		    //Set the notification info alignment. This includes the image and text.
		    int notificationInfoAlignment = Gravity.LEFT;
		    if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_CENTER_ALIGN_KEY, false)){
		    	notificationInfoAlignment = Gravity.CENTER_HORIZONTAL;
			}else{
				notificationInfoAlignment = Gravity.LEFT;
			}
		    _notificationInfoLinearLayout.setGravity(notificationInfoAlignment);
		    if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_BOLD_KEY, false)){
		    	_notificationInfoTextView.setTypeface(null, Typeface.BOLD);
		    }else{
		    	_notificationInfoTextView.setTypeface(null, Typeface.NORMAL);
		    }
	    }else{
	    	_notificationInfoLinearLayout.setVisibility(View.GONE);
	    }
	}
	
	/**
	 * Setup the context menus for the various items on the notification window.
	 */
	private void setupContextMenus(){
		if (_debug) Log.v(_context, "NotificationView.setupContextMenus()"); 
		if(_preferences.getBoolean(Constants.CONTEXT_MENU_DISABLED_KEY, false)){
			return;
		}
		_notificationActivity.registerForContextMenu(_contactLinearLayout);
	}

	/**
	 * Set the notification contact's image.
	 * 
	 * @author Camille Sévigny
	 */
	private class setNotificationContactImageAsyncTask extends AsyncTask<Long, Void, Bitmap> {
		
		/**
		 * Set up the contact image loading view.
		 */
	    protected void onPreExecute(){
			if (_debug) Log.v(_context, "NotificationView.setNotificationContactImageAsyncTask.onPreExecute()");
	    	_photoImageView.setVisibility(View.GONE);
	    	_photoProgressBar.setVisibility(View.VISIBLE);
	    }
	    
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected Bitmap doInBackground(Long... params){
			if (_debug) Log.v(_context, "NotificationView.setNotificationContactImageAsyncTask.doInBackground()");
	    	return getNotificationContactImage(params[0]);
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(Bitmap result){
			if (_debug) Log.v(_context, "NotificationView.setNotificationContactImageAsyncTask.onPostExecute()");
	    	_photoImageView.setImageBitmap(result);
	    	_photoProgressBar.setVisibility(View.GONE);
	    	_photoImageView.setVisibility(View.VISIBLE);
	    }
	}

	/**
	 * Get the image from the users contacts.
	 * 
	 * @param contactID - This contact's id.
	 */
	private Bitmap getNotificationContactImage(long contactID){
		if (_debug) Log.v(_context, "NotificationView.getNotificationContactImage()");
	    //Load contact photo if it exists.
		try{
		    Bitmap bitmap = getContactImage(contactID);
	    	int contactPhotoSize = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_SIZE_KEY, Constants.CONTACT_PHOTO_SIZE_DEFAULT));
		    if(bitmap!=null){
		    	return Common.getRoundedCornerBitmap(bitmap, 5, true, contactPhotoSize, contactPhotoSize);
		    }else{
		    	String contactPlaceholderImageIndex = _preferences.getString(Constants.CONTACT_PLACEHOLDER_KEY, Constants.CONTACT_PLACEHOLDER_DEFAULT);
		    	return Common.getRoundedCornerBitmap(BitmapFactory.decodeResource(_context.getResources(), getContactPhotoPlaceholderResourceID(Integer.parseInt(contactPlaceholderImageIndex))), 5, true, contactPhotoSize, contactPhotoSize);
		    }
		}catch(Exception ex){
			Log.e(_context, "NotificationView.getNotificationContactImage() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get the contact image for the corresponding contact id.
	 * 
	 * @param contactID - The contact id of the contact image we want to retrieve.
	 * 
	 * @return Bitmap - The bitmap of the contact image or null if there is none.
	 */
	private Bitmap getContactImage(long contactID){
		if (_debug) Log.v(_context, "NotificationView.getContactImage()");
		try{
			if(contactID < 0){
				if (_debug) Log.v(_context, "NotificationView.getContactImage() ContactID < 0. Exiting...");
				return null;
			}
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(_context.getContentResolver(), uri);
			Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
			if(contactPhotoBitmap!= null){
				return contactPhotoBitmap;
			}else{
				return null;
			}
		}catch(Exception ex){
			Log.e(_context, "NotificationView.getContactImage() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Get the contact photo placeholder image resource id.
	 * 
	 * @param index - The contact image index.
	 * 
	 * @return int - Returns the resource id of the image that corresponds to this index.
	 */
	private int getContactPhotoPlaceholderResourceID(int index){
		if (_debug) Log.v(_context, "NotificationView.getContactPhotoPlaceholderResourceID()");
		switch(index){
			case 1:{
				return R.drawable.ic_contact_picture_1;
			}
			case 2:{
				return R.drawable.ic_contact_picture_2;
			}
			case 3:{
				return R.drawable.ic_contact_picture_3;
			}
			case 4:{
				return R.drawable.ic_contact_picture_4;
			}
			case 5:{
				return R.drawable.ic_contact_picture_5;
			}
			case 6:{
				return R.drawable.ic_contact_picture_6;
			}
			case 7:{
				return R.drawable.ic_contact_picture_7;
			}
			case 8:{
				return R.drawable.ic_contact_picture_8;
			}
			case 9:{
				return R.drawable.ic_contact_picture_9;
			}
			case 10:{
				return R.drawable.ic_contact_picture_10;
			}
			case 11:{
				return R.drawable.ic_contact_picture_11;
			}
			case 12:{
				return R.drawable.ic_contact_picture_12;
			}
			default:{
				return R.drawable.ic_contact_picture_1;
			}
		}
	}	
	
	/**
	 * Remove the notification from the ViewFlipper.
	 */
	private void dismissNotification(boolean reschedule){
		if (_debug) Log.v(_context, "NotificationView.dismissNotification()");
		_notificationViewFlipper.removeActiveNotification(reschedule);
	}
	
	/**
	 * Launches a new Activity.
	 * Replies to the current message using the stock Android messaging app.
	 */
	private void replyToMessage(int notificationType){
		if (_debug) Log.v(_context, "NotificationView.replyToMessage()");
		//Setup Reply action.
		String sentFromAddress = _notification.getSentFromAddress();
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(sentFromAddress == null){
					Toast.makeText(_context, _context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
					return;
				}
				//Reply using any installed SMS messaging app.
				SMSCommon.startMessagingAppReplyActivity(_context, _notificationActivity, sentFromAddress, _notification.getMessageID(), _notification.getThreadID(), Constants.SEND_SMS_ACTIVITY);
				return;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(sentFromAddress == null){
					Toast.makeText(_context, _context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
					return;
				}
				//Reply using any installed SMS messaging app.
				SMSCommon.startMessagingAppReplyActivity(_context, _notificationActivity, sentFromAddress, _notification.getMessageID(), _notification.getThreadID(), Constants.SEND_SMS_ACTIVITY);
				return;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{			
				return;
			}
			case Constants.NOTIFICATION_TYPE_PHONE:{			
				return;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				//Reply using any installed K9 email app.
				K9Common.startK9MailAppReplyActivity(_context, _notificationActivity, _notification.getK9EmailUri(), _notificationSubType, Constants.K9_VIEW_EMAIL_ACTIVITY);
				return;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{			
				return;
			}
			default:{
				return;
			}
		}
	}
	
	/**
	 * Views a notification.
	 */
	private void viewNotification(int notificationType){
		if (_debug) Log.v(_context, "NotificationView.viewNotification()");
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	CalendarCommon.startViewCalendarEventActivity(_context, _notificationActivity, _notification.getCalendarEventID(), _notification.getCalendarEventStartTime(), _notification.getCalendarEventEndTime(), Constants.VIEW_CALENDAR_ACTIVITY);
				break;
			}
			case Constants.NOTIFICATION_TYPE_PHONE:{			
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{				
				break;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{			
				break;
			}
		}
	}
 
	/**
	 * Confirm the delete request of the current message.
	 */
	private void showDeleteDialog(){
		if (_debug) Log.v(_context, "NotificationView.showDeleteDialog()");
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper.
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.SMS_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
						builder.setTitle(_context.getString(R.string.delete));
						if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
							builder.setMessage(_context.getString(R.string.delete_message_dialog_text));
						}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
							builder.setMessage(_context.getString(R.string.delete_thread_dialog_text));
						}
						builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
									deleteMessage();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					            	dialog.cancel();
								}
							});
						builder.create().show();
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.SMS_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
						builder.setTitle(_context.getString(R.string.delete));
						if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
							builder.setMessage(_context.getString(R.string.delete_message_dialog_text));
						}else if(_preferences.getString(Constants.SMS_DELETE_KEY, "0").equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
							builder.setMessage(_context.getString(R.string.delete_thread_dialog_text));
						}
						builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
									deleteMessage();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					            	dialog.cancel();
								}
							});
						builder.create().show();
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				if(_preferences.getString(Constants.K9_DELETE_KEY, "0").equals(Constants.K9_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					deleteMessage();
				}else{
					if(_preferences.getBoolean(Constants.K9_CONFIRM_DELETION_KEY, true)){
						//Confirm deletion of the message.
						AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				        builder.setIcon(android.R.drawable.ic_dialog_alert);
						builder.setTitle(_context.getString(R.string.delete));
						builder.setMessage(_context.getString(R.string.delete_email_dialog_text));
						builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
									deleteMessage();
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id){
									//customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					            	dialog.cancel();
								}
							});
						builder.create().show();
					}else{
						//Remove the notification from the ViewFlipper.
						deleteMessage();
					}
				}
				break;
			}
			default:{
				break;
			}
		}
	}

	/**
	 * Delete the current Notification.
	 */
	private void deleteMessage(){
		if(_debug) Log.v(_context, "NotificationView.deleteMessage()");
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				String deleteButtonAction = _preferences.getString(Constants.SMS_DELETE_KEY, "0");
				if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}else if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					_notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}else if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					_notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					_notificationViewFlipper.removeNotificationsByThread(_notification.getThreadID());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				String deleteButtonAction = _preferences.getString(Constants.SMS_DELETE_KEY, "0");
				if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}else if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					_notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}else if(deleteButtonAction.equals(Constants.SMS_DELETE_ACTION_DELETE_THREAD)){
					//Delete the current message from the users phone.
					//The notification will remove ALL messages for this thread from the phone for us.
					_notification.deleteMessage();
					//Remove all Notifications with the thread ID.
					_notificationViewFlipper.removeNotificationsByThread(_notification.getThreadID());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				String deleteButtonAction = _preferences.getString(Constants.K9_DELETE_KEY, "0");
				if(deleteButtonAction.equals(Constants.K9_DELETE_ACTION_NOTHING)){
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}else if(deleteButtonAction.equals(Constants.K9_DELETE_ACTION_DELETE_MESSAGE)){
					//Delete the current message from the users phone.
					_notification.deleteMessage();
					//Remove the notification from the ViewFlipper
					dismissNotification(false);
				}
				break;
			}
			default:{
				break;
			}
		}
	}
	
	/**
	 * When the Missed Call Notification "Call" button is pressed, this determines what to do.
	 */
	private void callMissedCall(){
		if (_debug) Log.v(_context, "NotificationView.callMissedCall()");
		if(_preferences.getString(Constants.PHONE_CALL_KEY, Constants.PHONE_CALL_ACTION_CALL).equals(Constants.PHONE_CALL_ACTION_CALL)){
			PhoneCommon.makePhoneCall(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.CALL_ACTIVITY);
		}else if(_preferences.getString(Constants.PHONE_CALL_KEY, Constants.PHONE_CALL_ACTION_CALL).equals(Constants.PHONE_CALL_ACTION_CALL_LOG)){
			PhoneCommon.startCallLogViewActivity(_context, _notificationActivity, Constants.VIEW_CALL_LOG_ACTIVITY);
		}else{
			dismissNotification(false);
		}
	}
	
	/**
	 * Set the calendar snooze spinner default value.
	 */
	private void setCalendarSnoozeSpinner(){
		if (_debug) Log.v(_context, "NotificationView.setCalendarSnoozeSpinner()");
		int selectionValue = Integer.parseInt(_preferences.getString(Constants.CALENDAR_REMINDER_INTERVAL_KEY, Constants.CALENDAR_REMINDER_INTERVAL_DEFAULT));
		String selectionItem = _context.getString(R.string.s10_minutes_text);
		if(selectionValue == 1){
			selectionItem = _context.getString(R.string.s1_minute_text);
		}else if(selectionValue == 2){
			selectionItem = _context.getString(R.string.s2_minutes_text);
		}else if(selectionValue == 3){
			selectionItem = _context.getString(R.string.s3_minutes_text);
		}else if(selectionValue == 4){
			selectionItem = _context.getString(R.string.s4_minutes_text);
		}else if(selectionValue == 5){
			selectionItem = _context.getString(R.string.s5_minutes_text);
		}else if(selectionValue == 6){
			selectionItem = _context.getString(R.string.s6_minutes_text);
		}else if(selectionValue == 7){
			selectionItem = _context.getString(R.string.s7_minutes_text);
		}else if(selectionValue == 8){
			selectionItem = _context.getString(R.string.s8_minutes_text);
		}else if(selectionValue == 9){
			selectionItem = _context.getString(R.string.s9_minutes_text);
		}else if(selectionValue == 10){
			selectionItem = _context.getString(R.string.s10_minutes_text);
		}else if(selectionValue == 15){
			selectionItem = _context.getString(R.string.s15_minutes_text);
		}else if(selectionValue == 20){
			selectionItem = _context.getString(R.string.s20_minutes_text);
		}else if(selectionValue == 25){
			selectionItem = _context.getString(R.string.s25_minutes_text);
		}else if(selectionValue == 30){
			selectionItem = _context.getString(R.string.s30_minutes_text);		
		}else if(selectionValue == 35){
			selectionItem = _context.getString(R.string.s35_minutes_text);		
		}else if(selectionValue == 40){
			selectionItem = _context.getString(R.string.s40_minutes_text);
		}else if(selectionValue == 45){
			selectionItem = _context.getString(R.string.s45_minutes_text);		
		}else if(selectionValue == 50){
			selectionItem = _context.getString(R.string.s50_minutes_text);		
		}else if(selectionValue == 55){
			selectionItem = _context.getString(R.string.s55_minutes_text);		
		}else if(selectionValue == 60){
			selectionItem = _context.getString(R.string.s1_hour_text);		
		}else if(selectionValue == 120){
			selectionItem = _context.getString(R.string.s2_hours_text);	
		}else if(selectionValue == 180){
			selectionItem = _context.getString(R.string.s3_hours_text);		
		}else if(selectionValue == 240){
			selectionItem = _context.getString(R.string.s4_hours_text);		
		}else if(selectionValue == 300){
			selectionItem = _context.getString(R.string.s5_hours_text);		
		}else if(selectionValue == 360){
			selectionItem = _context.getString(R.string.s6_hours_text);		
		}else if(selectionValue == 420){
			selectionItem = _context.getString(R.string.s7_hours_text);		
		}else if(selectionValue == 480){
			selectionItem = _context.getString(R.string.s8_hours_text);		
		}else if(selectionValue == 540){
			selectionItem = _context.getString(R.string.s9_hours_text);		
		}else if(selectionValue == 600){
			selectionItem = _context.getString(R.string.s10_hours_text);		
		}else if(selectionValue == 660){
			selectionItem = _context.getString(R.string.s11_hours_text);		
		}else if(selectionValue == 720){
			selectionItem = _context.getString(R.string.s12_hours_text);		
		}else if(selectionValue == 1440){
			selectionItem = _context.getString(R.string.s24_hours_text);
		}else{
			selectionItem = _context.getString(R.string.s10_minutes_text);
		}
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> calendarSnoozeSpinnerArrayAdaptor = (ArrayAdapter<String>) _calendarSnoozeSpinner.getAdapter();
		int itemPosition = calendarSnoozeSpinnerArrayAdaptor.getPosition(selectionItem);
		_calendarSnoozeSpinner.setSelection(itemPosition);
	}
	
	/**
	 * Snooze a calendar event.
	 */
	private void snoozeCalendarEvent(){
		String snoozeText = _calendarSnoozeSpinner.getSelectedItem().toString();
		long snoozeTime = 10; //10 minute default time.
		if(snoozeText.equals(_context.getString(R.string.s1_minute_text))){
			snoozeTime = 1;
		}else if(snoozeText.equals(_context.getString(R.string.s2_minutes_text))){
			snoozeTime = 2;
		}else if(snoozeText.equals(_context.getString(R.string.s3_minutes_text))){
			snoozeTime = 3;
		}else if(snoozeText.equals(_context.getString(R.string.s4_minutes_text))){
			snoozeTime = 4;
		}else if(snoozeText.equals(_context.getString(R.string.s5_minutes_text))){
			snoozeTime = 5;
		}else if(snoozeText.equals(_context.getString(R.string.s6_minutes_text))){
			snoozeTime = 6;
		}else if(snoozeText.equals(_context.getString(R.string.s7_minutes_text))){
			snoozeTime = 7;
		}else if(snoozeText.equals(_context.getString(R.string.s8_minutes_text))){
			snoozeTime = 8;
		}else if(snoozeText.equals(_context.getString(R.string.s9_minutes_text))){
			snoozeTime = 9;
		}else if(snoozeText.equals(_context.getString(R.string.s10_minutes_text))){
			snoozeTime = 10;
		}else if(snoozeText.equals(_context.getString(R.string.s15_minutes_text))){
			snoozeTime = 15;
		}else if(snoozeText.equals(_context.getString(R.string.s30_minutes_text))){
			snoozeTime = 30;
		}else if(snoozeText.equals(_context.getString(R.string.s45_minutes_text))){
			snoozeTime = 45;
		}else if(snoozeText.equals(_context.getString(R.string.s1_hour_text))){
			snoozeTime = 60;
		}else if(snoozeText.equals(_context.getString(R.string.s2_hours_text))){
			snoozeTime = 120;
		}else if(snoozeText.equals(_context.getString(R.string.s3_hours_text))){
			snoozeTime = 180;
		}else if(snoozeText.equals(_context.getString(R.string.s4_hours_text))){
			snoozeTime = 240;
		}else if(snoozeText.equals(_context.getString(R.string.s5_hours_text))){
			snoozeTime = 300;
		}else if(snoozeText.equals(_context.getString(R.string.s6_hours_text))){
			snoozeTime = 360;
		}else if(snoozeText.equals(_context.getString(R.string.s7_hours_text))){
			snoozeTime = 420;
		}else if(snoozeText.equals(_context.getString(R.string.s8_hours_text))){
			snoozeTime = 480;
		}else if(snoozeText.equals(_context.getString(R.string.s9_hours_text))){
			snoozeTime = 540;
		}else if(snoozeText.equals(_context.getString(R.string.s10_hours_text))){
			snoozeTime = 600;
		}else if(snoozeText.equals(_context.getString(R.string.s11_hours_text))){
			snoozeTime = 660;
		}else if(snoozeText.equals(_context.getString(R.string.s12_hours_text))){
			snoozeTime = 720;
		}else if(snoozeText.equals(_context.getString(R.string.s24_hours_text))){
			snoozeTime = 1440;
		}else{
			snoozeTime = 10;
		}
		snoozeTime = snoozeTime * 60 * 1000;
		_notification.reschedule(System.currentTimeMillis() + snoozeTime, Constants.PENDING_INTENT_TYPE_SNOOZE);
    	dismissNotification(true);
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator)_notificationActivity.getSystemService(Context.VIBRATOR_SERVICE);
		if(_preferences.getBoolean(Constants.HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				vibrator.vibrate(50);
			}
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				vibrator.vibrate(100);
			}
		}
	}
	
	/**
	 * Send a quick reply message.
	 */
	private void sendQuickReply(){
    	String message = _messageEditText.getText().toString();
    	if(message.equals("")){
    		//No text was entered.
    	}else{		
    		//Cancel the reminder.
    		_notification.cancelReminder();
    		SMSCommon.sendSMSTask(_context, _notification.getSentFromAddress(), message, _notification.getMessageID(), _notification.getThreadID());
    		dismissNotification(false);
    		//Hide the soft keyboard.
    		hideSoftKeyboard();
    	}
	}
	
	/**
	 * Hide the soft keyboard.
	 */
	private void hideSoftKeyboard(){
		InputMethodManager inputMethodManager = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(_messageEditText.getWindowToken(), 0);
	}
	
	/**
	 * Sets a listener to global layout changes to determine if the soft keyboard has been shown or not.
	 */
	private void setKeyboardListener(){
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				return;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
				return;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				return;
			}
			case Constants.NOTIFICATION_TYPE_GENERIC:{
				return;
			}
			default:{
				return;
			}
		}
		boolean quickReplyEnabled = _preferences.getBoolean(Constants.QUICK_REPLY_ENABLED_KEY, false);
		boolean smsQuickReplyEnabled = _preferences.getBoolean(Constants.SMS_QUICK_REPLY_ENABLED_KEY, false);
		if(quickReplyEnabled || smsQuickReplyEnabled){
			final boolean hideHeaderPanel = _preferences.getBoolean(Constants.QUICK_REPLY_HIDE_HEADER_PANEL_KEY, false);
			final boolean hideContactPanel = _preferences.getBoolean(Constants.QUICK_REPLY_HIDE_CONTACT_PANEL_KEY, false);
			final boolean hideButtonPanel = _preferences.getBoolean(Constants.QUICK_REPLY_HIDE_BUTTON_PANEL_KEY, false);
			if(!hideHeaderPanel && !hideContactPanel && !hideButtonPanel){
				return;
			}
			_notificationWindowLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener( new OnGlobalLayoutListener(){
			    public void onGlobalLayout(){
		        	int phoneOrientation = getResources().getConfiguration().orientation;
		        	int maxLines = 5;
			    	Rect viewableRectangle = new Rect();
			    	_notificationWindowLinearLayout.getWindowVisibleDisplayFrame(viewableRectangle);
			    	int viewableRectangleHeight = viewableRectangle.bottom - viewableRectangle.top;
			    	int notificationHeight = _notificationWindowLinearLayout.getRootView().getHeight();			        	
		        	//if (_debug) Log.v(_context, "viewableRectangle.height(): " + viewableRectangleHeight);
		        	//if (_debug) Log.v(_context, "_notificationOriginalHeight: " + notificationHeight);
			        if (notificationHeight - viewableRectangleHeight > 200){
			        	if(!_softKeyboardOnScreen){
			        		_softKeyboardOnScreen = true;			        	
				        	//if (_debug) Log.v(_context, "NotificationView - KEYBOARD IS ON SCREEN!");
				        	maxLines = 5;
				        	if(phoneOrientation != Configuration.ORIENTATION_LANDSCAPE){
				        		if(hideHeaderPanel) _headerRelativeLayout.setVisibility(View.GONE);
				        		if(hideContactPanel) _contactLinearLayout.setVisibility(View.GONE);
					        	if(hideButtonPanel){
									_buttonLinearLayout.setVisibility(View.GONE);
								    _imageButtonLinearLayout.setVisibility(View.GONE);
					        	}
				        	}
				        	setNotificationBodyMaxLines(maxLines);
			        	}
			        }else{
			        	if(_softKeyboardOnScreen){
			        		_softKeyboardOnScreen = false;
				        	//if (_debug) Log.v(_context, "NotificationView - KEYBOARD IS NOT ON SCREEN!");			        	
				        	maxLines = Integer.parseInt(_preferences.getString(Constants.NOTIFICATION_BODY_MAX_LINES_KEY, Constants.NOTIFICATION_BODY_MAX_LINES_DEFAULT));
				    	    if(phoneOrientation == Configuration.ORIENTATION_LANDSCAPE){
				    	    	if(maxLines > 2) maxLines = maxLines / 2;
				            }else{
				        		if(hideHeaderPanel) _headerRelativeLayout.setVisibility(View.VISIBLE);
				            	if(hideContactPanel) _contactLinearLayout.setVisibility(View.VISIBLE);
					        	if(hideButtonPanel){
						    		String buttonDisplayStyle = _preferences.getString(Constants.BUTTON_DISPLAY_STYLE_KEY, Constants.BUTTON_DISPLAY_STYLE_DEFAULT);
						    		if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_ICON_ONLY)){
										_buttonLinearLayout.setVisibility(View.GONE);
										_imageButtonLinearLayout.setVisibility(View.VISIBLE);
									}else{
										_buttonLinearLayout.setVisibility(View.VISIBLE);
										_imageButtonLinearLayout.setVisibility(View.GONE);
									}
					        	}
				            }
				        	setNotificationBodyMaxLines(maxLines);
			        	}
			        }
			     }
			});
		}
	}
	
}