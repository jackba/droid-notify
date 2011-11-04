package apps.droidnotify.services;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
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
		    boolean rescheduleNotification = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean inMessagingApp = preferences.getBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
		    boolean blockingAppRunning = Common.isBlockingAppRunning(context);
		    String blockingAppRuningAction = preferences.getString(Constants.K9_BLOCKING_APP_RUNNING_ACTION_KEY, "0");
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	rescheduleNotification = true;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && inMessagingApp){
		    	//Messaging App is running.
		    	rescheduleNotification = true;
		    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && blockingAppRunning){ 
		    	//Blocking App is running.
		    	rescheduleNotification = true;
		    }
		    if(!rescheduleNotification){
				Intent k9Intent = new Intent(context, K9ReceiverService.class);
				k9Intent.putExtras(intent.getExtras());
				WakefulIntentService.sendWakefulWork(context, k9Intent);
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the k9 message info.
					String messageAddress = null;
					String messageBody = null;
					String contactName = null;
					String k9EmailUri = null;
		    		Bundle bundle = intent.getExtras();
		    		ArrayList<String> k9Array = Common.getK9MessagesFromIntent(context, bundle);
					if((k9Array != null) && (k9Array.size() > 0)){
			    		String k9ArrayItem = k9Array.get(0);
						String[] k9Info = k9ArrayItem.split("\\|");
		    			int arraySize = k9Info.length;
		    			if(arraySize > 0){
							if(arraySize >= 1) messageAddress = k9Info[0];
							if(arraySize >= 2) messageBody = k9Info[1];
							if(arraySize >= 5) k9EmailUri = k9Info[4];
							if(arraySize >= 8) contactName = k9Info[7];
		    			}
					}
					//Display Status Bar Notification
				    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_K9, callStateIdle, contactName, messageAddress, messageBody, k9EmailUri);
			    }
		    	//Ignore notification based on the users preferences.
		    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		return;
		    	}
		    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
		    	if(preferences.getBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, true)){
		    		long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");
					AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
					Intent k9Intent = new Intent(context, K9Receiver.class);
					k9Intent.putExtras(intent.getExtras());
					k9Intent.setAction("apps.droidnotify.VIEW/K9Reschedule/" + System.currentTimeMillis());
					PendingIntent k9PendingIntent = PendingIntent.getBroadcast(context, 0, k9Intent, 0);
					alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, k9PendingIntent);
		    	}
		    }
		}catch(Exception ex){
			if (_debug) Log.e("K9BroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}