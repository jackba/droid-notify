package apps.droidnotify;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterCommon;

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
	private NotificationViewFlipper _notificationViewFlipper = null;
	private Notification _notification = null;
	private int _notificationType = 0;
	private NotificationActivity _notificationActivity = null;
	private SharedPreferences _preferences = null;
	private TextView _contactNameTextView = null;
	private TextView _contactNumberTextView = null;
	private TextView _notificationCountTextView = null;
	private TextView _notificationInfoTextView = null;
	private TextView _notificationDetailsTextView = null;
	private TextView _mmsLinkTextView = null;
	private ImageView _notificationIconImageView = null;
	private Button _previousButton = null;
	private Button _nextButton = null;
	private LinearLayout _buttonLinearLayout = null;
	private LinearLayout _imageButtonLinearLayout = null;
	private ImageView _ttsButton = null;
	private Button _dismissButton = null;
	private Button _deleteButton = null;
	private Button _callButton = null;
	private Button _replySMSButton = null;
	private Button _viewCalendarButton = null;
	private Button _replyEmailButton = null;
	private ImageView _rescheduleButton = null;
	private ImageButton _dismissImageButton = null;
	private ImageButton _deleteImageButton = null;
	private ImageButton _callImageButton = null;
	private ImageButton _replySMSImageButton = null;
	private ImageButton _viewCalendarImageButton = null;
	private ImageButton _replyEmailImageButton = null;
	private LinearLayout _contactLinearLayout = null;
	private ImageView _photoImageView = null;
	private ProgressBar _photoProgressBar = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * Class Constructor.
     */	
	public NotificationView(Context context,  Notification notification) {
	    super(context);
	    _debug = Log.getDebug();
	    if (_debug) Log.v("NotificationView.NotificationView()");
	    _context = context;
	    _preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    _notificationActivity = (NotificationActivity)context;
	    _notification = notification;
	    _notificationType = notification.getNotificationType();
	    initLayoutItems(context);
	    initLongPressView();
	    setupNotificationViewButtons(notification);
	    populateNotificationViewInfo(notification);
	}

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 * 
	 * @param context - Application's Context.
	 */
	private void initLayoutItems(Context context) {
		if (_debug) Log.v("NotificationView.initLayoutItems()");
		//Set based on the theme. This is set in the user preferences.
		String applicationThemeSetting = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		int themeResource = R.layout.android_froyo_theme_notification;
		if(applicationThemeSetting.equals(Constants.ANDROID_FROYO_THEME)) themeResource = R.layout.android_froyo_theme_notification;
		if(applicationThemeSetting.equals(Constants.ANDROID_GINGERBREAD_THEME)) themeResource = R.layout.android_gingerbread_theme_notification;
		if(applicationThemeSetting.equals(Constants.IPHONE_THEME)) themeResource = R.layout.iphone_theme_notification;
		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_THEME)) themeResource = R.layout.dark_translucent_theme_notification;
		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V2_THEME)) themeResource = R.layout.dark_translucent_v2_theme_notification;
		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V3_THEME)) themeResource = R.layout.dark_translucent_v3_theme_notification;
		if(applicationThemeSetting.equals(Constants.HTC_SENSE_UI_THEME)) themeResource = R.layout.htc_theme_notification;
		if(applicationThemeSetting.equals(Constants.XPERIA_THEME)) themeResource = R.layout.xperia_theme_notification;
		View.inflate(context, themeResource, this);
		_contactNameTextView = (TextView) findViewById(R.id.contact_name_text_view);
		_contactNumberTextView = (TextView) findViewById(R.id.contact_number_text_view);
		_notificationCountTextView = (TextView) findViewById(R.id.notification_count_text_view);
		_notificationInfoTextView = (TextView) findViewById(R.id.notification_info_text_view);
		_photoImageView = (ImageView) findViewById(R.id.contact_photo_image_view);
		_photoProgressBar = (ProgressBar) findViewById(R.id.contact_photo_progress_bar);
	    _notificationIconImageView = (ImageView) findViewById(R.id.notification_type_icon_image_view);  
		_notificationDetailsTextView = (TextView) findViewById(R.id.notification_details_text_view);
		_notificationDetailsTextView.setMovementMethod(new ScrollingMovementMethod());
		_mmsLinkTextView = (TextView) findViewById(R.id.mms_link_text_view);
	    _buttonLinearLayout = (LinearLayout) findViewById(R.id.button_linear_layout);
	    _imageButtonLinearLayout = (LinearLayout) findViewById(R.id.image_button_linear_layout);
		_contactLinearLayout = (LinearLayout) findViewById(R.id.contact_wrapper_linear_layout);
		_notificationViewFlipper = _notificationActivity.getNotificationViewFlipper();
		//Initialize The Button Views
		_buttonLinearLayout.setVisibility(View.GONE);
    	_imageButtonLinearLayout.setVisibility(View.GONE);
	}

	/**
	 * Sets up the NotificationView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupNotificationViewButtons(Notification notification) {
		if (_debug) Log.v("NotificationView.setupNotificationViewButtons()");
		try{
			int notificationSubType = _notification.getNotificationSubType();
			boolean usingImageButtons = true;
			String buttonDisplayStyle = _preferences.getString(Constants.BUTTON_DISPLAY_STYLE_KEY, Constants.BUTTON_DISPLAY_STYLE_DEFAULT);
			//Show the LinearLayout of the specified button style (ImageButton vs Button)
			if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_ICON_ONLY)){
				usingImageButtons = true;
				_buttonLinearLayout.setVisibility(View.GONE);
		    	_imageButtonLinearLayout.setVisibility(View.VISIBLE);
			}else{
				usingImageButtons = false;
				_buttonLinearLayout.setVisibility(View.VISIBLE);
		    	_imageButtonLinearLayout.setVisibility(View.GONE);
			}
			//Previous Button
	    	_previousButton = (Button) findViewById(R.id.previous_button);
			_previousButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("Previous Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	_notificationViewFlipper.showPrevious();
			    }
			});
			//Next Button
			_nextButton = (Button) findViewById(R.id.next_button);
			_nextButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("Next Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	_notificationViewFlipper.showNext();
			    }
			});
			//TTS Button
			_ttsButton = (ImageView) findViewById(R.id.tts_button_image_view);
			if(_preferences.getBoolean(Constants.DISPLAY_TEXT_TO_SPEECH_KEY, true)){
				OnClickListener ttsButtonOnClickListener = new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("TTS Image Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	_notificationActivity.speak();
				    }
				};
				_ttsButton.setOnClickListener(ttsButtonOnClickListener);
			}else{
				_ttsButton.setVisibility(View.GONE);
			}
			//Reschedule Button
			_rescheduleButton = (ImageView) findViewById(R.id.reschedule_button_image_view);
			if(_preferences.getBoolean(Constants.DISPLAY_RESCHEDULE_BUTTON_KEY, true)){
				OnClickListener rescheduleButtonOnClickListener = new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("Reschedule Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	_notificationViewFlipper.rescheduleNotification();
				    }
				};
				_rescheduleButton.setOnClickListener(rescheduleButtonOnClickListener);
			}else{
				_rescheduleButton.setVisibility(View.GONE);
			}
			//Buttons
			_dismissButton = (Button) findViewById(R.id.dismiss_button);
			_deleteButton = (Button) findViewById(R.id.delete_button);
			_callButton = (Button) findViewById(R.id.call_button);
			_replySMSButton = (Button) findViewById(R.id.reply_sms_button);
			_viewCalendarButton = (Button) findViewById(R.id.view_calendar_button);
			_replyEmailButton = (Button) findViewById(R.id.reply_email_button);
			//Image Buttons
			_dismissImageButton = (ImageButton) findViewById(R.id.dismiss_image_button);
			_deleteImageButton = (ImageButton) findViewById(R.id.delete_image_button);
			_callImageButton = (ImageButton) findViewById(R.id.call_image_button);
			_replySMSImageButton = (ImageButton) findViewById(R.id.reply_sms_image_button);
			_viewCalendarImageButton = (ImageButton) findViewById(R.id.view_calendar_image_button);
			_replyEmailImageButton = (ImageButton) findViewById(R.id.reply_email_image_button);
			//Remove the icons from the View's buttons, based on the user preferences.
			if(buttonDisplayStyle.equals(Constants.BUTTON_DISPLAY_TEXT_ONLY)){
				_dismissButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				_deleteButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				_callButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				_replySMSButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				_viewCalendarButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				_replyEmailButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
			switch(_notificationType){
				case Constants.NOTIFICATION_TYPE_PHONE:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.PHONE_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else if(notificationCountAction == 1){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startCallLogViewActivity(_context, _notificationActivity, Constants.VIEW_CALL_LOG_ACTIVITY);
						    }
						});			
					}
					if(usingImageButtons){
						//Dismiss Button
				    	if(_preferences.getBoolean(Constants.PHONE_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View v) {
							    	if (_debug) Log.v("Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// Call Button
						if(_preferences.getBoolean(Constants.PHONE_DISPLAY_CALL_BUTTON_KEY, true)){
				    		_callImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View v) {
							    	if (_debug) Log.v("Call Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	Common.makePhoneCall(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.CALL_ACTIVITY);
							    }
							});
				    	}else{
							_callImageButton.setVisibility(View.GONE);
				    	}			
					}else{
						//Dismiss Button
				    	if(_preferences.getBoolean(Constants.PHONE_DISPLAY_DISMISS_BUTTON_KEY, true)){
							_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View v) {
							    	if (_debug) Log.v("Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// Call Button
						if(_preferences.getBoolean(Constants.PHONE_DISPLAY_CALL_BUTTON_KEY, true)){
				    		_callButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View v) {
							    	if (_debug) Log.v("Call Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	Common.makePhoneCall(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.CALL_ACTIVITY);
							    }
							});
				    	}else{
							_callButton.setVisibility(View.GONE);
				    	}
					}
					_deleteButton.setVisibility(View.GONE);
					_replySMSButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_replyEmailButton.setVisibility(View.GONE);
					_deleteImageButton.setVisibility(View.GONE);
					_replySMSImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					_replyEmailImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_SMS:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.SMS_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else if(notificationCountAction == 1){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
						    }
						});	
					}else if(notificationCountAction == 2){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.VIEW_SMS_THREAD_ACTIVITY);
						    }
						});	
					}else if(notificationCountAction == 3){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewInboxActivity(_context, _notificationActivity, Constants.MESSAGING_ACTIVITY);
						    }
						});		
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{		
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
				    		_deleteImageButton.setVisibility(View.GONE);
				    	}
						// Reply Button;
						if(_preferences.getBoolean(Constants.SMS_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replySMSImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_SMS);
							    }
							});
				    	}else{
				    		_replySMSImageButton.setVisibility(View.GONE);
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.SMS_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{		
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.SMS_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
				    		_deleteButton.setVisibility(View.GONE);
				    	}
						// Reply Button;
						if(_preferences.getBoolean(Constants.SMS_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replySMSButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("SMS Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_SMS);
							    }
							});
				    	}else{
				    		_replySMSButton.setVisibility(View.GONE);
				    	}
					}
					_callButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_replyEmailButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					_replyEmailImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.MMS_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else if(notificationCountAction == 1){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
						    }
						});	
					}else if(notificationCountAction == 2){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.VIEW_SMS_THREAD_ACTIVITY);
						    }
						});	
					}else if(notificationCountAction == 3){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewInboxActivity(_context, _notificationActivity, Constants.MESSAGING_ACTIVITY);
						    }
						});		
					}
					//Setup MMS Link
					if(!_preferences.getBoolean(Constants.MMS_HIDE_NOTIFICATION_BODY_KEY, false)){
						_mmsLinkTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification MMS Link Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startMessagingAppViewThreadActivity(_context, _notificationActivity, _notification.getSentFromAddress(), Constants.VIEW_SMS_MESSAGE_ACTIVITY);
						    }
						});
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.MMS_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{		
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.MMS_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
				    		_deleteImageButton.setVisibility(View.GONE);
				    	}
						// Reply Button;
						if(_preferences.getBoolean(Constants.MMS_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replySMSImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_MMS);
							    }
							});
				    	}else{
				    		_replySMSImageButton.setVisibility(View.GONE);
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.MMS_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{		
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.MMS_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
				    		_deleteButton.setVisibility(View.GONE);
				    	}
						// Reply Button;
						if(_preferences.getBoolean(Constants.MMS_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replySMSButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("MMS Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_MMS);
							    }
							});
				    	}else{
				    		_replySMSButton.setVisibility(View.GONE);
				    	}
					}
					_callButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_replyEmailButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					_replyEmailImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.CALENDAR_NOTIFICATION_COUNT_ACTION_KEY, Constants.NOTIFICATION_COUNT_ACTION_NOTHING));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else if(notificationCountAction == 1){
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startViewCalendarActivity(_context, _notificationActivity, Constants.CALENDAR_ACTIVITY);
						    }
						});			
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Calendar Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{	
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// View Button
				    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_VIEW_BUTTON_KEY, true)){
				    		_viewCalendarImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Calendar View Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	//viewCalendarEvent();
							    	Common.startViewCalendarEventActivity(_context, _notificationActivity, _notification.getCalendarEventID(), _notification.getCalendarEventStartTime(), _notification.getCalendarEventEndTime(), Constants.VIEW_CALENDAR_ACTIVITY);
							    }
							});
				    	}else{
				    		_viewCalendarImageButton.setVisibility(View.GONE);
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Calendar Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});
				    	}else{	
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// View Button
				    	if(_preferences.getBoolean(Constants.CALENDAR_DISPLAY_VIEW_BUTTON_KEY, true)){
				    		_viewCalendarButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Calendar View Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	//viewCalendarEvent();
							    	Common.startViewCalendarEventActivity(_context, _notificationActivity, _notification.getCalendarEventID(), _notification.getCalendarEventStartTime(), _notification.getCalendarEventEndTime(), Constants.VIEW_CALENDAR_ACTIVITY);
							    }
							});
				    	}else{
				    		_viewCalendarButton.setVisibility(View.GONE);
				    	}
					}
					_deleteButton.setVisibility(View.GONE);
					_callButton.setVisibility(View.GONE);
					_replySMSButton.setVisibility(View.GONE);
					_replyEmailButton.setVisibility(View.GONE);
					_deleteImageButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_replySMSImageButton.setVisibility(View.GONE);
					_replyEmailImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
	
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.TWITTER_NOTIFICATION_COUNT_ACTION_KEY, Constants.TWITTER_NOTIFICATION_COUNT_ACTION_LAUNCH_TWITTER_APP));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else{
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	TwitterCommon.startTwitterAppActivity(_context, _notificationActivity, Constants.TWITTER_OPEN_APP_ACTIVITY);
						    }
						});		
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Twitter Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}		
				    	// Delete Button
				    	if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION || notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
				    		_deleteImageButton.setVisibility(View.GONE);
				    	}else{							
							if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_DELETE_BUTTON_KEY, true)){
					    		_deleteImageButton.setOnClickListener(new OnClickListener() {
								    public void onClick(View view) {
								    	if (_debug) Log.v("Twitter Delete Button Clicked()");
								    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								    	showDeleteDialog();
								    }
								});
					    	}else{
								_deleteImageButton.setVisibility(View.GONE);
					    	}
				    	}
						// Reply Button
				    	if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
				    		_replyEmailImageButton.setVisibility(View.GONE);
				    	}else{	
							if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_REPLY_BUTTON_KEY, true)){
					    		_replyEmailImageButton.setOnClickListener(new OnClickListener() {
								    public void onClick(View view) {
								    	if (_debug) Log.v("Twitter Reply Button Clicked()");
								    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								    	replyToMessage(Constants.NOTIFICATION_TYPE_TWITTER);
								    }
								});
					    	}else{
								_replyEmailImageButton.setVisibility(View.GONE);
					    	}
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Twitter Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissButton.setVisibility(View.GONE);
				    	}		
				    	// Delete Button
				    	if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION || notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
				    		_deleteButton.setVisibility(View.GONE);
				    	}else{
							if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_DELETE_BUTTON_KEY, true)){
					    		_deleteButton.setOnClickListener(new OnClickListener() {
								    public void onClick(View view) {
								    	if (_debug) Log.v("Twitter Delete Button Clicked()");
								    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								    	showDeleteDialog();
								    }
								});
					    	}else{
								_deleteButton.setVisibility(View.GONE);
					    	}
				    	}
						// Reply Button
				    	if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
				    		_replyEmailButton.setVisibility(View.GONE);
				    	}else{	
							if(_preferences.getBoolean(Constants.TWITTER_DISPLAY_REPLY_BUTTON_KEY, true)){
					    		_replyEmailButton.setOnClickListener(new OnClickListener() {
								    public void onClick(View view) {
								    	if (_debug) Log.v("Twitter Reply Button Clicked()");
								    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
								    	replyToMessage(Constants.NOTIFICATION_TYPE_TWITTER);
								    }
								});
					    	}else{
								_replyEmailButton.setVisibility(View.GONE);
					    	}
				    	}
					}
					_callButton.setVisibility(View.GONE);
					_replySMSButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_replySMSImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.FACEBOOK_NOTIFICATION_COUNT_ACTION_KEY, Constants.FACEBOOK_NOTIFICATION_COUNT_ACTION_LAUNCH_FACEBOOK_APP));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else{
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	//TODO
						    	//Common.startTwitterAppViewInboxActivity(_context, _notificationActivity, Constants.FACEBOOK_VIEW_INBOX_ACTIVITY);
						    }
						});		
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
							_deleteImageButton.setVisibility(View.GONE);
				    	}
						// Reply Button
						if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replyEmailImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_FACEBOOK);
							    }
							});
				    	}else{
							_replyEmailImageButton.setVisibility(View.GONE);
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
							_deleteButton.setVisibility(View.GONE);
				    	}
						// Reply Button
						if(_preferences.getBoolean(Constants.FACEBOOK_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replyEmailButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("Facebook Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_FACEBOOK);
							    }
							});
				    	}else{
							_replyEmailButton.setVisibility(View.GONE);
				    	}
					}
					_callButton.setVisibility(View.GONE);
					_replySMSButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_replySMSImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					// Notification Count Text Button
					int notificationCountAction = Integer.parseInt(_preferences.getString(Constants.K9_NOTIFICATION_COUNT_ACTION_KEY, Constants.K9_NOTIFICATION_COUNT_ACTION_K9_INBOX));
					if(notificationCountAction == 0){
						//Do Nothing.
					}else{
						_notificationCountTextView.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						    	if (_debug) Log.v("Notification Count Button Clicked()");
						    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
						    	Common.startK9EmailAppViewInboxActivity(_context, _notificationActivity, Constants.K9_VIEW_INBOX_ACTIVITY);
						    }
						});		
					}
					if(usingImageButtons){
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.K9_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissImageButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.K9_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
							_deleteImageButton.setVisibility(View.GONE);
				    	}
						// Reply Button
						if(_preferences.getBoolean(Constants.K9_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replyEmailImageButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_K9);
							    }
							});
				    	}else{
							_replyEmailImageButton.setVisibility(View.GONE);
				    	}
					}else{
						// Dismiss Button
				    	if(_preferences.getBoolean(Constants.K9_DISPLAY_DISMISS_BUTTON_KEY, true)){
				    		_dismissButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Dismiss Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	dismissNotification(false);
							    }
							});	
				    	}else{
				    		_dismissButton.setVisibility(View.GONE);
				    	}
						// Delete Button
						if(_preferences.getBoolean(Constants.K9_DISPLAY_DELETE_BUTTON_KEY, true)){
				    		_deleteButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Delete Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	showDeleteDialog();
							    }
							});
				    	}else{
							_deleteButton.setVisibility(View.GONE);
				    	}
						// Reply Button
						if(_preferences.getBoolean(Constants.K9_DISPLAY_REPLY_BUTTON_KEY, true)){
				    		_replyEmailButton.setOnClickListener(new OnClickListener() {
							    public void onClick(View view) {
							    	if (_debug) Log.v("K9 Reply Button Clicked()");
							    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
							    	replyToMessage(Constants.NOTIFICATION_TYPE_K9);
							    }
							});
				    	}else{
							_replyEmailButton.setVisibility(View.GONE);
				    	}
					}
					_callButton.setVisibility(View.GONE);
					_replySMSButton.setVisibility(View.GONE);
					_viewCalendarButton.setVisibility(View.GONE);
					_callImageButton.setVisibility(View.GONE);
					_replySMSImageButton.setVisibility(View.GONE);
					_viewCalendarImageButton.setVisibility(View.GONE);
					break;
				}
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.setupNotificationViewButtons() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void populateNotificationViewInfo(Notification notification) {
		if (_debug) Log.v("NotificationView.populateNotificationViewInfo()");
		boolean loadContactPhoto = true;
		String notificationTitle = notification.getTitle();
    	if(notificationTitle == null || notificationTitle.equals("")){
    		notificationTitle = "No Title";
    	}
	    // Set from, number, message etc. views.
		if(_notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
			_contactNameTextView.setText(notificationTitle);
			_contactNumberTextView.setVisibility(View.GONE);
			_photoImageView.setVisibility(View.GONE);
			_photoProgressBar.setVisibility(View.GONE);
			if(_preferences.getBoolean(Constants.CALENDAR_HIDE_NOTIFICATION_BODY_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}else{
				//Set Message Body Font
				_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
			loadContactPhoto = false;
		}else{
			//Show/Hide Contact Name
			if(_preferences.getBoolean(Constants.CONTACT_NAME_DISPLAY_KEY, true)){
				_contactNameTextView.setText(notification.getContactName());
				_contactNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NAME_SIZE_KEY, Constants.CONTACT_NAME_SIZE_DEFAULT)));
				_contactNameTextView.setVisibility(View.VISIBLE);
			}else{
				_contactNameTextView.setVisibility(View.GONE);
			}
			//Show/Hide Contact Number
			if(_preferences.getBoolean(Constants.CONTACT_NUMBER_DISPLAY_KEY, true)){
				String sentFromAddress = notification.getSentFromAddress();
			    if(sentFromAddress.contains("@")){
			    	_contactNumberTextView.setText(sentFromAddress);
			    }else{
			    	if(_notificationType == Constants.NOTIFICATION_TYPE_TWITTER || _notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
			    		_contactNumberTextView.setText(sentFromAddress);
			    	}else{
			    		_contactNumberTextView.setText(Common.formatPhoneNumber(_context, sentFromAddress));
			    	}
			    	
			    }
			    _contactNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.CONTACT_NUMBER_SIZE_KEY, Constants.CONTACT_NUMBER_SIZE_DEFAULT)));
			    _contactNumberTextView.setVisibility(View.VISIBLE);
			}else{
				_contactNumberTextView.setVisibility(View.GONE);
			}
			//Show/Hide Contact Photo
			if(_preferences.getBoolean(Constants.CONTACT_PHOTO_DISPLAY_KEY, true)){
				//Set Contact Photo Background
				int contactPhotoBackground = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_BACKGKROUND_KEY, "0"));
				if(contactPhotoBackground == 1){
					_photoImageView.setBackgroundResource(R.drawable.image_picture_frame_froyo);
				}else if(contactPhotoBackground == 2){
					_photoImageView.setBackgroundResource(R.drawable.image_picture_frame_gingerbread);
				}else{
					_photoImageView.setBackgroundResource(R.drawable.image_picture_frame_white);
				}
			}else{
				_photoImageView.setVisibility(View.GONE);
				_photoProgressBar.setVisibility(View.GONE);
				loadContactPhoto = false;
			}
		    //Add the Quick Contact Android Widget to the Contact Photo.
		    setupQuickContact();
		}
		if(_notificationType == Constants.NOTIFICATION_TYPE_SMS){
			if(_preferences.getBoolean(Constants.SMS_HIDE_NOTIFICATION_BODY_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}else{
				_notificationDetailsTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_MMS){
			_notificationDetailsTextView.setVisibility(View.GONE);
			if(_preferences.getBoolean(Constants.MMS_HIDE_NOTIFICATION_BODY_KEY, false)){
				_mmsLinkTextView.setVisibility(View.GONE);
			}else{
				//Display MMS Link
				_mmsLinkTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_mmsLinkTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_PHONE){
			_notificationDetailsTextView.setVisibility(View.GONE);
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_TWITTER){
			if(_preferences.getBoolean(Constants.TWITTER_HIDE_NOTIFICATION_BODY_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}else{
				_notificationDetailsTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_FACEBOOK){
			if(_preferences.getBoolean(Constants.FACEBOOK_HIDE_NOTIFICATION_BODY_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}else{
				_notificationDetailsTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_GMAIL){
			
		}else if(_notificationType == Constants.NOTIFICATION_TYPE_K9){
			if(_preferences.getBoolean(Constants.K9_HIDE_NOTIFICATION_BODY_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}else{
				_notificationDetailsTextView.setVisibility(View.VISIBLE);
				//Set Message Body Font
				_notificationDetailsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_BODY_FONT_SIZE_KEY, Constants.NOTIFICATION_BODY_FONT_SIZE_DEFAULT)));
			}
		}
	    //Load the notification message.
	    setNotificationMessage(notification);
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo(notification);
	    //Add context menu items.
	    setupContextMenus();
	    //Load the image from the users contacts.
    	if(loadContactPhoto){
    		new setNotificationContactImageAsyncTask().execute(notification.getContactID());
    	}
	}
	
	/**
	 * Set the notification message. 
	 * This is specific to the type of notification that was received.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationMessage(Notification notification){
		if (_debug) Log.v("NotificationView.setNotificationMessage()");
		String notificationText = "";
		int notificationAlignment = Gravity.LEFT;
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
				notificationText = "Missed Call!";
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
				notificationText = notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				notificationText = notification.getMessageBody();	
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	notificationText = notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_GMAIL:{
				notificationText = notification.getMessageBody();	
				break;
			}
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				notificationText = notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
				notificationText = notification.getMessageBody();
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				notificationText = notification.getMessageBody();
				break;
			}
		} 
	    _notificationDetailsTextView.setText(Html.fromHtml(notificationText));
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
	private void setNotificationTypeInfo(Notification notification){
		if (_debug) Log.v("NotificationView.set_notificationTypeInfo()");
		Bitmap iconBitmap = null;
		// Update TextView that contains the image, contact info/calendar info, and timestamp for the Notification.
		String formattedTimestamp = Common.formatTimestamp(_context, notification.getTimeStamp());
	    String receivedAtText = "";
		switch(_notificationType){
			case Constants.NOTIFICATION_TYPE_PHONE:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_missed_call);
		    	receivedAtText = _context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_SMS:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.sms);
		    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.sms);
		    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_CALENDAR:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.calendar);
		    	receivedAtText = _context.getString(R.string.calendar_event_text);
				break;
			}
			case Constants.NOTIFICATION_TYPE_GMAIL:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_envelope_white);
		    	receivedAtText = _context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
				break;
			}
			case Constants.NOTIFICATION_TYPE_TWITTER:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.twitter);
		    	int notificationSubType = _notification.getNotificationSubType();
			    if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
			    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
			    	receivedAtText = _context.getString(R.string.mention_at_text, formattedTimestamp.toLowerCase());
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST){
			    	receivedAtText = _context.getString(R.string.follower_request_at_text, formattedTimestamp.toLowerCase());
		    	}
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.facebook);
		    	int notificationSubType = _notification.getNotificationSubType();
			    if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
			    	receivedAtText = _context.getString(R.string.notification_at_text, formattedTimestamp.toLowerCase());
			    }else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
			    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
			    	receivedAtText = _context.getString(R.string.friend_request_at_text, formattedTimestamp.toLowerCase());
		    	}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
		    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_envelope_white);
		    	receivedAtText = _context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
				break;
			}
		}   
		if(_preferences.getBoolean(Constants.NOTIFICATION_TYPE_INFO_ICON_KEY, true)){
		    if(iconBitmap != null){
		    	_notificationIconImageView.setImageBitmap(iconBitmap);
		    	_notificationIconImageView.setVisibility(View.VISIBLE);
		    }
		}else{
			_notificationIconImageView.setVisibility(View.GONE);
		}
		_notificationInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(_preferences.getString(Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_KEY, Constants.NOTIFICATION_TYPE_INFO_FONT_SIZE_DEFAULT)));
	    _notificationInfoTextView.setText(receivedAtText);
	}
	
	/**
	 * Remove the notification from the ViewFlipper.
	 */
	private void dismissNotification(boolean reschedule){
		if (_debug) Log.v("NotificationView.dismissNotification()");
		_notificationViewFlipper.removeActiveNotification(reschedule);
	}
	
	/**
	 * Launches a new Activity.
	 * Replies to the current message using the stock Android messaging app.
	 */
	private void replyToMessage(int notificationType) {
		if (_debug) Log.v("NotificationView.replyToMessage()");
		//Setup Reply action.
		String phoneNumber = _notification.getSentFromAddress();
		if(phoneNumber == null){
			Toast.makeText(_context, _context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
			return;
		}
		switch(notificationType){
			case Constants.NOTIFICATION_TYPE_SMS:{
				//Reply using any installed SMS messaging app.
				if(_preferences.getString(Constants.SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(Constants.SMS_MESSAGING_APP_REPLY)){
					Common.startMessagingAppReplyActivity(_context, _notificationActivity, phoneNumber, Constants.SEND_SMS_ACTIVITY);
				}else if(_preferences.getString(Constants.SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(Constants.SMS_QUICK_REPLY)){
					//Reply using the built in Quick Reply Activity.
					Common.startMessagingQuickReplyActivity(_context, _notificationActivity, Constants.SEND_SMS_QUICK_REPLY_ACTIVITY, phoneNumber, _notification.getContactName());        
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_MMS:{
				//Reply using any installed SMS messaging app.
				if(_preferences.getString(Constants.MMS_REPLY_BUTTON_ACTION_KEY, "0").equals(Constants.MMS_MESSAGING_APP_REPLY)){
					Common.startMessagingAppReplyActivity(_context, _notificationActivity, phoneNumber, Constants.SEND_SMS_ACTIVITY);
				}else if(_preferences.getString(Constants.MMS_REPLY_BUTTON_ACTION_KEY, "0").equals(Constants.MMS_QUICK_REPLY)){
					//Reply using the built in Quick Reply Activity.
					Common.startMessagingQuickReplyActivity(_context, _notificationActivity, Constants.SEND_SMS_QUICK_REPLY_ACTIVITY, phoneNumber, _notification.getContactName());
				}
				break;
			}
			case Constants.NOTIFICATION_TYPE_TWITTER:{
				//Reply using any installed Twitter app.
				int notificationSubType = _notification.getNotificationSubType();
			    if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE){
			    	TwitterCommon.startTwitterQuickReplyActivity(_context, _notificationActivity, Constants.SEND_TWITTER_QUICK_REPLY_ACTIVITY, _notification.getSentFromID(), _notification.getSentFromAddress(), _notification.getContactName(), Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE);
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_TWITTER_MENTION){
		    		TwitterCommon.startTwitterQuickReplyActivity(_context, _notificationActivity, Constants.SEND_TWITTER_QUICK_REPLY_ACTIVITY, _notification.getSentFromID(), _notification.getSentFromAddress(), _notification.getContactName(), Constants.NOTIFICATION_TYPE_TWITTER_MENTION);
		    	}
				break;
			}
			case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		    	int notificationSubType = _notification.getNotificationSubType();
			    if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION){
			    	//TODO
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE){
		    		//TODO
		    	}else if(notificationSubType == Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST){
		    		//TODO
		    	}
				break;
			}
			case Constants.NOTIFICATION_TYPE_K9:{
				//Reply using any installed K9 email app.
				Common.startK9MailAppReplyActivity(_context, _notificationActivity, _notification.getK9EmailUri(), Constants.K9_VIEW_EMAIL_ACTIVITY);
				break;
			}
		}
	}
 
	/**
	 * Confirm the delete request of the current message.
	 */
	private void showDeleteDialog(){
		if (_debug) Log.v("NotificationView.showDeleteDialog()");
		_notificationViewFlipper.showDeleteDialog();
	}
	
	/**
	 * Setup the context menus for the various items on the notification window.
	 */
	private void setupContextMenus(){
		if (_debug) Log.v("NotificationView.setupContextMenus()"); 
	    _notificationActivity.registerForContextMenu(_contactLinearLayout);
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator)_notificationActivity.getSystemService(Context.VIBRATOR_SERVICE);
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
	
	/**
	 * Creates and sets up the animation event when a long press is performed on the contact wrapper View.
	 */
	private void initLongPressView(){
		if (_debug) Log.v("NotificationView.initLongPressView()");	
		OnTouchListener contactWrapperOnTouchListener = new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionEvent){
	     		switch (motionEvent.getAction()){
		     		case MotionEvent.ACTION_DOWN:{
		     			if (_debug) Log.v("NotificationView.initLongPressView() ACTION_DOWN");
		                String applicationThemeSetting = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		        		int listSelectorBackgroundResource = R.drawable.froyo_list_selector_background_transition;
		        		int contactWrapperTextColorResource = R.color.black;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(Constants.ANDROID_FROYO_THEME)){
		        			listSelectorBackgroundResource = R.drawable.froyo_list_selector_background_transition;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.ANDROID_GINGERBREAD_THEME)){
		        			listSelectorBackgroundResource = R.drawable.gingerbread_list_selector_background_transition;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(Constants.HTC_SENSE_UI_THEME)){
		        			listSelectorBackgroundResource = R.drawable.htc_list_selector_background_transition;
		        			contactWrapperTextColorResource = R.color.black;
		        		}	
		        		if(applicationThemeSetting.equals(Constants.XPERIA_THEME)){
		        			listSelectorBackgroundResource = R.drawable.xperia_list_selector_background_transition;
		        			contactWrapperTextColorResource = R.color.black;
		        		}		        		
	        			TransitionDrawable transition = (TransitionDrawable) _context.getResources().getDrawable(listSelectorBackgroundResource);
	        			view.setBackgroundDrawable(transition);
		                transition.setCrossFadeEnabled(true);
		                transition.startTransition(300);
		                //Set Views children font color.
		                _notificationInfoTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNameTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNumberTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                break;
			        }
		     		case MotionEvent.ACTION_UP:{
		     			if (_debug) Log.v("NotificationView.initLongPressView() ACTION_UP");
		         		String applicationThemeSetting = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		        		int listSelectorBackgroundResource = R.drawable.froyo_list_selector_background_transition;
		        		int contactWrapperTextColorResource = R.color.white;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(Constants.ANDROID_FROYO_THEME)){
		        			listSelectorBackgroundResource = R.drawable.froyo_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.ANDROID_GINGERBREAD_THEME)){
		        			listSelectorBackgroundResource = R.drawable.gingerbread_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.HTC_SENSE_UI_THEME)){
		        			listSelectorBackgroundResource = R.drawable.htc_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.XPERIA_THEME)){
		        			listSelectorBackgroundResource = R.drawable.xperia_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		                view.setBackgroundResource(listSelectorBackgroundResource);
		                //Set Views children font color.
		                _notificationInfoTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNameTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNumberTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource)); 
		                break;
		     		}
		     		case MotionEvent.ACTION_CANCEL:{
		     			if (_debug) Log.v("NotificationView.initLongPressView() ACTION_CANCEL");
		         		String applicationThemeSetting = _preferences.getString(Constants.APP_THEME_KEY, Constants.APP_THEME_DEFAULT);
		        		int listSelectorBackgroundResource = R.drawable.froyo_list_selector_background_transition;
		        		int contactWrapperTextColorResource = R.color.white;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(Constants.ANDROID_FROYO_THEME)){
		        			listSelectorBackgroundResource = R.drawable.froyo_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.ANDROID_GINGERBREAD_THEME)){
		        			listSelectorBackgroundResource = R.drawable.gingerbread_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.HTC_SENSE_UI_THEME)){
		        			listSelectorBackgroundResource = R.drawable.htc_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(Constants.XPERIA_THEME)){
		        			listSelectorBackgroundResource = R.drawable.xperia_list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		                view.setBackgroundResource(listSelectorBackgroundResource);
		                //Set Views children font color.
		                _notificationInfoTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNameTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource));
		                _contactNumberTextView.setTextColor(_context.getResources().getColor(contactWrapperTextColorResource)); 
		                break;
		     		}
	     		}
	     		return false;
			}
	     };
	     LinearLayout contactWrapperLinearLayout = (LinearLayout) findViewById(R.id.contact_wrapper_linear_layout);
	     contactWrapperLinearLayout.setOnTouchListener(contactWrapperOnTouchListener);
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
	    protected void onPreExecute() {
			if (_debug) Log.v("NotificationView.setNotificationContactImageAsyncTask.onPreExecute()");
	    	_photoImageView.setVisibility(View.GONE);
	    	_photoProgressBar.setVisibility(View.VISIBLE);
	    }
	    
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - The contact's id.
	     */
	    protected Bitmap doInBackground(Long... params) {
			if (_debug) Log.v("NotificationView.setNotificationContactImageAsyncTask.doInBackground()");
	    	return getNotificationContactImage(params[0]);
	    }
	    
	    /**
	     * Set the image to the notification View.
	     * 
	     * @param result - The image of the contact.
	     */
	    protected void onPostExecute(Bitmap result) {
			if (_debug) Log.v("NotificationView.setNotificationContactImageAsyncTask.onPostExecute()");
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
		if (_debug) Log.v("NotificationView.getNotificationContactImage()");
	    //Load contact photo if it exists.
		try{
		    Bitmap bitmap = getContactImage(contactID);
	    	int contactPhotoSize = Integer.parseInt(_preferences.getString(Constants.CONTACT_PHOTO_SIZE_KEY, Constants.CONTACT_PHOTO_SIZE_DEFAULT));
		    if(bitmap!=null){
		    	return Common.getRoundedCornerBitmap(bitmap, 5, true, contactPhotoSize, contactPhotoSize);
		    }else{
		    	// Load the placeholder image if the contact has no photo.
		    	// This is based on user preferences from a list of predefined images.
		    	String contactPlaceholderImageID = _preferences.getString(Constants.CONTACT_PLACEHOLDER_KEY, Constants.CONTACT_PLACEHOLDER_DEFAULT);
		    	//Default image resource.
		    	int contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_1;
		    	if(contactPlaceholderImageID.equals("0")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_1;
		    	}else if(contactPlaceholderImageID.equals("1")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_2;
		    	}else if(contactPlaceholderImageID.equals("2")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_3;
		    	}else if(contactPlaceholderImageID.equals("3")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_4;
		    	}else if(contactPlaceholderImageID.equals("4")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_5;
		    	}else if(contactPlaceholderImageID.equals("5")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_6;
		    	}else if(contactPlaceholderImageID.equals("6")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_7;
		    	}else if(contactPlaceholderImageID.equals("7")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_8;
		    	}else if(contactPlaceholderImageID.equals("8")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_9;
		    	}else if(contactPlaceholderImageID.equals("9")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_10;
		    	}else if(contactPlaceholderImageID.equals("10")){
		    		contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_11;
		    	}
		    	return Common.getRoundedCornerBitmap(BitmapFactory.decodeResource(_context.getResources(), contactPlaceholderImageResourceID), 5, true, contactPhotoSize, contactPhotoSize);
		    }
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.getNotificationContactImage() ERROR: " + ex.toString());
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
		if (_debug) Log.v("NotificationView.getContactImage()");
		try{
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(_context.getContentResolver(), uri);
			Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
			if(contactPhotoBitmap!= null){
				return contactPhotoBitmap;
			}else{
				return null;
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.getContactImage() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Add the QuickContact widget to the Contact Photo. This is added to the OnClick event of the photo.
	 */
	private void setupQuickContact(){
		final String lookupKey = _notification.getLookupKey();
		if(lookupKey != null && !lookupKey.equals("")){
			_photoImageView.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("Contact Photo Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	try{
			    		ContactsContract.QuickContact.showQuickContact(_context, _photoImageView, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey), ContactsContract.QuickContact.MODE_MEDIUM, null);
			    	}catch(Exception ex){
			    		if (_debug) Log.e("Contact Photo Clicked ContactsContract.QuickContact.showQuickContact() Error: " + ex.toString());
			    	}
			    }
			});
		}
	}
	
}