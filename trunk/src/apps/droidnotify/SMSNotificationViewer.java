package apps.droidnotify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SMSNotificationViewer extends LinearLayout {

	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;	
	private TextView fromTV;
	private TextView messageReceivedTV;
	private TextView messageTV;

	private ScrollView messageScrollView = null;

	private Drawable contactPhotoPlaceholderDrawable = null;
//	private Bitmap contactPhoto = null;
//	private static int contactPhotoMargin = 3;
//	private static int contactPhotoDefaultMargin = 10;
//
//	private Uri contactLookupUri = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	public SMSNotificationViewer(Context context,  TextMessage message) {
	    super(context);
	    if (Log.DEBUG) Log.v("SMSNotificationViewer.SMSNotificationViewer().");
	    _context = context;
	    SetupLayout(context);
	    populateMessageViews(message);
	}

//	public SMSNotificationViewer(Context context, AttributeSet attrs) {
//	    super(context, attrs);
//	    _context = context;
//	    SetupLayout(context);
//	}

	//================================================================================
	// Accessors
	//================================================================================
	  
	//================================================================================
	// Public Methods
	//================================================================================
	  
	//================================================================================
	// Private Methods
	//================================================================================
	
	private void SetupLayout(Context context) {
		if (Log.DEBUG) Log.v("SMSNotificationViewer.SetupLayout().");
	    View.inflate(context, R.layout.smsmessage, this);
	    if (Log.DEBUG) Log.v("SMSNotificationViewer should be inflated now.");
	    // Find the main textviews and layouts
	    fromTV = (TextView) findViewById(R.id.from_text_view);
	    messageTV = (TextView) findViewById(R.id.message_text_view);
	    messageReceivedTV = (TextView) findViewById(R.id.header_text_view);
	    messageScrollView = (ScrollView) findViewById(R.id.message_scroll_view);
	    contactPhotoPlaceholderDrawable = getResources().getDrawable(android.R.drawable.ic_dialog_info);

	  }

	  /*
	   * Populate all the main SMS/MMS views with content from the actual SmsMmsMessage
	   */
	  private void populateMessageViews(TextMessage message) {
	    // Update TextView that contains the timestamp for the incoming message
	    String headerText = _context.getString(R.string.new_message_at_text, message.getTimeStamp());
	    // Set the from, message and header views
	    fromTV.setText(message.getContactName());
	    messageTV.setText(message.getMessageBody());
	    messageReceivedTV.setText(headerText);
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
