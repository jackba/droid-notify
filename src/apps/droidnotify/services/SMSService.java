package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.sms.SMSCommon;

/**
 * This class handles the work of processing incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public SMSService() {
		super("SMSService");
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
		Context context = getApplicationContext();
		try{
			Bundle newSMSBundle = intent.getExtras();
			Bundle smsNotificationBundle = SMSCommon.getSMSMessagesFromIntent(context, newSMSBundle);
			if(smsNotificationBundle != null){
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.BUNDLE_NOTIFICATION_TYPE, Constants.NOTIFICATION_TYPE_SMS);
				bundle.putBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME, smsNotificationBundle);
		    	Common.startNotificationActivity(context, bundle);
			}else{
				Log.e(context, "SMSService.doWakefulWork() No new SMSs were found. Exiting...");
			}
		}catch(Exception ex){
			Log.e(context, "SMSService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}