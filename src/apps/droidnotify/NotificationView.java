package apps.droidnotify;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import apps.droidnotify.log.Log;

/**
 * This class is the view which the ViewFlipper displays for each notification.
 * 
 * @author Camille Sévigny
 */
public class NotificationView extends LinearLayout {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private static final int SQUARE_IMAGE_SIZE = 80;
	
	private static final int NOTIFICATION_TYPE_PHONE = 0;
	private static final int NOTIFICATION_TYPE_SMS = 1;
	private static final int NOTIFICATION_TYPE_MMS = 2;
	private static final int NOTIFICATION_TYPE_CALENDAR = 3;
	private static final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private static final int PHONE_NUMBER_FORMAT_A = 1;
	private static final int PHONE_NUMBER_FORMAT_B = 2;
	private static final int PHONE_NUMBER_FORMAT_C = 3;
	private static final int PHONE_NUMBER_FORMAT_D = 4;
	private static final int PHONE_NUMBER_FORMAT_E = 5;
	
	//private static final int ADD_CONTACT_ACTIVITY = 1;
	//private static final int EDIT_CONTACT_ACTIVITY = 2;
	//private static final int VIEW_CONTACT_ACTIVITY = 3;
	private static final int SEND_SMS_ACTIVITY = 4;
	//private static final int MESSAGING_ACTIVITY = 5;
	//private static final int VIEW_SMS_MESSAGE_ACTIVITY = 6;
	//private static final int VIEW_SMS_THREAD_ACTIVITY = 7;
	private static final int CALL_ACTIVITY = 8;
	//private static final int CALENDAR_ACTIVITY = 9;
	//private static final int ADD_CALENDAR_ACTIVITY = 10;
	//private static final int EDIT_CALENDAR_ACTIVITY = 11;
	private static final int VIEW_CALENDAR_ACTIVITY = 12;
	private static final int SEND_SMS_QUICK_REPLY_ACTIVITY = 13;
	
	private static final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	private static final String SMS_REPLY_BUTTON_ACTION_KEY = "sms_reply_button_action";
	private static final String MMS_REPLY_BUTTON_ACTION_KEY = "mms_reply_button_action";
	private static final String CONTACT_PLACEHOLDER_KEY = "contact_placeholder";
	private static final String BUTTON_ICONS_KEY = "button_icons_enabled";
	private static final String PHONE_NUMBER_FORMAT_KEY = "phone_number_format_settings";
	private static final String SMS_HIDE_MESSAGE_KEY = "sms_hide_message_body_enabled";
	private static final String MMS_HIDE_MESSAGE_KEY = "mms_hide_message_body_enabled";
	private static final String SMS_HIDE_DISMISS_BUTTON_KEY = "sms_hide_dismiss_button_enabled";
	private static final String SMS_HIDE_DELETE_BUTTON_KEY = "sms_hide_delete_button_enabled";
	private static final String SMS_HIDE_REPLY_BUTTON_KEY = "sms_hide_reply_button_enabled";
	private static final String MMS_HIDE_DISMISS_BUTTON_KEY = "mms_hide_dismiss_button_enabled";
	private static final String MMS_HIDE_DELETE_BUTTON_KEY = "mms_hide_delete_button_enabled";
	private static final String MMS_HIDE_REPLY_BUTTON_KEY = "mms_hide_reply_button_enabled";
	private static final String PHONE_HIDE_DISMISS_BUTTON_KEY = "missed_call_hide_dismiss_button_enabled";
	private static final String PHONE_HIDE_CALL_BUTTON_KEY = "missed_call_hide_call_button_enabled";
	private static final String CALENDAR_HIDE_DISMISS_BUTTON_KEY = "calendar_hide_dismiss_button_enabled";
	private static final String CALENDAR_HIDE_VIEW_BUTTON_KEY = "calendar_hide_view_button_enabled";
	
	private static final String APP_THEME_KEY = "app_theme";
	private static final String ANDROID_THEME = "android";
	private static final String ANDROID_DARK_THEME = "android_dark";
	private static final String IPHONE_THEME = "iphone";
	private static final String DARK_TRANSLUCENT_THEME = "dark_translucent";
	private static final String DARK_TRANSLUCENT_V2_THEME = "dark_translucent_v2";
	private static final String DARK_TRANSLUCENT_V3_THEME = "dark_translucent_v3";
	
