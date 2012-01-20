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
import apps.droidnotify.receivers.RescheduleReceiver;

public class RescheduleBroadcastReceiverService extends WakefulIntentService {
	
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
	public RescheduleBroadcastReceiverService() {
		super("RescheduleBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("RescheduleBroadcastReceiverService.RescheduleBroadcastReceiverService()");
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
		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    Bundle bundle = intent.getExtras();
		    int notificationType = bundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE);
			int rescheduleNumber = bundle.getInt(Constants.BUNDLE_RESCHEDULE_NUMBER);
			//Determine if the notification should be rescheduled or not.
			boolean displayNotification = true;
			if(preferences.getBoolean(Constants.REMINDERS_ENABLED_KEY, false)){	
				int maxRescheduleAttempts = Integer.parseInt(preferences.getString(Constants.REMINDER_FREQUENCY_KEY, Constants.REMINDER_FREQUENCY_DEFAULT));
				if(maxRescheduleAttempts < 0){
					//Infinite Attempts.
					displayNotification = true;
				}else if(rescheduleNumber > maxRescheduleAttempts){
					displayNotification = false;
				}
			}
			if(!displayNotification){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Rescheduling Disabled or Max reschedule attempts made. Exiting...");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotification = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    String blockingAppRuningAction = null;
		    boolean showBlockedNotificationStatusBarNotification = false;
		    switch(notificationType){
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_PHONE:{
			    	blockingAppRuningAction = preferences.getString(Constants.PHONE_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_SMS:{
			    	blockingAppRuningAction = preferences.getString(Constants.SMS_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_MMS:{
			    	blockingAppRuningAction = preferences.getString(Constants.MMS_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.MMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_CALENDAR:{
			    	blockingAppRuningAction = preferences.getString(Constants.CALENDAR_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_GMAIL:{
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_TWITTER:{
			    	blockingAppRuningAction = preferences.getString(Constants.TWITTER_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK:{
			    	blockingAppRuningAction = preferences.getString(Constants.FACEBOOK_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_RESCHEDULE_K9:{
			    	blockingAppRuningAction = preferences.getString(Constants.K9_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
		    }
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{		    	
		    	notificationIsBlocked = Common.isNotificationBlocked(context, blockingAppRuningAction);
		    }
		    if(!notificationIsBlocked){
				Intent rescheduleIntent = new Intent(context, RescheduleService.class);
				rescheduleIntent.putExtras(intent.getExtras());
				WakefulIntentService.sendWakefulWork(context, rescheduleIntent);
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(showBlockedNotificationStatusBarNotification){
		    		//Get the notification info.
		    		Bundle rescheduleBundle = intent.getExtras();
		    		String[] rescheduleNotificationInfo = rescheduleBundle.getStringArray("rescheduleNotificationInfo");
		    		//========================================================
		    		//String[] Values:
		    		//[0]-notificationType
		    		//[1]-SentFromAddress
		    		//[2]-MessageBody
		    		//[3]-TimeStamp
		    		//[4]-ThreadID
		    		//[5]-ContactID
		    		//[6]-ContactName
		    		//[7]-MessageID
		    		//[8]-Title
		    		//[9]-CalendarID
		    		//[10]-CalendarEventID
		    		//[11]-CalendarEventStartTime
		    		//[12]-CalendarEventEndTime
		    		//[13]-AllDay
		    		//[14]-CallLogID
		    		//[15]-K9EmailUri
		    		//[16]-K9EmailDelUri
		    		//[17]-LookupKey
		    		//[18]-PhotoID
		    		//[19]-NotificationSubType
		    		//[20]-MessageStringID
		    		//========================================================
		    		String sentFromAddress = rescheduleNotificationInfo[1];
					String messageBody = rescheduleNotificationInfo[2];
					String contactName = rescheduleNotificationInfo[6];
	    			String title = rescheduleNotificationInfo[8];
	    			String k9EmailUri = rescheduleNotificationInfo[15];
	    			int notificationSubType = Integer.parseInt(rescheduleNotificationInfo[19]);
	    			//Display Status Bar Notification
					switch(notificationType){
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_PHONE:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_PHONE, 0, callStateIdle, contactName, sentFromAddress, null, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_SMS:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_SMS, 0, callStateIdle, contactName, sentFromAddress, messageBody, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_MMS:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_MMS, 0, callStateIdle, contactName, sentFromAddress, messageBody, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_CALENDAR:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_CALENDAR, 0, callStateIdle, null, null, title, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_GMAIL:{
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_TWITTER:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER, notificationSubType, callStateIdle, contactName, sentFromAddress, messageBody, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_FACEBOOK:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK, notificationSubType, callStateIdle, contactName, sentFromAddress, messageBody, null);
					    	break;
					    }
					    case Constants.NOTIFICATION_TYPE_RESCHEDULE_K9:{
					    	Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_K9, 0, callStateIdle, contactName, sentFromAddress, messageBody, k9EmailUri);
					    	break;
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
		    		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
					String intentActionText = "apps.droidnotify.alarm/RescheduleReceiverAlarm/" + String.valueOf(notificationType) + "/" + String.valueOf(System.currentTimeMillis());
					long rescheduleTime = System.currentTimeMillis() + rescheduleInterval;
					Common.startAlarm(context, RescheduleReceiver.class, intent.getExtras(), intentActionText, rescheduleTime);
		    	}
		    }
		}catch(Exception ex){
			Log.e("RescheduleBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}