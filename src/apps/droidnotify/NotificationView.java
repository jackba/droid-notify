package apps.droidnotify;

import java.text.SimpleDateFormat;

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
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class is the view which the ViewFlipper displays for each notification.
 * 
 * @author Camille Sévigny
 */
public class NotificationView extends LinearLayout {
	
	//================================================================================
    // Constants
    //================================================================================
	
	private final int SQUARE_IMAGE_SIZE = 80;
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final String HAPTIC_FEEDBACK_ENABLED_KEY = "haptic_feedback_enabled";
	private final String SMS_REPLY_BUTTON_ACTION_KEY = "sms_reply_button_action";
	private final String CONTACT_PLACEHOLDER_KEY = "contact_placeholder";
	private final String BUTTON_ICONS_KEY = "button_icons_enabled";
	
	private final String APP_THEME_KEY = "app_theme";
	private final String ANDROID_THEME = "android";
	private final String ANDROID_DARK_THEME = "android_dark";
	private final String IPHONE_THEME = "iphone";
	
	private final String SMS_ANDROID_REPLY = "0";
	private final String SMS_QUICK_REPLY = "1";
	
	private final String EVENT_BEGIN_TIME = "beginTime";
	private final String EVENT_END_TIME = "endTime";
	
	
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug;
	private int _notificationType;
	private TextView _fromTextView;
	private TextView _phoneNumberTextView;
	private TextView _receivedAtTextView;
	private TextView _notificationTextView;
	private ImageView _notificationIconImageView = null;
	private ImageView _photoImageView = null;
	private LinearLayout _contactLinearLayout = null;
	private NotificationViewFlipper _notificationViewFlipper = null;
	private LinearLayout _phoneButtonLinearLayout = null;
	private LinearLayout _smsButtonLinearLayout = null;
	private LinearLayout _calendarButtonLinearLayout = null;
	private Notification _notification = null;
	private float _oldTouchValue;

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
	    setNotification(notification);
	    setNotificationType(notification.getNotificationType());
	    initLayoutItems(context);
	    setupNotificationViewButtons(notification);
	    populateNotificationViewInfo(notification);
	}

	//================================================================================
	// Accessors
	//================================================================================
	
	/**
	 * Set the notificationType property.
	 * 
	 * @param notificationType - The type of notification this is.
	 */
	public void setNotificationType(int notificationType) {
		if (_debug) Log.v("NotificationView.seNotificationType()");
	    _notificationType = notificationType;
	}
	
	/**
	 * Get the notificationType property.
	 * 
	 * @return notificationType - The type of notification this is.
	 */
	public int getNotificationType() {
		if (_debug) Log.v("NotificationView.getNotificationType()");
	    return _notificationType;
	}

	/**
	 * Set the notification property.
	 * 
	 * @param notification - This view's Notification.
	 */
	public void setNotification(Notification notification) {
		if (_debug) Log.v("NotificationView.seNotification()");
	    _notification = notification;
	}
	
	/**
	 * Get the notification property.
	 * 
	 * @return notification - This view's Notification.
	 */
	public Notification getNotification() {
		if (_debug) Log.v("NotificationView.getNotification()");
	    return _notification;
	}
	
	/**
	 * Set the notificationViewFlipper property.
	 * 
	 * @param notificationViewFlipper - Applications' ViewFlipper.
	 */
	public void setNotificationViewFlipper(NotificationViewFlipper notificationViewFlipper) {
		if (_debug) Log.v("NotificationView.seNotificationViewFlipper()");
	    _notificationViewFlipper = notificationViewFlipper;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 * 
	 * @return notificationViewFlipper - Applications' ViewFlipper.
	 */
	public NotificationViewFlipper getNotificationViewFlipper() {
		if (_debug) Log.v("NotificationView.getNotificationViewFlipper()");
	    return _notificationViewFlipper;
	}
	
	/**
	 * Set the fromTextView property.
	 * 
	 * @param fromTextView - The "from" TextView.
	 */
	public void setFromTextView(TextView fromTextView) {
		if (_debug) Log.v("NotificationView.setFromTextView()");
	    _fromTextView = fromTextView;
	}
	
	/**
	 * Get the fromTextView property.
	 * 
	 * @return fromTextView - The "from" TextView.
	 */
	public TextView getFromTextView() {
		if (_debug) Log.v("NotificationView.getFromTextView()");
	    return _fromTextView;
	}
	
	/**
	 * Set the phoneNumberTextView property.
	 * 
	 * @param phoneNumberTextView - The "phone number" TextView.
	 */
	public void setPhoneNumberTextView(TextView phoneNumberTextView) {
		if (_debug) Log.v("NotificationView.setPhoneNumberTextView()");
	    _phoneNumberTextView = phoneNumberTextView;
	}
	
	/**
	 * Get the phoneNumberTextView property.
	 * 
	 * @return phoneNumberTextView - The "phone number" TextView.
	 */
	public TextView getPhoneNumberTextView() {
		if (_debug) Log.v("NotificationView.getPhoneNumberTextView()");
	    return _phoneNumberTextView;
	}
	
	/**
	 * Set the receivedAtTextView property.
	 * 
	 * @param receivedAtTextView - The "received at" TextView.
	 */
	public void setReceivedAtTextView(TextView receivedAtTextView) {
		if (_debug) Log.v("NotificationView.setReceivedAtTextView()");
	    _receivedAtTextView = receivedAtTextView;
	}
	
	/**
	 * Get the receivedAtTextView property.
	 * 
	 * @return receivedAtTextView - The "received at" TextView.
	 */
	public TextView getReceivedAtTextView() {
		if (_debug) Log.v("NotificationView.getReceivedAtTextView()");
	    return _receivedAtTextView;
	}
	
	/**
	 * Set the notificationTextView property.
	 * 
	 * @param notificationTextView - The "notification" TextView.
	 */
	public void setNotificationTextView(TextView notificationTextView) {
		if (_debug) Log.v("NotificationView.setNotificationTextView()");
	    _notificationTextView = notificationTextView;
	}
	
	/**
	 * Get the notificationTextView property.
	 * 
	 * @return notificationTextView - The "notification" TextView.
	 */
	public TextView getNotificationTextView() {
		if (_debug) Log.v("NotificationView.getNotificationTextView()");
	    return _notificationTextView;
	}
	
	/**
	 * Set the notificationIconImageView property.
	 * 
	 * @param notificationIconImageView - The "notification icon" ImageView.
	 */
	public void setNotificationIconImageView(ImageView notificationIconImageView) {
		if (_debug) Log.v("NotificationView.setNotificationIconImageView()");
	    _notificationIconImageView = notificationIconImageView;
	}
	
	/**
	 * Get the notificationIconImageView property.
	 * 
	 * @return notificationIconImageView - The "notification icon" ImageView.
	 */
	public ImageView getNotificationIconImageView() {
		if (_debug) Log.v("NotificationView.getNotificationIconImageView()");
	    return _notificationIconImageView;
	}

	/**
	 * Set the photoImageView property.
	 * 
	 * @param photoImageView - The "photo" ImageView.
	 */
	public void setPhotoImageView(ImageView photoImageView) {
		if (_debug) Log.v("NotificationView.setPhotoImageView()");
	    _photoImageView = photoImageView;
	}
	
	/**
	 * Get the photoImageView property.
	 * 
	 * @return photoImageView - The "photo" ImageView.
	 */
	public ImageView getPhotoImageView() {
		if (_debug) Log.v("NotificationView.getPhotoImageView()");
	    return _photoImageView;
	}
	
	/**
	 * Set the contactLinearLayout property.
	 * 
	 * @param contactLinearLayout - The "contact" LinearLayout.
	 */
	public void setContactLinearLayout(LinearLayout contactLinearLayout) {
		if (_debug) Log.v("NotificationView.setContactLinearLayout()");
	    _contactLinearLayout = contactLinearLayout;
	}
	
	/**
	 * Get the contactLinearLayout property.
	 * 
	 * @return contactLinearLayout - The "contact" LinearLayout.
	 */
	public LinearLayout getContactLinearLayout() {
		if (_debug) Log.v("NotificationView.getContactLinearLayout()");
	    return _contactLinearLayout;
	}

	/**
	 * Set the phoneButtonLinearLayout property.
	 * 
	 * @param phoneButtonLinearLayout - The "phone button" LinearLayout.
	 */
	public void setPhoneButtonLinearLayout(LinearLayout phoneButtonLinearLayout) {
		if (_debug) Log.v("NotificationView.setPhoneButtonLinearLayout()");
	    _phoneButtonLinearLayout = phoneButtonLinearLayout;
	}
	
	/**
	 * Get the phoneButtonLinearLayout property.
	 * 
	 * @return phoneButtonLinearLayout - The "phone button" LinearLayout.
	 */
	public LinearLayout getPhoneButtonLinearLayout() {
		if (_debug) Log.v("NotificationView.getPhoneButtonLinearLayout()");
	    return _phoneButtonLinearLayout;
	}


	/**
	 * Set the smsButtonLinearLayout property.
	 * 
	 * @param smsButtonLinearLayout - The "sms button" LinearLayout.
	 */
	public void setSMSButtonLinearLayout(LinearLayout smsButtonLinearLayout) {
		if (_debug) Log.v("NotificationView.setSMSButtonLinearLayout()");
	    _smsButtonLinearLayout = smsButtonLinearLayout;
	}
	
	/**
	 * Get the smsButtonLinearLayout property.
	 * 
	 * @return smsButtonLinearLayout - The "phone button" LinearLayout.
	 */
	public LinearLayout getSMSButtonLinearLayout() {
		if (_debug) Log.v("NotificationView.getSMSButtonLinearLayout()");
	    return _smsButtonLinearLayout;
	}

	/**
	 * Set the calendarButtonLinearLayout property.
	 * 
	 * @param calendarButtonLinearLayout - The "calendar button" LinearLayout.
	 */
	public void setCalendarButtonLinearLayout(LinearLayout calendarButtonLinearLayout) {
		if (_debug) Log.v("NotificationView.setCalendarButtonLinearLayout()");
	    _calendarButtonLinearLayout = calendarButtonLinearLayout;
	}
	
	/**
	 * Get the calendarButtonLinearLayout property.
	 * 
	 * @return calendarButtonLinearLayout - The "calendar button" LinearLayout.
	 */
	public LinearLayout getCalendarButtonLinearLayout() {
		if (_debug) Log.v("NotificationView.getCalendarButtonLinearLayout()");
	    return _calendarButtonLinearLayout;
	}

	/**
	 * Set the oldTouchValue property.
	 * 
	 * @param oldTouchValue - The touch value of a MotionEvent.
	 */
	public void setOldTouchValue(float oldTouchValue) {
		if (_debug) Log.v("NotificationView.setOldTouchValue()");
	    _oldTouchValue = oldTouchValue;
	}
	
	/**
	 * Get the oldTouchValue property.
	 * 
	 * @return oldTouchValue - The touch value of a MotionEvent.
	 */
	public float getOldTouchValue() {
		if (_debug) Log.v("NotificationView.getOldTouchValue()");
	    return _oldTouchValue;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
	
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String applicationThemeSetting = preferences.getString(APP_THEME_KEY, ANDROID_THEME);
		int themeResource = R.layout.android_theme_notification;
		if(applicationThemeSetting.equals(ANDROID_THEME)) themeResource = R.layout.android_theme_notification;
		if(applicationThemeSetting.equals(ANDROID_DARK_THEME)) themeResource = R.layout.android_dark_theme_notification;
		if(applicationThemeSetting.equals(IPHONE_THEME)) themeResource = R.layout.iphone_theme_notification;
		View.inflate(context, themeResource, this);
		setFromTextView((TextView) findViewById(R.id.contact_name_text_view));
		_phoneNumberTextView = (TextView) findViewById(R.id.contact_number_text_view);
		//Automatically format the phone number in this text view.
		_phoneNumberTextView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		setPhoneNumberTextView(_phoneNumberTextView);
		setReceivedAtTextView((TextView) findViewById(R.id.notification_info_text_view));
		setPhotoImageView((ImageView) findViewById(R.id.contact_photo_image_view));
	    setNotificationIconImageView((ImageView) findViewById(R.id.notification_type_icon_image_view));    
		_notificationTextView = (TextView) findViewById(R.id.notification_details_text_view);
		_notificationTextView.setMovementMethod(new ScrollingMovementMethod());
		_notificationTextView.setScrollbarFadingEnabled(false);
		setNotificationTextView(_notificationTextView);	
	    setPhoneButtonLinearLayout((LinearLayout) findViewById(R.id.phone_button_linear_layout));
	    setSMSButtonLinearLayout((LinearLayout) findViewById(R.id.sms_button_linear_layout));
		setCalendarButtonLinearLayout((LinearLayout) findViewById(R.id.calendar_button_linear_layout));
		setContactLinearLayout((LinearLayout) findViewById(R.id.contact_wrapper_linear_layout));	
		setNotificationViewFlipper(((NotificationActivity)getContext()).getNotificationViewFlipper());
	}

	/**
	 * Sets up the NotificationView's buttons.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setupNotificationViewButtons(Notification notification) {
		if (_debug) Log.v("NotificationView.setupNotificationViewButtons()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int notificationType = notification.getNotificationType();
		int phoneButtonLayoutVisibility = View.GONE;
		int smsButtonLayoutVisibility = View.GONE;
		int calendarButtonLayoutVisibility = View.GONE;
		LinearLayout phoneButtonLinearLayout = getPhoneButtonLinearLayout();
		LinearLayout smsButtonLinearLayout = getSMSButtonLinearLayout();
		LinearLayout calendarButtonLinearLayout = getCalendarButtonLinearLayout();
		// Previous Button
    	final Button previousButton = (Button) findViewById(R.id.previous_button);
		previousButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Previous Button Clicked()");
		    	NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	notificationViewFlipper.showPrevious();
		    }
		});
		// Next Button
		final Button nextButton = (Button) findViewById(R.id.next_button);
		nextButton.setOnClickListener(new OnClickListener() {
		    public void onClick(View view) {
		    	if (_debug) Log.v("Next Button Clicked()");
		    	NotificationViewFlipper notificationViewFlipper = getNotificationViewFlipper();
		    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    	notificationViewFlipper.showNext();
		    }
		});	
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.VISIBLE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button phoneDismissButton = (Button) findViewById(R.id.phone_dismiss_button);
			phoneDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (_debug) Log.v("Dismiss Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	dismissNotification();
			    }
			});
			// Call Button
			final Button phoneCallButton = (Button) findViewById(R.id.phone_call_button);
			phoneCallButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (_debug) Log.v("Call Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	makePhoneCall();
			    }
			});
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				phoneDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				phoneCallButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
	    }
		if(notificationType == NOTIFICATION_TYPE_SMS){
			//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.VISIBLE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button smsDismissButton = (Button) findViewById(R.id.sms_dismiss_button);
			smsDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("SMS Dismiss Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	dismissNotification();
			    }
			});		    			
			// Delete Button
			final Button smsDeleteButton = (Button) findViewById(R.id.sms_delete_button);
			smsDeleteButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("SMS Delete Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	showDeleteDialog();
			    }
			});
			// Reply Button
			final Button smsReplyButton = (Button) findViewById(R.id.sms_reply_button);
			smsReplyButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("SMS Reply Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	replyToMessage();
			    }
			});
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				smsDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				smsDeleteButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				smsReplyButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
		}
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
	    	//TODO - MMS
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.VISIBLE;
			// Dismiss Button
	    	final Button calendarDismissButton = (Button) findViewById(R.id.calendar_dismiss_button);
	    	calendarDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("Calendar Dismiss Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	dismissNotification();
			    }
			});			    			
			// View Button
	    	final Button calendarViewButton = (Button) findViewById(R.id.calendar_view_button);
			calendarViewButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (_debug) Log.v("Calendar View Button Clicked()");
			    	customPerformHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			    	viewCalendarEvent();
			    }
			});
			//Remove the icons from the View's buttons, based on the user preferences.
			if(!preferences.getBoolean(BUTTON_ICONS_KEY, true)){
				calendarDismissButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
				calendarViewButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			}
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
	    	//TODO - Email
	    }
		phoneButtonLinearLayout.setVisibility(phoneButtonLayoutVisibility);
    	smsButtonLinearLayout.setVisibility(smsButtonLayoutVisibility);
    	calendarButtonLinearLayout.setVisibility(calendarButtonLayoutVisibility);
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void populateNotificationViewInfo(Notification notification) {
		if (_debug) Log.v("NotificationView.populateNotificationViewInfo()");
		int notificationType = notification.getNotificationType();
	    // Set from, number, message etc. views.
		TextView fromTextView = getFromTextView();
		TextView phoneNumberTextView = getPhoneNumberTextView();
		if(notificationType == NOTIFICATION_TYPE_CALENDAR){
			String notificationTitle = notification.getTitle();
	    	if(notificationTitle.equals("")){
	    		notificationTitle = "No Title";
	    	}
			fromTextView.setText(notificationTitle);
			phoneNumberTextView.setVisibility(View.GONE);
			ImageView photoImageView = getPhotoImageView();
			photoImageView.setVisibility(View.GONE);
		}else{
			fromTextView.setText(notification.getContactName());
		    if(notification.getContactExists()){
		    	phoneNumberTextView.setText(notification.getAddressBookPhoneNumber());
		    }else{
		    	phoneNumberTextView.setText(notification.getPhoneNumber());
		    }
		}
		if(notificationType == NOTIFICATION_TYPE_PHONE){
			TextView notificationTextView = getNotificationTextView();
			notificationTextView.setVisibility(View.GONE);
		}
	    //Load the notification message.
	    setNotificationMessage(notification);
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo(notification);
	    //Load the image from the users contacts.
	    setNotificationImage(notification);
	    //Add context menu items.
	    setupContextMenus();
	}
	
	/**
	 * Set the notification message. 
	 * This is specific to the type of notification that was received.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationMessage(Notification notification){
		if (_debug) Log.v("NotificationView.setNotificationMessage()");
		int notificationType = notification.getNotificationType();
		String notificationText = "";
		TextView notificationTextView = getNotificationTextView();
		int notificationAlignment = Gravity.LEFT;
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	notificationText = "Missed Call!";
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	notificationText = notification.getMessageBody();
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	String notificationTitle = notification.getTitle();
	    	if(notificationTitle.equals("")){
	    		notificationTitle = "No Title";
	    	}
	    	notificationText = "<i>" + notification.getMessageBody() + "</i><br/>" + notificationTitle;
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	notificationText = notification.getTitle();
	    } 
	    notificationTextView.setText(Html.fromHtml(notificationText));
	    notificationTextView.setGravity(notificationAlignment);
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
		if (_debug) Log.v("NotificationView.setNotificationTypeInfo()");
		int notificationType = notification.getNotificationType();
		Context context = getContext();
		Bitmap iconBitmap = null;
		// Update TextView that contains the image, contact info/calendar info, and timestamp for the Notification.
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(notification.getTimeStamp());
	    String receivedAtText = "";
	    TextView receivedAtTextView = getReceivedAtTextView();
	    ImageView notificationIconImageView = getNotificationIconImageView();
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sym_call_missed);
	    	receivedAtText = context.getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sms);
	    	receivedAtText = context.getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.calendar);
	    	receivedAtText = context.getString(R.string.calendar_event_text);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.email);
	    	receivedAtText = context.getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
	    }    
	    if(iconBitmap != null){
	    	notificationIconImageView.setImageBitmap(iconBitmap);
	    }
	    receivedAtTextView.setText(receivedAtText);
	}
		
	/**
	 * Insert the image from the users contacts into the notification View.
	 * 
	 * @param notification - This View's Notification.
	 */
	private void setNotificationImage(Notification notification){
		if (_debug) Log.v("NotificationView.setNotificationImage()");
		ImageView photoImageView = getPhotoImageView();
	    //Load contact photo if it exists.
	    Bitmap bitmap = notification.getPhotoImg();
	    if(bitmap!=null){
	    	photoImageView.setImageBitmap((Bitmap)getRoundedCornerBitmap(notification.getPhotoImg(), 5));
	    }else{
	    	// Load the placeholder image if the contact has no photo.
	    	// This is based on user preferences from a list of predefined images.
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
	    	String contactPlaceholderImageID = preferences.getString(CONTACT_PLACEHOLDER_KEY, "0");
	    	//Default image resource.
	    	int contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_5;
	    	if(contactPlaceholderImageID.equals("0")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_1;
	    	if(contactPlaceholderImageID.equals("1")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_2;
	    	if(contactPlaceholderImageID.equals("2")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_3;
	    	if(contactPlaceholderImageID.equals("3")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_4;
	    	if(contactPlaceholderImageID.equals("4")) contactPlaceholderImageResourceID = R.drawable.ic_contact_picture_5;
	    	photoImageView.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(getContext().getResources(), contactPlaceholderImageResourceID), 5));
	    }
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
        Bitmap output = Bitmap.createBitmap(
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
	
	/**
	 * Remove the notification from the ViewFlipper.
	 */
	private void dismissNotification(){
		if (_debug) Log.v("NotificationView.dismissNotification()");
		getNotificationViewFlipper().removeActiveNotification();
	}
	
	/**
	 * Launches a new Activity.
	 * Replies to the current message using the stock Android messaging app.
	 */
	private void replyToMessage() {
		if (_debug) Log.v("NotificationView.replyToMessage()");
		Context context = getContext();
		Notification notification = getNotification();
		String phoneNumber = notification.getPhoneNumber();
		if(phoneNumber == null){
			return;
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		//Reply using Android's SMS Messaging app.
		if(preferences.getString(SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(SMS_ANDROID_REPLY)){
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setType("vnd.android-dir/mms-sms");
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
		        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
		        		| Intent.FLAG_ACTIVITY_NO_HISTORY
		        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    intent.putExtra("address", phoneNumber);
			    context.startActivity(intent);
			}catch(Exception ex){
				if (_debug) Log.e("NotificationView.replyToMessage() Android Reply ERROR: " + ex.toString());
			}
		}	
		//Reply using the built in Quick Reply Activity.
		if(preferences.getString(SMS_REPLY_BUTTON_ACTION_KEY, "0").equals(SMS_QUICK_REPLY)){
			try{
				Intent intent = new Intent(context, QuickReplyActivity.class);
				if (_debug) Log.v("NotificationView.replyToMessage() Put phone number in bundle");
		        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
		        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
			    intent.putExtra("smsPhoneNumber", phoneNumber);
			    intent.putExtra("smsMessage", "");
			    context.startActivity(intent);
			}catch(Exception ex){
				if (_debug) Log.e("NotificationView.replyToMessage() Quick Reply ERROR: " + ex.toString());
			}
		}
	}
	
	/**
	 * Launches a new Activity.
	 * Makes a phone call to the current missed call notification using the phones stock Android dialer & caller.
	 */
	private void makePhoneCall(){
		if (_debug) Log.v("NotificationView.makePhoneCall()");
		Context context = getContext();
		Notification notification = getNotification();
		String phoneNumber = notification.getPhoneNumber();
		if(phoneNumber == null){
			return;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNumber));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
	        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
	        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
	        		| Intent.FLAG_ACTIVITY_NO_HISTORY
	        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	        context.startActivity(intent);
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.makePhoneCall() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Launches a new Activity.
	 * Views the calendar event using the stock Android calendar app.
	 */
	private void viewCalendarEvent(){
		if (_debug) Log.v("NotificationView.viewCalendarEvent()");
		Context context = getContext();
		Notification notification = getNotification();
		long calendarEventID = notification.getCalendarEventID();
		if(calendarEventID == 0){
			return;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//Android 2.2+
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			//Android 2.1 and below.
			//intent.setData(Uri.parse("content://calendar/events/" + String.valueOf(calendarEventID)));
			intent.putExtra(EVENT_BEGIN_TIME,notification.getCalendarEventStartTime());
			intent.putExtra(EVENT_END_TIME,notification.getCalendarEventEndTime());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
	        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
	        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
	        		| Intent.FLAG_ACTIVITY_NO_HISTORY
	        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	        context.startActivity(intent);
		}catch(Exception ex){
			if (_debug) Log.e("NotificationView.viewCalendarEvent() ERROR: " + ex.toString());
		}
	}
 
	/**
	 * Confirm the delete request of the current message.
	 */
	private void showDeleteDialog(){
		if (_debug) Log.v("NotificationView.showDeleteDialog()");
		getNotificationViewFlipper().showDeleteDialog();
	}
	
	/**
	 * Setup the context menus for the various items on the notification window.
	 */
	private void setupContextMenus(){
		if (_debug) Log.v("NotificationActivity.setupContextMenus()"); 
		Context context = getContext();
		NotificationActivity notificationActivity = (NotificationActivity)context;
		LinearLayout contactLinearLayout = getContactLinearLayout();
		int notificationType = getNotificationType();
		if(notificationType == NOTIFICATION_TYPE_PHONE){
			notificationActivity.registerForContextMenu(contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS){
	    	notificationActivity.registerForContextMenu(contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_MMS){
	    	notificationActivity.registerForContextMenu(contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	notificationActivity.registerForContextMenu(contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//No menu at this time for Emails.
	    } 	
	}

	/**
	 * Performs haptic feedback based on the users preferences.
	 * 
	 * @param hapticFeedbackConstant - What type of action the feedback is responding to.
	 */
	private void customPerformHapticFeedback(int hapticFeedbackConstant){
		Context context = getContext();
		NotificationActivity notificationActivity = (NotificationActivity)context;
		Vibrator vibrator = (Vibrator)notificationActivity.getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		//Perform the haptic feedback based on the users preferences.
		if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.VIRTUAL_KEY){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(50);
			}
		}
		if(preferences.getBoolean(HAPTIC_FEEDBACK_ENABLED_KEY, true)){
			if(hapticFeedbackConstant == HapticFeedbackConstants.LONG_PRESS){
				//performHapticFeedback(hapticFeedbackConstant);
				vibrator.vibrate(100);
			}
		}
	}

//	/**
//	 * Add a calendar event.
//	 */
//	private void addCalendarEvent(){
//		if (_debug) Log.v("NotificationView.addCalendarEvent()");
//		Context context = getContext();
//		try{
//			//Android 2.2+
//			Intent intent = new Intent(Intent.ACTION_EDIT);
//			intent.setType("vnd.android.cursor.item/event");
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//	        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
//	        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
//	        		| Intent.FLAG_ACTIVITY_NO_HISTORY
//	        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//	        context.startActivity(intent);
//		}catch(Exception ex){
//			if (_debug) Log.e("NotificationView.addCalendarEvent() ERROR: " + ex.toString());
//		}
//	}

//	/**
//	 * Edit a calendar event.
//	 */
//	private void editCalendarEvent(){
//		if (_debug) Log.v("NotificationView.editCalendarEvent()");
//		Context context = getContext();
//	    Notification notification = getNotification();
//	    long calendarEventID = notification.getCalendarEventID();
//		try{
//			//Android 2.2+
//			Intent intent = new Intent(Intent.ACTION_EDIT);
//			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
//			intent.putExtra(EVENT_BEGIN_TIME,notification.getCalendarEventStartTime());
//			intent.putExtra(EVENT_END_TIME,notification.getCalendarEventEndTime());
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//	        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
//	        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
//	        		| Intent.FLAG_ACTIVITY_NO_HISTORY
//	        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//	        context.startActivity(intent);
//		}catch(Exception ex){
//			if (_debug) Log.e("NotificationView.editCalendarEvent() ERROR: " + ex.toString());
//		}
//	}
	
//	/**
//	 * Goto the messaging application inbox.
//	 */
//	private void gotoInbox() {
//		if (_debug) Log.v("NotificationVIew.gotoInbox()");
//		Context context = getContext();
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//	    intent.setType("vnd.android-dir/mms-sms");
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_SINGLE_TOP
//				| Intent.FLAG_ACTIVITY_CLEAR_TOP
//				| Intent.FLAG_ACTIVITY_NO_HISTORY
//				| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);	
//		context.startActivity(intent);
//	}
	
}
