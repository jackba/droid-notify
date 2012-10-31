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
import apps.droidnotify.sms.SMSCommon;
import apps.droidnotify.calendar.CalendarCommon;
import apps.droidnotify.reminder.ReminderCommon;

public class RescheduleService extends WakefulIntentService {
	
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
	public RescheduleService() {
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
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
		    Bundle bundle = intent.getExtras();
		    int notificationType = bundle.getInt(Constants.BUNDLE_NOTIFICATION_TYPE, -1);
		    //Check to see if this is a reminder and it has been dismissed.
		    String intentAction = intent.getAction();
		    if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() IntentAction: " + intentAction);
		    if(ReminderCommon.isDismissed(context, intentAction)){
		    	if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Reminder has been dismissed. Exiting...");
		    	return;
		    }
		    //If notification is an SMS/MMS, check to see if it's already been read/deleted.
		    if(notificationType == Constants.NOTIFICATION_TYPE_SMS || notificationType == Constants.NOTIFICATION_TYPE_MMS){
				Bundle rescheduleNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
			    if(rescheduleNotificationBundle != null){
					Bundle rescheduleNotificationBundleSingle = rescheduleNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
					if(rescheduleNotificationBundleSingle != null){
						long threadID = rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_THREAD_ID, -1);
						long messageID = rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_MESSAGE_ID, -1);
						if(messageID < 0){
							messageID = SMSCommon.getMessageID(context, rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), threadID, rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_TIMESTAMP, -1));
						}
				    	if(SMSCommon.isMessageRead(context, messageID, threadID)){
				    		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() SMS/MMS Message has already been marked read. Exiting...");
				    		return;
				    	}
					}
			    }
		    }
		    //If notification is an calendar event, check to see if it's already been dismissed or deleted.
		    if(notificationType == Constants.NOTIFICATION_TYPE_CALENDAR){
				Bundle rescheduleNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
			    if(rescheduleNotificationBundle != null){
					Bundle rescheduleNotificationBundleSingle = rescheduleNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
					if(rescheduleNotificationBundleSingle != null){
						long eventID = rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_CALENDAR_EVENT_ID, -1);
				    	if(CalendarCommon.isEventDismissed(context, eventID)){
				    		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Calendar event has already been dismissed. Exiting...");
				    		return;
				    	}
				    	if(!CalendarCommon.eventExists(context, eventID)){
				    		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() Calendar event has been deleted. Exiting...");
				    		return;
				    	}
					}
			    }
		    }
//		    //If notification is a k-9 email, check to see if it's been deleted.
//		    if(notificationType == Constants.NOTIFICATION_TYPE_K9){
//				Bundle rescheduleNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
//			    if(rescheduleNotificationBundle != null){
//					Bundle rescheduleNotificationBundleSingle = rescheduleNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
//					if(rescheduleNotificationBundleSingle != null){
//				    	if(!K9Common.k9EmailExists(context, rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_MESSAGE_ID, -1), rescheduleNotificationBundleSingle.getInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE, -1), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_K9_EMAIL_ACCOUNT_NAME))){
//				    		if (_debug) Log.v("RescheduleBroadcastReceiverService.doWakefulWork() K9 email has been deleted. Exiting...");
//				    		return;
//				    	}
//					}
//			    }
//		    }
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotificationInCall = false;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    boolean showBlockedNotificationStatusBarNotification = false;
		    switch(notificationType){
			    case Constants.NOTIFICATION_TYPE_PHONE:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.PHONE_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_SMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_MMS:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.SMS_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_CALENDAR:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.CALENDAR_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_K9:{
			    	showBlockedNotificationStatusBarNotification = preferences.getBoolean(Constants.K9_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true);
			    	break;
			    }
			    case Constants.NOTIFICATION_TYPE_GENERIC:{
			    	break;
			    }
		    }
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotificationInCall = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				Common.startNotificationActivity(getApplicationContext(), bundle);
		    }else{
		    	if(notificationType == Constants.NOTIFICATION_TYPE_GENERIC){
		    		Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, notificationType, bundle);
		    	}else{
			    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
					Bundle rescheduleNotificationBundle = bundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME);
			    	if(showBlockedNotificationStatusBarNotification){
					    if(rescheduleNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = rescheduleNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle rescheduleNotificationBundleSingle = rescheduleNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(rescheduleNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, 1, notificationType, rescheduleNotificationBundleSingle.getInt(Constants.BUNDLE_NOTIFICATION_SUB_TYPE), callStateIdle, rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_CONTACT_ID, -1), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_K9_EMAIL_URI), rescheduleNotificationBundleSingle.getString(Constants.BUNDLE_LINK_URL), rescheduleNotificationBundleSingle.getLong(Constants.BUNDLE_THREAD_ID, -1), false, Common.getStatusBarNotificationBundle(context, notificationType));
				    			}
							}			    			
						}
			    	}
				    Common.rescheduleBlockedNotification(context, callStateIdle, rescheduleNotificationInCall, notificationType, rescheduleNotificationBundle);
			    }
		    }
		}catch(Exception ex){
			Log.e("RescheduleBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}