package apps.droidnotify;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
	 * Construct SmsMmsMessage given a raw message (created from pdu).
	 */
	public TextMessage(Context context, Bundle b) {
		if (Log.DEBUG) Log.v("TextMessage.TextMessage()");
		setContext(context);
		SmsMessage[] msgs = null;
        String messagebody = "";            
        if (b != null){
            // Retrieve SMS message from bundle.
            Object[] pdus = (Object[]) b.get("pdus");
            msgs = new SmsMessage[pdus.length]; 
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
            }
            SmsMessage sms = msgs[0];
            setTimeStamp(new java.util.Date().getTime());
    		setFromAddress(sms.getDisplayOriginatingAddress());
    		setFromEmailGateway(sms.isEmail());
    		setMessageClass(sms.getMessageClass());
    		setMessageType(0);
            //Get the entire message body from the new message.
            for (int i=0; i<msgs.length; i++){                
                messagebody += msgs[i].getMessageBody().toString();
            }
            setMessageBody(messagebody);
    		setThreadID(getThreadIdByAddress(getContext(), getFromAddress()));  
    		setContactID(getContactIDByAddress(getContext(), getFromAddress())); 
    		//setMessageID(getMessageID(getContext(), getFromAddress())); 
        }
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
	public void setFromAddress(String fromaddress) {
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
	public void setMessageType(int messagetype) {
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
  
	//================================================================================
	// Private Methods
	//================================================================================

	 
	/**
	 * Get the contact ID from the ContactsContract content provider using the SMS message address.
	 */ 
	private long getContactIDByAddress(Context context, String address){
		if (Log.DEBUG) Log.v("TextMessage.getContactIDByAddress()");
		if (address == null) return 0;
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = null;
	    long contactID = 0;
//		Cursor cursor = context.getContentResolver().query(
//			ContactsContract.Contacts.CONTENT_URI,
//    		projection,
//    		selection,
//			selectionArgs,
//			sortOrder);
//	    if (cursor != null) {
//	    	if(cursor.getCount() > 0){
//	        	try {
//	        		while (cursor.moveToNext()) {
//	        	        String contactID = cursor.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//	        	        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//	         			if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//	         				final String[] phoneProjection = null;
//	         				final String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
//	         				final String[] phoneSelectionArgs = new String[]{contactID};
//	         				final String phoneSortOrder = null;
//	         				Cursor phoneNumberCursor = context.getContentResolver().query(
//	         						ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
//	         						phoneProjection, 
//	         						phoneSelection, 
//	         						phoneSelectionArgs, 
//	         						phoneSortOrder);
//	         			    if (phoneNumberCursor != null) {
//	         			    	if(phoneNumberCursor.getCount() > 0){
//	         			        	try {
//				         	 	        while (phoneNumberCursor.moveToNext()) {
//				         	 	        	if(phoneNumberCursor.getString(ContactsContract.CommonDataKinds.Phone.) == address){
//				         	 	        		contactID = phoneNumberCursor.getString(0);
//				         	 	        		if (Log.DEBUG) Log.v("TextMessage.getContactIDByAddress() Contact_ID Found: " + contactID);
//				         	 	        	}
//				         	 	        } 
//		         			    	} finally {
//		         			    		phoneNumberCursor.close();
//		         			    	}
//		         		    	}else{
//		         		    		phoneNumberCursor.close();
//		         		    	}
//	         			    }
//	         			}
//	         		}
//		    	} finally {
//		    		cursor.close();
//		    	}
//	    	}else{
//	        	cursor.close();
//	    	}
//        }
        return contactID;
	}
	
	/**
	 * Get the THREAD_ID from the SMS Contract content provider using the SMS message address.
	 */
	private long getThreadIdByAddress(Context context, String address){
		if (Log.DEBUG) Log.v("TextMessage.getThreadIdByAddress()");
		if (address == null) return 0;
		final String[] projection = new String[] { "_ID", "THREAD_ID" };
		final String selection = "address=" + address;
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
	    	if(cursor.getCount() > 0){
		    	try {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("THREAD_ID"));
		    			if (Log.DEBUG) Log.v("TextMessage.getThreadIdByAddress() Thread_ID Found: " + threadID);
		    		}
		    	} finally {
		    		cursor.close();
		    	}
	    	}else{
	        	cursor.close();
	    	}
	    }
	    return threadID;
	}

}