package apps.droidnotify.sms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import apps.droidnotify.NotificationActivity;
import apps.droidnotify.QuickReplyActivity;
import apps.droidnotify.R;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.email.EmailCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.phone.PhoneCommon;

/**
 * This class is a collection of SMS/MMS methods.
 * 
 * @author Camille Sévigny
 */
public class SMSCommon {

	//================================================================================
    // Properties
    //================================================================================
	
	private static boolean _debug = false;
	
	//================================================================================
	// Public Methods
	//================================================================================

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
		    	sentFromAddress = sentFromAddress.contains("@") ? EmailCommon.removeEmailFormatting(sentFromAddress) : PhoneCommon.removePhoneNumberFormatting(sentFromAddress);
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
			Log.e("Common.getSMSMessagesFromDisk() ERROR: " + ex.toString());
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
            sentFromAddress = sentFromAddress.contains("@") ? EmailCommon.removeEmailFormatting(sentFromAddress) : PhoneCommon.removePhoneNumberFormatting(sentFromAddress);
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
    		threadID = getThreadID(context, sentFromAddress, Constants.NOTIFICATION_TYPE_SMS);
    		messageID = getMessageID(context, threadID, messageBody, timeStamp, Constants.NOTIFICATION_TYPE_SMS);
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
			Log.e("Common.getSMSMessagesFromIntent() ERROR: " + ex.toString());
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
		    	String sentFromAddress = getMMSAddress(context, messageID);
		    	sentFromAddress = sentFromAddress.contains("@") ? EmailCommon.removeEmailFormatting(sentFromAddress) : PhoneCommon.removePhoneNumberFormatting(sentFromAddress);
		    	String messageBody = getMMSText(context, messageID);
		    	String[] mmsContactInfo = null;
		    	mmsContactInfo = sentFromAddress.contains("@") ? Common.getContactsInfoByEmail(context, sentFromAddress) : Common.getContactsInfoByPhoneNumber(context, sentFromAddress);
				if(mmsContactInfo == null){
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp);
				}else{
					mmsArray.add(sentFromAddress + "|" + messageBody.replace("\n", "<br/>") + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + mmsContactInfo[0] + "|" + mmsContactInfo[1] + "|" + mmsContactInfo[2] + "|" + mmsContactInfo[3]);
				}
		    	break;
	    	}
		}catch(Exception ex){
			Log.e("Common.getMMSMessagesFromDisk() ERROR: " + ex.toString());
		}finally{
    		cursor.close();
    	}
		return mmsArray;	
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
		if (address == null|| address.equals("")){
			if (_debug) Log.v("Common.getThreadID() Address provided is null or empty: Exiting...");
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
		    		Log.e("Common.getThreadID() EXCEPTION: " + e.toString());
	    	} finally {
	    		if(cursor != null){
					cursor.close();
				}
	    	}
	    	return threadID;
		}catch(Exception ex){
			Log.e("Common.getThreadID() ERROR: " + ex.toString());
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
				Log.e("Common.getMessageID() ERROR: " + ex.toString());
			}finally{
				if(cursor != null){
					cursor.close();
				}
		    }
		    return messageID;
		}catch(Exception ex){
			Log.e("Common.loadMessageID() ERROR: " + ex.toString());
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
			Log.e("Common.getMMSAddress() ERROR: " + ex.toString());
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
			Log.e("Common.getMMSText ERROR: " + ex.toString());
		} finally {
			if(cursor != null){
				cursor.close();
			}
    	}	   
	    return messageText.toString();  
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
	    	Log.e("Common.getMMSTextFromPart() ERROR: " + ex.toString());
	    }finally {
	    	try{
	    		inputStream.close();
	    	}catch(Exception ex){
	    		Log.e("Common.getMMSTextFromPart() ERROR: " + ex.toString());
	    	}
	    }
	    return messageText.toString();
	}
	
	/**
	 * Start the intent for the Quick Reply activity send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param requestCode - The request code we want returned.
	 * @param sendTo - The number/address/screen name we want to send a reply to.
	 * @param name - The name of the contact we are sending a reply to.
	 * 
	 * @return boolean - Returns true if the activity can be started.
	 */
	public static boolean startMessagingQuickReplyActivity(Context context, NotificationActivity notificationActivity, int requestCode, String sendTo, String name){
		_debug = Log.getDebug();
		if (_debug) Log.v("Common.startMessagingQuickReplyActivity()");
		if(sendTo == null){
			Toast.makeText(context, context.getString(R.string.app_android_reply_messaging_address_error), Toast.LENGTH_LONG).show();
			return false;
		}
		try{
			Intent intent = new Intent(context, QuickReplyActivity.class);
	        if (_debug) Log.v("NotificationView.replyToMessage() Put bundle in intent");
	        Bundle bundle = new Bundle();
	        bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_SMS);
	        bundle.putInt("notificationSubType", 0);
	        bundle.putString("sendTo", sendTo);
		    if(name != null && !name.equals( context.getString(android.R.string.unknownName))){
		    	bundle.putString("name", name);
		    }else{
		    	bundle.putString("name", "");
		    }
		    bundle.putString("message", "");
		    intent.putExtras(bundle);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("Common.startMessagingQuickReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_quick_reply_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
		}
	}
	
	/**
	 * Start the intent for any android messaging application to send a reply.
	 * 
	 * @param context - Application Context.
	 * @param notificationActivity - A reference to the parent activity.
	 * @param phoneNumber - The number/address/screen name we want to send a message to.
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
		    intent.setData(Uri.parse("smsto:" + PhoneCommon.removePhoneNumberFormatting(phoneNumber)));
		    // Exit the app once the SMS is sent.
		    intent.putExtra("compose_mode", true);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("Common.startMessagingAppReplyActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
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
		    intent.setData(Uri.parse("smsto:" + PhoneCommon.removePhoneNumberFormatting(phoneNumber)));
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("Common.startMessagingAppViewThreadActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
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
	        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        notificationActivity.startActivityForResult(intent, requestCode);
	        Common.setInLinkedAppFlag(context, true);
	        return true;
		}catch(Exception ex){
			Log.e("Common.startMessagingAppViewInboxActivity() ERROR: " + ex.toString());
			Toast.makeText(context, context.getString(R.string.app_android_messaging_app_error), Toast.LENGTH_LONG).show();
			Common.setInLinkedAppFlag(context, false);
			return false;
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
			Log.e("Common.deleteMessageThread() ERROR: " + ex.toString());
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
			Log.e("Common.deleteSingleMessage() ERROR: " + ex.toString());
			return false;
		}
	}

	/**
	 * Mark a single SMS/MMS message as being read or not.
	 * 
	 * @param context - The current context of this Activity.
	 * @param messageID - The Message ID that we want to alter.
	 * @param isViewed - The boolean value indicating if it was read or not.
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
			Log.e("Common.setMessageRead() ERROR: " + ex.toString());
			return false;
		}
	}
	
}
