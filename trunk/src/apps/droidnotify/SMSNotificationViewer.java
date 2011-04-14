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
public class SMSNotificationViewer extends LinearLayout {

	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;	
	private TextView fromTV;
	private TextView messageReceivedTV;
	private TextView messageTV;
	private ScrollView messageScrollView = null;
	private ImageView photoImageView = null;
	private Drawable contactPhotoPlaceholderDrawable = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	/**
     * 
     */	
	public SMSNotificationViewer(Context context,  TextMessage message) {
	    super(context);
	    if (Log.DEBUG) Log.v("SMSNotificationViewer.SMSNotificationViewer()");
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
		if (Log.DEBUG) Log.v("SMSNotificationViewer.SetupLayout()");
	    View.inflate(context, R.layout.smsmessage, this);
	    if (Log.DEBUG) Log.v("SMSNotificationViewer should be inflated now");
	    // Find the main textviews and layouts
	    fromTV = (TextView) findViewById(R.id.from_text_view);
	    messageTV = (TextView) findViewById(R.id.message_text_view);
	    messageReceivedTV = (TextView) findViewById(R.id.header_text_view);
	    messageScrollView = (ScrollView) findViewById(R.id.message_scroll_view);
	    photoImageView = (ImageView) findViewById(R.id.from_image_view);
	    contactPhotoPlaceholderDrawable = getResources().getDrawable(android.R.drawable.ic_dialog_info);

	
	
	}

	/**
	 * Populate all the main SMS/MMS views with content from the actual SmsMmsMessage
	 */
	private void populateMessageView(TextMessage message) {
		// Update TextView that contains the timestamp for the incoming message
		String formattedTimestamp = new SimpleDateFormat("h:mma").format(message.getTimeStamp());
	    String headerText = _context.getString(R.string.new_message_at_text, formattedTimestamp.toLowerCase());
	    // Set the from, message and header views
	    fromTV.setText(message.getContactName());
	    messageTV.setText(message.getMessageBody());
	    messageReceivedTV.setText(headerText);
	    // Set placeholder image
	    photoImageView.setImageDrawable(contactPhotoPlaceholderDrawable);
	    
		    
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
