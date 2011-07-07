package apps.droidnotify.common;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import apps.droidnotify.Log;

/**
 * This class is a collection of methods that are used more than once.
 * If a method is used more than once it is put here and made static so that 
 * it becomes accessible to all classes in the application.
 * 
 * @author Camille S�vigny
 */
public class Common {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	public static String[] loadContactsInfoByPhoneNumber(Context context, String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.loadContactsInfoByPhoneNumber()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		boolean _contactExists = false;
		if (incomingNumber == null) {
			if (_debug) Log.v("Common.loadContactsInfoByPhoneNumber() Phone number provided is null: Exiting...");
			return null;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("Common.loadContactsInfoByPhoneNumber() Phone number provided appears to be an email address: Exiting...");
			return null;
		}
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Common.loadContactsInfoByPhoneNumber() Searching Contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
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
					String contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if(removeFormatting(incomingNumber).equals(removeFormatting(contactNumber))){
						_contactID = Long.parseLong(contactID);
		    		  	if(contactName != null){
		    		  		_contactName = contactName;
		    		  	}
		    		  	if(photoID != null){
		    			  	_photoID = Long.parseLong(photoID);
		    		  	}
		  		      	_contactExists = true;
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(_contactExists) break;
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID)};
		}catch(Exception ex){
			if (_debug) Log.e("Common.loadContactsInfoByPhoneNumber() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Load the various contact info for this notification from an email.
	 * 
	 * @param context - Application Context.
	 * @param incomingEmail - Notifications's email address.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	public static String[] loadContactsInfoByEmail(Context context, String incomingEmail){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.loadContactsInfoByEmail()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		boolean _contactExists = false;
		if (incomingEmail == null) {
			if (_debug) Log.v("Common.loadContactsInfoByEmail() Email provided is null: Exiting...");
			return null;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("Common.loadContactsInfoByEmail() Email provided does not appear to be a valid email address: Exiting...");
			return null;
		}
		String contactID = null;
		try{
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
			if (_debug) Log.v("Common.loadContactsInfoByEmail() Searching contacts");
			while (cursor.moveToNext()) { 
				contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				final String[] emailProjection = null;
				final String emailSelection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID;
				final String[] emailSelectionArgs = null;
				final String emailSortOrder = null;
                Cursor emailCursor = context.getContentResolver().query(
                		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                		emailProjection,
                		emailSelection, 
                        emailSelectionArgs, 
                        emailSortOrder);
                while (emailCursor.moveToNext()) {
                	String contactEmail = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                	if(removeEmailFormatting(incomingEmail).equals(removeEmailFormatting(contactEmail))){
						_contactID = Long.parseLong(contactID);
		    		  	if(contactName != null){
		    		  		_contactName = contactName;
		    		  	}
		    		  	if(photoID != null){
		    			  	_photoID = Long.parseLong(photoID);
		    		  	}
		  		      	_contactExists = true;
		  		      	break;
					}
                }
                emailCursor.close();
                if(_contactExists) break;
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID)};
		}catch(Exception ex){
			if (_debug) Log.e("Common.loadContactsInfoByEmail() ERROR: " + ex.toString());
			return null;
		}
	}

	/**
	 * Remove all non-numeric items from the phone number.
	 * 
	 * @param phoneNumber - String of original phone number.
	 * 
	 * @return String - String of phone number with no formatting.
	 */
	public static String removeFormatting(String phoneNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeFormatting()");
		phoneNumber = phoneNumber.replace("-", "");
		phoneNumber = phoneNumber.replace("+", "");
		phoneNumber = phoneNumber.replace("(", "");
		phoneNumber = phoneNumber.replace(")", "");
		phoneNumber = phoneNumber.replace(" ", "");
		if(phoneNumber.length() > 10){
			phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
		}	
		return phoneNumber.trim();
	}
	
	/**
	 * Remove formatting from email addresses.
	 * 
	 * @param address - String of original email address.
	 * 
	 * @return String - String of email address with no formatting.
	 */
	public static String removeEmailFormatting(String address){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeEmailFormatting()");
		if(address.contains("<") && address.contains(">")){
			address = address.substring(address.indexOf("<") + 1,address.indexOf(">"));
		}
		if(address.contains("(") && address.contains(")")){
			address = address.substring(address.indexOf("(") + 1,address.indexOf(")"));
		}
		if(address.contains("[") && address.contains("]")){
			address = address.substring(address.indexOf("[") + 1,address.indexOf("]"));
		}
		return address.toLowerCase().trim();
	}
	
	/**
	 * Load the SMS/MMS thread id for this notification.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 */
	public static long loadThreadID(Context context, String address){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getThreadIdByAddress()");
		long threadID = 0;
		if (address == null){
			if (_debug) Log.v("Common.loadThreadID() Address provided is null: Exiting...");
			return 0;
		}
		if (address == ""){
			if (_debug) Log.v("Common.loadThreadID() Address provided is empty: Exiting...");
			return 0;
		}
		try{
			final String[] projection = new String[] { "_id", "thread_id" };
			final String selection = "address = " + DatabaseUtils.sqlEscapeString(address);
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = null;
			try {
		    	cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    	if (cursor != null) {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    			if (_debug) Log.v("Common.loadThreadID() Thread ID Found: " + threadID);
		    		}
		    	}
	    	}catch(Exception e){
		    		if (_debug) Log.e("Common.loadThreadID() EXCEPTION: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    	return threadID;
		}catch(Exception ex){
			if (_debug) Log.e("Common.loadThreadID() ERROR: " + ex.toString());
			return 0;
		}
	}
	
	/**
	 * Load the SMS/MMS message id for this notification.
	 * 
	 * @param context - Application Context.
	 * @param threadId - Notifications's threadID.
	 * @param timestamp - Notifications's timeStamp.
	 */
	public static long loadMessageID(Context context, long threadID, String messageBody, long timeStamp) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.loadMessageID()");
		if (messageBody == null){
			if (_debug) Log.v("Common.loadMessageID() Message body provided is null: Exiting...");
			return 0;
		} 
		long messageID = 0;
		try{
			final String[] projection = new String[] { "_id, body"};
			final String selection;
			if(threadID == 0){
				selection = null;
			}
			else{
				selection = "thread_id = " + threadID ;
			}
			final String[] selectionArgs = null;
			final String sortOrder = null;
		    Cursor cursor = null;
		    try{
		    	cursor = context.getContentResolver().query(
		    		Uri.parse("content://sms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
			    while (cursor.moveToNext()) { 
		    		if(cursor.getString(cursor.getColumnIndex("body")).trim().equals(messageBody)){
		    			messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    			if (_debug) Log.v("Common.loadMessageID() Message ID Found: " + messageID);
		    			break;
		    		}
			    }
		    }catch(Exception ex){
				if (_debug) Log.e("Common.loadMessageID() ERROR: " + ex.toString());
			}finally{
		    	cursor.close();
		    }
		    return messageID;
		}catch(Exception ex){
			if (_debug) Log.e("Common.loadMessageID() ERROR: " + ex.toString());
			return 0;
		}
	}
	
//	/**
//	 * Get the service center to use for a reply.
//	 * 
//	 * @param context
//	 * @param threadID
//	 * 
//	 * @return String - The service center address of the message.
//	 */
//	private String getServiceCenterAddress(Context context, long threadID) {
//		if (_debug) Log.v("Notification.loadServiceCenterAddress()");
//		if (threadID == 0){
//			if (_debug) Log.v("Notification.loadServiceCenterAddress() Thread ID provided is null: Exiting...");
//			return null;
//		} 
//		try{
//			final String[] projection = new String[] {"reply_path_present", "service_center"};
//			final String selection = "thread_id = " + threadID ;
//			final String[] selectionArgs = null;
//			final String sortOrder = "date DESC";
//			String serviceCenterAddress = null;
//		    Cursor cursor = context.getContentResolver().query(
//		    		Uri.parse("content://sms"),
//		    		projection,
//		    		selection,
//					selectionArgs,
//					sortOrder);
//		    try{		    	
////		    	for(int i=0; i<cursor.getColumnCount(); i++){
////		    		if (_debug) Log.v("Notification.loadServiceCenterAddress() Cursor Column: " + cursor.getColumnName(i) + " Column Value: " + cursor.getString(i));
////		    	}
//		    	while (cursor.moveToNext()) { 
//			    	serviceCenterAddress = cursor.getString(cursor.getColumnIndex("service_center"));
//	    			if(serviceCenterAddress != null){
//	    				return serviceCenterAddress;
//	    			}
//		    	}
//		    }catch(Exception ex){
//				if (_debug) Log.e("Notification.loadServiceCenterAddress() ERROR: " + ex.toString());
//			}finally{
//		    	cursor.close();
//		    }
//		    _serviceCenterAddress = serviceCenterAddress;
//		}catch(Exception ex){
//			if (_debug) Log.e("Notification.loadServiceCenterAddress() ERROR: " + ex.toString());
//		}	    
//		return null;
//	}
	
}