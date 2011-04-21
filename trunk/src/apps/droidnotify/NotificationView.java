package apps.droidnotify;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
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
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;	
	private int _notificationType;
	private TextView _fromTV;
	private TextView _phoneNumberTV;
	private TextView _receivedAtTV;
	private TextView _messageTV;
	private ScrollView _messageScrollView = null;
	private ImageView _notificationIconIV = null;
	private ImageView _photoImageView = null;

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
	    setNotificationType(notification.getNotificationType());
	    initLayoutItems(context);
	    populateNotificationView(notification);
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
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
     * 
     */	
	private void initLayoutItems(Context context) {
		if (Log.getDebug()) Log.v("NotificationView.initLayoutItems()");
	    View.inflate(context, R.layout.notification, this);
	    if (Log.getDebug()) Log.v("NotificationView should be inflated now");
	    // Find the main textviews and layouts
	    _fromTV = (TextView) findViewById(R.id.from_text_view);
	    _phoneNumberTV = (TextView) findViewById(R.id.phone_number_text_view);
	    _messageTV = (TextView) findViewById(R.id.notification_text_view);
	    _receivedAtTV = (TextView) findViewById(R.id.time_text_view);
	    _messageScrollView = (ScrollView) findViewById(R.id.notification_text_scroll_view);
	    _photoImageView = (ImageView) findViewById(R.id.from_image_view);
	    _notificationIconIV = (ImageView) findViewById(R.id.notification_type_icon_image_view);
	}

	/**
	 * Populate the notification view with content from the actual Notification.
	 */
	private void populateNotificationView(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationView.populateNotificationView()");
	    // Set from, number, message etc. views.
	    _fromTV.setText(notification.getContactName());
	    _phoneNumberTV.setText("(" + notification.getPhoneNumber() + ")");
	    //Load the notification message.
	    setNotificationMessage(notification);
	    //Load the notification type icon & text into the notification.
	    setNotificationTypeInfo(notification);
	    //Load the image from the users contacts.
	    setNotificationImage(notification);
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
	    _messageTV.setText(notificationText);
	    _messageTV.setGravity(notificationAlignment);
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
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.missed_call_icon);
	    	receivedAtText = getContext().getString(R.string.missed_call_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sms_icon);
	    	receivedAtText = getContext().getString(R.string.message_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.calendar_icon);
	    	receivedAtText = getContext().getString(R.string.appointment_at_text, formattedTimestamp.toLowerCase());
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	iconBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.email_icon);
	    	receivedAtText = getContext().getString(R.string.email_at_text, formattedTimestamp.toLowerCase());
	    }    
	    if(iconBitmap != null){
	    	_notificationIconIV.setImageBitmap(iconBitmap);
	    }
		_receivedAtTV.setText(receivedAtText);
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
	    	_photoImageView.setImageBitmap(getRoundedCornerBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.android_contact_placeholder), 5));
	    }
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
	
}
