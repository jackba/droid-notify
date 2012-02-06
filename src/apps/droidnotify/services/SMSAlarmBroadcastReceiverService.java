package apps.droidnotify.services;

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
import apps.droidnotify.sms.SMSCommon;

public class SMSAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
	public SMSAlarmBroadcastReceiverService() {
		super("SMSAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSAlarmBroadcastReceiverService.SMSAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("SMSAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("SMSAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("SMSAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if MMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("SMSAlarmBroadcastReceiverService.doWakefulWork() SMS Notifications Disabled. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = true;
		    boolean rescheduleNotificationInQuickReply = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inQuickReplyApp = Common.isUserInQuickReplyApp(context);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else if(inQuickReplyApp){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInQuickReply = preferences.getBoolean(Constants.IN_QUICK_REPLY_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, SMSService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		Bundle smsNotificationBundle = SMSCommon.getSMSMessagesFromDisk(context);
		    		if(smsNotificationBundle != null){
		    			Bundle smsNotificationBundleSingle = smsNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
		    			if(smsNotificationBundleSingle != null){
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_SMS, 0, callStateIdle, smsNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), smsNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), smsNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, null);
		    			}
		    		}				    
			    }					
		    	String intentActionText = "apps.droidnotify.alarm/SMSAlarmReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
		    	Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, SMSAlarmReceiver.class, null, intentActionText);
		    }
		}catch(Exception ex){
			Log.e("SMSAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}