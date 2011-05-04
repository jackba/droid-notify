package apps.droidnotify;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
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
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * @author Camille Sevigny
 *
 */
public class NotificationView extends LinearLayout {

	//================================================================================
    // Constants
    //================================================================================
	
	private final int NOTIFICATION_TYPE_PHONE = 0;
	private final int NOTIFICATION_TYPE_SMS = 1;
	private final int NOTIFICATION_TYPE_MMS = 2;
	private final int NOTIFICATION_TYPE_CALENDAR = 3;
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	private final int CONTACT_PHOTO_IMAGE_VIEW = R.id.contact_image_view;
	private final int CONTACT_PHOTO_LINEAR_LAYOUT = R.id.contact_linear_layout;
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;	
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

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * 
     */	
	public NotificationView(Context context,  Notification notification) {
	    super(context);
	    if (Log.getDebug()) Log.v("NotificationView.NotificationView()");
	    setContext(context);
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
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("NotificationView.setContext()");
	    _context = context;
	}
	
	/**
	 * Set the notification property.
	 */
	public void setNotification(Notification notification) {
		if (Log.getDebug()) Log.v("Notification.seNotification()");
	    _notification = notification;
	}
	
	/**
	 * Get the notification property.
	 */
	public Notification getNotification() {
		if (Log.getDebug()) Log.v("Notification.getNotification()");
	    return _notification;
	}
	
	/**
	 * Set the notificationViewFlipper property.
	 */
	public void setNotificationViewFlipper(NotificationViewFlipper notificationViewFlipper) {
		if (Log.getDebug()) Log.v("Notification.seNotificationViewFlipper()");
	    _notificationViewFlipper = notificationViewFlipper;
	}
	
	/**
	 * Get the notificationViewFlipper property.
	 */
	public NotificationViewFlipper getNotificationViewFlipper() {
		if (Log.getDebug()) Log.v("Notification.getNotificationViewFlipper()");
	    return _notificationViewFlipper;
	}
	
	/**
	 * Set the notificationType property.
	 */
	public void setNotificationType(int notificationType) {
		if (Log.getDebug()) Log.v("Notification.seNotificationType()");
	    _notificationType = notificationType;
	}
	
