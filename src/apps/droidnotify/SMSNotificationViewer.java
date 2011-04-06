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
	private TextView mmsSubjectTV = null;
	private ScrollView messageScrollView = null;
	private ImageView photoImageView = null;
	private Drawable contactPhotoPlaceholderDrawable = null;
	private Bitmap contactPhoto = null;
	private static int contactPhotoMargin = 3;
	private static int contactPhotoDefaultMargin = 10;
	private View mmsLayout = null;
	private View privacyLayout = null;
	private Uri contactLookupUri = null;

	//================================================================================
	// Constructors
	//================================================================================
	
	public SMSNotificationViewer(Context context,  TextMessage message) {
	    super(context);
	    _context = context;
	    SetupLayout(context);
	    //SetSmsMmsMessage(message);
	    return;
	}

	public SMSNotificationViewer(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    _context = context;
	    SetupLayout(context);
	    return;
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
	
	private void SetupLayout(Context context) {
	    View.inflate(context, R.layout.smsmessage, this);

	    // Find the main textviews and layouts
	    fromTV = (TextView) findViewById(R.id.from_text_view);
	    messageTV = (TextView) findViewById(R.id.message_text_view);
	    messageReceivedTV = (TextView) findViewById(R.id.header_text_view);
	    messageScrollView = (ScrollView) findViewById(R.id.message_scroll_view);
	    mmsLayout = findViewById(R.id.mms_linear_layout);
	    privacyLayout = findViewById(R.id.view_button_linear_layout);
	    mmsSubjectTV = (TextView) findViewById(R.id.mms_subject_text_view);

	    // Find the ImageView that will show the contact photo
	    photoImageView = (ImageView) findViewById(R.id.from_image_view);
	    contactPhotoPlaceholderDrawable = getResources().getDrawable(android.R.drawable.ic_dialog_info);

	    // The ViewMMS button
	    Button viewMmsButton = (Button) mmsLayout.findViewById(R.id.view_mms_button);
	    viewMmsButton.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {
	        //mOnReactToMessage.onReplyToMessage();
	      }
	    });

	    // The view button (if in privacy mode)
	    Button viewButton = (Button) privacyLayout.findViewById(R.id.view_button);
	    viewButton.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) {
	        //mOnReactToMessage.onViewMessage();
	      }
	    });
	  }

	  /*
	   * Populate all the main SMS/MMS views with content from the actual SmsMmsMessage
	   */
	  private void populateViews(TextMessage message) {

	    // If it's a MMS message, just show the MMS layout
//	    if (message.getMessageType() == SmsMmsMessage.MESSAGE_TYPE_MMS) {
//	      messageScrollView.setVisibility(View.GONE);
//	      mmsLayout.setVisibility(View.VISIBLE);
//	      // If no MMS subject, hide the subject text view
//	      if (TextUtils.isEmpty(message.getMessageBody())) {
//	        mmsSubjectTV.setVisibility(View.GONE);
//	      } else {
//	        mmsSubjectTV.setVisibility(View.VISIBLE);
//	      }
//	    } else {
//	      // Otherwise hide MMS layout
//	      mmsLayout.setVisibility(View.GONE);
//	    }

	    // Update TextView that contains the timestamp for the incoming message
//	    String headerText = _context.getString(R.string.new_text_at, message.getFormattedTimestamp());
//
//	    // Set the from, message and header views
//	    fromTV.setText(message.getContactName());
//	    if (message.getMessageType() == TextMessage.MESSAGE_TYPE_SMS) {
//	    	messageTV.setText(message.getMessageBody());
//	    } else {
//	    	mmsSubjectTV.setText(_context.getString(R.string.mms_subject) + " " + message.getMessageBody());
//	    }
//	    messageReceivedTV.setText(headerText);
	  }

	  private void loadContactPhoto() {
	    // Fetch contact photo in background
//	    if (contactPhoto == null) {
//	      SetContactPhotoToDefault(photoImageView);
//	      new FetchContactPhotoTask().execute(message.getContactId());
//	      addQuickContactOnClick();
//	    }
	  }

}