	private static final String SMS_MESSAGING_APP_REPLY = "0";
	private static final String SMS_QUICK_REPLY = "1";

	private static final String MMS_MESSAGING_APP_REPLY = "0";
	private static final String MMS_QUICK_REPLY = "1";
	
	private static final String EVENT_BEGIN_TIME = "beginTime";
	private static final String EVENT_END_TIME = "endTime";
	
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
	private TextView _notificationInfoTextView = null;
	private TextView _notificationDetailsTextView = null;
	private ImageView _notificationIconImageView = null;
	private ImageView _photoImageView = null;
	private LinearLayout _contactLinearLayout = null;
	private LinearLayout _phoneButtonLinearLayout = null;
	private LinearLayout _smsButtonLinearLayout = null;
	private LinearLayout _calendarButtonLinearLayout = null;
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
		String applicationThemeSetting = _preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		int themeResource = R.layout.android_theme_notification;
		if(applicationThemeSetting.equals(ANDROID_THEME)) themeResource = R.layout.android_theme_notification;
		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)) themeResource = R.layout.android_dark_theme_notification;
		if(applicationThemeSetting.equals(IPHONE_THEME)) themeResource = R.layout.iphone_theme_notification;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_THEME)) themeResource = R.layout.dark_translucent_theme_notification;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V2_THEME)) themeResource = R.layout.dark_translucent_v2_theme_notification;
		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V3_THEME)) themeResource = R.layout.dark_translucent_v3_theme_notification;
		View.inflate(context, themeResource, this);
		_contactNameTextView = (TextView) findViewById(R.id.contact_name_text_view);
		_contactNumberTextView = (TextView) findViewById(R.id.contact_number_text_view);
		//Automatically format the phone number in this text view.
		//_contactNumberTextView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		_notificationInfoTextView = (TextView) findViewById(R.id.notification_info_text_view);
		_photoImageView = (ImageView) findViewById(R.id.contact_photo_image_view);
		_photoProgressBar = (ProgressBar) findViewById(R.id.contact_photo_progress_bar);
	    _notificationIconImageView = (ImageView) findViewById(R.id.notification_type_icon_image_view);    
		_notificationDetailsTextView = (TextView) findViewById(R.id.notification_details_text_view);
		_notificationDetailsTextView.setMovementMethod(new ScrollingMovementMethod());
		//_notificationDetailsTextView.setScrollbarFadingEnabled(false);
	    _phoneButtonLinearLayout = (LinearLayout) findViewById(R.id.phone_button_linear_layout);
	    _smsButtonLinearLayout = (LinearLayout) findViewById(R.id.sms_button_linear_layout);
	    _calendarButtonLinearLayout = (LinearLayout) findViewById(R.id.calendar_button_linear_layout);
		_contactLinearLayout = (LinearLayout) findViewById(R.id.contact_wrapper_linear_layout);
		_notificationViewFlipper = _notificationActivity.getNotificationViewFlipper();
	}

	/**
	 * Sets up the NotificationView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupNotificationViewButtons(Notification notification) {
		if (_debug) Log.v("NotificationView.setupNotificationViewButtons()");
		int phoneButtonLayoutVisibility = View.GONE;
		int smsButtonLayoutVisibility = View.GONE;
		int calendarButtonLayoutVisibility = View.GONE;
		// Previous Button
    	final Button previousButton = (Button) findViewById(R.id.previous_button);
		previousButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Previous Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	_notificationViewFlipper.showPrevious();
		    }
		});
		// Next Button
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Next Button Clicked()");
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	_notificationViewFlipper.showNext();
		    }
		});	
	    if(_notificationType == NOTIFICATION_TYPE_PHONE){
	    	//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.VISIBLE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button phoneDismissButton = (Button) findViewById(R.id.phone_dismiss_button);
	    	if(_preferences.getBoolean(PHONE_HIDE_DISMISS_BUTTON_KEY, false)){
	    		phoneDismissButton.setVisibility(View.GONE);
	    	}else{
				phoneDismissButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View v) {
				    	if (_debug) Log.v("Dismiss Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	dismissNotification();
				    }
				});
	    	}
			// Call Button
			final Button phoneCallButton = (Button) findViewById(R.id.phone_call_button);
			if(_preferences.getBoolean(PHONE_HIDE_CALL_BUTTON_KEY, false)){
	    		phoneCallButton.setVisibility(View.GONE);
	    	}else{
				phoneCallButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View v) {
				    	if (_debug) Log.v("Call Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	makePhoneCall();
				    }
				});
	    	}
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!_preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				phoneDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				phoneCallButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
	    }
		if(_notificationType == NOTIFICATION_TYPE_SMS){
			//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.VISIBLE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button smsDismissButton = (Button) findViewById(R.id.sms_dismiss_button);
	    	if(_preferences.getBoolean(SMS_HIDE_DISMISS_BUTTON_KEY, false)){
	    		smsDismissButton.setVisibility(View.GONE);
	    	}else{
				smsDismissButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("SMS Dismiss Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	dismissNotification();
				    }
				});		
	    	}
			// Delete Button
			final Button smsDeleteButton = (Button) findViewById(R.id.sms_delete_button);
			if(_preferences.getBoolean(SMS_HIDE_DELETE_BUTTON_KEY, false)){
	    		smsDeleteButton.setVisibility(View.GONE);
	    	}else{
				smsDeleteButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("SMS Delete Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	showDeleteDialog();
				    }
				});
	    	}
			// Reply Button
			final Button smsReplyButton = (Button) findViewById(R.id.sms_reply_button);
			if(_preferences.getBoolean(SMS_HIDE_REPLY_BUTTON_KEY, false)){
	    		smsReplyButton.setVisibility(View.GONE);
	    	}else{
				smsReplyButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("SMS Reply Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	replyToMessage(NOTIFICATION_TYPE_SMS);
				    }
				});
	    	}
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!_preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				smsDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				smsDeleteButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				smsReplyButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
		}
	    if(_notificationType == NOTIFICATION_TYPE_MMS){
			//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.VISIBLE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button mmsDismissButton = (Button) findViewById(R.id.sms_dismiss_button);
	    	if(_preferences.getBoolean(MMS_HIDE_DISMISS_BUTTON_KEY, false)){
	    		mmsDismissButton.setVisibility(View.GONE);
	    	}else{
				mmsDismissButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("MMS Dismiss Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	dismissNotification();
				    }
				});	
	    	}
			// Delete Button
			final Button mmsDeleteButton = (Button) findViewById(R.id.sms_delete_button);
			if(_preferences.getBoolean(MMS_HIDE_DELETE_BUTTON_KEY, false)){
	    		mmsDeleteButton.setVisibility(View.GONE);
	    	}else{
				mmsDeleteButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("MMS Delete Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	showDeleteDialog();
				    }
				});
	    	}
			// Reply Button
			final Button mmsReplyButton = (Button) findViewById(R.id.sms_reply_button);
			if(_preferences.getBoolean(MMS_HIDE_REPLY_BUTTON_KEY, false)){
				mmsReplyButton.setVisibility(View.GONE);
	    	}else{
				mmsReplyButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("MMS Reply Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	replyToMessage(NOTIFICATION_TYPE_MMS);
				    }
				});
	    	}
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!_preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				mmsDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				mmsDeleteButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				mmsReplyButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
	    }
	    if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.VISIBLE;
			// Dismiss Button
	    	final Button calendarDismissButton = (Button) findViewById(R.id.calendar_dismiss_button);
	    	if(_preferences.getBoolean(CALENDAR_HIDE_DISMISS_BUTTON_KEY, false)){
	    		calendarDismissButton.setVisibility(View.GONE);
	    	}else{
		    	calendarDismissButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("Calendar Dismiss Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	dismissNotification();
				    }
				});	
	    	}
			// View Button
	    	final Button calendarViewButton = (Button) findViewById(R.id.calendar_view_button);
	    	if(_preferences.getBoolean(CALENDAR_HIDE_VIEW_BUTTON_KEY, false)){
	    		calendarViewButton.setVisibility(View.GONE);
	    	}else{
				calendarViewButton.setOnClickListener(new OnClickListener() {
				    public void onClick(View view) {
				    	if (_debug) Log.v("Calendar View Button Clicked()");
				    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				    	viewCalendarEvent();
				    }
				});
	    	}
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!_preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				calendarDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				calendarViewButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
	    }
	    if(_notificationType == NOTIFICATION_TYPE_EMAIL){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
	    	//TODO - Email
	    }
		_phoneButtonLinearLayout.setVisibility(phoneButtonLayoutVisibility);
    	_smsButtonLinearLayout.setVisibility(smsButtonLayoutVisibility);
    	_calendarButtonLinearLayout.setVisibility(calendarButtonLayoutVisibility);
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void populateNotificationViewInfo(Notification notification) {
		if (_debug) Log.v("NotificationView.populateNotificationViewInfo()");
	    // Set from, number, message etc. views.
		if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
			String notificationTitle = notification.getTitle();
	    	if(notificationTitle.equals("")){
	    		notificationTitle = "No Title";
	    	}
			_contactNameTextView.setText(notificationTitle);
			_contactNumberTextView.setVisibility(View.GONE);
			_photoImageView.setVisibility(View.GONE);
		}else{
			_contactNameTextView.setText(notification.getContactName());
			String sentFromAddress = notification.getSentFromAddress();
		    if(sentFromAddress.contains("@")){
		    	_contactNumberTextView.setText(sentFromAddress);
		    }else{
		    	_contactNumberTextView.setText(formatPhoneNumber(sentFromAddress));
		    }
		}
		if(_notificationType == NOTIFICATION_TYPE_SMS){
			if(_preferences.getBoolean(SMS_HIDE_MESSAGE_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}
		}
		if(_notificationType == NOTIFICATION_TYPE_MMS){
			if(_preferences.getBoolean(MMS_HIDE_MESSAGE_KEY, false)){
				_notificationDetailsTextView.setVisibility(View.GONE);
			}
		}
		if(_notificationType == NOTIFICATION_TYPE_PHONE){
			_notificationDetailsTextView.setVisibility(View.GONE);
		}
	    //Load the notification message.
	    setNotificationMessage(notification);
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo(notification);
	    //Add context menu items.
	    setupContextMenus();
	    //Load the image from the users contacts.
	    new setNotificationContactImageAsyncTask().execute(notification.getContactID());
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
	    if(_notificationType == NOTIFICATION_TYPE_PHONE){
	    	notificationText = "Missed Call!";
	    }
	    if(_notificationType == NOTIFICATION_TYPE_SMS){
	    	notificationText = notification.getMessageBody();
	    }
	    if(_notificationType == NOTIFICATION_TYPE_MMS){
	    	notificationText = notification.getMessageBody();
	    }
	    if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	String notificationTitle = notification.getTitle();
	    	if(notificationTitle.equals("")){
	    		notificationTitle = "No Title";
	    	}
	    	notificationText = "<i>" + notification.getMessageBody() + "</i><br/>" + notificationTitle;
	    }
	    if(_notificationType == NOTIFICATION_TYPE_EMAIL){
	    	notificationText = notification.getTitle();
	    } 
	    _notificationDetailsTextView.setText(Html.fromHtml(notificationText));
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
		SimpleDateFormat dateFormatted = new SimpleDateFormat("h:mma");
		dateFormatted.setTimeZone(TimeZone.getDefault());
		String formattedTimestamp = dateFormatted.format(notification.getTimeStamp());
		//String formattedTimestamp = new SimpleDateFormat("h:mma").format(notification.getTimeStamp());
	    String receivedAtText = "";
	    if(_notificationType == NOTIFICATION_TYPE_PHONE){
	    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_missed_call);
	    	receivedAtText = _context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(_notificationType == NOTIFICATION_TYPE_SMS){
	    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.sms);
	    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
	    }
	    if( _notificationType == NOTIFICATION_TYPE_MMS){
	    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.sms);
	    	receivedAtText = _context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.calendar);
	    	receivedAtText = _context.getString(R.string.calendar_event_text);
	    }
	    if(_notificationType == NOTIFICATION_TYPE_EMAIL){
	    	iconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.email);
	    	receivedAtText = _context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
	    }    
	    if(iconBitmap != null){
	    	_notificationIconImageView.setImageBitmap(iconBitmap);
	    }
	    _notificationInfoTextView.setText(receivedAtText);
	}
	
	/**
	 * Rounds the corners of a Bitmap image.
	 * 
	 * @param bitmap - The Bitmap to be formatted.
	 * @param pixels - The number of pixels as the diameter of the rounded corners.
	 * 
	 * @return Bitmap - The formatted Bitmap image.
	 */
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		if (_debug) Log.v("NotificationView.getRoundedCornerBitmap()");
		try{
			Bitmap output = null;
			if(bitmap == null){
				return null;
			}else{
		        output = Bitmap.createBitmap(
		        		bitmap.getWidth(), 
		        		bitmap
		                .getHeight(), 
		                Config.ARGB_8888);
		        Canvas canvas = new Canvas(output);
		        final int color = 0xff424242;
		        final Paint paint = new Paint();
		        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		        final RectF rectF = new RectF(rect);
		        final float roundPx = pixels;
		        paint.setAntiAlias(true);
		        canvas.drawARGB(0, 0, 0, 0);
		        paint.setColor(color);
		        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		        canvas.drawBitmap(bitmap, rect, rect, paint);
		        //Resize the Bitmap so that all images are consistent.
		        //Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
		        output = Bitmap.createScaledBitmap(output, SQUARE_IMAGE_SIZE, SQUARE_IMAGE_SIZE, true);
		        return output;
			}
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.getRoundedCornerBitmap() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Remove the notification from the ViewFlipper.
	 */
	private void dismissNotification(){
		if (_debug) Log.v("NotificationView.dismissNotification()");
		_notificationViewFlipper.removeActiveNotification();
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
		if(notificationType == NOTIFICATION_TYPE_SMS){
			//Reply using any installed SMS messaging app.
			if(_preferences.getString(SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(SMS_MESSAGING_APP_REPLY)){
				try{
					Intent intent = new Intent(Intent.ACTION_SENDTO);
				    intent.setData(Uri.parse("smsto:" + phoneNumber));
				    // Exit the app once the SMS is sent.
				    intent.putExtra("compose_mode", true);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			        _notificationActivity.startActivityForResult(intent,SEND_SMS_ACTIVITY);
				}catch(Exception ex){
					if (_debug) Log.e("NotificationView.replyToMessage() Android Reply ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
					return;
				}
			}		
			//Reply using the built in Quick Reply Activity.
			if(_preferences.getString(SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(SMS_QUICK_REPLY)){
				try{
					Intent intent = new Intent(_context, QuickReplyActivity.class);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
				    intent.putExtra("smsPhoneNumber", phoneNumber);
				    if(_notification.getContactExists()){
				    	intent.putExtra("smsName", _notification.getContactName());
				    }else{
				    	intent.putExtra("smsName", "");
				    }
				    intent.putExtra("smsMessage", "");
			        _notificationActivity.startActivityForResult(intent,SEND_SMS_QUICK_REPLY_ACTIVITY);
				}catch(Exception ex){
					if (_debug) Log.e("NotificationView.replyToMessage() Quick Reply ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
					return;
				}
			}
			//Temporary Preferences Fix
			//Remove In a month or two.
			if(Integer.parseInt(_preferences.getString(SMS_REPLY_BUTTON_ACTION_KEY, "0")) > 1){
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putString("SMS_REPLY_BUTTON_ACTION_KEY", "1");
				editor.commit();
			}
		}
		if(notificationType == NOTIFICATION_TYPE_MMS){
			//Reply using any installed SMS messaging app.
			if(_preferences.getString(MMS_REPLY_BUTTON_ACTION_KEY, "0").equals(MMS_MESSAGING_APP_REPLY)){
				try{
					Intent intent = new Intent(Intent.ACTION_SENDTO);
				    intent.setData(Uri.parse("smsto:" + phoneNumber));
				    // Exit the app once the SMS is sent.
				    intent.putExtra("compose_mode", true);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			        _notificationActivity.startActivityForResult(intent,SEND_SMS_ACTIVITY);
				}catch(Exception ex){
					if (_debug) Log.e("NotificationView.replyToMessage() Android Reply ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
					return;
				}
			}		
			//Reply using the built in Quick Reply Activity.
			if(_preferences.getString(MMS_REPLY_BUTTON_ACTION_KEY, "0").equals(MMS_QUICK_REPLY)){
				try{
					Intent intent = new Intent(_context, QuickReplyActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
				    intent.putExtra("smsPhoneNumber", phoneNumber);
				    if(_notification.getContactExists()){
				    	intent.putExtra("smsName", _notification.getContactName());
				    }else{
				    	intent.putExtra("smsName", "");
				    }
				    intent.putExtra("smsMessage", "");
			        _notificationActivity.startActivityForResult(intent,SEND_SMS_QUICK_REPLY_ACTIVITY);
				}catch(Exception ex){
					if (_debug) Log.e("NotificationView.replyToMessage() Quick Reply ERROR: " + ex.toString());
					Toast.makeText(_context, _context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
					return;
				}
			}
			//Temporary Preferences Fix
			//Remove In a month or two.
			if(Integer.parseInt(_preferences.getString(MMS_REPLY_BUTTON_ACTION_KEY, "0")) > 1){
				SharedPreferences.Editor editor = _preferences.edit();
				editor.putString("MMS_REPLY_BUTTON_ACTION_KEY", "1");
				editor.commit();
			}
		}
	}
	
	/**
	 * Launches a new Activity.
	 * Makes a phone call to the current missed call notification using the phones stock Android dialer & caller.
	 */
	private void makePhoneCall(){
		if (_debug) Log.v("NotificationView.makePhoneCall()");
		String phoneNumber = _notification.getSentFromAddress();
		String numberToBeCalled = phoneNumber;
		if(numberToBeCalled == null || numberToBeCalled.contains("@")){
			Toast.makeText(_context, _context.getString(R.string.app_android_phone_number_format_error), Toast.LENGTH_LONG).show();
			return;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + numberToBeCalled));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        _notificationActivity.startActivityForResult(intent,CALL_ACTIVITY);
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.makePhoneCall() ERROR: " + ex.toString());
			Toast.makeText(_context, _context.getString(R.string.app_android_phone_app_error), Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	/**
	 * Launches a new Activity.
	 * Views the calendar event using the stock Android calendar app.
	 */
	private void viewCalendarEvent(){
		if (_debug) Log.v("NotificationView.viewCalendarEvent()");
		long calendarEventID = _notification.getCalendarEventID();
		if(calendarEventID == 0){
			Toast.makeText(_context, _context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
			return;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//Android 2.2+
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			//Android 2.1 and below.
			//intent.setData(Uri.parse("content://calendar/events/" + String.valueOf(calendarEventID)));
			intent.putExtra(EVENT_BEGIN_TIME,_notification.getCalendarEventStartTime());
			intent.putExtra(EVENT_END_TIME,_notification.getCalendarEventEndTime());
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        _notificationActivity.startActivityForResult(intent,VIEW_CALENDAR_ACTIVITY);
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.viewCalendarEvent() ERROR: " + ex.toString());
			Toast.makeText(_context, _context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			return;
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
		if(_notificationType == NOTIFICATION_TYPE_PHONE){
			_notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(_notificationType == NOTIFICATION_TYPE_SMS){
	    	_notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(_notificationType == NOTIFICATION_TYPE_MMS){
	    	_notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(_notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	_notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(_notificationType == NOTIFICATION_TYPE_EMAIL){
	    	_notificationActivity.registerForContextMenu(_contactLinearLayout);
	    } 	
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Vibrator vibrator = (Vibrator)_notificationActivity.getSystemService(Context.VIBRATOR_SERVICE);
		//Perform the haptic feedback based on the users preferences.
		if(_preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(50);
			}
		}
		if(_preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
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
		                String applicationThemeSetting = _preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		        		int listSelectorBackgroundResource = R.layout.android_theme_notification;
		        		int contactWrapperTextColorResource = R.color.black;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(ANDROID_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
		        			contactWrapperTextColorResource = R.color.black;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_transition_blue;
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
		         		String applicationThemeSetting = _preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		        		int listSelectorBackgroundResource = R.drawable.list_selector_background;
		        		int contactWrapperTextColorResource = R.color.white;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(ANDROID_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
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
		         		String applicationThemeSetting = _preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		        		int listSelectorBackgroundResource = R.drawable.list_selector_background;
		        		int contactWrapperTextColorResource = R.color.white;
		         		//Set View background.
		        		if(applicationThemeSetting.equals(ANDROID_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(IPHONE_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V2_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
		        			contactWrapperTextColorResource = R.color.white;
		        		}
		        		if(applicationThemeSetting.equals(DARK_TRANSLUCENT_V3_THEME)){
		        			listSelectorBackgroundResource = R.drawable.list_selector_background_blue;
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
	 * Remove all non-numeric items from the phone number.
	 * 
	 * @param phoneNumber - String of original phone number.
	 * 
	 * @return phoneNumber - String of phone number with no formatting.
	 */
	private String removeFormatting(String  phoneNumber){
		if (_debug) Log.v("NotificationView.removeFormatting()");
		phoneNumber = phoneNumber.replace("-", "");
		phoneNumber = phoneNumber.replace("+", "");
		phoneNumber = phoneNumber.replace("(", "");
		phoneNumber = phoneNumber.replace(")", "");
		phoneNumber = phoneNumber.replace(" ", "");
		return phoneNumber.trim();
	}
	
	/**
	 * Function to format phone numbers.
	 * 
	 * @param inputPhoneNumber - Phone number to be formatted.
	 * 
	 * @return String - Formatted phone number string.
	 */
	private String formatPhoneNumber(String inputPhoneNumber){
		if (_debug) Log.v("NotificationView.formatPhoneNumber()");
		inputPhoneNumber = removeFormatting(inputPhoneNumber);
		StringBuilder outputPhoneNumber = new StringBuilder("");
		int phoneNumberFormatPreference = Integer.parseInt(_preferences.getString(PHONE_NUMBER_FORMAT_KEY, "1"));
		switch(phoneNumberFormatPreference){
			case PHONE_NUMBER_FORMAT_A:{
				if(inputPhoneNumber.length() >= 10){
					//Format ###-###-#### (e.g.123-456-7890)
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0,"-");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
					}else{
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0,"-");
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
				break;
			}
			case PHONE_NUMBER_FORMAT_B:{
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-##### (e.g.12-345-67890)
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 5, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 5));
					outputPhoneNumber.insert(0,"-");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0,"-");
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
				break;
			}
			case PHONE_NUMBER_FORMAT_C:{
				if(inputPhoneNumber.length() >= 10){
					//Format ##-##-##-##-## (e.g.12-34-56-78-90)
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 2, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length() - 2));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0,"-");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0,"-");
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
				break;
			}
			case PHONE_NUMBER_FORMAT_D:{
				//Format ########## (e.g.1234567890)
				outputPhoneNumber.append(inputPhoneNumber);
				break;
			}
			case PHONE_NUMBER_FORMAT_E:{
				if(inputPhoneNumber.length() >= 10){
					//Format (###) ###-#### (e.g.(123) 456-7890)
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0,"-");
					outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0,") ");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0,"(");
					}else{
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0," (");
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
				break;
			}
			default:{
				outputPhoneNumber.append(inputPhoneNumber);
				break;
			}
		}
		return outputPhoneNumber.toString();
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
		    if(bitmap!=null){
		    	return (Bitmap)getRoundedCornerBitmap(bitmap, 5);
		    }else{
		    	// Load the placeholder image if the contact has no photo.
		    	// This is based on user preferences from a list of predefined images.
		    	String contactPlaceholderImageID = _preferences.getString(CONTACT_PLACEHOLDER_KEY, "0");
		    	//Default image resource.
		    	int contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_5;
		    	if(contactPlaceholderImageID.equals("0")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_1;
		    	if(contactPlaceholderImageID.equals("1")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_2;
		    	if(contactPlaceholderImageID.equals("2")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_3;
		    	if(contactPlaceholderImageID.equals("3")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_4;
		    	if(contactPlaceholderImageID.equals("4")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_5;
		    	return (getRoundedCornerBitmap(BitmapFactory.decodeResource(_context.getResources(), contactPlaceholderImageResourceID), 5));
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
	
}