	/**
	 * Get the notificationType property.
	 */
	public int getNotificationType() {
		if (Log.getDebug()) Log.v("Notification.getNotificationType()");
	    return _notificationType;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================

	/**
	 * Remove the notification from the ViewFlipper.
	 */
	public void deleteMessage(){
		if (Log.getDebug()) Log.v("NotificationView.deleteMessage()");
		//Remove SMS or MMS message from ViewFlipper.
		getNotificationViewFlipper().removeActiveNotification();
		//TODO - Delete SMS or MMS message from device.
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Initialize the layout items.
	 */
	private void initLayoutItems(Context context) {
		if (Log.getDebug()) Log.v("NotificationView.initLayoutItems()");
	    View.inflate(context, R.layout.notification, this);
	    if (Log.getDebug()) Log.v("NotificationView should be inflated now");
	    _fromTextView = (TextView) findViewById(R.id.from_text_view);
	    _phoneNumberTextView = (TextView) findViewById(R.id.phone_number_text_view);
	    //Automatically format the phone number input into this text view.
		_phoneNumberTextView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	    _notificationTextView = (TextView) findViewById(R.id.notification_text_view);
	    _receivedAtTextView = (TextView) findViewById(R.id.time_text_view);
	    _photoImageView = (ImageView) findViewById(R.id.contact_image_view);
	    _notificationIconImageView = (ImageView) findViewById(R.id.notification_type_icon_image_view);    
	    setNotificationViewFlipper(((NotificationActivity)getContext()).getNotificationViewFlipper());
	    _phoneButtonLinearLayout = (LinearLayout) findViewById(R.id.phone_button_layout);
		_smsButtonLinearLayout = (LinearLayout) findViewById(R.id.sms_button_layout);
		_calendarButtonLinearLayout = (LinearLayout) findViewById(R.id.calendar_button_layout);
		_notificationTextView.setMovementMethod(new ScrollingMovementMethod());
		_notificationTextView.setScrollbarFadingEnabled(false);
		_contactLinearLayout = (LinearLayout) findViewById(R.id.contact_linear_layout);
	}

	private void setupNotificationViewButtons(Notification notification) {
		int notificationType = notification.getNotificationType();
		int phoneButtonLayoutVisibility = View.GONE;
		int smsButtonLayoutVisibility = View.GONE;
		int calendarButtonLayoutVisibility = View.GONE;
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.VISIBLE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button phoneDismissButton = (Button) findViewById(R.id.phone_dismiss_button);		      
			phoneDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (Log.getDebug()) Log.v("Dismiss Button Clicked()");
			    	dismissNotification();
			    }
			});
			// Call Button
			final Button phoneCallButton = (Button) findViewById(R.id.phone_call_button);		      
			phoneCallButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	if (Log.getDebug()) Log.v("Call Button Clicked()");
			    	makePhoneCall();
			    }
			});
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
			    	if (Log.getDebug()) Log.v("SMS Dismiss Button Clicked()");
			    	dismissNotification();
			    }
			});		    			
			// Delete Button
			final Button smsDeleteButton = (Button) findViewById(R.id.sms_delete_button);
			smsDeleteButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("SMS Delete Button Clicked()");
			    	showDeleteDialog();
			    }
			});
			// Reply Button
			final Button smsReplyButton = (Button) findViewById(R.id.sms_reply_button);
			smsReplyButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("SMS Reply Button Clicked()");
			    	replyToMessage();
			    }
			});
		}
	    if(notificationType == NOTIFICATION_TYPE_MMS){
			//Display the correct navigation buttons for each notification type.
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.VISIBLE;
	    	calendarButtonLayoutVisibility = View.GONE;
			// Dismiss Button
	    	final Button mmsDismissButton = (Button) findViewById(R.id.sms_dismiss_button);		      
			mmsDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("MMS Dismiss Button Clicked()");
			    	dismissNotification();
			    }
			});			    			
			// Delete Button
			final Button mmsDeleteButton = (Button) findViewById(R.id.sms_delete_button);
			mmsDeleteButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("MMS Delete Button Clicked()");
			    	showDeleteDialog();
			    }
			});
			// Reply Button
			final Button mmsReplyButton = (Button) findViewById(R.id.sms_reply_button);
			mmsReplyButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("MMS Reply Button Clicked()");
			    	replyToMessage();
			    }
			});
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.VISIBLE;
			// Dismiss Button
	    	final Button calendarDismissButton = (Button) findViewById(R.id.sms_dismiss_button);		      
	    	calendarDismissButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("Calendar Dismiss Button Clicked()");
			    	//TODO - Calendar Reminder
			    	//dismissNotification();
			    }
			});			    			
			// View Button
			final Button calendarViewButton = (Button) findViewById(R.id.calendar_view_button);
			calendarViewButton.setOnClickListener(new OnClickListener() {
			    public void onClick(View view) {
			    	if (Log.getDebug()) Log.v("Calendar View Button Clicked()");
			    	//TODO - Calendar Reminder
			    	//replyToMessage();
			    }
			});
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	phoneButtonLayoutVisibility = View.GONE;
	    	smsButtonLayoutVisibility = View.GONE;
	    	calendarButtonLayoutVisibility = View.GONE;
	    	//TODO - Email Notification
	    }
		_phoneButtonLinearLayout.setVisibility(phoneButtonLayoutVisibility);
    	_smsButtonLinearLayout.setVisibility(smsButtonLayoutVisibility);
    	_calendarButtonLinearLayout.setVisibility(calendarButtonLayoutVisibility);
	}
	
	/**
	 * Populate the notification view with content from the actual Notification.
	 */
	private void populateNotificationViewInfo(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationView.populateNotificationViewInfo()");
	    // Set from, number, message etc. views.
	    _fromTextView.setText(notification.getContactName());
	    if(notification.getContactExists()){
	    	_phoneNumberTextView.setText(notification.getAddressBookPhoneNumber());
	    }else{
	    	_phoneNumberTextView.setText(notification.getPhoneNumber());
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
	 * @param notification
	 */
	private void setNotificationMessage(Notification notification){
		if (Log.getDebug()) Log.v("NotificationView.setNotificationMessage()");
		int notificationType = notification.getNotificationType();
		String notificationText = "";
		int notificationAlignment = Gravity.LEFT;
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	notificationText = "Missed Call!";
	    	notificationAlignment = Gravity.CENTER;
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	notificationText = notification.getMessageBody();
	    	notificationAlignment = Gravity.LEFT;
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//TODO - Insert Calendar Item
	    	notificationText = "TODO-Insert Calendar Item";
	    	notificationAlignment = Gravity.LEFT;
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//TODO - Insert Email Item
	    	notificationText = "TODO-Insert Email Item";
	    	notificationAlignment = Gravity.LEFT;
	    } 
	    _notificationTextView.setText(notificationText);
	    _notificationTextView.setGravity(notificationAlignment);
	}
	
	/**
	 * Set notification specific details into the header of the notification.
	 * This is specific to the type of notification that was received.
	 * Details include:
	 * 		Icon,
	 * 		Icon Text,
	 * 		Date & Time,
	 * 		Etc...
	 * 
	 * @param notification
	 */
	private void setNotificationTypeInfo(Notification notification){
		if (Log.getDebug()) Log.v("NotificationView.setNotificationTypeInfo()");
		int notificationType = notification.getNotificationType();
		Bitmap iconBitmap = null;
		// Update TextView that contains the timestamp for the incoming message
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(notification.getTimeStamp());
	    String receivedAtText = "";
	    if(notificationType == NOTIFICATION_TYPE_PHONE){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sym_call_missed);
	    	receivedAtText = getContext().getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sms);
	    	receivedAtText = getContext().getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.calendar);
	    	receivedAtText = getContext().getString(R.string.appointment_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.email);
	    	receivedAtText = getContext().getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
	    }    
	    if(iconBitmap != null){
	    	_notificationIconImageView.setImageBitmap(iconBitmap);
	    }
		_receivedAtTextView.setText(receivedAtText);
	}
	
	
	/**
	 * Insert the image from the users contacts into the notification View.
	 * 
	 * @param notification
	 */
	private void setNotificationImage(Notification notification){
		if (Log.getDebug()) Log.v("NotificationView.setNotificationImage()");
	    //Setup ImageView
	    _photoImageView.setBackgroundResource(0);
	    _photoImageView.setPadding(0, 0, 0, 0);
	    //_photoImageView.setBackgroundResource(android.R.drawable.picture_frame);
	    _photoImageView.setBackgroundResource(R.drawable.image_picture_frame);
	    //Load contact photo if it exists.
	    Bitmap bitmap = notification.getPhotoImg();
	    if(bitmap!=null){
	    	_photoImageView.setImageBitmap((Bitmap)getRoundedCornerBitmap(notification.getPhotoImg(), 5));    
	    }else{  
	    	// Load the placeholder image if the contact has no photo.
	    	_photoImageView.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_contact_picture), 5));
	    }
	    //_photoImageView.setFocusable(true);
	    //_photoImageView.setClickable(true);
	}
	
	/**
	 * Function that rounds the corners of a Bitmap image.
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return Bitmap image
	 */
	private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		if (Log.getDebug()) Log.v("NotificationView.getRoundedCornerBitmap()");
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
        return output;
	}
	
	/**
	 * Remove the notification from the ViewFlipper.
	 */
	private void dismissNotification(){
		if (Log.getDebug()) Log.v("NotificationView.dismissNotification()");
		getNotificationViewFlipper().removeActiveNotification();
	}
	
	/**
	 * Reply to the current message using the built in SMS app.
	 * This starts the built in SMS app Activity.
	 */
	private void replyToMessage() {
		if (Log.getDebug()) Log.v("NotificationView.replyToMessage()");
		Context context = getContext();
		Notification notification = getNotification();
		String phoneNumber = notification.getPhoneNumber();
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setType("vnd.android-dir/mms-sms");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
        		| Intent.FLAG_ACTIVITY_NO_HISTORY
        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    intent.putExtra("address", phoneNumber);
	    context.startActivity(intent);
	    //Not sure if this should be a preference to end the notification activity when a reply is made?
        //NotificationActivity notificationActivity = (NotificationActivity)context;
		//notificationActivity.finishActivity(); 
	}
	
	/**
	 * Make a phone call to the current missed call notification.
	 * This starts the phones built in dialer & caller.
	 */
	private void makePhoneCall(){
		if (Log.getDebug()) Log.v("NotificationView.makePhoneCall()");
		Context context = getContext();
		Notification notification = getNotification();
		String phoneNumber = notification.getPhoneNumber();
		Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        		| Intent.FLAG_ACTIVITY_SINGLE_TOP
        		| Intent.FLAG_ACTIVITY_CLEAR_TOP
        		| Intent.FLAG_ACTIVITY_NO_HISTORY
        		| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivity(intent);
        //Not sure if this should be a preference to end the notification activity when a call is made?
        //NotificationActivity notificationActivity = (NotificationActivity)context;
		//notificationActivity.finishActivity();
	}
 
	/**
	 * Confirm the delete request of this message.
	 */
	private void showDeleteDialog(){
		if (Log.getDebug()) Log.v("NotificationView.showDeleteDialog()");
		//Show pop up dialog to confirm deletion.
		getNotificationViewFlipper().showDeleteDialog();
	}
	
	/**
	 * Setup the context menus for the various items on the notification window.
	 */
	private void setupContextMenus(){
		if (Log.getDebug()) Log.v("NotificationActivity.setupContextMenus()"); 
		Context context = getContext();
		NotificationActivity notificationActivity = (NotificationActivity)context;
		int notificationType = getNotificationType();
		if(notificationType == NOTIFICATION_TYPE_PHONE){
			notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	notificationActivity.registerForContextMenu(_contactLinearLayout);
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	
	    } 	
	}
	
//	/**
//	 * Take the user to the messaging application inbox.
//	 */
//	private void gotoInbox() {
//		if (Log.getDebug()) Log.v("NotificationActivity.gotoInbox()");
//		Intent i = new Intent(Intent.ACTION_MAIN);
//	    i.setType("vnd.android-dir/mms-sms");
//	    int flags =
//	    	Intent.FLAG_ACTIVITY_NEW_TASK |
//	    	Intent.FLAG_ACTIVITY_SINGLE_TOP |
//	    	Intent.FLAG_ACTIVITY_CLEAR_TOP;
//	    i.setFlags(flags);	
//		NotificationActivity.this.getApplicationContext().startActivity(i);
//		finishActivity();
//	}
	
}
