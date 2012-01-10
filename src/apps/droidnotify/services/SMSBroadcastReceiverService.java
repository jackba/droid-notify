package apps.droidnotify.services;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.SMSAlarmReceiver;
import apps.droidnotify.receivers.SMSReceiver;
import apps.droidnotify.sms.SMSCommon;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class SMSBroadcastReceiverService extends WakefulIntentService {
	
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
	public SMSBroadcastReceiverService() {
		super("SMSBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSBroadcastReceiverService.SMSBroadcastReceiverService()");
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
		if (_debug) Log.v("SMSBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("SMSBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("SMSBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if SMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("SMSBroadcastReceiverService.doWakefulWork() SMS Notifications Disabled. Exiting...");
				return;
			}
			if(preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals(Constants.SMS_READ_FROM_DISK)){
				//Schedule sms task x seconds after the broadcast.
				//This time is set by the users advanced preferences. 10 seconds is the default value.
				//This should allow enough time to pass for the sms inbox to be written to.
				long timeoutInterval = Long.parseLong(preferences.getString(Constants.SMS_TIMEOUT_KEY, "10")) * 1000;
				String intentActionText = "apps.droidnotify.alarm/SMSAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
				long rescheduleTime = System.currentTimeMillis() + timeoutInterval;
				Common.startAlarm(context, SMSAlarmReceiver.class, null, intentActionText, rescheduleTime);
			}else{
			    //Check the state of the users phone.
			    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			    boolean notificationIsBlocked = false;
			    boolean rescheduleNotification = true;
			    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
			    String blockingAppRuningAction = preferences.getString(Constants.SMS_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    //Reschedule notification based on the users preferences.
			    if(!callStateIdle){
			    	notificationIsBlocked = true;		    	
			    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
			    }else{		    	
			    	notificationIsBlocked = Common.isNotificationBlocked(context, blockingAppRuningAction);
			    }
			    if(!notificationIsBlocked){
					Intent smsIntent = new Intent(context, SMSService.class);
					smsIntent.putExtras(intent.getExtras());
					WakefulIntentService.sendWakefulWork(context, smsIntent);
			    }else{
			    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
			    	if(preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
			    		//Get the sms message info.
						String messageAddress = null;
						String messageBody = null;
						String contactName = null;
			    		Bundle bundle = intent.getExtras();
			    		ArrayList<String> smsArray = SMSCommon.getSMSMessagesFromIntent(context, bundle);
						if((smsArray != null) && (smsArray.size() > 0)){
				    		String smsArrayItem = smsArray.get(0);
							String[] smsInfo = smsArrayItem.split("\\|");
			    			int arraySize = smsInfo.length;
			    			if(arraySize > 0){
								if(arraySize >= 1) messageAddress = smsInfo[0];
								if(arraySize >= 2) messageBody = smsInfo[1];
								if(arraySize >= 7) contactName = smsInfo[6];
			    			}
						}
						//Display Status Bar Notification
					    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_SMS, 0, callStateIdle, contactName, messageAddress, messageBody, null);
				    }
			    	//Ignore notification based on the users preferences.
			    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
			    		rescheduleNotification = false;
			    		return;
			    	}
			    	if(rescheduleNotification){
				    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
				    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
			    		if (_debug) Log.v("SMSBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
						String intentActionText = "apps.droidnotify.alarm/SMSReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
						long rescheduleTime = System.currentTimeMillis() + rescheduleInterval;
						Common.startAlarm(context, SMSReceiver.class, intent.getExtras(), intentActionText, rescheduleTime);
			    	}
			    }
			}
		}catch(Exception ex){
			Log.e("SMSBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}