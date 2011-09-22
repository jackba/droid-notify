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
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;

/**
 * This class listens for incoming SMS messages.
 * 
 * @author Camille Sévigny
 */
public class SMSReceiver extends BroadcastReceiver{

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
	    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
			if (_debug) Log.v("SMSReceiver.onReceive() App Disabled. Exiting...");
			return;
		}
		//Read preferences and exit if SMS notifications are disabled.
	    if(!preferences.getBoolean(Constants.SMS_NOTIFICATIONS_ENABLED_KEY, true)){
			if (_debug) Log.v("SMSReceiver.onReceive() SMS Notifications Disabled. Exiting...");
			return;
		}
		if(preferences.getString(Constants.SMS_LOADING_SETTING_KEY, "0").equals(Constants.SMS_READ_FROM_DISK)){
			//Schedule sms task x seconds after the broadcast.
			//This time is set by the users advanced preferences. 10 seconds is the default value.
			//This should allow enough time to pass for the sms inbox to be written to.
			long timeoutInterval = Long.parseLong(preferences.getString(Constants.SMS_TIMEOUT_KEY, "10")) * 1000;
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent smsIntent = new Intent(context, SMSAlarmReceiver.class);
			PendingIntent smsPendingIntent = PendingIntent.getBroadcast(context, 0, smsIntent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeoutInterval, smsPendingIntent);	
		}else{
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean rescheduleNotification = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inMessagingApp = preferences.getBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
		    boolean messagingAppRunning = Common.isMessagingAppRunning(context);
		    String messagingAppRuningAction = preferences.getString(Constants.SMS_MESSAGING_APP_RUNNING_ACTION_KEY, "0");
		    if(!callStateIdle || inMessagingApp){
		    	rescheduleNotification = true;
		    }else{
		    	//Messaging App is running.
		    	if(messagingAppRunning){
		    		//Reschedule notification based on the users preferences.
				    if(messagingAppRuningAction.equals(Constants.MESSAGING_APP_RUNNING_ACTION_RESCHEDULE)){
						rescheduleNotification = true;
				    }
				    //Ignore notification based on the users preferences.
				    if(messagingAppRuningAction.equals(Constants.MESSAGING_APP_RUNNING_ACTION_IGNORE)){
				    	return;
				    }
		    	}
		    }
		    if(!rescheduleNotification){
				WakefulIntentService.acquireStaticLock(context);
				Intent smsIntent = new Intent(context, SMSReceiverService.class);
				smsIntent.putExtras(intent.getExtras());
				context.startService(smsIntent);
		    }else{
		    	// Set alarm to go off x minutes from the current time as defined by the user preferences.
		    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
		    	if(preferences.getBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, true)){
		    		if(rescheduleInterval == 0){
		    			SharedPreferences.Editor editor = preferences.edit();
		    			editor.putBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, false);
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

}