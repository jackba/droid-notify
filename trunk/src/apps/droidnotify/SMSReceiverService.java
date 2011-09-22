package apps.droidnotify;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class handles the work of processing incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	boolean _debug = false;

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
		Context context = getApplicationContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		ArrayList<String> smsArray = null;
		if(preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals("0")){
			Bundle newSMSBundle = intent.getExtras();
			smsArray = getSMSMessagesFromIntent(newSMSBundle);
		}else{
			smsArray = getSMSMessagesFromDisk();
		}
		if(smsArray.size() > 0){
			Bundle bundle = new Bundle();
			bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_SMS);
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
	
	/**
	 * Function to query the sms inbox and check for any new messages.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the sms information.
	 */
	private ArrayList<String> getSMSMessagesFromDisk(){
		if (_debug) Log.v("SMSReceiverService.getSMSMessagesFromDisk()");
		Context context = getApplicationContext();
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
			if (_debug) Log.e("SMSReceiverService.getSMSMessagesFromDisk() ERROR: " + ex.toString());
		} finally {
    		cursor.close();
    	}
		return smsArray;	
	}
	
	/**
	 * Parse the incoming SMS message directly.
	 * 
	 * @param bundle - Bundle from the incomming intent.
	 * 
	 * @return ArrayList<String> - Returns an ArrayList of Strings that contain the sms information.
	 */
	private ArrayList<String> getSMSMessagesFromIntent(Bundle bundle){
		if (_debug) Log.v("SMSReceiverService.getSMSMessagesFromIntent()");
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
			if (_debug) Log.v("SMSReceiverService.getSMSMessagesFromIntent() ERROR: " + ex.toString());
			return null;
		}
	}
		
}