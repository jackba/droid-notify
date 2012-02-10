package apps.droidnotify.services;

import com.google.code.linkedinapi.client.LinkedInApiClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.linkedin.LinkedInCommon;
import apps.droidnotify.log.Log;

public class LinkedInAlarmBroadcastReceiverService extends WakefulIntentService {
	
	//================================================================================
    // Properties
    //================================================================================
	
	private boolean _debug = false;

	//================================================================================
	// Public Methods
	//================================================================================
	
	/**
	 * Class Constructor.
	 */
	public LinkedInAlarmBroadcastReceiverService() {
		super("LinkedInAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.LinkedInAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if LinkedIn notifications are disabled.
		    if(!preferences.getBoolean(Constants.LINKEDIN_NOTIFICATIONS_ENABLED_KEY, false)){
				if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() LinkedIn Notifications Disabled. Exiting... ");
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
				WakefulIntentService.sendWakefulWork(context, new Intent(context, LinkedInService.class));
		    }else{
			    //Get LinkedIn Object
				LinkedInApiClient linkedInClient = LinkedInCommon.getLinkedIn(context);
			    if(linkedInClient == null){
			    	if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() LinkedInClient object is null. Exiting... ");
			    	return;
			    }
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
			    Bundle linkedInUpdateBundle = LinkedInCommon.getLinkedInupdates(context, linkedInClient);
		    	if(preferences.getBoolean(Constants.LINKEDIN_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
				    if(preferences.getBoolean(Constants.LINKEDIN_UPDATES_ENABLED_KEY, true)){
					    if(linkedInUpdateBundle != null){
					    	Bundle linkedInUpdateBundleSingle = linkedInUpdateBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_1");
					    	if(linkedInUpdateBundleSingle != null){
								//Display Status Bar Notification
							    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_LINKEDIN, Constants.NOTIFICATION_TYPE_LINKEDIN_UPDATE, callStateIdle, linkedInUpdateBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), linkedInUpdateBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), linkedInUpdateBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, null);
					    	}
						}else{
							if (_debug) Log.v("LinkedInAlarmBroadcastReceiverService.doWakefulWork() No Facebook Nnotifications were found. Exiting...");
						}
					}
			    }					
		    	if(linkedInUpdateBundle != null) Common.rescheduleBlockedNotification(context, rescheduleNotificationInCall, rescheduleNotificationInQuickReply, Constants.NOTIFICATION_TYPE_LINKEDIN, linkedInUpdateBundle);
		    }
		}catch(Exception ex){
			Log.e("LinkedInAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}