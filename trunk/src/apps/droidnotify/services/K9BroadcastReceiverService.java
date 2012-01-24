package apps.droidnotify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.email.EmailCommon;
import apps.droidnotify.log.Log;
import apps.droidnotify.receivers.K9Receiver;

public class K9BroadcastReceiverService extends WakefulIntentService {
	
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
	public K9BroadcastReceiverService() {
		super("K9BroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("K9BroadcastReceiverService.K9BroadcastReceiverService()");
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
		if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if K9 notifications are disabled.
		    if(!preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() K9 Notifications Disabled. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotification = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    String blockingAppRuningAction = preferences.getString(Constants.K9_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{		    	
		    	notificationIsBlocked = Common.isNotificationBlocked(context, blockingAppRuningAction);
		    }
		    if(!notificationIsBlocked){
				Intent k9Intent = new Intent( context, K9Service.class);
				k9Intent.putExtras(intent.getExtras());
				k9Intent.setAction(intent.getAction());
				WakefulIntentService.sendWakefulWork(context, k9Intent);
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the k9 email info.
		    		Bundle bundle = intent.getExtras();
		    		Bundle emailNotificationBundle = EmailCommon.getK9MessagesFromIntent(context, bundle, intent.getAction());
		    		if(emailNotificationBundle != null){
		    			Bundle emailNotificationBundleSingle = emailNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
		    			if(emailNotificationBundleSingle != null){
							//Display Status Bar Notification
						    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_K9, 0, callStateIdle, emailNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), emailNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), emailNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), emailNotificationBundleSingle.getString(Constants.BUNDLE_K9_EMAIL_URI), null);
		    			}
		    		}
		    	}
		    	//Ignore notification based on the users preferences.
		    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		rescheduleNotification = false;
		    		return;
		    	}
		    	if(rescheduleNotification){
			    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
			    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");				
					String intentActionText = "apps.droidnotify.alarm/K9ReceiverAlarm/" + String.valueOf(System.currentTimeMillis());
					long alarmTime = System.currentTimeMillis() + rescheduleInterval;
					Common.startAlarm(context, K9Receiver.class, null, intentActionText, alarmTime);
		    	}
		    }
		}catch(Exception ex){
			Log.e("K9BroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}