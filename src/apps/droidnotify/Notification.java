package apps.droidnotify;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

/**
 * 
 * @author Camille Sevigny
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
	private final int NOTIFICATION_TYPE_EMAIL = 4;
	
	final String SMS_DISMISS_KEY = "sms_dismiss_button_action";
	final String MMS_DISMISS_KEY = "mms_dismiss_button_action";
	final String MISSED_CALL_DISMISS_KEY = "missed_call_dismiss_button_action";
	final String SMS_DELETE_KEY = "sms_delete_button_action";
	final String MMS_DELETE_KEY = "mms_delete_button_action";
	final String SMS_DISMISS_ACTION_MARK_READ = "0";
	final String SMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	final String SMS_DELETE_ACTION_DELETE_THREAD = "1";
	final String MMS_DISMISS_ACTION_MARK_READ = "0";
	final String MMS_DELETE_ACTION_DELETE_MESSAGE = "0";
	final String MMS_DELETE_ACTION_DELETE_THREAD = "1";
	final String MISSED_CALL_DISMISS_ACTION_MARK_READ = "0";
	final String MISSED_CALL_DISMISS_ACTION_DELETE = "1";

	
	//================================================================================
    // Properties
    //================================================================================
	
	private Context _context;
	private String _phoneNumber;
	private String _addressBookPhoneNumber;
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
	private boolean _contactExists;
  
	//================================================================================
	// Constructors
	//================================================================================
  
	/**
	 * Construct SmsMmsMessage given a raw message (created from pdu).
	 */
	public Notification(Context context, Bundle bundle, int notificationType) {
		if (Log.getDebug()) Log.v("Notification.Notification(Context, Bundle, int)");
		setContext(context);
		setContactExists(false);
		SmsMessage[] msgs = null;
        String messageBody = "";            
        if (bundle != null){
        	setNotificationType(notificationType);
        	if(notificationType == NOTIFICATION_TYPE_PHONE){
        		//Do Nothing. This should not be called if a missed call is received.
    	    }
        	if(notificationType == NOTIFICATION_TYPE_SMS){
        		// Retrieve SMS message from bundle.
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	            }
	            SmsMessage sms = msgs[0];
	            setTimeStamp(sms.getTimestampMillis());
	    		setPhoneNumber(sms.getDisplayOriginatingAddress());
	    		setFromEmailGateway(sms.isEmail());
	    		setMessageClass(sms.getMessageClass());
	            //Get the entire message body from the new message.
	            for (int i=0; i<msgs.length; i++){                
	                messageBody += msgs[i].getMessageBody().toString();
	            }
	            setMessageBody(messageBody);
	    		loadThreadID(getContext(), getPhoneNumber());
	    		loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
	    		loadContactsInfo(getContext(), getPhoneNumber());
        	}
    	    if(notificationType == NOTIFICATION_TYPE_MMS){
    	    	//TODO - NOTIFICATION_TYPE_MMS - MMS Message
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
    	    	//TODO - NOTIFICATION_TYPE_CALENDAR - Calendar Event
    	    }
    	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
    	    	//TODO - NOTIFICATION_TYPE_EMAIL - Email Message
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
		setContactExists(false);
		setNotificationType(notificationType);
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_PHONE");
    		setPhoneNumber(phoneNumber);
    		setTimeStamp(timeStamp);
    		loadContactsInfo(getContext(), getPhoneNumber());
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS || notificationType == NOTIFICATION_TYPE_MMS){
    		if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_SMS OR NOTIFICATION_TYPE_MMS");
    		//Do Nothing. This should not be called if a SMS or MMS is received.
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_CALENDAR");
	    	//TODO - Calendar Reminder
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	if (Log.getDebug()) Log.v("Notification.Notification() NOTIFICATION_TYPE_EMAIL");
	    	//TODO - Email Message
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
	 * Set the addressBookPhoneNumber property.
	 */
	public void setAddressBookPhoneNumber(String addressBookPhoneNumber) {
		if (Log.getDebug()) Log.v("Notification.setAddressBookPhoneNumber() PhoneNumber: " + addressBookPhoneNumber);
		_addressBookPhoneNumber = addressBookPhoneNumber;
	}
	
	/**
	 * Get the addressBookPhoneNumber property.
	 */
	public String getAddressBookPhoneNumber() {
		if (Log.getDebug()) Log.v("Notification.getAddressBookPhoneNumber()");
		if(_addressBookPhoneNumber == null){
			loadContactsInfo(getContext(),getPhoneNumber());
		}
		if(_addressBookPhoneNumber == null){
			return _phoneNumber;
		}
		return _addressBookPhoneNumber;
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
		if(_threadID == 0){
			loadThreadID(getContext(), getPhoneNumber());
		}
		if (Log.getDebug()) Log.v("Notification.getThreadID() ThreadID: " + _threadID);
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
		if(_contactID == 0){
			loadContactsInfo(getContext(),getPhoneNumber());
		}
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
		if(_contactLookupKey == null){
			loadContactsInfo(getContext(),getPhoneNumber());
		}
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
	public void setMessageID(long messageID) {
		if (Log.getDebug()) Log.v("Notification.setMessageID() MessageID: " + messageID);
  		_messageID = messageID;
	}
	
	/**
	 * Get the messageID property.
	 */
	public long getMessageID() {
		if (Log.getDebug()) Log.v("Notification.getMessageID()");
		if(_messageID == 0){
			loadMessageID(getContext(), getThreadID(), getMessageBody(), getTimeStamp());
		}
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

	/**
	 * Set the contactExists property.
	 */
	public void setContactExists(boolean contactExists) {
		if (Log.getDebug()) Log.v("Notification.setContactExists()");
		_contactExists = contactExists;
	}
	
	/**
	 * Get the contactExists property.
	 */
	public boolean getContactExists() {
		if (Log.getDebug()) Log.v("Notification.getContactExists()");
  		return _contactExists;
	}
	
	//================================================================================
	// Public Methods
	//================================================================================
  
	/**
	 * Set this notification as being viewed on the users phone.
	 */
	public void setViewed(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setViewed()");
		Context context = getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int notificationType = getNotificationType();
		if (Log.getDebug()) Log.v("Notification.setViewed() Preference Value: " + preferences.getString(MISSED_CALL_DISMISS_KEY, "0"));
    	if(notificationType == NOTIFICATION_TYPE_PHONE){
    		//Action is determined by the users preferences. 
    		//Either mark the call log as viewed, delete the call log entry, or do nothing to the call log entry.
    		if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_MARK_READ)){
    			setCallViewed(isViewed);
    		}else if(preferences.getString(MISSED_CALL_DISMISS_KEY, "0").equals(MISSED_CALL_DISMISS_ACTION_DELETE)){
    			deleteFromCallLog();
    		}
	    }
    	if(notificationType == NOTIFICATION_TYPE_SMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(SMS_DISMISS_KEY, "0").equals(SMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
    	if(getNotificationType() == NOTIFICATION_TYPE_MMS){
    		//Action is determined by the users preferences. 
    		//Either mark the message as viewed or do nothing to the message.
    		if(preferences.getString(MMS_DISMISS_KEY, "0").equals(MMS_DISMISS_ACTION_MARK_READ)){
    			setMessageRead(isViewed);
    		}
    	}
	    if(notificationType == NOTIFICATION_TYPE_CALENDAR){
	    	//TODO - Notification.setViewed() - NOTIFICATION_TYPE_CALENDAR Set the notification as being viewed on the phone.
	    }
	    if(notificationType == NOTIFICATION_TYPE_EMAIL){
	    	//TODO - Notification.setViewed() - NOTIFICATION_TYPE_EMAIL Set the notification as being viewed on the phone.
	    }
	}
	
	/**
	 * Set this notification as being viewed on the users phone.
	 */
	public void deleteMessage(){
		if (Log.getDebug()) Log.v("Notification.deleteMessage()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int notificationType = getNotificationType();
		//Decide what to do here based on the users preferences.
		//Delete the single message, delete the entire thread, or do nothing.
		boolean deleteThread = false;
		boolean deleteMessage = false;
		if(notificationType == NOTIFICATION_TYPE_SMS){
			if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(SMS_DELETE_KEY, "0").equals(SMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}else if(notificationType == NOTIFICATION_TYPE_MMS){
			if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_MESSAGE)){
				deleteThread = false;
				deleteMessage = true;
			}else if(preferences.getString(MMS_DELETE_KEY, "0").equals(MMS_DELETE_ACTION_DELETE_THREAD)){
				deleteThread = true;
				deleteMessage = false;
			}
		}
		Context context = getContext();
		long threadID = getThreadID();
		if(threadID == 0){
			if (Log.getDebug()) Log.v("Notification.deleteMessage() Thread ID == 0. Load Thread ID");
			loadThreadID(context, getPhoneNumber());
			threadID = getThreadID();
		}
		long messageID = getMessageID();
		if(messageID == 0){
			if (Log.getDebug()) Log.v("Notification.deleteMessage() Message ID == 0. Load Message ID");
			loadMessageID(context, getThreadID(), getMessageBody(), getTimeStamp());
			messageID = getMessageID();
		}
		if(deleteMessage || deleteThread){
			if(deleteThread){
				//Delete entire SMS thread.
				if (Log.getDebug()) Log.v("Notification.deleteMessage() Delete Thread ID: " + threadID);
				//Delete from URI "content://sms/conversations/"
				context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + threadID), 
						null, 
						null);
			}else{
				//Delete single message.
				if (Log.getDebug()) Log.v("Notification.deleteMessage() Delete Message ID: " + messageID);
				//Delete from URI "content://sms"
				context.getContentResolver().delete(
						Uri.parse("content://sms/" + messageID),
						null, 
						null);
			}
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Load the SMS Thread ID for this notification.
	 * 
	 * @param context
	 * @param phoneNumber
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
	    			if (Log.getDebug()) Log.v("Notification.loadThreadID() Thread ID Found: " + threadID);
	    		}
	    	}catch(Exception e){
		    		if (Log.getDebug()) Log.v("Notification.loadThreadID() EXCEPTION: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    setThreadID(threadID);
	}

	/**
	 * Load the SMS Message ID for this notification.
	 * 
	 * @param context
	 * @param threadId
	 * @param timestamp
	 */
	public void loadMessageID(Context context, long threadID, String messageBody, long timeStamp) {
		if (Log.getDebug()) Log.v("Notification.loadMessageID()");
		if (threadID == 0){
			if (Log.getDebug()) Log.v("Notification.loadMessageID() Thread ID provided is NULL: Exiting loadMessageId()");
			return;
		}    
		if (messageBody == null){
			if (Log.getDebug()) Log.v("Notification.loadMessageID() Message body provided is NULL: Exiting loadMessageId()");
			return;
		} 
		final String[] projection = new String[] { "_ID"};
		final String selection = "THREAD_ID = " + threadID + " AND BODY = " + DatabaseUtils.sqlEscapeString(messageBody);
		final String[] selectionArgs = null;
		final String sortOrder = null;
		long messageID = 0;
	    Cursor cursor = context.getContentResolver().query(
	    		Uri.parse("content://sms/inbox"),
	    		projection,
	    		selection,
				selectionArgs,
				sortOrder);
	    if (cursor != null) {
	    	try {
	    		if (cursor.moveToFirst()) {
	    			messageID = cursor.getLong(cursor.getColumnIndex("_ID"));
	    			if (Log.getDebug()) Log.v("Notification.loadMessageID() Message ID Found: " + messageID);
	    		}
	    	}catch(Exception e){
	    		if (Log.getDebug()) Log.v("Notification.loadMessageID() EXCEPTION: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    }
	    setMessageID(messageID);
	}
	
	/**
	 * Load the various contact info for this notification.
	 * 
	 * @param context
	 * @param phoneNumber
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
		    	  String addressBookPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		    	  PhoneNumber contactNumber = new PhoneNumber(addressBookPhoneNumber);
		    	  if(incomingNumber.getPhoneNumber().equals(contactNumber.getPhoneNumber())){
		    		  setContactID(Long.parseLong(contactID));
		    		  if(addressBookPhoneNumber != null){
		    			  setAddressBookPhoneNumber(addressBookPhoneNumber);
		    		  }
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
		  		    	  setContactExists(true);
		  		      }
		  		      break;
		    	  }
		      } 
		      phoneCursor.close(); 
		   }
		}
		cursor.close();
	}

	/**
	 * Set the call log as viewed (not new) or new depending on the input.
	 * 
	 * @param isViewed
	 */
	private void setCallViewed(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setCallViewed()");
		Context context = getContext();
		String phoneNumber = getPhoneNumber();
		long timeStamp = getTimeStamp();
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put(android.provider.CallLog.Calls.NEW, 0);
		}else{
			contentValues.put(android.provider.CallLog.Calls.NEW, 1);
		}
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {phoneNumber, Long.toString(timeStamp)};
		context.getContentResolver().update(
				Uri.parse("content://call_log/calls"),
				contentValues,
				selection, 
				selectionArgs);
	}
	
	/**
	 * Delete the call log entry.
	 */
	private void deleteFromCallLog(){
		if (Log.getDebug()) Log.v("Notification.deleteFromCallLog()");
		Context context = getContext();
		String phoneNumber = getPhoneNumber();
		long timeStamp = getTimeStamp();
		String selection = android.provider.CallLog.Calls.NUMBER + " = ? and " + android.provider.CallLog.Calls.DATE + " = ?";
		String[] selectionArgs = new String[] {phoneNumber, Long.toString(timeStamp)};
		context.getContentResolver().delete(
				Uri.parse("content://call_log/calls"),
				selection, 
				selectionArgs);
	}

	/**
	 * Set the SMS/MMS message as read or unread depending on the input.
	 * 
	 * @param isViewed
	 */
	private void setMessageRead(boolean isViewed){
		if (Log.getDebug()) Log.v("Notification.setMessageRead()");
		Context context = getContext();
		long messageID = getMessageID();
		long threadID = getThreadID();
		String messageBody = getMessageBody();
		long timeStamp = getTimeStamp();
		if(messageID == 0){
			if (Log.getDebug()) Log.v("Notification.setMessageRead() Message ID == 0. Load Message ID");
			loadMessageID(context, threadID, messageBody, timeStamp);
			messageID = getMessageID();
		}
		ContentValues contentValues = new ContentValues();
		if(isViewed){
			contentValues.put("READ", 1);
		}else{
			contentValues.put("READ", 0);
		}
		String selection = null;
		String[] selectionArgs = null;
		context.getContentResolver().update(
				Uri.parse("content://sms/" + messageID), 
	    		contentValues, 
	    		selection, 
	    		selectionArgs);
	}
}