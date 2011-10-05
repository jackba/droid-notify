package apps.droidnotify.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.NotificationViewFlipper;
import apps.droidnotify.R;
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
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Rounds the corners of a Bitmap image.
	 * 
	 * @param bitmap - The Bitmap to be formatted.
	 * @param pixels - The number of pixels as the diameter of the rounded corners.
	 * 
	 * @return Bitmap - The formatted Bitmap image.
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, boolean resizeImage, int resizeX, int resizeY) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getRoundedCornerBitmap()");
		try{
			Bitmap output = null;
			if(bitmap == null){
				return null;
			}else{
		        output = Bitmap.createBitmap(
		        		bitmap.getWidth(), 
		        		bitmap
		                .getHeight(), 
		                Config.ARGB_8888);
		        Canvas canvas = new Canvas(output);
		        final int color = 0xff424242;
		        final Paint paint = new Paint();
		        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		        final RectF rectF = new RectF(rect);
		        final float roundPx = pixels;
		        paint.setAntiAlias(true);
		        canvas.drawARGB(0, 0, 0, 0);
		        paint.setColor(color);
		        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		        canvas.drawBitmap(bitmap, rect, rect, paint);
		        //Resize the Bitmap so that all images are consistent.
		        //Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
		        if(resizeImage){
		        	output = Bitmap.createScaledBitmap(output, resizeX, resizeY, true);
		        }
		        return output;
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.getRoundedCornerBitmap() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	public static String[] getContactsInfoByPhoneNumber(Context context, String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.loadContactsInfoByPhoneNumber()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
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
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
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
					if(isPhoneNumberEqual(contactNumber, incomingNumber)){
						_contactID = Long.parseLong(contactID);
		    		  	if(contactName != null){
		    		  		_contactName = contactName;
		    		  	}
		    		  	if(photoID != null){
		    			  	_photoID = Long.parseLong(photoID);
		    		  	}
		    		  	_lookupKey = lookupKey;
		  		      	_contactExists = true;
		  		      	break;
					}
				}
				phoneCursor.close(); 
				if(_contactExists) break;
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID), _lookupKey};
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
	public static String[] getContactsInfoByEmail(Context context, String incomingEmail){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.loadContactsInfoByEmail()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
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
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
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
		    		  	_lookupKey = lookupKey;
		  		      	_contactExists = true;
		  		      	break;
					}
                }
                emailCursor.close();
                if(_contactExists) break;
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID), _lookupKey};
		}catch(Exception ex){
			if (_debug) Log.e("Common.loadContactsInfoByEmail() ERROR: " + ex.toString());
			return null;
		}
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
	public static long getThreadID(Context context, String address, int messageType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getThreadIdByAddress()");
		String messageURI = "content://sms/inbox";
		if(messageType == Constants.MESSAGE_TYPE_SMS){
			messageURI = "content://sms/inbox";
		}else if(messageType == Constants.MESSAGE_TYPE_MMS){
			messageURI = "content://mms/inbox";
		}
		long threadID = 0;
		if (address == null){
			if (_debug) Log.v("Common.getThreadID() Address provided is null: Exiting...");
			return 0;
		}
		if (address == ""){
			if (_debug) Log.v("Common.getThreadID() Address provided is empty: Exiting...");
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
		    		Uri.parse(messageURI),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
		    	if (cursor != null) {
		    		if (cursor.moveToFirst()) {
		    			threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    			if (_debug) Log.v("Common.getThreadID() Thread ID Found: " + threadID);
		    		}
		    	}
	    	}catch(Exception e){
		    		if (_debug) Log.e("Common.getThreadID() EXCEPTION: " + e.toString());
	    	} finally {
	    		if(cursor != null){
					cursor.close();
				}
	    	}
	    	return threadID;
		}catch(Exception ex){
			if (_debug) Log.e("Common.getThreadID() ERROR: " + ex.toString());
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
	public static long getMessageID(Context context, long threadID, String messageBody, long timeStamp, int messageType) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getMessageID()");
		String messageURI = null;
		if(messageType == Constants.MESSAGE_TYPE_SMS){
			messageURI = "content://sms/inbox";
		}else{
			messageURI = "content://mms/inbox";
		}
		if (messageBody == null){
			if (_debug) Log.v("Common.getMessageID() Message body provided is null: Exiting...");
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
		    		Uri.parse(messageURI),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
			    while (cursor.moveToNext()) { 
		    		if(cursor.getString(cursor.getColumnIndex("body")).replace("\n", "<br/>").trim().equals(messageBody)){
		    			messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    			if (_debug) Log.v("Common.getMessageID() Message ID Found: " + messageID);
		    			break;
		    		}
			    }
		    }catch(Exception ex){
				if (_debug) Log.e("Common.getMessageID() ERROR: " + ex.toString());
			}finally{
				if(cursor != null){
					cursor.close();
				}
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
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getMMSAddress()");
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
			if (_debug) Log.e("Common.getMMSAddress() ERROR: " + ex.toString());
		} finally {
			if(cursor != null){
				cursor.close();
			}
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
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getMMSText()");
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
			if (_debug) Log.e("Common.getMMSText ERROR: " + ex.toString());
		} finally {
			if(cursor != null){
				cursor.close();
			}
    	}	   
	    return messageText.toString();  
	}

	/**
	 * Read the phones Calendars and return the information on them.
	 * 
	 * @return String - A string of the available Calendars. Specially formatted string with the Calendar information.
	 */
	public static String getAvailableCalendars(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getAvailableCalendars()");
		StringBuilder calendarsInfo = new StringBuilder();
		Cursor cursor = null;
		try{
			ContentResolver contentResolver = context.getContentResolver();
			// Fetch a list of all calendars synced with the device, their display names and whether the user has them selected for display.
			String contentProvider = "";
			//Android 2.2+
			contentProvider = "content://com.android.calendar";
			cursor = contentResolver.query(
				Uri.parse(contentProvider + "/calendars"), 
				new String[] { Constants.CALENDAR_ID, Constants.CALENDAR_DISPLAY_NAME, Constants.CALENDAR_SELECTED },
				null,
				null,
				null);
			while (cursor.moveToNext()) {
				final String calendarID = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_ID));
				final String calendarDisplayName = cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_DISPLAY_NAME));
				final Boolean calendarSelected = !cursor.getString(cursor.getColumnIndex(Constants.CALENDAR_SELECTED)).equals("0");
				if(calendarSelected){
					if (_debug) Log.v("Id: " + calendarID + " Display Name: " + calendarDisplayName + " Selected: " + calendarSelected);
					if(!calendarsInfo.toString().equals("")){
						calendarsInfo.append(",");
					}
					calendarsInfo.append(calendarID + "|" + calendarDisplayName);
				}
			}	
		}catch(Exception ex){
			if (_debug) Log.e("Common.getAvailableCalendars() ERROR: " + ex.toString());
			return null;
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		if(calendarsInfo.toString().equals("")){
			return null;
		}else{
			return calendarsInfo.toString();
		}
	}
	
	/**
	 * Place a phone call.
	 * 
	 * @param phoneNumber - The phone number we want to send a place a call to.
	 */
	public static boolean makePhoneCall(Context context, NotificationActivity notificationActivity, String phoneNumber, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.makePhoneCall()");
		try{
			if(phoneNumber == null){
				Toast.makeText(context, context.getString(R.string.app_android_phone_number_format_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNumber));
	        notificationActivity.startActivityForResult(intent, requestCode);
		    return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.makePhoneCall() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_phone_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent for any android messaging application to send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startMessagingAppReplyActivity(Context context, NotificationActivity notificationActivity, String phoneNumber, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startMessagingAppReplyActivity()");
		if(phoneNumber == null){
			Toast.makeText(context, context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
			return false;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_SENDTO);
		    intent.setData(Uri.parse("smsto:" + phoneNumber));
		    // Exit the app once the SMS is sent.
		    intent.putExtra("compose_mode", true);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * Start the intent for any android messaging application to view the message thread.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startMessagingAppViewThreadActivity(Context context, NotificationActivity notificationActivity, String phoneNumber, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startMessagingAppViewThreadActivity()");
		if(phoneNumber == null){
			Toast.makeText(context, context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
			return false;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
		    intent.setData(Uri.parse("smsto:" + phoneNumber));
	        notificationActivity.startActivityForResult(intent, requestCode);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppViewThreadActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * Start the intent for any android messaging application to view the messaging inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The phone number we want to send a message to.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startMessagingAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startMessagingAppViewInboxActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_MAIN);
		    intent.setType("vnd.android-dir/mms-sms");
	        notificationActivity.startActivityForResult(intent, requestCode);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent to view the phones call log.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startCallLogViewActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startCallLogViewActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android.cursor.dir/calls");
			notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startCallLogViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_call_log_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent to view a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to view.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactViewActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactViewActivity()");
		try{
			if(contactID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
		    notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent to start the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startViewCalendarActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity"); 
			notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception e){
			if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_MAIN); 
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.LaunchActivity"));
				notificationActivity.startActivityForResult(intent, requestCode);
				return true;
			}catch(Exception ex){
				if (_debug) Log.e("Common.startViewCalendarActivity() ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				return false;
			}
		}
	}
	
	/**
	 * Start the intent to add an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startAddCalendarEventActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startAddCalendarEventActivity()");
		try{
			//Androids calendar app.
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception e){
			if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.EditEvent"));
				notificationActivity.startActivityForResult(intent, requestCode);
				return true;
			}catch(Exception ex){
				if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				return false;
			}
		}
	}
	
	/**
	 * Start the intent to view an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startViewCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startViewCalendarEventActivity()");
		try{
			if(calendarEventID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//Android 2.2+
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
			notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startViewCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent to edit an event to the calendar app.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param calendarEventID - The id of the calendar event.
	 * @param calendarEventStartTime - The start time of the calendar event.
	 * @param calendarEventEndTime - The end time of the calendar event.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startEditCalendarEventActivity(Context context, NotificationActivity notificationActivity, long calendarEventID, long calendarEventStartTime, long calendarEventEndTime, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startEditCalendarEventActivity()");
		try{
			if(calendarEventID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_calendar_event_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			//Android 2.2+
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
			notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startEditCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	/**
	 * Start the intent to edit a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param contactID - The id of the contact we want to edit.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactEditActivity(Context context, NotificationActivity notificationActivity, long contactID, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactEditActivity()");
		try{
			if(contactID == 0){
				Toast.makeText(context, context.getString(R.string.app_android_contact_not_found_error), Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
		    notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactEditActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}	
	
	/**
	 * Start the intent to add a contact.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param sentFromAddress - The address (email or phone) of the contact we want to add.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startContactAddActivity(Context context, NotificationActivity notificationActivity, String sentFromAddress, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startContactAddActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			if(sentFromAddress.contains("@")){
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL, sentFromAddress);
			}else{
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, sentFromAddress);
			}
		    notificationActivity.startActivityForResult(intent, requestCode);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactAddActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			return false;
		}
	}	
	
	/**
	 * Determine if the users phone has a blocked app currently running on the phone.
	 * 
	 * @param context - Application Context.
	 * 
	 * @return boolean - Returns true if a app that is flagged to block is currently running.
	 */
	public static boolean isBlockingAppRunning(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isBlockingAppRunning()");
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List <RunningTaskInfo> runningTaskArray = activityManager.getRunningTasks(99999);
	    Iterator <RunningTaskInfo> runningTaskArrayIterator = runningTaskArray.iterator();
	    RunningTaskInfo runningTaskInfo = null;
	    while(runningTaskArrayIterator.hasNext()){
	    	runningTaskInfo = runningTaskArrayIterator.next();
	    	ComponentName runningTaskComponent = runningTaskInfo.baseActivity;
	    	String runningTaskPackageName = runningTaskComponent.getPackageName();
	        if (_debug) Log.v("Common.isBlockingAppRunning() runningTaskPackageName: " + runningTaskPackageName);
	        int messagingPackageNamesArraySize = Constants.BLOCKED_PACKAGE_NAMES_ARRAY.length;
	        for(int i = 0; i < messagingPackageNamesArraySize; i++){
	        	if (_debug) Log.v("Common.isBlockingAppRunning() MESSAGING_PACKAGE_NAMES_ARRAY[i]: " + Constants.BLOCKED_PACKAGE_NAMES_ARRAY[i]);
		        if(Constants.BLOCKED_PACKAGE_NAMES_ARRAY[i].contains(runningTaskPackageName)){
		        	return true;
		        }
	        }
	    }
		return false;
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
		return phoneNumber.trim();
	}
	
	/**
	 * Determines if the incoming number is a Private or Unknown number.
	 * 
	 * @param incomingNumber - The incoming phone number.
	 * 
	 * @return boolean - Returns true if the number is a Private number or Unknown number.
	 */
	public static boolean isPrivateUnknownNumber(String incomingNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isPrivateUnknownNumber() incomingNumber: " + incomingNumber);
		try{
			if(incomingNumber.length() > 4){
				return false;
			}
			int convertedNumber = Integer.parseInt(incomingNumber);
			if(convertedNumber < 1) return true;
		}catch(Exception ex){
			if (_debug) Log.v("Common.isPrivateUnknownNumber() Integer Parse Error");
			return false;
		}
		return false;
	}
	
	/**
	 * Convert a GMT timestamp to the phones local time.
	 * 
	 * @param inputTimestamp - GMT timestamp we want to convert.
	 * 
	 * @return long - The timestamp in the phones local time.
	 */
	public static long convertGMTToLocalTime(Context context, long inputTimeStamp){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.convertGMTToLocalTime() InputTimeStamp: " + inputTimeStamp);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    long offset = TimeZone.getDefault().getOffset(inputTimeStamp);
		long timeStampAdjustment = Long.parseLong(preferences.getString(Constants.SMS_TIMESTAMP_ADJUSTMENT_KEY, "0")) * 60 * 60 * 1000;
	    long outputTimeStamp = inputTimeStamp - offset + timeStampAdjustment;
	    if (_debug) Log.v("Common.convertGMTToLocalTime() OutputTimeStamp: " + outputTimeStamp);
	    return outputTimeStamp;
	}
	
	/**
	 * Cancel the stock missed call notification.
	 * 
	 * @return boolean - Returns true if the stock missed call notification was cancelled.
	 */
	public static boolean cancelStockMissedCallNotification(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.cancelStockMissedCallNotification()");
		try{
	        Class serviceManagerClass = Class.forName("android.os.ServiceManager");
	        Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
	        Object phoneService = getServiceMethod.invoke(null, "phone");
	        Class ITelephonyClass = Class.forName("com.android.internal.telephony.ITelephony");
	        Class ITelephonyStubClass = null;
	        for(Class clazz : ITelephonyClass.getDeclaredClasses()){
	            if (clazz.getSimpleName().equals("Stub")){
	                ITelephonyStubClass = clazz;
	                break;
	            }
	        }
	        if (ITelephonyStubClass != null) {
	            Class IBinderClass = Class.forName("android.os.IBinder");
	            Method asInterfaceMethod = ITelephonyStubClass.getDeclaredMethod("asInterface", IBinderClass);
	            Object iTelephony = asInterfaceMethod.invoke(null, phoneService);
	            if (iTelephony != null){
	                Method cancelMissedCallsNotificationMethod = iTelephony.getClass().getMethod("cancelMissedCallsNotification");
	                cancelMissedCallsNotificationMethod.invoke(iTelephony);
	            }else{
	            	if (_debug) Log.e("Telephony service is null, can't call cancelMissedCallsNotification.");
	    	    	return false;
	            }
	        }else{
	            if (_debug) Log.v("Unable to locate ITelephony.Stub class.");
		    	return false;
	        }
	        return true;
	    }catch (Exception ex){
	    	if (_debug) Log.e("Common.cancelStockMissedCallNotification() ERROR: " + ex.toString());
	    	return false;
	    }
	}
	
	/**
	 * Display the status bar notification.
	 * 
	 * @param context - The application context.
	 * @param notificationType - The type of notification we are working with.
	 * @param callStateIdle - The call state of the users phone. True if the users phone is idle (not being used).
	 * @param sentFromContactName - The contact name.
	 * @param sentFromAddress - The sent from address (phone number)
	 * @param message - The message of the notification.
	 * @param calendarEventID - The calendar event id.
	 * @param calendarEventStartTime - The calendar event start time.
	 * @param calendarEventEndTime - The calendar event end time.
	 */
	public static void setStatusBarNotification(Context context, int notificationType, boolean callStateIdle, String sentFromContactName, String sentFromAddress, String message){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setStatusBarNotification()");
		try{
			//Preference keys.
			String POPUP_ENABLED_KEY = null;
			String ENABLED_KEY = null;
			String SOUND_SETTING_KEY = null;
			String RINGTONE_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_RINGTONE_DEFAULT;;
			String IN_CALL_SOUND_ENABLED_KEY = null;
			String VIBRATE_SETTING_KEY = null;
			String VIBRATE_ALWAYS_VALUE = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_ALWAYS_VALUE;
			String VIBRATE_WHEN_VIBRATE_MODE_VALUE = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_WHEN_VIBRATE_MODE_VALUE;
			String IN_CALL_VIBRATE_ENABLED_KEY = null;
			String VIBRATE_PATTERN_KEY = null;
			String VIBRATE_PATTERN_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_DEFAULT;
			String VIBRATE_PATTERN_CUSTOM_VALUE_KEY = null;
			String VIBRATE_PATTERN_CUSTOM_KEY = null;
			String LED_ENABLED_KEY  = null;
			String LED_COLOR_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_LED_COLOR_DEFAULT;
			String LED_COLOR_KEY = null;
			String LED_COLOR_CUSTOM_VALUE_KEY = null;
			String LED_COLOR_CUSTOM_KEY = null;
			String LED_PATTERN_KEY = null;
			String LED_PATTERN_DEFAULT = Constants.STATUS_BAR_NOTIFICATIONS_LED_PATTERN_DEFAULT;
			String LED_PATTERN_CUSTOM_VALUE_KEY = null;
			String LED_PATTERN_CUSTOM_KEY = null;
			String ICON_ID = null;
			String ICON_DEFAULT = null;
			int icon = 0;
			CharSequence tickerText = null;
			CharSequence contentTitle = null;
			CharSequence contentText = null;
			Intent notificationContentIntent = null;
			PendingIntent contentIntent = null;
			Intent notificationDeleteIntent = null;
			PendingIntent deleteIntent = null;
			String sentFrom = null;
			if(message != null && message.length() > 0){
				message = message.replace("<br/><br/>", " ").replace("<br/>", " ");
			}else{
				message = "";
			}
			//Load values into the preference keys based on the notification type.
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_SMS");
					POPUP_ENABLED_KEY = Constants.SMS_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.SMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.SMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_sms);
					if(sentFromContactName == null){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					contentText = context.getString(R.string.status_bar_notification_content_text_sms, sentFrom, message);
					if(sentFromContactName == null){
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_sms, message);
					}else{
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_sms, sentFromContactName, message);
					}
					//Content Intent
					notificationContentIntent = new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null; //PendingIntent.getService(context, 0, notificationDeleteIntent, 0);
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_MMS");
					POPUP_ENABLED_KEY = Constants.MMS_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.MMS_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.MMS_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_mms);
					if(sentFromContactName == null){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					contentText = context.getString(R.string.status_bar_notification_content_text_mms, sentFrom, message);
					if(sentFromContactName == null){
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_mms, message);
					}else{
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_mms, sentFromContactName, message);
					}
					//Content Intent
					notificationContentIntent = new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null; //PendingIntent.getService(context, 0, notificationDeleteIntent, 0);
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_PHONE");
					POPUP_ENABLED_KEY = Constants.PHONE_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.PHONE_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_phone);
					if(sentFromContactName == null){
						sentFrom = sentFromAddress;
					}else{
						sentFrom = sentFromContactName;
					}
					contentText = context.getString(R.string.status_bar_notification_content_text_phone, sentFrom);
					tickerText = context.getString(R.string.status_bar_notification_ticker_text_phone, sentFrom);
					//Content Intent
					notificationContentIntent =  new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setType("vnd.android.cursor.dir/calls");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null; //PendingIntent.getService(context, 0, notificationDeleteIntent, 0);
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_CALENDAR");
					POPUP_ENABLED_KEY = Constants.CALENDAR_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_calendar);
					contentText = context.getString(R.string.status_bar_notification_content_text_calendar, message);
					tickerText = context.getString(R.string.status_bar_notification_ticker_text_calendar, message);
					//Content Intent
					notificationContentIntent = new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null; //PendingIntent.getService(context, 0, notificationDeleteIntent, 0);
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_GMAIL");
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_TWITTER");
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_FACEBOOK");
		
					break;
				}
			}
			//Notification properties
			Vibrator vibrator = null;
			MediaPlayer mediaPlayer = null;
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			boolean inNormalMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
			boolean inVibrateMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String notificationSound = null;
			boolean soundEnabled = false;
			boolean soundInCallEnabled = false;
			String notificationVibrate = null;
			boolean vibrateEnabled = false;
			boolean vibrateInCallEnabled = false;
			//Check if notifications are enabled or not.
			if(!preferences.getBoolean(ENABLED_KEY, true) && !preferences.getBoolean(POPUP_ENABLED_KEY, true)){
				if (_debug) Log.v("Common.setStatusBarNotification() Notifications Disabled: ENABLED_KEY " + ENABLED_KEY + " - Exiting...");
				return;
			}
			//Sound preferences
			notificationSound = preferences.getString(SOUND_SETTING_KEY, RINGTONE_DEFAULT);
			if(notificationSound != null && !notificationSound.equals("")){
				soundEnabled = true;
			}
			soundInCallEnabled = preferences.getBoolean(IN_CALL_SOUND_ENABLED_KEY, false);
			//Vibrate preferences
			notificationVibrate = preferences.getString(VIBRATE_SETTING_KEY, VIBRATE_ALWAYS_VALUE);
			if(notificationVibrate.equals(VIBRATE_ALWAYS_VALUE)){
				vibrateEnabled = true;
			}else if(notificationVibrate.equals(VIBRATE_WHEN_VIBRATE_MODE_VALUE) && inVibrateMode){
				vibrateEnabled = true;
			}
			vibrateInCallEnabled = preferences.getBoolean(IN_CALL_VIBRATE_ENABLED_KEY, true);
			String vibratePattern = null;
			if(vibrateEnabled){
				vibratePattern = preferences.getString(VIBRATE_PATTERN_KEY, VIBRATE_PATTERN_DEFAULT);
				if(vibratePattern.equals(VIBRATE_PATTERN_CUSTOM_VALUE_KEY)){
					vibratePattern = preferences.getString(VIBRATE_PATTERN_CUSTOM_KEY, VIBRATE_PATTERN_DEFAULT);
				}
			}	
			//LED preferences
			boolean ledEnabled = preferences.getBoolean(LED_ENABLED_KEY, true);
			String ledPattern = null;
			int ledColor = Color.parseColor(LED_COLOR_DEFAULT);
			String ledColorString = null;
			if(ledEnabled){
				//LED Color
				ledColorString = preferences.getString(LED_COLOR_KEY, LED_COLOR_DEFAULT);
				if(ledColorString.equals(LED_COLOR_CUSTOM_VALUE_KEY)){
					ledColorString = preferences.getString(LED_COLOR_CUSTOM_KEY, LED_COLOR_DEFAULT);
				}
				try{
					ledColor = Color.parseColor(ledColorString);
				}catch(Exception ex){
					//Do Nothing
				}
				//LED Pattern
				ledPattern = preferences.getString(LED_PATTERN_KEY, LED_PATTERN_DEFAULT);
				if(ledPattern.equals(LED_PATTERN_CUSTOM_VALUE_KEY)){
					ledPattern = preferences.getString(LED_PATTERN_CUSTOM_KEY, LED_PATTERN_DEFAULT);
				}
			}
			//Set Notification Icon
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_blue")){
						icon = R.drawable.status_bar_notification_sms_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_green")){
						icon = R.drawable.status_bar_notification_sms_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_grey")){
						icon = R.drawable.status_bar_notification_sms_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_orange")){
						icon = R.drawable.status_bar_notification_sms_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_pink")){
						icon = R.drawable.status_bar_notification_sms_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_purple")){
						icon = R.drawable.status_bar_notification_sms_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_red")){
						icon = R.drawable.status_bar_notification_sms_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_grey")){
						icon = R.drawable.status_bar_notification_sms_postcard_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_sms_green;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_blue")){
						icon = R.drawable.status_bar_notification_sms_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_green")){
						icon = R.drawable.status_bar_notification_sms_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_grey")){
						icon = R.drawable.status_bar_notification_sms_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_orange")){
						icon = R.drawable.status_bar_notification_sms_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_pink")){
						icon = R.drawable.status_bar_notification_sms_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_purple")){
						icon = R.drawable.status_bar_notification_sms_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_red")){
						icon = R.drawable.status_bar_notification_sms_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_aqua")){
						icon = R.drawable.status_bar_notification_sms_postcard_aqua;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_blue")){
						icon = R.drawable.status_bar_notification_sms_postcard_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_green")){
						icon = R.drawable.status_bar_notification_sms_postcard_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_grey")){
						icon = R.drawable.status_bar_notification_sms_postcard_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_orange")){
						icon = R.drawable.status_bar_notification_sms_postcard_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_pink")){
						icon = R.drawable.status_bar_notification_sms_postcard_pink;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_purple")){
						icon = R.drawable.status_bar_notification_sms_postcard_purple;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_red")){
						icon = R.drawable.status_bar_notification_sms_postcard_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_white")){
						icon = R.drawable.status_bar_notification_sms_postcard_white;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_postcard_yellow")){
						icon = R.drawable.status_bar_notification_sms_postcard_yellow;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_sms_green;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_black")){
						icon = R.drawable.status_bar_notification_missed_call_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_grey")){
						icon = R.drawable.status_bar_notification_missed_call_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_red")){
						icon = R.drawable.status_bar_notification_missed_call_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_missed_call_white")){
						icon = R.drawable.status_bar_notification_missed_call_white;
					}else {
						//Default Value
						icon = R.drawable.status_bar_notification_missed_call_black;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_black")){
						icon = R.drawable.status_bar_notification_calendar_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_blue")){
						icon = R.drawable.status_bar_notification_calendar_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_green")){
						icon = R.drawable.status_bar_notification_calendar_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_grey")){
						icon = R.drawable.status_bar_notification_calendar_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_orange")){
						icon = R.drawable.status_bar_notification_calendar_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_calendar_red")){
						icon = R.drawable.status_bar_notification_calendar_red;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_calendar_blue;
					}
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
		
					break;
				}
			}
			//Setup the notification
			Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
			//Set notification flags
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			//Setup the notification vibration
			if(vibrateEnabled && callStateIdle){
				long[] vibrationPattern = parseVibratePattern(vibratePattern);
				if(vibrationPattern == null){
					notification.defaults |= Notification.DEFAULT_VIBRATE;
				}else{
					notification.vibrate = vibrationPattern;
				}
			}else if(vibrateEnabled && !callStateIdle && vibrateInCallEnabled && (inVibrateMode || inNormalMode)){
				long[] vibrationPattern = parseVibratePattern(vibratePattern);
				if(vibrationPattern == null){
					//Do Nothing
				}else{
					try{
						vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(vibrationPattern, 0);
					}catch(Exception ex){
						if (_debug) Log.e("Common.setStatusBarNotification() Notification Vibrator ERROR: " + ex.toString());
					}
				}
			}
			//Setup the notification sound
			notification.audioStreamType = Notification.STREAM_DEFAULT;
			if(soundEnabled && callStateIdle){
				try{
					notification.sound = Uri.parse(notificationSound);
				}catch(Exception ex){
					if (_debug) Log.e("Common.setStatusBarNotification() Notification Sound Set ERROR: " + ex.toString());
					notification.defaults |= Notification.DEFAULT_SOUND;
				}
			}else if(soundEnabled && !callStateIdle && soundInCallEnabled && inNormalMode){
				try{
					mediaPlayer = MediaPlayer.create(context,  Uri.parse(notificationSound));
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}catch(Exception ex){
					if (_debug) Log.e("Common.setStatusBarNotification() Notification Sound Play ERROR: " + ex.toString());
				}
			}
			//Setup the notification LED lights
			if(ledEnabled){
				notification.flags |= Notification.FLAG_SHOW_LIGHTS;
				try{
					int[] ledPatternArray = parseLEDPattern(ledPattern);
					if(ledPatternArray == null){
						notification.defaults |= Notification.DEFAULT_LIGHTS;
					}else{
						//LED Color
				        notification.ledARGB = ledColor;
						//LED Pattern
						notification.ledOnMS = ledPatternArray[0];
				        notification.ledOffMS = ledPatternArray[1];
					}
				}catch(Exception ex){
					notification.defaults |= Notification.DEFAULT_LIGHTS;
				}
			}
			//Set notification intent values
			notification.deleteIntent = deleteIntent;
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(notificationType, notification);
			//Remove the stock status bar notification.
			if(notificationType == Constants.NOTIFICATION_TYPE_PHONE){
				cancelStockMissedCallNotification();
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.setStatusBarNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Clear the status bar notification if there are no more notifications of this type displayed.
	 * 
	 * @param context - The application context.
	 * @param notificationViewFlipper - The notification ViewFlipper.
	 * @param notificationType - The notification type.
	 * @param totalNotifications - The total number of current notifications.
	 */
	public static void clearNotifications(Context context, NotificationViewFlipper notificationViewFlipper, int notificationType, int totalNotifications){
		if(totalNotifications > 0){
			if(!notificationViewFlipper.containsNotificationType(notificationType)){
				removeStatusBarNotification(context, notificationType);
			}
		}else{
			removeStatusBarNotification(context, notificationType);
		}
	}
	
	/**
	 * Clear all status bar notifications.
	 * 
	 * @param context - The application context.
	 */
	public static void clearAllNotifications(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearAllNotifications()");
		try{
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancelAll();
		}catch(Exception ex){
			if (_debug) Log.e("Common.clearAllNotifications() ERROR: " + ex.toString());
		}
	}

	/**
	 * Function to query the sms inbox and check for any new messages.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the sms information.
	 */
	public static ArrayList<String> getSMSMessagesFromDisk(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getSMSMessagesFromDisk()");
		ArrayList<String> smsArray = new ArrayList<String>();
		final String[] projection = new String[] { "_id", "thread_id", "address", "person", "date", "body"};
		final String selection = "read = 0";
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
		    	long messageID = cursor.getLong(cursor.getColumnIndex("_id"));
		    	long threadID = cursor.getLong(cursor.getColumnIndex("thread_id"));
		    	String messageBody = cursor.getString(cursor.getColumnIndex("body"));
		    	String sentFromAddress = cursor.getString(cursor.getColumnIndex("address"));
	            if(sentFromAddress.contains("@")){
	            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
	            }
		    	long timeStamp = cursor.getLong(cursor.getColumnIndex("date"));
	    		String[] smsContactInfo = null;
	    		if(sentFromAddress.contains("@")){
		    		smsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
		    	}else{
		    		smsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
		    	}
	    		if(smsContactInfo == null){
					smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
				}else{
					smsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2] + "|" + smsContactInfo[3]);
				}
		    	break;
		    }
		}catch(Exception ex){
			if (_debug) Log.e("Common.getSMSMessagesFromDisk() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return smsArray;	
	}
	
	/**
	 * Parse the incoming SMS message directly.
	 * 
	 * @param context - The application context.
	 * @param bundle - Bundle from the incoming intent.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the sms information.
	 */
	public static ArrayList<String> getSMSMessagesFromIntent(Context context, Bundle bundle){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getSMSMessagesFromIntent()");
		ArrayList<String> smsArray = new ArrayList<String>();
    	long timeStamp = 0;
    	String sentFromAddress = null;
    	String messageBody = null;
    	StringBuilder messageBodyBuilder = null;
    	String messageSubject = null;
    	long threadID = 0;
    	long messageID = 0;
		try{
			SmsMessage[] msgs = null;
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
            }
            SmsMessage sms = msgs[0];
            timeStamp = sms.getTimestampMillis();
            //Adjust the timestamp to the localized time of the users phone.
            timeStamp = Common.convertGMTToLocalTime(context, timeStamp);
            sentFromAddress = sms.getDisplayOriginatingAddress().toLowerCase();
            if(sentFromAddress.contains("@")){
            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
            }
            messageSubject = sms.getPseudoSubject();
            messageBodyBuilder = new StringBuilder();
            //Get the entire message body from the new message.
    		  int messagesLength = msgs.length;
            for (int i = 0; i < messagesLength; i++){                
            	//messageBody.append(msgs[i].getMessageBody().toString());
            	messageBodyBuilder.append(msgs[i].getDisplayMessageBody().toString());
            }   
            messageBody = messageBodyBuilder.toString();
            if(messageBody.startsWith(sentFromAddress)){
            	messageBody = messageBody.substring(sentFromAddress.length()).replace("\n", "<br/>").trim();
            }    
            if(messageSubject != null && !messageSubject.equals("")){
				messageBody = "(" + messageSubject + ")" + messageBody.replace("\n", "<br/>").trim();
			}else{
				messageBody = messageBody.replace("\n", "<br/>").trim();
			}   
    		threadID = Common.getThreadID(context, sentFromAddress, Constants.NOTIFICATION_TYPE_SMS);
    		messageID = Common.getMessageID(context, threadID, messageBody, timeStamp, Constants.NOTIFICATION_TYPE_SMS);
    		String[] smsContactInfo = null;
    		if(sentFromAddress.contains("@")){
	    		smsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
	    	}else{
	    		smsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
	    	}
    		if(smsContactInfo == null){
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp);
			}else{
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2] + "|" + smsContactInfo[3]);
			}
    		return smsArray;
		}catch(Exception ex){
			if (_debug) Log.v("Common.getSMSMessagesFromIntent() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Function to query the mms inbox and check for any new messages.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the mms information.
	 */
	public static ArrayList<String> getMMSMessagesFromDisk(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getMMSMessagesFromDisk()");
		ArrayList<String> mmsArray = new ArrayList<String>();
		final String[] projection = new String[] {"_id", "thread_id", "date"};
		final String selection = "read = 0";
		final String[] selectionArgs = null;
		final String sortOrder = "date DESC";
		Cursor cursor = null;
        try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://mms/inbox"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) {		    	
	    		String messageID = cursor.getString(cursor.getColumnIndex("_id"));
	    		String threadID = cursor.getString(cursor.getColumnIndex("thread_id"));
		    	String timeStamp = cursor.getString(cursor.getColumnIndex("date"));
		    	String sentFromAddress = Common.getMMSAddress(context, messageID);
		    	if(sentFromAddress.contains("@")){
	            	sentFromAddress = Common.removeEmailFormatting(sentFromAddress);
	            }
		    	String messageBody = Common.getMMSText(context, messageID);
		    	String[] mmsContactInfo = null;
		    	if(sentFromAddress.contains("@")){
		    		mmsContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
		    	}else{
		    		mmsContactInfo = Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
		    	}
				if(mmsContactInfo == null){
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
				}else{
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + mmsContactInfo[0] + "|" + mmsContactInfo[1] + "|" + mmsContactInfo[2] + "|" + mmsContactInfo[3]);
				}
		    	break;
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("Common.getMMSMessagesFromDisk() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return mmsArray;	
	}
	
	/**
	 * Function to query the call log and check for any missed calls.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the missed call information.
	 */
	public static ArrayList<String> getMissedCalls(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getMissedCalls()");
		Boolean missedCallFound = false;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String missedCallPreference = preferences.getString(Constants.PHONE_DISMISS_BUTTON_ACTION_KEY, "0");
		ArrayList<String> missedCallsArray = new ArrayList<String>();
		final String[] projection = null;
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Cursor cursor = null;
		try{
		    cursor = context.getContentResolver().query(
		    		Uri.parse("content://call_log/calls"),
		    		projection,
		    		selection,
					selectionArgs,
					sortOrder);
	    	while (cursor.moveToNext()) { 
	    		String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
	    		String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
	    		String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
	    		String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
	    		String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
	    		if(Integer.parseInt(callType) == Constants.PHONE_TYPE && Integer.parseInt(isCallNew) > 0){
    				if (_debug) Log.v("Common.getMissedCalls() Missed Call Found: " + callNumber);
    				String[] missedCallContactInfo = null;
    				if(Common.isPrivateUnknownNumber(callNumber)){
    					if (_debug) Log.v("Common.getMissedCalls() Is a private or unknown number.");
    				}else{
    					missedCallContactInfo = Common.getContactsInfoByPhoneNumber(context, callNumber);
    				}
    				if(missedCallContactInfo == null){
    					missedCallsArray.add(callLogID + "|" + callNumber + "|" + callDate);
    				}else{
    					missedCallsArray.add(callLogID + "|" + callNumber + "|" + callDate + "|" + missedCallContactInfo[0] + "|" + missedCallContactInfo[1] + "|" + missedCallContactInfo[2] + "|" + missedCallContactInfo[3]);
    				}
    				if(missedCallPreference.equals(Constants.PHONE_GET_LATEST)){
    					if (_debug) Log.v("Common.getMissedCalls() Missed call found - Exiting");
    					break;
    				}
    				missedCallFound = true;
    			}else{
    				if(missedCallPreference.equals(Constants.PHONE_GET_RECENT)){
    					if (_debug) Log.v("Common.getMissedCalls() Found first non-missed call - Exiting");
    					break;
    				}
    			}
	    		if(!missedCallFound){
	    			if (_debug) Log.v("Common.getMissedCalls() Missed call not found - Exiting");
	    			break;
	    		}
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("Common.getMissedCalls() ERROR: " + ex.toString());
		}finally{
			cursor.close();
		}
	    return missedCallsArray;
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
//		    	if(cursor != null){
//					cursor.close();
//				}
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
	 * Remove a particular status bar notification.
	 * 
	 * @param context - The application context.
	 * @param notificationType - The notification type.
	 */
	private static void removeStatusBarNotification(Context context, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeStatusBarNotification()");
		try{
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			switch(notificationType){
				case Constants.NOTIFICATION_TYPE_SMS:{
					
					break;
				}
				case Constants.NOTIFICATION_TYPE_MMS:{
	
					break;
				}
				case Constants.NOTIFICATION_TYPE_PHONE:{
					
					break;
				}
				case Constants.NOTIFICATION_TYPE_CALENDAR:{
	
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
	
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
	
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
	
					break;
				}
			}
			notificationManager.cancel(notificationType);
		}catch(Exception ex){
			if (_debug) Log.e("Common.removeStatusBarNotification() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Read the message text of the MMS message.
	 * 
	 * @param messageID - The MMS message ID.
	 * 
	 * @return String - The message text of the MMS message.
	 */
	private static String getMMSTextFromPart(Context context, String messageID) {
		if (_debug) Log.v("Common.getMMSTextFromPart()");
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
	    	if (_debug) Log.e("Common.getMMSTextFromPart() ERROR: " + ex.toString());
	    }finally {
	    	try{
	    		inputStream.close();
	    	}catch(Exception ex){
	    		if (_debug) Log.e("Common.getMMSTextFromPart() ERROR: " + ex.toString());
	    	}
	    }
	    return messageText.toString();
	}
	
	/**
	 * Compares the two strings. 
	 * If the second string is larger and ends with the first string, return true.
	 * If the first string is larger and ends with the second string, return true.
	 * 
	 * @param contactNumber - The address books phone number.
	 * @param incomingNumber - The incoming phone number.
	 * 
	 * @return - boolean - 	 If the second string is larger ends with the first string, return true.
	 *                       If the first string is larger ends with the second string, return true.
	 */
	private static boolean isPhoneNumberEqual(String contactNumber, String incomingNumber){
		if (_debug) Log.v("Common.isPhoneNumberEqual()");
		//Remove any formatting from each number.
		contactNumber = removeFormatting(contactNumber);
		incomingNumber = removeFormatting(incomingNumber);
		//Remove any leading zero's from each number.
		contactNumber = removeLeadingZero(contactNumber);
		incomingNumber = removeLeadingZero(incomingNumber);	
		int contactNumberLength = contactNumber.length();
		int incomingNumberLength = incomingNumber.length();
		//Iterate through the ends of both strings...backwards from the end of the string.
		if(contactNumberLength <= incomingNumberLength){
			for(int i = 0; i < contactNumberLength; i++){
				if(contactNumber.charAt(contactNumberLength - 1 - i) != incomingNumber.charAt(incomingNumberLength - 1 - i)){
					return false;
				}
			}
		}else{
			for(int i = incomingNumberLength - 1; i >= 0 ; i--){
				if(contactNumber.charAt(contactNumberLength - 1 - i) != incomingNumber.charAt(incomingNumberLength - 1 - i)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove the leading zero from a string.
	 * 
	 * @param inputNumber - The number to remove the leading zero from.
	 * 
	 * @return String - The number after we have removed the leading zero.
	 */
	private static String removeLeadingZero(String inputNumber){
		if (_debug) Log.v("Common.removeLeadingZero() InputNumber: " + inputNumber);
		if(inputNumber.subSequence(0, 1).equals("0")){
			return inputNumber.substring(1);
		}
		return inputNumber;
	}
	
	/**
	 * Parse a vibration pattern.
	 * 
	 * @param vibratePattern - The vibrate pattern to verify.
	 * 
	 * @return boolean - Returns True if the vibrate pattern is valid.
	 */
	private static long[] parseVibratePattern(String vibratePattern){
		if (_debug) Log.v("Common.parseVibratePattern()");
	    final int VIBRATE_PATTERN_MAX_LENGTH = 60000;
	    final int VIBRATE_PATTERN_MAX_SIZE = 100;
		ArrayList<Long> vibratePatternArrayList = new ArrayList<Long>();
		long[] vibratePatternArray = null;
		String[] vibratePatternStringArray = vibratePattern.split(",");
		int arraySize = vibratePatternStringArray.length;
	    for (int i = 0; i < arraySize; i++) {
	    	long vibrateLength = 0;
	    	try {
	    		vibrateLength = Long.parseLong(vibratePatternStringArray[i].trim());
	    	} catch (Exception ex) {
	    		if (_debug) Log.e("Common.parseVibratePattern() ERROR: " + ex.toString());
	    		return null;
	    	}
	    	if(vibrateLength < 0){
	    		vibrateLength = 0;
	    	}
	    	if(vibrateLength > VIBRATE_PATTERN_MAX_LENGTH){
	    		vibrateLength = VIBRATE_PATTERN_MAX_LENGTH;
	    	}
	    	vibratePatternArrayList.add(vibrateLength);
	    }
	    arraySize = vibratePatternArrayList.size();
	    if (arraySize > VIBRATE_PATTERN_MAX_SIZE){
	    	arraySize = VIBRATE_PATTERN_MAX_SIZE;
	    }
	    vibratePatternArray = new long[arraySize];
	    for (int i = 0; i < arraySize; i++) {
	    	vibratePatternArray[i] = vibratePatternArrayList.get(i);
	    }
		return vibratePatternArray;
	}
	
	/**
	 * Parse an led blink pattern.
	 * 
	 * @param ledPattern - The blink pattern to verify.
	 * 
	 * @return boolean - Returns True if the blink pattern is valid.
	 */
	private static int[] parseLEDPattern(String ledPattern){
		if (_debug) Log.v("Common.parseLEDPattern()");
	    final int LED_PATTERN_MAX_LENGTH = 60000;
		int[] ledPatternArray = {0, 0};
		String[] ledPatternStringArray = ledPattern.split(",");
		if(ledPatternStringArray.length != 2){
			return null;
		}
	    for (int i = 0; i < 2; i++) {
	    	int blinkLength = 0;
	    	try {
	    		blinkLength = Integer.parseInt(ledPatternStringArray[i].trim());
	    	} catch (Exception ex) {
	    		if (_debug) Log.e("Common.parseLEDPattern() ERROR: " + ex.toString());
	    		return null;
	    	}
	    	if(blinkLength < 0){
	    		blinkLength = 0;
	    	}
	    	if(blinkLength > LED_PATTERN_MAX_LENGTH){
	    		blinkLength = LED_PATTERN_MAX_LENGTH;
	    	}
	    	ledPatternArray[i] = blinkLength;
	    }
		return ledPatternArray;
	}
	
}