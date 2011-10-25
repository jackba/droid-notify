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
			//Read preferences and exit if SMS notifications are disabled.
		    if(!preferences.getBoolean(Constants.K9_NOTIFICATIONS_ENABLED_KEY, true)){
				if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() K9 Notifications Disabled. Exiting...");
				return;
			}
//			    //Check the state of the users phone.
//			    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//			    boolean rescheduleNotification = false;
//			    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
//			    boolean inMessagingApp = preferences.getBoolean(Constants.USER_IN_MESSAGING_APP_KEY, false);
//			    boolean blockingAppRunning = Common.isBlockingAppRunning(context);
//			    String blockingAppRuningAction = preferences.getString(Constants.SMS_BLOCKING_APP_RUNNING_ACTION_KEY, "0");
//			    //Reschedule notification based on the users preferences.
//			    if(!callStateIdle){
//			    	rescheduleNotification = true;
//			    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && inMessagingApp){
//			    	//Messaging App is running.
//			    	rescheduleNotification = true;
//			    }else if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_RESCHEDULE) && blockingAppRunning){ 
//			    	//Blocking App is running.
//			    	rescheduleNotification = true;
//			    }
//			    if(!rescheduleNotification){
//					WakefulIntentService.acquireStaticLock(context);
//					Intent smsIntent = new Intent(context, SMSReceiverService.class);
//					smsIntent.putExtras(intent.getExtras());
//					context.startService(smsIntent);
//			    }else{
//			    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
//			    	if(preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
//			    		//Get the sms message info.
//						String messageAddress = null;
//						String messageBody = null;
//						String contactName = null;
//			    		Bundle bundle = intent.getExtras();
//			    		ArrayList<String> smsArray = Common.getSMSMessagesFromIntent(context, bundle);
//						if((smsArray != null) && (smsArray.size() > 0)){
//				    		String smsArrayItem = smsArray.get(0);
//							String[] smsInfo = smsArrayItem.split("\\|");
//			    			int arraySize = smsInfo.length;
//			    			if(arraySize > 0){
//								if(arraySize >= 1) messageAddress = smsInfo[0];
//								if(arraySize >= 2) messageBody = smsInfo[1];
//								if(arraySize >= 7) contactName = smsInfo[6];
//			    			}
//						}
//						//Display Status Bar Notification
//					    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_SMS, callStateIdle, contactName, messageAddress, messageBody);
//				    }
//			    	//Ignore notification based on the users preferences.
//			    	if(blockingAppRuningAction.equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
//			    		return;
//			    	}
//			    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
//			    	if(preferences.getBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, true)){
//				    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_NOTIFICATION_TIMEOUT_KEY, "5")) * 60 * 1000;
//			    		if(rescheduleInterval == 0){
//			    			SharedPreferences.Editor editor = preferences.edit();
//			    			editor.putBoolean(Constants.RESCHEDULE_NOTIFICATIONS_ENABLED_KEY, false);
//			    			editor.commit();
//			    			return;
//			    		}
//			    		if (_debug) Log.v("K9BroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");
//						AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//						Intent smsIntent = new Intent(context, SMSReceiverService.class);
//						smsIntent.putExtras(intent.getExtras());
//						smsIntent.setAction("apps.droidnotify.VIEW/SMSReschedule/" + System.currentTimeMillis());
//						PendingIntent smsPendingIntent = PendingIntent.getBroadcast(context, 0, smsIntent, 0);
//						alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + rescheduleInterval, smsPendingIntent);
//			    	}
//			    }
		}catch(Exception ex){
			if (_debug) Log.e("K9BroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}