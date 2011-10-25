package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import apps.droidnotify.NotificationActivity;
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
		if (_debug) Log.v("SMSReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			ArrayList<String> smsArray = null;
			if(preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals(Constants.SMS_READ_FROM_INTENT)){
				Bundle newSMSBundle = intent.getExtras();
				smsArray = Common.getSMSMessagesFromIntent(context, newSMSBundle);
			}else{
				smsArray = Common.getSMSMessagesFromDisk(context);
			}
			if(smsArray.size() > 0){
				Bundle bundle = new Bundle();
				bundle.putInt("notificationType", Constants.NOTIFICATION_TYPE_SMS);
				bundle.putStringArrayList("smsArrayList",smsArray);
		    	Intent smsNotificationIntent = new Intent(context, NotificationActivity.class);
		    	smsNotificationIntent.putExtras(bundle);
		    	smsNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		    	Common.acquirePartialWakeLock(context);
		    	context.startActivity(smsNotificationIntent);
			}else{
				if (_debug) Log.v("SMSReceiverService.doWakefulWork() No new SMSs were found. Exiting...");
			}
		}catch(Exception ex){
			if (_debug) Log.e("SMSReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}