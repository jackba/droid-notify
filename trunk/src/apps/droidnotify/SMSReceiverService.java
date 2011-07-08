package apps.droidnotify;

import java.util.ArrayList;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

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
            if(messageBody.toString().startsWith(sentFromAddress)){
            	messageBody = messageBody.toString().substring(sentFromAddress.length()).replace("\n", "<br/>").trim();
            }    
            if(messageSubject != null && !messageSubject.equals("")){
				messageBody = "(" + messageSubject + ")" + messageBody.toString().replace("\n", "<br/>").trim();
			}else{
				messageBody = messageBody.toString().replace("\n", "<br/>").trim();
			}   
    		threadID = Common.loadThreadID(context, sentFromAddress);
    		messageID = Common.loadMessageID(context, threadID, messageBody, timeStamp);
    		String[] smsContactInfo = null;
    		if(sentFromAddress.contains("@")){
	    		smsContactInfo = Common.loadContactsInfoByEmail(context, sentFromAddress);
	    	}else{
	    		smsContactInfo = Common.loadContactsInfoByPhoneNumber(context, sentFromAddress);
	    	}
    		if(smsContactInfo == null){
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp);
			}else{
				smsArray.add(sentFromAddress + "|" + messageBody + "|" + messageID + "|" + threadID + "|" + timeStamp + "|" + smsContactInfo[0] + "|" + smsContactInfo[1] + "|" + smsContactInfo[2]);
			}
    		return smsArray;
		}catch(Exception ex){
			if (_debug) Log.v("SMSReceiverService.getSMSMessages() ERROR: " + ex.toString());
			return null;
		}
	}
		
}