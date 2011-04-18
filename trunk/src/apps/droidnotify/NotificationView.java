package apps.droidnotify;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Bitmap;
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
	public NotificationView(Context context,  Notification message) {
	    super(context);
	    if (Log.getDebug()) Log.v("SMSNotificationViewer.SMSNotificationViewer()");
	    _context = context;
	    initLayoutItems(context);
	    populateMessageView(message);
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
		if (Log.getDebug()) Log.v("SMSNotificationViewer.SetupLayout()");
	    View.inflate(context, R.layout.notification, this);
	    if (Log.getDebug()) Log.v("SMSNotificationViewer should be inflated now");
	    // Find the main textviews and layouts
	    _fromTV = (TextView) findViewById(R.id.from_text_view);
	    _messageTV = (TextView) findViewById(R.id.message_text_view);
	    _messageReceivedTV = (TextView) findViewById(R.id.header_text_view);
	    _messageScrollView = (ScrollView) findViewById(R.id.message_scroll_view);
	    _photoImageView = (ImageView) findViewById(R.id.from_image_view);
	    _contactPhotoPlaceholderDrawable = getResources().getDrawable(android.R.drawable.ic_dialog_info);
	}

	/**
	 * Populate all the main SMS/MMS views with content from the actual SmsMmsMessage
	 */
	private void populateMessageView(Notification message) {
		// Update TextView that contains the timestamp for the incoming message
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(message.getTimeStamp());
	    String headerText = _context.getString(R.string.new_message_at_text, formattedTimestamp.toLowerCase());
	    // Set the from, message and header views
	    _fromTV.setText(message.getContactName());
	    _messageTV.setText(message.getMessageBody());
	    _messageReceivedTV.setText(headerText);
	    // Set placeholder image
	    _photoImageView.setImageDrawable(_contactPhotoPlaceholderDrawable);    
	}

//	  private void loadContactPhoto() {
//	    // Fetch contact photo in background
////	    if (contactPhoto == null) {
////	      SetContactPhotoToDefault(photoImageView);
////	      new FetchContactPhotoTask().execute(message.getContactId());
////	      addQuickContactOnClick();
////	    }
//	  }

}
