package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.k9.K9Common;
import apps.droidnotify.log.Log;

public class K9AlarmBroadcastReceiverService extends WakefulIntentService {

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public K9AlarmBroadcastReceiverService() {
		super("K9AlarmBroadcastReceiverService");
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
				Log.e(context, "K9AlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Check for a blacklist entry before doing anything else.
    		Bundle bundle = intent.getExtras();
    		Bundle emailNotificationBundle = K9Common.getK9MessagesFromIntent(context, bundle, intent.getAction());	
    		Bundle emailNotificationBundleSingle = null;
    		if(emailNotificationBundle != null){
    			emailNotificationBundleSingle = emailNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
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
				Intent k9Intent = new Intent( context, K9Service.class);
				k9Intent.putExtras(intent.getExtras());
				k9Intent.setAction(intent.getAction());
				WakefulIntentService.sendWakefulWork(context, k9Intent);
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		if(emailNotificationBundleSingle != null){
						//Display Status Bar Notification
					    Common.setStatusBarNotification(context, 1, Constants.NOTIFICATION_TYPE_K9, 0, callStateIdle, emailNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), emailNotificationBundleSingle.getLong(Constants.BUNDLE_CONTACT_ID, -1), emailNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), emailNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), emailNotificationBundleSingle.getString(Constants.BUNDLE_K9_EMAIL_URI), null, -1, false, Common.getStatusBarNotificationBundle(context, Constants.NOTIFICATION_TYPE_K9));
		    		}
		    	}
		    	if(emailNotificationBundle != null) Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, Constants.NOTIFICATION_TYPE_K9, emailNotificationBundle);
		    }
		}catch(Exception ex){
			Log.e(context, "K9AlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}