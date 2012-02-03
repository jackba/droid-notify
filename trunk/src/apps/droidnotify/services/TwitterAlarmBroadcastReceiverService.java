package apps.droidnotify.services;

import twitter4j.Twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import apps.droidnotify.common.Common;
import apps.droidnotify.common.Constants;
import apps.droidnotify.log.Log;
import apps.droidnotify.twitter.TwitterCommon;

/**
 * This class handles the polling of the users Facebook account.
 * 
 * @author Camille Sévigny
 */
public class TwitterAlarmBroadcastReceiverService extends WakefulIntentService {
	
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
	public TwitterAlarmBroadcastReceiverService() {
		super("TwitterAlarmBroadcastReceiverService");
		_debug = Log.getDebug();
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.TwitterAlarmBroadcastReceiverService()");
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
		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork()");
		try{
			Context context = getApplicationContext();
			//Determine the reminder interval based on the users preferences.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			//Read preferences and exit if app is disabled.
		    if(!preferences.getBoolean(Constants.APP_ENABLED_KEY, true)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() App Disabled. Exiting...");
				return;
			}
			//Block the notification if it's quiet time.
			if(Common.isQuietTime(context)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Quiet Time. Exiting...");
				return;
			}
			//Read preferences and exit if Twitter notifications are disabled.
		    if(!preferences.getBoolean(Constants.TWITTER_NOTIFICATIONS_ENABLED_KEY, false)){
				if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter Notifications Disabled. Exiting... ");
				return;
			}
		    //Check the state of the users phone.
		    TelephonyManager telemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    boolean notificationIsBlocked = false;
		    boolean rescheduleNotification = true;
		    boolean callStateIdle = telemanager.getCallState() == TelephonyManager.CALL_STATE_IDLE;
		    //Reschedule notification based on the users preferences.
		    if(!callStateIdle){
		    	notificationIsBlocked = true;		    	
		    	rescheduleNotification = preferences.getBoolean(Constants.IN_CALL_RESCHEDULING_ENABLED_KEY, false);
		    }else{		    	
		    	notificationIsBlocked = Common.isNotificationBlocked(context);
		    }
		    if(!notificationIsBlocked){
				WakefulIntentService.sendWakefulWork(context, new Intent(context, TwitterService.class));
		    }else{
		    	//Display the Status Bar Notification even though the popup is blocked based on the user preferences.
		    	if(preferences.getBoolean(Constants.TWITTER_STATUS_BAR_NOTIFICATIONS_SHOW_WHEN_BLOCKED_ENABLED_KEY, true)){
		    		//Get the Twitter message info.
					//Get Twitter Object
					Twitter twitter = TwitterCommon.getTwitter(context);
					if(twitter == null){
						if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Twitter object is null. Exiting...");
						return;
					}
					if(preferences.getBoolean(Constants.TWITTER_DIRECT_MESSAGES_ENABLED_KEY, true)){
						Bundle twitterDirectMessageNotificationBundle = TwitterCommon.getTwitterDirectMessages(context, twitter);
					    if(twitterDirectMessageNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = twitterDirectMessageNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle twitterDirectMessageNotificationBundleSingle = twitterDirectMessageNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(twitterDirectMessageNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_DIRECT_MESSAGE, callStateIdle, twitterDirectMessageNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), twitterDirectMessageNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), twitterDirectMessageNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, twitterDirectMessageNotificationBundleSingle.getString(Constants.BUNDLE_LINK_URL));
				    			}
							}			    			
						}else{
							if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No Twitter Direct Messages were found. Exiting...");
						}
					}
					if(preferences.getBoolean(Constants.TWITTER_MENTIONS_ENABLED_KEY, true)){
						Bundle twitterMentionNotificationBundle = TwitterCommon.getTwitterMentions(context, twitter);
					    if(twitterMentionNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = twitterMentionNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle twitterMentionNotificationBundleSingle = twitterMentionNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(twitterMentionNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_MENTION, callStateIdle, twitterMentionNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), twitterMentionNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), twitterMentionNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, twitterMentionNotificationBundleSingle.getString(Constants.BUNDLE_LINK_URL));
				    			}
							}			    			
						}else{
							if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No Twitter Mentions were found. Exiting...");
						}
					}
					if(preferences.getBoolean(Constants.TWITTER_FOLLOWER_REQUESTS_ENABLED_KEY, true)){
						Bundle twitterFollowerRequestNotificationBundle = TwitterCommon.getTwitterFollowerRequests(context, twitter);
					    if(twitterFollowerRequestNotificationBundle != null){
							//Loop through all the bundles that were sent through.
							int bundleCount = twitterFollowerRequestNotificationBundle.getInt(Constants.BUNDLE_NOTIFICATION_BUNDLE_COUNT);
							for(int i=1;i<=bundleCount;i++){
								Bundle twitterFollowerRequestNotificationBundleSingle = twitterFollowerRequestNotificationBundle.getBundle(Constants.BUNDLE_NOTIFICATION_BUNDLE_NAME + "_" + String.valueOf(i));
				    			if(twitterFollowerRequestNotificationBundleSingle != null){
									//Display Status Bar Notification
								    Common.setStatusBarNotification(context, Constants.NOTIFICATION_TYPE_TWITTER, Constants.NOTIFICATION_TYPE_TWITTER_FOLLOWER_REQUEST, callStateIdle, twitterFollowerRequestNotificationBundleSingle.getString(Constants.BUNDLE_CONTACT_NAME), twitterFollowerRequestNotificationBundleSingle.getString(Constants.BUNDLE_SENT_FROM_ADDRESS), twitterFollowerRequestNotificationBundleSingle.getString(Constants.BUNDLE_MESSAGE_BODY), null, twitterFollowerRequestNotificationBundleSingle.getString(Constants.BUNDLE_LINK_URL));
				    			}
							}			    			
						}else{
							if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() No Twitter Follower Requests were found. Exiting...");
						}
					}
			    }
		    	//Ignore notification based on the users preferences.
		    	if(preferences.getString(Constants.BLOCKING_APP_RUNNING_ACTION_KEY, Constants.BLOCKING_APP_RUNNING_ACTION_SHOW).equals(Constants.BLOCKING_APP_RUNNING_ACTION_IGNORE)){
		    		rescheduleNotification = false;
		    		return;
		    	}
		    	if(rescheduleNotification){
			    	//Set alarm to go off x minutes from the current time as defined by the user preferences.
			    	long rescheduleInterval = Long.parseLong(preferences.getString(Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_KEY, Constants.RESCHEDULE_BLOCKED_NOTIFICATION_TIMEOUT_DEFAULT)) * 60 * 1000;
		    		if (_debug) Log.v("TwitterAlarmBroadcastReceiverService.doWakefulWork() Rescheduling notification. Rechedule in " + rescheduleInterval + "minutes.");					
					TwitterCommon.setTwitterAlarm(context, System.currentTimeMillis() + rescheduleInterval);
		    	}
		    }
		}catch(Exception ex){
			Log.e("TwitterAlarmBroadcastReceiverService.doWakefulWork() ERROR: " + ex.toString());
		}
	}
		
}