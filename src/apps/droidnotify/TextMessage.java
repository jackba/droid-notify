package apps.droidnotify;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

/**
 * 
 */
public class TextMessage {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private String _fromaddress;
	private String _messagebody;
	private long _timestamp;
	private long _threadid;
	private long _contactid;
	private String _contactlookupkey;
	private String _contactname;
	private int _messagetype;
	private long _messageid;
	private boolean _fromemailgateway;
	private MessageClass _messageclass;
  
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * Construct SmsMmsMessage given a raw message (created from pdu), used for when
	 * a message is initially received via the network.
	 */
	public TextMessage(Context context, Bundle b) {
		if (Log.DEBUG) Log.v("TextMessage.TextMessage() 0");
		_context = context;
		SmsMessage[] msgs = null;
        String messagebody = "";            
        if (b != null){
        	if (Log.DEBUG) Log.v("TextMessage.TextMessage() Bundle is not null");
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) b.get("pdus");
            if (Log.DEBUG) Log.v("TextMessage.TextMessage() Get pdus");
            msgs = new SmsMessage[pdus.length]; 
            if (Log.DEBUG) Log.v("TextMessage.TextMessage() Created the msgs array");
            for (int i=0; i<msgs.length; i++){
            	if (Log.DEBUG) Log.v("TextMessage.TextMessage() Insside for loop");
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
            }
            if (Log.DEBUG) Log.v("TextMessage.TextMessage() Populated the msgs array");
            SmsMessage sms = msgs[0];
            _timestamp = new java.util.Date().getTime();
    		_fromaddress = sms.getDisplayOriginatingAddress();
    		_fromemailgateway = sms.isEmail();
    		_messageclass = sms.getMessageClass();
    		_messagetype = 0;
            //Get the entire message body from the new message
            for (int i=0; i<msgs.length; i++){                
                messagebody += msgs[i].getMessageBody().toString();
            }
            _messagebody = messagebody;
            if (Log.DEBUG) Log.v("TextMessage.TextMessage() Set the message body");
    		getThreadIdByAddress(_context, _fromaddress);  
        }
		if (Log.DEBUG) Log.v("TextMessage.TextMessage() Exiting");
	}

	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.DEBUG) Log.v("TextMessage.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.DEBUG) Log.v("TextMessage.getContext()");
	    return _context;
	}

	/**
	 * Set the fromaddress property.
	 */
	public void getFromAddress(String fromaddress) {
		if (Log.DEBUG) Log.v("TextMessage.setFromAddress()");
		_fromaddress = fromaddress;
	}
	
	/**
	 * Get the fromaddress property.
	 */
	public String getFromAddress() {
		if (Log.DEBUG) Log.v("TextMessage.getFromAddress()");
		return _fromaddress;
	}
	
	/**
	 * Set the messagebody property.
	 */
	public void setMessageBody(String messagebody) {
		if (Log.DEBUG) Log.v("TextMessage.setMessageBody()");
		_messagebody = messagebody;
	}
	
	/**
	 * Get the messagebody property.
	 */
	public String getMessageBody() {
		if (Log.DEBUG) Log.v("TextMessage.getMessageBody()");
		if (_messagebody == null) {
			_messagebody = "";
	    }
	    return _messagebody;
	}

	/**
	 * Set the timestamp property.
	 */
	public void setTimeStamp(long timestamp) {
		if (Log.DEBUG) Log.v("TextMessage.setTimeStamp()");
	    _timestamp = timestamp;
	}
	
	/**
	 * Get the timestamp property.
	 */
	public long getTimeStamp() {
		if (Log.DEBUG) Log.v("TextMessage.getTimeStamp()");
	    return _timestamp;
	}
	
	/**
	 * Set the threadid property.
	 */
	public void setThreadID(long threadid) {
		if (Log.DEBUG) Log.v("TextMessage.setThreadID()");
	    _threadid = threadid;
	}
	
	/**
	 * Get the threadid property.
	 */
	public long getThreadID() {
		if (Log.DEBUG) Log.v("TextMessage.getThreadID()");
	    return _threadid;
	}	
	
	/**
	 * Set the contactid property.
	 */
	public void setContactID(long contactid) {
		if (Log.DEBUG) Log.v("TextMessage.setContactID()");
	    _contactid = contactid;
	}
	
	/**
	 * Get the contactid property.
	 */
	public long getContactID() {
		if (Log.DEBUG) Log.v("TextMessage.getContactID()");
	    return _contactid;
	}
	
	/**
	 * Set the contactlookupkey property.
	 */
	public void setContactLookupKey(String contactlookupkey) {
		if (Log.DEBUG) Log.v("TextMessage.setContactLookupKey()");
		_contactlookupkey = contactlookupkey;
	}
	
	/**
	 * Get the contactlookupkey property.
	 */
	public String getContactLookupKey() {
		if (Log.DEBUG) Log.v("TextMessage.getContactLookupKey()");
	    return _contactlookupkey;
	}	

	/**
	 * Set the contactname property.
	 */
	public void setContactName(String contactname) {
		if (Log.DEBUG) Log.v("TextMessage.setContactName()");
		_contactname = contactname;
	}
	
	/**
	 * Get the contactname property.
	 */
	public String getContactName() {
		if (Log.DEBUG) Log.v("TextMessage.getContactName()");
		if (_contactname == null) {
			_contactname = _context.getString(android.R.string.unknownName);
	    }
		return _contactname;
	}
	
	/**
	 * Get the messageclass property.
	 */
	public MessageClass getMessageClass() {
		if (Log.DEBUG) Log.v("TextMessage.getMessageClass()");
		return _messageclass;
	}

	/**
	 * Set the messagetype property.
	 */
	public void getMessageType(int messagetype) {
		if (Log.DEBUG) Log.v("TextMessage.setMessageType()");
		_messagetype = messagetype;
	}
	
	/**
	 * Get the messagetype property.
	 */
	public int getMessageType() {
		if (Log.DEBUG) Log.v("TextMessage.getMessageType()");
		return _messagetype;
	}

	/**
	 * Set the messageid property.
	 */
	public void setMessageId(long messageid) {
		if (Log.DEBUG) Log.v("TextMessage.setMessageId()");
  		_messageid = messageid;
	}
	
	/**
	 * Get the messageid property.
	 */
	public long getMessageId() {
		if (Log.DEBUG) Log.v("TextMessage.getMessageId()");
  		return _messageid;
	}

	/**
	 * Set the fromemailgateway property.
	 */
	public void setFromEmailGateway(boolean fromemailgateway) {
		if (Log.DEBUG) Log.v("TextMessage.setFromEmailGateway()");
  		_fromemailgateway = fromemailgateway;
	}
	
	/**
	 * Get the fromemailgateway property.
	 */
	public boolean setFromEmailGateway() {
		if (Log.DEBUG) Log.v("TextMessage.getFromEmailGateway()");
  		return _fromemailgateway;
	}	

	/**
	 * Set the messageclass property.
	 */
	public void setMessageClass(MessageClass messageclass) {
		if (Log.DEBUG) Log.v("TextMessage.setMessageClass()");
		_messageclass = messageclass;
	}
	
	/**
	 * Get the messageclass property.
	 */
	public MessageClass setMessageClass() {
		if (Log.DEBUG) Log.v("TextMessage.getMessageClass()");
  		return _messageclass;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
 
	/**
	 * 
	 */ 
	public Intent getReplyIntent() {
		if (Log.DEBUG) Log.v("TextMessage.getReplyIntent()");
		if (getThreadID() == 0) {
			setThreadID(getThreadIdByAddress(getContext(), getFromAddress()));
		}	
		//Reply to SMS "thread_id"
		if (Log.DEBUG) Log.v("TextMessage.getReplyIntent() Replying by threadId: " + getThreadID());
		return getSMSReplyIntent(getContext(), getThreadID());
	}
	 
//	/**
//	 * 
//	 */ 
//  public void delete() {
//    SmsPopupUtils.deleteMessage(context, getMessageId(), threadId, messageType);
//  }
	 
//	/**
//	 * 
//	 */ 
//  public void locateMessageId() {
//    if (messageId == 0) {
//      if (threadId == 0) {
//        locateThreadId();
//      }
//      messageId =
//        SmsPopupUtils.findMessageId(context, threadId, timestamp, messageBody, messageType);
//    }
//  }

//  /**
//   * Sned a reply to this message
//   *
//   * @param quickreply the message to send
//   * @return true of the message was sent, false otherwise
//   */
//  public boolean replyToMessage(String quickReply) {
//
//    // Mark the message we're replying to as read
//    setMessageRead();
//
//    // Send new message
//    SmsMessageSender sender =
//      new SmsMessageSender(context, new String[] {fromAddress}, quickReply, getThreadId());
//
//    return sender.sendMessage();
//  }
  
	//================================================================================
	// Private Methods
	//================================================================================

	/**
	 * Returns the thread_id of the SMS message that corresponds to the address provided.
	 */
	private long getThreadIdByAddress(Context context, String address){
		if (Log.DEBUG) Log.v("TextMessage.getThreadIdByAddress()");
		if (address == null) return 0;
		final String[] projection = new String[] { "_id", "thread_id" };
		final String selection = "address=" + address;
		final String[] selectionArgs = null;
		final String sortOrder = null;
	    long threadID = 0;
	    Cursor cursor = context.getContentResolver().query(
	    		Uri.withAppendedPath(Uri.parse("content://sms"), "inbox"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	try {
	    		if (cursor.moveToFirst()) {
	    			threadID = cursor.getLong(1);
	    			if (Log.DEBUG) Log.v("TextMessage.getThreadIdByAddress() Thread_ID Found: " + threadID);
	    		}
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    return threadID;
	}

	/**
	 * Get system view sms thread Intent
	 *
	 * @param context context
	 * @param threadId the message thread id to view
	 * @return the intent that can be started with startActivity()
	 */
	public Intent getSMSReplyIntent(Context context, long threadID) {
		if (Log.DEBUG) Log.v("TextMessage.getSMSReplyIntent()");
	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setType("vnd.android-dir/mms-sms");
	    int flags =
	    	Intent.FLAG_ACTIVITY_NEW_TASK |
	    	Intent.FLAG_ACTIVITY_SINGLE_TOP |
	    	Intent.FLAG_ACTIVITY_CLEAR_TOP;
	    intent.setFlags(flags);
	    intent.putExtra("address", getFromAddress());
	    return intent;
	}
	
//  /**
//   * Looks up a contacts id, given their email address.
//   * Returns null if not found
//   */
//  private int getPersonIdFromEmail(Context context, String email) {
//    if (email == null) return null;
//
//    Cursor cursor = null;
//    try {
//      cursor = context.getContentResolver().query(
//          Uri.withAppendedPath(
//              ContactWrapper.getEmailLookupContentFilterUri(),
//              Uri.encode(extractAddrSpec(email))),
//              ContactWrapper.getEmailLookupProjection(),
//              null, null, null);
//    } catch (Exception e) {
//      Log.v("getPersonIdFromEmail(): " + e.toString());
//      return null;
//    }
//
//    if (cursor != null) {
//      try {
//        if (cursor.moveToFirst()) {
//
//          String contactId = String.valueOf(cursor.getlong(0));
//          String contactName = cursor.getString(1);
//          String contactLookup = null;
//
//          if (!PRE_ECLAIR) {
//            contactLookup = cursor.getString(2);
//          }
//
//          if (Log.DEBUG) Log.v("Found person: " + contactId + ", " + contactName + ", " + contactLookup);
//          return new ContactIdentification(contactId, contactLookup, contactName);
//        }
//      } finally {
//        cursor.close();
//      }
//    }
//    return null;
//  }

//  /**
//   *
//   * Looks up a contats photo by their contact id, returns a Bitmap array
//   * that represents their photo (or null if not found or there was an error.
//   *
//   * I do my own scaling and validation of sizes - Android OS supports any size
//   * for contact photos and some apps are adding huge photos to contacts.  Doing
//   * the scaling myself allows me more control over how things play out in those
//   * cases.
//   *
//   * @param context
//   * @param id contact id
//   * @return Bitmap of the contacts photo (null if none or an error)
//   */
//  public static Bitmap getPersonPhoto(Context context, String id) {
//
//    if (id == null) return null;
//    if ("0".equals(id)) return null;
//
//    // First let's just check the dimensions of the contact photo
//    BitmapFactory.Options options = new BitmapFactory.Options();
//    options.inJustDecodeBounds = true;
//
//    // The height and width are stored in 'options' but the photo itself is not loaded
//    //    Contacts.People.loadContactPhoto(
//    //        context, Uri.withAppendedPath(Contacts.People.CONTENT_URI, id), 0, options);
//    loadContactPhoto(context, id, 0, options);
//
//    // Raw height and width of contact photo
//    int height = options.outHeight;
//    int width = options.outWidth;
//
//    if (Log.DEBUG) Log.v("Contact photo size = " + height + "x" + width);
//
//    // If photo is too large or not found get out
//    if (height > CONTACT_PHOTO_MAXSIZE || width > CONTACT_PHOTO_MAXSIZE  ||
//        width == 0 || height == 0) return null;
//
//    // This time we're going to do it for real
//    options.inJustDecodeBounds = false;
//
//    // Calculate new thumbnail size based on screen density
//    final float scale = context.getResources().getDisplayMetrics().density;
//    int thumbsize = CONTACT_PHOTO_THUMBSIZE;
//    if (scale != 1.0) {
//      if (Log.DEBUG) Log.v("Screen density is not 1.0, adjusting contact photo");
//      thumbsize = Math.round(thumbsize * scale);
//    }
//
//    int newHeight = thumbsize;
//    int newWidth = thumbsize;
//
//    // If we have an abnormal photo size that's larger than thumbsize then sample it down
//    boolean sampleDown = false;
//
//    if (height > thumbsize || width > thumbsize) {
//      sampleDown = true;
//    }
//
//    // If the dimensions are not the same then calculate new scaled dimenions
//    if (height < width) {
//      if (sampleDown) {
//        options.inSampleSize = Math.round(height / thumbsize);
//      }
//      newHeight = Math.round(thumbsize * height / width);
//    } else {
//      if (sampleDown) {
//        options.inSampleSize = Math.round(width / thumbsize);
//      }
//      newWidth = Math.round(thumbsize * width / height);
//    }
//
//    // Fetch the real contact photo (sampled down if needed)
//    Bitmap contactBitmap = null;
//    try {
//      //contactBitmap = Contacts.People.loadContactPhoto(
//      //    context, Uri.withAppendedPath(Contacts.People.CONTENT_URI, id), 0, options);
//      //contactBitmap = loadContactPhoto(context, id, 0, options);
//    } catch (OutOfMemoryError e) {
//      Log.e("Out of memory when loading contact photo");
//    }
//
//    // Not found or error, get out
//    if (contactBitmap == null) return null;
//
//    // Return bitmap scaled to new height and width
//    return Bitmap.createScaledBitmap(contactBitmap, newWidth, newHeight, true);
//  }

//  /**
//   * Opens an InputStream for the person's photo and returns the photo as a Bitmap.
//   * If the person's photo isn't present returns the placeholderImageResource instead.
//   * @param context the Context
//   * @param id the id of the person
//   * @param placeholderImageResource the image resource to use if the person doesn't
//   *   have a photo
//   * @param options the decoding options, can be set to null
//   */
//  public static Bitmap loadContactPhoto(Context context, String id,
//      int placeholderImageResource, BitmapFactory.Options options) {
//    if (id == null) {
//      return loadPlaceholderPhoto(placeholderImageResource, context, options);
//    }
//
////    InputStream stream = ContactWrapper.openContactPhotoInputStream(context.getContentResolver(), id);
//
//    Bitmap bm = stream != null ? BitmapFactory.decodeStream(stream, null, options) : null;
//    if (bm == null) {
//      bm = loadPlaceholderPhoto(placeholderImageResource, context, options);
//    }
//    return bm;
//  }

//	/**
//	 * 
//	 */
//	private static Bitmap loadPlaceholderPhoto(int placeholderImageResource, Context context, BitmapFactory.Options options) {
//		if (Log.DEBUG) Log.v("TextMessage.loadPlaceholderPhoto()");
//		if (placeholderImageResource == 0) {
//			return null;
//		}
//		return BitmapFactory.decodeResource(context.getResources(), placeholderImageResource, options);
//	}
  
//	/**
//     * Return current unread message count from system db (sms only)
//     *
//     * @param context
//     * @param timestamp only messages before this timestamp will be counted
//     * @return unread sms message count
//     */
//	private int getUnreadMessagesCount(Context context, long timestamp, String messagebody) {
//		if (Log.DEBUG) Log.v("TextMessage.getUnreadMessagesCount()");
//		final String[] projection = new String[] { "_id", "body" };
//		final String selection = "read=0";
//		final String[] selectionArgs = null;
//		final String sortOrder = "date DESC";
//		int count = 0;
//		Cursor cursor = context.getContentResolver().query(
//				Uri.withAppendedPath(Uri.parse("content://sms"), "inbox"),
//				projection,
//				selection,
//				selectionArgs,
//				sortOrder);
//		if (cursor != null) {
//			try {
//				count = cursor.getCount();
//		        /*
//		         * We need to check if the message received matches the most recent one in the db
//		         * or not (to find out if our code ran before the system code or vice-versa)
//		         */
//		        if (messagebody != null && count > 0) {
//        		if (cursor.moveToFirst()) {
//	        		/*
//	        		 * Check the most recent message, if the body does not match then it hasn't yet
//	        		 * been inserted into the system database, therefore we need to add one to our
//	        		 * total count
//	        		 */
//        			if (!messagebody.equals(cursor.getString(1))) {
//        				if (Log.DEBUG) Log.v("TextMessage.GetUnreadMessagesCount(): Most recent message did not match body, adding 1 to count.");
//        				count++;
//        			}
//        		}
//        	}
//			} finally {
//	    	  cursor.close();
//	      	}
//		}
//	    /*
//	     * If count is still 0 and timestamp is set then its likely the system db had not updated
//	     * when this code ran, therefore let's add 1 so the notify will run correctly.
//	     */
//	    if (count == 0 && timestamp > 0) {
//	    	count = 1;
//	    }
//    	if (Log.DEBUG) Log.v("TextMessage.GetUnreadMessagesCount() Unread Count: " + count);
//    	return count;
//  }

}