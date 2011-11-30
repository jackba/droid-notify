package apps.droidnotify.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import oauth.signpost.OAuth;
import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.widget.Toast;

import apps.droidnotify.NotificationActivity;
import apps.droidnotify.NotificationViewFlipper;
import apps.droidnotify.QuickReplyActivity;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.CalendarAlarmReceiver;
import apps.droidnotify.receivers.RescheduleReceiver;
import apps.droidnotify.receivers.TwitterAlarmReceiver;
import apps.droidnotify.R;

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
	private static Context _context = null; 
	private static PowerManager.WakeLock _partialWakeLock = null;
	private static PowerManager.WakeLock _wakeLock = null;
	private static KeyguardLock _keyguardLock = null;
	
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
		if (_debug) Log.v("Common.getContactsInfoByPhoneNumber()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
		boolean _contactExists = false;
		if (incomingNumber == null) {
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Phone number provided is null: Exiting...");
			return null;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Phone number provided appears to be an email address: Exiting...");
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
			if (_debug) Log.v("Common.getContactsInfoByPhoneNumber() Searching Contacts");
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
			if (_debug) Log.e("Common.getContactsInfoByPhoneNumber() ERROR: " + ex.toString());
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
		if (_debug) Log.v("Common.getContactsInfoByEmail()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
		boolean _contactExists = false;
		if (incomingEmail == null) {
			if (_debug) Log.v("Common.getContactsInfoByEmail() Email provided is null: Exiting...");
			return null;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("Common.getContactsInfoByEmail() Email provided does not appear to be a valid email address: Exiting...");
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
			if (_debug) Log.v("Common.getContactsInfoByEmail() Searching contacts");
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
			if (_debug) Log.e("Common.getContactsInfoByEmail() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Load the various contact info for this notification from a phoneNumber.
	 * 
	 * @param context - Application Context.
	 * @param twitterID - The twitter ID of the person we are searching for.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	public static String[] getContactsInfoByTwitterID(Context context, long twitterID){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getContactsInfoByTwitterID()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
		if (twitterID == 0) {
			if (_debug) Log.v("Common.getContactsInfoByTwitterID() Twitter ID provided is 0. Exiting...");
			return null;
		}
		Twitter twitter = getTwitter(context);
		if(twitter == null){
			if (_debug) Log.v("Common.getContactsInfoByTwitterID() Twitter object is null. Exiting...");
			return null;
		}
		try{
			User twitterUser = twitter.showUser(twitterID);
			String twitterName = twitterUser.getName();
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
			if (_debug) Log.v("Common.getContactsInfoByTwitterID() Searching Contacts");
			while (cursor.moveToNext()) { 
				String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)); 
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)); 
				if(contactName.equals(twitterName)){
					_contactID = Long.parseLong(contactID);
					_contactName = contactName;
					_photoID = Long.parseLong(photoID);
					_lookupKey = lookupKey;					
					break;
				}
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, String.valueOf(_photoID), _lookupKey};
		}catch(Exception ex){
			if (_debug) Log.e("Common.getContactsInfoByTwitterID() ERROR: " + ex.toString());
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
	public static String[] getContactsInfoByName(Context context, String incomingName){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getContactsInfoByName()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		String _lookupKey = "";
		String _contactEmail = "";
		boolean _contactExists = false;
		if (incomingName == null) {
			if (_debug) Log.v("Common.getContactsInfoByName() Name provided is null: Exiting...");
			return null;
		}
		String contactID = null;
		try{
			final String[] projection = null;
			final String selection = ContactsContract.Contacts.DISPLAY_NAME + " = " + DatabaseUtils.sqlEscapeString(incomingName);
			final String[] selectionArgs = null;
			final String sortOrder = null;
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					projection, 
					selection, 
					selectionArgs, 
					sortOrder);
			if (_debug) Log.v("Common.getContactsInfoByName() Searching contacts");
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
                if(emailCursor.moveToFirst()) {
                	String contactEmail = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                	if(contactEmail != null){
                		_contactEmail = contactEmail;
                	}
					_contactID = Long.parseLong(contactID);
	    		  	if(contactName != null){
	    		  		_contactName = contactName;
	    		  	}
	    		  	if(photoID != null){
	    			  	_photoID = Long.parseLong(photoID);
	    		  	}
	    		  	_lookupKey = lookupKey;
	  		      	_contactExists = true;
                }
                emailCursor.close();
                if(_contactExists) break;
		   	}
			cursor.close();
			return new String[]{String.valueOf(_contactID), _contactName, _contactEmail, String.valueOf(_photoID), _lookupKey};
		}catch(Exception ex){
			if (_debug) Log.e("Common.getContactsInfoByName() ERROR: " + ex.toString());
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
			//messageURI = "content://mms/inbox";
			messageURI = "content://sms/inbox";
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
		}else if(messageType == Constants.MESSAGE_TYPE_MMS){
			//messageURI = "content://mms/inbox";
			messageURI = "content://sms/inbox";
		}else{
			if (_debug) Log.v("Common.getMessageID() Non SMS/MMS Message Type: Exiting...");
			return 0;
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
				setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNumber));
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
		    return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.makePhoneCall() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_phone_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	public static boolean startMessagingQuickReplyActivity(Context context, NotificationActivity notificationActivity, String phoneNumber, String contactName, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startMessagingQuickReplyActivity()");
		if(phoneNumber == null){
			Toast.makeText(context, context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
			return false;
		}
		try{
			Intent intent = new Intent(context, QuickReplyActivity.class);
	        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
		    intent.putExtra("smsPhoneNumber", phoneNumber);
		    if(contactName != null && !contactName.equals( context.getString(android.R.string.unknownName))){
		    	intent.putExtra("smsName", contactName);
		    }else{
		    	intent.putExtra("smsName", "");
		    }
		    intent.putExtra("smsMessage", "");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingQuickReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppViewThreadActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}

	/**
	 * Start the intent for any android messaging application to view the messaging inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startMessagingAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startCallLogViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_call_log_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactViewActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_MAIN); 
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.LaunchActivity"));
		        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				if (_debug) Log.e("Common.startViewCalendarActivity() ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception e){
			if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + e.toString());
			try{
				//HTC Sense UI calendar app.
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setComponent(new ComponentName("com.htc.calendar", "com.htc.calendar.EditEvent"));
		        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				notificationActivity.startActivityForResult(intent, requestCode);
				setInLinkedAppFlag(context, true);
				return true;
			}catch(Exception ex){
				if (_debug) Log.e("Common.startAddCalendarEventActivity ERROR: " + ex.toString());
				Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
				setInLinkedAppFlag(context, false);
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
				setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startViewCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
				setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));	
			intent.putExtra(Constants.CALENDAR_EVENT_BEGIN_TIME, calendarEventStartTime);
			intent.putExtra(Constants.CALENDAR_EVENT_END_TIME, calendarEventEndTime);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
			setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startEditCalendarEventActivity ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_calendar_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
				setInLinkedAppFlag(context, false);
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_EDIT);
			Uri viewContactURI = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactID));
		    intent.setData(viewContactURI);	
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactEditActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		    notificationActivity.startActivityForResult(intent, requestCode);
		    setInLinkedAppFlag(context, true);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startContactAddActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_contacts_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}

	/**
	 * Start the intent for any K9 email application to view the email inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9EmailAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startK9EmailAppViewInboxActivity()");
		try{
	        Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setComponent(new ComponentName("com.fsck.k9", "com.fsck.k9.activity.Accounts"));
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startK9EmailAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any android K9 email application to send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param k9EmailURI - The k9 email uri that is built for the k-9 clients.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startK9MailAppReplyActivity(Context context, NotificationActivity notificationActivity, String k9EmailUri, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startK9MailAppReplyActivity()");
		if(k9EmailUri == null || k9EmailUri.equals("")){
			Toast.makeText(context, context.getString(R.string.app_reply_email_address_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
		    intent.setData(Uri.parse(k9EmailUri));
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startK9MailAppReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_email_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any Twitter application to view the direct message inbox.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startTwitterAppViewInboxActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startTwitterAppViewInboxActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_SEND); 
			//intent.putExtra(Intent.EXTRA_TEXT, ""); 
			//intent.setType("application/twitter");
			intent.setType("text/plain");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode);
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startTwitterAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_twitter_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any android messaging application to send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startTwitterAppReplyActivity(Context context, NotificationActivity notificationActivity, int requestCode){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startTwitterAppReplyActivity()");
		try{
			Intent intent = new Intent(Intent.ACTION_SEND); 
			//intent.putExtra(Intent.EXTRA_TEXT, ""); 
			//intent.setType("application/twitter");
			intent.setType("text/plain");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			notificationActivity.startActivityForResult(intent, requestCode); 
	        return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.startTwitterAppReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_twitter_app_error), Toast.LENGTH_LONG).show();
			setInLinkedAppFlag(context, false);
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
	    	String runningTaskClassName = runningTaskComponent.getClassName();
	        if (_debug) Log.v("Common.isBlockingAppRunning() runningTaskPackageName: " + runningTaskPackageName + " runningTaskClassName: " + runningTaskClassName);
	        int messagingPackageNamesArraySize = Constants.BLOCKED_PACKAGE_NAMES_ARRAY.length;
	        for(int i = 0; i < messagingPackageNamesArraySize; i++){
	        	if (_debug) Log.v("Common.isBlockingAppRunning() Checking BLOCKED_PACKAGE_NAMES_ARRAY[i]: " + Constants.BLOCKED_PACKAGE_NAMES_ARRAY[i]);
	        	String[] blockedInfoArray = Constants.BLOCKED_PACKAGE_NAMES_ARRAY[i].split(",");
		        if(blockedInfoArray[0].equals(runningTaskPackageName)){
		        	if(blockedInfoArray.length > 1){
		        		if(blockedInfoArray[1].equals(runningTaskClassName)){
		        			return true;
		        		}
		        	}else{
		        		return true;
		        	}
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
	public static void setStatusBarNotification(Context context, int notificationType, boolean callStateIdle, String sentFromContactName, String sentFromAddress, String message, String k9EmailUri){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setStatusBarNotification() sentFromContactName: " + sentFromContactName + " sentFromAddress: " + sentFromAddress + " message: " + message);
		try{
			_context = context;
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Stop if app is disabled.
			if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("Common.setStatusBarNotification() App Disabled. Exiting...");
				return;
			}
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
				message = message.replace("<br/><br/>", " ").replace("<br/>", " ")
						.replace("<b>", "").replace("</b>", "")
						.replace("<i>", "").replace("</i>", "")
						.replace("<u>", "").replace("</u>", "");
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
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = Common.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_sms_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_sms_null);
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_MAIN);
						notificationContentIntent.setType("vnd.android-dir/mms-sms");
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_sms, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_sms, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_sms, sentFromContactName, message);
						}
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_VIEW);
						notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
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
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = Common.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_mms_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_mms_null);
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_MAIN);
						notificationContentIntent.setType("vnd.android-dir/mms-sms");
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_mms, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_mms, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_mms, sentFromContactName, message);
						}
						//Content Intent
						notificationContentIntent = new Intent(Intent.ACTION_VIEW);
						notificationContentIntent.setData(Uri.parse("smsto:" + sentFromAddress));
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
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
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = Common.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if(sentFrom == null || sentFrom.equals("")){
						contentText = context.getString(R.string.status_bar_notification_content_text_phone_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_phone_null);
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_phone, sentFrom);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_phone, sentFrom);
					}
					//Content Intent
					notificationContentIntent =  new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setType("vnd.android.cursor.dir/calls");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
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
					if(message == null || message.equals("")){
						contentText = context.getString(R.string.status_bar_notification_content_text_calendar_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_calendar_null);
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_calendar, message);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_calendar, message);
					}
					//Content Intent
					notificationContentIntent = new Intent(Intent.ACTION_VIEW);
					notificationContentIntent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity");
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_GMAIL:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_GMAIL");
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_TWITTER:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_TWITTER");
					POPUP_ENABLED_KEY = Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_email);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = Common.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_email_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_email_null);
						//Content Intent
						notificationContentIntent = null;
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_email, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_email, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_email, sentFromContactName, message);
						}
						//Content Intent
						//if(k9EmailUri!= null){
						//	notificationContentIntent = new Intent(Intent.ACTION_VIEW);
						//	notificationContentIntent.setData(Uri.parse(k9EmailUri));
						//}else{
							notificationContentIntent = null;
						//}
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
				case Constants.NOTIFICATION_TYPE_FACEBOOK:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_FACEBOOK");
		
					break;
				}
				case Constants.NOTIFICATION_TYPE_K9:{
					if (_debug) Log.v("Common.setStatusBarNotification() NOTIFICATION_TYPE_K9");
					POPUP_ENABLED_KEY = Constants.K9_NOTIFICATIONS_ENABLED_KEY;
					ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_ENABLED_KEY;
					SOUND_SETTING_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_SOUND_SETTING_KEY;
					IN_CALL_SOUND_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_SOUND_ENABLED_KEY;
					VIBRATE_SETTING_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_SETTING_KEY;
					IN_CALL_VIBRATE_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_IN_CALL_VIBRATE_ENABLED_KEY;
					VIBRATE_PATTERN_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_KEY;
					VIBRATE_PATTERN_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_VALUE_KEY;
					VIBRATE_PATTERN_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_VIBRATE_PATTERN_CUSTOM_KEY;
					LED_ENABLED_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_ENABLED_KEY;
					LED_COLOR_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_KEY;
					LED_COLOR_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_VALUE_KEY;
					LED_COLOR_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_COLOR_CUSTOM_KEY;
					LED_PATTERN_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_KEY;
					LED_PATTERN_CUSTOM_VALUE_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_VALUE_KEY;
					LED_PATTERN_CUSTOM_KEY = Constants.K9_STATUS_BAR_NOTIFICATIONS_LED_PATTERN_CUSTOM_KEY;
					ICON_ID = Constants.K9_STATUS_BAR_NOTIFICATIONS_ICON_SETTING_KEY;
					ICON_DEFAULT = Constants.K9_STATUS_BAR_NOTIFICATIONS_ICON_DEFAULT;
					contentTitle = context.getText(R.string.status_bar_notification_content_title_text_email);
					if(sentFromContactName == null || sentFromContactName.equals("")){
						sentFrom = Common.formatPhoneNumber(context, sentFromAddress);
					}else{
						sentFrom = sentFromContactName;
					}
					if( (sentFrom == null || sentFrom.equals("")) && (message == null || message.equals("")) ){
						contentText = context.getString(R.string.status_bar_notification_content_text_email_null);
						tickerText = context.getString(R.string.status_bar_notification_ticker_text_email_null);
						//Content Intent
						notificationContentIntent = null;
						//For now, don't display empty status bar notifications.
						return;
					}else{
						contentText = context.getString(R.string.status_bar_notification_content_text_email, sentFrom, message);
						if(sentFromContactName == null || sentFromContactName.equals("")){
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_unknown_contact_email, message);
						}else{
							tickerText = context.getString(R.string.status_bar_notification_ticker_text_email, sentFromContactName, message);
						}
						//Content Intent
						if(k9EmailUri!= null){
							notificationContentIntent = new Intent(Intent.ACTION_VIEW);
							notificationContentIntent.setData(Uri.parse(k9EmailUri));
						}else{
							notificationContentIntent = null;
						}
					}
					//Delete Intent
					notificationDeleteIntent = null;
					//Content Intent
					contentIntent = PendingIntent.getActivity(context, 0, notificationContentIntent, 0);
					//Delete Intent
					deleteIntent = null;
					break;
				}
			}
			//Notification properties
			Vibrator vibrator = null;
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			boolean inNormalMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
			boolean inVibrateMode = audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
			String notificationSound = null;
			boolean soundEnabled = false;
			boolean soundInCallEnabled = false;
			String notificationVibrate = null;
			boolean vibrateEnabled = false;
			boolean vibrateInCallEnabled = false;
			//Check if notifications are enabled or not.
			if(!preferences.getBoolean(ENABLED_KEY, true) || !preferences.getBoolean(POPUP_ENABLED_KEY, true)){
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
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_white")){
							icon = R.drawable.status_bar_notification_sms_white;
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
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_sms_white")){
						icon = R.drawable.status_bar_notification_sms_white;
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
					}else{
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
				case Constants.NOTIFICATION_TYPE_K9:{
					if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_black")){
						icon = R.drawable.status_bar_notification_email_black;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_blue")){
						icon = R.drawable.status_bar_notification_email_blue;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_green")){
						icon = R.drawable.status_bar_notification_email_green;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_grey")){
						icon = R.drawable.status_bar_notification_email_grey;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_orange")){
						icon = R.drawable.status_bar_notification_email_orange;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_red")){
						icon = R.drawable.status_bar_notification_email_red;
					}else if(preferences.getString(ICON_ID, ICON_DEFAULT).equals("status_bar_notification_email_white")){
						icon = R.drawable.status_bar_notification_email_white;
					}else{
						//Default Value
						icon = R.drawable.status_bar_notification_email_white;
					}
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
						vibrator.vibrate(vibrationPattern, -1);
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
					new playNotificationMediaFileAsyncTask().execute(notificationSound);
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
	public static void clearNotification(Context context, NotificationViewFlipper notificationViewFlipper, int notificationType, int totalNotifications){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearNotification()");
		try{
			if(totalNotifications > 0){
				if(!notificationViewFlipper.containsNotificationType(notificationType)){
					removeStatusBarNotification(context, notificationType);
				}
			}else{
				removeStatusBarNotification(context, notificationType);
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.clearNotification() ERROR: " + ex.toString());
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
				messageBody = "<b>" + messageSubject + "</b><br/>" + messageBody.replace("\n", "<br/>").trim();
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
			if (_debug) Log.e("Common.getSMSMessagesFromIntent() ERROR: " + ex.toString());
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
		}finally{
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

	/**
	 * Parse the incoming SMS message directly.
	 * 
	 * @param context - The application context.
	 * @param bundle - Bundle from the incoming intent.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the K9 information.
	 */
	public static ArrayList<String> getK9MessagesFromIntent(Context context, Bundle bundle){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getK9MessagesFromIntent()");
		ArrayList<String> k9Array = new ArrayList<String>();
    	long timeStamp = 0;
    	String sentFromAddress = null;
    	String messageBody = null;
    	String messageSubject = null;
    	long messageID = 0;
		String k9EmailUri = null;
		String k9EmailDelUri = null;
		try{
            Date sentDate = (Date) bundle.get("com.fsck.k9.intent.extra.SENT_DATE");
			timeStamp = sentDate.getTime();
            messageSubject = bundle.getString("com.fsck.k9.intent.extra.SUBJECT").toLowerCase();
            sentFromAddress = parseFromEmailAddress(bundle.getString("com.fsck.k9.intent.extra.FROM").toLowerCase());
            if (_debug) Log.v("Common.getK9MessagesFromIntent() sentFromAddress: " + sentFromAddress);
            //Get the message body.
    		final String[] projection = new String[] {"_id", "date", "sender", "subject", "preview", "account", "uri", "delUri"};
            final String selection = "date = " + timeStamp;
    		final String[] selectionArgs = null;
    		final String sortOrder = null;
    		Cursor cursor = null;
            try{
    		    cursor = context.getContentResolver().query(
    		    		Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages/"),
    		    		projection,
    		    		selection,
    					selectionArgs,
    					sortOrder);
    	    	if(cursor.moveToFirst()){
		    		messageID = Long.parseLong(cursor.getString(cursor.getColumnIndex("_id")));
		    		messageBody = cursor.getString(cursor.getColumnIndex("preview"));
		    		k9EmailUri = cursor.getString(cursor.getColumnIndex("uri"));
		    		k9EmailDelUri = cursor.getString(cursor.getColumnIndex("delUri"));
    	    	}else{
    	    		if (_debug) Log.v("Common.getK9MessagesFromIntent() No Email Found Matching Criteria!");
    	    	}
    		}catch(Exception ex){
    			if (_debug) Log.e("Common.getK9MessagesFromIntent() CURSOR ERROR: " + ex.toString());
    		}finally{
        		cursor.close();
    		}
            if(messageSubject != null && !messageSubject.equals("")){
				messageBody = "<b>" + messageSubject + "</b><br/>" + messageBody.replace("\n", "<br/>").trim();
			}else{
				messageBody = messageBody.replace("\n", "<br/>").trim();
			}
    		String[] k9ContactInfo = null;
    		k9ContactInfo = Common.getContactsInfoByEmail(context, sentFromAddress);
    		if(k9ContactInfo == null){
				k9Array.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + timeStamp + "|" + k9EmailUri + "|" + k9EmailDelUri);
			}else{
				k9Array.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + timeStamp + "|" + k9EmailUri + "|" + k9EmailDelUri + "|" + k9ContactInfo[0] + "|" + k9ContactInfo[1] + "|" + k9ContactInfo[2] + "|" + k9ContactInfo[3]);
			}
    		return k9Array;
		}catch(Exception ex){
			if (_debug) Log.e("Common.getK9MessagesFromIntent() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Process the Twitter notifications. Read account and notify as needed.
	 * 
	 * @param context - The application context.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the Twitter information.
	 */
	public static ArrayList<String> getTwitterDirectMessages(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getTwitterDirectMessages()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(preferences.getBoolean(Constants.TWITTER_DIRECT_MESSAGES_ENABLED_KEY, true)){
				Twitter twitter = getTwitter(context);
				if(twitter == null){
					if (_debug) Log.v("Common.getTwitterDirectMessages() Twitter object is null. Exiting...");
					return null;
				}
			    ResponseList <DirectMessage> messages = twitter.getDirectMessages();
			    ArrayList<String> twitterArray = new ArrayList<String>();
				for(DirectMessage message: messages){
					String messageBody = message.getText();
					long messageID = message.getId();
					long timeStamp = message.getCreatedAt().getTime();
			    	String sentFromAddress = message.getSenderScreenName();
			    	long twitterID = message.getSenderId();
		    		String[] twitterContactInfo = null;
		    		twitterContactInfo = Common.getContactsInfoByTwitterID(context, twitterID);
		    		if(twitterContactInfo == null){
		    			twitterArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp);
					}else{
						twitterArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + timeStamp + "|" + twitterContactInfo[0] + "|" + twitterContactInfo[1] + "|" + twitterContactInfo[2] + "|" + twitterContactInfo[3]);
					}
				}
				return twitterArray;
			}else{
				return null;
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.getTwitterDirectMessages() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Aquire a global partial wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void acquirePartialWakeLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.aquirePartialWakelock()");
		try{
			if(_partialWakeLock == null){
		    	PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		    	_partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.DROID_NOTIFY_WAKELOCK);
		    	_partialWakeLock.setReferenceCounted(false);
			}
			_partialWakeLock.acquire();
		}catch(Exception ex){
			if (_debug) Log.e("Common.aquirePartialWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Release the global partial wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void clearPartialWakeLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearPartialWakelock()");
		try{
	    	if(_partialWakeLock != null){
	    		_partialWakeLock.release();
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("Common.clearPartialWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function that acquires the WakeLock for this Activity.
	 * The type flags for the WakeLock will be determined by the user preferences. 
	 * 
	 * @param context - The application context.
	 */
	public static void acquireWakeLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.aquireWakelock()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if(_wakeLock == null){
				PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
				if(preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true)){
					if(preferences.getBoolean(Constants.SCREEN_DIM_ENABLED_KEY, true)){
						_wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}else{
						_wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constants.DROID_NOTIFY_WAKELOCK);
					}
				}else{
					_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.DROID_NOTIFY_WAKELOCK);
				}
				_wakeLock.setReferenceCounted(false);
			}
			if(_wakeLock != null){
				_wakeLock.acquire();
			}
			Common.clearPartialWakeLock();
		}catch(Exception ex){
			if (_debug) Log.e("Common.aquireWakelock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Release the global wakelock within this context.
	 * 
	 * @param context - The application context.
	 */
	public static void clearWakeLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearWakelock()");
		try{
			Common.clearPartialWakeLock();
	    	if(_wakeLock != null){
	    		_wakeLock.release();
	    	}
		}catch(Exception ex){
			if (_debug) Log.e("Common.clearWakelock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Function that disables the Keyguard for this Activity.
	 * The removal of the Keyguard will be determined by the user preferences. 
	 * 
	 * @param context - The current context of this Activity.
	 */
	public static void acquireKeyguardLock(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.acquireKeyguardLock()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			if(keyguardManager.inKeyguardRestrictedInputMode() && preferences.getBoolean(Constants.SCREEN_ENABLED_KEY, true) && preferences.getBoolean(Constants.KEYGUARD_ENABLED_KEY, true)){
				if(_keyguardLock == null){
					_keyguardLock = keyguardManager.newKeyguardLock(Constants.DROID_NOTIFY_KEYGUARD);
				}
				_keyguardLock.disableKeyguard();
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.acquireKeyguardLock() ERROR: " + ex.toString());
		}
	}

	/**
	 * Re-Enables the Keyguard for this Activity.
	 */
	public static void clearKeyguardLock(){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.clearKeyguardLock()");
		try{
			if(_keyguardLock != null){
				_keyguardLock.reenableKeyguard();
			}
		}catch(Exception ex){
			if (_debug) Log.e("Common.clearKeyguardLock() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Function to format phone numbers.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputPhoneNumber - Phone number to be formatted.
	 * 
	 * @return String - Formatted phone number string.
	 */
	public static String formatPhoneNumber(Context context, String inputPhoneNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.formatPhoneNumber()");
		try{
			if(inputPhoneNumber.equals("Private Number")){
				return inputPhoneNumber;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			inputPhoneNumber = Common.removeFormatting(inputPhoneNumber);
			StringBuilder outputPhoneNumber = new StringBuilder("");		
			int phoneNumberFormatPreference = Integer.parseInt(preferences.getString(Constants.PHONE_NUMBER_FORMAT_KEY, Constants.PHONE_NUMBER_FORMAT_DEFAULT));
			String numberSeparator = "-";
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_6 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 | phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8){
				numberSeparator = ".";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10 | phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11){
				numberSeparator = " ";
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_4){
				numberSeparator = "";
			}
			if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_1 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_6 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_9){
				if(inputPhoneNumber.length() >= 10){
					//Format ###-###-#### (e.g.123-456-7890)
					//Format ###-###-#### (e.g.123.456.7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_2 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_7 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_10){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-###-##### (e.g.12-345-67890)
					//Format ##-###-##### (e.g.12.345.67890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 5, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 5));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_3 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_8 || phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_11){
				if(inputPhoneNumber.length() >= 10){
					//Format ##-##-##-##-## (e.g.12-34-56-78-90)
					//Format ##-##-##-##-## (e.g.12.34.56.78.90)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 2, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length() - 2));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 6, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 8, inputPhoneNumber.length() - 6));
					outputPhoneNumber.insert(0, numberSeparator);
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0,inputPhoneNumber.substring(0, inputPhoneNumber.length() - 8));
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 8));
						outputPhoneNumber.insert(0, numberSeparator);
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_4){
				//Format ########## (e.g.1234567890)
				outputPhoneNumber.append(inputPhoneNumber);
			}else if(phoneNumberFormatPreference == Constants.PHONE_NUMBER_FORMAT_5){
				if(inputPhoneNumber.length() >= 10){
					//Format (###) ###-#### (e.g.(123) 456-7890)
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 4, inputPhoneNumber.length()));
					outputPhoneNumber.insert(0, numberSeparator);
					outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 7, inputPhoneNumber.length() - 4));
					outputPhoneNumber.insert(0, ") ");
					if(inputPhoneNumber.length() == 10){
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, "(");
					}else{
						outputPhoneNumber.insert(0, inputPhoneNumber.substring(inputPhoneNumber.length() - 10, inputPhoneNumber.length() - 7));
						outputPhoneNumber.insert(0, " (");
						if(preferences.getBoolean(Constants.PHONE_NUMBER_FORMAT_10_DIGITS_ONLY_KEY , false)){
							outputPhoneNumber.insert(0, "0");
						}else{
							outputPhoneNumber.insert(0, inputPhoneNumber.substring(0, inputPhoneNumber.length() - 10));
						}
					}
				}else{
					outputPhoneNumber.append(inputPhoneNumber);
				}
			}else{
				outputPhoneNumber.append(inputPhoneNumber);
			}
			return outputPhoneNumber.toString();
		}catch(Exception ex){
			if (_debug) Log.e("Common.formatPhoneNumber() ERROR: " + ex.toString());
			return inputPhoneNumber;
		}
	}

	/**
	 * Function to format timestamps.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputTimestamp - The timestamp to be formatted.
	 * 
	 * @return String - Formatted time string.
	 */
	public static String formatTimestamp(Context context, long inputTimestamp){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.formatTimestamp()");
		try{
			if(inputTimestamp == 0){
				return "";
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int timeFormatPreference = Integer.parseInt(preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT));
			String timestampFormat = null;
			if(timeFormatPreference == Constants.TIME_FORMAT_12_HOUR){
				timestampFormat = "h:mma";
			}else if(timeFormatPreference == Constants.TIME_FORMAT_24_HOUR){
				timestampFormat = "H:mm";
			}else{
				timestampFormat = "h:mma";
			}
			SimpleDateFormat dateFormatted = new SimpleDateFormat(timestampFormat);
			dateFormatted.setTimeZone(TimeZone.getDefault());
			return dateFormatted.format(inputTimestamp);
		}catch(Exception ex){
			if (_debug) Log.e("Common.formatTimestamp() ERROR: " + ex.toString());
			return "";
		}
	}

	/**
	 * Function to format dates.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputTimestamp - The date to be formatted.
	 * 
	 * @return String - Formatted date string.
	 */
	public static String formatDate(Context context, Date inputDate){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.formatDate()");
		try{
			if(inputDate == null){
				return "";
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int timeFormatPreference = Integer.parseInt(preferences.getString(Constants.TIME_FORMAT_KEY, Constants.TIME_FORMAT_DEFAULT));
			int dateFormatPreference = Integer.parseInt(preferences.getString(Constants.DATE_FORMAT_KEY, Constants.DATE_FORMAT_DEFAULT));
			String dateFormat = null;
			String timeFormat = null;
			switch(timeFormatPreference){
				case Constants.TIME_FORMAT_12_HOUR:{
					timeFormat = "h:mm a";
					break;
				}
				case Constants.TIME_FORMAT_24_HOUR:{
					timeFormat = "H:mm";
					break;
				}
				default:{
					timeFormat = "h:mm a";
					break;
				}
			}
			switch(dateFormatPreference){
				case Constants.DATE_FORMAT_0:{
					dateFormat = "M/d/yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_1:{
					dateFormat = "M.d.yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_2:{
					dateFormat = "MMM d yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_3:{
					dateFormat = "MMM d, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_4:{
					dateFormat = "MMMM d yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_5:{
					dateFormat = "MMMM d, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_6:{
					dateFormat = "d/M/yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_7:{
					dateFormat = "d.M.yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_8:{
					dateFormat = "d MMM yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_9:{
					dateFormat = "d MMM, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_10:{
					dateFormat = "d MMMM yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_11:{
					dateFormat = "d MMMM, yyyy" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_12:{
					dateFormat = "yyyy/M/d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_13:{
					dateFormat = "yyyy.M.d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_14:{
					dateFormat = "yyyy MMM d" + " " + timeFormat;
					break;
				}
				case Constants.DATE_FORMAT_15:{
					dateFormat = "yyyy MMMM d" + " " + " " + timeFormat;
					break;
				}
				default:{
					dateFormat = "M/d/yyyy" + " " + timeFormat;
					break;
				}
			}
			SimpleDateFormat dateFormatted = new SimpleDateFormat(dateFormat);
			dateFormatted.setTimeZone(TimeZone.getDefault());
			return dateFormatted.format(inputDate);
		}catch(Exception ex){
			if (_debug) Log.e("Common.formatDate() ERROR: " + ex.toString());
			return "";
		}
	}

	/**
	 * Function to parse date parts from formated date strings.
	 * 
	 * @param context - The current context of this Activity.
	 * @param inputFormattedDate - The formated date to be parsed.
	 * 
	 * @return String[] - Parsed date string.
	 */
	public static String[] parseDateInfo(Context context, String inputFormattedDate){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.parseDateInfo()");
		try{
			if(inputFormattedDate == null){
				return null;
			}
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			int dateFormatPreference = Integer.parseInt(preferences.getString(Constants.DATE_FORMAT_KEY, Constants.DATE_FORMAT_DEFAULT));
			String[] dateInfoArray = null;
			if(dateFormatPreference == Constants.DATE_FORMAT_0 || dateFormatPreference == Constants.DATE_FORMAT_1 || 
			   dateFormatPreference == Constants.DATE_FORMAT_6 || dateFormatPreference == Constants.DATE_FORMAT_7 ||
			   dateFormatPreference == Constants.DATE_FORMAT_12 || dateFormatPreference == Constants.DATE_FORMAT_13){
				dateInfoArray = inputFormattedDate.split(" ");
			}else{
				String[] dateInfoArrayTemp = inputFormattedDate.split(" ");
				if(dateInfoArrayTemp.length < 5){
					dateInfoArray = new String[]{dateInfoArrayTemp[0] + " " + dateInfoArrayTemp[1] + " " + dateInfoArrayTemp[2], dateInfoArrayTemp[3]};
				}else{
					dateInfoArray = new String[]{dateInfoArrayTemp[0] + " " + dateInfoArrayTemp[1] + " " + dateInfoArrayTemp[2], dateInfoArrayTemp[3], dateInfoArrayTemp[4]};
				}
			}
			return dateInfoArray;
		}catch(Exception ex){
			if (_debug) Log.e("Common.parseDateInfo() ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Deleta an entire SMS/MMS thread.
	 * 
	 * @param context - The current context of this Activity.
	 * @param threadID - The Thread ID that we want to delete.
	 * @param notificationType - The notification type.
	 * 
	 * @return boolean - Returns true if the thread was deleted successfully.
	 */
	public static boolean deleteMessageThread(Context context, long threadID, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.deleteMessageThread()");
		try{
			if(threadID == 0){
				if (_debug) Log.v("Common.deleteMessageThread() Thread ID == 0. Exiting...");
				return false;
			}
			if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
				context.getContentResolver().delete(
						Uri.parse("content://mms/conversations/" + String.valueOf(threadID)), 
						null, 
						null);
			}else{
				context.getContentResolver().delete(
						Uri.parse("content://sms/conversations/" + String.valueOf(threadID)), 
						null, 
						null);
			}
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.deleteMessageThread() ERROR: " + ex.toString());
			return false;
		}
	}

	/**
	 * Delete a single SMS/MMS message.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The Message ID that we want to delete.
	 * @param notificationType - The notification type.
	 * 
	 * @return boolean - Returns true if the message was deleted successfully.
	 */
	public static boolean deleteSingleMessage(Context context, long messageID, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.deleteSingleMessage()");
		try{
			if(messageID == 0){
				if (_debug) Log.v("Common.deleteSingleMessage() Message ID == 0. Exiting...");
				return false;
			}
			if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
				context.getContentResolver().delete(
						Uri.parse("content://mms/" + String.valueOf(messageID)),
						null, 
						null);
			}else{
				context.getContentResolver().delete(
						Uri.parse("content://sms/" + String.valueOf(messageID)),
						null, 
						null);
			}
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.deleteSingleMessage() ERROR: " + ex.toString());
			return false;
		}
	}

	/**
	 * Mark a single SMS/MMS message as being read or not.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The Message ID that we want to alter.
	 * @param notificationType - The notification type.
	 * 
	 * @return boolean - Returns true if the message was updated successfully.
	 */
	public static boolean setMessageRead(Context context, long messageID, boolean isViewed, int notificationType){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setMessageRead()");
		try{
			if(messageID == 0){
				if (_debug) Log.v("Common.setMessageRead() Message ID == 0. Exiting...");
				return false;
			}
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put("READ", 1);
			}else{
				contentValues.put("READ", 0);
			}
			String selection = null;
			String[] selectionArgs = null;			
			if(notificationType == Constants.NOTIFICATION_TYPE_MMS){
				context.getContentResolver().update(
						Uri.parse("content://mms/" + messageID), 
			    		contentValues, 
			    		selection, 
			    		selectionArgs);
			}else{
				context.getContentResolver().update(
						Uri.parse("content://sms/" + messageID), 
			    		contentValues, 
			    		selection, 
			    		selectionArgs);
			}
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.setMessageRead() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Delete a call long entry.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was deleted successfully.
	 */
	public static boolean deleteFromCallLog(Context context, long callLogID){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.deleteFromCallLog()");
		try{
			if(callLogID == 0){
				if (_debug) Log.v("Common.deleteFromCallLog() Call Log ID == 0. Exiting...");
				return false;
			}
			String selection = android.provider.CallLog.Calls._ID + " = " + callLogID;
			String[] selectionArgs = null;
			context.getContentResolver().delete(
					Uri.parse("content://call_log/calls"),
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.deleteFromCallLog() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Mark a call log entry as being viewed.
	 * 
	 * @param context - The current context of this Activity.
	 * @param callLogID - The call log ID that we want to delete.
	 * 
	 * @return boolean - Returns true if the call log entry was updated successfully.
	 */
	public static boolean setCallViewed(Context context, long callLogID, boolean isViewed){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setCallViewed()");
		try{
			if(callLogID == 0){
				if (_debug) Log.v("Common.setCallViewed() Call Log ID == 0. Exiting...");
				return false;
			}
			ContentValues contentValues = new ContentValues();
			if(isViewed){
				contentValues.put(android.provider.CallLog.Calls.NEW, 0);
			}else{
				contentValues.put(android.provider.CallLog.Calls.NEW, 1);
			}
			String selection = android.provider.CallLog.Calls._ID + " = " + callLogID;
			String[] selectionArgs = null;
			context.getContentResolver().update(
					Uri.parse("content://call_log/calls"),
					contentValues,
					selection, 
					selectionArgs);
			return true;
		}catch(Exception ex){
			if (_debug) Log.e("Common.setCallViewed() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Delete a K9 Email using it's own URI.
	 * 
	 * @param context - The current context of this Activity.
	 * @param k9EmailDelUri - The URI provided to delete the email.
	 */
	public static void deleteK9Email(Context context, String k9EmailDelUri){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.deleteK9Email()");
		try{
			if(k9EmailDelUri == null || k9EmailDelUri.equals("")){
				if (_debug) Log.v("Common.deleteK9Email() k9EmailDelUri == null/empty. Exiting...");
				return;
			}
			String selection = null;
			String[] selectionArgs = null;
			context.getContentResolver().delete(
					Uri.parse(k9EmailDelUri),
					selection, 
					selectionArgs);
			return;
		}catch(Exception ex){
			if (_debug) Log.e("Common.deleteK9Email() ERROR: " + ex.toString());
			return;
		}
	}
	
	/**
	 * Delete a Twitter Direct Message.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The message ID that we want to delete.
	 */
	public static void deleteTwitterDirectMessage(Context context, long messageID){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.deleteTwitterDirectMessage()");
		try{
			if(messageID == 0){
				if (_debug) Log.v("Common.deleteTwitterDirectMessage() messageID == 0. Exiting...");
				return;
			}
			Twitter twitter = getTwitter(context);
			if(twitter == null){
				if (_debug) Log.v("Common.deleteTwitterDirectMessage() Twitter object is null. Exiting...");
				return;
			}
			twitter.destroyDirectMessage(messageID);
			return;
		}catch(Exception ex){
			if (_debug) Log.e("Common.deleteTwitterDirectMessage() ERROR: " + ex.toString());
			return;
		}
	}
	
	/**
	 * Reschedule a notification.
	 * 
	 * @param context - The application context.
	 * @param notification - The Notification to reschedule.
	 * @param rescheduleTime - The time we want the notification to be rescheduled.
	 * @param rescheduleNumber - The reschedule attempt (in case we need to keep track).
	 */
	public static PendingIntent rescheduleNotification(Context context, apps.droidnotify.Notification notification, long rescheduleTime, int rescheduleNumber){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.rescheduleNotification()");
		//Store the notification information into an ArrayList.
		int notificationType = notification.getNotificationType() + 100;
		//Get Notification Values.
		//========================================================
		//String[] Values:
		//[0]-notificationType
		//[1]-SentFromAddress
		//[2]-MessageBody
		//[3]-TimeStamp
		//[4]-ThreadID
		//[5]-ContactID
		//[6]-ContactName
		//[7]-MessageID
		//[8]-Title
		//[9]-CalendarID
		//[10]-CalendarEventID
		//[11]-CalendarEventStartTime
		//[12]-CalendarEventEndTime
		//[13]-AllDay
		//[14]-CallLogID
		//[15]-K9EmailUri
		//[16]-K9EmailDelUri
		//[17]-LookupKey
		//[18]-PhotoID
		//========================================================
		String sentFromAddress = notification.getSentFromAddress();
		String messageBody = notification.getMessageBody();
		long timeStamp = notification.getTimeStamp();
		long threadID = notification.getThreadID();
		long contactID = notification.getContactID();
		String contactName = notification.getContactName();
		long messageID = notification.getMessageID();
		String title = notification.getTitle();
		long calendarID = notification.getCalendarID();
		long calendarEventID = notification.getCalendarEventID();
		long calendarEventStartTime = notification.getCalendarEventStartTime();
		long calendarEventEndTime = notification.getCalendarEventEndTime();
		String allDay = "0";
		if(notification.getAllDay()){
			allDay = "1";
		}
		long callLogID = notification.getCallLogID();
		String k9EmailUri = notification.getK9EmailUri();
		String k9EmailDelUri = notification.getK9EmailDelUri();
		String lookupKey = notification.getLookupKey();
		long photoID = notification.getPhotoID();
		//Build Notification Information String Array.
		String[] rescheduleNotificationInfo = new String[] {String.valueOf(notificationType), sentFromAddress, messageBody, String.valueOf(timeStamp), String.valueOf(threadID), String.valueOf(contactID), contactName, String.valueOf(messageID), title, String.valueOf(calendarID), String.valueOf(calendarEventID), String.valueOf(calendarEventStartTime), String.valueOf(calendarEventEndTime), allDay, String.valueOf(callLogID), k9EmailUri, k9EmailDelUri, lookupKey, String.valueOf(photoID)};
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent rescheduleIntent = new Intent(context, RescheduleReceiver.class);
		Bundle rescheduleBundle = new Bundle();
		rescheduleBundle.putStringArray("rescheduleNotificationInfo", rescheduleNotificationInfo);
		rescheduleBundle.putInt("rescheduleNumber", rescheduleNumber);
		if (_debug) Log.v("Common.rescheduleNotification() rescheduleNumber: " + rescheduleNumber);
		rescheduleBundle.putInt("notificationType", notificationType);
		rescheduleIntent.putExtras(rescheduleBundle);
		rescheduleIntent.setAction("apps.droidnotify.VIEW/RescheduleNotification/" + rescheduleNumber + "/" + String.valueOf(notification.getTimeStamp()));
		PendingIntent reschedulePendingIntent = PendingIntent.getBroadcast(context, 0, rescheduleIntent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, rescheduleTime, reschedulePendingIntent);
		return reschedulePendingIntent;
	}
	
	/**
	 * Determine if the current time falls during a defined quiet time.
	 * 
	 * @param context - The application context.
	 * 
	 * @return boolean - Returns true if Quiet Time is enabled and the current time falls within the defined tiem period.
	 */
	public static boolean isQuietTime(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isQuietTime()");
		_context = context;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if(preferences.getBoolean(Constants.QUIET_TIME_ENABLED_KEY, false)){
			Calendar calendar = new GregorianCalendar();
			if (_debug) Log.v("Common.isQuietTime() HOUR: " + calendar.get(Calendar.HOUR_OF_DAY));
			String startTime = preferences.getString(Constants.QUIET_TIME_START_TIME_KEY, "");
			String stopTime = preferences.getString(Constants.QUIET_TIME_STOP_TIME_KEY, "");
			int hourStart = 0;
			int minuteStart = 0;
			int hourStop = 0;
			int minueStop = 0;
			if(startTime.equals("")){
				return false;
			}
			if(stopTime.equals("")){
				return false;
			}
			String[] startTimeArray = startTime.split("\\|");
			if(startTimeArray.length != 2){
				return false;
			}
			String[] stopTimeArray = stopTime.split("\\|");
			if(stopTimeArray.length != 2){
				return false;
			}
			hourStart = Integer.parseInt(startTimeArray[0]);
			minuteStart = Integer.parseInt(startTimeArray[1]);
			hourStop = Integer.parseInt(stopTimeArray[0]);
			minueStop = Integer.parseInt(stopTimeArray[1]);
			if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_EVERYDAY_VALUE)){
				return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
			}else if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_ONLY_WEEKEND_VALUE)){
				if(calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7){
					return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
				}else{
					return false;
				}
			}else if(preferences.getString(Constants.QUIET_TIME_OF_WEEK_KEY, Constants.QUIET_TIME_EVERYDAY_VALUE).equals(Constants.QUIET_TIME_ONLY_WEEKDAY_VALUE)){
				if(calendar.get(Calendar.DAY_OF_WEEK) == 2 || calendar.get(Calendar.DAY_OF_WEEK) == 3 || calendar.get(Calendar.DAY_OF_WEEK) == 4 || calendar.get(Calendar.DAY_OF_WEEK) == 5 || calendar.get(Calendar.DAY_OF_WEEK) == 6){
					return timeFallsWithinPeriod(calendar, hourStart, minuteStart, hourStop, minueStop);
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Wrapper function to speak a message using TTS.
	 * 
	 * @param context - The application context.
	 * @param tts - The TTS Object.
	 * @param text - The text to speak.
	 * 
	 * @return boolean - Return true if the Android TTS engine could be started.
	 */
	public static boolean speak(Context context, TextToSpeech tts, String text){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.speak()");
		if (tts == null) {
			return false;
	    }else{
	    	tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	    	return true;
	    }
	}
	
	/**
	 * Remove the HTML formatting from a string.
	 * 
	 * @param input - The string to remove the formatting from.
	 * 
	 * @return String - The output string without any html.
	 */
	public static String removeHTML(String input){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.removeHTML()");
		String output = input;
		output = output.replace("<br/>", ". ");
		output = output.replace("<i>", "").replace("</i>", "");
		output = output.replace("<b>", "").replace("</b>", "");
		output = output.replace("<u>", "").replace("</u>", "");
		return output;
	}
	
	/**
	 * Set the UserInLinkedApp flag.
	 * 
	 * @param context - The application context.
	 * @param flag - Boolean flag to set.
	 */
	public static void setInLinkedAppFlag(Context context, boolean flag){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.setInLinkedAppFlag()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(Constants.USER_IN_LINKED_APP_KEY, flag);
		editor.commit();
	}

	/**
	 * Get the UserInLinkedApp flag.
	 * 
	 * @param context - The application context.
	 * 
	 * @return boolean - The boolean flag to return.
	 */
	public static boolean isUserInLinkedApp(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isUserInLinkedApp()");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(Constants.USER_IN_LINKED_APP_KEY, false);
	}
	
	/**
	 * This resends an intent to the main notification activity.
	 * 
	 * @param context - The application context.
	 * @param intent - The intent to resend.
	 */
	public static void resendNotification(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.resendNotification()");
		try{
			Bundle bundle = intent.getExtras();
	    	Intent smsNotificationIntent = new Intent(context, NotificationActivity.class);
	    	smsNotificationIntent.putExtras(bundle);
	    	smsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	Common.acquireWakeLock(context);
	    	context.startActivity(smsNotificationIntent);
		}catch(Exception ex){
			if (_debug) Log.e("Common.resendNotification() ERROR: " + ex.toString());
		}
	}

	/**
	 * Determine if the user has authenticated their twitter account. 
	 * 
	 * @param context - The application context.
	 *
	 * @return boolean - Return true if the user preferences have Twitter authentication data & are able to log into Twitter.
	 */
	public static boolean isTwitterAuthenticated(Context context) {
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.isTwitterAuthenticated()");	
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(OAuth.OAUTH_TOKEN, "");
			String oauthTokenSecret = preferences.getString(OAuth.OAUTH_TOKEN_SECRET, "");
			if (_debug) Log.v("Common.isTwitterAuthenticated() oauthToken: " + oauthToken);	
			if (_debug) Log.v("Common.isTwitterAuthenticated() oauthTokenSecret: " + oauthTokenSecret);	
			if(oauthToken.equals("") || oauthTokenSecret.equals("")){
				if (_debug) Log.v("Common.isTwitterAuthenticated() Twitter stored authentication details are null. Exiting...");
				return false;
			}	
			try {
				Twitter twitter = getTwitter(context);
				if(twitter == null){
					if (_debug) Log.v("Common.isTwitterAuthenticated() Twitter object is null. Exiting...");
					return false;
				} 
				twitter.getAccountSettings();
				return true;
			} catch (Exception ex) {
				if (_debug) Log.e("Common.isTwitterAuthenticated() Twitter Authentication - ERROR: " + ex.toString());
				return false;
			}
		} catch (Exception ex) {
			if (_debug) Log.e("Common.isTwitterAuthenticated() ERROR: " + ex.toString());
			return false;
		}
	}
	
	/**
	 * Start the Calendar recurring alarm.
	 * 
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startCalendarAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startCalendarAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.CALENDAR_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("Common.startCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Calendar recurring alarm. 
	 * 
	 * @param context - The application context.
	 */
	public static void cancelCalendarAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.cancelCalendarAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, CalendarAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("Common.cancelCalendarAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Start the Twitter recurring alarm.
	 *  
	 * @param context - The application context.
	 * @param alarmStartTime - The time to start the alarm.
	 */
	public static void startTwitterAlarmManager(Context context, long alarmStartTime){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startTwitterAlarmManager()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, TwitterAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			long pollingFrequency = Long.parseLong(preferences.getString(Constants.TWITTER_POLLING_FREQUENCY_KEY, "15")) * 60 * 1000;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime, pollingFrequency, pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("Common.startTwitterAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Cancel the Twitter recurring alarm.
	 *  
	 * @param context - The application context.
	 */
	public static void cancelTwitterAlarmManager(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.cancelTwitterAlarmManager()");
		try{
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, TwitterAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			alarmManager.cancel(pendingIntent);
		}catch(Exception ex){
			if (_debug) Log.e("Common.cancelTwitterAlarmManager() ERROR: " + ex.toString());
		}
	}
	
	/**
	 * Initialize and return a Twitter object.
	 * 
	 * @param context - The application context.
	 * 
	 * @return Twitter - The initialized Twitter object or null.
	 */
	public static Twitter getTwitter(Context context){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.getTwitter()");
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String oauthToken = preferences.getString(OAuth.OAUTH_TOKEN, null);
			String oauthTokenSecret = preferences.getString(OAuth.OAUTH_TOKEN_SECRET, null);
			if(oauthToken == null || oauthTokenSecret == null){
				if (_debug) Log.v("Common.getTwitter() Oauth Values Are Null. Exiting...");
				return null;
			}
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder(); 
			configurationBuilder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY); 
			configurationBuilder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET); 
			Configuration configuration =  configurationBuilder.build();  
			AccessToken accessToken = new AccessToken(oauthToken, oauthTokenSecret);
			TwitterFactory twitterFactory = new TwitterFactory(configuration);
			Twitter twitter = twitterFactory.getInstance(accessToken);
			return twitter;
		}catch(Exception ex){
			if (_debug) Log.e("Common.getTwitter() ERROR: " + ex.toString());
			return null;
		}	
	}
	
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
	
	/**
	 * Play a notification sound through the media player.
	 * 
	 * @author Camille Sévigny
	 */
	private static class playNotificationMediaFileAsyncTask extends AsyncTask<String, Void, Void> {
	    
	    /**
	     * Do this work in the background.
	     * 
	     * @param params - The URI of the notification sound.
	     */
	    protected Void doInBackground(String... params) {
			if (_debug) Log.v("Common.playNotificationMediaFileAsyncTask.doInBackground()");
			MediaPlayer mediaPlayer = null;
			try{
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setLooping(false);
				mediaPlayer.setDataSource(_context,  Uri.parse(params[0]));
				mediaPlayer.prepare();
				mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new OnCompletionListener(){
	                public void onCompletion(MediaPlayer mediaPlayer) {
	                	mediaPlayer.release();
	                	mediaPlayer = null;
	                }
				});	
		    	return null;
			}catch(Exception ex){
				if (_debug) Log.e("Common.playNotificationMediaFileAsyncTask.doInBackground() ERROR: " + ex.toString());
				mediaPlayer.release();
            	mediaPlayer = null;
				return null;
			}
	    }
	    
	    /**
	     * Nothing needs to happen once the media file has been played.
	     * 
	     * @param result - Void.
	     */
	    protected void onPostExecute(Void result) {
			if (_debug) Log.v("Common.playNotificationMediaFileAsyncTask.onPostExecute()");
	    }
	    
	}
	
	/**
	 * Parse an email address form a "FROM" email address.
	 * 
	 * @param inputFromAddress - The address we wish to parse.
	 * 
	 * @return String- The email address that we parsed/extracted.
	 */
	private static String parseFromEmailAddress(String inputFromAddress){
		if (_debug) Log.v("Common.parseFromEmailAddress()");
		try{
			if(inputFromAddress == null || inputFromAddress.equals("")){
				if (_debug) Log.v("Common.parseFromEmailAddress() InputFromAddress is null/empty. Exiting...");
				return inputFromAddress;
			}
			String outputEmailAddress = null;
			if(inputFromAddress.contains("<") && inputFromAddress.contains(">")){
				outputEmailAddress = inputFromAddress.substring(inputFromAddress.indexOf("<") + 1, inputFromAddress.indexOf(">"));
			}else{
				 outputEmailAddress = inputFromAddress;
			}
			return outputEmailAddress;
		}catch(Exception ex){
			if (_debug) Log.e("Common.parseFromEmailAddress() ERROR: " + ex.toString());
			return inputFromAddress;
		}
	}
	
	/**
	 * Determine if the current time falls within the period time.
	 * 
	 * @param calendar - The calendar we should use.
	 * @param hourStart - The starting hour of the time period.
	 * @param minuteStart - The starting minute of the time period.
	 * @param hourStop - The ending hour of the time period.
	 * @param minueStop - The ending minute of the time period.
	 * 
	 * @return boolean - Returns true if the current time falls within the period time.
	 */
	private static boolean timeFallsWithinPeriod(Calendar calendar, int hourStart, int minuteStart, int hourStop, int minuteStop){
		if (_debug) Log.v("Common.timeFallsWithinPeriod()");
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		int currentMinute = calendar.get(Calendar.MINUTE);
		if(hourStart < hourStop){
			//Time period is within the same day.
			if(currentHour >= hourStart && currentHour <= hourStop){
				if(currentHour == hourStart || currentHour == hourStop){
					if(currentHour == hourStart){
						if(currentMinute >= minuteStart){
							return true;
						}else{
							return false;
						}
					}else{
						if(currentMinute <= minuteStop){
							return true;
						}else{
							return false;
						}	
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}else{
			//Time period spans 2 days.
			if(currentHour >= hourStart || currentHour <= hourStop){
				if(currentHour == hourStart || currentHour == hourStop){
					if(currentHour == hourStart){
						if(currentMinute >= minuteStart){
							return true;
						}else{
							return false;
						}
					}else{
						if(currentMinute <= minuteStop){
							return true;
						}else{
							return false;
						}	
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}
	}
	
}