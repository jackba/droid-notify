package apps.droidnotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import apps.droidnotify.common.Common;
import apps.droidnotify.log.Log;

/**
 * This class listens for incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiver extends BroadcastReceiver{

	//================================================================================
    // Constants
    //================================================================================
	
	private static final String APP_ENABLED_KEY = "app_enabled";
	private static final String SMS_NOTIFICATIONS_ENABLED_KEY = "sms_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATIONS_ENABLED = "reschedule_notifications_enabled";
	private static final String RESCHEDULE_NOTIFICATION_TIMEOUT_KEY = "reschedule_notification_timeout_settings";
	private static final String USER_IN_MESSAGING_APP = "user_in_messaging_app";
	private static final String MESSAGING_APP_RUNNING_ACTION_SMS = "messaging_app_running_action_sms";
	
	private static final String MESSAGING_APP_RUNNING_ACTION_RESCHEDULE = "0";
	private static final String MESSAGING_APP_RUNNING_ACTION_IGNORE = "1";
	
	//================================================================================
    // Properties
    //================================================================================

	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Receives the incomming SMS message. The SMS message is located within the Intent object.
	 * This function starts the service that will handle the work or reschedules the work if the phone is in use.
	 * 
	 * @param context - Application Context.
	 * @param intent - Intent object that we are working with.
	 */
	@Override
	public void onReceive(Context context, Intent intent){
		_debug = Log.getDebug();
		if (_debug) Log.v("SMSReceiver.onReceive()");
		//Read preferences and exit if app is disabled.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    if(!preferences.getBoolean(APP_ENABLED_KEY, true)){
			if (_debug) Log.v("SMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if SMS notifications are disabled.
	    if(!preferences.getBoolean(SMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("SMSReceiver.onReceive() SMS Notifications Disabled. Exiting...");
			return;
		}
	    //Check the state of the users phone.
	    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    boolean rescheduleNotification = false;
	    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
	    boolean inMessagingApp = preferences.getBoolean(USER_IN_MESSAGING_APP, false);
	    boolean messagingAppRunning = Common.isMessagingAppRunning(context);
	    if(callStateIdle && !inMessagingApp){
	    	rescheduleNotification = true;
	    }
	    String messagingAppRuningAction = preferences.getString(MESSAGING_APP_RUNNING_ACTION_SMS, "0");
	    //Reschedule notification based on the users preferences.
	    if(messagingAppRuningAction.equals(MESSAGING_APP_RUNNING_ACTION_RESCHEDULE)){
	    	if(messagingAppRunning){
	    		rescheduleNotification = true;
	    	}
	    }
	    //Ignore notification based on the users preferences.
	    if(messagingAppRuningAction.equals(MESSAGING_APP_RUNNING_ACTION_IGNORE)){
	    	return;
	    }
	    if(!rescheduleNotification){
			WakefulIntentService.acquireStaticLock(context);
			Intent smsIntent = new Intent(context, SMSReceiverService.class);
			smsIntent.putExtras(intent.getExtras());
			context.startService(smsIntent);
	    }else{
	    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
	    	long rescheduleInterval = Long.parseLong(preferences.getString(RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
	    	if(preferences.getBoolean(RESCHEDULE_NOTIFICATIONS_ENABLED, true)){
	    		if(rescheduleInterval == 0){
	    			SharedPreferences.Editor editor = preferences.edit();
	    			editor.putBoolean(RESCHEDULE_NOTIFICATIONS_ENABLED, false);
	    			editor.commit();
	    			return;
	    		}
	    		if (_debug) Log.v("SMSReceiver.onReceive() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent smsIntent = new Intent(context, SMSReceiver.class);
				smsIntent.putExtras(intent.getExtras());
				smsIntent.setAction("apps.droidnotify.VIEW/SMSReschedule/" + System.currentTimeMillis());
				PendingIntent smsPendingIntent = PendingIntent.getBroadcast(context, 0, smsIntent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, smsPendingIntent);
	    	}
	    }
	}

}