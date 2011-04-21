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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
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
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;	
	private int _notificationType;
	private TextView _fromTV;
	private TextView _messageReceivedTV;
	private TextView _messageTV;
	private ScrollView _messageScrollView = null;
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
		if (Log.getDebug()) Log.v("NotificationView.SetupLayout()");
	    View.inflate(context, R.layout.notification, this);
	    if (Log.getDebug()) Log.v("NotificationView should be inflated now");
	    // Find the main textviews and layouts
	    _fromTV = (TextView) findViewById(R.id.from_text_view);
	    _messageTV = (TextView) findViewById(R.id.message_text_view);
	    _messageReceivedTV = (TextView) findViewById(R.id.header_text_view);
	    _messageScrollView = (ScrollView) findViewById(R.id.message_scroll_view);
	    _photoImageView = (ImageView) findViewById(R.id.from_image_view);
	}

	/**
	 * Populate the notification view with content from the actual Notification.
	 */
	private void populateNotificationView(Notification notification) {
		if (Log.getDebug()) Log.v("NotificationView.populateNotificationView()");
		// Update TextView that contains the timestamp for the incoming message
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(notification.getTimeStamp());
	    String headerText = _context.getString(R.string.new_message_at_text, formattedTimestamp.toLowerCase());
	    // Set the from, message and header views
	    _fromTV.setText(notification.getContactName());
	    _messageTV.setText(notification.getMessageBody());
	    _messageReceivedTV.setText(headerText);
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
