package apps.droidnotify.services;

import com.facebook.android.Facebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.facebook.FacebookCommon;
import apps.droidnotify.log.Log;

/**
 * This class handles the polling of the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class FacebookAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
	public FacebookAlarmBroadcastReceiverService() {
		super("FacebookAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.FacebookAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Facebook notifications are disabled.
		    if(!preferences.getBoolean(Constants.FACEBOOK_NOTIFICATIONS_ENABLED_KEY, false)){
				if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Facebook Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotification = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    String blockingAppRuningAction = preferences.getString(Constants.FACEBOOK_BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW);
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{		    	
		    	notificationIsBlocked = Common.isNotificationBlocked(context, blockingAppRuningAction);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, FacebookService.class));
		    }else{			    
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.FACEBOOK_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
				    //Get Facebook Object
					Facebook facebook = FacebookCommon.getFacebook(context);
				    if(facebook == null){
				    	if (_debug) Log.v("FacebookService.doWakefulWork() Facebook object is null. Exiting... ");
				    	return;
				    }
					String accessToken = preferences.getString(Constants.FACEBOOK_ACCESS_TOKEN_KEY, null);
					if(preferences.getBoolean(Constants.FACEBOOK_USER_NOTIFICATIONS_ENABLED_KEY, true)){
						Bundle facebookNotificationNotificationBundle = FacebookCommon.getFacebookNotifications(context, accessToken, facebook);
					    if(facebookNotificationNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = facebookNotificationNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle facebookNotificationNotificationBundleSingle = facebookNotificationNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(facebookNotificationNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_NOTIFICATION, callStateIdle, facebookNotificationNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), facebookNotificationNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), facebookNotificationNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null);
				    			}
							}			    			
						}
					}
					if(preferences.getBoolean(Constants.FACEBOOK_FRIEND_REQUESTS_ENABLED_KEY, true)){
					    Bundle facebookFriendRequestNotificationBundle = FacebookCommon.getFacebookFriendRequests(context, accessToken, facebook);
					    if(facebookFriendRequestNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = facebookFriendRequestNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle facebookFriendRequestNotificationBundleSingle = facebookFriendRequestNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(facebookFriendRequestNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_FRIEND_REQUEST, callStateIdle, facebookFriendRequestNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), facebookFriendRequestNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), facebookFriendRequestNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null);
				    			}
							}			    			
						}
					}
					if(preferences.getBoolean(Constants.FACEBOOK_MESSAGES_ENABLED_KEY, true)){
					    Bundle facebookMessageNotificationBundle = FacebookCommon.getFacebookMessages(context, accessToken, facebook);
					    if(facebookMessageNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = facebookMessageNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle facebookMessageNotificationBundleSingle = facebookMessageNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(facebookMessageNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_FACEBOOK, Constants.NOTIFICATION_TYPE_FACEBOOK_MESSAGE, callStateIdle, facebookMessageNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), facebookMessageNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), facebookMessageNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null);
				    			}
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
			    		if (_debug) Log.v("FacebookAlarmBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
						FacebookCommon.setFacebookAlarm(context, System.currentTimeMillis() + rescheduleInterval);
			    	}
			    }
		    }
		}catch(Exception ex){
			Log.e("FacebookAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}