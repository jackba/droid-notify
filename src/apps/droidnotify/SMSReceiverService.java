package apps.droidnotify;

import java.util.ArrayList;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

/**
 * This class handles the work of processing incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiverService extends WakefulIntentService {

	//================================================================================
    // Constants
    //================================================================================
	
	private static final int NOTIFICATION_TYPE_SMS = 1;
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;
	
	//================================================================================
	// Constructors
	//================================================================================
	
	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public SMSReceiverService() {
		super("SMSReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiverService.SMSReceiverService()");
	}

	//================================================================================
	// Protected Methods
	//================================================================================
	
	/**
	 * Do the work for the service inside this function.
	 * 
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiverService.doWakefulWork()");
		//startNotificationActivity(intent);
		Bundle newSMSBundle = intent.getExtras();
		ArrayList<String> smsArray = getSMSMessages(newSMSBundle);
		if(smsArray.size() > 0){
			Context context = getApplicationContext();
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", NOTIFICATION_TYPE_SMS);
			bundle.putStringArrayList("smsArrayList",smsArray);
	    	Intent smsNotificationIntent = new Intent(context, NotificationActivity.class);
	    	smsNotificationIntent.putExtras(bundle);
	    	smsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	    	context.startActivity(smsNotificationIntent);
		}else{
			if (_debug) Log.v("SMSReceiverService.doWakefulWork() No new SMSs were found. Exiting...");
		}
	}
	
	//================================================================================
	// Private Methods
	//================================================================================
	
//	/**
//	 * Display the notification to the screen.
//	 * Send add the SMS message to the Intent object that we created for the new activity.
//	 * 
//	 * @param intent - Intent object that we are working with.
//	 */
//	private void startNotificationActivity(Intent intent) {
//		if (_debug) Log.v("SMSReceiverService.startNotificationActivity()");
//		Context context = getApplicationContext();
//		Bundle bundle = intent.getExtras();
//		bundle.putInt("notificationType", NOTIFICATION_TYPE_SMS);
//    	Intent smsIntent = new Intent(context, NotificationActivity.class);
//    	smsIntent.putExtras(bundle);
//    	smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//    	context.startActivity(smsIntent);
//	}
	
	private ArrayList<String> getSMSMessages(Bundle bundle){
		if (_debug) Log.v("SMSReceiverService.getSMSMessages()");
		Context context = getApplicationContext();
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
            //I don't know why the line below is "-=" and not "+=" but for some reason it works.
            timeStamp -= TimeZone.getDefault().getOffset(timeStamp);
            sentFromAddress = sms.getDisplayOriginatingAddress().toLowerCase();
            if(sentFromAddress.contains("@")){
            	sentFromAddress = 	removeEmailFormatting(sentFromAddress);
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
            if(messageBody.toString().startsWith(sentFromAddress)){
            	messageBody = messageBody.toString().substring(sentFromAddress.length()).replace("\n", "<br/>").trim();
            }    
            if(messageSubject != null && !messageSubject.equals("")){
				messageBody = "(" + messageSubject + ")" + messageBody.toString().replace("\n", "<br/>").trim();
			}else{
				messageBody = messageBody.toString().replace("\n", "<br/>").trim();
			}   
    		threadID = loadThreadID(context, sentFromAddress);
    		messageID = loadMessageID(context, threadID, messageBody, timeStamp);
    		String[] smsContactInfo = null;
    		if(sentFromAddress.contains("@")){
	    		smsContactInfo = loadContactsInfoByEmail(context, sentFromAddress);
	    	}else{
	    		smsContactInfo = loadContactsInfoByPhoneNumber(context, sentFromAddress);
	    	}
    		if(smsContactInfo == null){
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp);
			}else{
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2]);
			}
    		return smsArray;
		}catch(Exception ex){
			if (_debug) Log.v("Notification.Notification(Context context, Bundle bundle, int notificationType) Parse Message Body ERROR: " + ex.toString());
			return null;
		}
	}
	
	/**
	 * Load the SMS/MMS thread id for this notification.
	 * 
	 * @param context - Application Context.
	 * @param phoneNumber - Notifications's phone number.
	 */
	private long loadThreadID(Context context, String address){
		if (_debug) Log.v("Notification.getThreadIdByAddress()");
		long threadID = 0;
		if (address == null){
			if (_debug) Log.v("Notification.loadThreadID() Address provided is null: Exiting...");
			return 0;
		}
		if (address == ""){
			if (_debug) Log.v("Notification.loadThreadID() Address provided is empty: Exiting...");
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
		    			if (_debug) Log.v("Notification.loadThreadID() Thread ID Found: " + threadID);
		    		}
		    	}
	    	}catch(Exception e){
		    		if (_debug) Log.e("Notification.loadThreadID() EXCEPTION: " + e.toString());
	    	} finally {
	    		cursor.close();
	    	}
	    	return threadID;
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadThreadID() ERROR: " + ex.toString());
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
	public long loadMessageID(Context context, long threadID, String messageBody, long timeStamp) {
		if (_debug) Log.v("Notification.loadMessageID()");
		if (messageBody == null){
			if (_debug) Log.v("Notification.loadMessageID() Message body provided is null: Exiting...");
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
		    			if (_debug) Log.v("Notification.loadMessageID() Message ID Found: " + messageID);
		    			break;
		    		}
			    }
		    }catch(Exception ex){
				if (_debug) Log.e("Notification.loadMessageID() ERROR: " + ex.toString());
			}finally{
		    	cursor.close();
		    }
		    return messageID;
		}catch(Exception ex){
			if (_debug) Log.e("Notification.loadMessageID() ERROR: " + ex.toString());
			return 0;
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
	private String[] loadContactsInfoByPhoneNumber(Context context, String incomingNumber){
		if (_debug) Log.v("MMSReceiverService.loadContactsInfoByPhoneNumber()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		boolean _contactExists = false;
		if (incomingNumber == null) {
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByPhoneNumber() Phone number provided is null: Exiting...");
			return null;
		}
		//Exit if the phone number is an email address.
		if (incomingNumber.contains("@")) {
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByPhoneNumber() Phone number provided appears to be an email address: Exiting...");
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
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByPhoneNumber() Searching Contacts");
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
			if (_debug) Log.e("MMSReceiverService.loadContactsInfoByPhoneNumber() ERROR: " + ex.toString());
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
	private String removeFormatting(String phoneNumber){
		if (_debug) Log.v("MMSReceiverService.removeFormatting()");
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
	 * Load the various contact info for this notification from an email.
	 * 
	 * @param context - Application Context.
	 * @param incomingEmail - Notifications's email address.
	 * 
	 * @return String[] - String Array of the contact information.
	 */ 
	private String[] loadContactsInfoByEmail(Context context, String incomingEmail){
		if (_debug) Log.v("MMSReceiverService.loadContactsInfoByEmail()");
		long _contactID = 0;
		String _contactName = "";
		long _photoID = 0;
		boolean _contactExists = false;
		if (incomingEmail == null) {
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByEmail() Email provided is null: Exiting...");
			return null;
		}
		if (!incomingEmail.contains("@")) {
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByEmail() Email provided does not appear to be a valid email address: Exiting...");
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
			if (_debug) Log.v("MMSReceiverService.loadContactsInfoByEmail() Searching contacts");
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
			if (_debug) Log.e("MMSReceiverService.loadContactsInfoByEmail() ERROR: " + ex.toString());
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
	private String removeEmailFormatting(String address){
		if (_debug) Log.v("Notification.removeEmailFormatting()");
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
	
}