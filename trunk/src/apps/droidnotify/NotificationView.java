package apps.droidnotify;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
    // Properties
    //================================================================================
	
	private Context _context;	
	private TextView _fromTV;
	private TextView _messageReceivedTV;
	private TextView _messageTV;
	private ScrollView _messageScrollView = null;
	private ImageView _photoImageView = null;
	private Drawable _contactPhotoPlaceholderDrawable = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * 
     */	
	public NotificationView(Context context,  Notification notification) {
	    super(context);
	    if (Log.getDebug()) Log.v("NotificationView.NotificationView()");
	    _context = context;
	    initLayoutItems(context);
	    populateNotificationView(notification);
	}

	//================================================================================
	// Accessors
	//================================================================================
	  
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
	 * Populate all the notification views with content from the actual Notification.
	 */
	private void populateNotificationView(Notification notification) {
		// Update TextView that contains the timestamp for the incoming message
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(notification.getTimeStamp());
	    String headerText = _context.getString(R.string.new_message_at_text, formattedTimestamp.toLowerCase());
	    // Set the from, message and header views
	    _fromTV.setText(notification.getContactName());
	    _messageTV.setText(notification.getMessageBody());
	    _messageReceivedTV.setText(headerText);
	    //Load contact photo if it exists.
	    Bitmap bitmap = notification.getPhotoImg();
	    if(bitmap!=null){
	    	_photoImageView.setImageBitmap(notification.getPhotoImg());    
	    }else{  
	    	// Load the placeholder image if the contact has no photo.
	    	_photoImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.android_contact_placeholder));
	    }
	}

}
