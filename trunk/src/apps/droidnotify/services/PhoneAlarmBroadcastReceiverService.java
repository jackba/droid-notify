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
import apps.droidnotify.phone.PhoneCommon;

/**
 * This class does the work of the BroadcastReceiver.
 * 
 * @author Camille Sévigny
 */
public class PhoneAlarmBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public PhoneAlarmBroadcastReceiverService() {
		super("PhoneAlarmBroadcastReceiverService");
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
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				Log.e(context, "PhoneAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Check for a blacklist entry before doing anything else.
		    Bundle missedCallNotificationBundle = PhoneCommon.getMissedCalls(context);		
    		Bundle missedCallNotificationBundleSingle = null;
    		if(missedCallNotificationBundle != null){
    			missedCallNotificationBundleSingle = missedCallNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
    		}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, PhoneService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
					if(missedCallNotificationBundleSingle != null){
		    			//Display Status Bar Notification
		    			Common.setStatusBarNotification(context, 1, Constants.NOTIFICATION_TYPE_PHONE, 0, callStateIdle, missedCallNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), missedCallNotificationBundleSingle.getLong(Constants.BUNDLE_CONTACT_ID, -1), missedCallNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), null, null, null, -1, false, Common.getStatusBarNotificationBundle(context, Constants.NOTIFICATION_TYPE_PHONE));
					}
	    		}					
		    	if(missedCallNotificationBundle != null) Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, Constants.NOTIFICATION_TYPE_PHONE, missedCallNotificationBundle);
		    }
	    }catch(Exception ex){
			Log.e(context, "PhoneAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}