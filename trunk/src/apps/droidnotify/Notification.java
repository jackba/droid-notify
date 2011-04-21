package apps.droidnotify;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

/**
 * 
 * @author csevigny
 *
 */
public class Notification {
	
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
	private String _phoneNumber;
	private String _messageBody;
	private long _timeStamp;
	private long _threadID;
	private long _contactID;
	private String _contactLookupKey;
	private String _contactName;
	private long _photoID;
	private Bitmap _photoImg;
	private int _notificationType;
	private long _messageID;
	private boolean _fromEmailGateway;
	private MessageClass _messageClass;
  
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * Construct SmsMmsMessage given a raw message (created from pdu).
	 */
	public Notification(Context context, Bundle bundle, int notificationType) {
		if (Log.getDebug()) Log.v("Notification.Notification(Context, Bundle, int)");
		setContext(context);
		SmsMessage[] msgs = null;
        String messageBody = "";            
        if (bundle != null){
        	setNotificationType(notificationType);
        	if(notificationType == NOTIFICATION_TYPE_PHONE){
        		//TODO - Missed Call
    	    }
        	if(notificationType == NOTIFICATION_TYPE_SMS){
        		// Retrieve SMS message from bundle.
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	            }
	            SmsMessage sms = msgs[0];
	            setTimeStamp(new java.util.Date().getTime());
	    		setPhoneNumber(sms.getDisplayOriginatingAddress());
	    		setFromEmailGateway(sms.isEmail());
	    		setMessageClass(sms.getMessageClass());
	            //Get the entire message body from the new message.
	            for (int i=0; i<msgs.length; i++){                
	                messageBody += msgs[i].getMessageBody().toString();
	            }
	            setMessageBody(messageBody);
	    		loadThreadID(getContext(), getPhoneNumber());  
	    		loadContactsInfo(getContext(), getPhoneNumber());
        	}
    	    if(notificationType == NOTIFICATION_TYPE_MMS){
    	    	//TODO - MMS Message
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
    	    	//TODO - Calendar Reminder
    	    }
        }
	}
	
	/**
	 * 
	 * @param context
	 * @param phoneNumber
	 * @param timestamp
	 * @param notificationType
	 */
	public Notification(Context context, String phoneNumber, long timeStamp, int notificationType){
		if (Log.getDebug()) Log.v("Notification.Notification(Context, String, long, int)");
		setContext(context);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_PHONE");
    		setPhoneNumber(phoneNumber);
    		setTimeStamp(timeStamp);
    		loadContactsInfo(getContext(), getPhoneNumber());
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
    		if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_SMS");
    		setPhoneNumber(phoneNumber);
    		setTimeStamp(timeStamp);
    		loadThreadID(getContext(), getPhoneNumber());  
    		loadContactsInfo(getContext(), getPhoneNumber());
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_CALENDAR");
	    	//TODO - Calendar Reminder
	    }
	}

	//================================================================================
	// Accessors
	//================================================================================

	/**
	 * Set the context property.
	 */
	public void setContext(Context context) {
		if (Log.getDebug()) Log.v("Notification.setContext()");
	    _context = context;
	}
	
	/**
	 * Get the context property.
	 */
	public Context getContext() {
		if (Log.getDebug()) Log.v("Notification.getContext()");
	    return _context;
	}

	/**
	 * Set the phoneNumber property.
	 */
	public void setPhoneNumber(String phoneNumber) {
		if (Log.getDebug()) Log.v("Notification.setPhoneNumber() PhoneNumber: " + phoneNumber);
		_phoneNumber = phoneNumber;
	}
	
	/**
	 * Get the phoneNumber property.
	 */
	public String getPhoneNumber() {
		if (Log.getDebug()) Log.v("Notification.getPhoneNumber()");
		return _phoneNumber;
	}
	
	/**
	 * Set the messageBody property.
	 */
	public void setMessageBody(String messageBody) {
		if (Log.getDebug()) Log.v("Notification.setMessageBody() MessageBody: " + messageBody);
		_messageBody = messageBody;
	}
	
	/**
	 * Get the messageBody property.
	 */
	public String getMessageBody() {
		if (Log.getDebug()) Log.v("Notification.getMessageBody()");
		if (_messageBody == null) {
			_messageBody = "";
	    }
	    return _messageBody;
	}

	/**
	 * Set the timeStamp property.
	 */
	public void setTimeStamp(long timeStamp) {
		if (Log.getDebug()) Log.v("Notification.setTimeStamp() TimeStamp: " + timeStamp);
	    _timeStamp = timeStamp;
	}
	
	/**
	 * Get the timeStamp property.
	 */
	public long getTimeStamp() {
		if (Log.getDebug()) Log.v("Notification.getTimeStamp()");
	    return _timeStamp;
	}
	
	/**
	 * Set the threadID property.
	 */
	public void setThreadID(long threadID) {
		if (Log.getDebug()) Log.v("Notification.setThreadID() ThreadID: " + threadID);
	    _threadID = threadID;
	}
	
	/**
	 * Get the threadID property.
	 */
	public long getThreadID() {
		if (Log.getDebug()) Log.v("Notification.getThreadID()");
	    return _threadID;
	}	
	
	/**
	 * Set the contactID property.
	 */
	public void setContactID(long contactID) {
		if (Log.getDebug()) Log.v("Notification.setContactID() ContactID: " + contactID);
	    _contactID = contactID;
	}
	
	/**
	 * Get the contactID property.
	 */
	public long getContactID() {
		if (Log.getDebug()) Log.v("Notification.getContactID()");
	    return _contactID;
	}
	
	/**
	 * Set the contactLookupKey property.
	 */
	public void setContactLookupKey(String contactLookupKey) {
		if (Log.getDebug()) Log.v("Notification.setContactLookupKey() ContactLookupKey: " + contactLookupKey);
		_contactLookupKey = contactLookupKey;
	}
	
	/**
	 * Get the contactLookupKey property.
	 */
	public String getContactLookupKey() {
		if (Log.getDebug()) Log.v("Notification.getContactLookupKey()");
	    return _contactLookupKey;
	}	

	/**
	 * Set the contactName property.
	 */
	public void setContactName(String contactName) {
		if (Log.getDebug()) Log.v("Notification.setContactName() ContactName: " + contactName);
		_contactName = contactName;
	}
	
	/**
	 * Get the contactName property.
	 */
	public String getContactName() {
		if (Log.getDebug()) Log.v("Notification.getContactName()");
		if (_contactName == null) {
			_contactName = _context.getString(android.R.string.unknownName);
	    }
		return _contactName;
	}

	/**
	 * Set the photoID property.
	 */
	public void setPhotoID(long photoID) {
		if (Log.getDebug()) Log.v("Notification.setPhotoID() PhotoID: " + photoID);
		_photoID = photoID;
	}
	
	/**
	 * Get the photoID property.
	 */
	public long getPhotoID() {
		if (Log.getDebug()) Log.v("Notification.getPhotoID()");
		return _photoID;
	}

	/**
	 * Set the photoImg property.
	 */
	public void setPhotoImg(Bitmap photoImg) {
		if (Log.getDebug()) Log.v("Notification.setPhotoID() PhotoIImg: " + photoImg);
		_photoImg = photoImg;
	}
	
	/**
	 * Get the photoIImg property.
	 */
	public Bitmap getPhotoImg() {
		if (Log.getDebug()) Log.v("Notification.getPhotoIImg()");
		return _photoImg;
	}
	
	/**
	 * Set the notificationType property.
	 */
	public void setNotificationType(int notificationType) {
		if (Log.getDebug()) Log.v("Notification.setNotificationType() NotificationType: " + notificationType);
		_notificationType = notificationType;
	}
	
	/**
	 * Get the notificationType property.
	 */
	public int getNotificationType() {
		if (Log.getDebug()) Log.v("Notification.getNotificationType()");
		return _notificationType;
	}

	/**
	 * Set the messageID property.
	 */
	public void setMessageId(long messageID) {
		if (Log.getDebug()) Log.v("Notification.setMessageId() MessageID: " + messageID);
  		_messageID = messageID;
	}
	
	/**
	 * Get the messageID property.
	 */
	public long getMessageId() {
		if (Log.getDebug()) Log.v("Notification.getMessageId()");
  		return _messageID;
	}

	/**
	 * Set the fromEmailGateway property.
	 */
	public void setFromEmailGateway(boolean fromEmailGateway) {
		if (Log.getDebug()) Log.v("Notification.setFromEmailGateway() FromEmailGateway: " + fromEmailGateway);
  		_fromEmailGateway = fromEmailGateway;
	}
	
	/**
	 * Get the fromEmailGateway property.
	 */
	public boolean setFromEmailGateway() {
		if (Log.getDebug()) Log.v("Notification.getFromEmailGateway()");
  		return _fromEmailGateway;
	}	

	/**
	 * Set the messageClass property.
	 */
	public void setMessageClass(MessageClass messageClass) {
		if (Log.getDebug()) Log.v("Notification.setMessageClass()");
		_messageClass = messageClass;
	}
	
	/**
	 * Get the messageClass property.
	 */
	public MessageClass getMessageClass() {
		if (Log.getDebug()) Log.v("Notification.getMessageClass()");
  		return _messageClass;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Get the THREAD_ID from the SMS Contract content provider using the SMS message address.
	 */
	private void loadThreadID(Context context, String phoneNumber){
		if (Log.getDebug()) Log.v("Notification.getThreadIdByAddress()");
		if (phoneNumber == null){
			if (Log.getDebug()) Log.v("Notification.loadThreadID() Phone number provided is NULL: Exiting loadThreadID()");
			return;
		}
		final String[] projection = new String[] { "_ID", "THREAD_ID" };
		final String selection = "ADDRESS = " + phoneNumber;
		final String[] selectionArgs = null;
		final String sortOrder = null;
	    long threadID = 0;
	    Cursor cursor = context.getContentResolver().query(
	    		Uri.parse("content://sms/inbox"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	try {
	    		if (cursor.moveToFirst()) {
	    			threadID = cursor.getLong(cursor.getColumnIndex("THREAD_ID"));
	    			if (Log.getDebug()) Log.v("Notification.getThreadIdByAddress() Thread_ID Found: " + threadID);
	    		}
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    setThreadID(threadID);
	}

	/**
	 * Load contact info from the ContactsContract content provider using the SMS message address.
	 */ 
	private void loadContactsInfo(Context context, String phoneNumber){
		if (Log.getDebug()) Log.v("Notification.loadContactsInfo()");
		if (phoneNumber == null) {
			if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Phone number provided is NULL: Exiting loadContactsInfo()");
			return;
		}
		PhoneNumber incomingNumber = new PhoneNumber(phoneNumber);
		if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Got PhoneNumber object");
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				projection, 
				selection, 
				selectionArgs, 
				sortOrder);
		if (Log.getDebug()) Log.v("Notification.loadContactsInfo() Searching contacts");
		while (cursor.moveToNext()) { 
		   String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
		   String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
		   String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		   String contactLookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		   String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
		   if (Integer.parseInt(hasPhone) > 0) { 
				final String[] phoneProjection = null;
				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
				final String[] phoneSelectionArgs = null;
				final String phoneSortOrder = null;
				Cursor phoneCursor = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
						phoneProjection, 
						phoneSelection, 
						phoneSelectionArgs, 
						phoneSortOrder); 
		      while (phoneCursor.moveToNext()) { 
		    	  String contactPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    	  PhoneNumber contactNumber = new PhoneNumber(contactPhoneNumber);
		    	  if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
		    		  setContactID(Long.parseLong(contactID));
		    		  if(contactLookupKey != null){
		    			  setContactLookupKey(contactLookupKey);
		    		  }
		    		  if(contactName != null){
		    			  setContactName(contactName);
		    		  }
		    		  if(photoID != null){
		    			  setPhotoID(Long.parseLong(photoID));
		    		  }
		  	          Uri uri = ContentUris.withAppendedId(
		  	        		  ContactsContract.Contacts.CONTENT_URI,
		  	        		  Long.parseLong(contactID));
		  		      InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		  		      Bitmap contactPhotoBitmap = BitmapFactory.decodeStream(input);
		  		      if(contactPhotoBitmap!= null){
		  		    	  setPhotoImg(contactPhotoBitmap);
		  		      }
		    	  }
		      } 
		      phoneCursor.close(); 
		   }
		}
		cursor.close();
	}


	
}