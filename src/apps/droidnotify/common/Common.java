package apps.droidnotify.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import apps.droidnotify.log.Log;

/**
 * This class is a collection of methods that are used more than once.
 * If a method is used more than once it is put here and made static so that 
 * it becomes accessible to all classes in the application.
 * 
 * @author Camille Sévigny
 */
public class Common {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final String _ID = "_id";
    private static final String CALENDAR_DISPLAY_NAME = "displayName"; 
    private static final String CALENDAR_SELECTED = "selected";
	
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
		if (_debug) Log.v("Common.removeEmailFormatting() Email Address: " + address);
		if(address.contains("<") && address.contains(">")){
			address = address.substring(address.indexOf("<") + 1,address.indexOf(">"));
		}
		if(address.contains("(") && address.contains(")")){
			address = address.substring(address.indexOf("(") + 1,address.indexOf(")"));
		}
		if(address.contains("[") && address.contains("]")){
			address = address.substring(address.indexOf("[") + 1,address.indexOf("]"));
		}
		if (_debug) Log.v("Common.removeEmailFormatting() Formatted Email Address: " + address);
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

	/**
	 * Gets the address of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The phone or email address of the MMS message.
	 */
	public static String getMMSAddress(Context context, String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSAddress()");
		final String[] projection = new String[] {"address"};
		final String selection = "msg_id = " + messageID;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		String messageAddress = null;
		Cursor cursor = null;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://mms/" + messageID + "/addr"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while(cursor.moveToNext()){
		    	messageAddress = cursor.getString(cursor.getColumnIndex("address"));
	            break;
	        }
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSAddress() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}	   
	    return messageAddress;
	}
	
	/**
	 * Read the message text of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The message text of the MMS message.
	 */
	public static String getMMSText(Context context, String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSText()");
		final String[] projection = new String[] {"_id", "ct", "_data", "text"};
		final String selection = "mid = " + messageID;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		StringBuilder messageText = new StringBuilder();
		Cursor cursor = null;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://mms/part"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    while(cursor.moveToNext()){
		        String partId = cursor.getString(cursor.getColumnIndex("_id"));
		        String contentType = cursor.getString(cursor.getColumnIndex("ct"));
		        String text = cursor.getString(cursor.getColumnIndex("text"));
		        if(text != null){
	            	if(!messageText.toString().equals("")){
	            		messageText.append(" ");
	            	}
			        messageText.append(text);
		        }
		        if (contentType.equals("text/plain")) {
		            String data = cursor.getString(cursor.getColumnIndex("_data"));
		            if (data != null) {
		            	if(!messageText.toString().equals("")){
		            		messageText.append(" ");
		            	}
		            	messageText.append(getMMSTextFromPart(context, partId));
		            }
		        }
	        }
		}catch(Exception ex){
			if (_debug) Log.e("MMSReceiverService.getMMSText ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}	   
	    return messageText.toString();  
	}

	/**
	 * Read the phones Calendars and return the information on them.
	 * 
	 * @return String - A string of the available Calendars. Specially formatted string with the Calendar information.
	 */
	public static String getAvailableCalendars(Context context){
		if (Log.getDebug()) Log.v("SelectCalendarListPreference.getAvailableCalendars()");
		StringBuilder calendarsInfo = new StringBuilder();
		Cursor cursor = null;
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
			//Android 2.2+
			contentProvider = "content://com.android.calendar";
			//Android 2.1 and below.
			//contentProvider = "content://calendar";
			cursor = contentResolver.query(
				Uri.parse(contentProvider + "/calendars"), 
				new String[] { _ID, CALENDAR_DISPLAY_NAME, CALENDAR_SELECTED },
				null,
				null,
				null);
			while (cursor.moveToNext()) {
				final String calendarID = cursor.getString(cursor.getColumnIndex(_ID));
				final String calendarDisplayName = cursor.getString(cursor.getColumnIndex(CALENDAR_DISPLAY_NAME));
				final Boolean calendarSelected = !cursor.getString(cursor.getColumnIndex(CALENDAR_SELECTED)).equals("0");
				if(calendarSelected){
					if (Log.getDebug()) Log.v("Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
					if(!calendarsInfo.toString().equals("")){
						calendarsInfo.append(",");
					}
					calendarsInfo.append(calendarID + "|" + calendarDisplayName);
				}
			}	
		}catch(Exception ex){
			if (Log.getDebug()) Log.e("SelectCalendarListPreference.getAvailableCalendars() ERROR: " + ex.toString());
			return null;
		}finally{
			cursor.close();
		}
		if(calendarsInfo.toString().equals("")){
			return null;
		}else{
			return calendarsInfo.toString();
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

	//================================================================================
	// Private Methods
	//================================================================================
	
	/**
	 * Read the message text of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The message text of the MMS message.
	 */
	private static String getMMSTextFromPart(Context context, String messageID) {
		if (_debug) Log.v("MMSReceiverService.getMMSTextFromPart()");
	    InputStream inputStream = null;
	    StringBuilder messageText = new StringBuilder();
	    try {
	    	inputStream = context.getContentResolver().openInputStream(Uri.parse("content://mms/part/" + messageID));
	        if (inputStream != null) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
	            BufferedReader reader = new BufferedReader(inputStreamReader);
	            String temp = reader.readLine();
	            while (temp != null) {
	            	messageText.append(temp);
	                temp = reader.readLine();
	            }
	        }
	    } catch (Exception ex) {
	    	if (_debug) Log.e("MMSReceiverService.getMMSTextFromPart() ERROR: " + ex.toString());
	    }finally {
	    	try{
	    		inputStream.close();
	    	}catch(Exception ex){
	    		if (_debug) Log.e("MMSReceiverService.getMMSTextFromPart() ERROR: " + ex.toString());
	    	}
	    }
	    return messageText.toString();
	}
	
}